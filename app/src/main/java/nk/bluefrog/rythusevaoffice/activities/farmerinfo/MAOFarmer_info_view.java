package nk.bluefrog.rythusevaoffice.activities.farmerinfo;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import nk.bluefrog.library.BluefrogActivity;
import nk.bluefrog.library.utils.PrefManger;
import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.utils.Constants;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;
import nk.mobileapps.spinnerlib.SearchableSpinner;
import nk.mobileapps.spinnerlib.SpinnerData;

public class MAOFarmer_info_view extends BluefrogActivity implements SearchableSpinner.SpinnerListener {
    String strResponse;
    long res = 0;
    LinearLayout ll_equip, ll_totalfarmers_installed, ll_totalfarmers, ll_data;
    TextView tv_equip, tv_totalfarmers_installed, tv_totalfarmers;
    SearchableSpinner sp_mandal;
    private DBHelper dbHelper;
    @SuppressLint("HandlerLeak")
    private Handler handFetchDataForfarmerinfo = new Handler() {

        public void handleMessage(Message msg) {
            System.out.println("nk handMandalData :" + strResponse);
            strResponse = "{\"Status\":\"$200\",\"Message\":\"Success\",\"Farmers\":{\"TotalFarmers\":371,\"FarmerInfo\":[{\"GPID\":\"104902\",\"GPName\":\"Nallepalle\",\"TotalFarmers\":85},{\"GPID\":\"104903\",\"GPName\":\"Kondepalle\",\"TotalFarmers\":43},{\"GPID\":\"104907\",\"GPName\":\"Kotagaram\",\"TotalFarmers\":98},{\"GPID\":\"104913\",\"GPName\":\"Gangadharnellore\",\"TotalFarmers\":88},{\"GPID\":\"104925\",\"GPName\":\"Thugundrum\",\"TotalFarmers\":57}]},\"InstalledFarmers\":{\"InstalledFarmers\":319,\"InstalledFarmersInfo\":[{\"GPID\":\"104902\",\"Panchayat_Name\":\"Nallepalle\",\"InstalledFarmers\":85},{\"GPID\":\"104903\",\"Panchayat_Name\":\"Kondepalle\",\"InstalledFarmers\":36},{\"GPID\":\"104907\",\"Panchayat_Name\":\"Kotagaram\",\"InstalledFarmers\":92},{\"GPID\":\"104913\",\"Panchayat_Name\":\"Gangadharnellore\",\"InstalledFarmers\":77},{\"GPID\":\"104925\",\"Panchayat_Name\":\"Thugundrum\",\"InstalledFarmers\":29}]},\"Equipments\":{\"TotalEquipments\":12,\"EquipmentInfo\":[{\"GPID\":\"104902\",\"GPName\":\"Nallepalle\",\"TotalEquipments\":3},{\"GPID\":\"104903\",\"GPName\":\"Kondepalle\",\"TotalEquipments\":3},{\"GPID\":\"104907\",\"GPName\":\"Kotagaram\",\"TotalEquipments\":3},{\"GPID\":\"104913\",\"GPName\":\"Gangadharnellore\",\"TotalEquipments\":2},{\"GPID\":\"104925\",\"GPName\":\"Thugundrum\",\"TotalEquipments\":1}]}}";
            closeProgressDialog();
            try {
                JSONObject jobjFARMERS = new JSONObject(strResponse);

                if (jobjFARMERS.getString("Status").startsWith("$200")) {
                    JSONObject jobjFARMERS_info = new JSONObject(jobjFARMERS.getString("Farmers"));
                    JSONObject jobjFARMERS_installed_info = new JSONObject(jobjFARMERS.getString("InstalledFarmers"));
                    JSONObject jobjFARMERS_equip_info = new JSONObject(jobjFARMERS.getString("Equipments"));
                    PrefManger.putSharedPreferencesString(MAOFarmer_info_view.this, Constants.sp_farmerinfo, jobjFARMERS_info.toString());
                    PrefManger.putSharedPreferencesString(MAOFarmer_info_view.this, Constants.sp_farmerinfo_installed, jobjFARMERS_installed_info.toString());
                    PrefManger.putSharedPreferencesString(MAOFarmer_info_view.this, Constants.sp_farmerinfo_equip, jobjFARMERS_equip_info.toString());
                    setDataToLayout();
                } else if (jobjFARMERS.getString("Status").startsWith("$100")) {
                    findViewById(R.id.ll_farmer).setVisibility(View.GONE);
                    findViewById(R.id.tv_status).setVisibility(View.VISIBLE);

                    //Helper.AlertMesg(SeedWithGridList.this, getString(R.string.no_available));
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                findViewById(R.id.ll_farmer).setVisibility(View.GONE);
                findViewById(R.id.tv_status).setVisibility(View.VISIBLE);
                /*test case*/
            /*    try {
                    JSONObject jobjFARMERS = new JSONObject(strResponse);
                    JSONObject jobjFARMERS_info = new JSONObject(jobjFARMERS.getString("Farmers"));
                    JSONObject jobjFARMERS_installed_info = new JSONObject(jobjFARMERS.getString("InstalledFarmers"));
                    JSONObject jobjFARMERS_equip_info = new JSONObject(jobjFARMERS.getString("Equipments"));
                    PrefManger.putSharedPreferencesString(MAOFarmer_info_view.this, Constants.sp_farmerinfo, jobjFARMERS_info.toString());
                    PrefManger.putSharedPreferencesString(MAOFarmer_info_view.this, Constants.sp_farmerinfo_installed, jobjFARMERS_installed_info.toString());
                    PrefManger.putSharedPreferencesString(MAOFarmer_info_view.this, Constants.sp_farmerinfo_equip, jobjFARMERS_equip_info.toString());
                    setDataToLayout();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }*/


                //Helper.AlertMesg(SeedWithGridList.this, getString(R.string.no_available));
            }

        }


    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mao_farmerinfo_view);
        dbHelper = new DBHelper(this);
        setToolBar("", "");
        findViews();

    }

