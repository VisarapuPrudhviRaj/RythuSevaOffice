package nk.bluefrog.rythusevaoffice.activities.mycluster;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import nk.bluefrog.library.BluefrogActivity;
import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.models.Gp;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;
import nk.bluefrog.rythusevaoffice.utils.DBTables;
import nk.bluefrog.rythusevaoffice.utils.SpacesItemDecoration;

public class MyCluster extends BluefrogActivity {

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_cluster);
        setToolBar(getString(R.string.menu_my_cluster), "");
        initViews();
    }

    private void initViews() {

        dbHelper = new DBHelper(this);
        TextView tvFarmerCount = findViewById(R.id.tvFarmerCount);
        RecyclerView recyclerView = findViewById(R.id.rv_list);

        tvFarmerCount.setText(String.valueOf(dbHelper.getCount(DBTables.FarmerTable.TABLE_NAME)));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new SpacesItemDecoration(16));

        ArrayList<Gp> gpList = new ArrayList<>();

        List<List<String>> gpData = dbHelper.getDataByQuery("Select distinct " + DBTables.FarmerTable.GP_ID + "," +
                DBTables.FarmerTable.GP_NAME +" from " +DBTables.FarmerTable.TABLE_NAME + " order by " + DBTables.FarmerTable.GP_NAME);

        if (gpData.size() > 0) {
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
