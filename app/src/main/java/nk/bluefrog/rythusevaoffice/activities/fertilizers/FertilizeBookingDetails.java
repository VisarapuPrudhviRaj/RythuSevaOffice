package nk.bluefrog.rythusevaoffice.activities.fertilizers;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import nk.bluefrog.library.BluefrogActivity;
import nk.bluefrog.library.utils.Helper;
import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.utils.Constants;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;
import nk.bluefrog.rythusevaoffice.utils.DBTables;


public class FertilizeBookingDetails extends BluefrogActivity {


    String equipment;
    DBHelper dbHelper;
    String gpsString = "";
    LatLng from, to;
    private GoogleMap mMap;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("Equipment", equipment);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        equipment = savedInstanceState.getString("Equipment");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fertilize_booking_details);

        setToolBar(this, getString(R.string.ferti_details), "");
        setToolBarWithID(getString(R.string.ferti_details), "", R.id.toolbar_);
        dbHelper = new DBHelper(this);
        if (getIntent().getExtras().containsKey("Equipment")) {
            equipment = getIntent().getStringExtra("Equipment");
            findViews();
        } else {
            Helper.showToast(this, getResources().getString(R.string.key_missing));
            finish();
        }
    }


    private void findViews() {
        setMap();
        try {

            //{"District":"Visakhapatnam","Mandal":"Ananthagiri","Panchayat":"Ananthagiri",
            // "ShopName":"Fancy","LicenceNo":"333","OwnerName":"Balu","MobileNo":"9494905481",
            // "Address":"opposite HSBC\nSiriporam \nVskp","Fertilizer":"Complex","Quantity":"10",
            // "Price":"1000","ImagePath":"http:\/\/65.19.149.158\/TM_Application\/Form\/\/\/GCFE1\/\/images\/\/GCFE1_Registration_28_10_2017_16_2_52_869_Registration_img-3.jpeg",
            // "GPS":"17.7219913$83.3160447","Distance":219.3}
            JSONObject jsonObject = new JSONObject(equipment);
            setToolBar(this, getString(R.string.ferti_details), jsonObject.getString("Fertilizer"));
            setToolBarWithID(getString(R.string.ferti_details), jsonObject.getString("Fertilizer"), R.id.toolbar_);

            ((TextView) findViewById(R.id.tv_shopName)).setText(jsonObject.getString("ShopName"));
            if (!jsonObject.getString("Address").trim().equals("")) {
                ((TextView) findViewById(R.id.tv_eqAddress)).setText(getString(R.string.address) + " : " + jsonObject.getString("Address").trim() +
                        "\n" + getString(R.string.panch) + " : " + jsonObject.getString("Panchayat") + "\n" + getString(R.string.mandal_) + " : " + jsonObject.getString("Mandal") + "\n" + getString(R.string.district_) + " : " + jsonObject.getString("District"));
            } else {
                String gpsPoints[] = jsonObject.getString("GPS").trim().split("\\$");
                LatLng Tloc = new LatLng(Double.parseDouble(gpsPoints[0]), Double.parseDouble(gpsPoints[1]));
                to = Tloc;
                setAddress(to);
            }
            Double itemprice = Double.parseDouble(jsonObject.getString("Price")) / 100;
            ((TextView) findViewById(R.id.tv_eqDist)).setText(getString(R.string.distance) + jsonObject.getString("Distance") + " KM");
            ((TextView) findViewById(R.id.tv_eqName)).setText(getString(R.string.fertilizers_name_av) + jsonObject.getString("Fertilizer").trim() +
                    "\n" + getString(R.string.quality) + " : " + jsonObject.getString("Quantity") +
                    "\n" + getString(R.string.price) + " : " + itemprice + " ₹ ");
            String image = jsonObject.getString("ImagePath").trim().replace("\\/", "/");
            ImageView iv_Equipment_Image = (ImageView) findViewById(R.id.iv_Equipment_Image);
            ProgressBar progress = (ProgressBar) findViewById(R.id.progress);
            loadImageElseWhite(image, iv_Equipment_Image, progress);

            ((TextView) findViewById(R.id.tv_ownerName)).setText(getString(R.string.str_name) + jsonObject.getString("OwnerName").trim());
            ((TextView) findViewById(R.id.tv_ownerPhone)).setText(getString(R.string.phone) + jsonObject.getString("MobileNo").trim());


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void call() {

        try {
            JSONObject jsonObject = new JSONObject(equipment);
            String phone = jsonObject.getString("MobileNo").trim();
            String aadharNo = dbHelper.getTabCol(DBTables.Register.TABLE_NAME, DBTables.Register.user_aadharid);
            Constants.call(dbHelper,phone, FertilizeBookingDetails.this, Constants.Type_Fertilizers, aadharNo);

        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("Ex:" + e.getMessage().toString());
        }


    }

    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.iv_call:
                call();
                break;


            default:
                break;
        }
    }

    private void setAddress(final LatLng latLng) {
        if (Helper.isNetworkAvailable(this)) {
            showProgressDialog(getString(R.string.fetch_book));
            new Thread() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Geocoder geocoder;
                            List<Address> addresses;
                            geocoder = new Geocoder(FertilizeBookingDetails.this, Locale.getDefault());
                            try {
                                addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                if (addresses != null && addresses.size() > 0) {
                                    closeProgressDialog();
                                    final String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                    final String city = addresses.get(0).getLocality();
                                    final String state = addresses.get(0).getAdminArea();
                                    final String country = addresses.get(0).getCountryName();
                                    final String postalCode = addresses.get(0).getPostalCode();
                                    String knownName = addresses.get(0).getFeatureName();
                                    final String locality = addresses.get(0).getLocality();
                                    final String subLocality = addresses.get(0).getSubAdminArea();


                                    StringBuilder sb = new StringBuilder();
                                    sb.append(address);
                                    if (!TextUtils.isEmpty(locality) && !locality.equalsIgnoreCase("null") && !locality.equalsIgnoreCase(city))
                                        sb.append(", " + locality);

                                    if (!TextUtils.isEmpty(city) && !city.equalsIgnoreCase("null"))
                                        sb.append(", " + city);

                                    if (!TextUtils.isEmpty(state) && !state.equalsIgnoreCase("null"))
                                        sb.append(", " + state);

                                    if (!TextUtils.isEmpty(country) && !country.equalsIgnoreCase("null"))
                                        sb.append(", " + country);

                                    if (!TextUtils.isEmpty(postalCode) && !postalCode.equalsIgnoreCase("null"))
                                        sb.append(", " + postalCode);

                                    ((TextView) findViewById(R.id.tv_eqAddress)).setText(getString(R.string.address) + " : " + sb.toString());
                                } else {
                                    closeProgressDialog();

                                }
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage().toString().trim() + " or low Internet! Please Check", Toast.LENGTH_SHORT).show();
                                closeProgressDialog();
                            }
                        }
                    });
                }
            }.start();


        } else {
            Helper.showToast(FertilizeBookingDetails.this, getString(R.string.no_net));
        }
    }

    private void setMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                if (ActivityCompat.checkSelfPermission(FertilizeBookingDetails.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(FertilizeBookingDetails.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.getUiSettings().setZoomGesturesEnabled(true);
                googleMap.getUiSettings().setRotateGesturesEnabled(true);
                googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location location) {
                        LatLng Floc = new LatLng(location.getLatitude(), location.getLongitude());
                        gpsString = location.getLatitude() + "-"
                                + location.getLongitude();
                        System.out.println("GPS FROM:" + gpsString);
                        from = Floc;
                    }
                });

                try {
                    JSONObject jsonObject = new JSONObject(equipment);
                    //System.out.println("GPS TO:" + jsonObject.getString("Equipment_GPS").trim());
                    String gpsPoints[] = jsonObject.getString("GPS").trim().split("\\$");
                    LatLng Tloc = new LatLng(Double.parseDouble(gpsPoints[0]), Double.parseDouble(gpsPoints[1]));
                    to = Tloc;
                    //String eq_name = dbHelper.getValueByIds(DBTables.Category.TABLE_NAME, DBTables.Category.NAME, new String[]{DBTables.Category.ID}, new String[]{jsonObject.getString("Equipment_Id").trim()});
                    drawMarker(jsonObject.getString("GPS").trim(), 1, jsonObject.getString("Fertilizer").trim(), jsonObject.getString("OwnerName").trim());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void drawMarker(String gpsPoint, int type, String title, String snippet) {
        MarkerOptions markerOptions = new MarkerOptions();

        if (type == 1) {
            //form
            String latlng[] = gpsPoint.toString().trim().split("\\$");
            from = new LatLng(Double.parseDouble(latlng[0]), Double.parseDouble(latlng[1]));
            // Setting latitude and longitude for the marker
            markerOptions.position(from).title(getString(R.string.fertilize_name) + title).snippet(snippet).flat(true);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        } else {
            //list_user
            //to
            String latlng[] = gpsPoint.toString().trim().split("\\$");
            to = new LatLng(Double.parseDouble(latlng[0]), Double.parseDouble(latlng[1]));
            // Setting latitude and longitude for the marker
            markerOptions.position(to).title(getString(R.string.fertilize_name) + title).snippet(snippet).flat(true);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }

        Marker marker = mMap.addMarker(markerOptions);
        dropPinEffect(marker);
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(from));
        // mMap.animateCamera(CameraUpdateFactory.zoomTo(Float.parseFloat("12")));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                from, 18));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(from));

    }

    private void dropPinEffect(final Marker marker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 2000;

        final Interpolator interpolator = new AnticipateInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = Math.max(
                        1 - interpolator.getInterpolation((float) elapsed
                                / duration), 0);
                marker.setAnchor(0.5f, 1.0f + 14 * t);

                if (t > 0.0) {
                    handler.postDelayed(this, 15);
                } else {
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void getRoute() {
        if (from != null && to != null)
            getDirection(from.latitude, from.longitude, to.latitude, to.longitude, getString(R.string.driving));
    }

    private void getDirection(final double fromLatitude, final double fromLongitude, final double toLatitude, final double toLongitude, final String type) {
        //Getting the URL
        String url = makeURL(fromLatitude, fromLongitude, toLatitude, toLongitude, type);

        //Showing a dialog till we get the route
        final ProgressDialog loading = ProgressDialog.show(this, getString(R.string.get_route), getString(R.string.wait), false, false);

        //Creating a string request
        StringRequest stringRequest = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        //Calling the method drawPath to draw the path
                        drawPath(response, fromLatitude, fromLongitude, toLatitude, toLongitude, type);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                    }
                });

        //Adding the request to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    //The parameter is the server response
    public void drawPath(String result, double fromLatitude, double fromLongitude, double toLatitude, double toLongitude, String type) {
        //Getting both the coordinates
        LatLng from = new LatLng(fromLatitude, fromLongitude);
        LatLng to = new LatLng(toLatitude, toLongitude);

        //Calculating the distance in meters
        Double distance = 0.0;//SphericalUtil.computeDistanceBetween(from, to);
        // tv_show_distance_time.setText(distance + getString(R.string.meters));

        //Displaying the distance
        Toast.makeText(this, String.valueOf(distance + getString(R.string.meters)), Toast.LENGTH_SHORT).show();
        System.out.println("result:" + result.toString());

        try {
            //Parsing json
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);
            if (type.startsWith("d")) {
                Polyline line = mMap.addPolyline(new PolylineOptions()
                        .addAll(list)
                        .width(10)
                        .color(Color.RED)
                        .geodesic(true));
            } else {
                Polyline line = mMap.addPolyline(new PolylineOptions()
                        .addAll(list)
                        .width(10)
                        .color(Color.BLUE)
                        .geodesic(true));
            }


        } catch (JSONException e) {

        }
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    //Draw Path
    public String makeURL(double sourcelat, double sourcelog, double destlat, double destlog, String type) {
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString
                .append(Double.toString(sourcelog));
        urlString.append("&destination=");// to
        urlString
                .append(Double.toString(destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));
        //driving,walking=type
        urlString.append("&sensor=false&mode=" + type + "&alternatives=true");
        urlString.append("&key=AIzaSyCdrwbbVirC_OROZl7BXpeIUJYCBUyWmGM");
        return urlString.toString();
    }


    public void loadImageElseWhite(String image, ImageView imageView, final ProgressBar progress) {
        System.out.println("image:" + image);


        try {
            if (image != null && image.length() > 0) {

                Glide.with(FertilizeBookingDetails.this).load(image).
                        listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                progress.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                progress.setVisibility(View.GONE);
                                return false;
                            }
                        }).thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL).error(R.mipmap.ic_launcher)
                        .into(imageView);
            } else {
                Glide.with(FertilizeBookingDetails.this)
                        .load("")
                        .placeholder(R.mipmap.ic_launcher)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(imageView);
            }
        } catch (IllegalArgumentException e) {

        }

    }

    private void setCollapsingToolbarLayout() {
        //AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        //CollapsingToolbarLayout mCollapisngToolbar = (CollapsingToolbarLayout) appBarLayout.findViewById(R.id.toolbar_layout);
        //mCollapisngToolbar.setTitle("Equipment Details Name");

        //Set the color of collapsed toolbar text
        //mCollapisngToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.common_white));

        //This will set Expanded text to transparent so it wount overlap the content of the toolbar
        //mCollapisngToolbar.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));

    }
}
