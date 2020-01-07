package nk.bluefrog.rythusevaoffice.activities.slidemenu;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import nk.bluefrog.library.BluefrogActivity;
import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;
import nk.bluefrog.rythusevaoffice.utils.DBTables;
import nk.bluefrog.rythusevaoffice.utils.SpacesItemDecoration;
import nk.mobileapps.spinnerlib.SearchableSpinner;
import nk.mobileapps.spinnerlib.SpinnerData;

public class MAOMyMandalActivity extends BluefrogActivity implements SearchableSpinner.SpinnerListener {

    SearchableSpinner sp_mandal;
    DBHelper dbHelper;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_mandal);
        setToolBar(getString(R.string.mao_menu_my_cluster), "");
        dbHelper = new DBHelper(this);
        findViews();
    }

    private void findViews() {
        sp_mandal = (SearchableSpinner) findViewById(R.id.sp_mandal);
        sp_mandal.setItems(loadMandals(), this);
        recyclerView = findViewById(R.id.rvGP);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SpacesItemDecoration(10));

    }

    private List<SpinnerData> loadMandals() {
        List<SpinnerData> mandalData = new ArrayList<>();
        List<List<String>> gpData = dbHelper.getMandalIds();
        if (gpData.size() > 0) {
            for (int i = 0; i < gpData.size(); i++) {
                SpinnerData data = new SpinnerData();
                data.setId(gpData.get(i).get(0));
                data.setName(gpData.get(i).get(1));
                mandalData.add(data);

            }
        }

        return mandalData;
    }


    @Override
    public void onItemsSelected(View view, List<SpinnerData> list, int i) {
        loadMandalCount(sp_mandal.getSelectedItem().get(0).getId());
    }

    private void loadMandalCount(String id) {
        ArrayList<String> gpList = new ArrayList<>();
        ArrayList<String> farmerCountList = new ArrayList<>();
        String query = "Select distinct " + DBTables.FarmerTable.GP_ID + "," + DBTables.FarmerTable.GP_NAME +
                " from " + DBTables.FarmerTable.TABLE_NAME + " where " + DBTables.FarmerTable.MANDAL_ID + " = " + "'" + id + "'" + " order by " + DBTables.FarmerTable.GP_NAME;
        List<List<String>> gpData = dbHelper.getDataByQuery(query);

        if (gpData.size() > 0) {
            for (int i = 0; i < gpData.size(); i++) {
                gpList.add(gpData.get(i).get(1));
                int farmersCount = dbHelper.getCountByValues(DBTables.FarmerTable.TABLE_NAME, new String[]{DBTables.FarmerTable.GP_ID}, new String[]{gpData.get(i).get(0)});
                farmerCountList.add(String.valueOf(farmersCount));

            }
        }

        GPAdapter adapter = new GPAdapter(this, gpList, farmerCountList);
        recyclerView.setAdapter(adapter);
    }
}
