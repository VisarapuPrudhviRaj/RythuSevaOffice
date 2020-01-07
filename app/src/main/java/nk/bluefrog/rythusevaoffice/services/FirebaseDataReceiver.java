package nk.bluefrog.rythusevaoffice.services;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import nk.bluefrog.rythusevaoffice.utils.Constants;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;
import nk.bluefrog.rythusevaoffice.utils.DBTables;


public class FirebaseDataReceiver extends WakefulBroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("BroadcastReceiver::", "BroadcastReceiver");
        notificationData(context, intent);
    }

    private void notificationData(Context context, Intent intent) {


        Bundle dataBundle = intent.getExtras();
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
                DBHelper dbHelper = new DBHelper(context);
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


                } else {

                }


            } catch (JSONException e) {
            }


        } else {

        }


    }
}