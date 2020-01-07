package nk.bluefrog.rythusevaoffice.activities.bulkbooking;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
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
import nk.bluefrog.rythusevaoffice.utils.Constants;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;
import nk.bluefrog.rythusevaoffice.utils.DBTables;
import nk.mobileapps.spinnerlib.SearchableSpinner;
import nk.mobileapps.spinnerlib.SpinnerData;

public class BulkBookingActivity extends BluefrogActivity implements ResponseListener, SearchableSpinner.SpinnerListener {

    final int GPS_FLAG = 1245;
    final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1456;
    String gpsString;
    private SearchableSpinner commoditySpinner;
    private SearchableSpinner seedSpinner;
    private SearchableSpinner subSeedSpinner;
    private EditText etQuantity;
    private TextView tvQtHint, tv_nodata;
    private RecyclerView rvList;
    private DBHelper dbHelper;
    private String[] commoditiesArray = {"Seed", "Fertilizers", "Nursery", "Cooperate Equipment"};
    private ArrayList<SpinnerData> commoditiesData;
    private ArrayList<SpinnerData> seedData;
    private int REUEST_CODE_SEED = 102;
    private int REUEST_CODE_FERTILIZER = 103;
    private int REUEST_CODE_NURSERY = 104;
    private int REUEST_CODE_EQUIPMENT = 105;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulk_booking);
        dbHelper = new DBHelper(this);
        setToolBar("", "");
        initViews();
