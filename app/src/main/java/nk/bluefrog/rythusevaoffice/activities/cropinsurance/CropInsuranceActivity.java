package nk.bluefrog.rythusevaoffice.activities.cropinsurance;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import nk.bluefrog.library.BluefrogActivity;
import nk.bluefrog.library.utils.PrefManger;
import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.activities.home.MPEOHomeActivity;
import nk.bluefrog.rythusevaoffice.utils.Constants;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;
import nk.bluefrog.rythusevaoffice.utils.DBTables;

public class CropInsuranceActivity extends BluefrogActivity {

    DBHelper dbHelper;
    private WebView webView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_insurance);
        setToolBar(getString(R.string.menu_near_cropinsurance), "");
        dbHelper = new DBHelper(this);
        findview();
    }

    public void setToolBar(String titleName, String subtitle) {
        Toolbar toolbar = (Toolbar) findViewById(nk.bluefrog.library.R.id.toolbar);
        toolbar.setTitle(titleName);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setSubtitle(subtitle);
        toolbar.setNavigationIcon(nk.bluefrog.library.R.drawable.ic_back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView != null && webView.canGoBack()) {
                        webView.goBack();
                    }  else {
                        gotoMainScreen();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        }  else {
            gotoMainScreen();
        }
    }

    private void gotoMainScreen() {
        Constants.navigationHomeScreen(dbHelper,CropInsuranceActivity.this);
        /*Intent intent = new Intent(CropInsuranceActivity.this, MPEOHomeActivity.class);
        startActivity(intent);
        finish();*/
    }


    private void findview() {
        webView = (WebView) findViewById(R.id.webview);
        setJavaScriptPropertiesNew(webView);
        loadServerPage();
    }

    private void loadServerPage() {
        String phone = dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.MOBILE);
        String lang = PrefManger.getSharedPreferencesString(CropInsuranceActivity.this, Constants.sp_lang, "");
//43.254.41.246/RythuMithra/Insurance/CropDetails.aspx?MN=6301228025&lan=E
        webView.loadUrl(Constants.url_cropInsurance + "MN=" + phone + "&lan=" + lang.substring(0, 1).toUpperCase());
    }

    private void setJavaScriptPropertiesNew(WebView wv) {
        wv.getSettings().setJavaScriptEnabled(true);
        wv.addJavascriptInterface(new WebAppInterface(this),
                "RythuSeva");
        wv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.setScrollbarFadingEnabled(false);
        wv.getSettings().setBuiltInZoomControls(false);
        wv.getSettings().setPluginState(WebSettings.PluginState.ON);
        wv.getSettings().setAllowFileAccess(true);
        wv.getSettings().setSupportZoom(false);
        wv.getSettings().setRenderPriority(
                WebSettings.RenderPriority.HIGH);

        if (!isConnected(this))
            wv.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ONLY);
        wv.getSettings().setAppCacheEnabled(true);
        WebSettings websettings = wv.getSettings();
        websettings.setDomStorageEnabled(true);
        websettings.setDefaultTextEncodingName("UTF-8");

        // webview.getSettings().setBuiltInZoomControls(true);
    }

    public boolean isConnected(Context context) {
        if (context == null)
            return false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();//isConnectedOrConnecting();

    }

    public class WebAppInterface {

        Context context;

        public WebAppInterface(Context context) {
            this.context = context;

        }

        @JavascriptInterface
        public void showReloadPage(String toast) {
            Toast.makeText(CropInsuranceActivity.this, toast + " Reloading page...", Toast.LENGTH_SHORT).show();
            loadServerPage();
        }
    }


}
