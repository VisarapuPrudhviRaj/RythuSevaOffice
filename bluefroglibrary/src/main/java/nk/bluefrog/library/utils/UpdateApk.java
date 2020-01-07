package nk.bluefrog.library.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import nk.bluefrog.library.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nagendra on 5/7/18.
 */

public class UpdateApk extends AsyncTask<String, String, JSONObject> {

    private String latestVersion;
    private String currentVersion;
    private String appName;
    private Context context;

    public UpdateApk(String currentVersion, String appName, Context context) {
        this.currentVersion = currentVersion;
        this.appName = appName;
        this.context = context;
    }

    @Override
    protected JSONObject doInBackground(String... params) {

        try {
            String html = getHtml("https://play.google.com/store/apps/details?id=" + context.getPackageName() + "&hl=en");
            if (html.contains("Current Version")) {
                html = html.split("Current Version")[1].substring(50, 100);
//                Pattern pattern = Pattern.compile("(\\d+\\.\\d+\\.\\d+|\\d)");
                Pattern pattern = Pattern.compile("(\\d+\\.\\d+\\.\\d+|\\d+\\.\\d+|\\d+)");
                Matcher matcher = pattern.matcher(html);
                matcher.find();
                latestVersion = matcher.group(0);
                Log.d("VersionAvailable", latestVersion);
                Log.d("VersionActual", currentVersion);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject();
        }
        return new JSONObject();
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {

        if (latestVersion != null) {
            if (!currentVersion.equalsIgnoreCase(latestVersion)) {
                if (!((Activity) context).isFinishing()) {
                    showForceUpdateDialog();
                }
            } else {

                PrefManger.putSharedPreferencesString(context, "DATE", Helper.getTodayDate());
            }
        }
        super.onPostExecute(jsonObject);
    }

    @SuppressLint("StringFormatMatches")
    public void showForceUpdateDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(context.getString(R.string.youAreNotUpdatedTitle));
        String text = context.getResources().getString(R.string.youAreNotUpdatedMessage, appName, latestVersion);
        alertDialogBuilder.setMessage(text);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                try {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName())));
                } catch (android.content.ActivityNotFoundException anfe) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id" + context.getPackageName())));
                }
                dialog.cancel();
            }
        });
        alertDialogBuilder.show();
    }

    String getHtml(String url1) {
        String str = "";
        try {
            URL url = new URL(url1);
            URLConnection spoof = url.openConnection();
            spoof.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0; H010818)");
            BufferedReader in = new BufferedReader(new InputStreamReader(spoof.getInputStream()));
            String strLine = "";
            // Loop through every line in the source
            while ((strLine = in.readLine()) != null) {
                str = str + strLine;
            }
        } catch (Exception e) {
            str = "";
        }
        return str;
    }
}
