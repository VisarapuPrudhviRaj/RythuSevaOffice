package nk.bluefrog.rythusevaoffice.activities.ICM;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.List;

import nk.bluefrog.library.BluefrogActivity;
import nk.bluefrog.library.utils.Helper;
import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.utils.Constants;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;
import nk.bluefrog.rythusevaoffice.utils.DBTables;

public class ICMdisplayactivityNEW extends BluefrogActivity {


    TextView tvTime, tvTitle, tvMessage, tvlink, tv_deptname;
    ImageView iv_image, iv_play,iv_changable;
    VideoView vv_video;

    LinearLayout ll_tv_reply;
    EditText et_reply;
    DBHelper dbhelper;

    ICMRecycleViewAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<ICMClass> al;
    FrameLayout ll_nottify;
    ProgressBar progrss;
    private boolean videoState = false;

    private MediaController mediacontroller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificationview_new);
        dbhelper = new DBHelper(this);
        mediacontroller = new MediaController(this);
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("notification")) {
            findViews();
        } else {
            finish();
        }


    }

    private void findViews() {

        tv_deptname = (TextView) findViewById(R.id.tv_deptname);
        tvTime = (TextView) findViewById(R.id.tvTime);

        ll_nottify = (FrameLayout) findViewById(R.id.ll_nottify);
        vv_video = (VideoView) findViewById(R.id.vv_video);
        iv_changable = (ImageView) findViewById(R.id.iv_changable);
        iv_image = (ImageView) findViewById(R.id.iv_image);
        progrss = (ProgressBar) findViewById(R.id.progrss);
        iv_play = (ImageView) findViewById(R.id.iv_play);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvMessage = (TextView) findViewById(R.id.tvMessage);
        tvlink = (TextView) findViewById(R.id.tvlink);

        ll_tv_reply = (LinearLayout) findViewById(R.id.ll_tv_reply);

        et_reply = (EditText) findViewById(R.id.et_reply);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        vv_video.setMediaController(mediacontroller);
        setNotificationToView();


    }

    private void setNotificationToView() {


        //ID, TITLE, MESSAGE, LINK, IMAGEPATH, TIMESTAMP, IS_READ, TYPE, VIDEOPATH, IS_IMG_VID, DEPT_NAME, REPLY, REPLY_URL
        List<String> notifi_data = getIntent().getExtras().getStringArrayList("notification");

        final String id = notifi_data.get(1);
        String title = notifi_data.get(2);
        String message = notifi_data.get(3);
        String link = notifi_data.get(4);
        String imagePath = notifi_data.get(5);
        String timeStamp = notifi_data.get(6);
        String isRead = notifi_data.get(7);
        String type = notifi_data.get(8);
        final String videopath = notifi_data.get(9);
        String IS_IMG_VID = notifi_data.get(10);
        String DEPT_NAME = notifi_data.get(11);
        String REPLY = notifi_data.get(12);
        String REPLY_URL = notifi_data.get(13);

        //Update
        dbhelper.updateByValues(DBTables.ICM.TABLE_NAME,
                new String[]{DBTables.ICM.IS_READ},
                new String[]{"1"},
                new String[]{DBTables.ICM.ID, DBTables.ICM.TITLE},
                new String[]{id, title});
        //iv_changable.setImageResource(R.drawable.chat);
        tv_deptname.setText(DEPT_NAME);
        tvTime.setText(timeStamp);

        //IMAGEPATH
        if (imagePath.trim().equals("NA")) {
            ll_nottify.setVisibility(View.GONE);
            iv_image.setVisibility(View.GONE);
            iv_play.setVisibility(View.GONE);
            progrss.setVisibility(View.GONE);
        } else {
            ll_nottify.setVisibility(View.VISIBLE);
            iv_image.setVisibility(View.VISIBLE);
            //iv_changable.setImageResource(R.drawable.image);
            iv_play.setVisibility(View.GONE);
            progrss.setVisibility(View.GONE);
           /* Glide.with(mContext).load(data.get(5).trim()).
                    thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL).error(R.mipmap.ic_launcher)
                    .into(holder.iv_image);*/

            Constants.loadImageElseWhite(ICMdisplayactivityNEW.this, imagePath, iv_image);

        }

        if (videopath.trim().equals("NA")) {

            vv_video.setVisibility(View.GONE);
            iv_play.setVisibility(View.GONE);
            progrss.setVisibility(View.GONE);

        } else {
            ll_nottify.setVisibility(View.VISIBLE);
            vv_video.setVisibility(View.VISIBLE);
            mediacontroller.setAnchorView(vv_video);
            iv_play.setVisibility(View.VISIBLE);

            iv_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (videopath.trim().contains("youtube")) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(videopath.trim())));
                    }else{
                        if (videoState) {
                            if (vv_video.isPlaying()) {
                                vv_video.pause();

                            }
                            iv_play.setVisibility(View.VISIBLE);
                            videoState = false;

                        } else {
                            progrss.setVisibility(View.VISIBLE);
                            iv_play.setVisibility(View.GONE);

                            vv_video.setVideoURI(Uri.parse(videopath.trim()));
                            vv_video.requestFocus();
                            vv_video.start();
                            videoState = true;

                        }
                    }



                }
            });

            vv_video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                // Close the progress bar and play the video
                public void onPrepared(MediaPlayer mp) {
                    progrss.setVisibility(View.GONE);
                    iv_play.setVisibility(View.GONE);
                }
            });

        }

        if (link.trim().equals("NA")) {
            tvlink.setVisibility(View.GONE);
        } else {
            tvlink.setVisibility(View.VISIBLE);
            //iv_changable.setImageResource(R.drawable.chat);
            final SpannableString s =
                    new SpannableString(link.trim());
            Linkify.addLinks(s, Linkify.WEB_URLS);
            tvlink.setMovementMethod(LinkMovementMethod.getInstance());
            tvlink.setText(s);
        }




        if (REPLY.trim().equalsIgnoreCase("Y")) {
            ll_tv_reply.setVisibility(View.GONE);
        } else {
            ll_tv_reply.setVisibility(View.GONE);
        }

        tvTime.setText(timeStamp);
        tvTitle.setText(title);
        tvMessage.setText(message);


        if (DEPT_NAME.trim().equals("")) {
            tv_deptname.setVisibility(View.GONE);
        } else {
            tv_deptname.setVisibility(View.VISIBLE);
            tv_deptname.setText(DEPT_NAME);
        }


        et_reply.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dbhelper.insertintoTable(DBTables.ICMREPLY.TABLE_NAME, DBTables.ICMREPLY.cols,
                        new String[]{id,
                                et_reply.getText().toString().trim(),
                                Helper.getTodayDateTime()});

                et_reply.setText("");
                loadRecycleView(id);
                //al = dbhelper.getReplyMessagesData(id);
                if (View.GONE != recyclerView.getVisibility())
                    adapter.notifyDataSetChanged();
            }
        });

        loadRecycleView(id);
    }

    public void loadRecycleView(String id) {
        int count = dbhelper.getCountByValues(DBTables.ICMREPLY.TABLE_NAME, new String[]{DBTables.ICMREPLY.NOTIFICATIONID},
                new String[]{id});
        if (count > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            al = dbhelper.getReplyMessagesData(id);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(linearLayoutManager);
            adapter = new ICMRecycleViewAdapter(ICMdisplayactivityNEW.this, al);
            recyclerView.setAdapter(adapter);
        } else {
            recyclerView.setVisibility(View.GONE);
        }
    }

    public void onClickReply(View view) {
        et_reply.setVisibility(View.VISIBLE);
    }


}
