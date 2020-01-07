package nk.bluefrog.library.gps;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import nk.bluefrog.library.BluefrogActivity;
import nk.bluefrog.library.R;
import nk.bluefrog.library.utils.Helper;
import nk.bluefrog.library.utils.PermissionResult;


public class GPSActivity extends BluefrogActivity {

    public static final String LOC_DATA = "gps_data";
    public static final String LOC_ALTITUDE = "gps_altitude";
    public static final String LOC_ACCURACY = "gps_accuracy";
    public static final String LOC_TIME_IN_MILLIS = "gps_time";
    public static final String MAX_ACCURACY = "max_accuracy";
    public static final String MAX_TIME_IN_MILLIS = "max_time";
    public static final String PROVIDER = "PROVIDER";

    public static final String GPS_PROVIDER = LocationManager.GPS_PROVIDER;
    public static final String NETWORK_PROVIDER = LocationManager.NETWORK_PROVIDER;
    public int accuracy = 50;
    public long maxTime = 30000;
    public long time = 0;
    private String providerType = LocationManager.GPS_PROVIDER;
    private String gpsData = "";
    private LocationManager locationManager = null;
    private MyLocationListener mlistener;
    private ProgressBar pbIndicator;
    private TextView tvCountDown;

    private String delimeter = "-";
    private String acuuracy = "";
    private String altitude = "";
    private CountDownTimer countDownTimer;

    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_gps_helper);
        this.setFinishOnTouchOutside(false);

        Intent gpsInten = getIntent();

        if (gpsInten != null) {

            accuracy = gpsInten.getIntExtra(MAX_ACCURACY, accuracy);
            maxTime = gpsInten.getLongExtra(MAX_TIME_IN_MILLIS, maxTime);
            //
            if (gpsInten.hasExtra(PROVIDER))
                providerType = gpsInten.getStringExtra(PROVIDER);
        }

        initView();

        askCompactPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, new PermissionResult() {
            @Override
            public void permissionGranted() {
//                providerType = LocationManager.NETWORK_PROVIDER;
                startSearchForGps();
            }

            @Override
            public void permissionDenied() {
                finish();
            }

            @Override
            public void permissionForeverDenied() {
                showApplicationDialog();
            }
        });
    }

    private void initView() {
        pbIndicator = (ProgressBar) findViewById(R.id.pbIndicator);
        tvCountDown = (TextView) findViewById(R.id.tvCountDown);
    }

    private void initialiseTimer(long totalTime, long interval) {
        countDownTimer = new CountDownTimer(totalTime, interval) {

            public void onTick(long millisUntilFinished) {
                tvCountDown.setText("Using " + providerType.toUpperCase() + " Countdown : " + (millisUntilFinished / 1000));
            }

            public void onFinish() {
                if (TextUtils.isEmpty(gpsData)) {
                    if (isFirst) {
                        isFirst = false;
                        if (mlistener != null)
                            locationManager.removeUpdates(mlistener);

                        if (isSimSupport()) {
                            providerType = LocationManager.NETWORK_PROVIDER;
                            startSearchForGps();
                        } else {
                            Helper.showToast(GPSActivity.this, "Sim card is not available to use network provider");
                            setFailResult();
                        }
                    } else {
                        Helper.showToast(GPSActivity.this, getString(R.string.unable_to_get_location));
                        setFailResult();
                    }

                }
            }

        }.start();
    }

    private void setSuccessResult() {
        Intent intent = new Intent();
        intent.putExtra(LOC_DATA, gpsData);
        intent.putExtra(LOC_ACCURACY, acuuracy);
        intent.putExtra(LOC_ALTITUDE, altitude);
        intent.putExtra(LOC_TIME_IN_MILLIS, time);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void setFailResult() {
        setResult(RESULT_CANCELED, null);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void startSearchForGps() {
        if (isGPSON()) {
            gpsFinding();
        } else {
            confirmGPS();
        }
    }

    @Override
    public void onBackPressed() {
    }

    public void gpsFinding() {
        initialiseTimer(maxTime, 1000);
        turnGPSOn();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mlistener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(providerType, 500, 0, mlistener);
    }

    public boolean isSimSupport() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);  //gets the current TelephonyManager
        return !(tm.getSimState() == TelephonyManager.SIM_STATE_ABSENT);

    }


    private void turnGPSOn() {

        String provider = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (!provider.contains("gps")) { // if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings",
                    "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
    }

    private void turnGPSOff() {
        String provider = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if (provider.contains("gps")) { // if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings",
                    "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }

    }

    public void confirmGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.enable_location));
        builder.setPositiveButton(getString(R.string.enable), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Helper.showToast(GPSActivity.this, getString(R.string.toast_enable_gps));
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, 1234);
            }
        });
        builder.setCancelable(false);
        builder.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1234) {
            if (resultCode == RESULT_CANCELED) {
                if (isGPSON()) {
                    gpsFinding();
                } else {
                    confirmGPS();
                }

            }
        }
    }

    public boolean isGPSON() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location location) {

            try {
                if (location != null) {
                      boolean isMock = (Build.VERSION.SDK_INT >= 18 && location.isFromMockProvider());
                    //  if (isMock) {
                    //      Helper.showToast(GPSActivity.this, "Fake GPS Point Will Not Accept.\nPlease Stop Fake GPS Application ");
                    //      setFailResult();
                    //      finish();
                    //  } else {
                    //                    if (location.getAccuracy() <= accuracy) {

                    if (ActivityCompat.checkSelfPermission(GPSActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(GPSActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    locationManager.removeUpdates(mlistener);
                    acuuracy = "" + location.getAccuracy();
                    altitude = "" + location.getAltitude();
                    gpsData = location.getLatitude() + delimeter + location.getLongitude();
                    time = location.getTime();

                    turnGPSOff();
                    countDownTimer.cancel();
                    setSuccessResult();
                    finish();
//                    }
                    // }


                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

    }

}
