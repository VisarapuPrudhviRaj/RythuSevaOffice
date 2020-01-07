package nk.bluefrog.rythusevaoffice.activities.bulkbookingtracker;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import nk.bluefrog.rythusevaoffice.R;

/**
 * Created by rajkumar on 8/1/19.
 */

public class BookingStatusAdapter extends RecyclerView.Adapter<BookingStatusAdapter.MyViewHolder> {

    Context context;
    List<StatusModel> list;

    public BookingStatusAdapter(Context context, List<StatusModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public BookingStatusAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_status_labels, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BookingStatusAdapter.MyViewHolder holder, int position) {
        StatusModel model = list.get(position);


        holder.tv_date.setText(model.getTodaydate());
        holder.tv_remark.setText(model.getRemark());

        if (model.getStatus().equals("1")) {
            if (position==0){
                holder.tv_status.setText("Accepted");
            }else {
                holder.tv_status.setText("");
            }
        } else if (model.getStatus().equals("2")) {
            if (list.get(position-1).getStatus().equals("2")){
                holder.tv_status.setText("");
            }else {
                holder.tv_status.setText("Progress");
            }
        } else if (model.getStatus().equals("3")) {
            holder.tv_status.setText("Completed");
        } else {
            holder.tv_status.setText("Rejected");
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView tv_date, tv_status, tv_remark;

        public MyViewHolder(View itemView) {
            super(itemView);


            tv_status = itemView.findViewById(R.id.tv_status);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_remark = itemView.findViewById(R.id.tv_remark);
        }
    }
}


