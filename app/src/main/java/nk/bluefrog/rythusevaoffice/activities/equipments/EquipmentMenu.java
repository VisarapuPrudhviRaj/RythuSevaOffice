package nk.bluefrog.rythusevaoffice.activities.equipments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import nk.bluefrog.library.BluefrogActivity;
import nk.bluefrog.library.network.ResponseListener;
import nk.bluefrog.library.network.RestServiceWithVolle;
import nk.bluefrog.library.utils.Helper;
import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.utils.Constants;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;
import nk.bluefrog.rythusevaoffice.utils.DBTables;

public class EquipmentMenu extends BluefrogActivity implements ResponseListener {

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_menu);
        setToolBar(getString(R.string.equipment_menu),"");
        dbHelper= new DBHelper(this);
    }

    public void equipmentAvailability(View view) {

        Intent intent = new Intent(this, EquipAvaliabilityGridListMapActivity.class);
        startActivity(intent);
    }

    public void updateCategories(View view) {
        if (Helper.isNetworkAvailable(getApplicationContext())) {
            serverHitForCategoryDetails();
        } else {
            Helper.AlertMesg(this, getString(R.string.network));
        }
    }

    public void serverHitForCategoryDetails() {


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

     /*   SoapService soapService = new SoapService(this,this,1,Constants.url_hub
                , Constants.METHOD_EQUIPMENT_CATEGORIES, new String[]{}, new String[]{},true);

        soapService.execute();*/

    }

    @Override
    public void onSuccess(int responseCode, String response) {

        Log.d("response",response);

        try {
            JSONObject object = new JSONObject(response);

            if(object.getString("status").contentEquals("200")){

                int count = dbHelper.getCount(DBTables.Category.TABLE_NAME);

                if(count>0){
                    dbHelper.deleteAll(DBTables.Category.TABLE_NAME);
                }

                JSONArray categoryArray = object.getJSONArray("Data") ;

                /*"Category_Id":"1","Category":"Tractor Drawn Equipment","Equipment_ID":"2","Equipment_Name":"MB Plough"}*/

                for (int i = 0; i <categoryArray.length() ; i++) {

                    JSONObject categoryObject = categoryArray.getJSONObject(i);

                    dbHelper.insertintoTable(DBTables.Category.TABLE_NAME, DBTables.Category.category_columns,
                            new String[]{categoryObject.getString("cid"), categoryObject.getString("cname"),
                                    categoryObject.getString("vid"), categoryObject.getString("vname")});


                }

                Helper.AlertMesg(this, getString(R.string.update_category));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    @Override
    public void onError(int responseCode, String error) {

    }
}
