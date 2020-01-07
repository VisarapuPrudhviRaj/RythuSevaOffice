package nk.bluefrog.rythusevaoffice.activities.land;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import nk.bluefrog.library.BluefrogActivity;
import nk.bluefrog.library.utils.Helper;
import nk.bluefrog.library.utils.PrefManger;
import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.utils.Constants;


public class LandActivity extends BluefrogActivity {

    WebView webview;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webpage);
        setToolBar(getString(R.string.menu_my_land), "");
        findView();
    }

    private void findView() {
        webview = (WebView) findViewById(R.id.webview);
        progressBar =  findViewById(R.id.web_progress);
        webview.setWebViewClient(new MyWebViewClient());
        String url = Constants.LanWeb+PrefManger.getSharedPreferencesString(this,Constants.sp_mobile,"");
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setLoadsImagesAutomatically(true);
        webview.getSettings().setSupportZoom(false);
        webview.getSettings().setBuiltInZoomControls(false);
        webview.getSettings().setDisplayZoomControls(false);
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                setProgress(newProgress * 100);

                if(newProgress == 100){
                    progressBar.setVisibility(View.GONE);
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                }
            }


        });
        if(Helper.isNetworkAvailable(this)){
            webview.loadUrl(url); // load a web page in a web view
        }else{
            Helper.AlertMesg(this,getString(R.string.no_network));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
