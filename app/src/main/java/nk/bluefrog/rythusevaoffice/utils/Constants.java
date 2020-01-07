package nk.bluefrog.rythusevaoffice.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import nk.bluefrog.library.network.RestServiceWithVolle;
import nk.bluefrog.library.utils.Helper;
import nk.bluefrog.library.utils.PrefManger;
import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.activities.home.JD_ADHomeActivity;
import nk.bluefrog.rythusevaoffice.activities.home.MAOHomeActivity;
import nk.bluefrog.rythusevaoffice.activities.home.MPEOHomeActivity;
import nk.mobileapps.progresslib.DottedProgress;

import static nk.bluefrog.library.network.ResponseListener.POST;


/**
 * Created by nagendra on 6/10/17.
 */

public class Constants {


    public static final String sp_lang = "lang";
    public static final String KEY_REG_ID = "DeviceId";
    public static final String KEY_Mobile_No = "Mobile_No";
    public static final String KEY_Device_Id = "Device_Id";
    //public static DottedProgress dottedProgress = null;
    public static final String Type_FarmerSeeds = "1";
    public static final String Type_SeedShops = "2";
    public static final String Type_Fertilizers = "3";
    public static final String Type_LiveStock = "4";
    public static final String Type_DungUrine = "5";
    public static final String Type_Hospitals = "6";
    public static final String Type_Nursery = "7";
    public static final String Type_Fooder = "8";
    public static final String Type_VermiData = "9";
    public static final String Type_FruitsnVegetables = "10";
    public static final String Type_Equipments = "11";
    public static final String Type_Organizations = "12";
    public static final String Type_Help = "13";
    public static final String Type_ZBNF = "14";
    public static final String TOPIC_GLOBAL = "global";
    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;
    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";
    public static final String PUSH_NOTIFICATION_REC = "pushNotificationRec";
    public static final String SHARED_PREF = "nk_firebase";
    //    public static final String sp_token = "token";
    public static final String sp_mobile = "mobile";
    public static final String sp_name = "name";
    public static final String sp_category_Id = "category_Id";
    public static final String sp_equipment_Id = "equipment_Id";
    public static final String sp_type = "sp_type";
    public static final String sp_subType = "sp_subType";
    public static final String sp_shop_farmer = "sp_shop_farmer";
    public static final String sp_gps = "sp_gps";
    public static final String sp_dist = "dist";
    public static final String sp_prize = "prize";
    public static final String sp_otp = "otp";
    public static final String sp_otpverify = "otpverify";
    public static final String sp_farmername = "farmername";
    public static final String sp_aadhar = "aadhar";
    public static final String sp_userid = "userid";
    public static final String sp_helpStr = "helpStr";
    public static final String sp_onetimeupdate = "onetimeupdate";
    public static final String sp_weather_date = "weather_date";
    public static final String sp_weather_temp = "weather_temp";
    public static final String sp_weather_rain = "weather_rain";
    public static final String sp_weather_wind = "weather_wind";
    public static final String sp_mandal_name_by_id = "mandal_name";
    public static final String sp_farmerinfo = "sp_farmerinfo";
    public static final String sp_farmerinfo_installed = "sp_farmerinfo_installed";
    public static final String sp_farmerinfo_equip = "sp_farmerinfo_equip";
    public static final String BR_ANN_RECEIVED = "com.broadcast.announcement";
    public static final String N_Message_Id = "Message_Id";
    public static final String N_Title = "Title";
    public static final String N_Message = "Message";
    public static final String N_Link = "Link";
    public static final String N_ImagePath = "ImagePath";
    public static final String N_Created_Datetime = "Created_Datetime";
    public static final String N_STATUS = "STATUS";
    public static final String villageID = "";
    public static final String N_VIDEOPATH = "VIDEOPATH";
    public static final String N_IS_IMG_VID = "IS_IMG_VID";//I,V,NA
    public static final String N_DEPT_NAME = "DEPT_NAME";
    public static final String N_REPLY = "REPLY";
    public static final String N_REPLY_URL = "REPLY_URL";
    public static String KEY_IS_LOGIN = "isLogin";


