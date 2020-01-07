package nk.bluefrog.rythusevaoffice.activities.masterdata;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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

import static nk.bluefrog.library.network.ResponseListener.POST;

public class MasterDataActivity extends BluefrogActivity {

    DBHelper dbHelper;
    LinearLayout ll_progressbar, ll_completed;
    TextView tv_msg, tv_progressbar;
    private int CROP_MASTER_CODE = 101;
    private Handler handlloadintoDB = new Handler() {
        public void handleMessage(android.os.Message msg) {
            // closeProgressDialog();

            if (msg.what == 0) {
                dbHelper.deleteAll(DBTables.CategoryAndVarities.TABLE_NAME);
                alertMsg(getString(R.string.not_update_categories));
            } else {

                if (msg.what == 1) {
                    cropMasterData(2);
                } else if (msg.what == 2) {
                    cropMasterData(3);
                } else if (msg.what == 3) {
                    cropMasterData(6);
                } else {
                    alertMsg(getString(R.string.update_categories));

                }

            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_data);
        this.setFinishOnTouchOutside(false);
        dbHelper = new DBHelper(this);
        dbHelper.deleteAll(DBTables.CategoryAndVarities.TABLE_NAME);

        findviews();

    }

    private void findviews() {
        ll_progressbar = (LinearLayout) findViewById(R.id.ll_progressbar);
        ll_progressbar.setVisibility(View.VISIBLE);
        ll_completed = (LinearLayout) findViewById(R.id.ll_completed);
        ll_completed.setVisibility(View.GONE);
        tv_msg = (TextView) findViewById(R.id.tv_msg);
        tv_progressbar = (TextView) findViewById(R.id.tv_progressbar);
        cropMasterData(1);
    }

    private void alertMsg(String msg) {
        ll_progressbar.setVisibility(View.GONE);
        ll_completed.setVisibility(View.VISIBLE);
        tv_msg.setText(msg);

    }

    private void cropMasterData(final int type) {
        tv_progressbar.setText(R.string.please_wait_serverhit);

        if (Helper.isNetworkAvailable(this)) {


            RestServiceWithVolle updateService = new RestServiceWithVolle(this, new ResponseListener() {
                @Override
                public void onSuccess(int responseCode, String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            loadDataIntoDB(response, type);
                        } else {
                            String message = jsonObject.getString("message");
                            alertMsg(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        alertMsg(e.getMessage());
                    }
                }

                @Override
                public void onError(int responseCode, String error) {
                    Log.d("error", "onError: " + error);
                    alertMsg(error);
                }
            }, CROP_MASTER_CODE, Constants.BASE_URL + "GetAllMasters", POST, false);
            updateService.loadRequest(cropJsonObject(type));
        } else {
            alertMsg(getString(R.string.no_internet_available));
        }


    }

    public void loadDataIntoDB(final String strResponse, final int type) {
        tv_progressbar.setText("Please wait ... Master Loading...");
        //  showProgressDialog("Please wait ... Master Loading...");
        new Thread() {
            public void run() {
                int status = 1;
                try {
                    JSONObject jsonObject = new JSONObject(strResponse);
                    JSONArray jsonArray = jsonObject.getJSONArray("Data");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject resultObj = jsonArray.getJSONObject(i);
                        insertIntoDB(resultObj, type);
                    }
                } catch (JSONException e) {
                    status = 0;
                    e.printStackTrace();

                }

                if (status == 0) {
                    handlloadintoDB.sendEmptyMessage(status);
                } else {
                    handlloadintoDB.sendEmptyMessage(type);
                }

            }


        }.start();
    }

    private void insertIntoDB(JSONObject resultObj, int type) {

        try {
            dbHelper.insertintoTable(DBTables.CategoryAndVarities.TABLE_NAME, DBTables.CategoryAndVarities.cols,
                    new String[]{resultObj.getString("cid"), resultObj.getString("cname"),
                            resultObj.getString("vid"),
                            resultObj.getString("vname"), type + ""});
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private JSONObject cropJsonObject(int type) {
        /*{ "User_Id": "22", "User_Type": "O", "OfficerType": "1", "MobileNo": "7306676544", "commodity":"1", "IMEI": "4354356454324234", "Version": "3.0" }
         */
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("User_Id", dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.UNIQUE_ID));
            jsonObject.put("User_MobileNo", dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.MOBILE));
            jsonObject.put("User_Type", "O");
            jsonObject.put("OfficerType", dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.OFFICER_TYPE));
            jsonObject.put("commodity", type + "");
            jsonObject.put("IMEI", Helper.getIMEINumber(MasterDataActivity.this));
            jsonObject.put("Version", getString(R.string.version));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }


    public void onClick_ok(View view) {
        finish();
    }
}
