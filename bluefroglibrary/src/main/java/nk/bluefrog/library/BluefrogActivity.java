package nk.bluefrog.library;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import nk.bluefrog.library.utils.Helper;
import nk.bluefrog.library.utils.PermissionResult;
import nk.bluefrog.library.utils.PrefManger;
import nk.bluefrog.library.utils.UnCaughtException;
import nk.bluefrog.library.utils.UpdateApk;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Created by nagendra on 22/3/17.
 */

public class BluefrogActivity extends AppCompatActivity {

    private final int KEY_PERMISSION = 999;
    private final String KEY_DATE = "DATE";


    public ProgressDialog pd;
    private PermissionResult permissionResult;
    private String permissionsAsk[];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new
                UnCaughtException(BluefrogActivity.this, getString(R.string.app_name)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            changeLanguage(PrefManger.getSharedPreferencesString(this,"lang",""),this);
        }

    }

    public static void changeLanguage(String languageToLoad, Context context) {
        PrefManger.putSharedPreferencesString(context, "lang", languageToLoad);
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

    @Override
    protected void onResume() {
        super.onResume();
        forceUpdate();
    }


    public void forceUpdate() {
        PackageManager packageManager = this.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String currentVersion = packageInfo.versionName;
        if (Helper.isNetworkAvailable(this) && !PrefManger.getSharedPreferencesString(this, KEY_DATE, "").trim().equals(Helper.getTodayDate()))
            new UpdateApk(currentVersion, getString(R.string.app_name), BluefrogActivity.this).execute();
    }


    public void setToolBar(String titleName, String subtitle) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(titleName);
        toolbar.setSubtitle(subtitle);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    public void setToolBar(Context context, String titleName, String subtitle) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(titleName);
        toolbar.setSubtitle(subtitle);
        toolbar.setTitleTextColor(context.getResources().getColor(R.color.common_white));
        toolbar.setSubtitleTextColor(context.getResources().getColor(R.color.common_white));
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    public void setToolBarWithID(String titleName, String subtitle, int id) {
        Toolbar toolbar = (Toolbar) findViewById(id);
        toolbar.setTitle(titleName);
        toolbar.setSubtitle(subtitle);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setToolBar(String titleName, String subtitle, int icon) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(titleName);
        toolbar.setSubtitle(subtitle);
        toolbar.setNavigationIcon(icon);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    public boolean isPermissionGranted(Context context, String permission) {
        return ((Build.VERSION.SDK_INT < Build.VERSION_CODES.M) || (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED));
    }


    public boolean isPermissionsGranted(Context context, String permissions[]) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;

        boolean granted = true;
        for (String permission : permissions) {
            if (!(ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED))
                granted = false;
        }
        return granted;
    }

    private void internalRequestPermission(String[] permissionAsk) {
        String arrayPermissionNotGranted[];
        ArrayList<String> permissionsNotGranted = new ArrayList<>();

        System.out.println("Total:" + permissionAsk.length);
        for (int i = 0; i < permissionAsk.length; i++) {
            System.out.println("ASking:" + permissionAsk[i].toString());
            if (!isPermissionGranted(BluefrogActivity.this, permissionAsk[i])) {
                System.out.println(i + ": Not Granted");
                permissionsNotGranted.add(permissionAsk[i]);
            }
        }

        if (permissionsNotGranted.isEmpty()) {
            if (permissionResult != null)
                permissionResult.permissionGranted();
        } else {
            arrayPermissionNotGranted = new String[permissionsNotGranted.size()];
            arrayPermissionNotGranted = permissionsNotGranted.toArray(arrayPermissionNotGranted);
            ActivityCompat.requestPermissions(BluefrogActivity.this, arrayPermissionNotGranted, KEY_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != KEY_PERMISSION) {
            return;
        }

        List<String> permissionDenied = new LinkedList<>();
        boolean granted = true;
        System.out.println("Total grantResults:" + grantResults.length);
        for (int i = 0; i < grantResults.length; i++) {
            if (!(grantResults[i] == PackageManager.PERMISSION_GRANTED)) {
                System.out.println(i + ":Denied :" + grantResults[i] + ":" + permissions[i]);
                granted = false;
                permissionDenied.add(permissions[i]);
            }
        }

        if (permissionResult != null) {
            if (granted) {
                permissionResult.permissionGranted();
            } else {
                for (String s : permissionDenied) {
                    try {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, s)) {
                            permissionResult.permissionForeverDenied();
                            return;
                        }
                    } catch (Exception e) {
                        Toast.makeText(BluefrogActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        permissionResult.permissionDenied();
                    }

                }
                permissionResult.permissionDenied();
            }
        }
    }

    public void askCompactPermission(String permission, PermissionResult permissionResult) {
        permissionsAsk = new String[]{permission};
        this.permissionResult = permissionResult;
        internalRequestPermission(permissionsAsk);
    }

    public void askCompactPermissions(String permissions[], PermissionResult permissionResult) {
        permissionsAsk = permissions;
        this.permissionResult = permissionResult;
        internalRequestPermission(permissionsAsk);
    }


    public void showApplicationDialog() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(BluefrogActivity.this);
        builder.setTitle(R.string.attention);
        builder.setMessage(R.string.messageperm);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                openSettingsApp(BluefrogActivity.this);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    public void openSettingsApp(Context context) {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    public void showProgressDialog(String msg) {
        try {
            pd = new ProgressDialog(this);
            // pd = CustomProgressDialog.ctor(this, msg);
           // pd.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            pd.setMessage(msg);
            pd.setCancelable(true);
            pd.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void closeProgressDialog() {
        try {
            if (pd != null)
                pd.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
