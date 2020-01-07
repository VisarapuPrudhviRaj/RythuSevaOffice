package nk.bluefrog.rythusevaoffice.activities.bulkbooking;

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
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.Interpolator;
import android.widget.EditText;
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
import nk.bluefrog.library.network.ResponseListener;
import nk.bluefrog.library.network.RestServiceWithVolle;
import nk.bluefrog.library.utils.Helper;
import nk.bluefrog.library.utils.PrefManger;
import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.activities.home.MPEOHomeActivity;
import nk.bluefrog.rythusevaoffice.utils.Constants;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;
import nk.bluefrog.rythusevaoffice.utils.DBTables;
import nk.mobileapps.spinnerlib.SearchableSpinner;
import nk.mobileapps.spinnerlib.SpinnerData;

public class EquipmentBulkBookingRequestActivity extends BluefrogActivity implements ResponseListener, SearchableSpinner.SpinnerListener {
    String seeds;
    BulkBookinRequestPojo bulkBookinRequestPojo;
    DBHelper dbHelper;
    String gpsString = "";
    LatLng from, to;
    Double itemprice;
    ImageView iv_equipment;
    TextView tv_discount, tv_distance, tv_Name, tv_equipName, tv_rent, tv_Variety, tv_seedName, tv_order_qty, tv_item_price, tv_total_amount, tv_discount_value, tv_bill_pay, tv_bill_discount;
    EditText et_quantity, et_delivery_address;
    String bookingid;
    private GoogleMap mMap;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("Seeds", seeds);
        outState.putSerializable("BBR", bulkBookinRequestPojo);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        seeds = savedInstanceState.getString("Seeds");
        bulkBookinRequestPojo = (BulkBookinRequestPojo) savedInstanceState.getSerializable("BBR");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulk_booking_request);
        setToolBar(getString(R.string.bulk_booking_order), "");
        dbHelper = new DBHelper(this);
        if (getIntent().getExtras().containsKey("Seeds")) {
            seeds = getIntent().getStringExtra("Seeds");
            bulkBookinRequestPojo = (BulkBookinRequestPojo) getIntent().getExtras().getSerializable("BBR");
            findViews();
        } else {
            Helper.showToast(this, getResources().getString(R.string.key_missing));
            finish();
        }
    }

    public void setToolBar(String titleName, String subtitle) {
        Toolbar toolbar = (Toolbar) findViewById(nk.bluefrog.library.R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    double requiredquantity;

    private void findViews() {
        setMap();
        try {

            JSONObject jsonObject = new JSONObject(seeds);
            /*if (!jsonObject.getString("Address").trim().equals("")) {
                ((TextView) findViewById(R.id.tv_seedAddress)).setText(getString(R.string.address) + jsonObject.getString("Address").trim() +
                        "\n" + getString(R.string.panch) + " : " + jsonObject.getString("Panchayat") + "\n" + getString(R.string.mandal_) + " : " + jsonObject.getString("Mandal") + "\n" + getString(R.string.district_) + " :" + jsonObject.getString("District"));
            } else {*/
            String gpsPoints[] = jsonObject.getString("GPS").trim().split("\\$");
            LatLng Tloc = new LatLng(Double.parseDouble(gpsPoints[0]), Double.parseDouble(gpsPoints[1]));
            to = Tloc;
            //setAddress(to);
            // }

            et_delivery_address = (EditText) findViewById(R.id.et_delivery_address);
            String seed_name = jsonObject.getString("SeedCategory").trim();
            tv_discount = (TextView) findViewById(R.id.tv_discount);
            tv_discount.setText(jsonObject.getString("Discount") + "% Discount");
            tv_distance = (TextView) findViewById(R.id.tv_distance);
            requiredquantity = Double.parseDouble(jsonObject.getString("Min_Quantity").trim().equalsIgnoreCase("null")?"0":jsonObject.getString("Min_Quantity"));
            tv_distance.setText(getResources().getString(R.string.mini_quantity) + requiredquantity);
            tv_Name = (TextView) findViewById(R.id.tv_Name);
            tv_Name.setText(jsonObject.getString("ShopName"));
            tv_equipName = (TextView) findViewById(R.id.tv_equipName);
            tv_equipName.setText(seed_name);
            tv_rent = (TextView) findViewById(R.id.tv_rent);
            itemprice = Double.parseDouble(jsonObject.getString("price")) / 100;
            tv_rent.setText(getResources().getString(R.string.price) + ":" + itemprice);
            tv_item_price = (TextView) findViewById(R.id.tv_item_price);
            tv_item_price.setText(itemprice + "");
            tv_bill_discount = (TextView) findViewById(R.id.tv_bill_discount);
            if (jsonObject.getString("Discount").equalsIgnoreCase("null") ) {
                tv_bill_discount.setText("0");
            } else {
                tv_bill_discount.setText(jsonObject.getString("Discount") + " % " + getResources().getString(R.string.discount));
            }
            tv_Variety = (TextView) findViewById(R.id.tv_Variety);
            tv_Variety.setText("Seeds");
            tv_seedName = (TextView) findViewById(R.id.tv_seedName);
            tv_seedName.setText(bulkBookinRequestPojo.getCategory_name());
            String image = jsonObject.getString("ImagePath").trim().replace("\\/", "/");
            ImageView iv_equipment = (ImageView) findViewById(R.id.iv_equipment);
            ProgressBar progress = (ProgressBar) findViewById(R.id.progress);
            loadImageElseWhite(image, iv_equipment, progress);

            tv_order_qty = (TextView) findViewById(R.id.tv_order_qty);
            tv_total_amount = (TextView) findViewById(R.id.tv_total_amount);
            tv_discount_value = (TextView) findViewById(R.id.tv_discount_value);
            tv_bill_pay = (TextView) findViewById(R.id.tv_bill_pay);

            et_quantity = (EditText) findViewById(R.id.et_quantity);
            et_quantity.setText(bulkBookinRequestPojo.getQuantity());
            setPaidamount();
            et_quantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    setPaidamount();

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    double quantity;

    public void setPaidamount() {
        if (et_quantity.getText().toString().trim().startsWith("00")) {
            et_quantity.setText("0");
        } else if (et_quantity.getText().toString().trim().startsWith(".")) {
            et_quantity.setText("0");
        } else if (et_quantity.getText().toString().trim().contains("..")) {
            et_quantity.setText("" + et_quantity.getText().toString().trim().split("\\.")[0]);
        } else {
            String value = et_quantity.getText().toString().trim();

            value = value.length() == 0 ? "0" : value;

            try {
                Float.parseFloat(et_quantity.getText().toString().trim());
            } catch (Exception e) {
                if (value.contains(".") && value.endsWith("."))
                    et_quantity.setText(value.substring(0, value.length() - 1));
            }

            int beforeDotlen = value.substring(0, value.indexOf(".") == -1 ? 0 : value.indexOf(".")).length();
            int afterDotlen = value.substring(value.indexOf(".") + 1).length();

            if (afterDotlen > 2) {
                if (value.contains("."))
                    et_quantity.setText(value.substring(0, value.indexOf(".")) + "." + value.substring(value.indexOf(".") + 1, value.indexOf(".") + 3));
            }
            quantity = Double.parseDouble(value);
            tv_order_qty.setText(quantity + "");
           Double price = Double.valueOf(tv_item_price.getText().toString());
            double amount_total = quantity * price;
            tv_total_amount.setText(amount_total + "");
            String[] discountsplit = tv_bill_discount.getText().toString().split(" ");
            Double discount = Double.valueOf(discountsplit[0]);
            Double calcdiscount = ((amount_total * discount) / 100);
            tv_discount_value.setText(calcdiscount + "");
            tv_bill_pay.setText(amount_total - calcdiscount + "");
        }
    }

    public void onclickbbr(View view) {
        if (et_quantity.getText().toString().length() == 0) {
            et_quantity.setError("Enter Quantity");
        } else if (et_delivery_address.getText().toString().length() == 0) {
            et_delivery_address.setError("Enter Delivery Address");
        } else if (requiredquantity >= quantity) {
            et_quantity.setError("Requested Quantity Is Not Acceptable By Dealer");
        } else {
            serverHit();
        }
    }

    private void serverHit() {

        try {
            JSONObject jsonObject = new JSONObject(seeds);
            JSONObject sendingObj = new JSONObject();
            sendingObj.put("commodity", bulkBookinRequestPojo.getCommodity());
            sendingObj.put("category_id", bulkBookinRequestPojo.getCategory_id());
            sendingObj.put("subcategory_id", bulkBookinRequestPojo.getSubcategory_id());
            sendingObj.put("category_name", bulkBookinRequestPojo.getCategory_name());
            sendingObj.put("quantity", et_quantity.getText().toString().trim());
            sendingObj.put("delivery_address", et_delivery_address.getText().toString().trim());
            sendingObj.put("lat", bulkBookinRequestPojo.getLat());
            sendingObj.put("lng", bulkBookinRequestPojo.getLng());
            sendingObj.put("shop_id", jsonObject.getString("ShopID"));
            sendingObj.put("shop_name", jsonObject.getString("ShopName"));
            sendingObj.put("shop_owner_name", jsonObject.getString("OwnerName"));
            sendingObj.put("shop_phnumber", jsonObject.getString("MobileNo"));
            sendingObj.put("shop_image", jsonObject.getString("ImagePath"));
            sendingObj.put("shop_address", jsonObject.getString("Address"));
            sendingObj.put("shop_gps", jsonObject.getString("GPS"));
            sendingObj.put("shop_discount", jsonObject.getString("Discount"));
            sendingObj.put("shop_mini_order_quantity", jsonObject.getString("Min_Quantity"));
            sendingObj.put("shop_category_quantity", jsonObject.getString("Quantity"));
            sendingObj.put("shop_categroy_price", jsonObject.getString("price"));
            sendingObj.put("DID", dbHelper.getTableColData(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.DID).toString().substring(2, dbHelper.getTableColData(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.DID).toString().length() - 2));
            sendingObj.put("MPEOPhNo", PrefManger.getSharedPreferencesString(this, Constants.sp_mobile, ""));
            sendingObj.put("MPEOName", dbHelper.getTableColData(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.NAME).toString().substring(2, dbHelper.getTableColData(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.NAME).toString().length() - 2));


            sendingObj.put("User_Id", dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.UNIQUE_ID));
            sendingObj.put("User_Type", "O");
            sendingObj.put("OfficerType", dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.OFFICER_TYPE));
            sendingObj.put("IMEI", Helper.getIMEINumber(this));
            sendingObj.put("Version", getString(R.string.version));

            RestServiceWithVolle soapService = new RestServiceWithVolle(this, this, 1, Constants.BASE_URL
                    +    "OfficerBulkRequest",POST, true);

            soapService.loadRequest(sendingObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onSuccess(int responseCode, String response) {
        Log.d("response", response);
        if (responseCode == 1) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getString("status").contentEquals("200")) {
                    bookingid = jsonObject.getString("BookingID");
                    Savedata();
                } else {
                    Helper.showToast(EquipmentBulkBookingRequestActivity.this, "Bulk Booking Request Failed! Please Try Again....");
                }


            } catch (JSONException e) {
                e.printStackTrace();
                Helper.showToast(EquipmentBulkBookingRequestActivity.this, "Bulk Booking Request Failed! Please Try Again....");
            }
        }


    }

    public void Savedata() {
        long res;
        JSONObject jsonObject = null;
        try {
           /* booking_id, commodity, category_id, subcategory_id,
                    category_name, quantity, delivery_address, lat, lng, shop_id, shop_name,
                    shop_owner_name, shop_phnumber,
                    shop_image, shop_address, shop_gps, shop_discount, shop_mini_order_quantity,
                    shop_category_quantity, shop_categroy_price,
                    MPEOName,MPEOPhNo,DID*/
            jsonObject = new JSONObject(seeds);
            res = dbHelper.insertintoTable(DBTables.BulkBookingTracker.TABLE_NAME,
                    DBTables.BulkBookingTracker.cols,
                    new String[]{bookingid,
                            bulkBookinRequestPojo.getCommodity(),
                            bulkBookinRequestPojo.getCategory_id(),
                            bulkBookinRequestPojo.getSubcategory_id(),
                            bulkBookinRequestPojo.getCategory_name(),
                            et_quantity.getText().toString().trim(),
                            et_delivery_address.getText().toString().trim(),
                            bulkBookinRequestPojo.getLat(),
                            bulkBookinRequestPojo.getLng(),
                            jsonObject.getString("ShopID"),
                            jsonObject.getString("ShopName"),
                            jsonObject.getString("OwnerName"),
                            jsonObject.getString("MobileNo"),
                            jsonObject.getString("ImagePath"),
                            jsonObject.getString("Address"),
                            jsonObject.getString("GPS"),
                            jsonObject.getString("Discount"),
                            jsonObject.getString("Min_Quantity"),
                            jsonObject.getString("Quantity"),
                            jsonObject.getString("price"),
                            dbHelper.getTableColData(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.NAME).toString().substring(2, dbHelper.getTableColData(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.NAME).toString().length() - 2),
                            PrefManger.getSharedPreferencesString(this, Constants.sp_mobile, ""),
                            dbHelper.getTableColData(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.DID).toString().substring(2, dbHelper.getTableColData(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.DID).toString().length() - 2)
                    });


            if (res > 0) {
                Helper.showToast(EquipmentBulkBookingRequestActivity.this, getString(R.string.submit_suceess));
                Intent intent = new Intent(EquipmentBulkBookingRequestActivity.this, MPEOHomeActivity.class);
                startActivity(intent);
            } else {
                Helper.AlertMesg(EquipmentBulkBookingRequestActivity.this, getString(R.string.not_submit));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onError(int responseCode, String error) {

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
                            geocoder = new Geocoder(EquipmentBulkBookingRequestActivity.this, Locale.getDefault());
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

                                    /*if (!TextUtils.isEmpty(city) && !city.equalsIgnoreCase("null"))
                                        sb.append(", " + city);

                                    if (!TextUtils.isEmpty(state) && !state.equalsIgnoreCase("null"))
                                        sb.append(", " + state);

                                    if (!TextUtils.isEmpty(country) && !country.equalsIgnoreCase("null"))
                                        sb.append(", " + country);

                                    if (!TextUtils.isEmpty(postalCode) && !postalCode.equalsIgnoreCase("null"))
                                        sb.append(", " + postalCode);
*/
                                    ((TextView) findViewById(R.id.tv_seedAddress)).setText(getString(R.string.address) + " : " + sb.toString());
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
            Helper.showToast(EquipmentBulkBookingRequestActivity.this, getString(R.string.no_net));
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
                if (ActivityCompat.checkSelfPermission(EquipmentBulkBookingRequestActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(EquipmentBulkBookingRequestActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                    JSONObject jsonObject = new JSONObject(seeds);
                    //System.out.println("GPS TO:" + jsonObject.getString("Equipment_GPS").trim());
                    String gpsPoints[] = jsonObject.getString("GPS").trim().split("\\$");
                    LatLng Tloc = new LatLng(Double.parseDouble(gpsPoints[0]), Double.parseDouble(gpsPoints[1]));
                    to = Tloc;
                    drawMarker(jsonObject.getString("GPS").trim(), 1, jsonObject.getString("SeedCategory").trim(), itemprice + "");
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
            markerOptions.position(from).title(getString(R.string.seeds_name) + title).snippet(getString(R.string.price) + ":" + snippet).flat(true);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        } else {
            //list_user
            //to
            String latlng[] = gpsPoint.toString().trim().split("\\$");
            to = new LatLng(Double.parseDouble(latlng[0]), Double.parseDouble(latlng[1]));
            // Setting latitude and longitude for the marker
            markerOptions.position(to).title(title).snippet(snippet).flat(true);
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

                Glide.with(EquipmentBulkBookingRequestActivity.this).load(image).
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
                Glide.with(EquipmentBulkBookingRequestActivity.this)
                        .load("")
                        .placeholder(R.mipmap.ic_launcher)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(imageView);
            }
        } catch (IllegalArgumentException e) {

        }

    }

    @Override
    public void onItemsSelected(View view, List<SpinnerData> list, int i) {

    }
}
