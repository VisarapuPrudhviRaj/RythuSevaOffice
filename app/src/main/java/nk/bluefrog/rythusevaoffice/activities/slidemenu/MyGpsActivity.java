package nk.bluefrog.rythusevaoffice.activities.slidemenu;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import nk.bluefrog.library.BluefrogActivity;
import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;
import nk.bluefrog.rythusevaoffice.utils.DBTables;
import nk.bluefrog.rythusevaoffice.utils.SpacesItemDecoration;

public class MyGpsActivity extends BluefrogActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_gp);

        setToolBar(getString(R.string.my_villages),"");

        DBHelper dbHelper = new DBHelper(this);

        RecyclerView recyclerView = findViewById(R.id.rvGP);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SpacesItemDecoration(10));

        ArrayList<String> gpList= new ArrayList<>();
        ArrayList<String> farmerCountList= new ArrayList<>();

        List<List<String>> gpData = dbHelper.getDataByQuery("Select distinct "+ DBTables.FarmerTable.GP_ID+","+DBTables.FarmerTable.GP_NAME+
                " from "+DBTables.FarmerTable.TABLE_NAME+" order by "+DBTables.FarmerTable.GP_NAME);

        if(gpData.size()>0){
            for (int i = 0; i <gpData.size() ; i++) {

                gpList.add(gpData.get(i).get(1));

                int farmersCount = dbHelper.getCountByValues(DBTables.FarmerTable.TABLE_NAME,new String[]{DBTables.FarmerTable.GP_ID},new String[]{gpData.get(i).get(0)});

                farmerCountList.add(String.valueOf(farmersCount));

            }
        }

        GPAdapter adapter = new GPAdapter(this,gpList,farmerCountList);

        recyclerView.setAdapter(adapter);



    }


}
