package nk.bluefrog.rythusevaoffice.activities.mycluster;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import nk.bluefrog.library.BluefrogActivity;
import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.models.Gp;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;
import nk.bluefrog.rythusevaoffice.utils.DBTables;
import nk.bluefrog.rythusevaoffice.utils.SpacesItemDecoration;
import nk.mobileapps.spinnerlib.SearchableSpinner;
import nk.mobileapps.spinnerlib.SpinnerData;

public class MAOMyCluster extends BluefrogActivity implements SearchableSpinner.SpinnerListener {

    SearchableSpinner sp_mandal;
    RecyclerView recyclerView;
    TextView textView;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mao_my_cluster);
        setToolBar(getString(R.string.mao_menu_my_cluster), "");
        initViews();
    }

    private void initViews() {

        dbHelper = new DBHelper(this);
        TextView tvFarmerCount = findViewById(R.id.tvFarmerCount);
        textView = findViewById(R.id.textView);
        recyclerView = findViewById(R.id.rv_list);
        sp_mandal = (SearchableSpinner) findViewById(R.id.sp_mandal);

        tvFarmerCount.setText(String.valueOf(dbHelper.getCount(DBTables.FarmerTable.TABLE_NAME)));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SpacesItemDecoration(16));


        sp_mandal.setItems(loadMandals(), this);


    }

    private List<SpinnerData> loadMandals() {

        List<SpinnerData> mandals = new ArrayList();
        List<List<String>> gpData = dbHelper.getMandalIds();
        if (gpData.size() > 0) {
            for (int i = 0; i < gpData.size(); i++) {
                SpinnerData data = data = new SpinnerData();
                data.setId(gpData.get(i).get(0));
                data.setName(gpData.get(i).get(1));
                mandals.add(data);

            }
        }

        return mandals;
    }

    @Override
    public void onItemsSelected(View view, List<SpinnerData> list, int i) {
        if (i != -1) {
            displayCountBasedOnMandal(sp_mandal.getSelectedId().get(0));
        }
    }

    private void displayCountBasedOnMandal(String selectedItemId) {
        ArrayList<Gp> gpList = new ArrayList<>();

        String query = "Select distinct " + DBTables.FarmerTable.GP_ID + "," +
                DBTables.FarmerTable.GP_NAME + " from " + DBTables.FarmerTable.TABLE_NAME
                + " where " + DBTables.FarmerTable.MANDAL_ID + " = " + "'" + selectedItemId + "'" + " order by " + DBTables.FarmerTable.GP_NAME;

        List<List<String>> gpData = dbHelper.getDataByQuery(query);

        if (gpData.size() > 0) {
            textView.setVisibility(View.VISIBLE);
            for (int i = 0; i < gpData.size(); i++) {
                Gp gp = new Gp();
                gp.setName(gpData.get(i).get(1));
                gp.setId(gpData.get(i).get(0));
                int farmersCount = dbHelper.getCountByValues(DBTables.FarmerTable.TABLE_NAME, new String[]{DBTables.FarmerTable.GP_ID}, new String[]{gpData.get(i).get(0)});
                gp.setFarmerCount(String.valueOf(farmersCount));
                gpList.add(gp);

            }
        }

        VillageAdapter adapter = new VillageAdapter(this, gpList);
        recyclerView.setAdapter(adapter);

    }
/*
    public void onBackPressed(View view) {

        onBackPressed();
    }*/


}
