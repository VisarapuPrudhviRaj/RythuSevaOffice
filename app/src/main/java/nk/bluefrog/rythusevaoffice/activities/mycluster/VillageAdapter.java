package nk.bluefrog.rythusevaoffice.activities.mycluster;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.models.Gp;

public class VillageAdapter extends RecyclerView.Adapter<VillageAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Gp> gpList;



    public VillageAdapter(Context context, ArrayList<Gp> gpList) {
        this.context= context;
        this.gpList=gpList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_item_cluster,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        final String gpId = gpList.get(i).getId();
        final String gpName = gpList.get(i).getName();
        viewHolder.gpName.setText(gpList.get(i).getName());
        viewHolder.farmersCount.setText(gpList.get(i).getFarmerCount());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context,FarmersInAVillageActivity.class);
                intent.putExtra("gpId",gpId);
                intent.putExtra("gpName",gpName);
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {

        return gpList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView gpName;
        private TextView farmersCount;

        ViewHolder(View itemView) {
            super(itemView);

            gpName = itemView.findViewById(R.id.tv_villagename);
            farmersCount = itemView.findViewById(R.id.tv_farmer_count);


        }
    }
}
