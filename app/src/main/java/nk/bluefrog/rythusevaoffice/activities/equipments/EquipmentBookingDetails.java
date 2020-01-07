package nk.bluefrog.rythusevaoffice.activities.equipments;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import nk.bluefrog.library.BluefrogActivity;
import nk.bluefrog.library.gps.GPSActivity;
import nk.bluefrog.library.network.ResponseListener;
import nk.bluefrog.library.network.RestServiceWithVolle;
import nk.bluefrog.library.utils.Helper;
import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.utils.Constants;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;
import nk.bluefrog.rythusevaoffice.utils.DBTables;

import static nk.bluefrog.library.network.ResponseListener.POST;


public class EquipmentBookingDetails extends BluefrogActivity implements View.OnClickListener {

    public static EquipmentBookingDetails instance;
    String equipment;
    DBHelper dbHelper;
    String gpsString = "";
    LatLng from, to;
    EditText et_userName, et_userMobile_No, et_GPS, et_Address, et_Purpose, et_Start_Date, et_noofhours, et_End_Date;
    ImageView iv_image;
    Button bt_bookEquip;
    String commodityType;
    TextInputLayout input_layout_Start_Date, input_layout_noofhours, input_layout_End_Date,
            input_layout_userName, input_layout_Address, input_layout_userMobile_No, input_layout_Purpose;


