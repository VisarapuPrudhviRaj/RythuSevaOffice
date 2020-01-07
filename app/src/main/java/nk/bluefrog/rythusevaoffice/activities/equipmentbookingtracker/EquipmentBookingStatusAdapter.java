package nk.bluefrog.rythusevaoffice.activities.equipmentbookingtracker;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.activities.bulkbookingtracker.StatusModel;

/**
 * Created by rajkumar on 8/1/19.
 */

public class EquipmentBookingStatusAdapter extends RecyclerView.Adapter<EquipmentBookingStatusAdapter.MyViewHolder> {

    Context context;
    List<EuipmentStatusModel> list;

    public EquipmentBookingStatusAdapter(Context context, List<EuipmentStatusModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public EquipmentBookingStatusAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_equipment_status_labels, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EquipmentBookingStatusAdapter.MyViewHolder holder, int position) {
        EuipmentStatusModel model = list.get(position);


        holder.tv_date.setText(model.getTodaydate());
        holder.tv_remark.setText(model.getRemark());

        if (model.getStatus().equals("Y")) {
            holder.tv_status.setText("Approved");
        } else if (model.getStatus().equals("P")) {
            holder.tv_status.setText("In Progress");
        } else if (model.getStatus().equals("D")) {
            holder.tv_status.setText("Completed");
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


