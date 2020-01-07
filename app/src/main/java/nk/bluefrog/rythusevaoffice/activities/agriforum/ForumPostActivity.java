package nk.bluefrog.rythusevaoffice.activities.agriforum;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import nk.bluefrog.library.camera.CameraActivity;
import nk.bluefrog.library.network.ResponseListener;
import nk.bluefrog.library.utils.Helper;
import nk.bluefrog.library.utils.PrefManger;
import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.utils.Constants;
import nk.bluefrog.rythusevaoffice.utils.DBHelper;
import nk.bluefrog.rythusevaoffice.utils.DBTables;
import nk.mobileapps.spinnerlib.SearchableSpinner;
import nk.mobileapps.spinnerlib.SpinnerData;

public class ForumPostActivity extends AppCompatActivity implements View.OnClickListener, SearchableSpinner.SpinnerListener {

    TextView tvLinkType;
    EditText etPost, etLink;
    ImageView imageToPost, img_pic1, img_pic2, img_pic3;
    ImageButton btnCamera;
    SearchableSpinner sp_crop_type, sp_category, sp_cultivatedtype;
    CardView CardView_Image;
    int selcted_item_index = -1;
    DBHelper dbHelper;
    private int CAMERA_REQUEST = 1;
    private String imagePath;
    private int REQUEST_CODE_POST = 2;
    private int CAMERA_REQUEST_IMG1 = 1;
    private int CAMERA_REQUEST_IMG2 = 2;
    private int CAMERA_REQUEST_IMG3 = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_post);
        dbHelper = new DBHelper(this);
        findview();
    }

    private void findview() {
        etPost = findViewById(R.id.etPost);
        etLink = findViewById(R.id.etLink);
        imageToPost = findViewById(R.id.imageToPost);
        btnCamera = findViewById(R.id.btnCamera);
        tvLinkType = findViewById(R.id.tvLinkType);
        sp_crop_type = (SearchableSpinner) findViewById(R.id.sp_crop_type);
        sp_category = (SearchableSpinner) findViewById(R.id.sp_category);
        sp_cultivatedtype = (SearchableSpinner) findViewById(R.id.sp_cultivatedtype);
        CardView_Image = findViewById(R.id.CardView_Image);

        tvLinkType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linkTypesDialog();

            }
        });

        img_pic1 = findViewById(R.id.img_pic1);
        img_pic2 = findViewById(R.id.img_pic2);
        img_pic3 = findViewById(R.id.img_pic3);


        btnCamera.setOnClickListener(this);
        img_pic1.setOnClickListener(this);
        img_pic2.setOnClickListener(this);
        img_pic3.setOnClickListener(this);

        loadCroptype();
        loadCategory();
        loadCultivatedType();
    }

    private void loadCroptype() {
//        String category = Helper.readTextFile(getApplicationContext(), R.raw.seed_category);
        String category = Helper.readTextFile(getApplicationContext(), R.raw.crop_master);
        String fert[] = category.split("\\n")[PrefManger.getSharedPreferencesString(this, Constants.sp_lang, "en").equals("en") ? 0 : 1].split("\\|");
        List<SpinnerData> ll_crop_type = new ArrayList<>();
        for (int i = 0; i < fert.length; i++) {
            String splitD[] = fert[i].split("\\,");
            SpinnerData spinnerData = new SpinnerData();
            spinnerData.setId(splitD[0]);
            spinnerData.setName(splitD[1]);
            ll_crop_type.add(spinnerData);
        }

        sp_crop_type.setItems(ll_crop_type, this);
    }

    private void loadCategory() {
        String category = Helper.readTextFile(getApplicationContext(), R.raw.post_category);
        String fert[] = category.split("\\n")[PrefManger.getSharedPreferencesString(this, Constants.sp_lang, "en").equals("en") ? 0 : 1].split("\\|");
        List<SpinnerData> ll_category_type = new ArrayList<>();
        for (int i = 0; i < fert.length; i++) {
            String splitD[] = fert[i].split("\\,");
            SpinnerData spinnerData = new SpinnerData();
            spinnerData.setId(splitD[0]);
            spinnerData.setName(splitD[1]);
            ll_category_type.add(spinnerData);
        }

        sp_category.setItems(ll_category_type, this);
    }

    private void loadCultivatedType() {
        String category = Helper.readTextFile(getApplicationContext(), R.raw.post_cultivated_type);
        String fert[] = category.split("\\n")[PrefManger.getSharedPreferencesString(this, Constants.sp_lang, "en").equals("en") ? 0 : 1].split("\\|");
        List<SpinnerData> ll_cultivated_type = new ArrayList<>();
        for (int i = 0; i < fert.length; i++) {
            String splitD[] = fert[i].split("\\,");
            SpinnerData spinnerData = new SpinnerData();
            spinnerData.setId(splitD[0]);
            spinnerData.setName(splitD[1]);
            ll_cultivated_type.add(spinnerData);
        }

        sp_cultivatedtype.setItems(ll_cultivated_type, this);
    }

    private void linkTypesDialog() {
        final CharSequence[] items = {getString(R.string.image_link), getString(R.string.video_link)};


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.Select_link_type));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection
                selcted_item_index = item;
                tvLinkType.setText(items[item]);
                if (item == 0) {
                    CardView_Image.setVisibility(View.VISIBLE);
                    btnCamera.setVisibility(View.VISIBLE);
                } else {
                    CardView_Image.setVisibility(View.GONE);
                    btnCamera.setVisibility(View.GONE);
                }

                if (item == 1) {
                    etLink.setVisibility(View.VISIBLE);
                } else {
                    etLink.setVisibility(View.GONE);
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    @Override
    public void onClick(View v) {

        Intent intent = new Intent(ForumPostActivity.this, CameraActivity.class);

        if (v.getId() == R.id.img_pic1) {

            startActivityForResult(intent, CAMERA_REQUEST_IMG1);
        }

        /*if(v.getId()==R.id.img_pic2) {

            startActivityForResult(intent, CAMERA_REQUEST_IMG2);
        }

        if(v.getId()==R.id.img_pic3) {

            startActivityForResult(intent, CAMERA_REQUEST_IMG3);
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && resultCode == Activity.RESULT_OK) {

            if (requestCode == CAMERA_REQUEST_IMG1) {
                imagePath = data.getStringExtra(CameraActivity.RESULT_IMG_PATH);
                img_pic1.setImageBitmap(Helper.getImageWithoutCrop(imagePath));
                img_pic1.setVisibility(View.VISIBLE);

            }
            if (requestCode == CAMERA_REQUEST_IMG2) {
                imagePath = data.getStringExtra(CameraActivity.RESULT_IMG_PATH);
                img_pic2.setImageBitmap(Helper.getImageWithoutCrop(imagePath));
                img_pic2.setVisibility(View.VISIBLE);

            }
            if (requestCode == CAMERA_REQUEST_IMG3) {
                imagePath = data.getStringExtra(CameraActivity.RESULT_IMG_PATH);
                img_pic3.setImageBitmap(Helper.getImageWithoutCrop(imagePath));
                img_pic3.setVisibility(View.VISIBLE);

            }
        }
    }

    @Override
    public void onItemsSelected(View view, List<SpinnerData> list, int i) {

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ForumPostActivity.this, ForumActivity.class);
        startActivity(intent);
        finish();
    }

    private JSONObject sendJsonOject() {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("AAdhar_ID", dbHelper.getTabCol(DBTables.Register.TABLE_NAME, DBTables.Register.user_aadharid));
            jsonObject.put("crop_id", sp_crop_type.getSelectedItemId());
            jsonObject.put("category", sp_crop_type.getSelectedItemId());
            jsonObject.put("cultivation_type", sp_crop_type.getSelectedItemId());
            jsonObject.put("question", etPost.getText().toString().trim());
            if (selcted_item_index == 0) {
                jsonObject.put("link_type", "0");
                jsonObject.put("image", Helper.getImageStringFromPath(imagePath));
            } else {
                jsonObject.put("link_type", "1");
                jsonObject.put("link_url", etLink.getText().toString().trim());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;

    }

    public void onClick_submit(View view) {
        if (isValidate()) {

            if (Helper.isNetworkAvailable(this)) {
                String[] key = {"poststring"};
                String[] value = {sendJsonOject().toString()};
/*
                SoapService postService = new SoapService(this, new ResponseListener() {
                    @Override
                    public void onSuccess(int responseCode, String response) {
                        Log.d("", "onSuccess: " + response);

                        Intent intent = new Intent(ForumPostActivity.this, ForumActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(int responseCode, String error) {
                        Log.d("", "onError: " + error);
                    }
                }, REQUEST_CODE_POST, Constants.url_farmerinfo, "FarmPosts", key, value);

                postService.execute();
*/

            } else {
                Helper.showToast(this, getString(R.string.no_internet_available));
            }

        }
    }

    private boolean isValidate() {
        boolean flag = true;

        if (!sp_crop_type.isSelected()) {
            Helper.showToast(this, getString(R.string.select) + " " + getString(R.string.crop_type_list));
            Helper.setViewFocus(sp_crop_type);
            flag = false;
        } else if (!sp_category.isSelected()) {
            Helper.showToast(this, getString(R.string.please) + " " + getString(R.string.category));
            Helper.setViewFocus(sp_category);
            flag = false;
        } else if (!sp_cultivatedtype.isSelected()) {
            Helper.showToast(this, getString(R.string.please) + " " + getString(R.string.cultivated_type));
            Helper.setViewFocus(sp_cultivatedtype);
            flag = false;
        } else if (etPost.getText().toString().trim().isEmpty()) {
            Helper.showToast(this, getString(R.string.please_enter) + " " + getString(R.string.type_here_to_share_massage));
            Helper.setViewFocus(etPost);
            flag = false;
        } else if (selcted_item_index == -1) {
            Helper.showToast(this, getString(R.string.select) + " " + getString(R.string.link_type));
            Helper.setViewFocus(tvLinkType);
            flag = false;
        } else if (selcted_item_index == 0 && TextUtils.isEmpty(imagePath)) {
            Helper.showToast(this, getString(R.string.please) + " " + getString(R.string.take_photo));
            Helper.setViewFocus(imageToPost);
            flag = false;
        }

        return flag;
    }
}
