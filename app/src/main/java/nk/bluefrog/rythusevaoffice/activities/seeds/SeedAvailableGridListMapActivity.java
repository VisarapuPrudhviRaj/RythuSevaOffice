package nk.bluefrog.rythusevaoffice.activities.seeds;

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
import nk.bluefrog.rythusevaoffice.activities.bulkbooking.BulkBookinRequestPojo;
import nk.bluefrog.rythusevaoffice.utils.Constants;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;
import nk.bluefrog.rythusevaoffice.utils.DBTables;
import nk.mobileapps.spinnerlib.SearchableSpinner;
import nk.mobileapps.spinnerlib.SpinnerData;


public class SeedAvailableGridListMapActivity extends BluefrogActivity implements ResponseListener {

    final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1456;
    final int GPS_FLAG = 1245;
    DBHelper dbHelper;
    Context context;
    String gpsString = "", strResponse;
    RecyclerView mRecyclerView;
    SeedGridListAdapter seedGridListAdapter;
    JSONArray jsonArray = new JSONArray();
    ImageView iv_switch;

    Dialog dlg;
    List<List<String>> ll_seedcat = new ArrayList<>();
    List<List<String>> ll_seedsubcat = new ArrayList<>();
    Spinner sp_me_shop;
    SearchableSpinner sp_type, sp_subtype;

