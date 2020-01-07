package nk.bluefrog.rythusevaoffice.activities.bulkbookingtracker;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

public class BulkBookingStatusActivity extends BluefrogActivity {

    TrackerModel trackerModel;
    TextView tv_seedName, tv_Variety, tv_quantity, tv_delivery_address,
            tv_discount, tv_price, tv_shopName, tv_Name, tv_mini_qunatity,
            tv_order_qty, tv_item_price, tv_bill_discount, tv_discount_value, tv_bill_pay, tv_total_amount, tv_booking_id, tv_booking_date;
    ImageView iv_shop, iv_call;
    ProgressBar progress;
    RecyclerView rvView;
    DBHelper dbHelper;
    List<StatusModel> list = new ArrayList<>();
    private int STATUS_RESPONSE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulk_booking_status);
        setToolBar("", "");
        dbHelper = new DBHelper(this);
        if (getIntent() != null && getIntent().getExtras() != null) {
            trackerModel = (TrackerModel) getIntent().getExtras().getSerializable("TrackerModel");

        }
        findViews();
    }

    public void refreshstatus(View view) {
        serverHitForstatusLables();
    }

    public void setToolBar(String titleName, String subtitle) {
        Toolbar toolbar = (Toolbar) findViewById(nk.bluefrog.library.R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void findViews() {
        tv_seedName = findViewById(R.id.tv_seedName);
        tv_Variety = findViewById(R.id.tv_Variety);
        tv_quantity = findViewById(R.id.tv_quantity);
        tv_delivery_address = findViewById(R.id.tv_delivery_address);
        tv_discount = findViewById(R.id.tv_discount);
        tv_shopName = findViewById(R.id.tv_shopName);
        tv_price = findViewById(R.id.tv_price);

        tv_Name = findViewById(R.id.tv_Name);
        iv_shop = findViewById(R.id.iv_shop);
        iv_call = findViewById(R.id.iv_call);
        progress = findViewById(R.id.progress);
        tv_mini_qunatity = findViewById(R.id.tv_mini_qunatity);
        tv_booking_id = findViewById(R.id.tv_booking_id);
        tv_booking_date = findViewById(R.id.tv_booking_date);

        tv_order_qty = findViewById(R.id.tv_order_qty);
        tv_item_price = findViewById(R.id.tv_item_price);
        tv_bill_discount = findViewById(R.id.tv_bill_discount);
        tv_discount_value = findViewById(R.id.tv_discount_value);
        tv_bill_pay = findViewById(R.id.tv_bill_pay);
        tv_total_amount = findViewById(R.id.tv_total_amount);

        rvView = findViewById(R.id.rvView);

        rvView.setHasFixedSize(true);
        rvView.setNestedScrollingEnabled(false);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvView.setLayoutManager(layoutManager);

        setData();

        serverHitForstatusLables();
//        insertIntoDB();


    }

    private String totalPrice(String totalKgs, String pricePerKg) {
        double result = Double.parseDouble(totalKgs) * Double.parseDouble(pricePerKg);
        String totalPrice = String.valueOf(result);
        return totalPrice;
    }

    private String discountValue(String totalAmount, String discountValue) {
        double result = Double.parseDouble(totalAmount) * Double.parseDouble(discountValue) / 100;
        String discountPrice = String.valueOf(result);
        return discountPrice;

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
            jsonObject.put("commodity",trackerModel.getCommodity() );
            jsonObject.put("BookingID",trackerModel.getBooking_id() );

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }



    private void serverHitForstatusLables() {
        if (Helper.isNetworkAvailable(this)) {
            dbHelper.deleteAll(DBTables.BulkBookStatus.TABLE_NAME);
           /* String[] keys = {"BookingID"};
            String[] values = {trackerModel.getBooking_id()};*/

            RestServiceWithVolle statusService = new RestServiceWithVolle(this, new ResponseListener() {
                @Override
                public void onSuccess(int responseCode, String response) {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equals("200"))
                            list.clear();
                        {

                            JSONArray jsonArray = jsonObject.getJSONArray("BookingStatusDetails");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject resultObj = jsonArray.getJSONObject(i);

                                //"AutoID":2,"status_date":"10-01-2019 3:32 PM","status_index":"Y","status_message"
                                dbHelper.insertintoTable(DBTables.BulkBookStatus.TABLE_NAME, DBTables.BulkBookStatus.cols,
                                        new String[]{resultObj.getString("AutoID"), trackerModel.getBooking_id(), resultObj.getString("status_date"),
                                                resultObj.getString("status_index"),
                                                resultObj.getString("status_message")});

                            }

                            loadData();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void onError(int responseCode, String error) {

                }
            }, STATUS_RESPONSE, Constants.BASE_URL+ "GetBookingStatus", POST);
            statusService.loadRequest(getSendingJsonObj());

        } else {
            Helper.showToast(this, getString(R.string.no_internet_available));
        }
    }

    private void insertIntoDB() {
        for (int i = 0; i < 5; i++) {
            dbHelper.insertintoTable(DBTables.BulkBookStatus.TABLE_NAME, DBTables.BulkBookStatus.cols,
                    new String[]{trackerModel.getBooking_id(), Helper.getTodayDate("dd/MM/yyyy"), "Y", "Ready to dispatch"});
        }


    }

    private void loadData() {
        List<List<String>> db_status = dbHelper.getTableColDataByCondORDEYBY(DBTables.BulkBookStatus.TABLE_NAME, "*", DBTables.BulkBookStatus.BookingID, trackerModel.getBooking_id(), DBTables.BulkBookStatus.AutoID);
        if (db_status.size() > 0) {
            for (int i = 0; i < db_status.size(); i++) {
                StatusModel statusModel = new StatusModel();
                statusModel.setTodaydate(db_status.get(i).get(3));
                statusModel.setStatus(db_status.get(i).get(4));
                statusModel.setRemark(db_status.get(i).get(5));

                list.add(statusModel);

            }
            if (list.size() > 0) {
                BookingStatusAdapter adapter = new BookingStatusAdapter(this, list);
                rvView.setAdapter(adapter);

            }
        }
    }

    private void setData() {


        String QuantityInKgs = trackerModel.getQuantity();
        String priceInKgs = getpriceKgs(trackerModel.getShop_categroy_price());

        Constants.loadImageElseWhite(this, trackerModel.getShop_image(), iv_shop, progress);
        tv_delivery_address.setText(trackerModel.getDelivery_address());
        tv_discount.setText(trackerModel.getShop_discount() + "%" + getString(R.string.discount));

        tv_booking_id.setText(getString(R.string.booking_id) + trackerModel.getBooking_id());
        tv_booking_date.setText(trackerModel.getBookingDate());


        tv_seedName.setText(trackerModel.getCategory_name());
        tv_shopName.setText(trackerModel.getShop_name());
        if (trackerModel.getCommodity().equals("1")) {
            tv_price.setText(getString(R.string.price) + " :" + " " + priceInKgs + "₹");
            tv_mini_qunatity.setText(getString(R.string.mini_quantity) + " :" + " " + trackerModel.getShop_mini_order_quantity());
            tv_Variety.setText("Seed");
            tv_quantity.setText(QuantityInKgs + " Kgs");
        } else if (trackerModel.getCommodity().equals("2")) {
            tv_Variety.setText("Fertilizers");
            tv_price.setText(getString(R.string.price) + " :" + " " + priceInKgs + "₹");
            tv_mini_qunatity.setText(getString(R.string.mini_quantity) + " :" + " " + trackerModel.getShop_mini_order_quantity());
            tv_quantity.setText(QuantityInKgs + " Kgs");
        } else if (trackerModel.getCommodity().equals("3")) {
            ((TextView) findViewById(R.id.quantity_label)).setText(getString(R.string.number_of_pots));
            tv_Variety.setText("Nursery");
            tv_quantity.setText(trackerModel.getQuantity() + " Plants");
            tv_mini_qunatity.setText(getString(R.string.mini_pots) + " :" + " " + trackerModel.getShop_mini_order_quantity());
            tv_price.setText(getString(R.string.price_plant) + " :" + " " + trackerModel.getShop_categroy_price() + "₹");
        }

        if (trackerModel.getCommodity().equals("3")) {
            tv_order_qty.setText(QuantityInKgs + " Plants");
            tv_item_price.setText(trackerModel.getShop_categroy_price() + "₹");

            String totalAmountMethod = totalPrice(QuantityInKgs, trackerModel.getShop_categroy_price());
            tv_total_amount.setText(totalAmountMethod + "₹");

            tv_bill_discount.setText(trackerModel.getShop_discount() + " %" + getString(R.string.discount));
            String discountAmountMethod = discountValue(totalAmountMethod, trackerModel.getShop_discount());
            tv_discount_value.setText("-" + discountAmountMethod + "₹");

            double totalPrice = Double.parseDouble(totalAmountMethod) - Double.parseDouble(discountAmountMethod);

            tv_bill_pay.setText(String.valueOf(totalPrice) + "₹");


        } else {
            tv_order_qty.setText(QuantityInKgs + " Kgs");
            tv_item_price.setText(priceInKgs + "₹");

            String totalAmountMethod = totalPrice(QuantityInKgs, priceInKgs);
            tv_total_amount.setText(totalAmountMethod + "₹");

            tv_bill_discount.setText(trackerModel.getShop_discount() + " %" + getString(R.string.discount));
            String discountAmountMethod = discountValue(totalAmountMethod, trackerModel.getShop_discount());
            tv_discount_value.setText("-" + discountAmountMethod + "₹");

            double totalPrice = Double.parseDouble(totalAmountMethod) - Double.parseDouble(discountAmountMethod);

            tv_bill_pay.setText(String.valueOf(totalPrice) + "₹");

        }


    }

    private String getQuintalIntoKgs(String quantity) {
        String quantialInkgs;
        int result = Integer.parseInt(quantity) * 100;
        quantialInkgs = String.valueOf(result);
        return quantialInkgs;
    }

    private String getpriceKgs(String price) {
        String priceInkgs;
        double result = (Double.parseDouble(price) / 100);
        priceInkgs = String.valueOf(result);
        return priceInkgs;
    }

}
