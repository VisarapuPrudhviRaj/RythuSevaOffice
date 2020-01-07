package nk.bluefrog.rythusevaoffice.activities.bulkbookingtracker;

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

public class BulkBookingTrackerListActivity extends BluefrogActivity {

    RecyclerView rvView;
    DBHelper dbHelper;
    List<TrackerModel> list = new ArrayList<>();
    private int RESPONSE_FETCH = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulk_booking_tracker_list);
        dbHelper = new DBHelper(this);
        setToolBar(getString(R.string.bulk_booking_tracking), "");
        findViews();

    }

    private void findViews() {
        rvView = findViewById(R.id.rvView);
        rvView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvView.setLayoutManager(layoutManager);

        checkData();

    }

    private void checkData() {
        /*db table checking  insertion & server hit*/
        if (dbHelper.getCount(DBTables.BulkBookingTracker.TABLE_NAME) == 0) {
//            insertIntoDB();
            serverHitForList();
        } else {
            loadData();
        }
    }

    private void loadData() {
   /*{booking_id,commodity,category_id,subcategory_id,category_name,quantity,delivery_address,lat,lng,
            shop_id,shop_name,shop_owner_name,shop_phnumber,shop_image,shop_address,shop_gps,
            shop_discount,shop_mini_order_quantity,shop_category_quantity,shop_categroy_price}*/

        List<List<String>> db_data = dbHelper.getTableData(DBTables.BulkBookingTracker.TABLE_NAME);
        if (db_data.size() > 0) {
            list.clear();
            for (int i = 0; i < db_data.size(); i++) {
                TrackerModel model = new TrackerModel();
                model.setRow_data(db_data.get(i));
                model.setBooking_id(db_data.get(i).get(1));
                model.setCommodity(db_data.get(i).get(2));
                model.setCategory_id(db_data.get(i).get(3));
                model.setSubcategory_id(db_data.get(i).get(4));
                model.setCategory_name(db_data.get(i).get(5));
                model.setQuantity(db_data.get(i).get(6));
                model.setDelivery_address(db_data.get(i).get(7));
                model.setLat(db_data.get(i).get(8));
                model.setLng(db_data.get(i).get(9));
                model.setShop_id(db_data.get(i).get(10));
                model.setShop_name(db_data.get(i).get(11));
                model.setShop_owner_name(db_data.get(i).get(12));
                model.setShop_phnumber(db_data.get(i).get(13));
                model.setShop_image(db_data.get(i).get(14));
                model.setShop_address(db_data.get(i).get(15));
                model.setShop_gps(db_data.get(i).get(16));
                model.setShop_discount(db_data.get(i).get(17));
                model.setShop_mini_order_quantity(db_data.get(i).get(18));
                model.setShop_category_quantity(db_data.get(i).get(19));
                model.setShop_categroy_price(db_data.get(i).get(20));
                model.setBookingDate(db_data.get(i).get(24));
                list.add(model);
            }

            if (list.size() > 0) {
                (findViewById(R.id.tv_nodata)).setVisibility(View.GONE);
                BookingTrackerAdapter adapter = new BookingTrackerAdapter(list, this);
                rvView.setAdapter(adapter);

            } else {
                (findViewById(R.id.tv_nodata)).setVisibility(View.VISIBLE);
            }
        } else {
            (findViewById(R.id.tv_nodata)).setVisibility(View.VISIBLE);
        }


    }


    private JSONObject getSendingJsonObj() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("User_Id", dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.UNIQUE_ID));
            jsonObject.put("User_Type", "O");
            jsonObject.put("OfficerType", dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.OFFICER_TYPE));
            jsonObject.put("MobileNo", PrefManger.getSharedPreferencesString(this, Constants.sp_mobile, ""));
            jsonObject.put("IMEI", Helper.getIMEINumber(this));
            jsonObject.put("Version", getString(R.string.version));
            jsonObject.put("DeviceId", PrefManger.getSharedPreferencesString(this, Constants.KEY_REG_ID, ""));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }


    private void serverHitForList() {

        if (Helper.isNetworkAvailable(this)) {
            String phone = dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.MOBILE);
           /* String[] keys = {"MobileNo"};
            String[] values = {phone};*/

            RestServiceWithVolle listService = new RestServiceWithVolle(this, new ResponseListener() {
                @Override
                public void onSuccess(int responseCode, String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("OfficerBookingDetails");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonResult = jsonArray.getJSONObject(i);

                                if (dbHelper.getCountByValues(DBTables.BulkBookingTracker.TABLE_NAME, new String[]{DBTables.BulkBookingTracker.booking_id}, new String[]{jsonResult.getString("BookingID")}) == 0) {
                                    dbHelper.insertintoTable(DBTables.BulkBookingTracker.TABLE_NAME, DBTables.BulkBookingTracker.cols,
                                            new String[]{jsonResult.getString("BookingID"),
                                                    jsonResult.getString("commodity") + "",
                                                    jsonResult.getString("category_id"),
                                                    jsonResult.getString("subcategory_id"),
                                                    jsonResult.getString("category_name"),
                                                    jsonResult.getString("quantity") + "",
                                                    jsonResult.getString("delivery_address"),
                                                    jsonResult.getString("lat"),
                                                    jsonResult.getString("lng"),
                                                    jsonResult.getString("shop_id"),
                                                    jsonResult.getString("shop_name"),
                                                    jsonResult.getString("shop_owner_name"),
                                                    jsonResult.getString("shop_phnumber"),
                                                    jsonResult.getString("shop_image"),
                                                    jsonResult.getString("shop_address"),
                                                    jsonResult.getString("shop_gps"),
                                                    jsonResult.getString("shop_discount"),
                                                    jsonResult.getString("shop_mini_order_quantity") + "",
                                                    jsonResult.getString("shop_category_quantity") + "",
                                                    jsonResult.getString("shop_categroy_price"),
                                                    jsonResult.getString("MPEOName"),
                                                    jsonResult.getString("MPEOPhNo"),
                                                    jsonResult.getString("DID"),
                                                    jsonResult.getString("BookingDate")});

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
                    Helper.AlertMsg(BulkBookingTrackerListActivity.this, error);


                }
            }, RESPONSE_FETCH, Constants.BASE_URL + "GetBulkBookingbyOfficer", POST);
            listService.loadRequest(getSendingJsonObj());


        } else {
            Helper.showToast(this, getString(R.string.no_internet_available));
        }

    }

    private void insertIntoDB() {
         /*{booking_id,commodity,category_id,subcategory_id,category_name,quantity,delivery_address,lat,lng,
            shop_id,shop_name,shop_owner_name,shop_phnumber,shop_image,shop_address,shop_gps,
            shop_discount,shop_mini_order_quantity,shop_category_quantity,shop_categroy_price}*/


        loadData();
    }

    public void onClick_refresh(View view) {
        serverHitForList();
    }
}
