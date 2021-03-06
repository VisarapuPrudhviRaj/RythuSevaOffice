package nk.bluefrog.rythusevaoffice.activities.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import nk.bluefrog.library.BluefrogActivity;
import nk.bluefrog.library.utils.Helper;
import nk.bluefrog.library.utils.PrefManger;
import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.activities.ICM.ICMActivity;
import nk.bluefrog.rythusevaoffice.activities.agriforum.ForumActivity;
import nk.bluefrog.rythusevaoffice.activities.bulkbooking.BulkBookingActivity;
import nk.bluefrog.rythusevaoffice.activities.bulkbookingtracker.BulkBookingTrackerListActivity;
import nk.bluefrog.rythusevaoffice.activities.cropinsurance.CropInsuranceActivity;
import nk.bluefrog.rythusevaoffice.activities.cropsowing.CropSowingActivity;
import nk.bluefrog.rythusevaoffice.activities.equipmentbookingtracker.EquipmentBookingTrackerListActivity;
import nk.bluefrog.rythusevaoffice.activities.equipments.EquipmentListActivity;
import nk.bluefrog.rythusevaoffice.activities.farmercommunication.FarmerCommMobile;
import nk.bluefrog.rythusevaoffice.activities.farmerinfo.Farmer_info_view;
import nk.bluefrog.rythusevaoffice.activities.fertilizers.FertilizersAvaliabilityGridMapListActivity;
import nk.bluefrog.rythusevaoffice.activities.land.LandActivity;
import nk.bluefrog.rythusevaoffice.activities.masterdata.MasterDataActivity;
import nk.bluefrog.rythusevaoffice.activities.mycluster.MyCluster;
import nk.bluefrog.rythusevaoffice.activities.nursery.NurseryAvaliabilityGridMapListActivity;
import nk.bluefrog.rythusevaoffice.activities.search.SearchActivity;
import nk.bluefrog.rythusevaoffice.activities.seeds.SeedAvailableGridListMapActivity;
import nk.bluefrog.rythusevaoffice.activities.slidemenu.MyGpsActivity;
import nk.bluefrog.rythusevaoffice.activities.slidemenu.ScheduleMeetingsActivity;
import nk.bluefrog.rythusevaoffice.activities.weather.WeatherReportActivity;
import nk.bluefrog.rythusevaoffice.services.LocationMonitoringService;
import nk.bluefrog.rythusevaoffice.utils.Constants;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;
import nk.bluefrog.rythusevaoffice.utils.DBTables;


public class MPEOHomeActivity extends BluefrogActivity implements MenuRecyclerViewAdapter.OnListFragmentInteractionListener {

