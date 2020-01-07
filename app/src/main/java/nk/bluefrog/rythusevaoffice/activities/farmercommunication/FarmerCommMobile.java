package nk.bluefrog.rythusevaoffice.activities.farmercommunication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import nk.bluefrog.library.BluefrogActivity;
import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;
import nk.bluefrog.rythusevaoffice.utils.DBTables;


public class FarmerCommMobile extends BluefrogActivity {

    DBHelper dbHelper;
    RecyclerView rv_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer_comm_mobile);
        dbHelper = new DBHelper(this);
        setToolBar(getResources().getString(R.string.farmer_communication), getString(R.string.list));
        findViews();


    }

    private void findViews() {
        FloatingActionButton fab = findViewById(R.id.fab_add);
        rv_list = findViewById(R.id.rv_list);
        loadRecycleView();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.OFFICER_TYPE).equals("1")) {
                    startActivity(new Intent(FarmerCommMobile.this, FarmerCommunicationActivity.class));
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                } else if (dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.OFFICER_TYPE).equals("2")) {
                    startActivity(new Intent(FarmerCommMobile.this, MaoFarmerCommunicationActivity.class));
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                } else if (dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.OFFICER_TYPE).equals("3")) {
                    startActivity(new Intent(FarmerCommMobile.this, JDFarmerCommunicationActivity.class));
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }

            }
        });
    }

    private void loadRecycleView() {
        int count = dbHelper.getCount(DBTables.FarmerCommunication.TABLE_NAME);
        if (count > 0) {
            findViewById(R.id.tv_nodata).setVisibility(View.GONE);
            rv_list.setVisibility(View.VISIBLE);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            rv_list.setHasFixedSize(true);
            rv_list.setLayoutManager(linearLayoutManager);
            ArrayList<FarmerCommModel> commModelList = new ArrayList<>();
            List<List<String>> commData = dbHelper.getTableColData(DBTables.FarmerCommunication.TABLE_NAME
                    , DBTables.FarmerCommunication.TITLE + "," + DBTables.FarmerCommunication.DESCRIPTION
                            + "," + DBTables.FarmerCommunication.GP_NAME + "," + DBTables.FarmerCommunication.IMAGE_PATH
                            + "," + DBHelper.UID + "," + DBTables.FarmerCommunication.LINK + "," + DBTables.FarmerCommunication.VIDEO_LINK
                            + "," + DBTables.FarmerCommunication.DATE
                            + "," + DBTables.FarmerCommunication.DIST_NAME
                            + "," + DBTables.FarmerCommunication.MANDAL_NAME);

            for (int i = 0; i < commData.size(); i++) {

                FarmerCommModel commModel = new FarmerCommModel();
                commModel.setTitle(commData.get(i).get(0));
                commModel.setDescription(commData.get(i).get(1));
                commModel.setVillage(commData.get(i).get(2));
                commModel.setImagePath(commData.get(i).get(3));
                commModel.setUid(commData.get(i).get(4));
                commModel.setLink(commData.get(i).get(5));
                commModel.setVideoLink(commData.get(i).get(6));
                commModel.setDate(commData.get(i).get(7));
                commModel.setDistrict(commData.get(i).get(8));
                commModel.setMandal(commData.get(i).get(9));
                commModelList.add(commModel);
            }
            //rv_list.setLayoutManager(new GridLayoutManager(this, 2));
            FarmerCommunicationAdapter adapter = new FarmerCommunicationAdapter(FarmerCommMobile.this, commModelList);
            rv_list.setAdapter(adapter);
        } else {
            findViewById(R.id.tv_nodata).setVisibility(View.VISIBLE);
            rv_list.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecycleView();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
