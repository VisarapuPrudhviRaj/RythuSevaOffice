package nk.bluefrog.rythusevaoffice.activities.slidemenu;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.activities.mycluster.FarmerProfileActivity;
import nk.bluefrog.rythusevaoffice.models.Farmer;
import nk.bluefrog.rythusevaoffice.utils.Constants;

public class FarmerAdapter extends RecyclerView.Adapter<FarmerAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Farmer> farmersList;



    public FarmerAdapter(Context context, ArrayList<Farmer> farmersList) {

        this.context= context;
        this.farmersList = farmersList;



    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_item_farmer,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        final Farmer farmer = farmersList.get(i);

        viewHolder.farmerName.setText(farmer.getName());
        if(!farmer.getAadhar().contentEquals("NA")){
            viewHolder.farmerAadhar.setText(String.format("Aadhaar : %s", farmer.getAadhar()));
        }else{
            viewHolder.farmerAadhar.setText(farmer.getName());
        }

        viewHolder.farmerMobile.setText(String.format("Mobile : %s", farmer.getMobile()));
        loadImageElseWhite(farmer.getImage(), viewHolder.farmerImage,viewHolder.progressBar);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,FarmerProfileActivity.class);
                intent.putExtra("aadhaar",farmer.getAadhar());
                context.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {

        return farmersList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView farmerName;
        private TextView farmerMobile;
        private TextView farmerAadhar;
        private CircleImageView farmerImage;
        private ProgressBar progressBar;//progressBarMain



        ViewHolder(View itemView) {
            super(itemView);

            farmerName = itemView.findViewById(R.id.tv_Name);
            farmerMobile = itemView.findViewById(R.id.tv_mobile);
            farmerAadhar = itemView.findViewById(R.id.tv_AadhaarID);
            farmerImage = itemView.findViewById(R.id.iv_mainupload);
            progressBar = itemView.findViewById(R.id.progressBarMain);





        }
    }

    public void loadImageElseWhite(String image, ImageView imageView, final ProgressBar progress) {
        System.out.println("image:" + image);


        try {
            if ((image != null) && image.length() > 0) {

                if(!image.contentEquals("NA")) {

                    Glide.with(context).load(image).
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
                }else{
                    progress.setVisibility(View.GONE);
                }
            } else {
                //progress.setVisibility(View.GONE);
                Glide.with(context)
                        .load("")
                        .placeholder(R.mipmap.ic_launcher)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(imageView);
            }
        } catch (IllegalArgumentException e) {

        }

    }

}