    private void findViews() {
        ll_data = (LinearLayout) findViewById(R.id.ll_data);
        ll_equip = (LinearLayout) findViewById(R.id.ll_equip);
        ll_totalfarmers_installed = (LinearLayout) findViewById(R.id.ll_totalfarmers_installed);
        ll_totalfarmers = (LinearLayout) findViewById(R.id.ll_totalfarmers);
        tv_equip = (TextView) findViewById(R.id.tv_equip);
        tv_totalfarmers_installed = (TextView) findViewById(R.id.tv_totalfarmers_installed);
        tv_totalfarmers = (TextView) findViewById(R.id.tv_totalfarmers);
        ll_data.setVisibility(View.GONE);
        sp_mandal = (SearchableSpinner) findViewById(R.id.sp_mandal);
        sp_mandal.setItems(loadMandals(), this);

    }

    private List<SpinnerData> loadMandals() {
        List<SpinnerData> mandalData = new ArrayList<>();

        SpinnerData data = new SpinnerData();
        List<List<String>> gpData = dbHelper.getMandalIds();
        if (gpData.size() > 0) {
            for (int i = 0; i < gpData.size(); i++) {
                data = new SpinnerData();
                data.setId(gpData.get(i).get(0));
                data.setName(gpData.get(i).get(1));
                mandalData.add(data);

            }
        }

        return mandalData;
    }


    @Override
    public void onItemsSelected(View view, List<SpinnerData> list, int i) {
        if (i != -1) {
            serverHitForformerinfo();
        }

    }

