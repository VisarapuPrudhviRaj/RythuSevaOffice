package nk.bluefrog.rythusevaoffice.activities.seeds;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;


import java.util.Vector;

import nk.bluefrog.library.BluefrogActivity;
import nk.bluefrog.rythusevaoffice.R;


public class Seed_Register extends BluefrogActivity implements AdapterView.OnItemSelectedListener {
    Spinner spn_Category, spn_Variety;
    ArrayAdapter<String> adp_Category, adp_Variety;

    String[] CropArr = new String[]{"Select", "Red gram", "Green gram", "Black gram", "Korra", "Sessamum/Gingelly", "Soybean", "Ragi", "Daincha", "Sunhemp",
            "Pillipesara", "Bengalgram", "Bengalgram", "Paddy", "Pvt.Hybrids"},
            CropIds = new String[]{"00", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15"},

    VarietyArr = new String[]{"LRG41 ICPL87119", "Hyb ICPH2740", "LGG460", "LBG752  PU31", "SIA3085", "YLM17 YLM66", "JS335", "Sri Chaitanya", "Local",
            "Local", "Local", "JG11", "NBEG3", "ADT37 ADT39", "BPT5204", "BPT3291", "CR1009", "Jyothi MattaTriveni UMA", "MTU 1001",
            "MTU 1010", "MTU 1075", "MTU 1061", "MTU 1121", "MTU 7029", "MTU1064", "NLR145", "NLR 30491", "NLR 34449",
            "RP Bio226 RNR15048", "RGL2537", "Maize", "Jowar", "Bajra", "Castor", "Sunflower"},
            VarietyIDs = new String[]{"0201", "0202", "0301", "0401", "0501", "0601", "0701", "0801", "0901", "1001", "1101", "1201", "1301", "1401",
                    "1402", "1403", "1404", "1405", "1406", "1407", "1408", "1409", "1410", "1411", "1412", "1413", "1414", "1415", "1416", "1417",
                    "1501", "1502", "1503", "1504", "1505"};

    int index = 1;
    String[] labels;
    TextView tv_ShopName, tv_LicenceNo, tv_Mobile, tv_Address, tv_Category, tv_Variety, tv_Quantity, tv_Price, tv_Image, tv_GPS;
    Vector<String> vec_Variety = new Vector<String>();
    Vector<String> vec_VarietyId = new Vector<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seed_register);
        setToolBar(getString(R.string.seedRegistration), "");
        if (index == 1) {
            labels = getResources().getStringArray(R.array.Eng_Seed_Label);
        } else {
            labels = getResources().getStringArray(R.array.Tel_Seed_Label);
        }

        spn_Category = (Spinner) findViewById(R.id.spn_Category);
        spn_Variety = (Spinner) findViewById(R.id.spn_Variety);

        adp_Category = new ArrayAdapter<String>(Seed_Register.this, R.layout.support_simple_spinner_dropdown_item, CropArr);
        adp_Category.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_Category.setAdapter(adp_Category);
        spn_Category.setOnItemSelectedListener(this);

        adp_Variety = new ArrayAdapter<String>(Seed_Register.this, R.layout.support_simple_spinner_dropdown_item);
        adp_Variety.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_Variety.setAdapter(adp_Variety);

        tv_ShopName = (TextView) findViewById(R.id.tv_shopName);
        tv_LicenceNo = (TextView) findViewById(R.id.tv_LicenceNo);
        tv_Mobile = (TextView) findViewById(R.id.tv_Mobile);
        tv_Address = (TextView) findViewById(R.id.tv_Address);
        tv_Category = (TextView) findViewById(R.id.tv_Category);
        tv_Variety = (TextView) findViewById(R.id.tv_Variety);
        tv_Quantity = (TextView) findViewById(R.id.tv_Quantity);
        tv_Price = (TextView) findViewById(R.id.tv_Price);
        tv_Image = (TextView) findViewById(R.id.tv_Image);
        tv_GPS = (TextView) findViewById(R.id.tv_GPS);

        tv_ShopName.setText(labels[0]);
        tv_LicenceNo.setText(labels[1]);
        tv_Mobile.setText(labels[2]);
        tv_Address.setText(labels[3]);
        tv_Category.setText(labels[4]);
        tv_Variety.setText(labels[5]);
        tv_Quantity.setText(labels[6]);
        tv_Price.setText(labels[7]);
        tv_Image.setText(labels[8]);
        tv_GPS.setText(labels[9]);
    }

    @Override
    public void onItemSelected(AdapterView<?> view, View arg, int i, long l) {
        if (view == spn_Category) {
            if (spn_Category.getSelectedItemPosition() != 0) {
                vec_VarietyId.clear();
                vec_Variety.clear();
                vec_Variety.add("Select");
                vec_VarietyId.add("Select");
                String CategoryID = CropIds[spn_Category.getSelectedItemPosition()].trim();

                for (int j = 0; j < VarietyIDs.length; j++) {
                    if (VarietyIDs[j].startsWith(CategoryID)) {
                        vec_VarietyId.add(VarietyIDs[j]);
                        vec_Variety.add(VarietyArr[j]);
                    }
                }

                adp_Variety = new ArrayAdapter<String>(Seed_Register.this, R.layout.support_simple_spinner_dropdown_item, vec_Variety);
                adp_Variety.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spn_Variety.setAdapter(adp_Variety);

            } else {
                adp_Variety = new ArrayAdapter<String>(Seed_Register.this, R.layout.support_simple_spinner_dropdown_item);
                adp_Variety.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                adp_Variety.add("Select");
                spn_Variety.setAdapter(adp_Variety);

            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
