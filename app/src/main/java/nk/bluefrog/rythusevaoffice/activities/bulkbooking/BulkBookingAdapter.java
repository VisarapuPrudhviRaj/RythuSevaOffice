package nk.bluefrog.rythusevaoffice.activities.bulkbooking;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
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

import java.util.List;

import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.activities.equipments.EquipmentBookingDetails;
import nk.bluefrog.rythusevaoffice.utils.Constants;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;
import nk.bluefrog.rythusevaoffice.utils.DBTables;


public class BulkBookingAdapter extends RecyclerView.Adapter {

    private static final int GRID_ITEM = 0;
    private static final int LIST_ITEM = 1;

    boolean isSwitchView = true;
    DBHelper dbHelper;
    private JSONArray jsonArray;
    private Activity context;
    private boolean shops;
    private BulkBookinRequestPojo bulkBookinRequestPojo;

    public BulkBookingAdapter(Activity context, boolean shops, JSONArray jsonArray, BulkBookinRequestPojo bulkBookinRequestPojo) {
        this.shops = shops;
        this.jsonArray = jsonArray;
        this.context = context;
        this.bulkBookinRequestPojo = bulkBookinRequestPojo;
        dbHelper = new DBHelper(context);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_view_list, parent, false);

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
            if (shops == true) {
                /*seed*/
                if (bulkBookinRequestPojo.getCommodity().equals("1")) {
                    ((ItemViewHolder) holder).tv_rent.setText(jsonEq.getString("SeedCategory").toString().trim());
                    if (jsonEq.getString("Discount").toString().equals("0")) {
                        ((ItemViewHolder) holder).tv_available.setVisibility(View.GONE);
                    } else {
                        ((ItemViewHolder) holder).tv_available.setText(jsonEq.getString("Discount").toString() + "% Discount");
                    }
                    Double itemprice = Double.parseDouble(jsonEq.getString("price")) / 100;
                    ((ItemViewHolder) holder).tv_equipName.setText(context.getString(R.string.price) + " ₹" + itemprice);
                    ((ItemViewHolder) holder).tv_distance.setText(context.getString(R.string.mini_quantity) + jsonEq.getString("Min_Quantity"));
                    loadImageElseWhite(jsonEq.getString("ImagePath"), ((ItemViewHolder) holder).iv_equipment, ((ItemViewHolder) holder).progress);

                    ((ItemViewHolder) holder).tv_Name.setText(jsonEq.getString("OwnerName").toString().trim());
                    ((ItemViewHolder) holder).iv_call.setOnClickListener(new View.OnClickListener() {
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

                    /*fertilizer*/
                } else if (bulkBookinRequestPojo.getCommodity().equals("2")) {

                    ((ItemViewHolder) holder).tv_rent.setText(jsonEq.getString("Fertilizer").toString().trim());
                    if ((jsonEq.getString("Discount") + "").equals("0")) {
                        ((ItemViewHolder) holder).tv_available.setVisibility(View.GONE);
                    } else {
                        ((ItemViewHolder) holder).tv_available.setText(jsonEq.getString("Discount").toString() + "% Discount");
                    }
                    Double itemprice = Double.parseDouble(jsonEq.getString("Price")) / 100;
                    ((ItemViewHolder) holder).tv_equipName.setText(context.getString(R.string.price) + " ₹" + itemprice);
                    ((ItemViewHolder) holder).tv_distance.setText(context.getString(R.string.mini_quantity) + jsonEq.getString("Min_Quantity") + "");
                    loadImageElseWhite(jsonEq.getString("ImagePath"), ((ItemViewHolder) holder).iv_equipment, ((ItemViewHolder) holder).progress);

                    ((ItemViewHolder) holder).tv_Name.setText(jsonEq.getString("ShopName").toString().trim());
                    ((ItemViewHolder) holder).iv_call.setOnClickListener(new View.OnClickListener() {
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

                                bulkBookinRequestPojo.setCategory_name(jsonEq.getString("Fertilizer"));
                                bulkBookinRequestPojo.setCategory_id(jsonEq.has("FertlizerId") ? jsonEq.getString("FertlizerId") : "1");
                                bulkBookinRequestPojo.setSubcategory_id("");
                                if (view != null) {
                                    Intent in = new Intent(context, FertlizerBulkBookingRequestActivity.class);
                                    in.putExtra("Seeds", (String) view.getTag());
                                    in.putExtra("BBR", bulkBookinRequestPojo);
                                    context.startActivity(in);
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    /*Nursery*/
                } else if (bulkBookinRequestPojo.getCommodity().equals("3")) {

                    ((ItemViewHolder) holder).tv_rent.setText(jsonEq.getString("PlantType").toString().trim());
                    if ((jsonEq.getString("Discount") + "").equals("0")) {
                        ((ItemViewHolder) holder).tv_available.setVisibility(View.GONE);
                    } else {
                        ((ItemViewHolder) holder).tv_available.setText(jsonEq.getString("Discount").toString() + "% Discount");
                    }
                    Double itemprice = Double.parseDouble(jsonEq.getString("Price")) / 100;
                    ((ItemViewHolder) holder).tv_equipName.setText(context.getString(R.string.price) + " ₹" + itemprice);
                    ((ItemViewHolder) holder).tv_distance.setText(context.getString(R.string.mini_pots) + jsonEq.getString("Min_Quantity") + "");
                    loadImageElseWhite(jsonEq.getString("ImagePath"), ((ItemViewHolder) holder).iv_equipment, ((ItemViewHolder) holder).progress);

                    ((ItemViewHolder) holder).tv_Name.setText(jsonEq.getString("NurseryName").toString().trim());
                    ((ItemViewHolder) holder).iv_call.setOnClickListener(new View.OnClickListener() {
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

                                bulkBookinRequestPojo.setCategory_name(jsonEq.getString("PlantType"));
                                bulkBookinRequestPojo.setCategory_id(jsonEq.has("NurseryId") ? jsonEq.getString("NurseryId") : "1");
                                bulkBookinRequestPojo.setSubcategory_id("");
                                if (view != null) {
                                    Intent in = new Intent(context, NurseryrBulkBookingRequestActivity.class);
                                    in.putExtra("Seeds", (String) view.getTag());
                                    in.putExtra("BBR", bulkBookinRequestPojo);
                                    context.startActivity(in);
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });

                }

            } else if (shops == false) {
                ((ItemViewHolder) holder).tv_Name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                ((ItemViewHolder) holder).tv_Name.setText(getEquimentName(jsonEq.getString("Equipment_Id").toString().trim()));

                ((ItemViewHolder) holder).tv_equipName.setText(Html.fromHtml("<font color='#000000' > <b>" + context.getString(R.string.rent_per_hour) + " : " + "</b></font>" + jsonEq.getString("Equipment_Rent") + " ₹"));
                ((ItemViewHolder) holder).tv_rent.setText(Html.fromHtml("<font color='#000000' > <b>" + context.getString(R.string.rent_per_acre) + " : " + "</b></font>" + jsonEq.getString("Equipment_Rent_Acre") + " ₹"));
                ((ItemViewHolder) holder).tv_available.setTextColor(context.getResources().getColor(R.color.black));
                ((ItemViewHolder) holder).tv_available.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                ((ItemViewHolder) holder).tv_available.setTypeface(null, Typeface.NORMAL);

                ((ItemViewHolder) holder).tv_available.setText(jsonEq.getString("User_Name"));
                ((ItemViewHolder) holder).tv_distance.setText(jsonEq.getString("Distance_in_Km") + " KM");
                loadImageElseWhite(jsonEq.getString("Equipment_Image_Path"), ((ItemViewHolder) holder).iv_equipment, ((ItemViewHolder) holder).progress);
                ((ItemViewHolder) holder).iv_call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            String aadharNo = dbHelper.getTabCol(DBTables.Register.TABLE_NAME, DBTables.Register.user_aadharid);
                            Constants.call(dbHelper,jsonEq.getString("Owner_Mobile_No").toString().trim(), context, Constants.Type_Equipments, aadharNo);
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
                            Intent in = new Intent(context, EquipmentBookingDetails.class);
                            in.putExtra("Equipment", (String) view.getTag());
                            if (bulkBookinRequestPojo.getCommodity().equals("4")) {
                                in.putExtra("commodityType", "6");
                            } else if (bulkBookinRequestPojo.getCommodity().equals("5")) {
                                in.putExtra("commodityType", "7");
                            }
                            context.startActivity(in);
                        }


                          /*  if (view != null) {
                                Intent in = new Intent(context, SeedBulkBookingRequestActivity.class);
                                in.putExtra("Seeds", (String) view.getTag());
                                in.putExtra("BBR", bulkBookinRequestPojo);
                                context.startActivity(in);
                            }*/


                    }
                });


            }


        } catch (Exception e) {
        }


    }

    private String getEquimentName(String equipment_id) {
        String equipmentName = "";
        List<List<String>> db_equipName = dbHelper.getTableColDataByCond(DBTables.CategoryAndVarities.TABLE_NAME,
                DBTables.CategoryAndVarities.catName + "," + DBTables.CategoryAndVarities.subCatName,
                new String[]{DBTables.CategoryAndVarities.subCatID, DBTables.CategoryAndVarities.categoryType},
                new String[]{equipment_id, "6"});
        if (db_equipName.size() > 0) {
            for (int i = 0; i < db_equipName.size(); i++) {
                equipmentName = db_equipName.get(0).get(1) + "(" + db_equipName.get(0).get(0) + ")";
            }
        }
        return equipmentName;
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

