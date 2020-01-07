package nk.bluefrog.rythusevaoffice.activities.login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import nk.bluefrog.library.BluefrogActivity;
import nk.bluefrog.library.network.ResponseListener;
import nk.bluefrog.library.network.RestServiceWithVolle;
import nk.bluefrog.library.receiver.SmsReceiver;
import nk.bluefrog.library.utils.Helper;
import nk.bluefrog.library.utils.PermissionResult;
import nk.bluefrog.library.utils.PrefManger;
import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.activities.home.JD_ADHomeActivity;
import nk.bluefrog.rythusevaoffice.activities.home.MAOHomeActivity;
import nk.bluefrog.rythusevaoffice.activities.home.MPEOHomeActivity;
import nk.bluefrog.rythusevaoffice.utils.Constants;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;
import nk.bluefrog.rythusevaoffice.utils.DBTables;

public class LoginActivity extends BluefrogActivity implements ResponseListener, SmsReceiver.OTPListener {


    ProgressDialog pd_fcm;
    /*9603697620*/
    private EditText etMobileNumber;
    private EditText etPassword;
    private DBHelper dbHelper;
    private String strResponse;
    @SuppressLint("HandlerLeak")
    private Handler handlerDB = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            Constants.closeDottedProgress();

            if (dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.OFFICER_TYPE).equals("1")) {
                PrefManger.putSharedPreferencesBoolean(LoginActivity.this, Constants.KEY_IS_LOGIN, true);
                Intent in = new Intent(LoginActivity.this, MPEOHomeActivity.class);
                startActivity(in);
                finish();
            } else if (dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.OFFICER_TYPE).equals("2")) {
                PrefManger.putSharedPreferencesBoolean(LoginActivity.this, Constants.KEY_IS_LOGIN, true);
                Intent in = new Intent(LoginActivity.this, MAOHomeActivity.class);
                startActivity(in);
                finish();
            } else if (dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.OFFICER_TYPE).equals("3")) {
                PrefManger.putSharedPreferencesBoolean(LoginActivity.this, Constants.KEY_IS_LOGIN, true);
                Intent in = new Intent(LoginActivity.this, JD_ADHomeActivity.class);
                startActivity(in);
                finish();
            }
        }
    };
    private String mpoNumber;
    private String mpoName;
    private String distID, distName;
    private String mandalID, mandalName;
    private Handler hand = new Handler() {
        public void handleMessage(android.os.Message msg) {
            String token = PrefManger.getSharedPreferencesString(getApplicationContext(), Constants.KEY_REG_ID, null);
            if (token == null || token.length() == 0) {
                checkTokenRegistra();
            } else {
                pd_fcm.dismiss();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            askCompactPermissions(new String[]{
                    Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CALL_PHONE}, new PermissionResult() {
                @Override
                public void permissionGranted() {
                }

                @Override
                public void permissionDenied() {
                    Helper.showToast(LoginActivity.this, "Accept Permissions!");
                    finish();
                }

                @Override
                public void permissionForeverDenied() {
                    Helper.showToast(LoginActivity.this, "Accept Permissions!");
                    finish();
                }
            });

        }

    }

    private void initViews() {
        SmsReceiver.bind(this, SmsReceiver.SMS_ORIGIN);
        dbHelper = new DBHelper(this);
        etMobileNumber = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_pwd);
        startApp();
        etMobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 9) {   //7382988548
                    if (Helper.isNetworkAvailable(LoginActivity.this)) {
                        navigateToHome();
                    } else {
                        Helper.AlertMesg(LoginActivity.this, getString(R.string.no_network));
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


    }


    private void startApp() {
        pd_fcm = new ProgressDialog(this);
        // pd_fcm.setProgressStyle(R.style.TransparentProgressDialog);
        pd_fcm.setMessage(getString(R.string.wait));
        pd_fcm.setCancelable(false);
        checkTokenRegistra();

    }

    private void checkTokenRegistra() {
        if (Helper.isNetworkAvailable(getApplicationContext())) {
            if (!pd_fcm.isShowing())
                pd_fcm.show();
            new Thread() {
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            FirebaseRegId();
                        }
                    });

                    hand.sendEmptyMessage(0);
                }
            }.start();
        } else {
            pd_fcm.dismiss();

            AlertDialog.Builder ab = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
            ab.setTitle("Alert!");
            ab.setMessage(R.string.network);
            ab.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            ab.show();
        }
    }


    private void FirebaseRegId() {
        System.out.println("token ID:" + FirebaseInstanceId.getInstance().getToken());
        String token = PrefManger.getSharedPreferencesString(LoginActivity.this, Constants.KEY_REG_ID, null);
        System.out.println("token:" + token);
        if (!TextUtils.isEmpty(token)) {
            //Helper.showToast(SplashActitivty.this, "FCM Reg Id is Done!");
        } else {
            // Helper.showToast(SplashActitivty.this, "FCM Reg Id is not received yet!");
        }
    }


    public void login(View view) {
        if (validate()) {
            if (Helper.isNetworkAvailable(this)) {
                String otp = PrefManger.getSharedPreferencesString(this, Constants.sp_otp, "");
                if (etPassword.getText().toString().contentEquals(otp)) {
                    JSONObject resObject = (JSONObject) etPassword.getTag();
                    try {
                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                        String currentDate = format.format(Calendar.getInstance().getTime());
                        dbHelper.insertintoTable(DBTables.MPOTable.TABLE_NAME
                                , DBTables.MPOTable.cols
                                , new String[]{resObject.getString("UserName"),
                                        resObject.getString("MobileNo"),
                                        "", "",
                                        "", "", currentDate,
                                        "", "",
                                        resObject.getString("OfficerType"),
                                        resObject.getString("Password"),
                                        resObject.getString("Unique_Id")});
                        PrefManger.putSharedPreferencesString(this, Constants.sp_mobile, resObject.getString("MobileNo"));
                        PrefManger.putSharedPreferencesString(this, Constants.sp_name, resObject.getString("UserName"));
                        final JSONArray dataArray = resObject.getJSONArray("Data");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                insertDataIntoDb(dataArray);
                            }
                        }).start();
                    } catch (Exception e) {
                        Helper.AlertMesg(this, e.getMessage());
                        e.getMessage();
                    }
                } else {
                    Helper.AlertMesg(this, getString(R.string.enter_valid_otp));
                }
            } else {
                Helper.AlertMesg(this, getString(R.string.no_internet_available));
            }
        }

