package nk.bluefrog.rythusevaoffice.activities.fertilizers;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

public class FertilizersAvaliabilityGridMapListActivity extends BluefrogActivity implements ResponseListener {
    final public int GPS_FLAG = 1245;
    final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1456;
    DBHelper dbHelper;
    Context context;

    Dialog dlg;
    String gpsString = "";
    String strResponse;

    List<Marker> l_marker = new ArrayList<>();
    LinearLayout ll_equipmentlist;
    SearchableSpinner sp_type;
    //Spinner sp_subtype;
    //List<List<String>> ll_category = new ArrayList<>();
    List<List<String>> ll_dist = new ArrayList<>();

    List<List<String>> ll_subcategory = new ArrayList<>();
    String fertilizers;
    JSONObject jsonObject;
    ImageView iv_switch;
    RecyclerView mRecyclerView;
    JSONArray jsonArray = new JSONArray();
    FertilizerGridListAdapter fertilizergridListAdapter;
    private GoogleMap mMap;
    private Handler handlEquipment = new Handler() {
        public void handleMessage(android.os.Message msg) {
            closeProgressDialog();
            try {
                jsonObject = new JSONObject(strResponse);
                if (jsonObject.getString("Status").equalsIgnoreCase("Success")) {

                    JSONArray jsonArray = jsonObject.getJSONArray("FertilizerDetails");
                    if (jsonArray.length() > 0) {
                        //loadMapAndList(strResponse);
                        findViewById(R.id.tv_status).setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        loadRecycleView(strResponse);

                    } else {
                        findViewById(R.id.tv_status).setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.GONE);
                        Helper.AlertMesg(FertilizersAvaliabilityGridMapListActivity.this, getString(R.string.fertilizer_not_available) + "\n" + getResources().getString(R.string.try_again));
                    }
                } else {
                    findViewById(R.id.tv_status).setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                    Helper.AlertMesg(FertilizersAvaliabilityGridMapListActivity.this, getResources().getString(R.string.try_again));
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Helper.AlertMesg(FertilizersAvaliabilityGridMapListActivity.this, e.getMessage() + "\n" + getResources().getString(R.string.try_again));
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
                    ((TextView) findViewById(R.id.tv_fertilizer_search)).setText("All Fertilizers");
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
        setContentView(R.layout.activity_fertilizers_avaliability_grid_map_list);

        dbHelper = new DBHelper(this);
        context = this;
        setToolBar("", "");
        findView();
        PrefManger.putSharedPreferencesString(this, Constants.sp_type, "0");
    }

    private void findView() {
        iv_switch = (ImageView) findViewById(R.id.iv_switch);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        onclick_Hit();
    }

    public void onClick_back(View v) {
        finish();
    }

    public void onClick_LocationSearch(View v) {
        placeSearch();
    }

    public void onClick_fertilizerSearch(View v) {
        getFilter(false);
    }

    private void placeSearch() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(FertilizersAvaliabilityGridMapListActivity.this);

            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
            Helper.showToast(FertilizersAvaliabilityGridMapListActivity.this, e.getMessage().trim());
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
            Helper.showToast(FertilizersAvaliabilityGridMapListActivity.this, e.getMessage().trim());
        }
    }

