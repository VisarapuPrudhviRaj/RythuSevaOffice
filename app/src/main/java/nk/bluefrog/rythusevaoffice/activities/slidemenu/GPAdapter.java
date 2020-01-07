package nk.bluefrog.rythusevaoffice.activities.slidemenu;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import nk.bluefrog.rythusevaoffice.R;

public class GPAdapter extends RecyclerView.Adapter<GPAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> gpList;
    private ArrayList<String> farmerCountList;



    public GPAdapter(Context context,ArrayList<String> gpList,ArrayList<String> farmerCountList) {

        this.context= context;
        this.farmerCountList=farmerCountList;
        this.gpList=gpList;


    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_item_gp,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        viewHolder.gpName.setText(gpList.get(i));
        viewHolder.farmersCount.setText(farmerCountList.get(i));



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

            gpName = itemView.findViewById(R.id.tvGpName);
            farmersCount = itemView.findViewById(R.id.tvFarmerCount);



        }
    }
}
