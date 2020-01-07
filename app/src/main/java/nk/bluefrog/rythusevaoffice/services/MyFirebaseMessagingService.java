package nk.bluefrog.rythusevaoffice.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicInteger;

import nk.bluefrog.library.utils.Helper;
import nk.bluefrog.library.utils.PrefManger;
import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.activities.ICM.ICMActivity;
import nk.bluefrog.rythusevaoffice.activities.ICM.ICMUtils;
import nk.bluefrog.rythusevaoffice.utils.Constants;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;
import nk.bluefrog.rythusevaoffice.utils.DBTables;

import static android.content.ContentValues.TAG;
import static nk.bluefrog.rythusevaoffice.activities.ICM.ICMActivity.getInstance;
import static nk.bluefrog.rythusevaoffice.utils.Constants.NOTIFICATION_ID;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static AtomicInteger at = new AtomicInteger(0);
    NotificationCompat.Builder mBuilder;
    DBHelper dbhelper;
    private ICMUtils icmutils;


    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        System.out.println("onTokenRefresh");
        // Saving reg id to shared preferences
        storeRegIdInPref(token);

        // sending reg id to your server
        sendRegistrationToServer(token);

        // Notify UI that token has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Constants.REGISTRATION_COMPLETE);
        registrationComplete.putExtra(Constants.KEY_REG_ID, token);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);


    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Log.e(TAG, "sendRegistrationToServer: " + token);
    }

    private void storeRegIdInPref(String token) {
        System.out.println("storeRegIdInPref token:" + token);
        PrefManger.putSharedPreferencesString(this, Constants.KEY_REG_ID, token);
        Log.d("fcm_token", token);
    }

    public void handleIntent(Intent intent) {
        dbhelper = new DBHelper(this);
        Bundle dataBundle = intent.getExtras();
        if (dataBundle != null) {
            Log.e(TAG, "handleIntent");
            if (ICMUtils.isAppIsInBackground(getApplicationContext())) {
                //App Killed/Closed
                Log.e(TAG, "App Killed");
                JSONObject notifData = new JSONObject();
                for (String key : dataBundle.keySet()) {
                    try {
                        if (!TextUtils.isEmpty(dataBundle.getString(key))) {
                            try {
                                notifData.put(key, dataBundle.getString(key).toString().trim());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                    }
                }
                Log.e(TAG, "Json Object Killed" + notifData.toString());
                if (dataBundle.containsKey("STATUS") && dataBundle.getString("STATUS").equals("N")) {
                    handleDataMessage(notifData, true);
                } else {
                   /* handleNotification(dataBundle.getString("gcm.notification.body"),
                            dataBundle.getString("gcm.notification.title"));*/
                }

            }
        }
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "N======== From: " + remoteMessage.getFrom());
        Log.e(TAG, "N========  To: " + remoteMessage.getTo());
        Log.e(TAG, "N========  Notification: " + remoteMessage.getNotification().toString());
        Log.e(TAG, "N========  Body: " + remoteMessage.getNotification().getBody());
        Log.e(TAG, "N========  Title: " + remoteMessage.getNotification().getTitle());
        Log.e(TAG, "N========  Data: " + remoteMessage.getData().toString());


        dbhelper = new DBHelper(this);

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null && remoteMessage.getNotification().getBody() != null && remoteMessage.getNotification().getTitle() != null) {
            if (remoteMessage.getNotification().getBody().contains("STATUS"))
                handleNotification(remoteMessage.getNotification().getBody().toString(), remoteMessage.getNotification().getTitle().toString());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "N========  Data Payload: " + remoteMessage.getData().toString());
            handleDataMessage(new JSONObject(remoteMessage.getData()), true);
        }
    }

    private void handleNotification(String body, String title) {

        try {
            System.out.println(body);
            JSONObject jsonObject = new JSONObject(body);
            String STATUS = jsonObject.getString("STATUS");


            if (!icmutils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Constants.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", body);
                pushNotification.putExtra("title", title);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                // NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                // notificationUtils.playNotificationSound();

                ICMActivity notificationsActivity = getInstance();
                if (notificationsActivity == null) {
                    Intent resultIntent = new Intent(getApplicationContext(), ICMActivity.class);
                    String timeStamp = "", message = "", link = "", imagePath = "", ID = "";
                    try {
                        ID = jsonObject.getString(Constants.N_Message_Id);
                        title = jsonObject.getString(Constants.N_Title);
                        message = jsonObject.getString(Constants.N_Message);
                        link = jsonObject.getString(Constants.N_Link);
                        imagePath = jsonObject.getString(Constants.N_ImagePath);
                        timeStamp = jsonObject.getString(Constants.N_Created_Datetime);
                        String VIDEOPATH = jsonObject.has(Constants.N_VIDEOPATH) ? jsonObject.getString(Constants.N_VIDEOPATH) : "NA";
                        String IS_IMG_VID = jsonObject.has(Constants.N_IS_IMG_VID) ? jsonObject.getString(Constants.N_IS_IMG_VID) : "NA";
                        String DEPT_NAME = jsonObject.has(Constants.N_DEPT_NAME) ? jsonObject.getString(Constants.N_DEPT_NAME) : "";
                        String REPLY = jsonObject.has(Constants.N_REPLY) ? jsonObject.getString(Constants.N_REPLY) : "N";
                        String REPLY_URL = jsonObject.has(Constants.N_REPLY_URL) ? jsonObject.getString(Constants.N_REPLY_URL) : "";


                        if (dbhelper.getCountByValue(DBTables.ICM.TABLE_NAME, DBTables.ICM.ID, ID) == 0) {
                            dbhelper.insertintoTable(DBTables.ICM.TABLE_NAME, DBTables.ICM.cols,
                                    new String[]{ID, title,
                                            message,
                                            link,
                                            imagePath,
                                            timeStamp,
                                            "0", "N", VIDEOPATH, IS_IMG_VID, DEPT_NAME, REPLY, REPLY_URL});
                        }


                    } catch (Exception e) {
                        timeStamp = Helper.getTodayDateTime();
                        message = body;
                        dbhelper.insertintoTable(DBTables.ICM.TABLE_NAME, DBTables.ICM.cols,
                                new String[]{ID, title, body, link, imagePath, Helper.getTodayDateTime(), "0", "N",});

                    }
                    resultIntent.putExtra("notification", jsonObject.toString());
                    sendNotification(title, message + "\n\n" + link, timeStamp, resultIntent);
                }


            } else {
                // If the app is in background means app closed, firebase itself handles the notification
                //or
                //implrment

                ICMActivity notificationsActivity = getInstance();
                if (notificationsActivity == null) {
                    Intent resultIntent = new Intent(getApplicationContext(), ICMActivity.class);
                    String timeStamp = "", message = "", link = "", imagePath = "", ID = "";
                    try {
                        ID = jsonObject.getString(Constants.N_Message_Id);
                        title = jsonObject.getString(Constants.N_Title);
                        message = jsonObject.getString(Constants.N_Message);
                        link = jsonObject.getString(Constants.N_Link);
                        imagePath = jsonObject.getString(Constants.N_ImagePath);
                        timeStamp = jsonObject.getString(Constants.N_Created_Datetime);
                        String VIDEOPATH = jsonObject.has(Constants.N_VIDEOPATH) ? jsonObject.getString(Constants.N_VIDEOPATH) : "NA";
                        String IS_IMG_VID = jsonObject.has(Constants.N_IS_IMG_VID) ? jsonObject.getString(Constants.N_IS_IMG_VID) : "NA";
                        String DEPT_NAME = jsonObject.has(Constants.N_DEPT_NAME) ? jsonObject.getString(Constants.N_DEPT_NAME) : "";
                        String REPLY = jsonObject.has(Constants.N_REPLY) ? jsonObject.getString(Constants.N_REPLY) : "N";
                        String REPLY_URL = jsonObject.has(Constants.N_REPLY_URL) ? jsonObject.getString(Constants.N_REPLY_URL) : "";


                        if (dbhelper.getCountByValue(DBTables.ICM.TABLE_NAME, DBTables.ICM.ID, ID) == 0) {
                            dbhelper.insertintoTable(DBTables.ICM.TABLE_NAME, DBTables.ICM.cols,
                                    new String[]{ID, title,
                                            message,
                                            link,
                                            imagePath,
                                            timeStamp,
                                            "0", "N", VIDEOPATH, IS_IMG_VID, DEPT_NAME, REPLY, REPLY_URL});
                        }


                    } catch (Exception e) {
                        timeStamp = Helper.getTodayDateTime();
                        message = body;
                        dbhelper.insertintoTable(DBTables.ICM.TABLE_NAME, DBTables.ICM.cols,
                                new String[]{ID, title, body, link, imagePath, Helper.getTodayDateTime(), "0", "N",});

                    }
                    resultIntent.putExtra("notification", jsonObject.toString());
                    sendNotification(title, message + "\n\n" + link, timeStamp, resultIntent);
                }

            }

            Intent pushNotification = new Intent(Constants.PUSH_NOTIFICATION);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);


        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "N======== " + e.getMessage());
        }


    }


    private void handleDataMessage(JSONObject notifData, boolean notificationAppear) {
        try {
            String STATUS = notifData.getString("STATUS");
            String id = notifData.getString(Constants.N_Message_Id);
            String Title = notifData.getString(Constants.N_Title);
            String Message = notifData.getString(Constants.N_Message);
            String Link = notifData.getString(Constants.N_Link);
            String ImagePath = notifData.getString(Constants.N_ImagePath);
            String Created_Datetime = notifData.getString(Constants.N_Created_Datetime);
            String VIDEOPATH = notifData.has(Constants.N_VIDEOPATH) ? notifData.getString(Constants.N_VIDEOPATH) : "NA";
            String IS_IMG_VID = notifData.has(Constants.N_IS_IMG_VID) ? notifData.getString(Constants.N_IS_IMG_VID) : "NA";
            String DEPT_NAME = notifData.has(Constants.N_DEPT_NAME) ? notifData.getString(Constants.N_DEPT_NAME) : "";
            String REPLY = notifData.has(Constants.N_REPLY) ? notifData.getString(Constants.N_REPLY) : "N";
            String REPLY_URL = notifData.has(Constants.N_REPLY_URL) ? notifData.getString(Constants.N_REPLY_URL) : "";

            if (STATUS.trim().equals("N")) {

                if (!ICMUtils.isAppIsInBackground(getApplicationContext())) {

                    // app is in foreground, broadcast the push message
                    Intent pushNotification = new Intent(Constants.PUSH_NOTIFICATION);
                    pushNotification.putExtra("message", notifData.toString());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                    // play notification sound
                    //NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                    //notificationUtils.playNotificationSound();

                    ICMActivity notificationsActivity = getInstance();
                    if (notificationsActivity == null) {
                        Intent resultIntent = new Intent(getApplicationContext(), ICMActivity.class);

                        if (dbhelper.getCountByValues(DBTables.ICM.TABLE_NAME, new String[]{DBTables.ICM.ID},
                                new String[]{id}) == 0) {
                            //ID, TITLE, MESSAGE, LINK, IMAGEPATH, TIMESTAMP, IS_READ, TYPE,VIDEOPATH, IS_IMG_VID, DEPT_NAME, REPLY, REPLY_URL
                            dbhelper.insertintoTable(DBTables.ICM.TABLE_NAME, DBTables.ICM.cols,
                                    new String[]{id, Title,
                                            Message,
                                            Link,
                                            ImagePath,
                                            Created_Datetime,
                                            "0", "N", VIDEOPATH, IS_IMG_VID, DEPT_NAME, REPLY, REPLY_URL});
                        }


                        resultIntent.putExtra("notification", notifData.toString());


                        // check for image attachment
                        if (notificationAppear)
                            if (TextUtils.isEmpty(ImagePath) || ImagePath.equals("NA")) {
                                showNotificationMessage(getApplicationContext(), Title, Message + "\n" + Link, "", resultIntent);
                            } else {
                                // image is present, show notification with image
                                showNotificationMessageWithImage(getApplicationContext(), Title, Message + "\n" + Link, "", resultIntent, ImagePath);
                            }
                    }
                } else {
                    // app is in background, show the notification in notification tray
                    ICMActivity notificationsActivity = getInstance();
                    if (notificationsActivity == null) {

                        Intent resultIntent = new Intent(getApplicationContext(), ICMActivity.class);

                        if (dbhelper.getCountByValues(DBTables.ICM.TABLE_NAME, new String[]{DBTables.ICM.ID},
                                new String[]{id}) == 0) {
                            //ID, TITLE, MESSAGE, LINK, IMAGEPATH, TIMESTAMP, IS_READ, TYPE,VIDEOPATH, IS_IMG_VID, DEPT_NAME, REPLY, REPLY_URL
                            dbhelper.insertintoTable(DBTables.ICM.TABLE_NAME, DBTables.ICM.cols,
                                    new String[]{id, Title,
                                            Message,
                                            Link,
                                            ImagePath,
                                            Created_Datetime,
                                            "0", "N", VIDEOPATH, IS_IMG_VID, DEPT_NAME, REPLY, REPLY_URL});
                        }


                        resultIntent.putExtra("notification", notifData.toString());


                        // check for image attachment
                        if (notificationAppear)
                            if (TextUtils.isEmpty(ImagePath)) {
                                System.out.println("N====== Killed 2");
                                showNotificationMessage(getApplicationContext(), Title, Message + "\n" + Link, "", resultIntent);
                            } else {
                                System.out.println("N====== Killed 3");
                                // image is present, show notification with image
                                showNotificationMessageWithImage(getApplicationContext(), Title, Message + "\n" + Link, "", resultIntent, ImagePath);
                            }
                    }

                    Intent pushNotification = new Intent(Constants.PUSH_NOTIFICATION);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                }

            }


        } catch (JSONException e) {
            Log.e(TAG, "N======Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "N======Exception: " + e.getMessage());
        }
    }


    private void sendNotification(String title, String message, String timeStamp, Intent intent) {
        // define sound URI, the sound to be played when there's a notification
        Uri soundUri = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManager mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        int requestID = (int) System.currentTimeMillis();

        PendingIntent contentIntent = PendingIntent.getActivity(this,
                requestID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(title);


        mBuilder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(message));
        mBuilder.setContentText(message);


        // Set Vibrate, Sound and Light
        int defaults = 0;
        defaults = defaults | Notification.DEFAULT_LIGHTS;
        defaults = defaults | Notification.DEFAULT_VIBRATE;
        defaults = defaults | Notification.DEFAULT_SOUND;

        mBuilder.setDefaults(defaults);
        mBuilder.setSound(soundUri);
        // Set the content for Notification
        // mBuilder.setContentText("New message from Server");
        // Set autocancel
        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }


    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        icmutils = new ICMUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        icmutils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        icmutils = new ICMUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        icmutils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}
