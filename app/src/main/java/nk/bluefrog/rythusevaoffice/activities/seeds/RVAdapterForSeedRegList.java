package nk.bluefrog.rythusevaoffice.activities.seeds;

/**
 * Created by nagendra on 1/3/17.
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

import nk.bluefrog.library.utils.Helper;
import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;
import nk.bluefrog.rythusevaoffice.utils.DBTables;


public class RVAdapterForSeedRegList extends RecyclerView.Adapter<RVAdapterForSeedRegList.ViewHolder> {
    ArrayList<SeedClass> al;
    Context context;
    DBHelper dbHelper;


    public RVAdapterForSeedRegList(Context context, ArrayList<SeedClass> al) {
        this.context = context;
        this.al = al;
        dbHelper = new DBHelper(context);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_seed_regstrip, viewGroup, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        final ViewHolder holder = (ViewHolder) viewHolder;

        holder.tv_title.setText(al.get(i).getTypeofSeed().split("\\|")[1]);
        holder.tv_desc.setText(al.get(i).getSeedSubType().split("\\|")[1]);
        holder.tv_data.setText(Html.fromHtml("<font color='#993333' > <b>" + context.getString(R.string.price) + " : " + "</b></font>") + "₹ " + al.get(i).getCost() + "");


        String imagurl = al.get(i).getImage();
        if (imagurl != null && imagurl.contains(".")) {
            if (imagurl.contains("http")) {
                Glide.with(context).load(imagurl).into(holder.iv_image);
            } else {
                Glide.with(context).load((new File(imagurl))).into(holder.iv_image);
            }
        }

        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Helper.confirmDialog(context, context.getString(R.string.seed_name) + ":" + al.get(i).getTypeofSeed().split("\\|")[1] + "\n\n" + context.getString(R.string.del_ques), context.getString(R.string.yes), context.getString(R.string.no), new Helper.IL() {
                    @Override
                    public void onSuccess() {
                        dbHelper.deleteByValues(DBTables.SeedsRegister.TABLE_NAME, new String[]{DBHelper.UID}, new String[]{al.get(i).getUID()});
                        al.remove(i);
                        notifyItemRemoved(i);
                        Helper.showToast(context, context.getString(R.string.deleted));
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return al.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView iv_image;
        private TextView tv_title;
        private TextView tv_desc, tv_data, tv_uploadStatus;
        private ImageView iv_delete;


        public ViewHolder(View view) {
            super(view);
            iv_image = view.findViewById(R.id.iv_image);
            tv_title = view.findViewById(R.id.tv_title);
            tv_desc = view.findViewById(R.id.tv_desc);
            tv_data = view.findViewById(R.id.tv_data);
            tv_uploadStatus = view.findViewById(R.id.tv_uploadStatus);
            iv_delete = view.findViewById(R.id.iv_delete);
        }
    }

}
