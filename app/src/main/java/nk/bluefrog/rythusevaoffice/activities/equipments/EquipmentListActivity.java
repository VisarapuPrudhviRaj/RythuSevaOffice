package nk.bluefrog.rythusevaoffice.activities.equipments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import nk.bluefrog.library.BluefrogActivity;
import nk.bluefrog.library.network.ResponseListener;
import nk.bluefrog.library.network.RestServiceWithVolle;
import nk.bluefrog.library.utils.Helper;
import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.activities.masterdata.MasterDataActivity;
import nk.bluefrog.rythusevaoffice.utils.Constants;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;
import nk.bluefrog.rythusevaoffice.utils.DBTables;

import static nk.bluefrog.library.network.ResponseListener.POST;

public class EquipmentListActivity extends BluefrogActivity {

    DBHelper dbHelper;
    private int REQUEST_EQUIP_CAT_DATA = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_list);
        setToolBar(getString(R.string.menu_near_equipments),"");
        dbHelper = new DBHelper(this);

    }


    public void onClick_equipavailablity(View view) {
        Intent intent = new Intent(EquipmentListActivity.this, EquipAvaliabilityGridListMapActivity.class);
        intent.putExtra("commodityType","6");
        startActivity(intent);
    }

    public void onClick_chcequipavailablity(View view) {
        Intent intent = new Intent(EquipmentListActivity.this, EquipAvaliabilityGridListMapActivity.class);
        intent.putExtra("commodityType","7");
        startActivity(intent);
    }



    public void onclick_equipreg(View view) {
        if (dbHelper.getCount(DBTables.Category.TABLE_NAME) == 0) {
            if (Helper.isNetworkAvailable(this)) {
                serverHitForCategoryDetails();
            } else {
                Helper.AlertMsg(this, getString(R.string.no_internet_available));
            }
        } else {
            Intent intent = new Intent(EquipmentListActivity.this, EquipmentRegistrationActivity.class);
            startActivity(intent);
        }


    }

    public void serverHitForCategoryDetails() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("User_Id", dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.UNIQUE_ID));
            jsonObject.put("User_MobileNo", dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.MOBILE));
            jsonObject.put("User_Type", "O");
            jsonObject.put("OfficerType", dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.OFFICER_TYPE));
            jsonObject.put("commodity",  "6");
            jsonObject.put("IMEI", Helper.getIMEINumber(this));
            jsonObject.put("Version", getString(R.string.version));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RestServiceWithVolle soapService = new RestServiceWithVolle(this, new ResponseListener() {
            @Override
            public void onSuccess(int responseCode, String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("status").contentEquals("200")) {

                        JSONArray categoryArray = jsonObject.getJSONArray("Data");
                        for (int i = 0; i < categoryArray.length(); i++) {
                            JSONObject catObj = categoryArray.getJSONObject(i);
                            dbHelper.insertintoTable(DBTables.Category.TABLE_NAME, DBTables.Category.category_columns,
                                    new String[]{catObj.getString("cid"), catObj.getString("cname"),
                                            catObj.getString("vid"), catObj.getString("vname")});

                        }

                        Intent intent = new Intent(EquipmentListActivity.this, EquipmentRegistrationActivity.class);
                        startActivity(intent);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onError(int responseCode, String error) {

            }
        }, REQUEST_EQUIP_CAT_DATA, Constants.BASE_URL
                + Constants.METHOD_CROP_MASTER,POST, true);

        soapService.loadRequest(jsonObject);

    }


}
