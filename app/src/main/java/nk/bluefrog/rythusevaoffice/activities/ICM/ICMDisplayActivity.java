package nk.bluefrog.rythusevaoffice.activities.ICM;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import nk.bluefrog.library.BluefrogActivity;
import nk.bluefrog.library.utils.Helper;
import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.utils.Constants;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;
import nk.bluefrog.rythusevaoffice.utils.DBTables;

public class ICMDisplayActivity extends BluefrogActivity implements ICMAdapter.NotificationsAdapterListener {

    public static ICMDisplayActivity instance;
    TextView tvTime, tvTitle, tvMessage, tvlink;
    ImageView iv_image;
    private BroadcastReceiver mNotificationBroadcastReceiver;

    EditText et_reply;
    DBHelper dbhelper;
    String id;
    ICMRecycleViewAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<ICMClass> al;

    public static ICMDisplayActivity getInstance() {
        // TODO Auto-generated method stub
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_display);
        instance = this;
        dbhelper = new DBHelper(this);
        findViews();
        try {
            if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("notification")) {

                List<String> notifData = (List<String>) getIntent().getExtras().getSerializable("notification");

                id = notifData.get(1);
                String title = notifData.get(2);
                String message = notifData.get(3);
                String link = notifData.get(4);
                String imagePath = notifData.get(5);
                String timeStamp = notifData.get(6);

                tvTime.setText(timeStamp);
                tvTitle.setText(title);
                tvMessage.setText(message);
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


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        et_reply.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                //dbhelper.deleteAll(DBTables.NOTIFICATIONSReply.TABLE_NAME);
                dbhelper.insertintoTable(DBTables.ICMREPLY.TABLE_NAME, DBTables.ICMREPLY.cols,
                        new String[]{id,
                                et_reply.getText().toString().trim(),
                                Helper.getTodayDateTime()});

                et_reply.setText("");
                loadRecycleView();
                if (View.GONE != recyclerView.getVisibility())
                    adapter.notifyDataSetChanged();
            }
        });

    }

    private void findViews() {

        tvTime = (TextView) findViewById(R.id.tvTime);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvMessage = (TextView) findViewById(R.id.tvMessage);
        tvlink = (TextView) findViewById(R.id.tvlink);

        iv_image = (ImageView) findViewById(R.id.iv_image);

        et_reply = (EditText) findViewById(R.id.et_reply);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        et_reply.setVisibility(View.GONE);

        loadRecycleView();


    }

    public void loadRecycleView() {
        int count = dbhelper.getCount(DBTables.ICMREPLY.TABLE_NAME);
        if (count > 0) {

            recyclerView.setVisibility(View.VISIBLE);

            al = dbhelper.getReplyMessagesData(id);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(linearLayoutManager);
            adapter = new ICMRecycleViewAdapter(ICMDisplayActivity.this, al);
            recyclerView.setAdapter(adapter);
        } else {
            recyclerView.setVisibility(View.GONE);
        }
    }

    public void onClickReply(View view) {


        et_reply.setVisibility(View.VISIBLE);

        /*Intent mainIntent = new Intent(ICMDisplayActivity.this, NotificationReplyActivity.class);
        startActivity(mainIntent);*/

    }




    @Override
    protected void onResume() {
        super.onResume();

        closeProgressDialog();
        loadRecycleView();
        if (View.GONE != recyclerView.getVisibility())
            adapter.notifyDataSetChanged();

        instance = this;

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mNotificationBroadcastReceiver,
                new IntentFilter(Constants.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        //ICMUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        instance = null;
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mNotificationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onNotificationSelected(List<String> notification) {
        Intent mainIntent = new Intent(ICMDisplayActivity.this, ICMdisplayactivityNEW.class);
        mainIntent.putStringArrayListExtra("notification", (ArrayList<String>) notification);
        startActivity(mainIntent);
    }

    @Override
    public void onNotificationDelete(List<String> notification) {

    }
}
