package nk.bluefrog.rythusevaoffice.activities.agriforum;
/*{"crop_id":"01","category":"0","cultivation_type":"0","question":"My Question .......","link_type":"0(image)/1(video)","link_url":"","image":""}*/

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.silvestrpredko.dotprogressbar.DotProgressBar;

import de.hdodenhof.circleimageview.CircleImageView;
import nk.bluefrog.library.BluefrogActivity;
import nk.bluefrog.library.utils.Helper;
import nk.bluefrog.library.utils.PrefManger;
import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.activities.home.JD_ADHomeActivity;
import nk.bluefrog.rythusevaoffice.activities.home.MAOHomeActivity;
import nk.bluefrog.rythusevaoffice.activities.home.MPEOHomeActivity;
import nk.bluefrog.rythusevaoffice.utils.Constants;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;
import nk.bluefrog.rythusevaoffice.utils.DBTables;


public class ForumActivity extends BluefrogActivity {

    DBHelper dbHelper;

    CircleImageView iv_mainupload;
    FloatingActionButton fab;
    ProgressBar progressBarMain;
    Button btn_otherPost, btn_myPost;
    private DotProgressBar dtd_progress = null;
    private WebView webview_openbusiness;
    private WebView currentTopExpandWV = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);
        setToolBar(getString(R.string.menu_agri_forum), "");
        dbHelper = new DBHelper(this);
        findViews();
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
                if (webview_openbusiness != null && webview_openbusiness.canGoBack()) {
//                    webview_openbusiness.goBack();
                    gotoMainScreen();
                } else if (currentTopExpandWV != null && currentTopExpandWV.canGoBack()) {
//                    currentTopExpandWV.goBack();
                    gotoMainScreen();
                } else {
                    gotoMainScreen();
                }
            }
        });
    }

    private void findViews() {
        dtd_progress = (DotProgressBar) findViewById(R.id.webview_progress);
        webview_openbusiness = findViewById(R.id.webview_openbusiness);
        webview_openbusiness.setBackgroundColor(Color.TRANSPARENT);
        currentTopExpandWV = webview_openbusiness;
        setJavaScriptPropertiesNew(webview_openbusiness);

        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(" ");
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(getString(R.string.menu_agri_forum));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });

        fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForumActivity.this, ForumPostActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_otherPost = findViewById(R.id.btn_otherPost);
        btn_myPost = findViewById(R.id.btn_myPost);

        //iv_mainupload = (CircleImageView) collapsingToolbarLayout.findViewById(R.id.iv_mainupload);
        //progressBarMain = (ProgressBar) collapsingToolbarLayout.findViewById(R.id.progressBarMain);


        //String adhar = dbHelper.getTabCol(DBTables.Register.TABLE_NAME, DBTables.Register.user_aadharid);

        ((TextView) findViewById(R.id.tv_username)).setText(PrefManger.getSharedPreferencesString(this, Constants.sp_name, ""));
        ((TextView) findViewById(R.id.tv_mobile)).setText(PrefManger.getSharedPreferencesString(this, Constants.sp_mobile, ""));

        /*((TextView) findViewById(R.id.tv_im)).setText(getResources().getStringArray(R.array.user_types)
                [Integer.parseInt(dbHelper.getTabCol(DBTables.Register.TABLE_NAME, DBTables.Register.user_type)) - 1]);

        ((TextView) findViewById(R.id.tv_phone)).setText(dbHelper.getTabCol(DBTables.Register.TABLE_NAME, DBTables.Register.user_phone));

        loadImageElseWhite(dbHelper.getTabCol(DBTables.Register.TABLE_NAME, DBTables.Register.gender_Code).toString().trim(), iv_mainupload, progressBarMain);
*/
        webViewFilechooser(webview_openbusiness);
        currentTopExpandWV = webview_openbusiness;

        btn_otherPost.setBackgroundColor(getResources().getColor(R.color.white));
        btn_myPost.setBackgroundColor(getResources().getColor(R.color.colorAccent));

        loadServerPage();

    }


    public void loadImageElseWhite(String image, ImageView imageView, final ProgressBar progress) {
        System.out.println("image:" + image);


        try {
            if (image != null && image.length() > 0) {

                Glide.with(this).load(image).
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
                Glide.with(this)
                        .load("")
                        .placeholder(R.mipmap.ic_launcher)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(imageView);
            }
        } catch (IllegalArgumentException e) {

        }

    }


    // LOAD WEB PAGE
    private void loadServerPage() {


        if (isConnected(this)) {
            startWebView();
            String mobile = PrefManger.getSharedPreferencesString(this, Constants.sp_mobile, "");
            String language = PrefManger.getSharedPreferencesString(this, Constants.sp_lang, "");
//            webview_openbusiness.loadUrl("https://www.google.com/");
//            webview_openbusiness.loadUrl("http://65.19.149.158/clic_rythuseva/RythusevaPost/RythusevaPost.aspx");
//            webview_openbusiness.loadUrl("http://65.19.149.158/clic_rythuseva/RythusevaPost/RythusevaPost.aspx?PAid=" + adhar + "");

            webview_openbusiness.loadUrl(Constants.generalWebUrl + "MBNO=" + mobile + "&Lan=" + language.substring(0, 1).toUpperCase() + "");

        } else {
            Helper.AlertMesg(
                    this,
                    "Not able to connect to Farmer.Please check your network connection and try again.");
            webview_openbusiness.loadUrl("file:///android_asset/errorpage.html");
        }

    }

    private void loadServerPage_general() {

        if (isConnected(this)) {

            startWebView();
            String adhar = PrefManger.getSharedPreferencesString(this, Constants.sp_aadhar, "");
            String language = PrefManger.getSharedPreferencesString(this, Constants.sp_lang, "");
//            webview_openbusiness.loadUrl("https://www.google.com/");
//            webview_openbusiness.loadUrl("http://65.19.149.158/clic_rythuseva/RythusevaPost/RythusevaPost.aspx");
//            webview_openbusiness.loadUrl("http://65.19.149.158/clic_rythuseva/RythusevaPost/RythusevaPost.aspx?PAid=" + adhar + "");

            webview_openbusiness.loadUrl(Constants.myPostWebUrl + "PAid=" + adhar + "&lan=" + language.substring(0, 1).toUpperCase() + "");


        } else {
            Helper.AlertMesg(
                    this,
                    "Not able to connect to Farmer.Please check your network connection and try again.");
            webview_openbusiness.loadUrl("file:///android_asset/errorpage.html");
        }
    }

    private void startWebView() {


        dtd_progress.setVisibility(ProgressBar.VISIBLE);
        webview_openbusiness.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }


            public void onPageFinished(WebView view, String url) {
                Log.i("", "Finished loading URL: " + url);
                dtd_progress.setVisibility(ProgressBar.GONE);
            }

            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                final String errorMsg = description;

                webview_openbusiness.loadUrl("file:///android_asset/errorpage.html");
                showError(errorCode);

            }

        });


    }

    public boolean isConnected(Context context) {
        if (context == null)
            return false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();//isConnectedOrConnecting();

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

    private void webViewFilechooser(final WebView wv) {
        wv.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (dtd_progress != null && wv == webview_openbusiness) {
                    if (progress < 100
                            && dtd_progress.getVisibility() == ProgressBar.GONE) {
                        dtd_progress.setVisibility(ProgressBar.VISIBLE);
                    }
                    if (progress == 100) {
                        dtd_progress.setVisibility(ProgressBar.GONE);
                    }
                }

            }


        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webview_openbusiness != null && webview_openbusiness.canGoBack()) {
                        webview_openbusiness.goBack();
                    } else if (currentTopExpandWV != null && currentTopExpandWV.canGoBack()) {
                        currentTopExpandWV.goBack();
                    } else {
                        gotoMainScreen();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    private void gotoMainScreen() {

        if (dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.OFFICER_TYPE).equals("1")) {
            Intent intent = new Intent(ForumActivity.this, MPEOHomeActivity.class);
            startActivity(intent);
            finish();
        } else if (dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.OFFICER_TYPE).equals("2")) {
            Intent intent = new Intent(ForumActivity.this, MAOHomeActivity.class);
            startActivity(intent);
            finish();
        } else if (dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.OFFICER_TYPE).equals("3")) {
            Intent intent = new Intent(ForumActivity.this, JD_ADHomeActivity.class);
            startActivity(intent);
            finish();
        }


    }

    public void WriteNewPostOnWall(View v) {

        Intent intent = new Intent(ForumActivity.this, ForumPostActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ForumActivity.this, MPEOHomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void showError(int errorCode) {
        String message = null;
        String title = null;
        if (errorCode == WebViewClient.ERROR_AUTHENTICATION) {
            message = "User authentication failed on server";
            title = "Auth Error";
        } else if (errorCode == WebViewClient.ERROR_TIMEOUT) {
            message = "The server is taking too much time to communicate. Try again later.";
            title = "Connection Timeout";
        } else if (errorCode == WebViewClient.ERROR_TOO_MANY_REQUESTS) {
            message = "Too many requests during this load";
            title = "Too Many Requests";
        } else if (errorCode == WebViewClient.ERROR_UNKNOWN) {
            message = "Generic error";
            title = "Unknown Error";
        } else if (errorCode == WebViewClient.ERROR_BAD_URL) {
            message = "Check entered URL..";
            title = "Malformed URL";
        } else if (errorCode == WebViewClient.ERROR_CONNECT) {
            message = "Failed to connect to the server";
            title = "Connection";
        } else if (errorCode == WebViewClient.ERROR_FAILED_SSL_HANDSHAKE) {
            message = "Failed to perform SSL handshake";
            title = "SSL Handshake Failed";
        } else if (errorCode == WebViewClient.ERROR_HOST_LOOKUP) {
            message = "Server or proxy hostname lookup failed";
            title = "Host Lookup Error";
        } else if (errorCode == WebViewClient.ERROR_PROXY_AUTHENTICATION) {
            message = "User authentication failed on proxy";
            title = "Proxy Auth Error";
        } else if (errorCode == WebViewClient.ERROR_REDIRECT_LOOP) {
            message = "Too many redirects";
            title = "Redirect Loop Error";
        } else if (errorCode == WebViewClient.ERROR_UNSUPPORTED_AUTH_SCHEME) {
            message = "Unsupported authentication scheme (not basic or digest)";
            title = "Auth Scheme Error";
        } else if (errorCode == WebViewClient.ERROR_UNSUPPORTED_SCHEME) {
            message = "Unsupported URI scheme";
            title = "URI Scheme Error";
        } else if (errorCode == WebViewClient.ERROR_FILE) {
            message = "Generic file error";
            title = "File";
        } else if (errorCode == WebViewClient.ERROR_FILE_NOT_FOUND) {
            message = "File not found";
            title = "File";
        } else if (errorCode == WebViewClient.ERROR_IO) {
            message = "The server failed to communicate. Try again later.";
            title = "IO Error";
        }

        if (message != null) {
            final String errormsg = message;
            new AlertDialog.Builder(this)
                    .setMessage(message)
                    .setTitle(title + "!")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    webview_openbusiness.loadUrl("javascript:callFromActivity(\"" + errormsg + "\")");
                                    return;
                                }
                            }).show();
        }
    }

    public void onClick_ClusterPost(View view) {

        btn_otherPost.setBackgroundColor(getResources().getColor(R.color.white));
        btn_myPost.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        loadServerPage();


    }

    public void onClick_GeneralPost(View view) {

        btn_otherPost.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        btn_myPost.setBackgroundColor(getResources().getColor(R.color.white));
        loadServerPage_general();

    }


    public class WebAppInterface {

        Context context;

        public WebAppInterface(Context context) {
            this.context = context;

        }

        @JavascriptInterface
        public void showReloadPage(String toast) {
            Toast.makeText(ForumActivity.this, toast + " Reloading page...", Toast.LENGTH_SHORT).show();
            loadServerPage();
        }
    }
}
