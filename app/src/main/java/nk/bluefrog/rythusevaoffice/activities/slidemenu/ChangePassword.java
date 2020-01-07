package nk.bluefrog.rythusevaoffice.activities.slidemenu;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import nk.bluefrog.library.BluefrogActivity;
import nk.bluefrog.library.utils.Helper;
import nk.bluefrog.library.utils.PrefManger;
import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.utils.Constants;

public class ChangePassword extends BluefrogActivity {

    private EditText etMobileNumber;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        initViews();

    }

    private void initViews() {

        setToolBar("Change Password","");

        etMobileNumber = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_pwd);


    }

    private String strResponse;

    public void updatePassword(View view) {

        if(validate()){

            Constants.showDottedProgress(this, getString(R.string.wait));

            final String keys[] ={"MPEO_MobileNo","Password"};//9849045642

            final String values[] ={etMobileNumber.getText().toString(),etPassword.getText().toString()};

            /*{"Mobile_No":"7306676544","Password":"999"}*/

/*
            new Thread(){

                @Override
                public void run() {
                    WebserviceCall call = new WebserviceCall();

                    strResponse = call.callCService(Constants.BASE_URL, Constants.METHOD_CHANGE_PASSWORD, keys, values);

                    handleLogin.sendEmptyMessage(0);
                }
            }.start();
*/

        }

    }

    @SuppressLint("HandlerLeak")
    private Handler handleLogin = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            Constants.closeDottedProgress();

            if(strResponse!=null){


                parseResponse(strResponse);



                //finish();

            }
        }
    };

    private void parseResponse(String strResponse) {



        Log.d("response",strResponse);

        /*{\"status\" :\"200\",\"Data\":Success}*/

        try {
            JSONObject resObject = new JSONObject(strResponse);


            if(resObject.getString("Status").contentEquals("$200")){

                PrefManger.putSharedPreferencesBoolean(this,"isPasswordChanged",true);

                showSuccessAlert();

            }




        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    private void showSuccessAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        // Setting Dialog Title
        alertDialog.setTitle(R.string.app_name);
        //alertDialog.setIcon(R.drawable.app);
        alertDialog.setCancelable(false);
        // Setting Dialog Message
        alertDialog.setMessage("Password updated successfully");
        alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        alertDialog.show();
    }

    private boolean validate(){

        boolean flag = true;

        if(etMobileNumber.getText().toString().isEmpty()){
            flag=false;
            Helper.showToast(this,getString(R.string.enter_mobile_number));

        }else if(!Helper.isValidMobile(etMobileNumber)){
            flag=false;
            Helper.showToast(this,getString(R.string.enter_valid_mobile_number));
        }else if(etPassword.getText().toString().isEmpty()){
            flag=false;
            Helper.showToast(this,getString(R.string.enter_pwd));
        }

        return flag;

    }
}
