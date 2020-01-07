package nk.bluefrog.rythusevaoffice.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import nk.bluefrog.library.BluefrogActivity;
import nk.bluefrog.library.utils.PrefManger;
import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.activities.ICM.ICMActivity;
import nk.bluefrog.rythusevaoffice.activities.ICM.ICMUtils;
import nk.bluefrog.rythusevaoffice.activities.home.JD_ADHomeActivity;
import nk.bluefrog.rythusevaoffice.activities.home.MAOHomeActivity;
import nk.bluefrog.rythusevaoffice.activities.home.MPEOHomeActivity;
import nk.bluefrog.rythusevaoffice.activities.home.StateOfficialsActivity;
import nk.bluefrog.rythusevaoffice.activities.login.LoginActivity;
import nk.bluefrog.rythusevaoffice.utils.Constants;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;
import nk.bluefrog.rythusevaoffice.utils.DBTables;

public class SplashActivity extends BluefrogActivity {

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        dbHelper = new DBHelper(this);

     /*   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.channelName);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_HIGH));
        }*/


        if (PrefManger.getSharedPreferencesString(this, Constants.sp_lang, "").isEmpty()) {
            PrefManger.putSharedPreferencesString(this, Constants.sp_lang, "en");
        }
/*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Constants.changeLanguage(PrefManger.getSharedPreferencesString(this, Constants.sp_lang, ""), this);
        }*/


        /*if(PrefManger.getSharedPreferencesString(this,Constants.sp_lang,"").equals("en")){
            PrefManger.putSharedPreferencesString(this,Constants.sp_lang,"en");
        }else if(PrefManger.getSharedPreferencesString(this,Constants.sp_lang,"").equals("en")) {
            PrefManger.putSharedPreferencesString(this,Constants.sp_lang,"te");
        }*/


        int SPLASH_TIME_OUT = 1000;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println("token:" + PrefManger.getSharedPreferencesString(SplashActivity.this, Constants.KEY_REG_ID, ""));
                if (PrefManger.getSharedPreferencesBoolean(SplashActivity.this, Constants.KEY_IS_LOGIN, false)) {
                    notificationData();
                } else {
                    Intent in = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(in);
                    finish();
                }


            }
        }, SPLASH_TIME_OUT);

    }


    private void notificationData() {

        Bundle dataBundle = getIntent().getExtras();
        if (dataBundle != null) {
            JSONObject notifData = new JSONObject();
            for (String key : dataBundle.keySet()) {
                try {
                    if (!TextUtils.isEmpty(dataBundle.getString(key))) {
                        System.out.println("Key:" + key + " Values:" + dataBundle.getString(key).toString());
                        try {
                            notifData.put(key, dataBundle.getString(key).toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                }
            }

            try {
               /* *//**//*{"STATUS":"N","Message":"message","from":"830957233976",
                "Title":"title","google.message_id":"0:1531993414645302%178ee793178ee793",
                "google.priority":"high","collapse_key":"nk.bluefrog.farmerhub","TimeStamp":"18-5-9665"}*//**//*
*/
                String STATUS = notifData.getString("STATUS");
                if (STATUS.trim().equals("N")) {
                    //TITLE, MESSAGE, LINK, IMAGEPATH, TIMESTAMP, IS_READ,TYPE
                    String VIDEOPATH = notifData.has(Constants.N_VIDEOPATH) ? notifData.getString(Constants.N_VIDEOPATH) : "NA";
                    String IS_IMG_VID = notifData.has(Constants.N_IS_IMG_VID) ? notifData.getString(Constants.N_IS_IMG_VID) : "NA";
                    String DEPT_NAME = notifData.has(Constants.N_DEPT_NAME) ? notifData.getString(Constants.N_DEPT_NAME) : "";
                    String REPLY = notifData.has(Constants.N_REPLY) ? notifData.getString(Constants.N_REPLY) : "N";
                    String REPLY_URL = notifData.has(Constants.N_REPLY_URL) ? notifData.getString(Constants.N_REPLY_URL) : "";


                    if (dbHelper.getCountByValue(DBTables.ICM.TABLE_NAME, DBTables.ICM.ID, notifData.getString(Constants.N_Message_Id)) == 0)
                        dbHelper.insertintoTable(DBTables.ICM.TABLE_NAME, DBTables.ICM.cols,
                                new String[]{notifData.getString(Constants.N_Message_Id),
                                        notifData.getString(Constants.N_Title),
                                        notifData.getString(Constants.N_Message),
                                        notifData.getString(Constants.N_Link),
                                        notifData.getString(Constants.N_ImagePath),
                                        notifData.getString(Constants.N_Created_Datetime),
                                        "0", "N", VIDEOPATH, IS_IMG_VID, DEPT_NAME, REPLY, REPLY_URL});

                    Intent mainIntent = new Intent(SplashActivity.this, ICMActivity.class);
                    mainIntent.putExtra("notification", notifData.toString());
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);
                    finish();
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

                } else {
                    navigationActivities();
                }


            } catch (JSONException e) {
               /* Intent mainIntent = new Intent(SplashActivity.this, MAOHomeActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);*/
                navigationActivities();
            }


        } else {
            ICMUtils.clearNotifications(SplashActivity.this);
            /*Intent in = new Intent(SplashActivity.this, MAOHomeActivity.class);
            startActivity(in);
            finish();*/
            navigationActivities();
        }
    }

    private void navigationActivities() {
        if (dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.OFFICER_TYPE).equals("1")) {
            Intent mainIntent = new Intent(SplashActivity.this, MPEOHomeActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        } else if (dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.OFFICER_TYPE).equals("2")) {
            Intent mainIntent = new Intent(SplashActivity.this, MAOHomeActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        } else if (dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.OFFICER_TYPE).equals("3")) {
            Intent mainIntent = new Intent(SplashActivity.this, JD_ADHomeActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
    }


}
