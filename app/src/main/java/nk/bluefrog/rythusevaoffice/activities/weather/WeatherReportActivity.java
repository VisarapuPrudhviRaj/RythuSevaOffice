package nk.bluefrog.rythusevaoffice.activities.weather;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nk.bluefrog.library.BluefrogActivity;
import nk.bluefrog.library.network.ResponseListener;
import nk.bluefrog.library.network.RestService;
import nk.bluefrog.library.network.RestServiceWithVolle;
import nk.bluefrog.library.utils.Helper;
import nk.bluefrog.library.utils.PrefManger;
import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.utils.Constants;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;
import nk.bluefrog.rythusevaoffice.utils.DBTables;
import nk.mobileapps.spinnerlib.SearchableSpinner;
import nk.mobileapps.spinnerlib.SpinnerData;

public class WeatherReportActivity extends BluefrogActivity implements SearchableSpinner.SpinnerListener {

    SearchableSpinner sp_mandal;
    RecyclerView recycler_view;
    TextView tv_nodata;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_report);

        setToolBar(getString(R.string.weather_report), "");
        findView();

    }

    private void findView() {
        sp_mandal = (SearchableSpinner) findViewById(R.id.sp_mandal);
        recycler_view = findViewById(R.id.recycler_view);
        tv_nodata = findViewById(R.id.tv_nodata);
        dbHelper = new DBHelper(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recycler_view.setLayoutManager(layoutManager);

        loadMandalData();
    }

    private void loadMandalData() {

        String dist = Helper.readTextFile(getApplicationContext(), R.raw.mandalnames);
        String distString[] = dist.split("\\|");
        List<SpinnerData> ll_mandal = new ArrayList<>();
        for (int i = 0; i < distString.length; i++) {
            String splitD[] = distString[i].split("\\^");
            SpinnerData spinnerData = new SpinnerData();
            spinnerData.setId(splitD[0]);
            spinnerData.setName(splitD[1]);
            ll_mandal.add(spinnerData);
        }

        sp_mandal.setItems(ll_mandal, this);
        sp_mandal.setItemPosition(0);
        sp_mandal.setItemID(dbHelper.getTableColData(DBTables.MPOTable.TABLE_NAME,DBTables.MPOTable.MID).get(0).get(0));
        sp_mandal.setEnabled(false);

    }

    @Override
    public void onItemsSelected(View view, List<SpinnerData> list, int position) {

        if (position != -1) {
            serverHit(sp_mandal.getSelectedItem().get(0).getName());
        }

    }

    private void serverHit(String name) {
        if (Helper.isNetworkAvailable(this)) {
           // Constants.showDottedProgress(this, getString(R.string.wait));
            temperatureServerHit(name);

        } else {
            Helper.showToast(this, getString(R.string.no_internet_available));
        }
    }

    private void temperatureServerHit(final String name) {
        String weather_date = Constants.getTodayDate();
        String nexttendaysdate = Constants.getFurtureDate(getApplicationContext(), 20);

        RestService temperatureService = new RestService(this, new ResponseListener() {
            @Override
            public void onSuccess(int responseCode, String response) {
                PrefManger.putSharedPreferencesString(getApplicationContext(), Constants.sp_weather_temp, Constants.getTodayDate() + "|" + response);
                Log.e("", "onSuccess: " + response);
                windServerHit(name);

            }

            @Override
            public void onError(int responseCode, String error) {
                Log.e("error", "onError: " + error);
                Constants.closeDottedProgress();
                recycler_view.setVisibility(View.GONE);
                tv_nodata.setVisibility(View.VISIBLE);

            }
        }, 1,
                Constants.url_weather + name + "/" + weather_date + "/" + nexttendaysdate + "/temperature",
                ResponseListener.GET, true);
        temperatureService.execute();
    }

    private void windServerHit(final String name) {
        String weather_date = Constants.getTodayDate();
        String nexttendaysdate = Constants.getFurtureDate(getApplicationContext(), 20);

        RestService windservice = new RestService(this, new ResponseListener() {
            @Override
            public void onSuccess(int responseCode, String response) {
                PrefManger.putSharedPreferencesString(getApplicationContext(), Constants.sp_weather_wind, Constants.getTodayDate() + "|" + response);
                Log.e("", "onSuccess: " + response);
                rainServerHit(name);

            }

            @Override
            public void onError(int responseCode, String error) {
                Log.e("error", "onError: " + error);
                Constants.closeDottedProgress();
                recycler_view.setVisibility(View.GONE);
                tv_nodata.setVisibility(View.VISIBLE);


            }
        }, 2,
                Constants.url_weather + name + "/" + weather_date + "/" + nexttendaysdate + "/windspeed",
                ResponseListener.GET, true);
        windservice.execute();
    }

    private void rainServerHit(String name) {
        String weather_date = Constants.getTodayDate();
        String nexttendaysdate = Constants.getFurtureDate(getApplicationContext(), 20);

        RestService rainService = new RestService(this, new ResponseListener() {
            @Override
            public void onSuccess(int responseCode, String response) {
                PrefManger.putSharedPreferencesString(getApplicationContext(), Constants.sp_weather_rain, Constants.getTodayDate() + "|" + response);
                Log.e("", "onSuccess: " + response);
                //Constants.closeDottedProgress();
                setWeatherData();


            }

            @Override
            public void onError(int responseCode, String error) {
                Log.e("error", "onError: " + error);
                Constants.closeDottedProgress();
                recycler_view.setVisibility(View.GONE);
                tv_nodata.setVisibility(View.VISIBLE);

            }
        }, 3,
                Constants.url_weather + name + "/" + weather_date + "/" + nexttendaysdate + "/rainfall",
                ResponseListener.GET, true);

        rainService.execute();
    }

    private void setWeatherData() {


        try {

            JSONObject jsonObjectTemp = new JSONObject(PrefManger.getSharedPreferencesString(this, Constants.sp_weather_temp, "").trim().split("\\|")[1]);
            JSONObject jsonObjectWind = new JSONObject(PrefManger.getSharedPreferencesString(this, Constants.sp_weather_rain, "").trim().split("\\|")[1]);
            JSONObject jsonObjectRain = new JSONObject(PrefManger.getSharedPreferencesString(this, Constants.sp_weather_wind, "").trim().split("\\|")[1]);

            List<WeatherModel> weatherModels = new ArrayList<>();
            for (int i = 0; i < jsonObjectTemp.length(); i++) {
                String date = Constants.getFurtureDate(WeatherReportActivity.this, i);
                weatherModels.add(getDateWiseWeather(date,
                        jsonObjectTemp.getJSONObject(date), jsonObjectWind.getString(date), jsonObjectRain.getString(date)));

            }
            //Call Adapter

            WeatherReportAdapter adapter = new WeatherReportAdapter(WeatherReportActivity.this, weatherModels);
            recycler_view.setAdapter(adapter);


        } catch (Exception e) {
            recycler_view.setVisibility(View.GONE);
            tv_nodata.setVisibility(View.VISIBLE);
        }

    }

    private WeatherModel getDateWiseWeather(String date_str, JSONObject temp, String wind, String rain) {

        WeatherModel model = new WeatherModel();
        //Change Date Format

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM EEE");
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        try {
            Date date = format.parse(date_str);
            date_str = sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        model.setDate(date_str);

        DecimalFormat df = new DecimalFormat("#.#");
        try {
            String max_min = df.format(Double.parseDouble(temp.getString("MAX"))) + " ℃ " + "\n " + "(" + df.format(Double.parseDouble(temp.getString("MIN"))) + ")";
            model.setTemp(max_min);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        model.setWind(df.format(Double.parseDouble(wind)) + " Km/h");
        model.setRain(df.format(Double.parseDouble(rain)) + " mm");

        return model;
    }


    public void onClick_refresh(View view) {
        if (Helper.isNetworkAvailable(this)) {
           // Constants.showDottedProgress(this, getString(R.string.wait));
            temperatureServerHit(sp_mandal.getSelectedItem().get(0).getName());
        } else {
            Helper.showToast(this, getString(R.string.no_internet_available));
        }
    }
}