    Handler geocoderHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    ((TextView) findViewById(R.id.tv_loc_search)).setText(locationAddress.trim().equals("null") ? "" : locationAddress);
                    ((TextView) findViewById(R.id.tv_equip_search)).setText("All Seeds");
                    break;
                default:
                    ((TextView) findViewById(R.id.tv_loc_search)).setText("");
                    locationAddress = null;
            }
            Log.e("location Address=", locationAddress);
        }
    };

    private Handler handlFromFarmer = new Handler() {
        public void handleMessage(android.os.Message msg) {
            closeProgressDialog();
            try {
                JSONObject jsonObject = new JSONObject(strResponse);
                if (jsonObject.getString("Status").equalsIgnoreCase("Success")) {
                    //200=Success
                    jsonArray = jsonObject.getJSONArray("FarmersSeedDetails");
                    loadRecycleView(false);
                } else if (strResponse.contains("101")) {
                    mRecyclerView.setVisibility(View.GONE);
                    findViewById(R.id.tv_status).setVisibility(View.VISIBLE);
                    Helper.AlertMesg(SeedAvailableGridListMapActivity.this, getResources().getString(R.string.no_available));
                } else {
                    mRecyclerView.setVisibility(View.GONE);
                    findViewById(R.id.tv_status).setVisibility(View.VISIBLE);
                    Helper.AlertMesg(SeedAvailableGridListMapActivity.this, getResources().getString(R.string.no_available));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                mRecyclerView.setVisibility(View.GONE);
                findViewById(R.id.tv_status).setVisibility(View.VISIBLE);
                Helper.AlertMesg(SeedAvailableGridListMapActivity.this, e.getMessage() + getResources().getString(R.string.no_available));
            }
        }
    };
    private Handler handlFromShop = new Handler() {
        public void handleMessage(android.os.Message msg) {
            closeProgressDialog();
            try {
                JSONObject jsonObject = new JSONObject(strResponse);
                if (jsonObject.getString("Status").equalsIgnoreCase("Success")) {
                    //200=Success
                    jsonArray = jsonObject.getJSONArray("SeedDetails");
                    loadRecycleView(true);
                } else if (strResponse.contains("101")) {
                    mRecyclerView.setVisibility(View.GONE);
                    findViewById(R.id.tv_status).setVisibility(View.VISIBLE);
                    Helper.AlertMesg(SeedAvailableGridListMapActivity.this, getResources().getString(R.string.no_available));
                } else {
                    mRecyclerView.setVisibility(View.GONE);
                    findViewById(R.id.tv_status).setVisibility(View.VISIBLE);
                    Helper.AlertMesg(SeedAvailableGridListMapActivity.this, getResources().getString(R.string.no_available));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                mRecyclerView.setVisibility(View.GONE);
                findViewById(R.id.tv_status).setVisibility(View.VISIBLE);
                Helper.AlertMesg(SeedAvailableGridListMapActivity.this, e.getMessage() + getResources().getString(R.string.no_available));
            }
        }
    };
    private ArrayList<SpinnerData> seedTypeData, subSeedTypeData;
    private String msg1, msg2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seed_available_grid_list_map);
        context = this;
        dbHelper = new DBHelper(this);
        setToolBar("", "");

        findViews();
        gpsString = PrefManger.getSharedPreferencesString(SeedAvailableGridListMapActivity.this, Constants.sp_gps, "");

    }

    private void setCollapsingToolbarLayout() {
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        CollapsingToolbarLayout mCollapisngToolbar = (CollapsingToolbarLayout) appBarLayout.findViewById(R.id.toolbar_layout);
        mCollapisngToolbar.setTitle(getString(R.string.seed_availability));

        //Set the color of collapsed toolbar text
        mCollapisngToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.common_white));

        //This will set Expanded text to transparent so it wount overlap the content of the toolbar
        mCollapisngToolbar.setExpandedTitleColor(ContextCompat.getColor(this, R.color.colorPrimary));
        ((TextView) findViewById(R.id.title)).setText(getString(R.string.seed_availability));
    }

    private void findViews() {
        iv_switch = (ImageView) findViewById(R.id.iv_switch);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        getFilter(true);
    }

    private void loadRecycleView(boolean shops) {
        //{"Status" : "Success","SeedDetails" : [{"District":"Visakhapatnam","Mandal":"Anakapalli","Panchayat":"Thummapala",
        // "ShopName":"abc","LicenceNo":"123","OwnerName":"praderp","MobileNo":"9966912595","Address":"abc","SeedCategory":"Korra",
        // "Variety":"SIA3085","Quantity":"15","price":"400",
        // "ImagePath":"http://65.19.149.158/TM_Application/Form///GCSE1//images//GCSE1_Registration_28_10_2017_14_23_19_354_Registration_img-3.jpeg",
        // "GPS":"17.7212441$83.3168441","Distance":0.117}]}

        if (jsonArray.length() > 0) {
            findViewById(R.id.tv_status).setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(linearLayoutManager);
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            BulkBookinRequestPojo bulkBookinRequestPojo = new BulkBookinRequestPojo();
            bulkBookinRequestPojo.setCommodity("1");
            bulkBookinRequestPojo.setCategory_id(sp_type.getSelectedId().get(0));
            bulkBookinRequestPojo.setSubcategory_id(sp_type.getSelectedId().get(0));
            bulkBookinRequestPojo.setCategory_name(sp_type.getSelectedItem().get(0).getName() + "(" + sp_subtype.getSelectedItem().get(0).getName() + ")");
            bulkBookinRequestPojo.setQuantity("0");
            bulkBookinRequestPojo.setLat(gpsString.split("\\-")[0].trim());
            bulkBookinRequestPojo.setLng(gpsString.split("\\-")[1].trim());
            seedGridListAdapter = new SeedGridListAdapter(this, shops, jsonArray, bulkBookinRequestPojo);
            mRecyclerView.setAdapter(seedGridListAdapter);
        } else {
            ((TextView) findViewById(R.id.tv_status)).setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }



        /*mRecyclerView.setOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
               // ((TextView) findViewById(R.id.title)).setTextColor(getResources().getColor(R.color.white));
            }

            @Override
            public void onShow() {
              //  ((TextView) findViewById(R.id.title)).setTextColor(getResources().getColor(R.color.colorPrimary));

            }
        });*/


    }

    public void onClick_LocationSearch(View view) {
        placeSearch();
    }

    public void onClick_EquipSearch(View view) {
        getFilter(false);
    }

    private void placeSearch() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(SeedAvailableGridListMapActivity.this);

            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
            Helper.showToast(SeedAvailableGridListMapActivity.this, e.getMessage().trim());
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
            Helper.showToast(SeedAvailableGridListMapActivity.this, e.getMessage().trim());
        }
    }

    public void getFilter(boolean flag) {
        dlg = new Dialog(this);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.dialog_seedsfilter);
        dlg.setCancelable(true);
        dlg.show();

        seedTypeData = new ArrayList<>();
        subSeedTypeData = new ArrayList<>();


        sp_me_shop = dlg.findViewById(R.id.sp_me_shop);
        Helper.setSpinnerLabelValueData(SeedAvailableGridListMapActivity.this, sp_me_shop, new String[]{getString(R.string.shop), getString(R.string.farmer)}, "From");
        sp_type = dlg.findViewById(R.id.sp_type);

        SpinnerData seedData = new SpinnerData();
        seedData.setId("0");
        seedData.setName("All");
        seedTypeData.add(seedData);

        List<List<String>> seedIds = dbHelper.getDataByQuery("SELECT DISTINCT catID,catName FROM CategoryAndVarities");
        
       /* String seedcat = Helper.readTextFile(getApplicationContext(), R.raw.seed_category);
        String[] fert = seedcat.split("\\|");
        for (String aFert : fert) {
            String splitD[] = aFert.split(",");

            SpinnerData seedData1 = new SpinnerData();
            seedData1.setId(splitD[0]);
            seedData1.setName(splitD[1]);
            seedTypeData.add(seedData1);

        }
*/
        for (int i = 0; i < seedIds.size(); i++) {
            SpinnerData seedData1 = new SpinnerData();
            seedData1.setId(seedIds.get(i).get(0));
            seedData1.setName(seedIds.get(i).get(1));
            seedTypeData.add(seedData1);
        }

        sp_subtype = dlg.findViewById(R.id.sp_subtype);

        sp_type.setItems(seedTypeData, new SearchableSpinner.SpinnerListener() {
            @Override
            public void onItemsSelected(View view, List<SpinnerData> list, int pos) {
                if (pos != -1) {
                    subSeedTypeData.clear();
                    msg1 = list.get(pos).getName();
                    PrefManger.putSharedPreferencesString(context, Constants.sp_type, list.get(pos).getId());
                    PrefManger.putSharedPreferencesString(context, Constants.sp_subType, "0");


                    SpinnerData subCatData = new SpinnerData();
                    subCatData.setId("0");
                    subCatData.setName("All");
                    subSeedTypeData.add(subCatData);

                    loadSubType(sp_type.getSelectedItem().get(0).getId());

                  /*  String seedsubcat = Helper.readTextFile(getApplicationContext(), R.raw.seed_subcategory);
                    String[] fert = seedsubcat.split("\\|");

                    for (String aFert : fert) {
                        if (aFert.trim().startsWith(list.get(pos).getId())) {
                            String splitD[] = aFert.split(",");
                            SpinnerData seedData1 = new SpinnerData();
                            seedData1.setId(splitD[0]);
                            seedData1.setName(splitD[1]);
                            subSeedTypeData.add(seedData1);

                        }
                    }*/

                    sp_subtype.setItems(subSeedTypeData, new SearchableSpinner.SpinnerListener() {
                        @Override
                        public void onItemsSelected(View view, List<SpinnerData> list, int pos) {

                            if (pos != -1) {
                                PrefManger.putSharedPreferencesString(context, Constants.sp_subType, list.get(pos).getId());
                                msg2 = list.get(pos).getName();
                            }

                        }
                    });

                    sp_subtype.setItemID("0");
                }
            }
        });


        sp_type.setItemID("0");
        //sp_type.setTag("0");


       /* Helper.setSpinnerLabelValueData(SeedAvailableGridListMapActivity.this, sp_type, ll_seedcat, "All", 0, 1, getString(R.string.selectSeed));

        sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                if (position == 0) {
                    sp_subtype.setVisibility(View.VISIBLE);
                    sp_subtype.setTag("0");
                    PrefManger.putSharedPreferencesString(context, Constants.sp_type, "0");
                    PrefManger.putSharedPreferencesString(context, Constants.sp_subType, "0");
                    ll_seedsubcat.clear();
                    Helper.setSpinnerLabelValueData(SeedAvailableGridListMapActivity.this, sp_subtype, ll_seedsubcat, "All", 0, 1, getString(R.string.selectSeedtype));
                } else {
                    ll_seedsubcat.clear();
                    sp_type.setTag(ll_seedcat.get(sp_type.getSelectedItemPosition() - 1).get(0));
                    System.out.println("=======ID:" + (String) sp_type.getTag());
                    sp_subtype.setVisibility(View.VISIBLE);
                    PrefManger.putSharedPreferencesString(context, Constants.sp_type, (String) sp_type.getTag());
                    PrefManger.putSharedPreferencesString(context, Constants.sp_subType, "0");
                    String seedsubcat = Helper.readTextFile(getApplicationContext(), R.raw.seed_subcategory);
                    String[] fert = seedsubcat.split("\\|");

                    for (int i = 0; i < fert.length; i++) {
                        if (fert[i].trim().startsWith((String) sp_type.getTag())) {
                            String splitD[] = fert[i].split("\\,");
                            List<String> l_add = new ArrayList<>();
                            //write condition here
                            l_add.add(splitD[0]);
                            l_add.add(splitD[1]);
                            ll_seedsubcat.add(l_add);
                        }
                    }
                    Helper.setSpinnerLabelValueData(SeedAvailableGridListMapActivity.this, sp_subtype, ll_seedsubcat, "All", 0, 1, getString(R.string.selectSeedtype));
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
                    sp_subtype.setTag(ll_seedsubcat.get(sp_subtype.getSelectedItemPosition() - 1).get(0));

                    PrefManger.putSharedPreferencesString(context, Constants.sp_subType, (String) sp_subtype.getTag());
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
*/

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
                PrefManger.putSharedPreferencesBoolean(context, Constants.sp_shop_farmer, sp_me_shop.getSelectedItemPosition() == 0 ? true : false);
                String msg = "" + msg1 + msg2;
                ((TextView) findViewById(R.id.tv_equip_search)).setText(msg);
                onclick_Hit();
            }
        });
        if (flag) {
            btn_cancel.setVisibility(View.GONE);
        }


    }

    private void loadSubType(String cat_id) {
        List<List<String>> subSeeds = dbHelper.getTableColDataByCond(DBTables.CategoryAndVarities.TABLE_NAME
                , DBTables.CategoryAndVarities.subCatID + "," + DBTables.CategoryAndVarities.subCatName
                , new String[]{DBTables.CategoryAndVarities.catID, DBTables.CategoryAndVarities.categoryType}, new String[]{cat_id, "1"});

        if (subSeeds.size() > 0) {
            for (int i = 0; i < subSeeds.size(); i++) {
                SpinnerData seedData1 = new SpinnerData();
                seedData1.setId(subSeeds.get(i).get(0));
                seedData1.setName(subSeeds.get(i).get(1));
                subSeedTypeData.add(seedData1);

            }

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

    public String getGpids() {
        List<List<String>> gpData = dbHelper.getGpIds();
        String gpids = "";
        for (int i = 0; i < gpData.size(); i++) {
            gpids += gpData.get(i).get(0) + ",";
        }
        return gpids.trim().equals("") ? "" : gpids.substring(0, gpids.length() - 1);
    }

    public void onclick_Hit() {

        String Equipment_ID = PrefManger.getSharedPreferencesString(context, Constants.sp_type, "0") + "";
        String Category_Id = PrefManger.getSharedPreferencesString(context, Constants.sp_subType, "0") + "";

        String msg = "" + msg1 + "," + msg2;
        ((TextView) findViewById(R.id.tv_equip_search)).setText(msg);
        if (sp_me_shop.getSelectedItemPosition() == 0) {
            PrefManger.putSharedPreferencesBoolean(context, Constants.sp_shop_farmer, true);
            //from shop
            //setToolBar(getString(R.string.seedsAvailable), "From:" + getString(R.string.shop));

            if (gpsString.contains("-")) {
                String keys[] = {"lat", "lng", "SeedCategory", "Variety", Constants.KEY_Mobile_No, Constants.KEY_Device_Id, "Quantity", "GPIDs"};

                String values[] = {gpsString.split("\\-")[0], gpsString.split("\\-")[1],
                        Equipment_ID, Category_Id
                        , PrefManger.getSharedPreferencesString(this, Constants.sp_mobile, "")
                        , PrefManger.getSharedPreferencesString(this, Constants.KEY_REG_ID, ""), "", getGpids()};

                if (Helper.isNetworkAvailable(this)) {
//                    serverHitFormShop(keys, values, getString(R.string.wait));
                    serverHitFormShop(getSendingJsonObj("shop"), getString(R.string.wait));
                } else {
                    Helper.AlertMesg(this, getString(R.string.no_network));
                }

            } else {
                startActivityForResult(new Intent(getApplicationContext(),
                        GPSActivity.class), GPS_FLAG);
            }

        } else {
            PrefManger.putSharedPreferencesBoolean(context, Constants.sp_shop_farmer, false);
            //from farmer
            // setToolBar(getString(R.string.seedsAvailable), "From:" + getString(R.string.farmer));

            if (gpsString.contains("-")) {
                String keys[] = {"lat", "lng", "SeedCategory", "Variety", Constants.KEY_Mobile_No, Constants.KEY_Device_Id};
                String values[] = {gpsString.split("\\-")[0], gpsString.split("\\-")[1], Equipment_ID, Category_Id
                        , PrefManger.getSharedPreferencesString(this, Constants.sp_mobile, "")
                        , PrefManger.getSharedPreferencesString(this, Constants.KEY_REG_ID, "")};

                if (Helper.isNetworkAvailable(this)) {
//                    serverHitFormFarmer(keys, values, getString(R.string.wait));
                    serverHitFormFarmer(getSendingJsonObj("farmer"), getString(R.string.wait));
                } else {
                    Helper.AlertMesg(this, getString(R.string.no_network));
                }

            } else {
                startActivityForResult(new Intent(getApplicationContext(),
                        GPSActivity.class), GPS_FLAG);
            }
        }


    }



    private JSONObject getSendingJsonObj(String type) {
        String Equipment_ID = PrefManger.getSharedPreferencesString(context, Constants.sp_type, "0") + "";
        String Category_Id = PrefManger.getSharedPreferencesString(context, Constants.sp_subType, "0") + "";
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("User_Id", dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.UNIQUE_ID));
            jsonObject.put("User_Type", "O");
            jsonObject.put("OfficerType", dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.OFFICER_TYPE));
            jsonObject.put("lat", gpsString.split("\\-")[0]);
            jsonObject.put("lng", gpsString.split("\\-")[1]);
            jsonObject.put("SeedCategory", Equipment_ID);
            jsonObject.put("Variety", Category_Id);
            jsonObject.put("MobileNo", PrefManger.getSharedPreferencesString(this, Constants.sp_mobile, ""));
            jsonObject.put("DeviceId", PrefManger.getSharedPreferencesString(this, Constants.KEY_REG_ID, ""));
            jsonObject.put("Quantity","");
            jsonObject.put("GPIDs", type.trim().equals("shop")?getGpids():"");
            jsonObject.put("Price", "");
            jsonObject.put("IMEI", Helper.getIMEINumber(this));
            jsonObject.put("Version", getString(R.string.version));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }


    private void serverHitFormFarmer(JSONObject jsonObject, final String msg) {
        showProgressDialog(msg);

        RestServiceWithVolle soapService = new RestServiceWithVolle(this, this, 1, Constants.BASE_URL
                + Constants.METHOD_GET_SEEDS, POST, false);

        soapService.loadRequest(jsonObject);



      /*  new Thread() {
            public void run() {
                WebserviceCall call = new WebserviceCall();
                strResponse = call.callCService(Constants.BASE_URL,
                        Constants.METHOD_GET_SEEDS, keys,
                        values);

                handlFromFarmer.sendEmptyMessage(0);
            }

        }.start();*/
    }


    private void serverHitFormShop(JSONObject jsonObject, final String msg) {
        showProgressDialog(msg);
        RestServiceWithVolle soapService = new RestServiceWithVolle(this, this, 2, Constants.BASE_URL+
                 Constants.METHOD_GET_SHOPS,POST, false);

        soapService.loadRequest(jsonObject);
     /*   new Thread() {
            public void run() {
                WebserviceCall call = new WebserviceCall();
                strResponse = call.callCService(Constants.BASE_URL,
                        Constants.METHOD_GET_SHOPS, keys,
                        values);

                handlFromShop.sendEmptyMessage(0);
            }

        }.start();*/
    }

    public void onClick_Grid_List(View view) {
        if (seedGridListAdapter != null && seedGridListAdapter.getItemCount() > 0) {
            boolean isSwitched = seedGridListAdapter.toggleItemViewType();
            if (isSwitched) {
                iv_switch.setImageResource(R.drawable.ic_grid);
            } else {
                iv_switch.setImageResource(R.drawable.ic_list);
            }

            mRecyclerView.setLayoutManager(isSwitched ? new GridLayoutManager(this, 2) : new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            seedGridListAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onSuccess(int responseCode, String strResponse) {

        if (responseCode == 1) {
            closeProgressDialog();
            try {
                JSONObject jsonObject = new JSONObject(strResponse);
                if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                    //200=Success
                    jsonArray = jsonObject.getJSONArray("FarmersSeedDetails");
                    loadRecycleView(false);
                } else if (strResponse.contains("101")) {
                    mRecyclerView.setVisibility(View.GONE);
                    findViewById(R.id.tv_status).setVisibility(View.VISIBLE);
                    Helper.AlertMesg(SeedAvailableGridListMapActivity.this, getResources().getString(R.string.no_available));
                } else {
                    mRecyclerView.setVisibility(View.GONE);
                    findViewById(R.id.tv_status).setVisibility(View.VISIBLE);
                    Helper.AlertMesg(SeedAvailableGridListMapActivity.this, getResources().getString(R.string.no_available));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                mRecyclerView.setVisibility(View.GONE);
                findViewById(R.id.tv_status).setVisibility(View.VISIBLE);
                Helper.AlertMesg(SeedAvailableGridListMapActivity.this, e.getMessage() + getResources().getString(R.string.no_available));
            }
        }
        if (responseCode == 2) {
            closeProgressDialog();
            try {
                JSONObject jsonObject = new JSONObject(strResponse);
                if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                    //200=Success
                    jsonArray = jsonObject.getJSONArray("SeedDetails");
                    loadRecycleView(true);
                } else if (strResponse.contains("101")) {
                    mRecyclerView.setVisibility(View.GONE);
                    findViewById(R.id.tv_status).setVisibility(View.VISIBLE);
                    Helper.AlertMesg(SeedAvailableGridListMapActivity.this, getResources().getString(R.string.no_available));
                } else {
                    mRecyclerView.setVisibility(View.GONE);
                    findViewById(R.id.tv_status).setVisibility(View.VISIBLE);
                    Helper.AlertMesg(SeedAvailableGridListMapActivity.this, getResources().getString(R.string.no_available));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                mRecyclerView.setVisibility(View.GONE);
                findViewById(R.id.tv_status).setVisibility(View.VISIBLE);
                Helper.AlertMesg(SeedAvailableGridListMapActivity.this, e.getMessage() + getResources().getString(R.string.no_available));
            }
        }
    }

    @Override
    public void onError(int responseCode, String error) {

        Helper.AlertMesg(this, error);


    }


}