    //Intent intent = getIntent();
    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };
    DatePickerDialog.OnDateSetListener date2 = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel2();
        }

    };
    String gpsData = "", accuracy = "";
    private GoogleMap mMap;
    private int gpsRequestCode = 101;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("Equipment", equipment);
        outState.putString("commodityType", commodityType);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        equipment = savedInstanceState.getString("Equipment");
        commodityType = savedInstanceState.getString("commodityType");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.equipment_booking_details);
        instance = this;
        setToolBar(this, getString(R.string.details_book), getString(R.string.for_book));
        setToolBarWithID(getString(R.string.details_book), getString(R.string.for_book), R.id.toolbar_);
        dbHelper = new DBHelper(this);
        if (getIntent().getExtras().containsKey("Equipment")) {
            equipment = getIntent().getStringExtra("Equipment");
            commodityType = getIntent().getStringExtra("commodityType");
            Log.d("json", equipment);
            findViews();
        } else {
            Helper.showToast(this, getResources().getString(R.string.key_missing));
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        instance = this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        et_Start_Date.setText(sdf.format(myCalendar.getTime()));
        //  et_Start_Date.setText(Helper.getTodayDate());
    }

    private void updateLabel2() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        et_End_Date.setText(sdf.format(myCalendar.getTime()));
    }

    public Long dateAfterDays(String str, int in) {


        String arr[] = str.split("\\/");

        Calendar cal = Calendar.getInstance();

        if (arr[0].length() > 2) {

            cal.set(Calendar.YEAR, Integer.parseInt(arr[0]));

            cal.set(Calendar.MONTH, Integer.parseInt(arr[1]) - 1);

            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(arr[2]));

        } else {

            cal.set(Calendar.YEAR, Integer.parseInt(arr[2]));

            cal.set(Calendar.MONTH, Integer.parseInt(arr[1]) - 1);

            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(arr[0]));

        }


        cal.add(Calendar.DATE, in); // add how many days we want


        return cal.getTime().getTime();

    }


    private void findViews() {

        et_userName = (EditText) findViewById(R.id.et_userName);
        et_Address = (EditText) findViewById(R.id.et_Address);
        et_userMobile_No = (EditText) findViewById(R.id.et_userMobile_No);
        et_Purpose = (EditText) findViewById(R.id.et_Purpose);
        et_Start_Date = (EditText) findViewById(R.id.et_Start_Date);
        et_End_Date = (EditText) findViewById(R.id.et_End_Date);
        et_noofhours = (EditText) findViewById(R.id.et_noofhours);


        input_layout_Start_Date = findViewById(R.id.input_layout_Start_Date);
        input_layout_noofhours = findViewById(R.id.input_layout_noofhours);
        input_layout_End_Date = findViewById(R.id.input_layout_End_Date);
        input_layout_userName = findViewById(R.id.input_layout_userName);
        input_layout_Address = findViewById(R.id.input_layout_Address);
        input_layout_userMobile_No = findViewById(R.id.input_layout_userMobile_No);
        input_layout_Purpose = findViewById(R.id.input_layout_Purpose);

        et_GPS = (EditText) findViewById(R.id.et_GPS);
        iv_image = (ImageView) findViewById(R.id.iv_image);

        et_userName.setText(dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.NAME));
        et_userMobile_No.setText(dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.MOBILE));
        bt_bookEquip = findViewById(R.id.bt_bookEquip);
        bt_bookEquip.setOnClickListener(this);

        et_Start_Date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                // et_Start_Date.setText(Helper.getTodayDate());
                DatePickerDialog dialog = new DatePickerDialog(EquipmentBookingDetails.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                dialog.show();
            }
        });

        et_End_Date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (TextUtils.isEmpty(et_Start_Date.getText().toString().trim())) {
                    input_layout_Start_Date.setError(getResources().getString(R.string.enter_start_date));
                    input_layout_Start_Date.requestFocus();
                } else {
                    DatePickerDialog dialog2 = new DatePickerDialog(EquipmentBookingDetails.this, date2, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH));


                    dialog2.getDatePicker().setMinDate(dateAfterDays(et_Start_Date.getText().toString().trim(), 2));
                    dialog2.show();
                }

            }
        });

        iv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gpsIntent = new Intent(EquipmentBookingDetails.this, GPSActivity.class);
                startActivityForResult(gpsIntent, gpsRequestCode);
            }
        });


        setMap();
        try {
            //Equipment_Id":"213123","Barcode":"23423423",
            //"Serial_No":"24234","Equipment_Rent":"231",
            // "Equipment_Image_Path":"URL","Equipment_GPS":"2",
            //"Address":"EE", User_Name:”dfsdf”,
            // User_Id:”2342342342”,"Owner_Mobile_No":"9000503078",
            //"Equipment_Status_Id":"Name1",”Distance_in_Km”:””}
            JSONObject jsonObject = new JSONObject(equipment);


            if (!jsonObject.getString("Address").trim().equals("")) {
                ((TextView) findViewById(R.id.tv_eqAddress)).setText(getString(R.string.address) + " : " + jsonObject.getString("Address").trim());
            } else {
                String gpsPoints[] = jsonObject.getString("Equipment_GPS").trim().split("-");
                LatLng Tloc = new LatLng(Double.parseDouble(gpsPoints[0]), Double.parseDouble(gpsPoints[1]));
                to = Tloc;
                setAddress(to);
            }

            ((TextView) findViewById(R.id.tv_eqDist)).setText(getString(R.string.distance) + jsonObject.getString("Distance_in_Km") + " KM");

            String eq_name = dbHelper.getValueByIds(DBTables.Category.TABLE_NAME, DBTables.Category.EQUIPMENT_NAME, new String[]{DBTables.Category.EQUIPMENT_NAME}, new String[]{jsonObject.getString("Equipment_Id").trim()});
            ((TextView) findViewById(R.id.tv_eqName)).setText(eq_name + "");
            ((TextView) findViewById(R.id.tv_eqSerialNo)).setText(getString(R.string.s_no) + jsonObject.getString("Serial_No").trim());
            ((TextView) findViewById(R.id.tv_eqRent)).setText(getString(R.string.rent_book) + jsonObject.getString("Equipment_Rent").trim() + " per hourly based.");
            try {
                if (!jsonObject.getString("Equipment_Rent_Acre").toString().trim().equals("")) {
                    ((TextView) findViewById(R.id.tv_eqArc)).setText(getString(R.string.rent_book) + jsonObject.getString("Equipment_Rent_Acre").trim() + " per acre based.");
                } else {
                    ((TextView) findViewById(R.id.tv_eqArc)).setText(getString(R.string.rent_book) + "not available" + " per acre based.");
                }
            } catch (Exception e) {
            }


            String image = jsonObject.getString("Equipment_Image_Path").trim().replace("\\/", "/");
            ImageView iv_Equipment_Image = (ImageView) findViewById(R.id.iv_Equipment_Image);
            ProgressBar progress = (ProgressBar) findViewById(R.id.progress);
            loadImageElseWhite(image, iv_Equipment_Image, progress);


            ((TextView) findViewById(R.id.tv_eqStatus)).setText(getResources().getStringArray(R.array.statusdetails)[Integer.parseInt(jsonObject.getString("Equipment_Status_Id").trim())]);
            ((TextView) findViewById(R.id.tv_ownerName)).setText(getString(R.string.str_name) + jsonObject.getString("User_Name").trim());
            ((TextView) findViewById(R.id.tv_ownerPhone)).setText(getString(R.string.phone) + jsonObject.getString("Owner_Mobile_No").trim());


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (Activity.RESULT_OK == resultCode && data != null) {
            if (gpsRequestCode == requestCode) {
                //take gps data here
                Helper.showToast(EquipmentBookingDetails.this, getResources().getString(R.string.gps_captures_success));
                accuracy = data.getExtras().getString(GPSActivity.LOC_ACCURACY);
                gpsData = data.getExtras().getString(GPSActivity.LOC_DATA);
                et_GPS.setText(gpsData);
            }

        }
    }

    private void call() {

        try {
            JSONObject jsonObject = new JSONObject(equipment);
            String phone = jsonObject.getString("Owner_Mobile_No").trim();
            String aadharNo = dbHelper.getTabCol(DBTables.Register.TABLE_NAME, DBTables.Register.user_aadharid);
            Constants.call(dbHelper,phone, EquipmentBookingDetails.this, Constants.Type_Equipments, aadharNo);

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



            case R.id.bt_bookEquip:
                if (isValidate()) {
                    serverHit();
                }
                break;
            default:
                break;
        }
    }

    private boolean isValidate() {

        input_layout_userName.setErrorEnabled(false);
        input_layout_Address.setErrorEnabled(false);
        input_layout_userMobile_No.setErrorEnabled(false);
        input_layout_Purpose.setErrorEnabled(false);
        input_layout_Start_Date.setErrorEnabled(false);
        input_layout_noofhours.setErrorEnabled(false);
        boolean flag = true;
        if (TextUtils.isEmpty(et_userName.getText().toString().trim())) {
            input_layout_userName.setError(getResources().getString(R.string.enter_username));
            input_layout_userName.requestFocus();
            flag = false;
        } else if (!Helper.isValidMobile(et_userMobile_No)) {
            input_layout_userMobile_No.setError(getResources().getString(R.string.enter_phone));
            input_layout_userMobile_No.requestFocus();
            flag = false;
        } else if (TextUtils.isEmpty(gpsData)) {
            Helper.showToast(EquipmentBookingDetails.this, getResources().getString(R.string.cap_gps));
            flag = false;
        } else if (TextUtils.isEmpty(et_Address.getText().toString().trim())) {
            input_layout_Address.setError(getResources().getString(R.string.enter_address));
            input_layout_Address.requestFocus();
            flag = false;
        } else if (TextUtils.isEmpty(et_Purpose.getText().toString().trim())) {
            input_layout_Purpose.setError(getResources().getString(R.string.enter_purpose));
            input_layout_Purpose.requestFocus();
            flag = false;
        } else if (TextUtils.isEmpty(et_Start_Date.getText().toString().trim())) {
            input_layout_Start_Date.setError(getString(R.string.which_date));
            input_layout_Start_Date.requestFocus();
            flag = false;
        } else if (TextUtils.isEmpty(et_noofhours.getText().toString().trim())) {
            input_layout_noofhours.setError(getString(R.string.no_of_hours_need));
            input_layout_noofhours.requestFocus();
            flag = false;
        } else if (TextUtils.isEmpty(et_End_Date.getText().toString().trim())) {
            input_layout_End_Date.setError(getResources().getString(R.string.enter_end_date));
            input_layout_End_Date.requestFocus();
            flag = false;
        }
        return flag;
    }

    private void serverHit() {
        try {

            final JSONObject jsonObject = new JSONObject(equipment);
            JSONObject bookDetailsObj = new JSONObject();
            bookDetailsObj.put("User_Id", dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.UNIQUE_ID));
            bookDetailsObj.put("MobileNo", et_userMobile_No.getText().toString().trim());
            bookDetailsObj.put("User_Name", et_userName.getText().toString().trim());
            bookDetailsObj.put("User_Type", "O");
            bookDetailsObj.put("OfficerType", dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.OFFICER_TYPE));

            bookDetailsObj.put("Owner_Type", jsonObject.getString("User_Type").trim());
            bookDetailsObj.put("LicenceNo", jsonObject.getString("LicenceNo").trim());

            bookDetailsObj.put("Owner_User_Id", jsonObject.getString("Unique_Id").trim());
            bookDetailsObj.put("Equipment_Id", jsonObject.getString("Equipment_Id").trim());
            bookDetailsObj.put("Barcode", jsonObject.getString("Barcode").trim());
            bookDetailsObj.put("Address", et_Address.getText().toString().trim());
            bookDetailsObj.put("User_GPS", gpsData);
            bookDetailsObj.put("Start_Date", et_Start_Date.getText().toString().trim());
            bookDetailsObj.put("End_Date", et_End_Date.getText().toString().trim());
            bookDetailsObj.put("No_of_Hours", et_noofhours.getText().toString().trim());
            bookDetailsObj.put("Purpose", et_Purpose.getText().toString().trim());

            if (jsonObject.getString("User_Type").trim().equals("D")) {
                bookDetailsObj.put("Commodity", commodityType);
            } else {
                bookDetailsObj.put("Commodity", "");
            }
            bookDetailsObj.put("Order_Status_Id","");
            bookDetailsObj.put("IMEI",Helper.getIMEINumber(this));
            bookDetailsObj.put("Version",getString(R.string.version));


            RestServiceWithVolle soapService = new RestServiceWithVolle(this, new ResponseListener() {
                @Override
                public void onSuccess(int responseCode, String response) {

                    if (response.contains("200")) {
                        //200=Success
                        System.out.println("200- strResponse:" + response);
                        insertUserOrder(response);
                    } else {
                        try {
                            JSONObject jsonObject1 = new JSONObject(response);
                            Helper.AlertMsg(EquipmentBookingDetails.this, jsonObject1.getString("message") + "\n" +
                                    getString(R.string.booking_failed));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Helper.AlertMsg(EquipmentBookingDetails.this, e.getMessage() + "\n" +
                                    getString(R.string.booking_failed));
                        }
                    }
                }

                @Override
                public void onError(int responseCode, String error) {
                   /* error = "{\"Owner_User_Id\":\"1377938\",\"Order_Status_Id\":\"1\",\"Equipment_Id\":\"2\",\"Barcode\":\"03240200220190203015716\",\"Customer_ID\":\"2204734\",\"Customer_Name\":\"Prudhvi\",\"Mobile_No\":\"9618034357\",\"Address\":\"test\",\"Customer_GPS\":\"17.6868-83.2185\",\"Start_Date\":\"12/06/2019\",\"End_Date\":\"14/06/2019\",\"No_of_Hours\":\"2\",\"Purpose\":\"test\"}";
                    insertUserOrder(error);*/

                }
            }, 1, Constants.BASE_URL
                    + "EquipmentBookingRequest", POST, true);
            soapService.loadRequest(bookDetailsObj);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void insertUserOrder(String str) {
        /*Equipment_Id, Barcode, Serial_No,
                Equipment_Rent, Equipment_Image_Path, Equipment_GPS,
                Equipment_Address, Owner_Name, Owner_Id, Owner_Mobile_No, Equipment_Status_Id,
                Booking_Id, Customer_Name, Customer_Mobile_No, Customer_Address, Customer_GPS,
                Start_Date, End_Date, Purpose, Order_Status*/


        try {
//              Equipment_Id":"213123","Barcode":"23423423",
//              "Serial_No":"24234","Equipment_Rent":"231",
//              "Equipment_Image_Path":"URL","Equipment_GPS":"2",
//              "Address":"EE", User_Name:”dfsdf”,
//              User_Id:”2342342342”,"Owner_Mobile_No":"9000503078",
//              "Equipment_Status_Id":"Name1",”Distance_in_Km”:””}
            JSONObject jsonObjectRes = new JSONObject(str);
            String bookingID = jsonObjectRes.getString("Booking_Id");
            JSONObject jsonObject = new JSONObject(equipment);

            long result = dbHelper.insertintoTable(DBTables.UserOrders.TABLE_NAME_, DBTables.UserOrders.equipOders_cols,
                    new String[]{jsonObject.getString("Equipment_Id").trim(),
                            jsonObject.getString("Barcode").trim(),
                            jsonObject.getString("Serial_No").trim(),
                            jsonObject.getString("Equipment_Rent").trim(),
                            jsonObject.getString("Equipment_Image_Path").trim(),
                            jsonObject.getString("Equipment_GPS").trim(),
                            jsonObject.getString("Address").trim(),
                            jsonObject.getString("User_Name").trim(),
                            jsonObject.getString("Unique_Id").trim(),
                            jsonObject.getString("Owner_Mobile_No").trim(),
                            jsonObject.getString("Equipment_Status_Id").trim(),
                            bookingID,//Booking_Id
                            et_userName.getText().toString().trim(),
                            et_userMobile_No.getText().toString().trim(),
                            et_Address.getText().toString().trim(),
                            et_GPS.getText().toString().trim(),
                            et_Start_Date.getText().toString().trim() + "," + et_End_Date.getText().toString().trim(),
                            et_noofhours.getText().toString().trim(),
                            et_Purpose.getText().toString(), "3",
                            et_userMobile_No.getText().toString().trim(),/*MPEO Mobile Number*/
                            jsonObject.getString("Equipment_Rent_Acre"),
                            Helper.getCurrentTodayDate("dd/MM/yyyy hh:mm aa"),
                            jsonObject.getString("User_Type").equals("D") ? commodityType : "",
                            jsonObject.getString("ShopName"),
                            jsonObject.getString("User_Type")

                    });


            if (result > 0) {
                Helper.showToast(EquipmentBookingDetails.this, getResources().getString(R.string.booking_success));
                EquipmentBookingDetails.this.finish();
                EquipmentBookingDetails.instance.finish();
            } else {
                Helper.showToast(EquipmentBookingDetails.this, getString(R.string.failed));
            }


        } catch (JSONException e) {
            e.printStackTrace();
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
                            geocoder = new Geocoder(EquipmentBookingDetails.this, Locale.getDefault());
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
            Helper.showToast(EquipmentBookingDetails.this, getString(R.string.no_net));
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
                if (ActivityCompat.checkSelfPermission(EquipmentBookingDetails.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(EquipmentBookingDetails.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                    System.out.println("GPS TO:" + jsonObject.getString("Equipment_GPS").trim());
                    String gpsPoints[] = jsonObject.getString("Equipment_GPS").trim().replace("--", "-").split("-");
                    LatLng Tloc = new LatLng(Double.parseDouble(gpsPoints[0]), Double.parseDouble(gpsPoints[1]));
                    to = Tloc;
                    String eq_name = dbHelper.getValueByIds(DBTables.Category.TABLE_NAME, DBTables.Category.EQUIPMENT_NAME, new String[]{DBTables.Category.EQUIPMENT_ID}, new String[]{jsonObject.getString("Equipment_Id").trim()});
                    drawMarker(jsonObject.getString("Equipment_GPS").trim().replace("--", "-"), 1, eq_name, getString(R.string.rent_book) + jsonObject.getString("Equipment_Rent").trim() + " per hourly based.");
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

            String latlng[] = gpsPoint.toString().trim().split("\\-");
            from = new LatLng(Double.parseDouble(latlng[0]), Double.parseDouble(latlng[1]));
            // Setting latitude and longitude for the marker
            markerOptions.position(from).title(getString(R.string.equipment_name) + title).snippet(snippet).flat(true);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        } else {
            //list_user
            //to
            String latlng[] = gpsPoint.toString().trim().split("\\-");
            to = new LatLng(Double.parseDouble(latlng[0]), Double.parseDouble(latlng[1]));
            // Setting latitude and longitude for the marker
            markerOptions.position(to).title(getString(R.string.equipment_name) + title).snippet(snippet).flat(true);
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

                Glide.with(EquipmentBookingDetails.this).load(image).
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
                Glide.with(EquipmentBookingDetails.this)
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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
