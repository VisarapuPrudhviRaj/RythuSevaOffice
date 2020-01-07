package nk.bluefrog.rythusevaoffice.activities.slidemenu;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class ReviewMeeting extends BluefrogActivity implements SearchableSpinner.SpinnerListener
        , SearchableMultiSpinner.SpinnerListener, ResponseListener {

    final public int GPS_FLAG = 1245;
    private final int galleryRequestCode = 456;
    private SearchableSpinner villageSpinner;
    private SearchableMultiSpinner farmerSpinner;
    private EditText etDesc;
    private EditText etRemarks;
    private TextView farmerCount;
    private TextView tvRemarks;
    private LinearLayout formLayout;
    private LinearLayout detailsLayout;
    private DBHelper dbHelper;
    private ImageView meetingImage, imgCapture;
    private List<List<String>> farmerData;
    private String gpsString;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_meeting);
        setToolBar(getString(R.string.meeting_review), getIntent().getStringExtra("date"));
        initViews();

    }

    private void initViews() {

        villageSpinner = findViewById(R.id.villageSpinner);
        farmerSpinner = findViewById(R.id.farmerSpinner);
        etDesc = findViewById(R.id.description_meeting);
        etRemarks = findViewById(R.id.et_remarks);

        farmerCount = findViewById(R.id.farmerCount);
        tvRemarks = findViewById(R.id.meetingRemarks);

        formLayout = findViewById(R.id.llForm);
        detailsLayout = findViewById(R.id.llDetails);

        meetingImage = findViewById(R.id.ivImageMeeting);

        imgCapture = findViewById(R.id.img);

        villageSpinner.setItems(new ArrayList<SpinnerData>());
        farmerSpinner.setItems(new ArrayList<SpinnerData>());

        formLayout.setVisibility(View.GONE);
        detailsLayout.setVisibility(View.GONE);

        dbHelper = new DBHelper(this);


      /*  List<List<String>> villageData = dbHelper.getTableColDataByCond(DBTables.ScheduleMeetings.TABLE_NAME
                ,DBTables.ScheduleMeetings.GP_ID+","+DBTables.ScheduleMeetings.GP_NAME
                ,new String[]{DBTables.ScheduleMeetings.MEETING_DATE},new String[]{getIntent().getStringExtra("date")});*/

        List<List<String>> villageData = dbHelper.getGpIds();

        ArrayList<SpinnerData> spinnerData = new ArrayList<>();

        for (int i = 0; i < villageData.size(); i++) {

            SpinnerData data = new SpinnerData();
            data.setId(villageData.get(i).get(0));
            data.setName(villageData.get(i).get(1));

            spinnerData.add(data);

        }

        villageSpinner.setItems(spinnerData, this);


    }

    private String getSpinnerIDs(List<String>  itemsIds){
        String ids=itemsIds.get(0).trim();
        for (int i = 1; i <itemsIds.size() ; i++) {
            ids+=","+itemsIds.get(i).trim();
        }

        return ids;

    }

    public void submitReview(View view) {

        if (checkValidation()) {

            /*{"MPEO_MobileNo":"7306676544","GPID":"053317","MeetingDate":"07-12-2018","Description":"Meeting on RythuSeva","Remarks":"","Image":"NA"
            ,"Farmers":[{"AadharID":"201033688422","FarmerName":"Buddaraju Anjaneyaraju"}
            ,{"AadharID":"203621501588","FarmerName":"Parvathala Nagalakshmi"}]}*/

            if (Helper.isNetworkAvailable(getApplicationContext())) {
                JSONObject jobMain = new JSONObject();
                try {

                    jobMain.put("User_Id", dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.UNIQUE_ID));
                    jobMain.put("User_Type", "O");
                    jobMain.put("OfficerType", dbHelper.getTabCol(DBTables.MPOTable.TABLE_NAME, DBTables.MPOTable.OFFICER_TYPE));


                    jobMain.put("MobileNo", PrefManger.getSharedPreferencesString(this, Constants.sp_mobile, ""));
                    jobMain.put("GPID", villageSpinner.getSelectedItem().get(0).getId());
                    jobMain.put("MeetingDate", getIntent().getStringExtra("date"));
                    jobMain.put("Description", etDesc.getText().toString());
                    if (!etRemarks.getText().toString().trim().isEmpty()) {
                        jobMain.put("Remarks", etRemarks.getText().toString());
                    } else {
                        jobMain.put("Remarks", "NA");
                    }


                    if (imagePath != null) {
                        jobMain.put("Image", Helper.getImageStringFromPath((String) imgCapture.getTag()));
                    } else {
                        jobMain.put("Image", "NA");
                    }

                    JSONArray farmerArray = new JSONArray();

                    for (int i = 0; i < farmerSpinner.getSelectedItems().size(); i++) {

                        JSONObject farmerObject = new JSONObject();
                        farmerObject.put("Unique_Id", farmerSpinner.getSelectedItems().get(i).getId());
                        farmerObject.put("FarmerName", farmerSpinner.getSelectedItems().get(i).getName());

                        farmerArray.put(farmerObject);

                    }

                    jobMain.put("Farmers", farmerArray);
                    jobMain.put("IMEI",Helper.getIMEINumber(this));
                    jobMain.put("Version",getString(R.string.version));


                    sendRegistrationData(jobMain);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Helper.AlertMesg(getApplicationContext(), getString(R.string.network));
            }
        }


    }

    private void sendRegistrationData(JSONObject sendingString) {


        /*final String keys[] = {"data"};
        final String values[] = {sendingString};*/

        RestServiceWithVolle soapService = new RestServiceWithVolle(this, this, 1, Constants.BASE_URL
                + Constants.METHOD_MEETING_REVIEW, POST);

        soapService.loadRequest(sendingString);
    }

    @Override
    public void onSuccess(int responseCode, String strResponse) {

        try {
            JSONObject jobj = new JSONObject(strResponse);
            if (jobj.getString("status").contains("200")) {
                dbHelper.insertintoTable(DBTables.MeetingReview.TABLE_NAME, DBTables.MeetingReview.cols,
                        new String[]{getIntent().getStringExtra("date"), villageSpinner.getSelectedId().get(0)
                                , String.valueOf(farmerData.size()), etDesc.getText().toString(), etRemarks.getText().toString()
                                , imagePath, new SimpleDateFormat("dd/MM/yyyy", Locale.UK).format(Calendar.getInstance().getTime())});
                Helper.showToast(ReviewMeeting.this, getString(R.string.submit_suceess));
                finish();
            } else {
                Helper.showToast(ReviewMeeting.this, getString(R.string.not_submit));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onError(int responseCode, String error) {

        Helper.showToast(ReviewMeeting.this, error);

    }

    private boolean checkValidation() {
        boolean status = true;
        if (villageSpinner.getSelectedId().size() == 0) {
            status = false;
            Helper.showToast(this, getString(R.string.please_select_village));
        } else if (farmerSpinner.getSelectedIds().size() == 0) {
            status = false;
            Helper.showToast(this, getString(R.string.please_select_farmer));
        } else if (!ValidationClass.hasText(etDesc)) {
            status = false;
        }


        return status;
    }

    @Override
    public void onItemsSelected(View view, List<SpinnerData> list, int i) {

        if (i != -1) {

            int count = dbHelper.getCountByValues(DBTables.MeetingReview.TABLE_NAME, new String[]{DBTables.MeetingReview.MEETING_DATE
                            , DBTables.MeetingReview.GP_ID}
                    , new String[]{getIntent().getStringExtra("date"), list.get(i).getId()});

            formLayout.setVisibility(View.VISIBLE);
            detailsLayout.setVisibility(View.GONE);

            farmerData = new ArrayList<>();

            farmerData = dbHelper.getTableColDataByCond(DBTables.FarmerTable.TABLE_NAME
                    , DBTables.FarmerTable.NAME + "," + DBTables.FarmerTable.AADHAR + "," + DBTables.FarmerTable.MOBILE
                    , new String[]{DBTables.FarmerTable.GP_ID}, new String[]{list.get(i).getId()});

            ArrayList<SpinnerData> spinnerData = new ArrayList<>();

            for (int j = 0; j < farmerData.size(); j++) {

                SpinnerData data = new SpinnerData();
                data.setName(farmerData.get(j).get(0));
                data.setId(farmerData.get(j).get(1));
                spinnerData.add(data);

            }

            farmerSpinner.setItems(spinnerData, this);

            /*if(count==0){

                formLayout.setVisibility(View.VISIBLE);
                detailsLayout.setVisibility(View.GONE);

                farmerData = new ArrayList<>();

                farmerData = dbHelper.getTableColDataByCond(DBTables.FarmerTable.TABLE_NAME
                        ,DBTables.FarmerTable.NAME+","+DBTables.FarmerTable.AADHAR+","+DBTables.FarmerTable.MOBILE
                        ,new String []{DBTables.FarmerTable.GP_ID},new String []{list.get(i).getId()});

                ArrayList<SpinnerData> spinnerData = new ArrayList<>();

                for (int j = 0; j <farmerData.size() ; j++) {

                    SpinnerData data = new SpinnerData();
                    data.setName(farmerData.get(j).get(0));
                    data.setId(farmerData.get(j).get(1));
                    spinnerData.add(data);

                }

                farmerSpinner.setItems(spinnerData,this);


            }else{

                formLayout.setVisibility(View.GONE);
                detailsLayout.setVisibility(View.VISIBLE);

                List<List<String>> meetingData = dbHelper.getTableColDataByCond(DBTables.MeetingReview.TABLE_NAME
                        ,DBTables.MeetingReview.FARMER_COUNT+","+DBTables.MeetingReview.DESCRIPTION+","+DBTables.MeetingReview.IMAGE_PATH
                        ,new String []{DBTables.MeetingReview.GP_ID},new String []{list.get(i).getId()});

                farmerCount.setText(meetingData.get(0).get(0));
                tvRemarks.setText(meetingData.get(0).get(1));

                if(meetingData.get(0).get(2)!=null){
                    File imageFile = new File(meetingData.get(0).get(2));
                    meetingImage.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
                }



            }*/


        }

    }

    @Override
    public void onItemsSelected(View view, List<SpinnerData> list, List<SpinnerData> list1) {

    }

    public void captureImage(View view) {

        startActivityForResult(new Intent(getApplicationContext(),
                GPSActivity.class), GPS_FLAG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


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
                    imgCapture.setImageBitmap(BitmapFactory.decodeFile(imagePath));
                    imgCapture.setTag(uriSting);
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
                imgCapture.setImageBitmap(bitmap);
                imgCapture.setTag(imagePath);
            }
        }
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

    private void openCamera() {
        startActivityForResult(new Intent(this, CameraActivity.class), 1);
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
                        Helper.showToast(ReviewMeeting.this, getString(R.string.accept_cam_permissions));
                        //setCancelResult();
                    }

                    @Override
                    public void permissionForeverDenied() {
                        openSettingsApp(ReviewMeeting.this);
                    }
                });

    }


}
