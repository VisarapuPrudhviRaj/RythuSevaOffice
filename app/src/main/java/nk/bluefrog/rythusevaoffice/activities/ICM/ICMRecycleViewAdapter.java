package nk.bluefrog.rythusevaoffice.activities.ICM;

/**
 * Created by nagendra on 1/3/17.
 */

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import nk.bluefrog.library.utils.Helper;
import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;


public class ICMRecycleViewAdapter extends RecyclerView.Adapter<ICMRecycleViewAdapter.ViewHolder> {
    ArrayList<ICMClass> al;
    DBHelper dbHelper;
    private Context context;


    public ICMRecycleViewAdapter(Context context, ArrayList<ICMClass> al) {
        this.context = context;
        this.al = al;
        dbHelper = new DBHelper(context);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_replymessages, viewGroup, false);
        ViewHolder vh = new ViewHolder(view);


        return vh;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        final ViewHolder holder1 = (ViewHolder) viewHolder;

        holder1.tv_message.setText(Html.fromHtml(al.get(i).getReplyMessage()));
        //holder1.tv_time.setText(Html.fromHtml("<font color='#993333' > <b>" + "</b></font>" + al.get(i).getLastUpdated()));

        String dateStart = al.get(i).getLastUpdated();
        String dateStop = Helper.getTodayDateTime();

        //HH converts hour in 24 hours format (0-23), day calculation
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = format.parse(dateStart);
            d2 = format.parse(dateStop);

            //in milliseconds
            long diff = d2.getTime() - d1.getTime();

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            System.out.print(diffDays + " days, ");
            System.out.print(diffHours + " hours, ");
            System.out.print(diffMinutes + " minutes, ");
            System.out.print(diffSeconds + " seconds.");

            holder1.tv_time.setText(Html.fromHtml(diffDays + " d " + diffHours + " h "
                    + diffMinutes + " m "));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return al.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll;
        private TextView tv_message, tv_time;


        public ViewHolder(View view) {
            super(view);
            ll = (LinearLayout) view.findViewById(R.id.ll);
            tv_message = (TextView) view.findViewById(R.id.tv_message);
            tv_time = (TextView) view.findViewById(R.id.tv_time);
        }
    }

}
