package nk.bluefrog.rythusevaoffice.activities.equipmentbookingtracker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.utils.Constants;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;
import nk.bluefrog.rythusevaoffice.utils.DBTables;

/**
 * Created by rajkumar on 8/1/19.
 */

public class EquipmentBookingTrackerAdapter extends RecyclerView.Adapter<EquipmentBookingTrackerAdapter.MyViewHolder> {

    List<EquipmentTrackerModel> list;
    Context context;
    DBHelper dbHelper;

    public EquipmentBookingTrackerAdapter(List<EquipmentTrackerModel> list, Context context) {
        this.list = list;
        this.context = context;
        dbHelper = new DBHelper(context);
    }

    @Override
    public EquipmentBookingTrackerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_equipment_tracker_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EquipmentBookingTrackerAdapter.MyViewHolder holder, int position) {
        final EquipmentTrackerModel model = list.get(position);
//
        holder.tv_seedName.setText(getEquipmentName(model.getEquipment_Id()));
        holder.tv_price_acre.setText(context.getString(R.string.rent_per_acre) + " :" + model.getEquipment_Rent_Arce() + "₹");
        holder.tv_delivery_address.setText(model.getAddress());
        holder.tv_Name.setText(model.getUser_Name());
        holder.tv_price.setText(context.getString(R.string.rent_per_hour) + " :" + " " + model.getEquipment_Rent() + "₹");
        holder.tv_purpose.setText(model.getPurpose());
        holder.tv_shopName.setText(model.getCustomer_Name());
        holder.tv_Variety.setText("Equipment");
        holder.iv_category.setImageDrawable(context.getResources().getDrawable(R.drawable.equipment_availability));
        holder.tv_mini_qunatity.setText("");
        holder.tv_booking_id.setText(context.getString(R.string.booking_id) + model.getBooking_Id());
        holder.tv_booking_date.setText(model.getBooking_date());
        holder.iv_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + model.getOwner_Mobile_No()));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                context.startActivity(intent);
            }
        });


        Constants.loadImageElseWhite(context, model.getEquipment_Image_Path(), holder.iv_shop, holder.progress);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EquipmentBookingStatusActivity.class);
                intent.putExtra("BookingId", model.getBooking_Id());
                intent.putExtra("TrackerModel", model);
                context.startActivity(intent);

            }
        });


    }

    private String getEquipmentName(String equipment_id) {
        String equipName = "";

        List<List<String>> db_equipName = dbHelper.getTableColDataByCond(DBTables.CategoryAndVarities.TABLE_NAME,
                DBTables.CategoryAndVarities.catName + "," + DBTables.CategoryAndVarities.subCatName,
                new String[]{DBTables.CategoryAndVarities.subCatID, DBTables.CategoryAndVarities.categoryType},
                new String[]{equipment_id, "6"});
        if (db_equipName.size() > 0) {
            for (int i = 0; i < db_equipName.size(); i++) {
                equipName = db_equipName.get(0).get(0) + "(" + db_equipName.get(0).get(1) + ")";
            }
        }

        return equipName;
    }

    private String getQuintalIntoKgs(String quantity) {
        String quantialInkgs;
        int result = Integer.parseInt(quantity) * 100;
        quantialInkgs = String.valueOf(result);
        return quantialInkgs;
    }

    private String getpriceKgs(String price) {
        String priceInkgs;
        int result = (int) (Double.parseDouble(price) / 100);
        priceInkgs = String.valueOf(result);
        return priceInkgs;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_seedName, tv_Variety, tv_purpose, tv_delivery_address,
                tv_price_acre, tv_price, tv_shopName, tv_Name, tv_mini_qunatity, quantity_label, tv_booking_date, tv_booking_id;
        ImageView iv_shop, iv_call, iv_category;
        ProgressBar progress;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv_seedName = itemView.findViewById(R.id.tv_seedName);
            tv_Variety = itemView.findViewById(R.id.tv_Variety);
            tv_purpose = itemView.findViewById(R.id.tv_purpose);
            tv_delivery_address = itemView.findViewById(R.id.tv_delivery_address);
            tv_price_acre = itemView.findViewById(R.id.tv_price_acre);
            tv_shopName = itemView.findViewById(R.id.tv_shopName);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_Name = itemView.findViewById(R.id.tv_Name);
            iv_shop = itemView.findViewById(R.id.iv_shop);
            iv_call = itemView.findViewById(R.id.iv_call);
            progress = itemView.findViewById(R.id.progress);
            tv_mini_qunatity = itemView.findViewById(R.id.tv_mini_qunatity);
            iv_category = itemView.findViewById(R.id.img_category);
            quantity_label = itemView.findViewById(R.id.quantity_label);
            tv_booking_id = itemView.findViewById(R.id.tv_booking_id);
            tv_booking_date = itemView.findViewById(R.id.tv_booking_date);

        }
    }
}
