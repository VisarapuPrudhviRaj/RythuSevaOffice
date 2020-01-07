package nk.bluefrog.rythusevaoffice.activities.nursery;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

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
import nk.bluefrog.rythusevaoffice.activities.bulkbooking.BulkBookinRequestPojo;
import nk.bluefrog.rythusevaoffice.utils.Constants;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;
import nk.bluefrog.rythusevaoffice.utils.DBTables;
import nk.mobileapps.spinnerlib.SearchableSpinner;
import nk.mobileapps.spinnerlib.SpinnerData;


public class NurseryAvaliabilityGridMapListActivity extends BluefrogActivity implements ResponseListener {

    final public int GPS_FLAG = 1245;
    final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1456;
    LinearLayout layoutBottomSheet;
    BottomSheetBehavior sheetBehavior;
    DBHelper dbHelper;
    Context context;

    Dialog dlg;
    String gpsString = "";
    String strResponse;

    boolean searchPlace = false;

    List<Marker> l_marker = new ArrayList<>();
    LinearLayout ll_equipmentlist;
    SearchableSpinner sp_type;
    //Spinner sp_subtype;
    //List<List<String>> ll_category = new ArrayList<>();
    List<List<String>> ll_dist = new ArrayList<>();

    List<List<String>> ll_subcategory = new ArrayList<>();
    String nurseries;
    JSONObject jsonObject;
    ImageView iv_switch;
    RecyclerView mRecyclerView;
    JSONArray jsonArray = new JSONArray();
    NursuryGridListAdapter nursurygridListAdapter;
    private GoogleMap mMap;
    private Handler handlEquipment = new Handler() {
        public void handleMessage(Message msg) {
            closeProgressDialog();

            try {
                jsonObject = new JSONObject(strResponse);
                if (jsonObject.getString("Status").equalsIgnoreCase("Success")) {

                    findViewById(R.id.tv_status).setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    loadRecycleView(strResponse);


                } else if (strResponse.contains("101")) {
                    findViewById(R.id.tv_status).setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);

                    Helper.AlertMesg(NurseryAvaliabilityGridMapListActivity.this, getString(R.string.nursery_not_availabale));
                } else {
                    findViewById(R.id.tv_status).setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                    try {
                        JSONObject jsonObject = new JSONObject(strResponse);
                        Helper.AlertMesg(NurseryAvaliabilityGridMapListActivity.this, jsonObject.getString("message"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Helper.AlertMesg(NurseryAvaliabilityGridMapListActivity.this, e.getMessage() + getResources().getString(R.string.failed));
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
                findViewById(R.id.tv_status).setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
                Helper.AlertMesg(NurseryAvaliabilityGridMapListActivity.this, getString(R.string.nursery_not_availabale));
            }
        }
    };

    private Handler geocoderHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    ((TextView) findViewById(R.id.tv_loc_search)).setText(locationAddress.trim().equals("null") ? "" : locationAddress);
                    ((TextView) findViewById(R.id.tv_nursury_search)).setText("All Nursury");
                    break;
                default:
                    ((TextView) findViewById(R.id.tv_loc_search)).setText("");
                    locationAddress = null;
            }
            Log.e("location Address=", locationAddress);
        }
    };
    private String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nursery_avaliability_grid_map_list);
        dbHelper = new DBHelper(this);
        context = this;
        setToolBar("", "");
        //setToolBar(getString(R.string.Nursery_availability), "");

        // setToolBarWithID(getString(R.string.Nursery_availability), "", R.id.toolbar_);

        findView();
        PrefManger.putSharedPreferencesString(this, Constants.sp_type, "0");

    }

    public void onClick_back(View v) {
        finish();
    }

    public void onClick_LocationSearch(View v) {
        placeSearch();
    }

    public void onClick_NursurySearch(View v) {
        getFilter(false);
    }

    private void placeSearch() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(NurseryAvaliabilityGridMapListActivity.this);

            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
            Helper.showToast(NurseryAvaliabilityGridMapListActivity.this, e.getMessage().trim());
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
            Helper.showToast(NurseryAvaliabilityGridMapListActivity.this, e.getMessage().trim());
        }
    }

    public void getFilter(boolean flag) {
        dlg = new Dialog(this);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.dialog_nurseryfilter);
        dlg.setCancelable(true);
        dlg.show();

        ArrayList<SpinnerData> nurseryData = new ArrayList<>();
        SpinnerData data1 = new SpinnerData();
        data1.setId("0");
        data1.setName("All Nursury");
        nurseryData.add(data1);

        sp_type = dlg.findViewById(R.id.sp_type);

        nurseries = Helper.readTextFile(getApplicationContext(), R.raw.nurseries);
        String[] fert = nurseries.split(",");
        for (String aFert : fert) {
            String splitD[] = aFert.split("-");
            SpinnerData data = new SpinnerData();
            data.setId(splitD[0]);
            data.setName(splitD[1]);
            nurseryData.add(data);

        }

        sp_type.setItems(nurseryData, new SearchableSpinner.SpinnerListener() {
            @Override
            public void onItemsSelected(View view, List<SpinnerData> list, int i) {

                if (i != -1) {
                    PrefManger.putSharedPreferencesString(context, Constants.sp_type, list.get(i).getId());
                    msg = list.get(i).getName();

                }

            }
        });

        sp_type.setItemID("0");


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
                searchPlace = false;

                ((TextView) findViewById(R.id.tv_nursury_search)).setText(msg);

                onclick_Hit();
            }
        });
        if (flag) {
            btn_cancel.setVisibility(View.GONE);
        }


    }


    private void findView() {

        iv_switch = (ImageView) findViewById(R.id.iv_switch);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        onclick_Hit();
    }

    private void loadRecycleView(String strResponse) {

        try {
            JSONObject jsonObject = new JSONObject(strResponse);
            jsonArray = jsonObject.getJSONArray("NurseryDetails");
            if (jsonArray.length() > 0) {
                findViewById(R.id.tv_status).setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setLayoutManager(linearLayoutManager);
                mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
                BulkBookinRequestPojo bulkBookinRequestPojo = new BulkBookinRequestPojo();
                bulkBookinRequestPojo.setCommodity("3");
                bulkBookinRequestPojo.setCategory_id(PrefManger.getSharedPreferencesString(this, Constants.sp_type, "") + "");
                bulkBookinRequestPojo.setSubcategory_id(PrefManger.getSharedPreferencesString(this, Constants.sp_type, "") + "");
                bulkBookinRequestPojo.setCategory_name(PrefManger.getSharedPreferencesString(this, Constants.sp_type, "").trim().equals("0") ? "All" : sp_type.getSelectedItem().get(0).getName());
                bulkBookinRequestPojo.setQuantity("0");
                bulkBookinRequestPojo.setLat(gpsString.split("\\-")[0].trim());
                bulkBookinRequestPojo.setLng(gpsString.split("\\-")[1].trim());
                nursurygridListAdapter = new NursuryGridListAdapter(this, jsonArray, bulkBookinRequestPojo);
                mRecyclerView.setAdapter(nursurygridListAdapter);
            } else {
                findViewById(R.id.tv_status).setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            }

        } catch (Exception e) {

            findViewById(R.id.tv_status).setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }


    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                getFilter(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

    @Override
    public void onBackPressed() {
        finish();
    }

    private JSONObject getSendingJsonObjNur() {
        String Category_Id = PrefManger.getSharedPreferencesString(this, Constants.sp_type, "") + "";
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("User_Id", dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.UNIQUE_ID));
            jsonObject.put("User_Type", "O");
            jsonObject.put("OfficerType", dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.OFFICER_TYPE));
            jsonObject.put("IMEI", Helper.getIMEINumber(this));
            jsonObject.put("Version", getString(R.string.version));

            jsonObject.put("MobileNo", dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.MOBILE));
            jsonObject.put("DeviceId", PrefManger.getSharedPreferencesString(this, Constants.KEY_REG_ID, ""));
            jsonObject.put("lat", gpsString.split("\\-")[0]);
            jsonObject.put("lng", gpsString.split("\\-")[1]);
            jsonObject.put("Price", "");
            jsonObject.put("GPIDs", "");
            jsonObject.put("PlantType", Category_Id);
            jsonObject.put("Age", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public void onclick_Hit() {
        String Category_Id = PrefManger.getSharedPreferencesString(context, Constants.sp_type, "0") + "";
        if (gpsString.contains("-")) {
            String keys[] = {"lat", "lng", "PlantType", Constants.KEY_Mobile_No, Constants.KEY_Device_Id};
            String values[] = {gpsString.split("\\-")[0], gpsString.split("\\-")[1], Category_Id
                    , PrefManger.getSharedPreferencesString(this, Constants.sp_mobile, "")
                    , PrefManger.getSharedPreferencesString(this, Constants.KEY_REG_ID, "")};
            if (Helper.isNetworkAvailable(this)) {
                serverHitForEquipments(getSendingJsonObjNur(), getResources().getString(R.string.find_Nursery));
            } else {
                Helper.AlertMesg(this, getString(R.string.no_network));
            }

        } else {
            startActivityForResult(new Intent(getApplicationContext(),
                    GPSActivity.class), GPS_FLAG);
        }
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

    private void serverHitForEquipments(JSONObject jsonObject, final String msg) {
        showProgressDialog(msg);

        RestServiceWithVolle soapService = new RestServiceWithVolle(this, this, 1, Constants.BASE_URL
                + Constants.METHOD_GET_NURSERY, POST, false);

        soapService.loadRequest(jsonObject);
    }

    public void onClick_Grid_List(View view) {
        if (nursurygridListAdapter != null && nursurygridListAdapter.getItemCount() > 0) {
            boolean isSwitched = nursurygridListAdapter.toggleItemViewType();
            if (isSwitched) {
                iv_switch.setImageResource(R.drawable.ic_grid);
            } else {
                iv_switch.setImageResource(R.drawable.ic_list);
            }

            mRecyclerView.setLayoutManager(isSwitched ? new GridLayoutManager(this, 2) : new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            nursurygridListAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onSuccess(int responseCode, String strResponse) {
        closeProgressDialog();

        try {
            jsonObject = new JSONObject(strResponse);
            if (jsonObject.getString("status").equalsIgnoreCase("200")) {

                findViewById(R.id.tv_status).setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                loadRecycleView(strResponse);


            } else if (strResponse.contains("101")) {
                findViewById(R.id.tv_status).setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);

                Helper.AlertMesg(NurseryAvaliabilityGridMapListActivity.this, getString(R.string.nursery_not_availabale));
            } else {
                findViewById(R.id.tv_status).setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = new JSONObject(strResponse);
                    Helper.AlertMesg(NurseryAvaliabilityGridMapListActivity.this, jsonObject.getString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Helper.AlertMesg(NurseryAvaliabilityGridMapListActivity.this, e.getMessage() + getResources().getString(R.string.failed));
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
            findViewById(R.id.tv_status).setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            Helper.AlertMesg(NurseryAvaliabilityGridMapListActivity.this, getString(R.string.nursery_not_availabale));
        }
    }

    @Override
    public void onError(int responseCode, String error) {
        Helper.AlertMesg(this, error);
    }
}
