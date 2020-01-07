package nk.bluefrog.rythusevaoffice.activities.farmercommunication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import nk.bluefrog.rythusevaoffice.R;

public class OfficerListAdapter extends RecyclerView.Adapter<OfficerListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<OfficerListModel> officerList;


    public OfficerListAdapter(Context context, ArrayList<OfficerListModel> officerList) {
        this.context = context;
        this.officerList = officerList;
    }





    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup,  final int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.officerlist_row, viewGroup, false);
        return new OfficerListAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final OfficerListModel officerListModel = officerList.get(position);
        holder.officerNameText.setText(officerListModel.getOfficerName());
        holder.officerPhoneText.setText(officerListModel.getOfficerPhone());

    }


    @Override
    public int getItemCount() {
        return officerList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

       private TextView officerNameText;
       private TextView officerPhoneText;


        public ViewHolder(View itemView) {
            super(itemView);

            officerNameText = itemView.findViewById(R.id.officer_name_text);
            officerPhoneText = itemView.findViewById(R.id.officer_phone_text);

        }
    }
}
