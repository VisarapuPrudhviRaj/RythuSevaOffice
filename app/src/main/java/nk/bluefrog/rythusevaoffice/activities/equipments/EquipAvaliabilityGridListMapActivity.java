package nk.bluefrog.rythusevaoffice.activities.equipments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import nk.bluefrog.library.BluefrogActivity;
import nk.bluefrog.library.gps.GPSActivity;
import nk.bluefrog.library.network.ResponseListener;
import nk.bluefrog.library.network.RestServiceWithVolle;
import nk.bluefrog.library.utils.Helper;
import nk.bluefrog.library.utils.PrefManger;
import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.activities.masterdata.MasterDataActivity;
import nk.bluefrog.rythusevaoffice.utils.Constants;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;
import nk.bluefrog.rythusevaoffice.utils.DBTables;


public class EquipAvaliabilityGridListMapActivity extends BluefrogActivity implements ResponseListener {

    final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1456;
    final int GPS_FLAG = 1245;
    DBHelper dbHelper;
    String gpsString = "", strResponse;
    RecyclerView mRecyclerView;
    GridListAdapter gridListAdapter;
    JSONArray jsonArray = new JSONArray();
    ImageView iv_switch;


    Handler geocoderHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    ((TextView) findViewById(R.id.tv_loc_search)).setText(locationAddress.trim().equals("null") ? "" : locationAddress);
                    ((TextView) findViewById(R.id.tv_equip_search)).setText(getString(R.string.all_equpments));
                    break;
                default:
                    ((TextView) findViewById(R.id.tv_loc_search)).setText("");
                    locationAddress = null;
            }
            Log.e("location Address=", locationAddress);
        }
    };

    Handler handlEquipment = new Handler() {
        public void handleMessage(Message msg) {
            closeProgressDialog();
            findViewById(R.id.tv_status).setVisibility(View.GONE);
            if (strResponse.contains("200")) {
                //200=Success
                loadRecycleView(strResponse);
            } else if (strResponse.contains("101")) {
                mRecyclerView.setVisibility(View.GONE);
                findViewById(R.id.tv_status).setVisibility(View.VISIBLE);
                Helper.AlertMesg(EquipAvaliabilityGridListMapActivity.this, getResources().getString(R.string.no_available));
            } else {
                mRecyclerView.setVisibility(View.GONE);
                findViewById(R.id.tv_status).setVisibility(View.VISIBLE);
                try {
                    JSONObject jsonObject = new JSONObject(strResponse);
                    Helper.AlertMesg(EquipAvaliabilityGridListMapActivity.this, jsonObject.getString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Helper.AlertMesg(EquipAvaliabilityGridListMapActivity.this, e.getMessage() + getResources().getString(R.string.failed));
                }

            }
        }
    };
    Dialog dlg;
    Spinner sp_type;
    Spinner sp_subtype;
    List<List<String>> ll_category = new ArrayList<>();
    List<List<String>> ll_subcategory = new ArrayList<>();
    Context context;
    String commodityType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equip_avaliability_grid_list_map);
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey("commodityType")) {
            commodityType = getIntent().getStringExtra("commodityType");
            context = this;
            dbHelper = new DBHelper(this);
            setToolBar("", "");
            gpsString = PrefManger.getSharedPreferencesString(this, Constants.sp_gps, "");
            findViews();
        } else {
            finish();
        }

    }

    private void findViews() {
        iv_switch = (ImageView) findViewById(R.id.iv_switch);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        onclick_Hit();
    }

    private void loadRecycleView(String strResponse) {
        // "equipments":[{"Equipment_Id":"213123","Barcode":"23423423","Serial_No":"24234","Equipment_Rent":"231","Equipment_Image_Path":"URL","Equipment_GPS":"2","Address":"EE", User_Name:”dfsdf”,User_Id:”2342342342”,"Owner_Mobile_No":"9000503078","Equipment_Status_Id":"Name1",”Distance_in_Km”:””}
        try {
            JSONObject jsonObject = new JSONObject(strResponse);
            jsonArray = jsonObject.getJSONArray("equipments");
            if (jsonArray.length() > 0) {
                findViewById(R.id.tv_status).setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setLayoutManager(linearLayoutManager);
                mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
                gridListAdapter = new GridListAdapter(this, jsonArray, getIntent().getExtras().getString("commodityType"));
                mRecyclerView.setAdapter(gridListAdapter);
            } else {
                findViewById(R.id.tv_status).setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            }

        } catch (Exception e) {

            findViewById(R.id.tv_status).setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }


    }


    private void setCollapsingToolbarLayout() {
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        CollapsingToolbarLayout mCollapisngToolbar = (CollapsingToolbarLayout) appBarLayout.findViewById(R.id.toolbar_layout);
        mCollapisngToolbar.setTitle(getString(R.string.equipments_availability));

        //Set the color of collapsed toolbar text
        mCollapisngToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.common_white));

        //This will set Expanded text to transparent so it wount overlap the content of the toolbar
        mCollapisngToolbar.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));
        ((TextView) findViewById(R.id.title)).setText(getString(R.string.equipments_availability));
    }

    public void onClick_LocationSearch(View view) {
        placeSearch();
    }

    public void onClick_EquipSearch1(View view) {
        int count = dbHelper.getCount(DBTables.Category.TABLE_NAME);
        if (count > 0) {
            getFilter(false);
        } else {

            serverHitForCategoryDetails();

            //Helper.AlertMesg(this, getString(R.string.please_update_categories));
        }

    }

    public void serverHitForCategoryDetails() {
        /*{"User_Id":"22","User_MobileNo":"7306676544","User_Type":"O","OfficerType":"1",
        "commodity":"1","IMEI":"358240051111110","Version":"1.1"}*/
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("User_Id", dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.UNIQUE_ID));
            jsonObject.put("User_MobileNo", dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.MOBILE));
            jsonObject.put("User_Type", "O");
            jsonObject.put("OfficerType", dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.OFFICER_TYPE));
            jsonObject.put("commodity", "6");
            jsonObject.put("IMEI", Helper.getIMEINumber(this));
            jsonObject.put("Version", getString(R.string.version));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RestServiceWithVolle soapService = new RestServiceWithVolle(this, this, 2, Constants.BASE_URL
                + Constants.METHOD_CROP_MASTER,POST, true);

        soapService.loadRequest(jsonObject);

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void getFilter(boolean flag) {
        dlg = new Dialog(this);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.dialog_equipfilter);
        dlg.setCancelable(true);
        dlg.show();


        sp_type = (Spinner) dlg.findViewById(R.id.sp_type);
        sp_type.setTag("0");
        sp_subtype = (Spinner) dlg.findViewById(R.id.sp_subtype);
        sp_subtype.setVisibility(View.VISIBLE);

        ll_category = dbHelper.getDataByQuery("select distinct CID,CName from Category ");

        Helper.setSpinnerLabelValueData(EquipAvaliabilityGridListMapActivity.this, sp_type, ll_category, "All", 0, 1, getString(R.string.eq_cat));

        sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                if (position == 0) {
                    sp_subtype.setTag("0");
                    PrefManger.putSharedPreferencesInt(context, Constants.sp_category_Id, 0);
                    PrefManger.putSharedPreferencesInt(context, Constants.sp_equipment_Id, 0);
                    List<List<String>> ll_category = dbHelper.getDataByQuery("select  EQUIPMENT_ID,EQUIPMENT_ID from category where CID=0");
                    Helper.setSpinnerLabelValueData(EquipAvaliabilityGridListMapActivity.this, sp_subtype, ll_category, "All", 0, 1, getString(R.string.eq_type));
                } else {
                    System.out.println("==== ID:" + sp_type.getTag() + " :" + ll_category.get(position - 1).get(0));
                    sp_type.setTag(ll_category.get(position - 1).get(0));
                    PrefManger.putSharedPreferencesInt(context, Constants.sp_category_Id, Integer.parseInt((String) sp_type.getTag()));
                    PrefManger.putSharedPreferencesInt(context, Constants.sp_equipment_Id, 0);
                    ll_subcategory = dbHelper.getDataByQuery("select  EQUIPMENT_ID,EQUIPMENT_NAME from " + DBTables.Category.TABLE_NAME + " where CID=" + (String) sp_type.getTag() + "");
                    for (int i = 0; i < ll_subcategory.size(); i++) {
                        System.out.println("ID_Name:" + ll_subcategory.get(i).get(0).toString() + ":" + ll_subcategory.get(i).get(1).toString());
                    }

                    Helper.setSpinnerLabelValueData(EquipAvaliabilityGridListMapActivity.this, sp_subtype, ll_subcategory, "All", 0, 1, "Select Equipment Type");
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sp_subtype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    if (ll_subcategory.size() > 0) {
                        sp_subtype.setTag(ll_subcategory.get(position - 1).get(0));
                        PrefManger.putSharedPreferencesInt(context, Constants.sp_equipment_Id, Integer.parseInt((String) sp_subtype.getTag()));
                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        Button btn_cancel = (Button) dlg.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dlg.dismiss();
            }
        });
        Button btn_apply = (Button) dlg.findViewById(R.id.btn_apply);
        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dlg.dismiss();

                String msg = "" + sp_type.getSelectedItem().toString().trim() + (sp_subtype.getSelectedItem().toString().trim().equals("All") ? "" : "," + sp_subtype.getSelectedItem().toString().trim());
                ((TextView) findViewById(R.id.tv_equip_search)).setText(msg);
                onclick_Hit();
            }
        });
        if (flag) {
            btn_cancel.setVisibility(View.GONE);
        }


    }

    private void placeSearch() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(EquipAvaliabilityGridListMapActivity.this);

            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
            Helper.showToast(EquipAvaliabilityGridListMapActivity.this, e.getMessage().trim());
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
            Helper.showToast(EquipAvaliabilityGridListMapActivity.this, e.getMessage().trim());
        }
    }

    private JSONObject getJsonObject() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("User_Id", dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.UNIQUE_ID));
            jsonObject.put("User_MobileNo", dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.MOBILE));
            jsonObject.put("User_Type", "O");
            jsonObject.put("OfficerType", dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.OFFICER_TYPE));
            jsonObject.put("Category_Id", PrefManger.getSharedPreferencesInt(this, Constants.sp_category_Id, 0) + "");
            jsonObject.put("Equipment_ID", PrefManger.getSharedPreferencesInt(this, Constants.sp_equipment_Id, 0) + "");
            jsonObject.put("GPS", gpsString);
            jsonObject.put("Distance", PrefManger.getSharedPreferencesString(this, Constants.sp_dist, "10000"));
            jsonObject.put("Prize", PrefManger.getSharedPreferencesString(this, Constants.sp_prize, "500"));
            jsonObject.put("Version", getString(R.string.version));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public void onclick_Hit() {
        String distance = PrefManger.getSharedPreferencesString(this, Constants.sp_dist, "10000");
        String Equipment_ID = PrefManger.getSharedPreferencesInt(this, Constants.sp_equipment_Id, 0) + "";
        String prize = PrefManger.getSharedPreferencesString(this, Constants.sp_prize, "500");
        String Category_Id = PrefManger.getSharedPreferencesInt(this, Constants.sp_category_Id, 0) + "";


        if (gpsString.contains("-")) {
/*
            String keys[] = {"GPS", "Distance", "Equipment_ID", "User_Id", "Prize", "Category_Id"};
            String values[] = {gpsString, distance + "", Equipment_ID + "", "", prize + "", Category_Id + ""};
*/

            String keys[] = {"postString"};
            String values[] = {getJsonObject().toString()};
            /*Change User_id value for Officer APP. Aadhar Number given above is for testing only*/
            if (Helper.isNetworkAvailable(getApplicationContext())) {
                serverHitForEquipments(getJsonObject(), getResources().getString(R.string.find_equips));
            } else {
                Helper.AlertMesg(this, getString(R.string.network));
            }
        } else {
            startActivityForResult(new Intent(getApplicationContext(),
                    GPSActivity.class), GPS_FLAG);
        }
    }

    private void serverHitForEquipments(JSONObject jsonObject, final String msg) {

        RestServiceWithVolle soapService = new RestServiceWithVolle(this, this, 1, Constants.BASE_URL
                + (commodityType.equals("6") ? Constants.METHOD_EQUIPMENT_DETAILS_DISTANCE : Constants.METHOD_EQUIPMENT_DETAILS_DISTANCE), POST, true);
        soapService.loadRequest(jsonObject);
    }

    @Override
    public void onSuccess(int responseCode, String strResponse) {
        Log.d("response", strResponse);
        if (responseCode == 1) {
            findViewById(R.id.tv_status).setVisibility(View.GONE);
            if (strResponse.contains("200")) {
                //200=Success
                loadRecycleView(strResponse);
            } else if (strResponse.contains("101")) {
                mRecyclerView.setVisibility(View.GONE);
                findViewById(R.id.tv_status).setVisibility(View.VISIBLE);
                Helper.AlertMesg(EquipAvaliabilityGridListMapActivity.this, getResources().getString(R.string.no_available));
            } else {
                mRecyclerView.setVisibility(View.GONE);
                findViewById(R.id.tv_status).setVisibility(View.VISIBLE);
                try {
                    JSONObject jsonObject = new JSONObject(strResponse);
                    Helper.AlertMesg(EquipAvaliabilityGridListMapActivity.this, jsonObject.getString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Helper.AlertMesg(EquipAvaliabilityGridListMapActivity.this, e.getMessage() + getResources().getString(R.string.failed));
                }
            }
        } else {
            try {
                JSONObject object = new JSONObject(strResponse);
                if (object.getString("status").contentEquals("200")) {
                    int count = dbHelper.getCount(DBTables.Category.TABLE_NAME);
                    if (count > 0) {
                        dbHelper.deleteAll(DBTables.Category.TABLE_NAME);
                    }
                    JSONArray categoryArray = object.getJSONArray("Data");
                    /*"Category_Id":"1","Category":"Tractor Drawn Equipment","Equipment_ID":"2","Equipment_Name":"MB Plough"}*/
                    for (int i = 0; i < categoryArray.length(); i++) {
                        JSONObject categoryObject = categoryArray.getJSONObject(i);
                        dbHelper.insertintoTable(DBTables.Category.TABLE_NAME, DBTables.Category.category_columns,
                                new String[]{categoryObject.getString("cid"), categoryObject.getString("cname"),
                                        categoryObject.getString("vid"), categoryObject.getString("vname")});
                    }
                    getFilter(false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onError(int responseCode, String error) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPS_FLAG) {
            if (resultCode == RESULT_OK) {
                gpsString = data.getStringExtra(GPSActivity.LOC_DATA);
                Constants.getAddressFromLocation(Double.parseDouble(gpsString.split("\\-")[0]), Double.parseDouble(gpsString.split("\\-")[1]), this, geocoderHandler);
                onclick_Hit();
            } else {
                Helper.showToast(getApplicationContext(),
                        getResources().getString(R.string.gps_failed));
            }
        } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                ((TextView) findViewById(R.id.tv_loc_search)).setText(place.getName());
                gpsString = place.getLatLng().latitude + "-" + place.getLatLng().longitude;
                onclick_Hit();
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Helper.showToast(getApplicationContext(),
                        status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {

            }
        }
    }


    public void onClick_Grid_List(View view) {
        if (gridListAdapter != null && gridListAdapter.getItemCount() > 0) {
            boolean isSwitched = gridListAdapter.toggleItemViewType();
            if (isSwitched) {
                iv_switch.setImageResource(R.drawable.ic_grid);
            } else {
                iv_switch.setImageResource(R.drawable.ic_list);
            }

            mRecyclerView.setLayoutManager(isSwitched ? new GridLayoutManager(this, 2) : new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            gridListAdapter.notifyDataSetChanged();
        }

    }


}