//        getSeedMaster();


        gpsString = PrefManger.getSharedPreferencesString(BulkBookingActivity.this, Constants.sp_gps, "");


    }

    public void setToolBar(String titleName, String subtitle) {
        Toolbar toolbar = (Toolbar) findViewById(nk.bluefrog.library.R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {

        Constants.navigationHomeScreen(dbHelper, BulkBookingActivity.this);

        /*Intent intent = new Intent(BulkBookingActivity.this, MPEOHomeActivity.class);
        startActivity(intent);
        finish();*/
    }

    private void getSeedMaster() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (Helper.isNetworkAvailable(this)) {

            RestServiceWithVolle soapService = new RestServiceWithVolle(this, this, 1, Constants.BASE_URL
                    + Constants.METHOD_CROP_MASTER, POST, true);

            soapService.loadRequest(jsonObject);
        } else {
            Helper.AlertMesg(this, getString(R.string.no_network));
        }
    }

    private void initViews() {
        tv_nodata = findViewById(R.id.tv_nodata);
        commoditySpinner = findViewById(R.id.spinnerCommodity);
        seedSpinner = findViewById(R.id.spinnerSeed);
        subSeedSpinner = findViewById(R.id.spinnerSubSeed);
        subSeedSpinner.setVisibility(View.GONE);
        etQuantity = findViewById(R.id.etQuantity);
        etQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (etQuantity.getText().toString().trim().startsWith("00")) {
                    etQuantity.setText("0");
                } else if (etQuantity.getText().toString().trim().startsWith(".")) {
                    etQuantity.setText("");
                } else if (etQuantity.getText().toString().trim().contains("..")) {
                    etQuantity.setText("" + etQuantity.getText().toString().trim().split("\\.")[0]);
                } else {
                    String value = etQuantity.getText().toString().trim();
                    value = value.length() == 0 ? "0" : value;
                    try {
                        Float.parseFloat(etQuantity.getText().toString().trim());
                    } catch (Exception e) {
                        if (value.contains(".") && value.endsWith("."))
                            etQuantity.setText(value.substring(0, value.length() - 1));
                    }

                    int beforeDotlen = value.substring(0, value.indexOf(".") == -1 ? 0 : value.indexOf(".")).length();
                    int afterDotlen = value.substring(value.indexOf(".") + 1).length();

                    if (afterDotlen > 2) {
                        if (value.contains("."))
                            etQuantity.setText(value.substring(0, value.indexOf(".")) + "." + value.substring(value.indexOf(".") + 1, value.indexOf(".") + 3));
                    }
                    double quantity = Double.parseDouble(value) / 100;
                    tvQtHint.setText("Hint " + quantity + "" + "in Quantals");
                }
//                tvQtHint.setText("Hint "+);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        tvQtHint = findViewById(R.id.tvQtHint);
        rvList = findViewById(R.id.rv_list);
        rvList.setLayoutManager(new LinearLayoutManager(this));
        commoditySpinner.setItems(new ArrayList<SpinnerData>());
        seedSpinner.setItems(new ArrayList<SpinnerData>());
        subSeedSpinner.setItems(new ArrayList<SpinnerData>());
        commoditiesData = new ArrayList<>();
        for (int i = 0; i < commoditiesArray.length; i++) {

            SpinnerData data = new SpinnerData();
            data.setId(String.valueOf(i + 1));
            data.setName(commoditiesArray[i]);
            commoditiesData.add(data);
        }
        commoditySpinner.setItems(commoditiesData);

        commoditySpinner.setListener(new SearchableSpinner.SpinnerListener() {
            @Override
            public void onItemsSelected(View view, List<SpinnerData> list, int position) {
                if (position != -1) {
                    if (commoditySpinner.getSelectedItem().get(0).getId().equals("1")) {
                        subSeedSpinner.setVisibility(View.VISIBLE);
                    } else if (commoditySpinner.getSelectedItem().get(0).getId().equals("2")) {
                        subSeedSpinner.setVisibility(View.GONE);
                    } else if (commoditySpinner.getSelectedItem().get(0).getId().equals("3")) {
                        subSeedSpinner.setVisibility(View.GONE);
                    } else if (commoditySpinner.getSelectedItem().get(0).getId().equals("4")) {
                        subSeedSpinner.setVisibility(View.VISIBLE);
                    }

                    setSeedSpinner();
                }
            }
        });


    }

    public void getDeals(View view) {

        if (validate()) {
            if (Helper.isNetworkAvailable(this)) {
                if (commoditySpinner.getSelectedItem().get(0).getId().equals("1")) {
                    serverHit();
                } else if (commoditySpinner.getSelectedItem().get(0).getId().equals("2")) {
                    serverHitForFertilizers();
                } else if (commoditySpinner.getSelectedItem().get(0).getId().equals("3")) {
                    serverHitForNursery();
                } else if (commoditySpinner.getSelectedItem().get(0).getId().equals("4")) {
                    serverHitForEquipment();
                }

                /*test case object*/
               /* String response = "{\"Status\" : \"Success\",\"SeedDetails\" : [{\"ShopID\":2417,\"District\":\"Visakhapatnam\",\"Mandal\":\"Munchingiputtu\",\"Panchayat\":\"Pedaguda\",\"ShopName\":\"Blue seed shop\",\"LicenceNo\":\"Blue8888\",\"OwnerName\":\"Androidbluemobile\",\"MobileNo\":\"8888888880\",\"Address\":\"Manchester\",\"SeedCategoryID\":\"02\",\"SeedCategory\":\"Red gram\",\"VarietyID\":\"0201\",\"Variety\":\"LRG41 ICPL87119\",\"Quantity\":8,\"price\":\"8000\",\"Min_Quantity\":null,\"Discount\":null,\"ImagePath\":\"http://65.19.149.158/RaithuMIthra/Images/SeedShopImages/8888888880/8888888880_190103114425927354.JPG\",\"GPS\":\"17.7220349$83.3161508\",\"Distance\":0.003},{\"ShopID\":2417,\"District\":\"Visakhapatnam\",\"Mandal\":\"Munchingiputtu\",\"Panchayat\":\"Pedaguda\",\"ShopName\":\"Blue seed shop\",\"LicenceNo\":\"Blue8888\",\"OwnerName\":\"Androidbluemobile\",\"MobileNo\":\"8888888880\",\"Address\":\"Manchester\",\"SeedCategoryID\":\"02\",\"SeedCategory\":\"Red gram\",\"VarietyID\":\"0202\",\"Variety\":\"Hyb ICPH2740\",\"Quantity\":4,\"price\":\"2500\",\"Min_Quantity\":null,\"Discount\":null,\"ImagePath\":\"http://65.19.149.158/RaithuMIthra/Images/SeedShopImages/8888888880/8888888880_190103114425927354.JPG\",\"GPS\":\"17.7220349$83.3161508\",\"Distance\":0.003},{\"ShopID\":6,\"District\":\"Visakhapatnam\",\"Mandal\":\"Anandapuram\",\"Panchayat\":\"Anandapuram\",\"ShopName\":\"Laxmi Enterprises\",\"LicenceNo\":\"VSP/25/JDA/SD/2016/8494\",\"OwnerName\":\"Uday Bhaskar\",\"MobileNo\":\"9440135594\",\"Address\":\"Anadhapuranm NH 5\",\"SeedCategoryID\":\"02\",\"SeedCategory\":\"Red gram\",\"VarietyID\":\"0201\",\"Variety\":\"LRG41 ICPL87119\",\"Quantity\":50,\"price\":\"120\",\"Min_Quantity\":null,\"Discount\":null,\"ImagePath\":\"http://65.19.149.158/TM_Application/Form///GCSE1//images//GCSE1_Registration_7_11_2017_16_42_26_16_Registration_img-3.jpeg\",\"GPS\":\"17.895410561952723$83.3792058256591\",\"Distance\":20.39},{\"ShopID\":172,\"District\":\"Krishna\",\"Mandal\":\"Bapulapadu\",\"Panchayat\":\"Bapulapadu\",\"ShopName\":\"ANNAPURNA SEEDS\",\"LicenceNo\":\"KRI/0/JDA/SD/2014/1816\",\"OwnerName\":\"CH ANJANEYALU\",\"MobileNo\":\"9494383893\",\"Address\":\"BAPULAPADU\",\"SeedCategoryID\":\"02\",\"SeedCategory\":\"Red gram\",\"VarietyID\":\"0202\",\"Variety\":\"Hyb ICPH2740\",\"Quantity\":70,\"price\":\"150\",\"Min_Quantity\":null,\"Discount\":null,\"ImagePath\":\"http://65.19.149.158/TM_Application/Form///GCSE1//images//GCSE1_Registration_5_10_2018_16_47_0_503_Registration_img-3.jpeg\",\"GPS\":\"16.64854499$80.96783938\",\"Distance\":276.5},{\"ShopID\":134,\"District\":\"Krishna\",\"Mandal\":\"Vissannapet\",\"Panchayat\":\"Vissannapet\",\"ShopName\":\"VENKATA KRISHNA SEEDS\",\"LicenceNo\":\"KRI/14/JDA/SD/2014/1964\",\"OwnerName\":\"Samineni Venkata Krishnarao\",\"MobileNo\":\"9885433948\",\"Address\":\"8/244/A,Vissannapeta ,Krishna district, pin code-521215\",\"SeedCategoryID\":\"02\",\"SeedCategory\":\"Red gram\",\"VarietyID\":\"0201\",\"Variety\":\"LRG41 ICPL87119\",\"Quantity\":1.2,\"price\":\"10000\",\"Min_Quantity\":null,\"Discount\":null,\"ImagePath\":\"http://65.19.149.158/TM_Application/Form///GCSE1//images//GCSE1_Registration_3_10_2018_11_17_6_511_Registration_img-3.jpeg\",\"GPS\":\"16.9413391$80.78237413\",\"Distance\":282.6},{\"ShopID\":761,\"District\":\"Krishna\",\"Mandal\":\"Gannavaram\",\"Panchayat\":\"Gannavaram\",\"ShopName\":\"APSSDC LTD GANNAVARAM\",\"LicenceNo\":\"2049/2013-14\",\"OwnerName\":\"APSSDC LTD GANNAVARAM\",\"MobileNo\":\"9849908745\",\"Address\":\"GANNAVARAM, D.NO.8-156\\nNEAR BUSSTAND\",\"SeedCategoryID\":\"02\",\"SeedCategory\":\"Red gram\",\"VarietyID\":\"0201\",\"Variety\":\"LRG41 ICPL87119\",\"Quantity\":67.68,\"price\":\"6450\",\"Min_Quantity\":null,\"Discount\":null,\"ImagePath\":\"http://65.19.149.158/TM_Application/Form///GCSE1//images//GCSE1_Registration_16_10_2018_14_10_24_170_Registration_img-3.jpeg\",\"GPS\":\"16.54384333333333$80.80912166666667\",\"Distance\":296.8},{\"ShopID\":1953,\"District\":\"Guntur\",\"Mandal\":\"Amaravathi\",\"Panchayat\":\"Amaravathi\",\"ShopName\":\"Sri sainadha fertiliser\",\"LicenceNo\":\"GUN/09/JDA/SD/2016/10527\",\"OwnerName\":\"Vinnakota Gopalarao\",\"MobileNo\":\"9949588435\",\"Address\":\"dono.10-104,mainroad,Amaravathi (po)(md)\",\"SeedCategoryID\":\"02\",\"SeedCategory\":\"Red gram\",\"VarietyID\":\"0201\",\"Variety\":\"LRG41 ICPL87119\",\"Quantity\":5,\"price\":\"500\",\"Min_Quantity\":null,\"Discount\":null,\"ImagePath\":\"http://65.19.149.158/TM_Application/Form///GCSE1//images//GCSE1_Registration_11_11_2018_10_22_24_158_Registration_img-3.jpeg\",\"GPS\":\"16.576391666666666$80.35898666666667\",\"Distance\":339},{\"ShopID\":560,\"District\":\"Prakasam\",\"Mandal\":\"Addanki\",\"Panchayat\":\"Addanki\",\"ShopName\":\"hanuman rythu depot\",\"LicenceNo\":\"pkm/16/jda/sd/2015/4431\",\"OwnerName\":\"m. siva kiran kumar\",\"MobileNo\":\"9440542768\",\"Address\":\"9-506. net road, Addanki\",\"SeedCategoryID\":\"02\",\"SeedCategory\":\"Red gram\",\"VarietyID\":\"0201\",\"Variety\":\"LRG41 ICPL87119\",\"Quantity\":1,\"price\":\"9000\",\"Min_Quantity\":null,\"Discount\":null,\"ImagePath\":\"http://65.19.149.158/TM_Application/Form///GCSE1//images//GCSE1_Registration_11_10_2018_17_35_24_800_Registration_img-3.jpeg\",\"GPS\":\"15.811436666666669$79.974835\",\"Distance\":414.3},{\"ShopID\":1639,\"District\":\"Guntur\",\"Mandal\":\"Durgi\",\"Panchayat\":\"Adigoppula\",\"ShopName\":\"Venkatarama traders\",\"LicenceNo\":\"GUN/19/JDA/SD/2016/11128\",\"OwnerName\":\"Gunda vistunu murthi\",\"MobileNo\":\"9866040185\",\"Address\":\"door no3-566 Adigi pula village Durgi mandal guntur dist\",\"SeedCategoryID\":\"02\",\"SeedCategory\":\"Red gram\",\"VarietyID\":\"0201\",\"Variety\":\"LRG41 ICPL87119\",\"Quantity\":1,\"price\":\"110\",\"Min_Quantity\":null,\"Discount\":null,\"ImagePath\":\"http://65.19.149.158/TM_Application/Form///GCSE1//images//GCSE1_Registration_3_11_2018_18_57_17_263_Registration_img-3.jpeg\",\"GPS\":\"16.4436925$79.6259048\",\"Distance\":417.1},{\"ShopID\":1640,\"District\":\"Guntur\",\"Mandal\":\"Durgi\",\"Panchayat\":\"Adigoppula\",\"ShopName\":\"Vigneswara Fertilisers\",\"LicenceNo\":\"RSL/1247\",\"OwnerName\":\"Kotturi ramasubbarao\",\"MobileNo\":\"9849710471\",\"Address\":\"door no 3-51 Adigoppula village Durgi mandalam guntur dist\",\"SeedCategoryID\":\"02\",\"SeedCategory\":\"Red gram\",\"VarietyID\":\"0201\",\"Variety\":\"LRG41 ICPL87119\",\"Quantity\":1,\"price\":\"110\",\"Min_Quantity\":null,\"Discount\":null,\"ImagePath\":\"http://65.19.149.158/TM_Application/Form///GCSE1//images//GCSE1_Registration_3_11_2018_19_21_59_998_Registration_img-3.jpeg\",\"GPS\":\"16.4436925$79.6259048\",\"Distance\":417.1}]}";
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("Status").equals("Success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("SeedDetails");
                        if (jsonArray.length() > 0) {
                            loadRecycler(jsonArray);
                            (findViewById(R.id.tv_nodata)).setVisibility(View.GONE);
                        } else {
                            rvList.setVisibility(View.GONE);
                            (findViewById(R.id.tv_nodata)).setVisibility(View.VISIBLE);

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }*/

            } else {
                Helper.AlertMesg(this, getString(R.string.no_network));
            }
        }


    }

    private boolean validate() {

        boolean flag = true;

        if (commoditySpinner.getSelectedItem().size() == 0) {
            Helper.showToast(this, getString(R.string.selelct_commodity));
            flag = false;
        } else if (seedSpinner.getSelectedItem().size() == 0) {
            Helper.showToast(this, getString(R.string.select_category_type));
            flag = false;
        } else if (commoditySpinner.getSelectedId().get(0).equals("1") && subSeedSpinner.getSelectedItem().size() == 0) {
            Helper.showToast(this, getString(R.string.select_subcategory_type));
            flag = false;
        }

        return flag;

    }


    public String getGpids() {
        List<List<String>> gpData = dbHelper.getGpIds();
        String gpids = "";
        for (int i = 0; i < gpData.size(); i++) {
            gpids += gpData.get(i).get(0) + ",";
        }
        return gpids.trim().equals("") ? "" : gpids.substring(0, gpids.length() - 1);
    }

    /*For service json object*/
    private JSONObject getSendingJsonObj() {
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("User_Id", dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.UNIQUE_ID));
            jsonObject.put("User_Type", "O");
            jsonObject.put("OfficerType", dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.OFFICER_TYPE));
            jsonObject.put("lat", gpsString.split("\\-")[0]);
            jsonObject.put("lng", gpsString.split("\\-")[1]);
            jsonObject.put("SeedCategory", seedSpinner.getSelectedId().get(0));
            jsonObject.put("Variety", subSeedSpinner.getSelectedId().get(0));
            jsonObject.put("MobileNo", PrefManger.getSharedPreferencesString(this, Constants.sp_mobile, ""));
            jsonObject.put("DeviceId", PrefManger.getSharedPreferencesString(this, Constants.KEY_REG_ID, ""));
            jsonObject.put("Quantity", etQuantity.getText().toString().trim());
            jsonObject.put("GPIDs", getGpids());
            jsonObject.put("Price", "");
            jsonObject.put("IMEI", Helper.getIMEINumber(this));
            jsonObject.put("Version", getString(R.string.version));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    private void serverHit() {
        if (gpsString.trim().contains("-")) {


           /* String keys[] = {"lat", "lng", "SeedCategory", "Variety",
                    Constants.KEY_Mobile_No, Constants.KEY_Device_Id, "Quantity", "GPIDs"};

            String values[] = {gpsString.split("\\-")[0], gpsString.split("\\-")[1],
                    seedSpinner.getSelectedId().get(0), subSeedSpinner.getSelectedId().get(0)
                    , PrefManger.getSharedPreferencesString(this, Constants.sp_mobile, "")
                    , PrefManger.getSharedPreferencesString(this, Constants.KEY_REG_ID, ""),
                    etQuantity.getText().toString(), getGpids()};
*/
          /*  SoapService soapService = new SoapService(this, this, REUEST_CODE_SEED, Constants.BASE_URL
                    , Constants.METHOD_GET_SHOPS, keys, values, true);

            soapService.execute();*/

            RestServiceWithVolle soapService = new RestServiceWithVolle(this, this, REUEST_CODE_SEED, Constants.BASE_URL
                    + Constants.METHOD_GET_SHOPS, POST, true);

            soapService.loadRequest(getSendingJsonObj());
        } else {
            getGps();
        }


    }


    /*For service json object*/
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

    private void serverHitForFertilizers() {
        String Category_Id = PrefManger.getSharedPreferencesString(this, Constants.sp_type, "0") + "";
        if (gpsString.trim().contains("-")) {
            String keys[] = {"lat", "lng", "FertilizerType", Constants.KEY_Mobile_No, Constants.KEY_Device_Id};
            String values[] = {gpsString.split("\\-")[0], gpsString.split("\\-")[1], Category_Id
                    , PrefManger.getSharedPreferencesString(this, Constants.sp_mobile, "")
                    , PrefManger.getSharedPreferencesString(this, Constants.KEY_REG_ID, "")};

            RestServiceWithVolle soapService = new RestServiceWithVolle(this, this, REUEST_CODE_FERTILIZER, Constants.BASE_URL
                    + Constants.METHOD_GET_FERTILIZERS, POST, true);

            soapService.loadRequest(getJsonObjFer());
        } else {
            getGps();
        }
    }

    /*For service json object*/
    private JSONObject getJsonObjNur() {
        String Category_Id = PrefManger.getSharedPreferencesString(this, Constants.sp_type, "") + "";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("lat", gpsString.split("\\-")[0]);
            jsonObject.put("lng", gpsString.split("\\-")[1]);
            jsonObject.put("PlantType", Category_Id);
            jsonObject.put(Constants.KEY_Mobile_No, PrefManger.getSharedPreferencesString(this, Constants.sp_mobile, ""));
            jsonObject.put(Constants.KEY_Device_Id, PrefManger.getSharedPreferencesString(this, Constants.KEY_REG_ID, ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    private JSONObject getSendingJsonObjNur() {
        String Category_Id = PrefManger.getSharedPreferencesString(this, Constants.sp_type, "0") + "";
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


    private void serverHitForNursery() {
        String Category_Id = PrefManger.getSharedPreferencesString(this, Constants.sp_type, "") + "";
        if (gpsString.trim().contains("-")) {
            String keys[] = {"lat", "lng", "PlantType", Constants.KEY_Mobile_No, Constants.KEY_Device_Id};
            String values[] = {gpsString.split("\\-")[0], gpsString.split("\\-")[1], Category_Id
                    , PrefManger.getSharedPreferencesString(this, Constants.sp_mobile, "")
                    , PrefManger.getSharedPreferencesString(this, Constants.KEY_REG_ID, "")};
            RestServiceWithVolle soapService = new RestServiceWithVolle(this, this, REUEST_CODE_NURSERY, Constants.BASE_URL
                    + Constants.METHOD_GET_NURSERY, POST, true);

            soapService.loadRequest(getSendingJsonObjNur());
        } else {
            getGps();
        }


    }

    private void serverHitForEquipment() {
        if (gpsString.trim().contains("-")) {
            String keys[] = {"postString"};
            String values[] = {getJsonObject().toString()};

            RestServiceWithVolle soapService = new RestServiceWithVolle(this, this, REUEST_CODE_EQUIPMENT, Constants.BASE_URL
                    + Constants.METHOD_EQUIPMENT_DETAILS_DISTANCE, POST, true);

            soapService.loadRequest(getJsonObject());
        } else {
            getGps();
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

    @Override
    public void onSuccess(int responseCode, String response) {

        Log.d("response", response);
        JSONObject jsonObject = null;
        JSONArray jsonArray = null;
        try {
            jsonObject = new JSONObject(response);

            if (responseCode == 1) {
                if (jsonObject.getString("status").contentEquals("200")) {
                    dbHelper.deleteAll(DBTables.CategoryAndVarities.TABLE_NAME);
                    jsonArray = jsonObject.getJSONArray("Data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject resultObj = jsonArray.getJSONObject(i);
                        insertIntoDB(resultObj);
                    }
                }
            }
            if (responseCode == REUEST_CODE_SEED) {
               /* response = "{\"Status\" : \"Success\",\"SeedDetails\" : [{\"ShopID\":2417,\"District\":\"Visakhapatnam\",\"Mandal\":\"Munchingiputtu\",\"Panchayat\":\"Pedaguda\",\"ShopName\":\"Blue seed shop\",\"LicenceNo\":\"Blue8888\",\"OwnerName\":\"Androidbluemobile\",\"MobileNo\":\"8888888880\",\"Address\":\"Manchester\",\"SeedCategoryID\":\"02\",\"SeedCategory\":\"Red gram\",\"VarietyID\":\"0201\",\"Variety\":\"LRG41 ICPL87119\",\"Quantity\":8,\"price\":\"8000\",\"Min_Quantity\":null,\"Discount\":null,\"ImagePath\":\"http://65.19.149.158/RaithuMIthra/Images/SeedShopImages/8888888880/8888888880_190103114425927354.JPG\",\"GPS\":\"17.7220349$83.3161508\",\"Distance\":0.003},{\"ShopID\":2417,\"District\":\"Visakhapatnam\",\"Mandal\":\"Munchingiputtu\",\"Panchayat\":\"Pedaguda\",\"ShopName\":\"Blue seed shop\",\"LicenceNo\":\"Blue8888\",\"OwnerName\":\"Androidbluemobile\",\"MobileNo\":\"8888888880\",\"Address\":\"Manchester\",\"SeedCategoryID\":\"02\",\"SeedCategory\":\"Red gram\",\"VarietyID\":\"0202\",\"Variety\":\"Hyb ICPH2740\",\"Quantity\":4,\"price\":\"2500\",\"Min_Quantity\":null,\"Discount\":null,\"ImagePath\":\"http://65.19.149.158/RaithuMIthra/Images/SeedShopImages/8888888880/8888888880_190103114425927354.JPG\",\"GPS\":\"17.7220349$83.3161508\",\"Distance\":0.003},{\"ShopID\":6,\"District\":\"Visakhapatnam\",\"Mandal\":\"Anandapuram\",\"Panchayat\":\"Anandapuram\",\"ShopName\":\"Laxmi Enterprises\",\"LicenceNo\":\"VSP/25/JDA/SD/2016/8494\",\"OwnerName\":\"Uday Bhaskar\",\"MobileNo\":\"9440135594\",\"Address\":\"Anadhapuranm NH 5\",\"SeedCategoryID\":\"02\",\"SeedCategory\":\"Red gram\",\"VarietyID\":\"0201\",\"Variety\":\"LRG41 ICPL87119\",\"Quantity\":50,\"price\":\"120\",\"Min_Quantity\":null,\"Discount\":null,\"ImagePath\":\"http://65.19.149.158/TM_Application/Form///GCSE1//images//GCSE1_Registration_7_11_2017_16_42_26_16_Registration_img-3.jpeg\",\"GPS\":\"17.895410561952723$83.3792058256591\",\"Distance\":20.39},{\"ShopID\":172,\"District\":\"Krishna\",\"Mandal\":\"Bapulapadu\",\"Panchayat\":\"Bapulapadu\",\"ShopName\":\"ANNAPURNA SEEDS\",\"LicenceNo\":\"KRI/0/JDA/SD/2014/1816\",\"OwnerName\":\"CH ANJANEYALU\",\"MobileNo\":\"9494383893\",\"Address\":\"BAPULAPADU\",\"SeedCategoryID\":\"02\",\"SeedCategory\":\"Red gram\",\"VarietyID\":\"0202\",\"Variety\":\"Hyb ICPH2740\",\"Quantity\":70,\"price\":\"150\",\"Min_Quantity\":null,\"Discount\":null,\"ImagePath\":\"http://65.19.149.158/TM_Application/Form///GCSE1//images//GCSE1_Registration_5_10_2018_16_47_0_503_Registration_img-3.jpeg\",\"GPS\":\"16.64854499$80.96783938\",\"Distance\":276.5},{\"ShopID\":134,\"District\":\"Krishna\",\"Mandal\":\"Vissannapet\",\"Panchayat\":\"Vissannapet\",\"ShopName\":\"VENKATA KRISHNA SEEDS\",\"LicenceNo\":\"KRI/14/JDA/SD/2014/1964\",\"OwnerName\":\"Samineni Venkata Krishnarao\",\"MobileNo\":\"9885433948\",\"Address\":\"8/244/A,Vissannapeta ,Krishna district, pin code-521215\",\"SeedCategoryID\":\"02\",\"SeedCategory\":\"Red gram\",\"VarietyID\":\"0201\",\"Variety\":\"LRG41 ICPL87119\",\"Quantity\":1.2,\"price\":\"10000\",\"Min_Quantity\":null,\"Discount\":null,\"ImagePath\":\"http://65.19.149.158/TM_Application/Form///GCSE1//images//GCSE1_Registration_3_10_2018_11_17_6_511_Registration_img-3.jpeg\",\"GPS\":\"16.9413391$80.78237413\",\"Distance\":282.6},{\"ShopID\":761,\"District\":\"Krishna\",\"Mandal\":\"Gannavaram\",\"Panchayat\":\"Gannavaram\",\"ShopName\":\"APSSDC LTD GANNAVARAM\",\"LicenceNo\":\"2049/2013-14\",\"OwnerName\":\"APSSDC LTD GANNAVARAM\",\"MobileNo\":\"9849908745\",\"Address\":\"GANNAVARAM, D.NO.8-156\\nNEAR BUSSTAND\",\"SeedCategoryID\":\"02\",\"SeedCategory\":\"Red gram\",\"VarietyID\":\"0201\",\"Variety\":\"LRG41 ICPL87119\",\"Quantity\":67.68,\"price\":\"6450\",\"Min_Quantity\":null,\"Discount\":null,\"ImagePath\":\"http://65.19.149.158/TM_Application/Form///GCSE1//images//GCSE1_Registration_16_10_2018_14_10_24_170_Registration_img-3.jpeg\",\"GPS\":\"16.54384333333333$80.80912166666667\",\"Distance\":296.8},{\"ShopID\":1953,\"District\":\"Guntur\",\"Mandal\":\"Amaravathi\",\"Panchayat\":\"Amaravathi\",\"ShopName\":\"Sri sainadha fertiliser\",\"LicenceNo\":\"GUN/09/JDA/SD/2016/10527\",\"OwnerName\":\"Vinnakota Gopalarao\",\"MobileNo\":\"9949588435\",\"Address\":\"dono.10-104,mainroad,Amaravathi (po)(md)\",\"SeedCategoryID\":\"02\",\"SeedCategory\":\"Red gram\",\"VarietyID\":\"0201\",\"Variety\":\"LRG41 ICPL87119\",\"Quantity\":5,\"price\":\"500\",\"Min_Quantity\":null,\"Discount\":null,\"ImagePath\":\"http://65.19.149.158/TM_Application/Form///GCSE1//images//GCSE1_Registration_11_11_2018_10_22_24_158_Registration_img-3.jpeg\",\"GPS\":\"16.576391666666666$80.35898666666667\",\"Distance\":339},{\"ShopID\":560,\"District\":\"Prakasam\",\"Mandal\":\"Addanki\",\"Panchayat\":\"Addanki\",\"ShopName\":\"hanuman rythu depot\",\"LicenceNo\":\"pkm/16/jda/sd/2015/4431\",\"OwnerName\":\"m. siva kiran kumar\",\"MobileNo\":\"9440542768\",\"Address\":\"9-506. net road, Addanki\",\"SeedCategoryID\":\"02\",\"SeedCategory\":\"Red gram\",\"VarietyID\":\"0201\",\"Variety\":\"LRG41 ICPL87119\",\"Quantity\":1,\"price\":\"9000\",\"Min_Quantity\":null,\"Discount\":null,\"ImagePath\":\"http://65.19.149.158/TM_Application/Form///GCSE1//images//GCSE1_Registration_11_10_2018_17_35_24_800_Registration_img-3.jpeg\",\"GPS\":\"15.811436666666669$79.974835\",\"Distance\":414.3},{\"ShopID\":1639,\"District\":\"Guntur\",\"Mandal\":\"Durgi\",\"Panchayat\":\"Adigoppula\",\"ShopName\":\"Venkatarama traders\",\"LicenceNo\":\"GUN/19/JDA/SD/2016/11128\",\"OwnerName\":\"Gunda vistunu murthi\",\"MobileNo\":\"9866040185\",\"Address\":\"door no3-566 Adigi pula village Durgi mandal guntur dist\",\"SeedCategoryID\":\"02\",\"SeedCategory\":\"Red gram\",\"VarietyID\":\"0201\",\"Variety\":\"LRG41 ICPL87119\",\"Quantity\":1,\"price\":\"110\",\"Min_Quantity\":null,\"Discount\":null,\"ImagePath\":\"http://65.19.149.158/TM_Application/Form///GCSE1//images//GCSE1_Registration_3_11_2018_18_57_17_263_Registration_img-3.jpeg\",\"GPS\":\"16.4436925$79.6259048\",\"Distance\":417.1},{\"ShopID\":1640,\"District\":\"Guntur\",\"Mandal\":\"Durgi\",\"Panchayat\":\"Adigoppula\",\"ShopName\":\"Vigneswara Fertilisers\",\"LicenceNo\":\"RSL/1247\",\"OwnerName\":\"Kotturi ramasubbarao\",\"MobileNo\":\"9849710471\",\"Address\":\"door no 3-51 Adigoppula village Durgi mandalam guntur dist\",\"SeedCategoryID\":\"02\",\"SeedCategory\":\"Red gram\",\"VarietyID\":\"0201\",\"Variety\":\"LRG41 ICPL87119\",\"Quantity\":1,\"price\":\"110\",\"Min_Quantity\":null,\"Discount\":null,\"ImagePath\":\"http://65.19.149.158/TM_Application/Form///GCSE1//images//GCSE1_Registration_3_11_2018_19_21_59_998_Registration_img-3.jpeg\",\"GPS\":\"16.4436925$79.6259048\",\"Distance\":417.1}]}";
                jsonObject = new JSONObject(response);*/

                if (jsonObject.getString("status").equals("200")) {
                    jsonArray = jsonObject.getJSONArray("SeedDetails");
                    if (jsonArray.length() > 0) {
                        loadRecycler(jsonArray);
                        (findViewById(R.id.tv_nodata)).setVisibility(View.GONE);
                    } else {
                        rvList.setVisibility(View.GONE);
                        (findViewById(R.id.tv_nodata)).setVisibility(View.VISIBLE);

                    }
                } else {
                    rvList.setVisibility(View.GONE);
                    tv_nodata.setVisibility(View.VISIBLE);

                }

            } else if (responseCode == REUEST_CODE_EQUIPMENT) {
                if (jsonObject.getString("status").equals("200")) {
                    jsonArray = jsonObject.getJSONArray("equipments");
                    if (jsonArray.length() > 0) {
                        loadRecycler(jsonArray);
                        (findViewById(R.id.tv_nodata)).setVisibility(View.GONE);
                    } else {
                        rvList.setVisibility(View.GONE);
                        (findViewById(R.id.tv_nodata)).setVisibility(View.VISIBLE);

                    }
                } else {
                    rvList.setVisibility(View.GONE);
                    tv_nodata.setVisibility(View.VISIBLE);
                }
            } else if (responseCode == REUEST_CODE_FERTILIZER) {
                if (jsonObject.getString("status").equals("200")) {
                    jsonArray = jsonObject.getJSONArray("FertilizerDetails");
                    if (jsonArray.length() > 0) {
                        loadRecycler(jsonArray);
                        (findViewById(R.id.tv_nodata)).setVisibility(View.GONE);
                    } else {
                        rvList.setVisibility(View.GONE);
                        (findViewById(R.id.tv_nodata)).setVisibility(View.VISIBLE);

                    }
                } else {
                    rvList.setVisibility(View.GONE);
                    tv_nodata.setVisibility(View.VISIBLE);

                }
            } else if (responseCode == REUEST_CODE_NURSERY) {
                if (jsonObject.getString("status").equals("200")) {
                    jsonArray = jsonObject.getJSONArray("NurseryDetails");
                    if (jsonArray.length() > 0) {
                        loadRecycler(jsonArray);
                        (findViewById(R.id.tv_nodata)).setVisibility(View.GONE);
                    } else {
                        rvList.setVisibility(View.GONE);
                        (findViewById(R.id.tv_nodata)).setVisibility(View.VISIBLE);

                    }
                } else {
                    rvList.setVisibility(View.GONE);
                    tv_nodata.setVisibility(View.VISIBLE);

                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onError(int responseCode, String error) {
 /* response = "{\"Status\" : \"Success\",\"SeedDetails\" : [{\"ShopID\":2417,\"District\":\"Visakhapatnam\",\"Mandal\":\"Munchingiputtu\",\"Panchayat\":\"Pedaguda\",\"ShopName\":\"Blue seed shop\",\"LicenceNo\":\"Blue8888\",\"OwnerName\":\"Androidbluemobile\",\"MobileNo\":\"8888888880\",\"Address\":\"Manchester\",\"SeedCategoryID\":\"02\",\"SeedCategory\":\"Red gram\",\"VarietyID\":\"0201\",\"Variety\":\"LRG41 ICPL87119\",\"Quantity\":8,\"price\":\"8000\",\"Min_Quantity\":null,\"Discount\":null,\"ImagePath\":\"http://65.19.149.158/RaithuMIthra/Images/SeedShopImages/8888888880/8888888880_190103114425927354.JPG\",\"GPS\":\"17.7220349$83.3161508\",\"Distance\":0.003},{\"ShopID\":2417,\"District\":\"Visakhapatnam\",\"Mandal\":\"Munchingiputtu\",\"Panchayat\":\"Pedaguda\",\"ShopName\":\"Blue seed shop\",\"LicenceNo\":\"Blue8888\",\"OwnerName\":\"Androidbluemobile\",\"MobileNo\":\"8888888880\",\"Address\":\"Manchester\",\"SeedCategoryID\":\"02\",\"SeedCategory\":\"Red gram\",\"VarietyID\":\"0202\",\"Variety\":\"Hyb ICPH2740\",\"Quantity\":4,\"price\":\"2500\",\"Min_Quantity\":null,\"Discount\":null,\"ImagePath\":\"http://65.19.149.158/RaithuMIthra/Images/SeedShopImages/8888888880/8888888880_190103114425927354.JPG\",\"GPS\":\"17.7220349$83.3161508\",\"Distance\":0.003},{\"ShopID\":6,\"District\":\"Visakhapatnam\",\"Mandal\":\"Anandapuram\",\"Panchayat\":\"Anandapuram\",\"ShopName\":\"Laxmi Enterprises\",\"LicenceNo\":\"VSP/25/JDA/SD/2016/8494\",\"OwnerName\":\"Uday Bhaskar\",\"MobileNo\":\"9440135594\",\"Address\":\"Anadhapuranm NH 5\",\"SeedCategoryID\":\"02\",\"SeedCategory\":\"Red gram\",\"VarietyID\":\"0201\",\"Variety\":\"LRG41 ICPL87119\",\"Quantity\":50,\"price\":\"120\",\"Min_Quantity\":null,\"Discount\":null,\"ImagePath\":\"http://65.19.149.158/TM_Application/Form///GCSE1//images//GCSE1_Registration_7_11_2017_16_42_26_16_Registration_img-3.jpeg\",\"GPS\":\"17.895410561952723$83.3792058256591\",\"Distance\":20.39},{\"ShopID\":172,\"District\":\"Krishna\",\"Mandal\":\"Bapulapadu\",\"Panchayat\":\"Bapulapadu\",\"ShopName\":\"ANNAPURNA SEEDS\",\"LicenceNo\":\"KRI/0/JDA/SD/2014/1816\",\"OwnerName\":\"CH ANJANEYALU\",\"MobileNo\":\"9494383893\",\"Address\":\"BAPULAPADU\",\"SeedCategoryID\":\"02\",\"SeedCategory\":\"Red gram\",\"VarietyID\":\"0202\",\"Variety\":\"Hyb ICPH2740\",\"Quantity\":70,\"price\":\"150\",\"Min_Quantity\":null,\"Discount\":null,\"ImagePath\":\"http://65.19.149.158/TM_Application/Form///GCSE1//images//GCSE1_Registration_5_10_2018_16_47_0_503_Registration_img-3.jpeg\",\"GPS\":\"16.64854499$80.96783938\",\"Distance\":276.5},{\"ShopID\":134,\"District\":\"Krishna\",\"Mandal\":\"Vissannapet\",\"Panchayat\":\"Vissannapet\",\"ShopName\":\"VENKATA KRISHNA SEEDS\",\"LicenceNo\":\"KRI/14/JDA/SD/2014/1964\",\"OwnerName\":\"Samineni Venkata Krishnarao\",\"MobileNo\":\"9885433948\",\"Address\":\"8/244/A,Vissannapeta ,Krishna district, pin code-521215\",\"SeedCategoryID\":\"02\",\"SeedCategory\":\"Red gram\",\"VarietyID\":\"0201\",\"Variety\":\"LRG41 ICPL87119\",\"Quantity\":1.2,\"price\":\"10000\",\"Min_Quantity\":null,\"Discount\":null,\"ImagePath\":\"http://65.19.149.158/TM_Application/Form///GCSE1//images//GCSE1_Registration_3_10_2018_11_17_6_511_Registration_img-3.jpeg\",\"GPS\":\"16.9413391$80.78237413\",\"Distance\":282.6},{\"ShopID\":761,\"District\":\"Krishna\",\"Mandal\":\"Gannavaram\",\"Panchayat\":\"Gannavaram\",\"ShopName\":\"APSSDC LTD GANNAVARAM\",\"LicenceNo\":\"2049/2013-14\",\"OwnerName\":\"APSSDC LTD GANNAVARAM\",\"MobileNo\":\"9849908745\",\"Address\":\"GANNAVARAM, D.NO.8-156\\nNEAR BUSSTAND\",\"SeedCategoryID\":\"02\",\"SeedCategory\":\"Red gram\",\"VarietyID\":\"0201\",\"Variety\":\"LRG41 ICPL87119\",\"Quantity\":67.68,\"price\":\"6450\",\"Min_Quantity\":null,\"Discount\":null,\"ImagePath\":\"http://65.19.149.158/TM_Application/Form///GCSE1//images//GCSE1_Registration_16_10_2018_14_10_24_170_Registration_img-3.jpeg\",\"GPS\":\"16.54384333333333$80.80912166666667\",\"Distance\":296.8},{\"ShopID\":1953,\"District\":\"Guntur\",\"Mandal\":\"Amaravathi\",\"Panchayat\":\"Amaravathi\",\"ShopName\":\"Sri sainadha fertiliser\",\"LicenceNo\":\"GUN/09/JDA/SD/2016/10527\",\"OwnerName\":\"Vinnakota Gopalarao\",\"MobileNo\":\"9949588435\",\"Address\":\"dono.10-104,mainroad,Amaravathi (po)(md)\",\"SeedCategoryID\":\"02\",\"SeedCategory\":\"Red gram\",\"VarietyID\":\"0201\",\"Variety\":\"LRG41 ICPL87119\",\"Quantity\":5,\"price\":\"500\",\"Min_Quantity\":null,\"Discount\":null,\"ImagePath\":\"http://65.19.149.158/TM_Application/Form///GCSE1//images//GCSE1_Registration_11_11_2018_10_22_24_158_Registration_img-3.jpeg\",\"GPS\":\"16.576391666666666$80.35898666666667\",\"Distance\":339},{\"ShopID\":560,\"District\":\"Prakasam\",\"Mandal\":\"Addanki\",\"Panchayat\":\"Addanki\",\"ShopName\":\"hanuman rythu depot\",\"LicenceNo\":\"pkm/16/jda/sd/2015/4431\",\"OwnerName\":\"m. siva kiran kumar\",\"MobileNo\":\"9440542768\",\"Address\":\"9-506. net road, Addanki\",\"SeedCategoryID\":\"02\",\"SeedCategory\":\"Red gram\",\"VarietyID\":\"0201\",\"Variety\":\"LRG41 ICPL87119\",\"Quantity\":1,\"price\":\"9000\",\"Min_Quantity\":null,\"Discount\":null,\"ImagePath\":\"http://65.19.149.158/TM_Application/Form///GCSE1//images//GCSE1_Registration_11_10_2018_17_35_24_800_Registration_img-3.jpeg\",\"GPS\":\"15.811436666666669$79.974835\",\"Distance\":414.3},{\"ShopID\":1639,\"District\":\"Guntur\",\"Mandal\":\"Durgi\",\"Panchayat\":\"Adigoppula\",\"ShopName\":\"Venkatarama traders\",\"LicenceNo\":\"GUN/19/JDA/SD/2016/11128\",\"OwnerName\":\"Gunda vistunu murthi\",\"MobileNo\":\"9866040185\",\"Address\":\"door no3-566 Adigi pula village Durgi mandal guntur dist\",\"SeedCategoryID\":\"02\",\"SeedCategory\":\"Red gram\",\"VarietyID\":\"0201\",\"Variety\":\"LRG41 ICPL87119\",\"Quantity\":1,\"price\":\"110\",\"Min_Quantity\":null,\"Discount\":null,\"ImagePath\":\"http://65.19.149.158/TM_Application/Form///GCSE1//images//GCSE1_Registration_3_11_2018_18_57_17_263_Registration_img-3.jpeg\",\"GPS\":\"16.4436925$79.6259048\",\"Distance\":417.1},{\"ShopID\":1640,\"District\":\"Guntur\",\"Mandal\":\"Durgi\",\"Panchayat\":\"Adigoppula\",\"ShopName\":\"Vigneswara Fertilisers\",\"LicenceNo\":\"RSL/1247\",\"OwnerName\":\"Kotturi ramasubbarao\",\"MobileNo\":\"9849710471\",\"Address\":\"door no 3-51 Adigoppula village Durgi mandalam guntur dist\",\"SeedCategoryID\":\"02\",\"SeedCategory\":\"Red gram\",\"VarietyID\":\"0201\",\"Variety\":\"LRG41 ICPL87119\",\"Quantity\":1,\"price\":\"110\",\"Min_Quantity\":null,\"Discount\":null,\"ImagePath\":\"http://65.19.149.158/TM_Application/Form///GCSE1//images//GCSE1_Registration_3_11_2018_19_21_59_998_Registration_img-3.jpeg\",\"GPS\":\"16.4436925$79.6259048\",\"Distance\":417.1}]}";
                jsonObject = new JSONObject(response);*/
        Log.e("Error", "onError: " + error);
    }

    private void loadRecycler(JSONArray jsonArray) {
        String category_sub_name = "";
        rvList.setVisibility(View.GONE);
        BulkBookinRequestPojo bulkBookinRequestPojo = new BulkBookinRequestPojo();
        bulkBookinRequestPojo.setCommodity(commoditySpinner.getSelectedId().get(0));
        bulkBookinRequestPojo.setCategory_id(seedSpinner.getSelectedId().get(0));
        if (commoditySpinner.getSelectedId().get(0).equals("1") || commoditySpinner.getSelectedId().get(0).equals("4")) {
            bulkBookinRequestPojo.setSubcategory_id(subSeedSpinner.getSelectedId().get(0));
        } else {
            bulkBookinRequestPojo.setSubcategory_id("");
        }
        if (commoditySpinner.getSelectedId().get(0).equals("1")) {
            category_sub_name = seedSpinner.getSelectedItem().get(0).getName() + "(" + subSeedSpinner.getSelectedItem().get(0).getName() + ")";
        } else if (commoditySpinner.getSelectedId().get(0).equals("2")) {
            category_sub_name = seedSpinner.getSelectedItem().get(0).getName();
        } else if (commoditySpinner.getSelectedId().get(0).equals("3")) {
            category_sub_name = seedSpinner.getSelectedItem().get(0).getName();
        } else if (commoditySpinner.getSelectedId().get(0).equals("4")) {
            category_sub_name = seedSpinner.getSelectedItem().get(0).getName() + "(" + subSeedSpinner.getSelectedItem().get(0).getName() + ")";
        }
        bulkBookinRequestPojo.setCategory_name(category_sub_name);
        bulkBookinRequestPojo.setQuantity(etQuantity.getText().toString().trim());
        bulkBookinRequestPojo.setLat(gpsString.split("\\-")[0].trim());
        bulkBookinRequestPojo.setLng(gpsString.split("\\-")[1].trim());
        if (jsonArray.length() > 0) {

            if (commoditySpinner.getSelectedItem().get(0).getId().equals("4") || commoditySpinner.getSelectedItem().get(0).getId().equals("5")) {
                rvList.setVisibility(View.VISIBLE);
                BulkBookingAdapter adapter = new BulkBookingAdapter(this, false, jsonArray, bulkBookinRequestPojo);
                rvList.setAdapter(adapter);
            } else {
                rvList.setVisibility(View.VISIBLE);
                BulkBookingAdapter adapter = new BulkBookingAdapter(this, true, jsonArray, bulkBookinRequestPojo);
                rvList.setAdapter(adapter);
            }


        } else {
            rvList.setVisibility(View.GONE);
        }

    }


    private void insertIntoDB(JSONObject resultObj) {

        try {
            dbHelper.insertintoTable(DBTables.CategoryAndVarities.TABLE_NAME, DBTables.CategoryAndVarities.cols,
                    new String[]{resultObj.getString("cid"), resultObj.getString("cname"),
                            resultObj.getString("vid"),
                            resultObj.getString("vname")});
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void setSeedSpinner() {

        if (commoditySpinner.getSelectedItem().get(0).getId().equals("1")) {
            String query = "SELECT DISTINCT catID,catName FROM CategoryAndVarities where categoryType = 1 ";
            List<List<String>> seedIds = dbHelper.getDataByQuery(query);
            seedData = new ArrayList<>();
            SpinnerData sData = new SpinnerData();
            sData.setId("0");
            sData.setName("All");
            seedData.add(sData);

            for (int j = 0; j < seedIds.size(); j++) {
                sData = new SpinnerData();
                sData.setId(seedIds.get(j).get(0));
                sData.setName(seedIds.get(j).get(1));
                seedData.add(sData);
            }
        } else if (commoditySpinner.getSelectedItem().get(0).getId().equals("2")) {
           /* String ferlizers = Helper.readTextFile(BulkBookingActivity.this, R.raw.fertilizers);
            String[] fertlizersarray = ferlizers.trim().split("\\|");*/
            String query = "SELECT DISTINCT catID,catName FROM CategoryAndVarities where categoryType = 2 ";
            List<List<String>> fertlizersarray = dbHelper.getDataByQuery(query);
            seedData = new ArrayList<>();
            SpinnerData sData = new SpinnerData();
            sData.setId("0");
            sData.setName("All");
            seedData.add(sData);

            for (int i = 0; i < fertlizersarray.size(); i++) {
                sData = new SpinnerData();
                sData.setId(fertlizersarray.get(i).get(0));
                sData.setName(fertlizersarray.get(i).get(1));
                seedData.add(sData);
            }


        } else if (commoditySpinner.getSelectedItem().get(0).getId().equals("3")) {
          /*  String nursery = Helper.readTextFile(BulkBookingActivity.this, R.raw.nurseries);
            String[] nurseryArray = nursery.trim().split("\\,");*/
            String query = "SELECT DISTINCT catID,catName FROM CategoryAndVarities where categoryType = 3 ";
            List<List<String>> nurseryArray = dbHelper.getDataByQuery(query);
            seedData = new ArrayList<>();
            SpinnerData sData = new SpinnerData();
            sData.setId("0");
            sData.setName("All");
            seedData.add(sData);

            for (int i = 0; i < nurseryArray.size(); i++) {
                sData = new SpinnerData();
                sData.setId(nurseryArray.get(i).get(0));
                sData.setName(nurseryArray.get(i).get(1));
                seedData.add(sData);
            }

        } else if (commoditySpinner.getSelectedItem().get(0).getId().equals("4")) {
          /*  String nursery = Helper.readTextFile(BulkBookingActivity.this, R.raw.nurseries);
            String[] nurseryArray = nursery.trim().split("\\,");*/
            String query = "SELECT DISTINCT catID,catName FROM CategoryAndVarities where categoryType = 6 ";
            List<List<String>> equipment = dbHelper.getDataByQuery(query);
            seedData = new ArrayList<>();
            SpinnerData sData = new SpinnerData();
            sData.setId("0");
            sData.setName("All");
            seedData.add(sData);

            for (int i = 0; i < equipment.size(); i++) {
                sData = new SpinnerData();
                sData.setId(equipment.get(i).get(0));
                sData.setName(equipment.get(i).get(1));
                seedData.add(sData);
            }

        }


        seedSpinner.setItems(seedData, this);
    }

    private void getGps() {
        startActivityForResult(new Intent(getApplicationContext(),
                GPSActivity.class), GPS_FLAG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == GPS_FLAG) {
                gpsString = data.getStringExtra(GPSActivity.LOC_DATA);
            }
            if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                ((TextView) findViewById(R.id.tv_loc_search)).setText(place.getName());
                gpsString = place.getLatLng().latitude + "-" + place.getLatLng().longitude;

                if (validate()) {
                    if (Helper.isNetworkAvailable(this)) {
                        serverHit();
                    } else {
                        Helper.AlertMesg(this, getString(R.string.no_network));
                    }
                }

            }
        }
    }

    @Override
    public void onItemsSelected(View view, List<SpinnerData> list, int i) {

        if (view == seedSpinner) {

            if (i != -1) {
                ArrayList<SpinnerData> subSeedData = new ArrayList<>();
                if (commoditySpinner.getSelectedItem().get(0).getId().equals("1")) {


                    List<List<String>> subSeeds = dbHelper.getTableColDataByCond(DBTables.CategoryAndVarities.TABLE_NAME
                            , DBTables.CategoryAndVarities.subCatID + "," + DBTables.CategoryAndVarities.subCatName
                            , new String[]{DBTables.CategoryAndVarities.catID, DBTables.CategoryAndVarities.categoryType}
                            , new String[]{list.get(i).getId(), commoditySpinner.getSelectedId().get(0)});

                    SpinnerData sData = new SpinnerData();
                    sData.setId("0");
                    sData.setName("All");
                    subSeedData.add(sData);

                    for (int j = 0; j < subSeeds.size(); j++) {


                        sData = new SpinnerData();
                        sData.setId(subSeeds.get(j).get(0));
                        sData.setName(subSeeds.get(j).get(1));

                        subSeedData.add(sData);


                    }
                } else if (commoditySpinner.getSelectedItem().get(0).getId().equals("4")) {
                    List<List<String>> subSeeds = dbHelper.getTableColDataByCond(DBTables.CategoryAndVarities.TABLE_NAME
                            , DBTables.CategoryAndVarities.subCatID + "," + DBTables.CategoryAndVarities.subCatName
                            , new String[]{DBTables.CategoryAndVarities.catID, DBTables.CategoryAndVarities.categoryType}
                            , new String[]{list.get(i).getId(), "6"});

                    SpinnerData sData = new SpinnerData();
                    sData.setId("0");
                    sData.setName("All");
                    subSeedData.add(sData);

                    for (int j = 0; j < subSeeds.size(); j++) {


                        sData = new SpinnerData();
                        sData.setId(subSeeds.get(j).get(0));
                        sData.setName(subSeeds.get(j).get(1));

                        subSeedData.add(sData);


                    }
                }


                subSeedSpinner.setItems(subSeedData, this);


            }
        }
        if (view == commoditySpinner) {
            if (i > 0) {


            }
        }

    }

    public void onClick_LocationSearch(View view) {

        placeSearch();

    }

    private void placeSearch() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);

            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
            Helper.showToast(this, e.getMessage().trim());
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
            Helper.showToast(this, e.getMessage().trim());
        }
    }
}
