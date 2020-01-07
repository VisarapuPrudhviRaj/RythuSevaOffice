package nk.bluefrog.rythusevaoffice.activities.equipments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import nk.bluefrog.library.BluefrogActivity;
import nk.bluefrog.library.camera.CameraActivity;
import nk.bluefrog.library.gps.GPSActivity;
import nk.bluefrog.library.network.ResponseListener;
import nk.bluefrog.library.network.RestServiceWithVolle;
import nk.bluefrog.library.utils.Helper;
import nk.bluefrog.library.utils.PrefManger;
import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.utils.Constants;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;
import nk.bluefrog.rythusevaoffice.utils.DBTables;
import nk.mobileapps.spinnerlib.SearchableSpinner;
import nk.mobileapps.spinnerlib.SpinnerData;

import static nk.bluefrog.library.network.ResponseListener.POST;


public class EquipmentRegistrationActivity extends BluefrogActivity implements SearchableSpinner.SpinnerListener {

    DBHelper dbHelper;
    SearchableSpinner spinnerdist, spinnerMandal, spinnervillage, spinnerfarmer, spinnerCategory, spinnerCategoryType;
    Spinner spinnerOwnerby, spinnerStatus;
    RadioGroup rdGroup_isfarmer, rdGroup;
    RadioButton rdbtnisfarmer_yes, rdbtnisfaremer_no, rdbtn_yes, rdbtn_no;

    EditText serial, et_modelName, et_manufactureName, et_yearmanufacture, et_specification, equip_rent, equip_rent_arc,
            et_ownername, address, mobile, et_Available_Date;

