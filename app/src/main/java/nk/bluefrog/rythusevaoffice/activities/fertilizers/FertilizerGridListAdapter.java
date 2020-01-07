package nk.bluefrog.rythusevaoffice.activities.fertilizers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.activities.bulkbooking.BulkBookinRequestPojo;
import nk.bluefrog.rythusevaoffice.activities.bulkbooking.FertlizerBulkBookingRequestActivity;
import nk.bluefrog.rythusevaoffice.utils.Constants;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;
import nk.bluefrog.rythusevaoffice.utils.DBTables;


public class FertilizerGridListAdapter extends RecyclerView.Adapter {

    private static final int GRID_ITEM = 0;
    private static final int LIST_ITEM = 1;

    boolean isSwitchView = true;
    DBHelper dbHelper;
    private JSONArray jsonArray;
    private Activity context;
    private BulkBookinRequestPojo bulkBookinRequestPojo;

    public FertilizerGridListAdapter(Activity context, JSONArray jsonArray, BulkBookinRequestPojo bulkBookinRequestPojo) {
        this.jsonArray = jsonArray;
        this.context = context;
        this.bulkBookinRequestPojo = bulkBookinRequestPojo;
        dbHelper = new DBHelper(context);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == GRID_ITEM) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_view_gird_fertilizer, parent, false);

        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_view_list_fertilizer, parent, false);
        }
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        try {
            JSONObject model = jsonArray.getJSONObject(position);
            initializeViews(model, holder, position);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void initializeViews(final JSONObject jsonEq, final RecyclerView.ViewHolder holder, int position) {

        try {
            holder.itemView.setTag(jsonEq.toString());
            String name = jsonEq.getString("Fertilizer").toString().trim();
            ((ItemViewHolder) holder).tv_ename.setText(name);

            /*String status = context.getResources().getStringArray(R.array.statusdetails)[Integer.parseInt(jsonEq.getString("Equipment_Status_Id").toString().trim())];
            if (jsonEq.getString("Equipment_Status_Id").trim().equals("1")) {
                ((ItemViewHolder) holder).tv_available.setText("Available");
                ((ItemViewHolder) holder).tv_available.setTextColor(context.getResources().getColor(R.color.primary_dark_green));
            } else {
                ((ItemViewHolder) holder).tv_available.setText("Unavailable");
                ((ItemViewHolder) holder).tv_available.setTextColor(context.getResources().getColor(R.color.red));
            }*/
           Double itemprice = Double.parseDouble(jsonEq.getString("Price")) / 100;
            ((ItemViewHolder) holder).tv_km.setText(String.format("%.2f", Double.parseDouble(jsonEq.getString("Distance"))) + " KM");
            ((ItemViewHolder) holder).tv_erent.setText(context.getString(R.string.price_avv) + " ₹" + itemprice);

            loadImageElseWhite(jsonEq.getString("ImagePath"), ((ItemViewHolder) holder).iv_upload, ((ItemViewHolder) holder).progress);

            ((ItemViewHolder) holder).tv_Name.setText(jsonEq.getString("OwnerName").toString().trim());
            ((ItemViewHolder) holder).iv_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        String aadharNo = dbHelper.getTabCol(DBTables.Register.TABLE_NAME, DBTables.Register.user_aadharid);
                        Constants.call(dbHelper,jsonEq.getString("MobileNo").toString().trim(), context, Constants.Type_Fertilizers, aadharNo);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("json", view.getTag().toString());

                    try {
                        bulkBookinRequestPojo.setCategory_name(jsonEq.getString("Fertilizer"));
                        bulkBookinRequestPojo.setCategory_id(jsonEq.has("FertlizerId") ? jsonEq.getString("FertlizerId") : "1");
                        bulkBookinRequestPojo.setSubcategory_id("");
                        if (view != null) {
                            Intent in = new Intent(context, FertlizerBulkBookingRequestActivity.class);
                            in.putExtra("Seeds", (String) view.getTag());
                            in.putExtra("BBR", bulkBookinRequestPojo);
                            context.startActivity(in);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            });

        } catch (Exception e) {

            e.printStackTrace();
            e.getMessage();
        }

       /* String imageUrl = model.getImagePath();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .into(((ItemViewHolder) holder).imageView);

        }
        ((ItemViewHolder) holder).name.setText(model.getName());*/
    }

    public void loadImageElseWhite(String image, ImageView imageView, final ProgressBar progress) {
        System.out.println("image:" + image);


        try {
            if (image != null && image.length() > 0) {

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
            } else {
                Glide.with(context)
                        .load("")
                        .placeholder(R.mipmap.ic_launcher)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(imageView);
            }
        } catch (IllegalArgumentException e) {

        }

    }

    public boolean toggleItemViewType() {
        isSwitchView = !isSwitchView;
        return isSwitchView;
    }

    @Override
    public int getItemViewType(int position) {
        if (isSwitchView) {
            return GRID_ITEM;
        } else {
            return LIST_ITEM;
        }
    }


    @Override
    public int getItemCount() {
        return jsonArray.length();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tv_ename, tv_erent, tv_available, tv_km;
        ImageView iv_upload;
        ProgressBar progress;
        TextView tv_Name;
        ImageView iv_call;

        public ItemViewHolder(View itemView) {
            super(itemView);
            tv_ename = itemView.findViewById(R.id.tv_ename);
            tv_erent = itemView.findViewById(R.id.tv_erent);
            tv_available = itemView.findViewById(R.id.tv_available);
            tv_km = itemView.findViewById(R.id.tv_km);
            iv_upload = itemView.findViewById(R.id.iv_upload);
            progress = itemView.findViewById(R.id.progress);
            tv_Name = itemView.findViewById(R.id.tv_Name);
            iv_call = itemView.findViewById(R.id.iv_call);
        }
    }


}
