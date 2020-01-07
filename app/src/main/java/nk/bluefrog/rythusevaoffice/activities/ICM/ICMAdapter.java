package nk.bluefrog.rythusevaoffice.activities.ICM;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.List;

import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.utils.Constants;

public class ICMAdapter extends RecyclerView.Adapter<ICMAdapter.MyViewHolder> {

    private Context mContext;
    private List<List<String>> tasksData;
    private NotificationsAdapterListener listener;
    private MediaController mediacontroller;
    private boolean videoState = false;


    public ICMAdapter(Context mContext, List<List<String>> taskData, NotificationsAdapterListener listener) {
        this.mContext = mContext;
        this.tasksData = taskData;
        this.listener = listener;
        mediacontroller = new MediaController(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_announcement, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        //ID, TITLE, MESSAGE, LINK, IMAGEPATH, TIMESTAMP, IS_READ, TYPE,
        // VIDEOPATH, IS_IMG_VID, DEPT_NAME, REPLY, REPLY_URL
        final List<String> data = tasksData.get(position);
        if (data.get(7).trim().equals("1")) {
            holder.rl_read.setBackgroundColor(mContext.getResources().getColor(R.color.notification_card_background_read));
        } else {
            holder.rl_read.setBackgroundColor(mContext.getResources().getColor(R.color.notification_card_background_unread));
        }
       // holder.iv_changable.setImageResource(R.drawable.chat);
        holder.tvTitle.setText(data.get(2));
        holder.tvImageTitle.setText(data.get(11));
        holder.tvMessage.setText(data.get(3));
        if (data.get(4).trim().equals("NA")) {
            holder.tvlink.setVisibility(View.GONE);
        } else {
            holder.tvlink.setVisibility(View.VISIBLE);
            final SpannableString s =
                    new SpannableString(data.get(4));
            Linkify.addLinks(s, Linkify.WEB_URLS);
            holder.tvlink.setMovementMethod(LinkMovementMethod.getInstance());
            holder.tvlink.setText(s);
        }

        //IMAGEPATH
        if (data.get(5).trim().equals("NA")) {
            holder.fl_image.setVisibility(View.GONE);
            holder.iv_image.setVisibility(View.GONE);
            holder.iv_play.setVisibility(View.GONE);
            holder.progrss.setVisibility(View.GONE);
        } else {
            holder.fl_image.setVisibility(View.VISIBLE);
            holder.iv_image.setVisibility(View.VISIBLE);
            holder.iv_changable.setImageResource(R.drawable.image);
            holder.iv_play.setVisibility(View.GONE);
            holder.progrss.setVisibility(View.GONE);
           /* Glide.with(mContext).load(data.get(5).trim()).
                    thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL).error(R.mipmap.ic_launcher)
                    .into(holder.iv_image);*/

            Constants.loadImageElseWhite(mContext, data.get(5).trim(), holder.iv_image);

        }

        holder.tvTime.setText(data.get(6));

        if (data.get(9).trim().equals("NA")) {

            holder.vv_video.setVisibility(View.GONE);
            holder.iv_play.setVisibility(View.GONE);
            holder.progrss.setVisibility(View.GONE);

        } else {
            holder.fl_image.setVisibility(View.VISIBLE);
            holder.vv_video.setVisibility(View.VISIBLE);
            mediacontroller.setAnchorView(holder.vv_video);
            holder.iv_play.setVisibility(View.VISIBLE);

            holder.iv_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (data.get(9).trim().contains("youtube")) {
                        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(data.get(9).trim())));
                    }else{
                        if (videoState) {
                            if (holder.vv_video.isPlaying()) {
                                holder.vv_video.pause();

                            }
                            holder.iv_play.setVisibility(View.VISIBLE);
                            videoState = false;

                        } else {
                            holder.progrss.setVisibility(View.VISIBLE);
                            holder.iv_play.setVisibility(View.GONE);
                            holder.vv_video.setMediaController(mediacontroller);
                            holder.vv_video.setVideoURI(Uri.parse(data.get(9).trim()));
                            holder.vv_video.requestFocus();
                            holder.vv_video.start();
                            videoState = true;

                        }
                    }



                }
            });

            holder.vv_video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                // Close the progress bar and play the video
                public void onPrepared(MediaPlayer mp) {
                    holder.progrss.setVisibility(View.GONE);
                    holder.iv_play.setVisibility(View.GONE);
                }
            });

        }


        if (data.get(12).trim().equalsIgnoreCase("Y")) {
            holder.tv_reply.setVisibility(View.GONE);

        } else {
            holder.tv_reply.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return tasksData.size();
    }

    public void removeItem(int position) {
        tasksData.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(List<String> item, int position) {
        tasksData.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }

    public interface NotificationsAdapterListener {
        void onNotificationSelected(List<String> notification);

        void onNotificationDelete(List<String> notification);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout view_background, rl_read;
        public CardView card_view;
        TextView tvImageTitle, tvTime, tvTitle, tvMessage, tvlink, tv_reply;
        ImageView iv_delete, iv_image, iv_play, iv_changable;
        FrameLayout fl_image;
        VideoView vv_video;
        ProgressBar progrss;

        public MyViewHolder(View view) {
            super(view);
            tvImageTitle = view.findViewById(R.id.tvImageTitle);
            fl_image = view.findViewById(R.id.fl_image);
            rl_read = view.findViewById(R.id.rl_read);
            view_background = view.findViewById(R.id.view_background);
            card_view = view.findViewById(R.id.card_view);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvMessage = view.findViewById(R.id.tvMessage);
            tvTime = view.findViewById(R.id.tvTime);
            tvlink = view.findViewById(R.id.tvlink);
            iv_delete = view.findViewById(R.id.iv_delete);
            iv_image = view.findViewById(R.id.iv_image);
            iv_play = view.findViewById(R.id.iv_play);
            tv_reply = view.findViewById(R.id.tv_reply);
            vv_video = view.findViewById(R.id.vv_video);
            progrss = view.findViewById(R.id.progrss);
            iv_changable = view.findViewById(R.id.iv_changable);

            tv_reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onNotificationSelected((tasksData.get(getAdapterPosition())));
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onNotificationSelected((tasksData.get(getAdapterPosition())));
                }
            });

            iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onNotificationDelete((tasksData.get(getAdapterPosition())));
                }
            });
        }
    }
}
