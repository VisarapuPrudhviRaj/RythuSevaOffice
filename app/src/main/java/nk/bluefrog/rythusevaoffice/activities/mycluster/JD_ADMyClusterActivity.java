package nk.bluefrog.rythusevaoffice.activities.mycluster;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import nk.bluefrog.library.BluefrogActivity;
import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;
import nk.bluefrog.rythusevaoffice.utils.DBTables;
import nk.mobileapps.spinnerlib.SearchableMultiSpinner;
import nk.mobileapps.spinnerlib.SpinnerData;

public class JD_ADMyClusterActivity extends BluefrogActivity implements SearchableMultiSpinner.SpinnerListener {

    SearchableMultiSpinner sp_mandal;
    TextView tv_nodata, tv_title;

    LinearLayout ll_main;
    DBHelper dbHelper;
    LayoutInflater layoutInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jd__admy_cluster);
        setToolBar(getString(R.string.mandals_in_my_cluster), "");
        dbHelper = new DBHelper(this);
        findViews();
    }

    private void findViews() {
        layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        sp_mandal = (SearchableMultiSpinner) findViewById(R.id.sp_mandal);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_nodata = (TextView) findViewById(R.id.tv_nodata);

        ll_main = (LinearLayout) findViewById(R.id.ll_main);

        sp_mandal.setItems(loadMandalData(), this);
        sp_mandal.setFixedCheckPostion("0");
    }

    private List<SpinnerData> loadMandalData() {
        List<SpinnerData> mandalData = new ArrayList<>();

        SpinnerData data = new SpinnerData();

        data.setId("0");
        data.setName("All");
        mandalData.add(data);

        List<List<String>> gpData = dbHelper.getMandalIds();
        if (gpData.size() > 0) {
            for (int i = 0; i < gpData.size(); i++) {
                data = new SpinnerData();
                data.setId(gpData.get(i).get(0));
                data.setName(gpData.get(i).get(1));
                mandalData.add(data);

            }
        }

        return mandalData;
    }

    @Override
    public void onItemsSelected(View view, List<SpinnerData> list, List<SpinnerData> list1) {
        if (list1.size() > 0) {
            tv_nodata.setVisibility(View.GONE);
            if (list1.get(0).getId().equals("0")) {
                getMandalData(list);
            } else {
                getMandalData(list1);
            }
        } else {
            tv_nodata.setVisibility(View.VISIBLE);
            ll_main.removeAllViews();
        }
    }

    private void getMandalData(List<SpinnerData> selectedItems) {

        if (selectedItems.size() > 0) {
            ll_main.removeAllViews();
            for (int i = 0; i < selectedItems.size(); i++) {

                if (!selectedItems.get(i).getName().equals("All")) {

                    final List<List<String>> gpData = dbHelper.getGpBasedonIdSelection("'" + selectedItems.get(i).getId() + "'");
                    View view = layoutInflater.inflate(R.layout.row_ad_jd_header_layout_cluster, null, false);

                    TextView tv_mandalName = view.findViewById(R.id.tv_mandalName);
                    TextView tv_nodata = view.findViewById(R.id.tv_nodata);
                    LinearLayout ll_addgpViews = view.findViewById(R.id.ll_header);

                    if (gpData.size() > 0) {
                        tv_nodata.setVisibility(View.GONE);
                        tv_mandalName.setText(gpData.get(0).get(2) + " (" + gpData.size() + ")");

                        for (int j = 0; j < gpData.size(); j++) {
                            View sub_view = layoutInflater.inflate(R.layout.row_item_ad_jd_header_layout_cluster, null, false);
                            final TextView tv_villagename = sub_view.findViewById(R.id.tv_villagename);
                            TextView tv_farmer_count = sub_view.findViewById(R.id.tv_farmer_count);
                            final LinearLayout layout_gp = sub_view.findViewById(R.id.layout_gp);
                            layout_gp.setTag(gpData.get(j).get(0));
                            tv_villagename.setText(gpData.get(j).get(1));
                            tv_farmer_count.setText(dbHelper.getCountByValue(DBTables.FarmerTable.TABLE_NAME,
                                    DBTables.FarmerTable.GP_ID, gpData.get(j).get(0)) + "");
                            layout_gp.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(JD_ADMyClusterActivity.this, FarmersInAVillageActivity.class);
                                    intent.putExtra("gpId", layout_gp.getTag().toString());
                                    intent.putExtra("gpName", tv_villagename.getText().toString());
                                    startActivity(intent);
                                }
                            });
                            ll_addgpViews.addView(sub_view);
                        }
                    } else {
                        tv_nodata.setVisibility(View.VISIBLE);
                        tv_mandalName.setText(selectedItems.get(i).getName() + " (0)");
                    }

                    ll_main.addView(view);
                }
            }


        } else {
            ll_main.removeAllViews();
        }

    }
}