    ImageView img;
    TextView gps, barcode;
    Button bt_register;
    String imagePath;
    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };
    private int CAMERA_REQUEST = 101;
    private int GPS_REQUEST = 102;
    private int REQUEST_EQUIP_REG = 108;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_registration);
        setToolBar(getString(R.string.equipment_registration), "");
        dbHelper = new DBHelper(this);
        findview();
        loadDist();
        loadEuipCatData();
        loadData();

    }

    private void loadEuipCatData() {
        List<List<String>> db_cat = dbHelper.getDataByQuery("select distinct CID,CName from Category ");
        if (db_cat.size() > 0) {
            List<SpinnerData> ll_spinnerData = new ArrayList<>();
            for (int i = 0; i < db_cat.size(); i++) {
                SpinnerData catSpinnerData = new SpinnerData();
                catSpinnerData.setId(db_cat.get(i).get(0));
                catSpinnerData.setName(db_cat.get(i).get(1));
                ll_spinnerData.add(catSpinnerData);
            }

            spinnerCategory.setItems(ll_spinnerData, this);
            spinnerCategoryType.setItems(new ArrayList<SpinnerData>(), this);

        }
    }

    private void loadVillage() {

        List<List<String>> db_village = dbHelper.getGpIds();
        if (db_village.size() > 0) {
            List<SpinnerData> ll_dist = new ArrayList<>();
            for (int i = 0; i < db_village.size(); i++) {
                SpinnerData spinnerData = new SpinnerData();
                spinnerData.setId(db_village.get(i).get(0));
                spinnerData.setName(db_village.get(i).get(1));
                ll_dist.add(spinnerData);
            }
            spinnervillage.setItems(ll_dist, this);
            spinnerfarmer.setItems(new ArrayList<SpinnerData>(), this);


        }

    }

    public void loadDist() {
        String dist = Helper.readTextFile(getApplicationContext(), R.raw.dist);
        String distString[] = dist.split("\\\n");
        List<SpinnerData> ll_dist = new ArrayList<>();
        for (int i = 0; i < distString.length; i++) {
            String splitD[] = distString[i].split("\\^");
            SpinnerData spinnerData = new SpinnerData();
            spinnerData.setId(splitD[0]);
            spinnerData.setName(splitD[1]);
            ll_dist.add(spinnerData);
        }
        spinnerdist.setItems(ll_dist, this);
        spinnerMandal.setItems(new ArrayList<SpinnerData>(), this);
        spinnerfarmer.setItems(new ArrayList<SpinnerData>(), this);
        spinnervillage.setItems(new ArrayList<SpinnerData>(), this);


    }

    private void findview() {
        spinnerdist = (SearchableSpinner) findViewById(R.id.spinnerdist);
        spinnerMandal = (SearchableSpinner) findViewById(R.id.spinnerMandal);
        spinnervillage = (SearchableSpinner) findViewById(R.id.spinnervillage);
        spinnerfarmer = (SearchableSpinner) findViewById(R.id.spinnerfarmer);
        spinnerCategory = (SearchableSpinner) findViewById(R.id.spinnerCategory);
        spinnerCategoryType = (SearchableSpinner) findViewById(R.id.spinnerCategoryType);
        spinnerOwnerby = (Spinner) findViewById(R.id.spinnerOwnerby);
        spinnerStatus = (Spinner) findViewById(R.id.spinnerStatus);
        barcode = (TextView) findViewById(R.id.barcode);

        rdGroup_isfarmer = findViewById(R.id.rdGroup_isfarmer);
        rdGroup = findViewById(R.id.rdGroup);
        rdbtnisfarmer_yes = findViewById(R.id.rdbtnisfarmer_yes);
        rdbtnisfaremer_no = findViewById(R.id.rdbtnisfaremer_no);
        rdbtn_yes = findViewById(R.id.rdbtn_yes);
        rdbtn_no = findViewById(R.id.rdbtn_no);

        serial = (EditText) findViewById(R.id.serial);
        et_modelName = (EditText) findViewById(R.id.et_modelName);
        et_manufactureName = (EditText) findViewById(R.id.et_manufactureName);
        et_yearmanufacture = (EditText) findViewById(R.id.et_yearmanufacture);
        et_specification = (EditText) findViewById(R.id.et_specification);
        equip_rent = (EditText) findViewById(R.id.equip_rent);
        equip_rent_arc = (EditText) findViewById(R.id.equip_rent_arc);
        et_ownername = (EditText) findViewById(R.id.et_ownername);
//        et_aadhar = (EditText) findViewById(R.id.et_aadhar);
        address = (EditText) findViewById(R.id.address);
        mobile = (EditText) findViewById(R.id.mobile);
        et_Available_Date = (EditText) findViewById(R.id.et_Available_Date);
        img = (ImageView) findViewById(R.id.img);
        gps = (TextView) findViewById(R.id.gps);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EquipmentRegistrationActivity.this, GPSActivity.class);
                startActivityForResult(intent, GPS_REQUEST);
            }
        });

        rdGroup_isfarmer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (group == rdGroup_isfarmer) {
                    if (rdbtnisfaremer_no.isChecked()) {
                        spinnerfarmer.setVisibility(View.GONE);
                        ((TextView) findViewById(R.id.gpselectiontype)).setVisibility(View.GONE);
                    } else if (rdbtnisfarmer_yes.isChecked()) {
                        ((TextView) findViewById(R.id.gpselectiontype)).setVisibility(View.VISIBLE);
                        spinnerfarmer.setVisibility(View.VISIBLE);
                    }
                }
            }
        });


        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinnerStatus.getSelectedItemPosition() == 1) {
                    rdbtn_yes.setChecked(true);
                    rdbtn_no.setChecked(false);
                    ((TextInputLayout) findViewById(R.id.input_layout_Start_Date)).setVisibility(View.GONE);
                } else {
                    rdbtn_yes.setChecked(false);
                    rdbtn_no.setChecked(true);
                    ((TextInputLayout) findViewById(R.id.input_layout_Start_Date)).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        et_Available_Date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                // et_Start_Date.setText(Helper.getTodayDate());
                DatePickerDialog dialog = new DatePickerDialog(EquipmentRegistrationActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                dialog.show();
            }
        });

        mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (spinnerfarmer.getSelectedId().size() > 0 && spinnerfarmer.getSelectedItem().get(0).getId().equals("NEW")) {
                    if (s.toString().length() == 10) {
                        checkAdharFromDb(mobile.getText().toString().trim());
                    }
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_yearmanufacture.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() >= 4) {
                    getManfactureYear(et_yearmanufacture.getText().toString().trim());
                }
            }
        });


    }

    private boolean checkAdharFromDb(String adhar) {
        boolean status = true;
        final List<List<String>> db_adar = dbHelper.getTableColDataByCond(DBTables.FarmerTable.TABLE_NAME,
                DBTables.FarmerTable.NAME, new String[]{DBTables.FarmerTable.MOBILE},
                new String[]{adhar});

        if (db_adar.size() > 0) {
            Helper.AlertMsg(EquipmentRegistrationActivity.this, getString(R.string.already_exist_adhar_number) + db_adar.get(0).get(0));
            mobile.setText("");
        }

        return status;

    }

    private void updateLabel() {
//        yyyy-MM-dd
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        et_Available_Date.setText(sdf.format(myCalendar.getTime()));
    }

    private void openCamera() {
        Runtime.getRuntime().gc();
        Intent intent = new Intent(EquipmentRegistrationActivity.this, CameraActivity.class);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST) {
            if (resultCode == RESULT_OK) {
                File photoFile = null;
                photoFile = new File(data.getStringExtra(CameraActivity.RESULT_IMG_PATH));
                imagePath = data.getStringExtra(CameraActivity.RESULT_IMG_PATH);
                if (photoFile != null && !TextUtils.isEmpty(photoFile.getAbsolutePath())) {
                    img.setImageBitmap(Helper.getImageWithoutCrop(imagePath));
                    // img.setImageBitmap(BitmapFactory.decodeFile(photoFile.getAbsolutePath()));
                } else {
                    Helper.showToast(getApplicationContext(),
                            getResources().getString(R.string.photo_failed));
                }

            } else {
                Helper.showToast(getApplicationContext(),
                        getResources().getString(R.string.photo_failed));
            }
        } else if (requestCode == GPS_REQUEST) {
            if (resultCode == RESULT_OK) {
                String gpsString = data.getStringExtra(GPSActivity.LOC_DATA);
                gps.setText(gpsString);
                openCamera();
            } else {
                Helper.showToast(getApplicationContext(),
                        getResources().getString(R.string.gps_photo_failed));
            }
        }

    }

    private boolean isValidate() {
        boolean status = true;

        /*if (!rdbtnisfarmer_yes.isChecked() && !rdbtnisfaremer_no.isChecked()) {
            Helper.showToast(this, "Please Select Is Farmer Exist");
            Helper.setViewFocus(rdGroup_isfarmer);
            status = false;
        } else*/

        if (spinnervillage.getSelectedId().size() == 0) {
            Helper.showToast(this, getString(R.string.please_select_village));
            Helper.setViewFocus(spinnervillage);
            status = false;
        } else if (spinnerfarmer.getSelectedId().size() == 0) {
            Helper.showToast(this, getString(R.string.please_select_farmer));
            Helper.setViewFocus(spinnerfarmer);
            status = false;
        } else if (spinnerCategory.getSelectedId().size() == 0) {
            Helper.showToast(this, getString(R.string.please_select_category));
            Helper.setViewFocus(spinnerCategory);
            status = false;
        } else if (spinnerCategoryType.getSelectedId().size() == 0) {
            Helper.showToast(this, getString(R.string.please_select_category_type));
            Helper.setViewFocus(spinnerCategoryType);
            status = false;
        } else if (spinnerOwnerby.getSelectedItemPosition() == 0) {
            Helper.showToast(this, getString(R.string.please_select_owner_by));
            Helper.setViewFocus(spinnerOwnerby);
            status = false;
        } else if (serial.getText().toString().trim().isEmpty()) {
            Helper.showToast(this, getString(R.string.please_enter_serial_number));
            Helper.setViewFocus(serial);
            status = false;
        } else if (et_modelName.getText().toString().trim().isEmpty()) {
            Helper.showToast(this, getString(R.string.please_enter_model_name));
            Helper.setViewFocus(et_modelName);
            status = false;
        } else if (et_manufactureName.getText().toString().trim().isEmpty()) {
            Helper.showToast(this, getString(R.string.please_enter_manufacture));
            Helper.setViewFocus(et_manufactureName);
            status = false;
        } else if (et_yearmanufacture.getText().toString().trim().isEmpty() || et_yearmanufacture.getText().toString().trim().length() < 4) {
            Helper.showToast(this, getString(R.string.please_enter_year_of_manufacture));
            Helper.setViewFocus(et_yearmanufacture);
            status = false;
        } else if (et_specification.getText().toString().trim().isEmpty()) {
            Helper.showToast(this, getString(R.string.please_enter_specifications));
            Helper.setViewFocus(et_specification);
            status = false;
        } else if (equip_rent.getText().toString().trim().isEmpty()) {
            Helper.showToast(this, getString(R.string.please_rent_per_hour));
            Helper.setViewFocus(equip_rent);
            status = false;
        } else if (equip_rent_arc.getText().toString().trim().isEmpty()) {
            Helper.showToast(this, getString(R.string.please_enter_rent_per_acre));
            Helper.setViewFocus(equip_rent_arc);
            status = false;
        } else if (TextUtils.isEmpty(imagePath)) {
            Helper.showToast(this, getString(R.string.please_take_image));
            Helper.setViewFocus(img);
            status = false;
        } else if (et_ownername.getText().toString().trim().isEmpty()) {
            Helper.showToast(this, getString(R.string.please_enter_owner_name));
            Helper.setViewFocus(et_ownername);
            status = false;
        } else if (address.getText().toString().trim().isEmpty()) {
            Helper.showToast(this, getString(R.string.please_enter_address));
            Helper.setViewFocus(address);
            status = false;
        } else if (mobile.getText().toString().trim().isEmpty()) {
            Helper.showToast(this, getString(R.string.please_enter_mobile_number));
            Helper.setViewFocus(mobile);
            status = false;
        } else if (spinnerfarmer.getSelectedItem().get(0).getId().trim().equals("NEW") && !Helper.isValidMobile(mobile)) {
            Helper.showToast(this, getString(R.string.please_enter_valid_mobile_number));
            Helper.setViewFocus(mobile);
            status = false;
        } else if (spinnerStatus.getSelectedItemPosition() == 0) {
            Helper.showToast(this, getString(R.string.please_select_status));
            Helper.setViewFocus(spinnerStatus);
            status = false;
        } else if (!(spinnerStatus.getSelectedItemPosition() == 1) && et_Available_Date.getText().toString().trim().isEmpty()) {
            Helper.showToast(this, getString(R.string.please_select_date));
            Helper.setViewFocus(spinnerStatus);
            status = false;
        }


        return status;
    }


    private void loadData() {



        List<List<String>> default_data =  dbHelper.getDataByQuery("select distinct dist_id,dist_name,mandal_id from farmer");
        if (default_data.size() > 0) {
            spinnerdist.setItemID(default_data.get(0).get(0));

            if (dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.OFFICER_TYPE).equals("1")) {
                spinnerdist.setEnabled(false);
                spinnerMandal.setEnabled(false);
                spinnerMandal.setItemID(default_data.get(0).get(2));
            } else if (dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.OFFICER_TYPE).equals("2")) {
                spinnerdist.setEnabled(false);
                spinnerMandal.setEnabled(true);
            }

            spinnerOwnerby.setSelection(4);
            spinnerOwnerby.setEnabled(false);
        }

    }

    public void loadMandal(int distPos, String selectMandalID) {

        List<SpinnerData> ll_mandal = new ArrayList<>();
        String mandal = Helper.readTextFile(getApplicationContext(), R.raw.mandals);
        String mandalString[] = mandal.split("\\\n")[distPos].split("\\,");
        for (int i = 0; i < mandalString.length; i++) {
            String splitD[] = mandalString[i].split("\\^");
            SpinnerData spinnerData = new SpinnerData();
            spinnerData.setId(splitD[0]);
            spinnerData.setName(splitD[1]);
            ll_mandal.add(spinnerData);
        }

        spinnerMandal.setItems(ll_mandal, this);

        if (!selectMandalID.trim().equals("")) {
            spinnerMandal.setItemID(selectMandalID);
            spinnerMandal.setEnabled(false);
        }

        spinnervillage.setItems(new ArrayList<SpinnerData>(), this);
        spinnerfarmer.setItems(new ArrayList<SpinnerData>(), this);


    }


    @Override
    public void onItemsSelected(View parent, List<SpinnerData> list, int position) {
        if (parent == spinnerdist) {
            if (position != -1) {
                if (dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.OFFICER_TYPE).equals("1")) {
                    spinnerMandal.setItems(loadMandal(), this);
                } else {
                    spinnerMandal.setItems(loadMandal(), this);
                    spinnerMandal.setItemPosition(0);
                }
            }
        } else if (parent == spinnerMandal) {
            if (position != -1) {
                if (dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.OFFICER_TYPE).equals("2")) {
                    spinnervillage.setItems(loadVillageBasedMandal(spinnerMandal.getSelectedItem().get(0).getId()), this);
                } else {
                    spinnervillage.setItems(loadVillageBasedMandal(spinnerMandal.getSelectedItem().get(0).getId()), this);

                }
            }

        } else if (parent == spinnervillage) {
            if (position != -1) {
                loadFarmer(spinnervillage.getSelectedId().get(0));
            }

        } else if (parent == spinnerfarmer) {
            if (position != -1) {
                if (!spinnerfarmer.getSelectedId().get(0).toString().trim().equals("NEW")) {
                    loadFarmerDetails(spinnerfarmer.getSelectedItem().get(0).getId());
                } else if (spinnerfarmer.getSelectedId().get(0).toString().trim().equals("NEW")) {
                    resetForm();

                }
            } else {
                resetForm();
            }

        } else if (parent == spinnerCategory) {
            if (position != -1) {
                loadSubCatEquip(spinnerCategory.getSelectedItem().get(0).getId());
            }
        }
    }

    private List<SpinnerData> loadVillageBasedMandal(String id) {
        List<SpinnerData> mandalData = new ArrayList<>();

        List<List<String>> gpData = dbHelper.getGpBasedonIdSelection("'" + id + "'");
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


    private List<SpinnerData> loadMandal() {
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

    private void resetForm() {
        et_ownername.setText("");
        et_ownername.setTag("No");
        mobile.setText("");

        spinnerCategory.clearSelections();
        spinnerCategoryType.setItems(new ArrayList<SpinnerData>());
        serial.setText("");
        serial.requestFocus();
        serial.setFocusable(true);
        et_modelName.setText("");
        et_manufactureName.setText("");
        et_yearmanufacture.setText("");
        et_specification.setText("");
        equip_rent.setText("");
        equip_rent_arc.setText("");
        gps.setText("");
        img.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_camera));
        address.setText("");
        spinnerStatus.setSelection(0);
        et_Available_Date.setText("");
        imagePath = "";
    }

    private void loadSubCatEquip(String catId) {
        List<List<String>> db_subcat = dbHelper.getDataByQuery("select  EQUIPMENT_ID,EQUIPMENT_NAME from " + DBTables.Category.TABLE_NAME + " where CID=" + catId + "");
        if (db_subcat.size() > 0) {
            List<SpinnerData> ll_subCat = new ArrayList<>();
            for (int i = 0; i < db_subcat.size(); i++) {
                SpinnerData spinnerData = new SpinnerData();
                spinnerData.setId(db_subcat.get(i).get(0));
                spinnerData.setName(db_subcat.get(i).get(1));
                ll_subCat.add(spinnerData);
            }

            spinnerCategoryType.setItems(ll_subCat, this);
        }
    }

    private void loadFarmerDetails(String uid) {
        List<List<String>> db_farmer = dbHelper.getTableDataByCond(DBTables.FarmerTable.TABLE_NAME,
                new String[]{DBHelper.UID}, new String[]{uid});

        if (db_farmer.size() > 0) {
            mobile.setText(db_farmer.get(0).get(2));

            et_ownername.setText(db_farmer.get(0).get(3));
            et_ownername.setTag(db_farmer.get(0).get(4));
            serial.requestFocus();
            serial.setFocusable(true);

            spinnerCategory.clearSelections();
            spinnerCategoryType.setItems(new ArrayList<SpinnerData>());
            serial.setText("");
            et_modelName.setText("");
            et_manufactureName.setText("");
            et_yearmanufacture.setText("");
            et_specification.setText("");
            equip_rent.setText("");
            equip_rent_arc.setText("");
            gps.setText("");
            img.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_camera));
            address.setText("");
            spinnerStatus.setSelection(0);
            et_Available_Date.setText("");
            imagePath = "";
        }
    }

    private void loadFarmer(String village_id) {
//                al_farmer_uids.clear();

        barcode.setText(Helper.getTransactionIDWithMin(Helper.getTodayDate(), village_id));

        List<List<String>> db_farmer = dbHelper.getTableDataByCond(DBTables.FarmerTable.TABLE_NAME,
                new String[]{DBTables.FarmerTable.GP_ID}, new String[]{village_id});


        List<SpinnerData> ll_farmer = new ArrayList<>();

        SpinnerData spinnerData = new SpinnerData();
        spinnerData.setId("NEW");
        spinnerData.setName("NEW FARMER");
        ll_farmer.add(spinnerData);
        if (db_farmer.size() > 0) {

            for (int i = 0; i < db_farmer.size(); i++) {
                spinnerData = new SpinnerData();
                spinnerData.setId(db_farmer.get(i).get(0));
                spinnerData.setName(db_farmer.get(i).get(3));
                ll_farmer.add(spinnerData);
            }

            spinnerfarmer.setItems(ll_farmer, this);
            spinnerfarmer.setListener(this);

        }

        resetForm();


    }

    private JSONObject getJsonobjectEquip() {
        JSONObject jsonObjectMain = new JSONObject();

        try {
            jsonObjectMain.put("User_Id", dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.UNIQUE_ID));
            jsonObjectMain.put("User_MobileNo", dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.MOBILE));
            jsonObjectMain.put("User_Type", "O");
            jsonObjectMain.put("OfficerType", dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.OFFICER_TYPE));
            jsonObjectMain.put("DeviceId", PrefManger.getSharedPreferencesString(this, Constants.KEY_REG_ID, ""));
            jsonObjectMain.put("FarmerUnique_Id", et_ownername.getTag().toString().trim());
            jsonObjectMain.put("IMEI", Helper.getIMEINumber(this));
            jsonObjectMain.put("Version", getString(R.string.version));
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("village_code", spinnervillage.getSelectedItem().get(0).getId());
            jsonObject.put("Equipment_Id", spinnerCategoryType.getSelectedItem().get(0).getId());
            jsonObject.put("Owned_by_Id", spinnerOwnerby.getSelectedItemPosition());
            jsonObject.put("Barcode", barcode.getText().toString().trim());
            jsonObject.put("Serial_No", serial.getText().toString().trim());
            jsonObject.put("Model_Name", et_modelName.getText().toString().trim());
            jsonObject.put("Manufacture_Name", et_manufactureName.getText().toString().trim());
            jsonObject.put("Year_of_Manufacture", et_yearmanufacture.getText().toString().trim());
            jsonObject.put("Specifications", et_specification.getText().toString().trim());
            jsonObject.put("Equipment_Rent", equip_rent.getText().toString().trim());
            jsonObject.put("Equipment_Rent_per_acre", equip_rent_arc.getText().toString().trim());
            jsonObject.put("Equipment_GPS", gps.getText().toString().trim());
            jsonObject.put("Owner_Name", et_ownername.getText().toString().trim());
            jsonObject.put("Address", address.getText().toString().trim());
            jsonObject.put("Owner_Mobile_No", mobile.getText().toString().trim());
            if (spinnerStatus.getSelectedItemPosition() == 1) {
                jsonObject.put("Willing_ForRent_Id", "1");
                jsonObject.put("Available_Date", "");
            } else {
                jsonObject.put("Willing_ForRent_Id", "2");
                jsonObject.put("Available_Date", et_Available_Date.getText().toString().trim());

//                Date formate with yyyy/mm/dd
            }

            if (spinnerfarmer.getSelectedItem().get(0).getId().equals("NEW")) {
                jsonObject.put("Isfarmernew", "Y");
            } else {
                jsonObject.put("Isfarmernew", "N");
            }


            jsonObject.put("Equipment_Status_Id", spinnerStatus.getSelectedItemPosition());
            jsonObject.put("Version", getResources().getString(R.string.version));

            jsonObject.put("Mpo_Phone_Number", dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.MOBILE));
            jsonObject.put("Equipment_Image_Path", Helper.getImageStringFromPath(imagePath));

            jsonArray.put(jsonObject);

            jsonObjectMain.put("EquipmentRegDetails", jsonArray);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObjectMain;
    }

    private String getManfactureYear(String year) {

        if (!(Integer.parseInt(year) >= 1999 && Integer.parseInt(year) <= 2019)) {
            Helper.showToast(this, getString(R.string.please_enter_valid_manufacturing_year));
            et_yearmanufacture.setText("");
        }

        return year;
    }

    public void onclick_submit(View view) {
        if (isValidate()) {
            if (Helper.isNetworkAvailable(this)) {
//pending
                /*String[] key = {"Unique_Id", "equipReg"};
                String[] value = {et_ownername.getTag().toString().trim(), getJsonobjectEquip().toString()};*/
                RestServiceWithVolle soapService = new RestServiceWithVolle(this, new ResponseListener() {
                    @Override
                    public void onSuccess(int responseCode, String response) {

                        Log.d("EquipRegistration", "onSuccess: " + response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status").equals("200")) {
                                //Unique_Id pending
                                if (spinnerfarmer.getSelectedItem().get(0).getId().trim().equals("NEW")) {
                                    dbHelper.insertintoTable(DBTables.FarmerTable.TABLE_NAME, DBTables.FarmerTable.cols,
                                            new String[]{spinnervillage.getSelectedItem().get(0).getId(),
                                                    mobile.getText().toString().trim(), et_ownername.getText().toString().trim(),
                                                    jsonObject.getString("unique_Id"), Helper.getTodayDate()});

                                    Helper.showToast(EquipmentRegistrationActivity.this, getString(R.string.data_submited_success));
                                    Intent intent = new Intent(EquipmentRegistrationActivity.this, EquipmentListActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Helper.showToast(EquipmentRegistrationActivity.this, getString(R.string.data_submited_success));
                                    Intent intent = new Intent(EquipmentRegistrationActivity.this, EquipmentListActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                Helper.AlertMsg(EquipmentRegistrationActivity.this, jsonObject.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Helper.AlertMsg(EquipmentRegistrationActivity.this, e.getMessage().toString());
                        }

                    }

                    @Override
                    public void onError(int responseCode, String error) {
                        Log.d("Error", "onError() returned: " + error);

                        Helper.AlertMsg(EquipmentRegistrationActivity.this, error);

                    }
                }, REQUEST_EQUIP_REG, Constants.BASE_URL + Constants.METHOD_EQUIPMENT_REGISTRATION, POST);
                soapService.loadRequest(getJsonobjectEquip());


            } else {
                Helper.showToast(this, getString(R.string.no_internet_available));
            }
        }
    }
}