    private void setDataToLayout() {
        ll_data.setVisibility(View.VISIBLE);
        findViewById(R.id.ll_farmer).setVisibility(View.VISIBLE);
        findViewById(R.id.tv_status).setVisibility(View.GONE);
        try {
            JSONObject jsonObject = new JSONObject(PrefManger.getSharedPreferencesString(this, Constants.sp_farmerinfo, ""));
            String TOT_Farmers = jsonObject.getString("TotalFarmers");
            tv_totalfarmers.setText(getString(R.string.Total_Farmers) + " (" + TOT_Farmers.toString() + ")");
            addViewsFarmerInfo(jsonObject.getJSONArray("FarmerInfo"));

            JSONObject jsonObjectInstalled = new JSONObject(PrefManger.getSharedPreferencesString(this, Constants.sp_farmerinfo_installed, ""));
            String TOT_Farmers_INS = jsonObjectInstalled.getString("InstalledFarmers");
            tv_totalfarmers_installed.setText(String.format("%s( %s )", getString(R.string.Total_Farmers_Installed), TOT_Farmers_INS));
            addViewsFarmerinstalledInfo(jsonObjectInstalled.getJSONArray("InstalledFarmersInfo"));

            JSONObject jsonObjectequip = new JSONObject(PrefManger.getSharedPreferencesString(this, Constants.sp_farmerinfo_equip, ""));
            String TOT_EQUIP = jsonObjectequip.getString("TotalEquipments");
            tv_equip.setText(String.format("%s( %s )", getString(R.string.Total_Equipments), TOT_EQUIP));
            addViewsFarmerequipInfo(jsonObjectequip.getJSONArray("EquipmentInfo"));


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void addViewsFarmerInfo(JSONArray jsonArray) {
        ll_totalfarmers.removeAllViewsInLayout();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("GPName");
                String count = jsonObject.getString("TotalFarmers").toString();
                View inflatedLayout = getLayoutInflater().inflate(R.layout.row_farmerinfo, null, false);
                TextView tv_name = (TextView) inflatedLayout.findViewById(R.id.tv_name);
                TextView tv_count = (TextView) inflatedLayout.findViewById(R.id.tv_count);
                tv_name.setText(name);
                tv_count.setText(count);
                ll_totalfarmers.addView(inflatedLayout);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addViewsFarmerinstalledInfo(JSONArray jsonArray) {
        ll_totalfarmers_installed.removeAllViewsInLayout();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("Panchayat_Name");
                String count = jsonObject.getString("InstalledFarmers");
                View inflatedLayout = getLayoutInflater().inflate(R.layout.row_farmerinfo, null, false);
                TextView tv_name = (TextView) inflatedLayout.findViewById(R.id.tv_name);
                TextView tv_count = (TextView) inflatedLayout.findViewById(R.id.tv_count);
                tv_name.setText(name);
                tv_count.setText(count);
                ll_totalfarmers_installed.addView(inflatedLayout);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addViewsFarmerequipInfo(JSONArray jsonArray) {
        ll_equip.removeAllViewsInLayout();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("GPName");
                String count = jsonObject.getString("TotalEquipments");
                View inflatedLayout = getLayoutInflater().inflate(R.layout.row_farmerinfo, null, false);
                TextView tv_name = (TextView) inflatedLayout.findViewById(R.id.tv_name);
                TextView tv_count = (TextView) inflatedLayout.findViewById(R.id.tv_count);
                tv_name.setText(name);
                tv_count.setText(count);
                ll_equip.addView(inflatedLayout);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onClick_Grid_List(View view) {
        serverHitForformerinfo();
    }

    public void serverHitForformerinfo() {
        //GetLiveStockInfo GetWebLandInfo
        showProgressDialog(getString(R.string.wait));
/*
        new Thread() {
            public void run() {
                WebserviceCall wsc = new WebserviceCall();
                strResponse = wsc.callCService(Constants.url_farmerinfo,
                        */
/*Mandal id*//*

                        "MPEODashBoardCounts", new String[]{"MobileNo", "Mandal"},
                        new String[]{PrefManger.getSharedPreferencesString(MAOFarmer_info_view.this, Constants.sp_mobile, ""), sp_mandal.getSelectedItem().get(0).getId()});
                handFetchDataForfarmerinfo.sendEmptyMessage(0);
            }
        }.start();
*/
    }


}
