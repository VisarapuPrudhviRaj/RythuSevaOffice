package nk.bluefrog.rythusevaoffice.activities.ICM;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import nk.bluefrog.library.BluefrogActivity;
import nk.bluefrog.library.utils.Helper;
import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.activities.home.JD_ADHomeActivity;
import nk.bluefrog.rythusevaoffice.activities.home.MAOHomeActivity;
import nk.bluefrog.rythusevaoffice.activities.home.MPEOHomeActivity;
import nk.bluefrog.rythusevaoffice.utils.Constants;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;
import nk.bluefrog.rythusevaoffice.utils.DBTables;
import nk.bluefrog.rythusevaoffice.utils.RecyclerItemTouchHelper;

public class ICMActivity extends BluefrogActivity implements ICMAdapter.NotificationsAdapterListener,
        RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    public static ICMActivity instance;
    DBHelper dbhelper;
    CoordinatorLayout main_content;
    private BroadcastReceiver mNotificationBroadcastReceiver;
    private RecyclerView recyclerView;
    private RelativeLayout noData;
    private ICMAdapter adapter;
    private List<List<String>> annData;

    public static ICMActivity getInstance() {
        // TODO Auto-generated method stub
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        instance = this;
        dbhelper = new DBHelper(this);
        findViews();
        notificationSetup();
        try {
            if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("notification")) {
                JSONObject jsonObject = new JSONObject(getIntent().getExtras().getString("notification"));
                if (jsonObject.getString(Constants.N_STATUS).trim().equals("N")) {
                    refresh();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void notificationSetup() {
        mNotificationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(Constants.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    String obj = intent.getStringExtra("message");

                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(obj.trim());
                        if (jsonObject.getString(Constants.N_STATUS).trim().equals("N")) {
                            String id = jsonObject.getString(Constants.N_Message_Id);
                            String title = jsonObject.getString(Constants.N_Title);
                            String message = jsonObject.getString(Constants.N_Message);
                            String link = jsonObject.getString(Constants.N_Link);
                            String imagePath = jsonObject.getString(Constants.N_ImagePath);
                            String timeStamp = jsonObject.getString(Constants.N_Created_Datetime);
                            String VIDEOPATH = jsonObject.has(Constants.N_VIDEOPATH) ? jsonObject.getString(Constants.N_VIDEOPATH) : "NA";
                            String IS_IMG_VID = jsonObject.has(Constants.N_IS_IMG_VID) ? jsonObject.getString(Constants.N_IS_IMG_VID) : "NA";
                            String DEPT_NAME = jsonObject.has(Constants.N_DEPT_NAME) ? jsonObject.getString(Constants.N_DEPT_NAME) : "";
                            String REPLY = jsonObject.has(Constants.N_REPLY) ? jsonObject.getString(Constants.N_REPLY) : "N";
                            String REPLY_URL = jsonObject.has(Constants.N_REPLY_URL) ? jsonObject.getString(Constants.N_REPLY_URL) : "";


                            long insertID = dbhelper.insertintoTable(DBTables.ICM.TABLE_NAME, DBTables.ICM.cols,
                                    new String[]{id, title,
                                            message,
                                            link,
                                            imagePath,
                                            timeStamp,
                                            "0", "N", VIDEOPATH, IS_IMG_VID, DEPT_NAME, REPLY, REPLY_URL});

                            refresh();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.push_notification), Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        instance = this;

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mNotificationBroadcastReceiver,
                new IntentFilter(Constants.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
      //  ICMUtils.clearNotifications(getApplicationContext());
        refresh();
    }

    @Override
    protected void onPause() {
        instance = null;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mNotificationBroadcastReceiver);
        super.onPause();
    }


    private void findViews() {

       // setToolBar(getString(R.string.internal_communication),"");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.internal_communication));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        main_content = (CoordinatorLayout) findViewById(R.id.main_content);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        noData = (RelativeLayout) findViewById(R.id.noData);
        annData = new ArrayList<>();
        annData = dbhelper.getDataByQuery("Select * from " + DBTables.ICM.TABLE_NAME + " order by " + DBHelper.UID + " DESC");
        adapter = new ICMAdapter(ICMActivity.this, annData, this);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


        if (annData.size() > 0) {
            noData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            noData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
    }


    private void getAnnouncementData() {
        annData = new ArrayList<>();
        annData = dbhelper.getTableData(DBTables.ICM.TABLE_NAME);
        if (annData.size() > 0) {
            adapter = new ICMAdapter(ICMActivity.this, annData, this);
            recyclerView.setAdapter(adapter);
            noData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            noData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    private void refresh() {
        annData.clear();
        List<List<String>> l_notificationData = dbhelper.getDataByQuery("Select * from " + DBTables.ICM.TABLE_NAME + " order by " + DBHelper.UID + " DESC");
        annData.addAll(l_notificationData);
        if (annData.size() > 0) {
            //adapter = new ICMAdapter(ICMActivity.this, annData, this);
            //recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            noData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            noData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (annData != null && annData.size() > 0)
            getMenuInflater().inflate(R.menu.clearnotif, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear:
                if (annData != null && annData.size() > 0) {
                    new AlertDialog.Builder(this).setTitle("Do you really want to clear all Notifications?").setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dbhelper.deleteAll(DBTables.ICM.TABLE_NAME);
                            Intent mainIntent = new Intent(ICMActivity.this, MPEOHomeActivity.class);
                            startActivity(mainIntent);
                            finish();
                            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        }
                    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
                } else {
                    Helper.showToast(ICMActivity.this, "No Notifications!");
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (dbhelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.OFFICER_TYPE).equals("1")) {
            Intent mainIntent = new Intent(ICMActivity.this, MPEOHomeActivity.class);
            startActivity(mainIntent);
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        } else if (dbhelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.OFFICER_TYPE).equals("2")) {
            Intent mainIntent = new Intent(ICMActivity.this, MAOHomeActivity.class);
            startActivity(mainIntent);
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        } else if (dbhelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.OFFICER_TYPE).equals("3")) {
            Intent mainIntent = new Intent(ICMActivity.this, JD_ADHomeActivity.class);
            startActivity(mainIntent);
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }


    }

    @Override
    public void onNotificationSelected(List<String> notification) {
        //ID,TITLE, MESSAGE, LINK, IMAGEPATH, TIMESTAMP, IS_READ, TYPE
        Intent mainIntent = new Intent(ICMActivity.this, ICMdisplayactivityNEW.class);
        mainIntent.putStringArrayListExtra("notification", (ArrayList<String>) notification);
        startActivity(mainIntent);



    }

    @Override
    public void onNotificationDelete(List<String> notification) {

    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof ICMAdapter.MyViewHolder) {

            // backup of removed item for undo purpose
            final List<String> deletedItem = annData.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            adapter.removeItem(viewHolder.getAdapterPosition());
            dbhelper.deleteByValues(DBTables.ICM.TABLE_NAME, new String[]{DBTables.ICM.ID},
                    new String[]{deletedItem.get(1)});

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(main_content, deletedItem.get(2).trim() + " removed from notifications!", Snackbar.LENGTH_LONG);
            snackbar.setAction("DELETED!", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    //adapter.restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
            if (annData.size() == 0) {
                noData.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                noData.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void notificationAlertChanged(final String id, final String title, final String msg, String link, String imagePath, String timeStamp, String VIDEOPATH, String IS_IMG_VID, String DEPT_NAME, String REPLY, String REPLY_URL) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ICMActivity.this);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dbhelper.updateByValues(DBTables.ICM.TABLE_NAME,
                        new String[]{DBTables.ICM.IS_READ},
                        new String[]{"1"},
                        new String[]{DBTables.ICM.ID, DBTables.ICM.TITLE, DBTables.ICM.MESSAGE},
                        new String[]{id, title, msg});
                refresh();
                dialog.dismiss();
            }
        });


        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.notification_alert, null);
        builder.setView(dialoglayout);
        TextView tvTime = (TextView) dialoglayout.findViewById(R.id.tvTime);
        TextView tvTitle = (TextView) dialoglayout.findViewById(R.id.tvTitle);
        TextView tvMessage = (TextView) dialoglayout.findViewById(R.id.tvMessage);
        TextView tvlink = (TextView) dialoglayout.findViewById(R.id.tvlink);
        ImageView iv_image = (ImageView) dialoglayout.findViewById(R.id.iv_image);

        tvTime.setText(timeStamp);
        tvTitle.setText(title);
        tvMessage.setText(msg);
        if (link.trim().equals("NA")) {
            tvlink.setVisibility(View.GONE);
        } else {
            tvlink.setVisibility(View.VISIBLE);
            final SpannableString s =
                    new SpannableString(link);
            Linkify.addLinks(s, Linkify.WEB_URLS);
            tvlink.setMovementMethod(LinkMovementMethod.getInstance());
            tvlink.setText(s);
        }

        if (!imagePath.trim().equals("NA")) {
            iv_image.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(imagePath)
                    .into(iv_image);
            Constants.loadImageElseWhite(getApplicationContext(), imagePath, iv_image);
        } else {
            iv_image.setVisibility(View.GONE);
        }

        builder.show();
    }

}
