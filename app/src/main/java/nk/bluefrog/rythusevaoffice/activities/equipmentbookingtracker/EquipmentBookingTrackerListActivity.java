package nk.bluefrog.rythusevaoffice.activities.equipmentbookingtracker;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import nk.bluefrog.library.BluefrogActivity;
import nk.bluefrog.library.network.ResponseListener;
import nk.bluefrog.library.network.RestServiceWithVolle;
import nk.bluefrog.library.utils.Helper;
import nk.bluefrog.library.utils.PrefManger;
import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.utils.Constants;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;
import nk.bluefrog.rythusevaoffice.utils.DBTables;

import static nk.bluefrog.library.network.ResponseListener.POST;

public class EquipmentBookingTrackerListActivity extends BluefrogActivity {

    RecyclerView rvView;
    DBHelper dbHelper;
    List<EquipmentTrackerModel> list = new ArrayList<>();
    private int RESPONSE_FETCH = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_booking_tracker_list);
        dbHelper = new DBHelper(this);
        setToolBar(getString(R.string.equipment_booking_tracking), "");
        findViews();

    }

    private void findViews() {
        rvView = findViewById(R.id.rvView);
        rvView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvView.setLayoutManager(layoutManager);
//        dbHelper.deleteAll(DBTables.UserOrders.TABLE_NAME_);
        checkData();

    }

    private void checkData() {
        /*db table checking  insertion & server hit*/
        if (dbHelper.getCount(DBTables.UserOrders.TABLE_NAME_) == 0) {
            serverHitForList();
        } else {
            loadData();
        }
    }

    private void loadData() {
   /*{booking_id,commodity,category_id,subcategory_id,category_name,quantity,delivery_address,lat,lng,
            shop_id,shop_name,shop_owner_name,shop_phnumber,shop_image,shop_address,shop_gps,
            shop_discount,shop_mini_order_quantity,shop_category_quantity,shop_categroy_price}*/

        List<List<String>> db_data = dbHelper.getTableData(DBTables.UserOrders.TABLE_NAME_);
        if (db_data.size() > 0) {
            list.clear();
            for (int i = 0; i < db_data.size(); i++) {
                EquipmentTrackerModel model = new EquipmentTrackerModel();
                model.setRow_data(db_data.get(i));
                model.setEquipment_Id(db_data.get(i).get(1));
                model.setBarcode(db_data.get(i).get(2));
                model.setSerialNo(db_data.get(i).get(2));
                model.setEquipment_Rent(db_data.get(i).get(4));
                model.setEquipment_Image_Path(db_data.get(i).get(5));
                model.setEquipment_GPS(db_data.get(i).get(6));
                model.setEquipment_Address(db_data.get(i).get(7));
                model.setUser_Name(db_data.get(i).get(8));
                model.setOwner_User_Id(db_data.get(i).get(9));
                model.setOwner_Mobile_No(db_data.get(i).get(10));
                model.setEquipment_Status_Id(db_data.get(i).get(11));
                model.setBooking_Id(db_data.get(i).get(12));
                model.setCustomer_Name(db_data.get(i).get(13));
                model.setMobile_No(db_data.get(i).get(14));
                model.setAddress(db_data.get(i).get(15));
                model.setCustomer_GPS(db_data.get(i).get(16));
                model.setStart_Date(db_data.get(i).get(17));
                model.setEnd_Date(db_data.get(i).get(18));
                model.setPurpose(db_data.get(i).get(19));
                model.setOrder_Status_ID(db_data.get(i).get(20));
                model.setCustomer_ID(db_data.get(i).get(21));
                model.setEquipment_Rent_Arce(db_data.get(i).get(22));
                model.setBooking_date(db_data.get(i).get(23));
                model.setCommodity(db_data.get(i).get(24));
                model.setShopName(db_data.get(i).get(25));
                model.setUserType(db_data.get(i).get(26));

                list.add(model);
            }

            if (list.size() > 0) {
                (findViewById(R.id.tv_nodata)).setVisibility(View.GONE);
                EquipmentBookingTrackerAdapter adapter = new EquipmentBookingTrackerAdapter(list, this);
                rvView.setAdapter(adapter);

            } else {
                (findViewById(R.id.tv_nodata)).setVisibility(View.VISIBLE);
            }
        } else {
            (findViewById(R.id.tv_nodata)).setVisibility(View.VISIBLE);
        }


    }

    private JSONObject getJsonObect() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("User_Id", dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.UNIQUE_ID));
            jsonObject.put("User_Type", "O");
            jsonObject.put("MobileNo", dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.MOBILE));
            jsonObject.put("OfficerType", dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.OFFICER_TYPE));
            jsonObject.put("DeviceId", PrefManger.getSharedPreferencesString(this,Constants.KEY_REG_ID,""));
            jsonObject.put("IMEI",Helper.getIMEINumber(this));
            jsonObject.put("Version", getString(R.string.version));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void serverHitForList() {

        if (Helper.isNetworkAvailable(this)) {
            String phone = dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.MOBILE);
            String[] keys = {"postString"};
            String[] values = {getJsonObect().toString()};

            RestServiceWithVolle listService = new RestServiceWithVolle(this, new ResponseListener() {
                @Override
                public void onSuccess(int responseCode, String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("BookingDetails");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonResult = jsonArray.getJSONObject(i);

                                if (dbHelper.getCountByValues(DBTables.UserOrders.TABLE_NAME_, new String[]{DBTables.UserOrders.Booking_Id}, new String[]{jsonResult.getString("Booking_Id")}) == 0) {
                                    dbHelper.insertintoTable(DBTables.UserOrders.TABLE_NAME_, DBTables.UserOrders.equipOders_cols,
                                            new String[]{jsonResult.getString("Equipment_Id"),
                                                    jsonResult.getString("Barcode") + "", "",
                                                    jsonResult.getString("Equipment_Rent"),
                                                    jsonResult.getString("Equipment_Image_Path"),
                                                    jsonResult.getString("Equipment_GPS") + "",
                                                    jsonResult.getString("Equipment_Address"),
                                                    jsonResult.getString("Owner_Name"),
                                                    jsonResult.getString("Owner_unique_Id"),
                                                    jsonResult.getString("Owner_Mobile_No"),
                                                    jsonResult.getString("Equipment_Status_Id"),
                                                    jsonResult.getString("Booking_Id"),
                                                    jsonResult.getString("User_Name"),
                                                    jsonResult.getString("User_MobileNo"),
                                                    jsonResult.getString("Address"),
                                                    jsonResult.getString("User_GPS"),
                                                    jsonResult.getString("Start_Date"),
                                                    jsonResult.getString("End_Date") + "",
                                                    jsonResult.getString("Purpose") + "",
                                                    jsonResult.getString("Order_Status_Id"),
                                                    dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.UNIQUE_ID),
                                                    jsonResult.getString("Equipment_Rent_Acre"),
                                                    jsonResult.getString("booking_date"),
                                                    jsonResult.getString("Commodity"),
                                                    jsonResult.getString("ShopName"),
                                                    jsonResult.getString("User_Type")});

                                } else {
                                    dbHelper.updateByValues(DBTables.UserOrders.TABLE_NAME_,
                                            new String[]{DBTables.UserOrders.Order_Status_ID},
                                            new String[]{jsonResult.getString("Order_Status_Id")},
                                            new String[]{DBTables.UserOrders.Booking_Id},
                                            new String[]{jsonResult.getString("Booking_Id")});

                                    /*Booking_Id*/
                                }
                            }
                            loadData();
                        } else {
                            (findViewById(R.id.tv_nodata)).setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        (findViewById(R.id.tv_nodata)).setVisibility(View.GONE);
                    }
                }

                @Override
                public void onError(int responseCode, String error) {
                    Log.e("Error", "onError: " + error);
                    Helper.AlertMsg(EquipmentBookingTrackerListActivity.this, error);
                }
            }, RESPONSE_FETCH, Constants.BASE_URL+ "GetEquipmentBookingDetails",POST);
            listService.loadRequest(getJsonObect());
        } else {
            Helper.showToast(this, getString(R.string.no_internet_available));
        }

    }

    private void insertIntoDB() {
         /*{booking_id,commodity,category_id,subcategory_id,category_name,quantity,delivery_address,lat,lng,
            shop_id,shop_name,shop_owner_name,shop_phnumber,shop_image,shop_address,shop_gps,
            shop_discount,shop_mini_order_quantity,shop_category_quantity,shop_categroy_price}*/

        dbHelper.insertintoTable(DBTables.UserOrders.TABLE_NAME_, DBTables.UserOrders.equipOders_cols,
                new String[]{"1", "123456789632", "", "500", "0305000001_170303175930791180.JPG",
                        "17.7118-83.99799999999999", "hyb", "raju", "0305000001", "7207286365", "1", "1703080001",
                        "ravi", "9999998888", "hud", "17.7118-83.99799999999999", "3/8/2017 12:00:00 AM",
                        "3/9/2017 12:00:00 AM", "test", "", "7306676544", "0", "", "6", "Axy Shop", "F"});


        loadData();
    }

    public void onClick_refresh(View view) {
        serverHitForList();
    }
}
