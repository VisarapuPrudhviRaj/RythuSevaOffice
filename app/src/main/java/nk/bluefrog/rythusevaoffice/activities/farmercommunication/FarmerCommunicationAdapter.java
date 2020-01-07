package nk.bluefrog.rythusevaoffice.activities.farmercommunication;

/**
 * Created by nagendra on 1/3/17.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.util.ArrayList;

import nk.bluefrog.library.utils.Helper;
import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;


public class FarmerCommunicationAdapter extends RecyclerView.Adapter<FarmerCommunicationAdapter.ViewHolder> {
    private Context context;
    private DBHelper dbHelper;
    private ArrayList<FarmerCommModel> communicationList;
    private MediaController mediacontroller;
    private boolean videoState = false;


    public FarmerCommunicationAdapter(Context context,ArrayList<FarmerCommModel> communicationList) {
        this.context = context;
        dbHelper = new DBHelper(context);
        this.communicationList = communicationList;
        mediacontroller = new MediaController(context);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_farmer_communication, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        final FarmerCommModel commModel = communicationList.get(i);
        viewHolder.fc_title.setText(commModel.getTitle());
        viewHolder.fc_title_card.setText(commModel.getTitle());
        viewHolder.fc_time.setText(commModel.getDate());
        viewHolder.fc_desc.setText(commModel.getDescription());

        if(!commModel.getLink().contentEquals("") ) {

            viewHolder.fc_link.setVisibility(View.VISIBLE);
            viewHolder.fc_link.setText(commModel.getLink());
            viewHolder.fc_link.setPaintFlags(viewHolder.fc_link.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }else{
            viewHolder.fc_link.setVisibility(View.GONE);

        }

        if(commModel.getImagePath()!=null ){
            viewHolder.fLayout.setVisibility(View.VISIBLE);
            viewHolder.imageView.setVisibility(View.VISIBLE);
            File imageFile = new File(commModel.getImagePath());
            viewHolder.imageView.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
        }else{
            viewHolder.imageView.setVisibility(View.GONE);
        }

        viewHolder.fc_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(commModel.getLink()));
                    context.startActivity(browserIntent);    
                }catch (Exception e){
                    Helper.showToast(context,context.getString(R.string.invalid_link));
                }
                
            }
        });

        if(!commModel.getVideoLink().contentEquals("")){
            viewHolder.fLayout.setVisibility(View.VISIBLE);
            viewHolder.videoView.setVisibility(View.VISIBLE);
            viewHolder.ivPlay.setVisibility(View.VISIBLE);
            viewHolder.progress.setVisibility(View.GONE);
            viewHolder.imageView.setVisibility(View.GONE);

            mediacontroller.setAnchorView(viewHolder.videoView);

        }else{
            viewHolder.videoView.setVisibility(View.GONE);
            viewHolder.ivPlay.setVisibility(View.GONE);
            viewHolder.progress.setVisibility(View.GONE);
        }

/*https://www.youtube.com/watch?v=Zas9IRpP-P4*/
        viewHolder.ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (commModel.getVideoLink().trim().contains("youtube")) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(commModel.getVideoLink())));
                }else{
                    if(videoState) {
                        if (viewHolder.videoView.isPlaying()) {
                            viewHolder.videoView.pause();
                        }

                        viewHolder.videoView.setVisibility(View.VISIBLE);
                        videoState = false;
                    }else {
                        viewHolder.progress.setVisibility(View.VISIBLE);
                        viewHolder.ivPlay.setVisibility(View.GONE);
                        viewHolder.videoView.setMediaController(mediacontroller);
                        viewHolder.videoView.setVideoURI(Uri.parse(commModel.getVideoLink()));
                        viewHolder.videoView.requestFocus();
                        viewHolder.videoView.start();
                        videoState = true;

                    }
                }



            }
        });

        viewHolder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                viewHolder.progress.setVisibility(View.GONE);
                viewHolder.ivPlay.setVisibility(View.GONE);
            }
        });

        /*viewHolder.tv_title.setText(commModel.getTitle());
        viewHolder.tv_desc.setText(commModel.getDescription());
        viewHolder.tv_data.setText(String.format("Village :%s", commModel.getVillage()));
        if(commModel.getImagePath()!=null){
            File imageFile = new File(commModel.getImagePath());
            viewHolder.iv_image.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
        }

        viewHolder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Helper.confirmDialog(context, context.getString(R.string.del_ques), context.getString(R.string.yes), context.getString(R.string.no), new Helper.IL() {
                    @Override
                    public void onSuccess() {
                        dbHelper.deleteByValues(DBTables.FarmerCommunication.TABLE_NAME, new String[]{DBHelper.UID}, new String[]{commModel.getUid()});
                        communicationList.remove(i);
                        notifyItemRemoved(i);
                        Helper.showToast(context, context.getString(R.string.deleted));
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });*/


    }

    @Override
    public int getItemCount() {
        return communicationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_image;
        private TextView tv_title;
        private TextView tv_desc, tv_data, tv_uploadStatus;
        private ImageView iv_delete;

        private TextView fc_title;
        private TextView fc_time;
        private TextView fc_title_card;
        private TextView fc_desc;
        private TextView fc_link;
        private FrameLayout fLayout;
        private ImageView ivPlay;
        private ImageView imageView;
        private VideoView videoView;
        private ProgressBar progress;





        public ViewHolder(View view) {
            super(view);

            /*iv_image = view.findViewById(R.id.iv_image);
            tv_title = view.findViewById(R.id.tv_title);
            tv_desc = view.findViewById(R.id.tv_desc);
            tv_data = view.findViewById(R.id.tv_data);
            //tv_uploadStatus = view.findViewById(R.id.tv_uploadStatus);
            iv_delete = view.findViewById(R.id.iv_delete);*/

            fc_title = view.findViewById(R.id.tvImageTitle);
            fc_time = view.findViewById(R.id.tvTime);
            fc_title_card = view.findViewById(R.id.tvTitle);
            fc_desc = view.findViewById(R.id.tvMessage);
            fc_link = view.findViewById(R.id.tvlink);
            fLayout= view.findViewById(R.id.fl_image);

            ivPlay= view.findViewById(R.id.iv_play);
            imageView= view.findViewById(R.id.iv_image);
            videoView= view.findViewById(R.id.vv_video);
            progress= view.findViewById(R.id.progrss);

            progress.setVisibility(View.GONE);
            videoView.setVisibility(View.GONE);
            ivPlay.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);

            fLayout.setVisibility(View.GONE);




        }
    }

}
