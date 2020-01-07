package nk.bluefrog.rythusevaoffice.schedulemeeting;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import nk.bluefrog.library.BluefrogActivity;
import nk.bluefrog.library.network.ResponseListener;
import nk.bluefrog.library.network.RestServiceWithVolle;
import nk.bluefrog.library.utils.Helper;
import nk.bluefrog.library.utils.PrefManger;
import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.utils.CalendarView;
import nk.bluefrog.rythusevaoffice.utils.Constants;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;
import nk.bluefrog.rythusevaoffice.utils.DBTables;
import nk.mobileapps.spinnerlib.SearchableMultiSpinner;
import nk.mobileapps.spinnerlib.SpinnerData;

public class MAOScheduleMeetingsActivity extends BluefrogActivity implements ResponseListener, SearchableMultiSpinner.SpinnerListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mao_schedule_meetings);
        setToolBar(getString(R.string.title_schedule_meetings),"");
        initViews();
    }

    private CalendarView calendarView;
    private ArrayList<String> datesFromDb;

    private void initViews() {

        calendarView =findViewById(R.id.calendarView);
        calendarView.updateCalendar(getDatesFromDB());
        calendarView.setEventHandler(new CalendarView.EventHandler() {
            @Override
            public void onDayPress(String date, String key) {
                ArrayList<String> gpsFromDb = new ArrayList<>();
                datesFromDb = new ArrayList<>();
                List<List<String>> datesData = dbHelper.getTableColData(DBTables.ScheduleMeetings.TABLE_NAME
                        , DBTables.ScheduleMeetings.MEETING_DATE + "," + DBTables.ScheduleMeetings.GP_NAME);
                if (datesData.size() > 0) {
                    for (int i = 0; i < datesData.size(); i++) {
                        datesFromDb.add(datesData.get(i).get(0));
                        gpsFromDb.add(datesData.get(i).get(1));

                    }
                }

                Intent intent = new Intent(MAOScheduleMeetingsActivity.this,MAOReviewMeeting.class);
                intent.putExtra("date",date);
                startActivity(intent);

               /* if (!datesFromDb.contains(date)) {

                    int gpsCount = dbHelper.getCount(DBTables.GPTable.TABLE_NAME);
                    if (datesData.size() == gpsCount) {

                        Helper.showToast(ScheduleMeetingsActivity.this, getString(R.string.delete_schedule));

                    } else {
                        openScheduleDialog(date);
                    }

                } else {

                    Intent intent = new Intent(ScheduleMeetingsActivity.this,ReviewMeeting.class);

                    intent.putExtra("date",date);

                    startActivity(intent);

                   // openGpDataDialog(date, gpsFromDb.get(datesFromDb.indexOf(date)));

                }*/

            }


            @Override
            public void onNextClicked() {

                calendarView.updateCalendar(getDatesFromDB());

            }

            @Override
            public void onPreviousClicked() {
                calendarView.updateCalendar(getDatesFromDB());
            }
        }, new CalendarView.OnEventExist() {
            @Override
            public void onEvent(Date date,TextView event) {

                List<List<String>> gpNames = dbHelper.getTableColDataByCond(DBTables.ScheduleMeetings.TABLE_NAME,DBTables.ScheduleMeetings.GP_NAME,new String[]{DBTables.ScheduleMeetings.MEETING_DATE},new String[]{new SimpleDateFormat("dd/MM/yyyy", Locale.UK).format(date)});


                event.setText(gpNames.get(0).get(0));
            }
        });



    }


    public void submitSchedule(View view) {

        int gpsCount = dbHelper.getGpIds().size();
        /*Newly added*/


        if(dbHelper.getCount(DBTables.ScheduleMeetings.TABLE_NAME)!=gpsCount){
            Helper.showToast(this,getString(R.string.schedule_meeting_all));
        }else{

            if(Helper.isNetworkAvailable(this)){
                submitScheduleToServer();
            }else{
                Helper.AlertMesg(this,getString(R.string.no_network));
            }

        }



    }


    private void submitScheduleToServer() {

        final String keys[] = {"postString"};

        JSONObject sendingObject = new JSONObject();

        try {
            sendingObject.put(Constants.KEY_Mobile_No,PrefManger.getSharedPreferencesString(this,Constants.sp_mobile,""));
            sendingObject.put(Constants.KEY_REG_ID,PrefManger.getSharedPreferencesString(this,Constants.KEY_REG_ID,""));

            JSONArray gpArray = new JSONArray();


            List<List<String>> datesData = dbHelper.getTableColData(DBTables.ScheduleMeetings.TABLE_NAME
                    ,DBTables.ScheduleMeetings.MEETING_DATE+","+DBTables.ScheduleMeetings.GP_ID+","+DBTables.ScheduleMeetings.REMARKS);



            for (int i = 0; i <datesData.size() ; i++) {
                JSONObject gpObject = new JSONObject();

                gpObject.put("GPId",datesData.get(i).get(1));
                gpObject.put("DateOfMeeting",datesData.get(i).get(0));
                gpObject.put("Remarks",datesData.get(i).get(2));


                gpArray.put(gpObject);



            }

            sendingObject.put("GPData",gpArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String values[]={sendingObject.toString()};

        Constants.showDottedProgress(this, getString(R.string.wait));


        RestServiceWithVolle soapService = new RestServiceWithVolle(this,this,1,Constants.BASE_URL
                +Constants.METHOD_SCHEDULE_MEETING, POST,false);

        soapService.loadRequest(sendingObject);


    }



    private void openGpDataDialog(final String date,String gpName) {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete_gp);
        dialog.setCancelable(true);
        dialog.show();

        TextView title = dialog.findViewById(R.id.title_dialog_schedule);
        title.setText(date);
        TextView gp = dialog.findViewById(R.id.scheduledGp);

        gp.setText(String.format("Scheduled Gp :%s", gpName));


        Button delete = dialog.findViewById(R.id.btn_dlt_schedule);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHelper.deleteRowData(MAOScheduleMeetingsActivity.this,DBTables.ScheduleMeetings.TABLE_NAME,new String[]{DBTables.ScheduleMeetings.MEETING_DATE},new String[]{date});
                List<List<String>> datesData = dbHelper.getTableColData(DBTables.ScheduleMeetings.TABLE_NAME
                        ,DBTables.ScheduleMeetings.MEETING_DATE+","+DBTables.ScheduleMeetings.GP_NAME);


                calendarView.updateCalendar(getDatesFromDB());

                dialog.dismiss();
            }
        });
    }

    private Dialog dialog ;
    private DBHelper dbHelper;
    private String selectedVillageIds;
    private String selectedVillageNames;
    private EditText remarks;
    private String scheduleDate;

    private void openScheduleDialog(final String date) {

        scheduleDate = date;

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_schedule_gp);
        dialog.setCancelable(true);
        dialog.show();

        final SearchableMultiSpinner gpSpinner = dialog.findViewById(R.id.gpSpinner);

        TextView title = dialog.findViewById(R.id.title_dialog_schedule);
        title.setText(date);

        remarks = dialog.findViewById(R.id.remarks_schedule);

        Button save = dialog.findViewById(R.id.btn_save_schedule);

        final ArrayList<String> gpList= new ArrayList<>();
        final ArrayList<String> gpIdList= new ArrayList<>();
        ArrayList<SpinnerData> gpSpinnerData = new ArrayList<>();


        List<List<String>> gpData = dbHelper.getGpIds();

        if(gpData.size()>0){
            for (int i = 0; i <gpData.size() ; i++) {

                gpList.add(gpData.get(i).get(1));
                gpIdList.add(gpData.get(i).get(0));

                SpinnerData data = new SpinnerData();

                data.setId(gpData.get(i).get(0));
                data.setName(gpData.get(i).get(1));
                gpSpinnerData.add(data);

            }
        }

       /* List<List<String>> gpScheduledData = dbHelper.getTableColData(DBTables.ScheduleMeetings.TABLE_NAME,DBTables.ScheduleMeetings.GP_ID+","+DBTables.GPTable.NAME);

        if(gpScheduledData.size()>0){
            for (int i = 0; i <gpScheduledData.size() ; i++) {

                if(gpIdList.contains(gpScheduledData.get(i).get(0))){
                    int index = gpIdList.indexOf(gpScheduledData.get(i).get(0));

                    gpList.remove(index);
                    gpIdList.remove(index);

                    gpSpinnerData.remove(index);

                }

            }
        }
*/
        gpSpinner.setItems(gpSpinnerData,this);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(gpSpinner.getSelectedIds().size()>0){


                    for (int i = 0; i <gpSpinner.getSelectedIds().size() ; i++) {

                        if(i==0){
                            selectedVillageIds = gpSpinner.getSelectedItems().get(i).getId();
                            selectedVillageNames = gpSpinner.getSelectedItems().get(i).getName();
                        }else{
                            selectedVillageIds = String.format("%s,%s", selectedVillageIds, gpSpinner.getSelectedItems().get(i).getId());
                            selectedVillageNames = String.format("%s,%s", selectedVillageNames, gpSpinner.getSelectedItems().get(i).getName());

                        }


                    }


                    if(Helper.isNetworkAvailable(MAOScheduleMeetingsActivity.this)){
                        //submitScheduleToServer();

                        final String keys[] = {"postString"};

                        JSONObject sendingObject = new JSONObject();

                        try {
                            sendingObject.put(Constants.KEY_Mobile_No,PrefManger.getSharedPreferencesString(MAOScheduleMeetingsActivity.this,Constants.sp_mobile,""));
                            sendingObject.put(Constants.KEY_REG_ID,PrefManger.getSharedPreferencesString(MAOScheduleMeetingsActivity.this,Constants.KEY_REG_ID,""));

                            JSONArray gpArray = new JSONArray();

                            for (int i = 0; i <gpSpinner.getSelectedItems().size(); i++) {

                                JSONObject gpObject = new JSONObject();

                                gpObject.put("GPId",gpSpinner.getSelectedItems().get(i).getId());
                                gpObject.put("DateOfMeeting",date);
                                gpObject.put("Remarks",remarks.getText());

                                gpArray.put(gpObject);

                            }

                            sendingObject.put("GPData",gpArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        final String values[]={sendingObject.toString()};

                        Constants.showDottedProgress(MAOScheduleMeetingsActivity.this, getString(R.string.wait));


                        RestServiceWithVolle soapService = new RestServiceWithVolle(MAOScheduleMeetingsActivity.this, MAOScheduleMeetingsActivity.this,1,Constants.BASE_URL
                                + Constants.METHOD_SCHEDULE_MEETING, POST,false);

                        soapService.loadRequest(sendingObject);
                    }else{
                        Helper.AlertMesg(MAOScheduleMeetingsActivity.this,getString(R.string.no_network));
                    }




                }else{
                    Helper.showToast(MAOScheduleMeetingsActivity.this,getString(R.string.select_gp));
                }


                /*if(gpSpinner.getSelectedItemPosition()==-1){
                    Helper.showToast(ScheduleMeetingsActivity.this,getString(R.string.select_gp));
                }else{
                    dbHelper.insertintoTable(DBTables.ScheduleMeetings.TABLE_NAME
                            ,new String[]{DBTables.ScheduleMeetings.MEETING_DATE,DBTables.ScheduleMeetings.GP_ID,DBTables.ScheduleMeetings.GP_NAME,DBTables.ScheduleMeetings.REMARKS,DBTables.ScheduleMeetings.DATE}
                            ,new String[]{date,selSpinnerData.getId(),selSpinnerData.getName(),remarks.getText().toString(),new SimpleDateFormat("dd/MM/yyyy", Locale.UK).format(Calendar.getInstance().getTime())});

                    Helper.showToast(ScheduleMeetingsActivity.this,getString(R.string.saved));


                    calendarView.updateCalendar(getDatesFromDB());

                    dialog.dismiss();

                }*/
            }
        });


    }


    private HashSet<Date> getDatesFromDB(){

        dbHelper = new DBHelper(this);

        HashSet<Date> bDatesList  = new HashSet<>();
        datesFromDb=new ArrayList<>();

        List<List<String>> datesData = dbHelper.getTableColData(DBTables.ScheduleMeetings.TABLE_NAME
                ,DBTables.ScheduleMeetings.MEETING_DATE+","+DBTables.ScheduleMeetings.GP_NAME);

        if(datesData.size()>0){

            for (int i = 0; i <datesData.size() ; i++) {

                try {
                    bDatesList.add(new SimpleDateFormat("dd/MM/yyyy", Locale.UK).parse(datesData.get(i).get(0)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                datesFromDb.add(datesData.get(i).get(0));

            }
        }

        return bDatesList;

    }

    @Override
    public void onSuccess(int responseCode, String strResponse) {

        Constants.closeDottedProgress();

        try {
            JSONObject response = new JSONObject(strResponse);

            /*"Status":"$200"*/

            if(response.getString("Status").contentEquals("$200")){

                dbHelper.insertintoTable(DBTables.ScheduleMeetings.TABLE_NAME
                        ,new String[]{DBTables.ScheduleMeetings.MEETING_DATE,DBTables.ScheduleMeetings.GP_ID,DBTables.ScheduleMeetings.GP_NAME,DBTables.ScheduleMeetings.REMARKS,DBTables.ScheduleMeetings.DATE}
                        ,new String[]{scheduleDate,selectedVillageIds,selectedVillageNames,remarks.getText().toString(),new SimpleDateFormat("dd/MM/yyyy", Locale.UK).format(Calendar.getInstance().getTime())});

                Helper.showToast(MAOScheduleMeetingsActivity.this,getString(R.string.saved));


                calendarView.updateCalendar(getDatesFromDB());

                dialog.dismiss();
                Helper.showToast(MAOScheduleMeetingsActivity.this,getString(R.string.data_submited_success));

                finish();
            }else{
                dialog.dismiss();
                Helper.showToast(MAOScheduleMeetingsActivity.this,getString(R.string.insertion_failed));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(int responseCode, String error) {
        Helper.showToast(this,error);
    }

    SpinnerData selSpinnerData;

    @Override
    public void onItemsSelected(View view, List<SpinnerData> list, List<SpinnerData> list1) {

    }

    /*@Override
    public void onItemsSelected(View view, List<SpinnerData> list, int i) {

        if(i!=-1){
            selSpinnerData = list.get(i);
        }



    }*/


}