    public static String BASE_URL = "http://103.210.74.213/RythusevaAPI/api/Foss/";
    public static String URL_FARMER_COMM = "http://103.210.74.213/RythuMithra/TMServices/FarmerNotifications.asmx";
    public static String URL_FARMER_PROFILE = "http://103.210.74.213/RythusevaMWeb/FarmerProfile.aspx?AID=";
    //OLD
    public static String url_weather = "http://apaims.vassarlabs.com/api/forecast/bf/weather/data/";
    public static String url_cropInsurance = "http://103.210.74.213/RythusevaMWeb/Insurance/CropDetails.aspx?";


    public static String myPostWebUrl = "http://103.210.74.213/RythusevaMWeb/RythusevaPost/RythusevaPost.aspx?";
    public static String generalWebUrl = "http://103.210.74.213/RythusevaMWeb/RythusevaPost/MyClusterPosts.aspx?";
    public static String LanWeb = "http://103.210.74.213/clic_rythuseva/land.aspx?MN=";
    public static String cropWeb = "http://103.210.74.213/clic_rythuseva/crop.aspx?MN=";
    public static String METHOD_CROP_MASTER = "GetAllMasters";
    public static String METHOD_OFFICER_LOGIN = "OfficerLogin";
    public static String METHOD_GET_FERTILIZERS = "GetFertilizerDetails";
    public static String METHOD_GET_NURSERY = "GetNurseryDetailsBasedOnDistance";

    public static String METHOD_GET_SEEDS = "GetNearestFarmersSeeds";
    public static String METHOD_GET_SHOPS = "GetNearestSeedShops";
    public static String METHOD_INSERT_SEEDS = "InsertDetailsSeed";
    public static String METHOD_SCHEDULE_MEETING = "ScheduleMeeting";
    public static String METHOD_FARMER_COMM = "GetNotifyFarmers";
    public static String METHOD_MEETING_REVIEW = "InsertMeetingReview";
    public static String METHOD_EQUIPMENT_CATEGORIES = "GetCategoryEquipmentDetails";
    public static String METHOD_EQUIPMENT_DETAILS_DISTANCE = "GetEquipmentDetailsBasedOnDistance";
    public static String METHOD_EQUIPMENT_DETAILS_DISTANCE_NEW = "GetEquipmentDetailsBasedOnDistanceNew";

    public static String METHOD_EQUIPMENT_REGISTRATION = "InsertEquipmentRegDetails";
    public static DottedProgress dottedProgress = null;
    static String TAG = "Constants";