/*
        if (etPassword.getText().toString().isEmpty()) {
            Helper.AlertMesg(this, getString(R.string.otp_enter));
        } else {
            String otp = PrefManger.getSharedPreferencesString(this, Constants.sp_otp, "");
            if (etPassword.getText().toString().contentEquals(otp)) {
                getFarmerDetails();
            } else {
                Helper.AlertMesg(this, getString(R.string.enter_valid_otp));
            }
        }
*/

       /* if(validate()){

            if(Helper.isNetworkAvailable(this)){
                navigateToHome();
            }else{
                Helper.AlertMesg(this,getString(R.string.no_network));
            }

        }*/


    }


    private void navigateToHome() {
        /*{"Mobile_No":"7306676544","DeviceId":"DDDD","Password":"999"}*/
        final String keys[] = {"postString"};
        JSONObject sendObject = new JSONObject();
        try {
            sendObject.put("MobileNo", etMobileNumber.getText().toString());
            sendObject.put("DeviceId", PrefManger.getSharedPreferencesString(this, Constants.KEY_REG_ID, ""));
            sendObject.put("IMEI", Helper.getIMEINumber(LoginActivity.this));
            sendObject.put("Version", getResources().getString(R.string.version));
            sendObject.put("Password", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String values[] = {sendObject.toString()};
        Constants.showDottedProgress(this, getString(R.string.wait));
        RestServiceWithVolle soapService = new RestServiceWithVolle(this, this, 1, Constants.BASE_URL
                + Constants.METHOD_OFFICER_LOGIN, POST, false);
        soapService.loadRequest(sendObject);
    }


    private void insertDataIntoDb(JSONArray dataArray) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        String currentDate = format.format(Calendar.getInstance().getTime());
        if (dataArray.length() > 0) {
            for (int i = 0; i < dataArray.length(); i++) {
                try {
                    JSONArray farmerArray = dataArray.getJSONObject(i).getJSONArray("FarmerDetails");
                    if (farmerArray.length() > 0) {
                        for (int j = 0; j < farmerArray.length(); j++) {
                            String farmerName = farmerArray.getJSONObject(j).getString("FarmerName");
                            if (farmerName.contentEquals("") || farmerName.contentEquals("NA")) {
                                farmerName = "NA";
                            }
                            String mobileNumber = farmerArray.getJSONObject(j).getString("MobileNo");
                            if (mobileNumber.contentEquals("") || mobileNumber.contentEquals("NA")) {
                                mobileNumber = "NA";
                            }
                            String aadharid = farmerArray.getJSONObject(j).getString("Unique_Id");

                            if (aadharid.contentEquals("") || aadharid.contentEquals("NA")) {
                                aadharid = "NA";
                            }
                            String image = farmerArray.getJSONObject(j).getString("Image");
                            if (image.contentEquals("") || image.contentEquals("NA")) {
                                image = "NA";
                            }
                            dbHelper.insertintoTable(DBTables.FarmerTable.TABLE_NAME
                                    , DBTables.FarmerTable.cols
                                    , new String[]{dataArray.getJSONObject(i).getString("GPID"), mobileNumber, farmerName, aadharid, currentDate,
                                            image, dataArray.getJSONObject(i).getString("GPName"), dataArray.getJSONObject(i).getString("DID")
                                            , dataArray.getJSONObject(i).getString("DName"), dataArray.getJSONObject(i).getString("MID"),
                                            dataArray.getJSONObject(i).getString("MName")});
                            //Constants.closeDottedProgress();

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        handlerDB.sendEmptyMessage(0);
    }


    private boolean validate() {
        boolean flag = true;
        if (etMobileNumber.getText().toString().isEmpty()) {
            flag = false;
            Helper.showToast(this, getString(R.string.enter_mobile_number));
        } else if (!Helper.isValidMobile(etMobileNumber)) {
            flag = false;
            Helper.showToast(this, getString(R.string.enter_valid_mobile_number));
        } else if (etPassword.getText().toString().isEmpty()) {
            flag = false;
            Helper.showToast(this, getString(R.string.enter_pwd));
        }
        return flag;

    }

    private void showAlert(String msg) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        // Setting Dialog Title
        alertDialog.setTitle(R.string.app_name);
        //alertDialog.setIcon(R.drawable.app);
        alertDialog.setCancelable(false);
        // Setting Dialog Message
        alertDialog.setMessage(msg);
        alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.create().dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onSuccess(int responseCode, String response) {
        Log.d("response", response);
        try {
            JSONObject resObject = new JSONObject(response);
            if (responseCode == 1) {
                Constants.closeDottedProgress();
                if (resObject.getString("status").contentEquals("200")) {
                    etPassword.setTag(resObject);
                    PrefManger.putSharedPreferencesString(this, Constants.sp_mobile, resObject.getString("MobileNo"));
                    PrefManger.putSharedPreferencesString(this, Constants.sp_otp, resObject.getString("Password"));
                    Helper.showToast(this, "OTP SENT TO :" + resObject.getString("MobileNo"));
                } else {
                    Constants.closeDottedProgress();
                    showAlert(resObject.getString("message"));
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(int responseCode, String error) {
        showAlert(error);
    }

    @Override
    public void otpReceived(String messageText) {
        etPassword.setText(messageText);
    }
}