    private void loadRecycleView(String strResponse) {
        try {
            JSONObject jsonObject = new JSONObject(strResponse);
            jsonArray = jsonObject.getJSONArray("FertilizerDetails");
            if (jsonArray.length() > 0) {
                findViewById(R.id.tv_status).setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setLayoutManager(linearLayoutManager);
                mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
                BulkBookinRequestPojo bulkBookinRequestPojo = new BulkBookinRequestPojo();
                bulkBookinRequestPojo.setCommodity("2");
                bulkBookinRequestPojo.setCategory_id(PrefManger.getSharedPreferencesString(this,Constants.sp_type,""));
                bulkBookinRequestPojo.setSubcategory_id(PrefManger.getSharedPreferencesString(this,Constants.sp_type,""));
                bulkBookinRequestPojo.setCategory_name(PrefManger.getSharedPreferencesString(this,Constants.sp_type,"").trim().equals("0")?"All":sp_type.getSelectedItem().get(0).getName());
                bulkBookinRequestPojo.setQuantity("0");
                bulkBookinRequestPojo.setLat(gpsString.split("\\-")[0].trim());
                bulkBookinRequestPojo.setLng(gpsString.split("\\-")[1].trim());
                fertilizergridListAdapter = new FertilizerGridListAdapter(this, jsonArray, bulkBookinRequestPojo);
                mRecyclerView.setAdapter(fertilizergridListAdapter);
            } else {
                findViewById(R.id.tv_status).setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            }

        } catch (Exception e) {

            findViewById(R.id.tv_status).setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }


    }

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
    }

    @Override
    public void onBackPressed() {
        gpsString = "";
        finish();
    }

    public void getFilter(boolean flag) {
        dlg = new Dialog(this);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.dialog_fertilizefilter);
        dlg.setCancelable(true);
        dlg.show();

        ArrayList<SpinnerData> fertData = new ArrayList<>();
        SpinnerData data1 = new SpinnerData();
        data1.setId("0");
        data1.setName("All Fertilizers");
        fertData.add(data1);


        sp_type = dlg.findViewById(R.id.sp_type);
        //ll_dist.clear();
        fertilizers = Helper.readTextFile(getApplicationContext(), R.raw.fertilizers);
        String[] fert = fertilizers.split("\\|");
        for (String aFert : fert) {
            String splitD[] = aFert.split(",");
            SpinnerData data = new SpinnerData();
            data.setId(splitD[0]);
            data.setName(splitD[1]);
            fertData.add(data);
           /* List<String> l_add = new ArrayList<>();
            l_add.add(splitD[0]);
            l_add.add(splitD[1]);
            ll_dist.add(l_add);*/
        }

        sp_type.setItems(fertData, new SearchableSpinner.SpinnerListener() {
            @Override
            public void onItemsSelected(View view, List<SpinnerData> list, int i) {

                if (i != -1) {
                    PrefManger.putSharedPreferencesString(context, Constants.sp_type, list.get(i).getId());
                    msg = list.get(i).getName();

                }

            }
        });

        sp_type.setItemID("0");
        /*Helper.setSpinnerLabelValueData(FertilizersAvaliabilityGridMapListActivity.this, sp_type, ll_dist, getString(R.string.all), 0, 1, getString(R.string.ferti_cat));
        sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                if (position == 0) {
                    PrefManger.putSharedPreferencesString(context, Constants.sp_type, "0");
                } else {
                    sp_type.setTag(ll_dist.get(position - 1).get(0));
                    PrefManger.putSharedPreferencesString(context, Constants.sp_type, (String) sp_type.getTag());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/


        Button btn_cancel = dlg.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dlg.dismiss();
            }
        });
        Button btn_apply = dlg.findViewById(R.id.btn_apply);
        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (sp_type.getSelectedItemPosition() != -1) {
                    dlg.dismiss();

                    ((TextView) findViewById(R.id.tv_fertilizer_search)).setText(msg);
                    onclick_Hit();
                } else {
                    Helper.showToast(FertilizersAvaliabilityGridMapListActivity.this, getString(R.string.select_fertilizer_type));
                }

            }
        });
        if (flag) {
            btn_cancel.setVisibility(View.GONE);
        }


    }

    private JSONObject getJsonObjFer() {
        String Category_Id = PrefManger.getSharedPreferencesString(this, Constants.sp_type, "0") + "";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("lat", gpsString.split("\\-")[0]);
            jsonObject.put("lng", gpsString.split("\\-")[1]);
            jsonObject.put("FertilizerType", Category_Id);
            jsonObject.put("MobileNo", PrefManger.getSharedPreferencesString(this, Constants.sp_mobile, ""));
            jsonObject.put("DeviceId", PrefManger.getSharedPreferencesString(this, Constants.KEY_REG_ID, ""));

            jsonObject.put("User_Id", dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.UNIQUE_ID));
            jsonObject.put("User_Type", "O");
            jsonObject.put("OfficerType", dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.OFFICER_TYPE));

            jsonObject.put("IMEI", Helper.getIMEINumber(this));
            jsonObject.put("Version", getString(R.string.version));

            jsonObject.put("Quantity", "");
            jsonObject.put("Price", "");
            jsonObject.put("GPIDs", "");


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }


    public void onclick_Hit() {
        String Category_Id = PrefManger.getSharedPreferencesString(context, Constants.sp_type, "0") + "";
        if (gpsString.contains("-")) {
            String keys[] = {"lat", "lng", "FertilizerType", Constants.KEY_Mobile_No, Constants.KEY_Device_Id};
            String values[] = {gpsString.split("\\-")[0], gpsString.split("\\-")[1], Category_Id
                    , PrefManger.getSharedPreferencesString(this, Constants.sp_mobile, "")
                    , PrefManger.getSharedPreferencesString(this, Constants.KEY_REG_ID, "")};
            if (Helper.isNetworkAvailable(this)) {
                serverHitForEquipments(getJsonObjFer(), getResources().getString(R.string.find_ferti));
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
                + Constants.METHOD_GET_FERTILIZERS, POST, false);

        soapService.loadRequest(jsonObject);
    }

    public void onClick_Grid_List(View view) {
        if (fertilizergridListAdapter.getItemCount() > 0) {
            boolean isSwitched = fertilizergridListAdapter.toggleItemViewType();
            if (isSwitched) {
                iv_switch.setImageResource(R.drawable.ic_grid);
            } else {
                iv_switch.setImageResource(R.drawable.ic_list);
            }

            mRecyclerView.setLayoutManager(isSwitched ? new GridLayoutManager(this, 2) : new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            fertilizergridListAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onSuccess(int responseCode, String strResponse) {

        closeProgressDialog();
        try {
            jsonObject = new JSONObject(strResponse);
            if (jsonObject.getString("status").equalsIgnoreCase("200")) {

                JSONArray jsonArray = jsonObject.getJSONArray("FertilizerDetails");
                if (jsonArray.length() > 0) {
                    //loadMapAndList(strResponse);
                    findViewById(R.id.tv_status).setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    loadRecycleView(strResponse);

                } else {
                    findViewById(R.id.tv_status).setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                    Helper.AlertMesg(FertilizersAvaliabilityGridMapListActivity.this, getString(R.string.fertilizer_not_available) + "\n" + getResources().getString(R.string.try_again));
                }
            } else {
                findViewById(R.id.tv_status).setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
                Helper.AlertMesg(FertilizersAvaliabilityGridMapListActivity.this, getResources().getString(R.string.try_again));
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Helper.AlertMesg(FertilizersAvaliabilityGridMapListActivity.this, e.getMessage() + "\n" + getResources().getString(R.string.try_again));
        }

    }

    @Override
    public void onError(int responseCode, String error) {
        Helper.AlertMesg(FertilizersAvaliabilityGridMapListActivity.this, error);

    }
}
