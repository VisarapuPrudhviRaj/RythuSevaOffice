package nk.bluefrog.rythusevaoffice.activities.equipmentbookingtracker;

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

public class EquipmentBookingStatusActivity extends BluefrogActivity {

    EquipmentTrackerModel trackerModel;
    TextView tv_seedName, tv_Variety, tv_purpose, tv_delivery_address,
            tv_price_acre, tv_price, tv_shopName, tv_Name, tv_mini_qunatity, tv_noreport,
            tv_booking_id, tv_booking_date;
    ImageView iv_shop, iv_call;
    ProgressBar progress;
    RecyclerView rvView;
    DBHelper dbHelper;
    List<EuipmentStatusModel> list = new ArrayList<>();
    private int STATUS_RESPONSE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_booking_status);
        setToolBar("", "");
        dbHelper = new DBHelper(this);
        /*{"Booking_Id":"1703080001","Customer_Name":"ravi","Equipment_Id":"1","Barcode":"123456789632","Mobile_No":"9999998888","Address":"hyb","Start_Date":"3/8/2017 12:00:00 AM","User_Type":"F","End_Date":"3/9/2017 12:00:00 AM","Equipment_Rent_Arce":"0","ShopName" :"Axy shop ","booking_date":"","commodity":"6","Purpose":"work","Order_Status":"Accept","Order_Status_ID":"3","Customer_GPS":"17.7118-83.99799999999999","Equipment_Rent":"500","Equipment_Image_Path":"0305000001_170303175930791180.JPG","Equipment_GPS":"17.7118-83.99799999999999","Equipment_Status_Id":"1","Equipment_Address":"hyb","Owner_User_Id":"0305000001","User_Name":"raju","Owner_Mobile_No":"7207286365"}*/
        if (getIntent() != null && getIntent().getExtras() != null) {
            trackerModel = (EquipmentTrackerModel) getIntent().getExtras().getSerializable("TrackerModel");
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
        tv_purpose = findViewById(R.id.tv_purpose);
        tv_delivery_address = findViewById(R.id.tv_delivery_address);
        tv_price_acre = findViewById(R.id.tv_price_acre);
        tv_shopName = findViewById(R.id.tv_shopName);
        tv_price = findViewById(R.id.tv_price);

        tv_Name = findViewById(R.id.tv_Name);
        iv_shop = findViewById(R.id.iv_shop);
        iv_call = findViewById(R.id.iv_call);
        progress = findViewById(R.id.progress);
        tv_mini_qunatity = findViewById(R.id.tv_mini_qunatity);
        tv_noreport = findViewById(R.id.tv_noreport);

        tv_booking_id = findViewById(R.id.tv_booking_id);
        tv_booking_date = findViewById(R.id.tv_booking_date);

        rvView = findViewById(R.id.rvView);

        rvView.setHasFixedSize(true);
        rvView.setNestedScrollingEnabled(false);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvView.setLayoutManager(layoutManager);

        setData();
        serverHitForstatusLables();


    }

    private String getEquipmentName(String equipment_id) {
        String equipName = "";
        List<List<String>> db_equipName = dbHelper.getTableColDataByCond(DBTables.CategoryAndVarities.TABLE_NAME,
                DBTables.CategoryAndVarities.catName + "," + DBTables.CategoryAndVarities.subCatName,
                new String[]{DBTables.CategoryAndVarities.subCatID, DBTables.CategoryAndVarities.categoryType},
                new String[]{equipment_id, "6"});
        if (db_equipName.size() > 0) {
            for (int i = 0; i < db_equipName.size(); i++) {
                equipName = db_equipName.get(0).get(0) + "(" + db_equipName.get(0).get(1) + ")";
            }
        }
        return equipName;
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


    private void serverHitForstatusLables() {
        if (Helper.isNetworkAvailable(this)) {
            dbHelper.deleteAll(DBTables.BulkBookStatus.TABLE_NAME);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("BookingID",trackerModel.getBooking_Id());
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
            String[] keys = {"Booking_Id"};
            String[] values = {trackerModel.getBooking_Id()};

            RestServiceWithVolle statusService = new RestServiceWithVolle(this, new ResponseListener() {
                @Override
                public void onSuccess(int responseCode, String response) {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equals("200")) {

                            JSONArray jsonArray = jsonObject.getJSONArray("ProgressDetails");

                            dbHelper.deleteByValues(DBTables.EquipBulkBookStatus.TABLE_NAME,
                                    new String[]{DBTables.EquipBulkBookStatus.Booking_Id},
                                    new String[]{trackerModel.getBooking_Id()});

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject resultObj = jsonArray.getJSONObject(i);

                                //"AutoID":2,"status_date":"10-01-2019 3:32 PM","status_index":"Y","status_message"
                                dbHelper.insertintoTable(DBTables.EquipBulkBookStatus.TABLE_NAME, DBTables.EquipBulkBookStatus.cols,
                                        new String[]{resultObj.getString("Status"),
                                                resultObj.getString("Dispatch_GPS"),
                                                resultObj.getString("Remarks"),
                                                resultObj.getString("Trans_Date"),
                                                resultObj.getString("No_of_Hours"),
                                                trackerModel.getBooking_Id()});

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
            }, STATUS_RESPONSE, Constants.BASE_URL+ "GetEquipmentProgressDetails", POST);
            statusService.loadRequest(jsonObject);

        } else {
            loadData();
            Helper.showToast(this, getString(R.string.no_internet_available));
        }
    }

    private void insertIntoDB() {
        for (int i = 0; i < 5; i++) {
            dbHelper.insertintoTable(DBTables.BulkBookStatus.TABLE_NAME, DBTables.BulkBookStatus.cols,
                    new String[]{trackerModel.getBooking_Id(), Helper.getTodayDate("dd/MM/yyyy"), "Y", "Ready to dispatch"});
        }


    }

    private void loadData() {
        List<List<String>> db_status = dbHelper.getTableColDataByCondORDEYBY(DBTables.EquipBulkBookStatus.TABLE_NAME, "*",
                DBTables.EquipBulkBookStatus.Booking_Id, trackerModel.getBooking_Id(), DBTables.EquipBulkBookStatus.Status);
        if (db_status.size() > 0) {
            list.clear();
            for (int i = 0; i < db_status.size(); i++) {
                EuipmentStatusModel statusModel = new EuipmentStatusModel();
                statusModel.setTodaydate(db_status.get(i).get(4));
                statusModel.setStatus(db_status.get(i).get(1));
                statusModel.setRemark(db_status.get(i).get(3));
                statusModel.setHours(db_status.get(i).get(5));
                list.add(statusModel);

            }
            if (list.size() > 0) {
                EquipmentBookingStatusAdapter adapter = new EquipmentBookingStatusAdapter(this, list);
                rvView.setAdapter(adapter);
                tv_noreport.setVisibility(View.GONE);
            } else {
                tv_noreport.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setData() {


        String QuantityInKgs = trackerModel.getPurpose();
        String priceInKgs = trackerModel.getEquipment_Rent();

        Constants.loadImageElseWhite(this, trackerModel.getEquipment_Image_Path(), iv_shop, progress);
        tv_delivery_address.setText(trackerModel.getAddress());
        tv_price_acre.setText(getString(R.string.rent_per_hour) + " :" + trackerModel.getEquipment_Rent_Arce() + "₹");
        tv_mini_qunatity.setText("");
        tv_Variety.setText("Equipment");
        tv_purpose.setText(QuantityInKgs);
        tv_seedName.setText(getEquipmentName(trackerModel.getEquipment_Id()));
        tv_shopName.setText(trackerModel.getShopName().equals("NA") ? "" : trackerModel.getShopName());
        tv_price.setText(getString(R.string.rent_per_hour) + " :" + " " + priceInKgs + "₹");
        tv_booking_id.setText(getString(R.string.booking_id) + trackerModel.getBooking_Id());
        tv_booking_date.setText(trackerModel.getBooking_date());


    }

    private String getQuintalIntoKgs(String quantity) {
        String quantialInkgs;
        int result = Integer.parseInt(quantity) * 100;
        quantialInkgs = String.valueOf(result);
        return quantialInkgs;
    }

    private String getpriceKgs(String price) {
        String priceInkgs;
        int result = (int) (Double.parseDouble(price) / 100);
        priceInkgs = String.valueOf(result);
        return priceInkgs;
    }

}
