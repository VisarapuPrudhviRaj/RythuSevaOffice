package nk.bluefrog.rythusevaoffice.activities.seeds;

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
import nk.bluefrog.rythusevaoffice.activities.bulkbooking.SeedBulkBookingRequestActivity;
import nk.bluefrog.rythusevaoffice.utils.Constants;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;
import nk.bluefrog.rythusevaoffice.utils.DBTables;


public class SeedGridListAdapter extends RecyclerView.Adapter {

    private static final int GRID_ITEM = 0;
    private static final int LIST_ITEM = 1;

    boolean isSwitchView = true;
    DBHelper dbHelper;
    private JSONArray jsonArray;
    private Activity context;
    private boolean shops;
    private BulkBookinRequestPojo bulkBookinRequestPojo;

    public SeedGridListAdapter(Activity context, boolean shops, JSONArray jsonArray, BulkBookinRequestPojo bulkBookinRequestPojo) {
        this.shops = shops;
        this.jsonArray = jsonArray;
        this.context = context;
        this.bulkBookinRequestPojo = bulkBookinRequestPojo;
        dbHelper = new DBHelper(context);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == GRID_ITEM) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_view_gird, parent, false);

        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_view_list, parent, false);
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
            if (shops) {

                ((SeedGridListAdapter.ItemViewHolder) holder).tv_rent.setText(jsonEq.getString("SeedCategory").toString().trim());
                if (jsonEq.getString("Discount").toString().equals("0")) {
                    ((SeedGridListAdapter.ItemViewHolder) holder).tv_available.setVisibility(View.GONE);
                } else {
                    ((SeedGridListAdapter.ItemViewHolder) holder).tv_available.setText(jsonEq.getString("Discount").toString() + "% Discount");
                }
                Double itemprice = Double.parseDouble(jsonEq.getString("price")) / 100;
                ((SeedGridListAdapter.ItemViewHolder) holder).tv_equipName.setText(context.getString(R.string.price) + " ₹" + itemprice);
                ((SeedGridListAdapter.ItemViewHolder) holder).tv_distance.setText(context.getString(R.string.mini_quantity) + jsonEq.getString("Min_Quantity"));
                loadImageElseWhite(jsonEq.getString("ImagePath"), ((SeedGridListAdapter.ItemViewHolder) holder).iv_equipment, ((SeedGridListAdapter.ItemViewHolder) holder).progress);

                ((SeedGridListAdapter.ItemViewHolder) holder).tv_Name.setText(jsonEq.getString("OwnerName").toString().trim());
                ((SeedGridListAdapter.ItemViewHolder) holder).iv_call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            String aadharNo = dbHelper.getTabCol(DBTables.Register.TABLE_NAME, DBTables.Register.user_aadharid);
                            Constants.call(dbHelper,jsonEq.getString("MobileNo").toString().trim(), context, Constants.Type_SeedShops, aadharNo);
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
                            bulkBookinRequestPojo.setCategory_name(jsonEq.getString("SeedCategory") + "(" + jsonEq.getString("Variety") + ")");
                            bulkBookinRequestPojo.setCategory_id(jsonEq.getString("SeedCategoryID"));
                            bulkBookinRequestPojo.setSubcategory_id(jsonEq.getString("VarietyID"));
                            if (view != null) {
                                Intent in = new Intent(context, SeedBulkBookingRequestActivity.class);
                                in.putExtra("Seeds", (String) view.getTag());
                                in.putExtra("BBR", bulkBookinRequestPojo);
                                context.startActivity(in);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            } else {

                ((ItemViewHolder) holder).tv_rent.setText(jsonEq.getString("Catagory").toString().trim());
                ((ItemViewHolder) holder).tv_available.setVisibility(View.GONE);
                ((ItemViewHolder) holder).tv_equipName.setText(context.getString(R.string.price) + " ₹" + jsonEq.getString("Price").toString().trim());
                ((ItemViewHolder) holder).tv_distance.setText(jsonEq.getString("Distance") + " km");
                loadImageElseWhite(jsonEq.getString("Image"), ((ItemViewHolder) holder).iv_equipment, ((ItemViewHolder) holder).progress);


                ((ItemViewHolder) holder).tv_Name.setText(jsonEq.getString("FarmerName").toString().trim());
                ((ItemViewHolder) holder).iv_call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            String aadharNo = dbHelper.getTabCol(DBTables.Register.TABLE_NAME, DBTables.Register.user_aadharid);
                            Constants.call(dbHelper,jsonEq.getString("Phone").toString().trim(), context, Constants.Type_FarmerSeeds, aadharNo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("json", view.getTag().toString());
                        if (view != null) {
                            Intent in = new Intent(context, SeedsAvailablePlaceDetailsFromFarmer.class);
                            in.putExtra("Seeds", (String) view.getTag());
                            context.startActivity(in);
                        }
                    }
                });


            }


        } catch (Exception e) {
        }


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
        TextView tv_equipName, tv_rent, tv_available, tv_distance;
        ImageView iv_equipment;
        ProgressBar progress;
        TextView tv_Name;
        ImageView iv_call;

        public ItemViewHolder(View itemView) {
            super(itemView);
            tv_equipName = itemView.findViewById(R.id.tv_equipName);
            tv_rent = itemView.findViewById(R.id.tv_rent);
            tv_available = itemView.findViewById(R.id.tv_available);
            tv_distance = itemView.findViewById(R.id.tv_distance);
            iv_equipment = itemView.findViewById(R.id.iv_equipment);
            progress = itemView.findViewById(R.id.progress);

            tv_Name = itemView.findViewById(R.id.tv_Name);
            iv_call = itemView.findViewById(R.id.iv_call);
        }
    }


}

