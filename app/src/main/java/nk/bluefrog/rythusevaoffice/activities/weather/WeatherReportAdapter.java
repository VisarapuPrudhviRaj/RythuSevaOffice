package nk.bluefrog.rythusevaoffice.activities.weather;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import nk.bluefrog.rythusevaoffice.R;


/**
 * Created by rajkumar on 22/11/18.
 */

public class WeatherReportAdapter extends RecyclerView.Adapter<WeatherReportAdapter.MyViewHolder> {

    Context mContext;
    List<WeatherModel> weatherModelList;

    public WeatherReportAdapter(Context mContext, List<WeatherModel> weatherModelList) {
        this.mContext = mContext;
        this.weatherModelList = weatherModelList;
    }

    @NonNull
    @Override
    public WeatherReportAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.weather_item_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherReportAdapter.MyViewHolder holder, int position) {

        WeatherModel model = weatherModelList.get(position);
        holder.tv_date.setText(model.getDate());
        holder.tv_temp.setText(model.getTemp());
        holder.tv_wind.setText(model.getWind());
        holder.tv_rain.setText(model.getRain());
    }

    @Override
    public int getItemCount() {
        return weatherModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_date, tv_temp, tv_wind, tv_rain;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv_date = itemView.findViewById(R.id.tv_date);
            tv_temp = itemView.findViewById(R.id.tv_temp);
            tv_wind = itemView.findViewById(R.id.tv_wind);
            tv_rain = itemView.findViewById(R.id.tv_rain);
        }
    }
}
