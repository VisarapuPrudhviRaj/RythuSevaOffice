package nk.bluefrog.rythusevaoffice.activities.farmercommunication;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

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
import nk.bluefrog.library.utils.PermissionResult;
import nk.bluefrog.library.utils.PrefManger;
import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.utils.Constants;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;
import nk.bluefrog.rythusevaoffice.utils.DBTables;
import nk.bluefrog.rythusevaoffice.utils.ImageUtils;
import nk.bluefrog.rythusevaoffice.utils.ValidationClass;
import nk.mobileapps.spinnerlib.SearchableMultiSpinner;
import nk.mobileapps.spinnerlib.SearchableSpinner;
import nk.mobileapps.spinnerlib.SpinnerData;

import static nk.bluefrog.rythusevaoffice.utils.ImageUtils.getScreenWidthInDPs;

public class FarmerCommunicationSOActivity extends BluefrogActivity implements SearchableSpinner.SpinnerListener, ResponseListener, SearchableMultiSpinner.SpinnerListener, AdapterView.OnItemSelectedListener {

    final public int GPS_FLAG = 1245;
    private final int galleryRequestCode = 456;
    private DBHelper dbHelper;
    private SearchableMultiSpinner spinnerVillage, spinnerDistrict, spinnerMandal;
    private SearchableSpinner spinnerCrop;
    private Spinner spinnerUploadType;
    private ImageView img;
    private EditText etTitle;
    private EditText etDescription;
    private EditText etLink;
    private EditText etVideoLink;
    private String gpsString;
    private LinearLayout imageLayout;
    //private ArrayList<SpinnerData> distTypeData = new ArrayList<>();
    private TextInputLayout videoLinkLayout;
    private ArrayList<SpinnerData> cropTypeData = new ArrayList<>();
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.farmer_comm_so);
        dbHelper = new DBHelper(this);
        spinnerVillage = findViewById(R.id.spinnerVillage);
        spinnerDistrict = findViewById(R.id.spinnerDistrict);
        spinnerMandal = findViewById(R.id.spinnerMandal);
        spinnerCrop = findViewById(R.id.spinnerCrop);
        spinnerUploadType = findViewById(R.id.spinnerUploadType);
        img = findViewById(R.id.img);
        etTitle = findViewById(R.id.et_title);
        etDescription = findViewById(R.id.et_description);
        etLink = findViewById(R.id.link_general);
        etVideoLink = findViewById(R.id.link_video);

        imageLayout = findViewById(R.id.imageLayout);
        videoLinkLayout = findViewById(R.id.videoLinkLayout);

        spinnerUploadType.setOnItemSelectedListener(this);

        spinnerMandal.setItems(new ArrayList<SpinnerData>());
        spinnerVillage.setItems(new ArrayList<SpinnerData>());

        spinnerDistrict.setItems(loadDist(), new SearchableMultiSpinner.SpinnerListener() {
            @Override
            public void onItemsSelected(View view, List<SpinnerData> list, List<SpinnerData> list1) {

                spinnerMandal.setItems(getMandalsData(), new SearchableMultiSpinner.SpinnerListener() {
                    @Override
                    public void onItemsSelected(View view, List<SpinnerData> list, List<SpinnerData> list1) {

                        spinnerVillage.setItems(getVillageData(), new SearchableMultiSpinner.SpinnerListener() {
                            @Override
                            public void onItemsSelected(View view, List<SpinnerData> list, List<SpinnerData> list1) {


                            }
                        });

                    }
                });
            }
        });
        spinnerDistrict.setFixedCheckPostion("0");

        spinnerMandal.setFixedCheckPostion("0");

        spinnerVillage.setFixedCheckPostion("0");
        spinnerCrop.setItems(new ArrayList<SpinnerData>());

        imageLayout.setVisibility(View.GONE);
        videoLinkLayout.setVisibility(View.GONE);

        SpinnerData seedData = new SpinnerData();
        seedData.setId("0");
        seedData.setName("All");
        cropTypeData.add(seedData);
        String seedcat = Helper.readTextFile(getApplicationContext(), R.raw.crop_master);
        String fert[] = seedcat.split("\\n")[PrefManger.getSharedPreferencesString(this, Constants.sp_lang, "en").equals("en") ? 0 : 1].split("\\|");

        for (String aFert : fert) {
            String splitD[] = aFert.split(",");

            seedData = new SpinnerData();
            seedData.setId(splitD[0]);
            seedData.setName(splitD[1]);
            cropTypeData.add(seedData);

        }

        spinnerCrop.setItems(cropTypeData);

        ArrayList<String> mList = new ArrayList<>();
        ArrayList<String> VList = new ArrayList<>();


    }

    public void submit(View view) {
        if (checkValidation()) {

         /*   {"MobileNo":"7306676544","Panchayat":"053317","CropType":"1","Title":"Rythu Seva","Description":"desc"
            ,"UploadType":"1","Image_Video":"NA"}
*/
            StringBuilder villageIds = new StringBuilder();
            StringBuilder districtIds = new StringBuilder();
            StringBuilder mandalIds = new StringBuilder();


            if (Helper.isNetworkAvailable(getApplicationContext())) {
                JSONObject jobMain = new JSONObject();
                try {
                    jobMain.put("MobileNo", PrefManger.getSharedPreferencesString(this, Constants.sp_mobile, ""));

                    JSONArray villageArray = new JSONArray();

                    for (int i = 0; i < spinnerVillage.getSelectedItems().size(); i++) {


                        if (i == 0) {
                            villageIds = new StringBuilder(spinnerVillage.getSelectedItems().get(i).getId());
                        } else {
                            villageIds.append(",").append(spinnerVillage.getSelectedItems().get(i).getId());
                        }


                        JSONObject villageObject = new JSONObject();
                        villageObject.put("Panchayat", spinnerVillage.getSelectedItems().get(i).getId());

                        villageArray.put(villageObject);

                    }


                    //jobMain.put("PanchayatArray",villageArray);
                    jobMain.put("Panchayat", villageIds.toString());

                    JSONArray districtArray = new JSONArray();

                    for (int j = 0; j < spinnerDistrict.getSelectedItems().size(); j++) {

                        if (j == 0) {

                            districtIds = new StringBuilder(spinnerDistrict.getSelectedItems().get(j).getId());

                        } else {

                            districtIds.append(",").append(spinnerDistrict.getSelectedItems().get(j).getId());
                        }

                        JSONObject districtObject = new JSONObject();
                        districtObject.put("District", spinnerDistrict.getSelectedItems().get(j).getId());

                        districtArray.put(districtObject);
                    }

                    jobMain.put("District", districtIds.toString());


                    JSONArray mandalArray = new JSONArray();

                    for (int k = 0; k < spinnerMandal.getSelectedItems().size(); k++) {


                        if (k == 0) {

                            mandalIds = new StringBuilder(spinnerMandal.getSelectedItems().get(k).getId());
                        } else {

                            mandalIds.append(",").append(spinnerMandal.getSelectedItems().get(k).getId());
                        }

                        JSONObject mandalObject = new JSONObject();
                        mandalObject.put("Mandals", spinnerMandal.getSelectedItems().get(k).getId());
                        mandalArray.put(mandalObject);
                    }

                    jobMain.put("Mandals", mandalIds);


                    jobMain.put("CropType", spinnerCrop.getSelectedId().get(0));
                    jobMain.put("Title", etTitle.getText().toString());
                    jobMain.put("Description", etDescription.getText().toString());
                    jobMain.put("CropType", spinnerCrop.getSelectedId().get(0));

                    if (spinnerUploadType.getSelectedItemPosition() == 1) {
                        jobMain.put("UploadType", "1");
                        jobMain.put("Image_Video", Helper.getImageStringFromPath((String) img.getTag()));
                    }

                    if (spinnerUploadType.getSelectedItemPosition() == 2) {
                        jobMain.put("UploadType", "2");
                        jobMain.put("Image_Video", etVideoLink.getText().toString());
                    }

                    if (gpsString != null) {
                        jobMain.put("Image_Video", Helper.getImageStringFromPath((String) img.getTag()));
                        jobMain.put("UploadType", "1");
                    } else {
                        if (etVideoLink.getText().toString().isEmpty()) {
                            jobMain.put("Image_Video", "NA");
                        } else {
                            jobMain.put("Image_Video", etVideoLink.getText().toString());
                            jobMain.put("UploadType", "1");
                        }

                    }

                    jobMain.put("General_link", etLink.getText().toString());
                    // jobMain.put("video_link", "1");

                    // insertIntoDB();


                    if (Helper.isNetworkAvailable(this)) {
                        sendRegistrationData(jobMain);
                    } else {
                        Helper.AlertMesg(this, getString(R.string.no_network));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Helper.AlertMesg(getApplicationContext(), getString(R.string.network));
            }
        }
    }

    public void sendRegistrationData(JSONObject finalString) {
        showProgressDialog(getString(R.string.please_wait_submitting));

     /*   final String keys[] = {"data"};
        final String values[] = {finalString};*/

        Constants.showDottedProgress(this, getString(R.string.wait));


        RestServiceWithVolle soapService = new RestServiceWithVolle(this, this, 1, Constants.URL_FARMER_COMM
                + Constants.METHOD_FARMER_COMM, POST, false);

        soapService.loadRequest(finalString);

    }

    private boolean checkValidation() {
        boolean status = true;
        if (spinnerDistrict.getSelectedIds().size() == 0) {
            status = false;
            Helper.showToast(this, getString(R.string.please_select_district));
        } else if (spinnerMandal.getSelectedIds().size() == 0) {

            status = false;
            Helper.showToast(this, getString(R.string.please_select_mandal));

        } else if (spinnerVillage.getSelectedIds().size() == 0) {
            status = false;
            Helper.showToast(this, getString(R.string.please_select_village));
        } else if (spinnerCrop.getSelectedId().size() == 0) {
            status = false;
            Helper.showToast(this, getString(R.string.please_select_crop));
        } else if (!ValidationClass.hasText(etTitle)) {
            status = false;
        } else if (!ValidationClass.hasText(etDescription)) {
            status = false;
        } else if (spinnerUploadType.getSelectedItemPosition() == 0) {
            status = false;
            Helper.showToast(this, getString(R.string.pleae_select_upload_type));
        } else if (spinnerUploadType.getSelectedItemPosition() == 1 && imagePath == null) {
            status = false;
            Helper.showToast(this, getString(R.string.please_capture_image));
        } else if (spinnerUploadType.getSelectedItemPosition() == 2 && etVideoLink.getText().toString().isEmpty()) {
            status = false;
            Helper.showToast(this, getString(R.string.please_enter_videolink));
        }/*else if (imagePath!=null && !etVideoLink.getText().toString().isEmpty()) {
           Helper.showToast(this, "Please give either videolink or image");
           status = false;
       }*/
        return status;
    }

    public void captureImage(View view) {

        openImageOptionsDialog();

       /* startActivityForResult(new Intent(getApplicationContext(),
                GPSActivity.class), GPS_FLAG);*/
    }

    private void openImageOptionsDialog() {

        LayoutInflater inflater = LayoutInflater.from(this);

        View view = inflater.inflate(R.layout.dialog_image_options, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(view);

        final AlertDialog dialog = builder.create();

        Button gallery = view.findViewById(R.id.gallery);
        Button camera = view.findViewById(R.id.camera);

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                openGallery();

            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                openCamera();
            }
        });

        dialog.show();

    }

    private void openGallery() {
        showFileChooser();
    }

    private void showFileChooser() {

        askCompactPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                new PermissionResult() {
                    @Override
                    public void permissionGranted() {
                        //isCameraPermissionsGranted = true;
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_PICK);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), galleryRequestCode);
                    }

                    @Override
                    public void permissionDenied() {
                        Helper.showToast(FarmerCommunicationSOActivity.this, getString(R.string.accept_cam_permissions));
                        //setCancelResult();
                    }

                    @Override
                    public void permissionForeverDenied() {
                        openSettingsApp(FarmerCommunicationSOActivity.this);
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {

            if (requestCode == GPS_FLAG) {
                gpsString = data.getStringExtra(GPSActivity.LOC_DATA);
                openImageOptionsDialog();
            }
            if (requestCode == 1) {
                File photoFile = null;
                photoFile = new File(data.getStringExtra(CameraActivity.RESULT_IMG_PATH));
                String uriSting = data.getStringExtra(CameraActivity.RESULT_IMG_PATH);
                imagePath = photoFile.getAbsolutePath();
                if (photoFile != null && !TextUtils.isEmpty(photoFile.getAbsolutePath())) {
                    img.setImageBitmap(BitmapFactory.decodeFile(imagePath));
                    img.setTag(uriSting);
                } else {
                    Helper.showToast(getApplicationContext(),
                            getResources().getString(R.string.photo_failed));
                }

            }
            if (requestCode == galleryRequestCode) {
                Uri filePath = data.getData();
                ImageUtils imageUtils = new ImageUtils(this);
                imagePath = imageUtils.getPath(filePath);
                Bitmap bitmap = imageUtils.getScaledBitmap(imagePath, getScreenWidthInDPs(this), 240);
                img.setImageBitmap(bitmap);
                img.setTag(imagePath);
            }

        }
    }

    private void openCamera() {
        startActivityForResult(new Intent(this, CameraActivity.class), 1);
    }

    @Override
    public void onItemsSelected(View view, List<SpinnerData> list, int i) {


        if (view == spinnerVillage) {
            if (i != -1) {

            }
        }
        if (view == spinnerDistrict) {
            if (i != -1) {

                getMandalsData();
            }
        }


    }

    private List<SpinnerData> loadDist() {
        List<SpinnerData> distTypeData = new ArrayList<>();

        SpinnerData spnrDistData = new SpinnerData();
        spnrDistData.setId("0");
        spnrDistData.setName("All");
        distTypeData.add(spnrDistData);
        String distList = Helper.readTextFile(getApplicationContext(), R.raw.dist);
        String splitDist[] = distList.split("\\\r\n");
        for (int i = 0; i < splitDist.length; i++) {


            String splitD[] = splitDist[i].split("\\^");
            SpinnerData spinnerData = new SpinnerData();
            spinnerData.setId(splitD[0]);
            spinnerData.setName(splitD[1]);
            distTypeData.add(spinnerData);
        }


     /*   List<List<String>> distData = dbHelper.getDistrictIds();
        if(distData.size() >0){

            for(int i = 0 ; i< distData.size(); i++){

                spnrDistData = new SpinnerData();
                spnrDistData.setId(distData.get(i).get(0));
                spnrDistData.setName(distData.get(i).get(1));

                distTypeData.add(spnrDistData);
            }
        }*/
        //spinnerMandal.setItems(new ArrayList<SpinnerData>(), this);
        //spinnerVillage.setItems(new ArrayList<SpinnerData>(),this);

        return distTypeData;

    }


    private List<SpinnerData> getMandalsData() {


        List<SpinnerData> mandalsData = new ArrayList<>();
        String mandalList = Helper.readTextFile(getApplicationContext(), R.raw.mandals);
        String splitMandal[] = mandalList.split("\\,");
        SpinnerData spnrMandalData = new SpinnerData();

        spnrMandalData.setId("0");
        spnrMandalData.setName("All");
        mandalsData.add(spnrMandalData);


        for (int i = 0; i < splitMandal.length; i++) {

            String mandal = splitMandal[i];


            for (int j = 0; j < spinnerDistrict.getSelectedIds().size(); j++) {

                if (splitMandal[i].startsWith(spinnerDistrict.getSelectedIds().get(j))) {


                    String splitM[] = splitMandal[i].split("\\^");

                    SpinnerData spinnerData = new SpinnerData();
                    spinnerData.setId(splitM[0]);


                    String mandalName = splitM[1] + "( " + spinnerDistrict.getSelectedItems().get(j).getName() + " )";

                    spinnerData.setName(mandalName);
                    mandalsData.add(spinnerData);

                }


            }

        }
        return mandalsData;
    }


    private List<SpinnerData> getVillageData() {

        List<SpinnerData> villagesData = new ArrayList<>();
        String villageList = Helper.readTextFile(getApplicationContext(), R.raw.gp);
        String splitVillage[] = villageList.split("\\,");
        SpinnerData spnrVillageData = new SpinnerData();

        spnrVillageData.setId("0");
        spnrVillageData.setName("All");
        villagesData.add(spnrVillageData);


        for (int i = 0; i < splitVillage.length; i++) {

            String village = splitVillage[i];


            for (int j = 0; j < spinnerMandal.getSelectedIds().size(); j++) {

                if (splitVillage[i].startsWith(spinnerMandal.getSelectedIds().get(j))) {


                    String splitV[] = splitVillage[i].split("\\^");

                    SpinnerData spinnerData = new SpinnerData();
                    spinnerData.setId(splitV[0]);

                    String VillageName = splitV[1] + "( " + spinnerMandal.getSelectedItems().get(j).getName() + " )";

                    spinnerData.setName(VillageName);
                    villagesData.add(spinnerData);

                }


            }

        }
        return villagesData;
    }
    /*https://www.youtube.com/watch?v=MTXXMDfIicA*/

    @Override
    public void onSuccess(int responseCode, String strResponse) {
        Constants.closeDottedProgress();
        try {
            JSONObject jobj = new JSONObject(strResponse);
            if (jobj.getString("Status").contains("$200")) {
                insertIntoDB();
            } else {
                insertIntoDB();
                Helper.showToast(FarmerCommunicationSOActivity.this, getString(R.string.not_submit));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(int responseCode, String error) {
        insertIntoDB();
        Helper.showToast(FarmerCommunicationSOActivity.this, error);
    }

    private void insertIntoDB() {

        StringBuilder gpIds = new StringBuilder();
        StringBuilder gpNames = new StringBuilder();
        StringBuilder distIds = new StringBuilder();
        StringBuilder distNames = new StringBuilder();
        StringBuilder mandalIds = new StringBuilder();
        StringBuilder mandalNames = new StringBuilder();


        for (int i = 0; i < spinnerDistrict.getSelectedItems().size(); i++) {

            if (i == 0) {

                distIds = new StringBuilder(spinnerDistrict.getSelectedItems().get(i).getId());
                distNames = new StringBuilder(spinnerDistrict.getSelectedItems().get(i).getName());
            } else {

                distIds.append(",").append(spinnerDistrict.getSelectedItems().get(i).getId());
                distNames.append(",").append(spinnerDistrict.getSelectedItems().get(i).getName());


            }
        }
        for (int k = 0; k < spinnerMandal.getSelectedItems().size(); k++) {

            if (k == 0) {

                mandalIds = new StringBuilder(spinnerMandal.getSelectedItems().get(k).getId());
                mandalNames = new StringBuilder(spinnerMandal.getSelectedItems().get(k).getName());

            } else {

                mandalIds.append(",").append(spinnerMandal.getSelectedItems().get(k).getId());
                mandalNames.append(",").append(spinnerMandal.getSelectedItems().get(k).getName());

            }
        }

        for (int j = 0; j < spinnerVillage.getSelectedItems().size(); j++) {

            if (j == 0) {
                gpIds = new StringBuilder(spinnerVillage.getSelectedItems().get(j).getId());
                gpNames = new StringBuilder(spinnerVillage.getSelectedItems().get(j).getName());
            } else {
                gpIds.append(",").append(spinnerVillage.getSelectedItems().get(j).getId());
                gpNames.append(",").append(spinnerVillage.getSelectedItems().get(j).getName());
            }


        }

        dbHelper.insertintoTable(DBTables.FarmerCommunication.TABLE_NAME, DBTables.FarmerCommunication.cols,
                new String[]{gpIds.toString(), gpNames.toString(), spinnerCrop.getSelectedId().get(0), etTitle.getText().toString(), etDescription.getText().toString(), "1"
                        , imagePath, gpNames.toString(), etLink.getText().toString(), etVideoLink.getText().toString()
                        , new SimpleDateFormat("dd/MM/yyyy", Locale.UK).format(Calendar.getInstance().getTime()), distIds.toString(), distNames.toString(), mandalIds.toString(), mandalNames.toString()});

       /* dbHelper.insertintoTable(DBTables.FarmerCommunication.TABLE_NAME, DBTables.FarmerCommunication.cols,
                new String[]{spinnerVillage.getSelectedItems().get(0).getId(),spinnerVillage.getSelectedItems().get(0).getName(),etTitle.getText().toString(),etDescription.getText().toString(),"1"
                        ,imagePath,etLink.getText().toString(),etVideoLink.getText().toString()
                        ,new SimpleDateFormat("dd/MM/yyyy", Locale.UK).format(Calendar.getInstance().getTime())});*/

        Helper.showToast(FarmerCommunicationSOActivity.this, getString(R.string.submit_suceess));
        finish();
    }

    /*For Searchable Multi Spinner*/

    @Override
    public void onItemsSelected(View view, List<SpinnerData> list, List<SpinnerData> list1) {


    }

    /*Normal Spinner*/

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        if (adapterView == spinnerUploadType) {
            if (spinnerUploadType.getSelectedItemPosition() > 0) {
                if (spinnerUploadType.getSelectedItemPosition() == 1) {
                    imageLayout.setVisibility(View.VISIBLE);
                    videoLinkLayout.setVisibility(View.GONE);
                }
                if (spinnerUploadType.getSelectedItemPosition() == 2) {
                    imageLayout.setVisibility(View.GONE);
                    videoLinkLayout.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
