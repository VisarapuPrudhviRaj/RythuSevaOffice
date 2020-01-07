package nk.bluefrog.rythusevaoffice.activities.mycluster;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import nk.bluefrog.library.BluefrogActivity;
import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.activities.slidemenu.FarmerAdapter;
import nk.bluefrog.rythusevaoffice.models.Farmer;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;
import nk.bluefrog.rythusevaoffice.utils.DBTables;
import nk.bluefrog.rythusevaoffice.utils.SpacesItemDecoration;
import nk.mobileapps.spinnerlib.SearchableSpinner;

public class FarmersInAVillageActivity extends BluefrogActivity  {

    private DBHelper dbHelper;
    private ArrayList<Farmer> farmersList;
    private EditText etSearch;
    private CardView cvSearch;
    private RecyclerView recyclerView;
    private SearchableSpinner gpSpinner;
    private List<List<String>> farmerData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmers_village);
        setToolBar(getString(R.string.title_my_farmers),getIntent().getStringExtra("gpName"));

        initViews();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                filterResults(charSequence);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }


    private void initViews() {

        dbHelper = new DBHelper(this);

        recyclerView = findViewById(R.id.rvFarmers);
        etSearch = findViewById(R.id.et_search_farmers);
        cvSearch = findViewById(R.id.cv_search);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SpacesItemDecoration(10));

        cvSearch.setVisibility(View.GONE);

        setFarmersData(getIntent().getStringExtra("gpId"));


    }

    /* To filter farmers with either Name or Aadhar number or Mobile Number*/
    private void filterResults(CharSequence keyword) {

        if(farmerData.size()>0){

            farmersList = new ArrayList<>();

            for (int i = 0; i <farmerData.size() ; i++) {

                if(farmerData.get(i).get(0).toLowerCase().contains(keyword.toString().toLowerCase())){

                    Farmer farmer = new Farmer();

                    farmer.setName(farmerData.get(i).get(0));
                    farmer.setAadhar(farmerData.get(i).get(1));
                    farmer.setMobile(farmerData.get(i).get(2));
                    farmer.setImage(farmerData.get(i).get(3));
                    farmersList.add(farmer);

                }else if(farmerData.get(i).get(1).toLowerCase().contains(keyword.toString().toLowerCase())){
                    Farmer farmer = new Farmer();

                    farmer.setName(farmerData.get(i).get(0));
                    farmer.setAadhar(farmerData.get(i).get(1));
                    farmer.setMobile(farmerData.get(i).get(2));
                    farmer.setImage(farmerData.get(i).get(3));

                    farmersList.add(farmer);
                }else if(farmerData.get(i).get(2).toLowerCase().contains(keyword.toString().toLowerCase())){
                    Farmer farmer = new Farmer();

                    farmer.setName(farmerData.get(i).get(0));
                    farmer.setAadhar(farmerData.get(i).get(1));
                    farmer.setMobile(farmerData.get(i).get(2));
                    farmer.setImage(farmerData.get(i).get(3));

                    farmersList.add(farmer);
                }

            }

            FarmerAdapter adapter = new FarmerAdapter(FarmersInAVillageActivity.this,farmersList);
            recyclerView.setAdapter(adapter);

        }

    }


    private void setFarmersData(String gpId) {

        farmersList = new ArrayList<>();

        farmerData = dbHelper.getTableColDataByCond(DBTables.FarmerTable.TABLE_NAME
                ,DBTables.FarmerTable.NAME+","+DBTables.FarmerTable.AADHAR+","+DBTables.FarmerTable.MOBILE+","+DBTables.FarmerTable.IMAGE_URL
                ,new String []{DBTables.FarmerTable.GP_ID},new String []{gpId});

        if(farmerData.size()>0){

            cvSearch.setVisibility(View.VISIBLE);

            for (int j = 0; j <farmerData.size() ; j++) {

                Farmer farmer = new Farmer();

                farmer.setName(farmerData.get(j).get(0));
                farmer.setAadhar(farmerData.get(j).get(1));
                farmer.setMobile(farmerData.get(j).get(2));
                farmer.setImage(farmerData.get(j).get(3));

                farmersList.add(farmer);

            }

        }

        FarmerAdapter adapter = new FarmerAdapter(FarmersInAVillageActivity.this,farmersList);
        recyclerView.setAdapter(adapter);


    }
}