    public static void applyFont(final Context context, final View root, final String fontName) {
        try {
            if (root instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) root;
                for (int i = 0; i < viewGroup.getChildCount(); i++)
                    applyFont(context, viewGroup.getChildAt(i), fontName);
            } else if (root instanceof TextView)
                ((TextView) root).setTypeface(Typeface.createFromAsset(context.getAssets(), fontName));
        } catch (Exception e) {
            Log.e(TAG, String.format("Error occured when trying to apply %s font for %s view", fontName, root));
            e.printStackTrace();
        }
    }

    public static void changeLanguage(String languageToLoad, Context context) {
        PrefManger.putSharedPreferencesString(context, sp_lang, languageToLoad);
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        Resources res = context.getResources();
        Configuration configuration = new Configuration(res.getConfiguration());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
            // context.createConfigurationContext(configuration);
        } else
            configuration.locale = locale;

        res.updateConfiguration(configuration, res.getDisplayMetrics());

    }


    public static void navigationHomeScreen(DBHelper dbHelper, Activity context) {

        if (dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.OFFICER_TYPE).equals("1")) {
            Intent mainIntent = new Intent(context, MPEOHomeActivity.class);
            context.startActivity(mainIntent);
            context.finish();
        } else if (dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.OFFICER_TYPE).equals("2")) {
            Intent mainIntent = new Intent(context, MAOHomeActivity.class);
            context.startActivity(mainIntent);
            context.finish();
        } else if (dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.OFFICER_TYPE).equals("3")) {
            Intent mainIntent = new Intent(context, JD_ADHomeActivity.class);
            context.startActivity(mainIntent);
            context.finish();
        }
    }

    public static void getAddressFromLocation(final double latitude, final double longitude, final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                            sb.append(address.getAddressLine(i)); //.append("\n");
                        }
                        sb.append(address.getLocality()).append("\n");
                        // sb.append(address.getPostalCode()).append("\n");
                        // sb.append(address.getCountryName());
                        result = sb.toString();
                    }
                } catch (IOException e) {
                    Log.e("Location Address Loader", "Unable connect to Geocoder", e);
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (result != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("address", result);
                        message.setData(bundle);
                    } else {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = "Location Search Failed Try Again";
                        bundle.putString("address", result);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }


    public static String getTodayDate() {
        Date currentdate = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
        return sdf.format(currentdate);
    }

    public static String getFurtureDate(Context context, int days) {
        Date currentdate = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
        String dt = sdf.format(currentdate);
        Log.e("Current date", "getdays: " + dt);

        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(dt));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, days);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);

        String output = sdf1.format(c.getTime());
        Log.e("", "getdays: " + output);
        return output;

    }

    public static void call(DBHelper dbHelper, String pnumber, Activity context, String Component_type, String Consumer_AadharID) {
        //, , String Latitude, String Longitude


        Intent iCall = new Intent(Intent.ACTION_CALL);
        iCall.setData(Uri.parse("tel:" + pnumber));
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        context.startActivity(iCall);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ComponentType", Component_type);
            jsonObject.put("User_Id", dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.UNIQUE_ID));
            jsonObject.put("User_Type", "O");
            jsonObject.put("MobileNo", dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.MOBILE));
            jsonObject.put("OfficerType", dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.OFFICER_TYPE));
            jsonObject.put("IMEI", Helper.getIMEINumber(context));
            jsonObject.put("Version", context.getString(R.string.version));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RestServiceWithVolle callApi = new RestServiceWithVolle(context, null, 0,
                Constants.BASE_URL + "Component_CallHits",
                POST, false);
        callApi.loadRequest(jsonObject);


    }

    public static DottedProgress showDottedProgress(Context context, String msg) {
        if (context == null) {
            return null;
        }
        dottedProgress = new DottedProgress(context, R.style.DottedProgressDefault);
        dottedProgress.show();
        dottedProgress.setCancelable(false);
        dottedProgress.setMessage(msg);
        dottedProgress.show();


        return dottedProgress;
    }

    public static void closeDottedProgress() {
        if (dottedProgress != null && dottedProgress.isShowing()) {
            dottedProgress.dismiss();
        }

    }

    public static void loadImageElseWhite(final Context context, final String image, final ImageView imageView) {

        try {
            if (image != null && image.length() > 0) {

                Glide.with(context).load(image).
                        listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                imageView.setImageResource(R.drawable.tryagain);
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        loadImageElseWhite(context, image, imageView);
                                    }
                                });
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                imageView.setEnabled(false);
                                return false;
                            }
                        }).thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.tryagain)
                        .into(imageView);
            } else {
                Glide.with(context)
                        .load("")
                        .placeholder(R.drawable.tryagain)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(imageView);
            }
        } catch (IllegalArgumentException e) {
            Glide.with(context)
                    .load("")
                    .placeholder(R.drawable.tryagain)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(imageView);
        }

    }

    public static String encryptAadhaar(String aadhaar) {

        String returnValue = aadhaar;

        if (!aadhaar.contains("*")) {

            returnValue = aadhaar.substring(0, 4) + "******" + aadhaar.substring(10, 12);

        }
        return returnValue;

    }


    public static void loadImageElseWhite(Context context, String image, ImageView imageView, final ProgressBar progress) {
        System.out.println("image:" + image);


        try {
            if ((image != null) && image.length() > 0) {

                if (!image.contentEquals("NA")) {

                    Glide.with(context).load(image).
                            listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    progress.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    progress.setVisibility(View.GONE);
                                    return false;
                                }
                            }).thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL).error(R.mipmap.ic_launcher)
                            .into(imageView);
                } else {
                    progress.setVisibility(View.GONE);
                }
            } else {
                //progress.setVisibility(View.GONE);
                Glide.with(context)
                        .load("")
                        .placeholder(R.mipmap.ic_launcher)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(imageView);
            }
        } catch (IllegalArgumentException e) {

        }

    }


}
