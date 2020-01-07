package nk.bluefrog.rythusevaoffice.activities.bulkbookingtracker;

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

/**
 * Created by rajkumar on 8/1/19.
 */

public class BookingTrackerAdapter extends RecyclerView.Adapter<BookingTrackerAdapter.MyViewHolder> {

    List<TrackerModel> list;
    Context context;

    public BookingTrackerAdapter(List<TrackerModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public BookingTrackerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_tracker_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BookingTrackerAdapter.MyViewHolder holder, int position) {
        final TrackerModel model = list.get(position);

        holder.tv_seedName.setText(model.getCategory_name());
        holder.tv_discount.setText(model.getShop_discount() + "%" + context.getString(R.string.discount));
        holder.tv_delivery_address.setText(model.getDelivery_address());
        holder.tv_Name.setText(model.getShop_owner_name());

        holder.tv_quantity.setText((model.getQuantity()) + " Kgs");
        holder.tv_shopName.setText(model.getShop_name());
        holder.tv_booking_id.setText(context.getString(R.string.booking_id) + model.getBooking_id());
        holder.tv_booking_date.setText(model.getBookingDate());
        if (model.getCommodity().equalsIgnoreCase("1")) {
            holder.tv_Variety.setText("Seed");
            holder.iv_category.setImageDrawable(context.getResources().getDrawable(R.drawable.bottom_seeds));
            holder.tv_mini_qunatity.setText(context.getString(R.string.mini_quantity) + " :" + " " + model.getShop_mini_order_quantity());
            holder.tv_price.setText(context.getString(R.string.price) + " :" + " " + getpriceKgs(model.getShop_categroy_price()) + "₹");
        } else if (model.getCommodity().equalsIgnoreCase("2")) {
            holder.iv_category.setImageDrawable(context.getResources().getDrawable(R.drawable.nearbyfertilizers));
            holder.tv_Variety.setText("Fertilizers");
            holder.tv_mini_qunatity.setText(context.getString(R.string.mini_quantity) + " :" + " " + model.getShop_mini_order_quantity());
            holder.tv_price.setText(context.getString(R.string.price) + " :" + " " + getpriceKgs(model.getShop_categroy_price()) + "₹");
        } else if (model.getCommodity().equalsIgnoreCase("3")) {
            holder.tv_Variety.setText("Nursery");
            holder.tv_mini_qunatity.setText(context.getString(R.string.mini_pots) + " :" + " " + model.getShop_mini_order_quantity());
            holder.tv_quantity.setText((model.getQuantity()) + " Plants");
            holder.quantity_label.setText(context.getString(R.string.number_of_pots));
            holder.iv_category.setImageDrawable(context.getResources().getDrawable(R.drawable.nearbynursary));
            holder.tv_price.setText(context.getString(R.string.price) + " :" + " " + model.getShop_categroy_price() + "₹");
        }

        holder.iv_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + model.getShop_phnumber()));
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


        Constants.loadImageElseWhite(context, model.getShop_image(), holder.iv_shop, holder.progress);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BulkBookingStatusActivity.class);
                intent.putExtra("BookingId", model.getBooking_id());
                intent.putExtra("TrackerModel", model);
                context.startActivity(intent);

            }
        });


    }

    private String getQuintalIntoKgs(String quantity) {
        String quantialInkgs;
        int result = Integer.parseInt(quantity) * 100;
        quantialInkgs = String.valueOf(result);
        return quantialInkgs;
    }

    private String getpriceKgs(String price) {
        String priceInkgs;
        double result = (Double.parseDouble(price) / 100);
        priceInkgs = String.valueOf(result);
        return priceInkgs;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_seedName, tv_Variety, tv_quantity, tv_delivery_address,
                tv_discount, tv_price, tv_shopName, tv_Name, tv_mini_qunatity, quantity_label,
                tv_booking_id, tv_booking_date;
        ImageView iv_shop, iv_call, iv_category;
        ProgressBar progress;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv_seedName = itemView.findViewById(R.id.tv_seedName);
            tv_Variety = itemView.findViewById(R.id.tv_Variety);
            tv_quantity = itemView.findViewById(R.id.tv_quantity);
            tv_delivery_address = itemView.findViewById(R.id.tv_delivery_address);
            tv_discount = itemView.findViewById(R.id.tv_discount);
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