    private static final int REQ_CODE_SPEECH_INPUT = 101;
    TextView textNotificationItemCount;
    ImageView imageNotificationItem, voice_icon;
    RecyclerView rvFilterList;
    TextView et_voicetext, iv_clear;
    ArrayList<String> result;
    BroadcastReceiver broadcastReceiver;
    private Toolbar toolbar;
    private RecyclerView menuView;
    private DBHelper dbHelper;

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Constants.changeLanguage(PrefManger.getSharedPreferencesString(this, Constants.sp_lang, ""), this);
        initViews();
        checkMasterData();
        drawerView();
        Intent intent = new Intent(MPEOHomeActivity.this, LocationMonitoringService.class);
        startService(intent);

    }

    private void checkMasterData() {
        if (dbHelper.getCountByValues(DBTables.CategoryAndVarities.TABLE_NAME, new String[]{DBTables.CategoryAndVarities.categoryType},
                new String[]{"1"}) == 0 || dbHelper.getCountByValues(DBTables.CategoryAndVarities.TABLE_NAME, new String[]{DBTables.CategoryAndVarities.categoryType}, new String[]{"2"}) == 0 ||
                dbHelper.getCountByValues(DBTables.CategoryAndVarities.TABLE_NAME, new String[]{DBTables.CategoryAndVarities.categoryType}, new String[]{"3"}) == 0) {
            Intent intent = new Intent(MPEOHomeActivity.this, MasterDataActivity.class);
            startActivity(intent);
        }

    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        menuView = findViewById(R.id.list);
        rvFilterList = findViewById(R.id.rvFilterList);
        dbHelper = new DBHelper(this);
        toolbar.setTitle(getString(R.string.title_mpeo_home));

        et_voicetext = findViewById(R.id.et_voicetext);
        iv_clear = findViewById(R.id.iv_clear);
        voice_icon = findViewById(R.id.voice_icon);

        iv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_voicetext.setText("");
                Intent intent = new Intent(MPEOHomeActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        voice_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceInput();
            }
        });

        setSupportActionBar(toolbar);
        menuView.setHasFixedSize(true);

        setMenuView();
        setBroadcastReceiverIntilization();
    }

    private void setBroadcastReceiverIntilization() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setupNotificationCount();

                onResume();
            }
        };
    }

    @Override
    protected void onStart() {
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(Constants.PUSH_NOTIFICATION));
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);

        super.onDestroy();
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.hello_officer_how_can_i_help_you));
        startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {

                if (resultCode == RESULT_OK && null != data) {
                    result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //et_voicetext.setText(result.get(0));
                    Intent intent = new Intent(MPEOHomeActivity.this, SearchActivity.class);
                    intent.putExtra("voiceWord", result.get(0));
                    startActivity(intent);
//                    voiceBaseSearch(result.get(0));
                }
                break;
            }

        }

    }


    private void setMenuView() {

        ((TextView) findViewById(R.id.tv_internalcomm)).setText(getString(R.string.officer_communication));
        ((TextView) findViewById(R.id.tv_farmercomm)).setText(getString(R.string.farmer_communication));
        ((TextView) findViewById(R.id.tv_agriform)).setText(getString(R.string.menu_agri_forum));
        ((TextView) findViewById(R.id.iv_clear)).setText(getString(R.string.search_for_farmers));


        if (isTablet(this)) {
            menuView.setLayoutManager(new GridLayoutManager(this, 4));
        } else {
            menuView.setLayoutManager(new GridLayoutManager(this, 3));
        }


        int icons[] = {R.drawable.mycluster, R.drawable.myland, R.drawable.cropsowing, R.drawable.myclusterinsurance,
                R.drawable.ubarization, R.drawable.farmerawarenesscampaign,
                R.drawable.blukbooking, R.drawable.bookingtracker, R.drawable.equipment_availability,
                R.drawable.seed, R.drawable.nearbyfertilizers, R.drawable.equipment_availability,
                R.drawable.nearbynursary, R.drawable.weather, R.drawable.apaims};

        String titles[] = new String[]{getString(R.string.menu_my_cluster), getString(R.string.menu_my_land), getString(R.string.menu_crop_sowing), getString(R.string.menu_near_cropinsurance),
                getString(R.string.menu_farmer_info), getString(R.string.menu_farmer_awareness),
                getString(R.string.menu_bulk_booking), getString(R.string.menu_booking_tracker),
                getString(R.string.menu_equip_booking_tracker),
                getString(R.string.menu_near_seeds), getString(R.string.menu_near_fertilizers)
                , getString(R.string.menu_near_equipments), getString(R.string.nursery), getString(R.string.weather), getString(R.string.apaims)};

        menuView.setAdapter(new MenuRecyclerViewAdapter(icons, titles, this));

    }

    private void drawerView() {
        DrawerLayout drawer = findViewById(R.id.drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open
                , R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                switch (id) {

                    case R.id.nav_gps:
                        Intent intent1 = new Intent(MPEOHomeActivity.this, MyGpsActivity.class);
                        startActivity(intent1);
                        break;
                    /*case R.id.nav_farmers:

                        Intent intent2 = new Intent(MPEOHomeActivity.this, MyFarmersActivity.class);
                        startActivity(intent2);

                        break;*/
                    /*case R.id.nav_crops:

                        break;*/

                   /* case R.id.nav_pin:
                        Intent intent4 = new Intent(MPEOHomeActivity.this, ChangePassword.class);
                        startActivity(intent4);

                        break;*/

                    case R.id.nav_schedule_meetings:

                        Intent intent5 = new Intent(MPEOHomeActivity.this, ScheduleMeetingsActivity.class);
                        startActivity(intent5);
                        break;

                    case R.id.nav_masterupdate:
                        Intent intent = new Intent(MPEOHomeActivity.this, MasterDataActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }


                DrawerLayout drawer = findViewById(R.id.drawer);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        updateProfileData();
    }

    private void updateProfileData() {

        NavigationView navigationView = findViewById(R.id.navigation_view);
        ImageView iv_profileimage = navigationView.getHeaderView(0).findViewById(R.id.iv_profileimage);
        TextView tv_username = navigationView.getHeaderView(0).findViewById(R.id.tv_username);
        TextView tv_mobile = navigationView.getHeaderView(0).findViewById(R.id.tv_mobile);
        TextView tv_district = navigationView.getHeaderView(0).findViewById(R.id.tv_district);
        TextView tv_mandal = navigationView.getHeaderView(0).findViewById(R.id.tv_mandal);

        List<List<String>> mpoData = dbHelper.getDataByQuery(" Select distinct " + DBTables.FarmerTable.MANDAL_NAME + "," + DBTables.FarmerTable.DIST_NAME + " from " + DBTables.FarmerTable.TABLE_NAME);

        tv_username.setText(PrefManger.getSharedPreferencesString(this, Constants.sp_name, ""));
        tv_mobile.setText(PrefManger.getSharedPreferencesString(this, Constants.sp_mobile, ""));
        tv_district.setText(mpoData.get(0).get(1));
        tv_mandal.setText(mpoData.get(0).get(0));
    }

    @Override
    public void onListFragmentInteraction(int pos) {


        switch (pos) {
            case 0:
                Intent intent = new Intent(this, MyCluster.class);
//                Intent intent = new Intent(this, JD_ADMyClusterActivity.class);
                startActivity(intent);

                break;
            case 1:
/*
                if (Helper.isNetworkAvailable(this)) {
                    Intent intent2 = new Intent(this, LandActivity.class);
                    startActivity(intent2);
                    break;
                } else {
                    Helper.AlertMesg(this, getString(R.string.no_internet_available));
                }
*/

                break;

            case 2:
              /*  if (Helper.isNetworkAvailable(this)) {
                    Intent intent3 = new Intent(this, CropSowingActivity.class);
                    startActivity(intent3);
                    break;
                } else {
                    Helper.AlertMesg(this, getString(R.string.no_internet_available));
                }*/
                break;

            case 4:
                if (Helper.isNetworkAvailable(this)) {
                    Intent intent4 = new Intent(this, Farmer_info_view.class);
                    startActivity(intent4);
                    break;
                } else {
                    Helper.AlertMesg(this, getString(R.string.no_internet_available));
                }

                break;


            case 6:
                if (Helper.isNetworkAvailable(this)) {
                    Intent intent5 = new Intent(this, BulkBookingActivity.class);
                    startActivity(intent5);
                    finish();
                    break;
                } else {
                    Helper.AlertMesg(this, getString(R.string.no_internet_available));
                }

                break;

            case 7:
                if (Helper.isNetworkAvailable(this)) {
                    Intent intent6 = new Intent(this, BulkBookingTrackerListActivity.class);
                    startActivity(intent6);
                    break;
                } else {
                    Helper.AlertMesg(this, getString(R.string.no_internet_available));
                }

                break;

            case 5:
                Intent intent7 = new Intent(MPEOHomeActivity.this, ScheduleMeetingsActivity.class);
                startActivity(intent7);
                break;
            case 9:
                if (Helper.isNetworkAvailable(this)) {
                    Intent intent8 = new Intent(this, SeedAvailableGridListMapActivity.class);
                    startActivity(intent8);
                    break;
                } else {
                    Helper.AlertMesg(this, getString(R.string.no_internet_available));
                }
                break;

            case 10:
                if (Helper.isNetworkAvailable(this)) {
                    Intent intent9 = new Intent(this, FertilizersAvaliabilityGridMapListActivity.class);
                    startActivity(intent9);
                    break;
                } else {
                    Helper.AlertMesg(this, getString(R.string.no_internet_available));
                }
                break;

            case 11:
                if (Helper.isNetworkAvailable(this)) {
                    Intent intent10 = new Intent(this, EquipmentListActivity.class);
                    startActivity(intent10);
                    break;
                } else {
                    Helper.AlertMesg(this, getString(R.string.no_internet_available));
                }
                break;

            /*Nursery column change to crop insurnace*/
          /*  case 10:
                if (Helper.isNetworkAvailable(this)) {
                    Intent intent11 = new Intent(this, NurseryAvaliabilityGridMapListActivity.class);
                    startActivity(intent11);
                    break;
                } else {
                    Helper.AlertMesg(this, getString(R.string.no_internet_available));
                }
                break;
*/
            case 3:
                if (Helper.isNetworkAvailable(this)) {
                    Intent intent11 = new Intent(this, CropInsuranceActivity.class);
                    startActivity(intent11);
                    break;
                } else {
                    Helper.AlertMesg(this, getString(R.string.no_internet_available));
                }
                break;

            case 13:
                /*Weather*/
                if (Helper.isNetworkAvailable(this)) {
                    Intent intent12 = new Intent(this, WeatherReportActivity.class);
                    startActivity(intent12);
                    break;
                } else {
                    Helper.AlertMesg(this, getString(R.string.no_internet_available));
                }
                break;
            case 14:
                //Pests
                final String appPackageName = "com.vassar.unifiedapp.apaims"; // getPackageName() from Context or Activity object
                boolean isAppInstalled = appInstalledOrNot(appPackageName);
                if (isAppInstalled) {
                    //This intent will help you to launch if the package is already installed
                    Intent LaunchIntent = getPackageManager()
                            .getLaunchIntentForPackage(appPackageName);
                    startActivity(LaunchIntent);
                } else {
                    // Do whatever we want to do if application not installed
                    // For example, Redirect to play store
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                }
                break;

            case 12:

                if (Helper.isNetworkAvailable(this)) {
                    Intent intent11 = new Intent(this, NurseryAvaliabilityGridMapListActivity.class);
                    startActivity(intent11);
                    break;
                } else {
                    Helper.AlertMesg(this, getString(R.string.no_internet_available));
                }

                break;

            case 8:

                if (Helper.isNetworkAvailable(this)) {
                    Intent intent11 = new Intent(this, EquipmentBookingTrackerListActivity.class);
                    startActivity(intent11);
                    break;
                } else {
                    Helper.AlertMesg(this, getString(R.string.no_internet_available));
                }

                break;


        }


    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (PrefManger.getSharedPreferencesString(this, Constants.sp_lang, "").equals("en")) {
            getMenuInflater().inflate(R.menu.menu_eng, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_tel, menu);
        }

        final MenuItem menuItem = menu.findItem(R.id.action_notification);

        View actionView = menuItem.getActionView();
        textNotificationItemCount = actionView.findViewById(R.id.notification_badge);
        imageNotificationItem = actionView.findViewById(R.id.iv_notification);


        setupNotificationCount();

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOptionsItemSelected(menuItem);
            }
        });

        return true;
    }

    private void setupNotificationCount() {

        if (textNotificationItemCount != null) {
            int mCartItemCount = dbHelper.getCountByValue(DBTables.ICM.TABLE_NAME, DBTables.ICM.IS_READ, "0");
            if (mCartItemCount == 0) {
                if (textNotificationItemCount.getVisibility() != View.GONE) {
                    textNotificationItemCount.setVisibility(View.GONE);
                    imageNotificationItem.setAnimation(null);
                }
            } else {
                textNotificationItemCount.setText(String.valueOf(Math.min(mCartItemCount, 99)));
                if (textNotificationItemCount.getVisibility() != View.VISIBLE) {
                    textNotificationItemCount.setVisibility(View.VISIBLE);

                }
                AnimateBell(imageNotificationItem);

            }

        }
    }

    public void AnimateBell(ImageView imageView) {
        if (imageView != null) {
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shakeanimation);
            imageView.setAnimation(shake);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Runtime.getRuntime().gc();

        if (item.getItemId() == R.id.action_language) {
            setLang();
        }
        if (item.getItemId() == R.id.action_notification) {
            startActivity(new Intent(MPEOHomeActivity.this, ICMActivity.class));
            finish();
        }
        return true;
    }

    public void setLang() {

        if (PrefManger.getSharedPreferencesString(this, Constants.sp_lang, "").toString().trim().equals("te")) {
            PrefManger.putSharedPreferencesString(this, Constants.sp_lang, "en");
            changeLanguage_("en", this);
        } else {
            PrefManger.putSharedPreferencesString(this, Constants.sp_lang, "te");
            changeLanguage_("te", this);

        }

    }

    public void changeLanguage_(String languageToLoad, Context context) {

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

        recreate();

    }

    public void icmopen(View view) {
        startActivity(new Intent(MPEOHomeActivity.this, ICMActivity.class));
        finish();
    }

    public void farmerCommunication(View view) {

        startActivity(new Intent(MPEOHomeActivity.this, FarmerCommMobile.class));
    }

    public void openAgriForum(View view) {
        Intent intent = new Intent(this, ForumActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        exitDialog();
    }

    private void exitDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MPEOHomeActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        // Setting Dialog Title
        alertDialog.setTitle(R.string.app_name);
        alertDialog.setCancelable(false);
        // Setting Dialog Message
        alertDialog.setMessage(R.string.are_you_sure_want_to_exit);
        alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    finishAffinity();
                } else {
                    finish();
                }

            }
        });
        alertDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupNotificationCount();
    }



    public class RecyclerViewNoBugLinearLayoutManager extends LinearLayoutManager {
        public RecyclerViewNoBugLinearLayoutManager(Context context) {
            super(context);
        }

        public RecyclerViewNoBugLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public RecyclerViewNoBugLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                //try catch一下
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

        }
    }
}
