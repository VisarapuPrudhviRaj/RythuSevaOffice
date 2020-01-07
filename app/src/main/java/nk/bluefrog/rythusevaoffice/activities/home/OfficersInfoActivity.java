package nk.bluefrog.rythusevaoffice.activities.home;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import nk.bluefrog.library.BluefrogActivity;
import nk.bluefrog.library.network.ResponseListener;
import nk.bluefrog.library.network.RestServiceWithVolle;
import nk.bluefrog.library.utils.Helper;
import nk.bluefrog.library.utils.PrefManger;
import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.activities.farmercommunication.OfficerListAdapter;
import nk.bluefrog.rythusevaoffice.activities.farmercommunication.OfficerListModel;
import nk.bluefrog.rythusevaoffice.utils.Constants;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;
import nk.bluefrog.rythusevaoffice.utils.DBTables;
import nk.mobileapps.spinnerlib.SearchableSpinner;
import nk.mobileapps.spinnerlib.SpinnerData;

import static nk.bluefrog.library.network.ResponseListener.POST;

public  class OfficersInfoActivity extends BluefrogActivity implements SearchableSpinner.SpinnerListener {

    SearchableSpinner mandalSpnr;
    RecyclerView mandalOfficersList;
    //String mandalItemsId,mandalItemsNames;
    DBHelper dbHelper;
    ArrayList<OfficerListModel> officerList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.officers_info);
        dbHelper = new DBHelper(this);
        initViews();
    }

    private void initViews(){

        mandalSpnr = findViewById(R.id.mandal_spnr_officerinfo);
        mandalOfficersList = findViewById(R.id.officer_info_list);
        //mandalSpnr.setItems(dbHelper.getMandalIdsOnSelection(mandalItemsId));
        mandalSpnr.setItems(loadMandals());

    }

    public List<List<String>> getMandalIds(){

     List<List<String>> list  =  dbHelper.getDataByQuery("Select distinct " + DBTables.FarmerTable.MANDAL_ID + ","+   DBTables.FarmerTable.MANDAL_NAME+ " from  " + DBTables.FarmerTable.TABLE_NAME + " where " + DBTables.FarmerTable.MANDAL_ID  + " order by " + DBTables.FarmerTable.MANDAL_NAME);


            return  list;

    }

    public List<SpinnerData> loadMandals(){

        List<SpinnerData> mandalTypeData = new ArrayList<>();

        SpinnerData spnrMandalData = new SpinnerData();
        spnrMandalData.setId("0");
        spnrMandalData.setName("All");
        mandalTypeData.add(spnrMandalData);
        String MandalList = getMandalIds().toString();
            for(int i = 0; i<getMandalIds().size(); i++){

                SpinnerData spinnerData = new SpinnerData();
                spinnerData.setId(getMandalIds().get(i).get(0));
                spinnerData.setName(getMandalIds().get(i).get(1));
                mandalTypeData.add(spinnerData);
            }

            return mandalTypeData;
    }










    @Override
    public void onItemsSelected(View view, List<SpinnerData> list, int i) {


        getOfficersList();
    }


        public void getOfficersList(){

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("MobileNo", PrefManger.getSharedPreferencesString(this, Constants.sp_mobile, ""));
                jsonObject.put("mandalId", mandalSpnr.getSelectedId());
                jsonObject.put("type", 3);


            }catch (JSONException e) {
                e.printStackTrace();

            }

                showProgressDialog(getString(R.string.please_wait_submitting));

                final String keys[] = {"data"};
                final String values[] = {jsonObject.toString()};

                RestServiceWithVolle soapService = new RestServiceWithVolle(this, (ResponseListener) this, 1, Constants.BASE_URL
                        + Constants.METHOD_FARMER_COMM, POST, false);

                soapService.loadRequest(jsonObject);



    }



    public void onSuccess(int responseCode, String strResponse) {
        Constants.closeDottedProgress();
        try {
            JSONObject jobj = new JSONObject(strResponse);
            if (jobj.getString("Status").contains("$200")) {


              JSONArray officerArray =  jobj.getJSONArray("data");

                for(int i = 0; i<officerArray.length(); i++){
                    JSONObject officerObject = officerArray.getJSONObject(i);
                    OfficerListModel model = new OfficerListModel();
                    model.setOfficerName(officerObject.getString("officerName"));
                    model.setOfficerPhone(officerObject.getString("officerPhone"));

                }



            } else {

                Helper.showToast(OfficersInfoActivity.this, getString(R.string.not_submit));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public void onError(int responseCode, String error) {

        OfficerListAdapter adapter = new OfficerListAdapter(OfficersInfoActivity.this, officerList);
        mandalOfficersList.setAdapter(adapter);

        Helper.showToast(OfficersInfoActivity.this, error);

    }

}
