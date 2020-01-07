package nk.bluefrog.library.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import nk.bluefrog.library.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by nagendra on 22/3/17.
 */


public class Helper {

    /**
     * Declare version of the application
     */
    public static String Version = "1.0";
    // The multiplication table
    static int[][] d = new int[][]{{0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
            {1, 2, 3, 4, 0, 6, 7, 8, 9, 5}, {2, 3, 4, 0, 1, 7, 8, 9, 5, 6},
            {3, 4, 0, 1, 2, 8, 9, 5, 6, 7}, {4, 0, 1, 2, 3, 9, 5, 6, 7, 8},
            {5, 9, 8, 7, 6, 0, 4, 3, 2, 1}, {6, 5, 9, 8, 7, 1, 0, 4, 3, 2},
            {7, 6, 5, 9, 8, 2, 1, 0, 4, 3}, {8, 7, 6, 5, 9, 3, 2, 1, 0, 4},
            {9, 8, 7, 6, 5, 4, 3, 2, 1, 0}};
    // The permutation table
    static int[][] p = new int[][]{{0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
            {1, 5, 7, 6, 2, 8, 3, 0, 9, 4}, {5, 8, 0, 3, 7, 9, 6, 1, 4, 2},
            {8, 9, 1, 6, 0, 4, 3, 5, 2, 7}, {9, 4, 5, 3, 1, 2, 6, 8, 7, 0},
            {4, 2, 8, 6, 5, 7, 3, 9, 0, 1}, {2, 7, 9, 3, 8, 0, 6, 4, 1, 5},
            {7, 0, 4, 6, 9, 1, 3, 2, 5, 8}};

    /**
     * this method is used to validate the name
     *
     * @param name-entered name
     * @return
     */
    public static boolean validateName(String name) {
        String spl = "!@#$%^&*()+=-[]\\\';,./{}|\":<>?0987654321";
        if (name.length() > 2) {
            boolean flag = true;
            for (int i = 0; i < name.length(); i++) {
                if (spl.indexOf(name.charAt(i)) != -1) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                return true;
            }
        }
        return false;
    }

    /**
     * this method is used for navigation from one class to another class
     *
     * @param context-Object of Context, context from where the activity is going
     *                       to start.
     * @param cls-class      to move from current class
     */
    public static void moveActivity(Context context, Class cls) {
        Intent intent = new Intent(context, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }

    /**
     * custom toast to show message to user
     *
     * @param context-Object of Context, context from where the activity is going
     *                       to start.
     * @param msg-           Message String that represents toast message
     */
    public static void showToast(Context context, String msg) {
        try {
            View layout = LayoutInflater.from(context).inflate(R.layout.layout_custome_toast, null);

            TextView text = (TextView) layout.findViewById(R.id.txtToastMsg);
            text.setText(msg);

            Toast toast = new Toast(context);
            toast.setGravity(Gravity.BOTTOM, 0, 150);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * method to get selected spinner index
     *
     * @param spn-name of the particular spinner
     * @return
     */
    public static String getSpinnerIndex(Spinner spn) {
        return spn.getSelectedItemPosition() + "".trim();
    }

    /**
     * method to get selected spinner index if it is YES/NO.
     *
     * @param spn-name of the particular spinner.
     * @return
     */
    public static String getSpinnerYesNo(Spinner spn) {
        if (spn.getSelectedItemPosition() == 1)
            return "Y";
        else
            return "N";
    }

    /**
     * method to read data residing in the text file
     *
     * @param context-Object      of Context, context from where the activity is going
     *                            to start.
     * @param resourceID-resource Id from which the text file is acessing from
     * @return
     */

    public static String readTextFile(Context context, int resourceID) {

        InputStream is = context.getResources().openRawResource(resourceID);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            int code = is.read();
            while (code != -1) {
                baos.write(code);
                code = is.read();
            }
            is.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return baos.toString();
    }

    public static boolean checkInternetConnection(Context paramContext) {
        try {
            ConnectivityManager localConnectivityManager = (ConnectivityManager) paramContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            if ((localConnectivityManager.getActiveNetworkInfo() != null) && (localConnectivityManager.getActiveNetworkInfo().isAvailable()) && (localConnectivityManager.getActiveNetworkInfo().isConnected())) {
                return true;
            }
            Toast.makeText(paramContext, paramContext.getResources().getString(R.string.no_internet_available), Toast.LENGTH_SHORT).show();
            return false;
        } catch (Exception localException) {
            Toast.makeText(paramContext, localException.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    /**
     * This method is used to show alert message with OK
     *
     * @param context-Object of Context, context from where the activity is going
     *                       to start.
     * @param msg-Message    String that represents alert message.
     */
    public static void AlertMsg(Context context, String msg) {
        Builder ab = new Builder(context, R.style.MyAlertDialogStyle);
//        ab.setTitle("Message!");
        ab.setMessage(msg);
        ab.setCancelable(false);
        ab.setPositiveButton(context.getString(R.string.alert_ok), null);
        ab.show();

    }


    /**
     * This method is used to separate the values from bulk data wih separation
     *
     * @param data-data           with some separations like $*_.
     * @param DELIMETER-Delimiter to separate values from bulk
     * @param al_id-              Array list which is useful to store id's
     * @param al_name-Array       list which is useful to store names.
     */
    public static void separateValueById(String[] data, String DELIMETER,
                                         ArrayList<String> al_id, ArrayList<String> al_name) {
        al_id.clear();
        al_name.clear();
        String[] temp;
        for (int i = 0; i < data.length; i++) {
            temp = data[i].split("\\" + DELIMETER);
            if (temp.length > 0) {
                al_id.add(temp[0]);
                al_name.add(temp[1]);
            }
        }
    }

    /**
     * This method is used to set data to spinner(Dropdown)
     *
     * @param context-Object of Context, context from where the activity is going
     *                       to start.
     * @param spn-name       of the particular spinner.
     * @param values-List    of values that will be displayed in Dropdown.
     * @param msg-Message    String that represents message.
     */
    public static void setSpinnerData(Context context, Spinner spn,
                                      ArrayList<String> values, String msg) {

        ArrayAdapter<String> aa = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item);
        aa.add(msg);
        for (String name : values) {
            aa.add(name);
        }
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn.setAdapter(aa);
    }

    public static void setSpinnerData(Context context, Spinner spn,
                                      String[] values) {

        ArrayAdapter<String> aa = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item);

        for (String name : values) {
            aa.add(name);
        }
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn.setAdapter(aa);
    }


    public static void setSpinnerData(Context context, Spinner spn,
                                      String[] values, final int color, String splitchar, int pos) {

        ArrayAdapter<String> aa = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (position == 0)
                    ((TextView) v).setTextColor(color);
                else
                    ((TextView) v).setTextColor(Color.BLACK);
                return v;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);

                TextView item = (TextView) v;
                if (position == 0) {
                    item.setTextColor(color);
                } else
                    ((TextView) v).setTextColor(Color.BLACK);

                return v;
            }
        };
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        for (String name : values) {
            aa.add(name.split("\\|")[pos]);
        }
        spn.setAdapter(aa);
        aa.notifyDataSetChanged();
    }

    public static void setSpinnerData(Context context, Spinner spn,
                                      String[] values, final int color) {

        ArrayAdapter<String> aa = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (position == 0)
                    ((TextView) v).setTextColor(color);
                else
                    ((TextView) v).setTextColor(Color.BLACK);
                return v;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);

                TextView item = (TextView) v;
                if (position == 0) {
                    item.setTextColor(color);
                } else
                    ((TextView) v).setTextColor(Color.BLACK);

                return v;
            }
        };
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        for (String name : values) {
            aa.add(name);
        }
        spn.setAdapter(aa);
        aa.notifyDataSetChanged();
    }

    public static void setSpinnerDataLL(Context context, Spinner spn,
                                        List<List<String>> values, String msg, int pos, final int color) {

        ArrayAdapter<String> aa = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (position == 0)
                    ((TextView) v).setTextColor(color);
                else
                    ((TextView) v).setTextColor(Color.BLACK);
                return v;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);


                TextView item = (TextView) v;
                if (position == 0) {
                    item.setTextColor(color);
                } else
                    ((TextView) v).setTextColor(Color.BLACK);

                final TextView finalItem = item;
                item.post(new Runnable() {
                    @Override
                    public void run() {
                        finalItem.setSingleLine(false);
                    }
                });


                return v;
            }
        };
        aa.add(msg);
        for (int i = 0; i < values.size(); i++) {
            aa.add(values.get(i).get(pos).trim());
        }

        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn.setAdapter(aa);
    }

    /**
     * This method is used to set default data to spinner(Dropdown)
     *
     * @param context-Object of Context, context from where the activity is going
     *                       to start.
     * @param spn-name       of the particular spinner.
     * @param values-List    of values that will be displayed in Dropdown.
     * @param msg-Message    String that represents message.
     */
    public static void setDefaultSpinnerData(Context context, Spinner spn,
                                             ArrayList<String> values, String msg) {

        ArrayAdapter<String> aa = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item);
        aa.add(msg);
        for (String name : values) {
            aa.add(name);
        }
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn.setAdapter(aa);
    }

    /**
     * This method is used to set data to auto complete text view.
     *
     * @param context-Object of Context, context from where the activity is going
     *                       to start.
     * @param act-name       of the auto complete text view
     * @param values-values  that will be displayed in auto complete text view.
     */
    public static void setAutoCompleteTextData(Context context, AutoCompleteTextView act, ArrayList<String> values) {

        ArrayAdapter<String> actAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_dropdown_item, values);
        act.setAdapter(actAdapter);

    }

    /**
     * this method is used to get image string image file path.
     *
     * @param path-image path of the captured image.
     * @return
     */
    public static String getImageStringFromPath(String path) {
        Bitmap bm = null;
        if (path.contains("http")) {

            URL url = null;
            try {
                url = new URL(path);
                bm = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else {
            bm = BitmapFactory.decodeFile(path);
        }

        if (bm == null) {
            return "";
        } else {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte[] byteArrayImage = baos.toByteArray();

            return android.util.Base64.encodeToString(byteArrayImage, android.util.Base64.DEFAULT);

        }
    }

    public static String getImageString(Bitmap bm) {
        if (bm == null) {
            return "";
        } else {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte[] byteArrayImage = baos.toByteArray();

            return android.util.Base64.encodeToString(byteArrayImage, android.util.Base64.DEFAULT);

        }
    }

    /**
     * This method is used to check whether the view is Empty or Zero.
     *
     * @param v-View to validate the value.
     * @return
     */
    public static boolean isViewEmptyOrZero(View v) {
        boolean res = true;
        if (v instanceof Spinner) {
            res = ((Spinner) v).getSelectedItemPosition() == 0 ? true : false;
        } else if (v instanceof EditText) {
            res = ((EditText) v).getText().toString().length() == 0 ? true
                    : false;
        }
        if (res)
            setViewFocus(v);
        return res;
    }

    /**
     * This method is used to get the value of the edit text.
     *
     * @param v-name of the Edit text view.
     * @return
     */
    public static String getETValue(EditText v) {

        return v.getText().toString().trim();
    }

    /**
     * This method is used to get the value of the entered value in edit text.
     *
     * @param v-name of the edit text view.
     * @return
     */
    public static String getETValuebyZero(EditText v) {

        String val = v.getText().toString().trim();
        return val.length() > 0 ? val : "NA";
    }

    /**
     * This method is used to get the value of the text view.
     *
     * @param v-name of the text view.
     * @return
     */
    public static String getTVValuebyZero(TextView v) {
        String val = v.getText().toString().trim();
        return val.length() > 0 ? val : "0";
    }

    /**
     * This method is used to keep focus on view
     *
     * @param v-name of the view
     */

    public static void setViewFocus(View v) {
        v.setFocusableInTouchMode(true);
        v.setFocusable(true);
        v.requestFocus();
    }

    /**
     * This method is used to get the hint value of the edit text view.
     *
     * @param et-name if the edit text view.
     * @return
     */
    public static String getETHint(EditText et) {
        return et.getHint().toString().trim();
    }

    /**
     * This method is used to set error and focus on that Edit text view
     *
     * @param et-name of the edit text view.
     */
    public static void setETError(EditText et) {
        et.setError(getETHint(et));
        setViewFocus(et);
    }

    /**
     * This method is used to set error with custom message
     *
     * @param et
     * @param msg
     */
    public static void setETError(EditText et, String msg) {
        et.setError(msg);
        setViewFocus(et);
    }

    /**
     * This method is used to get selected item id from selected spinner
     *
     * @param spn-name    of the spinner.
     * @param al_id-Array list name which contains ID's
     * @return
     */
    public static String getSpinnerID(Spinner spn, ArrayList<String> al_id) {
        String res = "0";
        try {
            res = al_id.get(spn.getSelectedItemPosition() - 1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return res;
    }

    /**
     * This method is used to show alert dialog box for force close application
     *
     * @param context - Object of Context, context from where the activity is going
     *                to start.
     * @param msg     - Message String that represents alert box message
     * @throws Exception
     */
    public static void exit(final Activity context, final String msg) {

        Builder alert = new Builder(context, R.style.MyAlertDialogStyle);

        alert.setTitle("EXIT !");
        alert.setMessage(msg);
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    context.finishAffinity();
                } else {
                    context.finish();
                }
                System.exit(0);


            }
        });
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.show();

    }

    /**
     * This method is used to validate the entered mobile number in Edit text view.
     *
     * @param mobileNo-Entered value in the Edittext.
     * @return
     */
    public static boolean isValidMobile(EditText mobileNo) {

        if (mobileNo == null) return false;

        String mobile = mobileNo.getText().toString().trim();

        if (TextUtils.isEmpty(mobile)) return false;

        boolean res = false;
        if (mobile.length() == 10
                && (mobile.startsWith("9") || mobile.startsWith("8") || mobile
                .startsWith("7") || mobile.startsWith("6")))
            res = true;
        return res;
    }

    /**
     * this method is used to get image bitmap image file path.
     *
     * @param filePath-image path of the captured image.
     * @return
     */
    public static Bitmap getImage(String filePath) {
        Bitmap bmp = null;
        try {
            bmp = BitmapFactory.decodeFile(filePath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp = Bitmap.createScaledBitmap(bmp, 500, 800, true);
            bmp.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte b[] = baos.toByteArray();
            Bitmap img = BitmapFactory.decodeByteArray(b, 0, b.length);
            File f = new File(filePath);
            ExifInterface exif = new ExifInterface(f.getPath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            int angle = 0;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                angle = 90;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                angle = 180;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                angle = 270;
            }
            Matrix mat = new Matrix();
            mat.postRotate(angle);

            bmp = Bitmap.createBitmap(img, 0, 0, 500, 800, mat, true);
            ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 70, baos1);
            byte b1[] = baos1.toByteArray();

        } catch (Exception e) {

        }
        return bmp;
    }

    /**
     * this method is used to get image bitmap image file path.
     *
     * @param filePath-image path of the captured image.
     * @return
     */
    public static Bitmap getImageWithoutCrop(String filePath) {
        Bitmap bmp = null;
        try {
            bmp = BitmapFactory.decodeFile(filePath);

            File f = new File(filePath);
            ExifInterface exif = new ExifInterface(f.getPath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            int angle = 0;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                angle = 90;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                angle = 180;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                angle = 270;
            }
            Matrix mat = new Matrix();
            mat.postRotate(angle);

            bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), mat, true);

        } catch (Exception e) {

        }
        return bmp;
    }

    /**
     * This method is used to check network availability
     *
     * @param context-Object of Context, context from where the activity is going to start.
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * This method is used to show alert dialog box for force close from current screen.
     *
     * @param context - Object of Context, context from where the activity is going
     *                to start.
     * @param msg     - Message String that represents alert box message
     * @throws Exception
     */
    public static void alert(Context context, String msg, boolean isCancelable, final IL il) {
        try {
            AlertDialog.Builder alertDialogBuilder = getBuilder(context);
            alertDialogBuilder.setMessage(msg);
            alertDialogBuilder.setCancelable(isCancelable);
            alertDialogBuilder.setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    if (il != null) {
                        il.onSuccess();
                    }
                }
            });

            if (isCancelable) {
                alertDialogBuilder.setOnKeyListener(new DialogInterface.OnKeyListener() {

                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            dialog.dismiss();
                        }
                        return false;
                    }
                });
            }
            AlertDialog alertDialog = alertDialogBuilder.create(); // create
            // alert
            // dialog
            alertDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method is used to show alert dialog box for force close application
     *
     * @param context - Object of Context, context from where the activity is going
     *                to start.
     * @param msg     - Message String that represents alert box message.
     * @throws Exception
     */
    public static void confirmDialog(Context context, String msg, String positiveBtnText, String negativeBtnText, final IL il) {
        try {
            AlertDialog.Builder alertDialogBuilder = getBuilder(context);
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setMessage(msg);
            alertDialogBuilder.setPositiveButton(positiveBtnText, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (il != null)
                        il.onSuccess();
                    dialog.dismiss();
                }
            });

            alertDialogBuilder.setNegativeButton(negativeBtnText, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (il != null)
                        il.onCancel();
                    dialog.dismiss();
                }
            });

            alertDialogBuilder.setOnKeyListener(new DialogInterface.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (il != null)
                            il.onCancel();
                        dialog.dismiss();
                    }
                    return false;
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create(); // create
            // alert
            // dialog
            alertDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Aadhaar Number Validation ------------------------------------------------------------------

    private static AlertDialog.Builder getBuilder(Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
        alertDialogBuilder.setCancelable(false);
        return alertDialogBuilder;
    }

    public static boolean isValidAadhaarNo(EditText aadharNumber) {
        String text = aadharNumber.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        Pattern aadharPattern = Pattern.compile("\\d{12}");
        boolean isValidAadhar = aadharPattern.matcher(text).matches();
        if (isValidAadhar) {
            isValidAadhar = validateVerhoeff(text);
        }
        return isValidAadhar;
    }

    /*
     * Validates that an entered number is Verhoeff compliant.
     * NB: Make sure the check digit is the last one.
     */
    public static boolean validateVerhoeff(String num) {

        int c = 0;
        int[] myArray = stringToReversedIntArray(num);

        for (int i = 0; i < myArray.length; i++) {
            c = d[c][p[(i % 8)][myArray[i]]];
        }

        return (c == 0);
    }

    /*
     * Converts a string to a reversed integer array.
     */
    private static int[] stringToReversedIntArray(String num) {

        int[] myArray = new int[num.length()];

        for (int i = 0; i < num.length(); i++) {
            myArray[i] = Integer.parseInt(num.substring(i, i + 1));
        }

        myArray = reverse(myArray);

        return myArray;

    }

    /*
     * Reverses an int array
     */
    private static int[] reverse(int[] myArray) {
        int[] reversed = new int[myArray.length];

        for (int i = 0; i < myArray.length; i++) {
            reversed[i] = myArray[myArray.length - (i + 1)];
        }

        return reversed;
    }

    /**
     * This method returns Boolean value if spinner is selected or not.
     *
     * @param spinner-name of the spinner
     * @return
     */
    public static boolean isNotSelected(Spinner spinner) {
        if (spinner == null) {
            return true;
        } else if (spinner.getSelectedItemPosition() == 0) {
            spinner.setFocusable(true);
            spinner.setFocusableInTouchMode(true);
            spinner.requestFocus();
            return true;
        }
        return false;
    }

    // --------------------------------------------------------------------------------------------

    /**
     * This method is used to reset entered value to the actual value.
     *
     * @param v-name of View
     */
    public static void resetToActualValue(View v) {

        EditText et = (EditText) v;
        String value = et.getText().toString().trim();
        System.out.println("Value is" + value);

        if (!value.startsWith("0.") && !value.equals("0")
                && value.startsWith("0")) {

            if (!value.contains(".")) {
                et.setText(new Long(value).toString());
                et.setSelection(et.getText().length());
            } else {
                et.setText(new Double(value).doubleValue() + "");
                et.setSelection(et.getText().length());
            }

        }
    }

    /**
     * This method is used to reset entered value to the actual value regarding interest.
     *
     * @param v-name of View
     */
    public static void resetToInterestActualValue(View v) {

        EditText et = (EditText) v;
        String value = et.getText().toString().trim();
        int beforeDotlen = value.substring(0,
                value.indexOf(".") == -1 ? 0 : value.indexOf(".")).length();
        int afterDotlen = value.substring(value.indexOf(".") + 1).length();
        System.out.println("Value is" + value);

        if (!value.startsWith("0.") && !value.equals("0")
                && value.startsWith("0")) {

            if (!value.contains(".")) {
                if (beforeDotlen < 2) {
                    et.setText(new Long(value).toString());
                    et.setSelection(et.getText().length());
                } else {
                    // Stop
                }
            } else {
                if (afterDotlen < 2) {
                    et.setText(new Double(value).doubleValue() + "");
                    et.setSelection(et.getText().length());
                }

            }

        }
    }

    /**
     * This method is used to generate unique Id with transaction date and Required Id.
     *
     * @param tranDate-Transaction Date`
     * @param ReqId-Required       Id
     * @return
     */
    public static String getTransactionID(String tranDate, String ReqId) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getDateField(tranDate));
        int yr = (int) cal.get(Calendar.YEAR);
        String year = "" + (yr > 9 ? "" + yr : "0" + yr);
        String month = (cal.get(Calendar.MONTH) + 1) > 9 ? ""
                + (cal.get(Calendar.MONTH) + 1) : "0"
                + (cal.get(Calendar.MONTH) + 1);
        String day = cal.get(Calendar.DAY_OF_MONTH) > 9 ? ""
                + cal.get(Calendar.DAY_OF_MONTH) : "0"
                + cal.get(Calendar.DAY_OF_MONTH);
        String code26 = ReqId + year + month + day;
        System.out.println("code26: " + code26);
        System.gc();
        return code26;
    }


    public static String getTransactionIDWithMin(String tranDate, String shgID) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getDateField(tranDate));
        int yr = (int) cal.get(Calendar.YEAR);
        String year = "" + (yr > 9 ? "" + yr : "0" + yr);
        String month = (cal.get(Calendar.MONTH) + 1) > 9 ? ""
                + (cal.get(Calendar.MONTH) + 1) : "0"
                + (cal.get(Calendar.MONTH) + 1);
        String day = cal.get(Calendar.DAY_OF_MONTH) > 9 ? ""
                + cal.get(Calendar.DAY_OF_MONTH) : "0"
                + cal.get(Calendar.DAY_OF_MONTH);

        String hour = cal.get(Calendar.HOUR) > 9 ? "" + cal.get(Calendar.HOUR)
                : "0" + cal.get(Calendar.HOUR);
        String min = cal.get(Calendar.MINUTE) > 9 ? ""
                + cal.get(Calendar.MINUTE) : "0" + cal.get(Calendar.MINUTE);
        String sec = cal.get(Calendar.SECOND) > 9 ? ""
                + cal.get(Calendar.SECOND) : "0" + cal.get(Calendar.SECOND);
        String code26 = shgID + year + month + day + hour + min + sec;
        System.out.println("code26: " + code26);
        System.gc();
        return code26;
    }

    /**
     * This method is used to convert date string into Date Format.
     *
     * @param date-Date in the form of string
     * @return
     */
    public static Date getDateField(String date) {
        String[] temp;
        if (date.contains("/")) {
            temp = date.split("\\/");
        } else {
            temp = date.split("\\-");
        }

        Calendar cal = Calendar.getInstance();
        if (temp[0].length() > 2) {
            cal.set(Calendar.YEAR, Integer.parseInt(temp[0]));
            cal.set(Calendar.MONTH, Integer.parseInt(temp[1]) - 1);
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(temp[2]));
        } else {
            cal.set(Calendar.YEAR, Integer.parseInt(temp[2]));
            cal.set(Calendar.MONTH, Integer.parseInt(temp[1]) - 1);
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(temp[0]));
        }
        return cal.getTime();
    }

    // Date Methods

    /**
     * This method is used to date with requested format
     *
     * @param date-date       in the form of string
     * @param symbol-required symbol in the format
     * @return
     */
    public static String getDateStringDate(String date, String symbol) {
        String[] temp;
        if (date.contains("/")) {
            temp = date.split("\\/");
        } else {
            temp = date.split("\\-");
        }
        if (temp[0].length() > 2) {
            return temp[2] + symbol + temp[1] + symbol + temp[0];
        } else {
            return temp[0] + symbol + temp[1] + symbol + temp[2];
        }

    }

    public static String getTodayDateTime() {

        return getTodayDate("yyyy-MM-dd hh:mm:ss");
    }

    public static String getTodayDate() {
        //dd-MMM-yy HH:mm:ss
        //dd-MM-yyyy HH:mm:ss
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.format(new Date());
    }

    /**
     * This method is used to get the current date
     *
     * @param formate-Required date format which will be in String.
     * @return
     */
    public static String getTodayDate(String formate) {
        SimpleDateFormat adf = new SimpleDateFormat(formate);
        return adf.format(new Date());
    }

    public static String getTodayTime() {
        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        return dateFormat.format(new Date());
    }

    /**
     * This method is used to get current date by passing required format.
     *
     * @param formate-required format
     * @return
     */
    public static String getCurrentTodayDate(String formate) {
        SimpleDateFormat adf = new SimpleDateFormat(formate);
        return adf.format(new Date());
    }

    /**
     * This method is used to validate the date
     *
     * @param date-Date to validate
     * @return
     */
    public static boolean validateDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        if (year > 1900) {
            if (month < 12) {
                if (month == 0 || month == 2 || month == 4 || month == 6
                        || month == 7 || month == 9 || month == 11) {
                    if (day < 32) {
                        return true;
                    }
                } else if (month == 3 || month == 5 || month == 8
                        || month == 10) {
                    if (day < 31) {
                        return true;
                    }
                } else if (month == 1) {
                    if (year % 4 == 0 && day < 30) {
                        return true;
                    } else if (day < 29) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * This method is used to check whether the date is Future date
     *
     * @param firstDate-First   date to validate.
     * @param secondDate-Second date to validate.
     * @return
     */
    public static boolean isFutureDate(Date firstDate, Date secondDate) {
        if (validateDate(firstDate) && validateDate(secondDate)) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(secondDate);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(firstDate);
            int year1 = cal1.get(Calendar.YEAR);
            int month1 = cal1.get(Calendar.MONTH);
            int day1 = cal1.get(Calendar.DAY_OF_MONTH);
            if (year > year1) {
                return true;
            } else if (year == year1) {
                if (month > month1) {
                    return true;
                } else if (month == month1) {
                    if (day > day1) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * This method is used for comparison between two dates.
     *
     * @param firstDate-First   Date to compare.
     * @param secondDate-Second Date to compare.
     * @return
     */
    public static boolean compareDates(Date firstDate, Date secondDate) {
        if (validateDate(firstDate) && validateDate(secondDate)) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(secondDate);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(firstDate);
            int year1 = cal1.get(Calendar.YEAR);
            int month1 = cal1.get(Calendar.MONTH);
            int day1 = cal1.get(Calendar.DAY_OF_MONTH);
            if (year > year1) {
                return true;
            } else if (year == year1) {
                if (month > month1) {
                    return true;
                } else if (month == month1) {
                    if (day >= day1) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * This method is used set the values to the views
     *
     * @param views-Views   that to be displayed.
     * @param values-values that are to be displayed on views.
     */
    public static void setValues(View views[], String values[]) {
        for (int i = 0; i < views.length; i++) {
            System.out.println(i + " View:" + views[i] + " value:" + values[i]);
            View v = views[i];
            if (v instanceof EditText) {
                EditText et = (EditText) v;
                et.setText(values[i]);
            } else if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(values[i]);
            } else if (v instanceof Spinner) {
                Spinner sp = (Spinner) v;
                sp.setSelection(Integer.parseInt(values[i]));
            }
        }
    }

    /**
     * This method is used to validate the view.
     *
     * @param view-name of the view that to be validate
     * @return
     */
    public static boolean isViewValidate(View view[]) {
        boolean flag = true;

        for (int i = 0; i < view.length; i++) {
            System.out.println("i:" + i);
            if (view[i] instanceof EditText) {

                EditText et = (EditText) view[i];

                if (et.getText().toString().trim().length() == 0
                        || et.getText().toString().trim().equals("")) {
                    flag = false;
                    setETError((EditText) view[i], "Please Enter");
                    break;
                }
            }
        }

        return flag;
    }

    /**
     * This method is used to get IMEI Number
     *
     * @param av-Activity name
     * @return
     */
    public static String getIMEINumber(Activity av) {
        TelephonyManager tm = (TelephonyManager) av
                .getSystemService(Context.TELEPHONY_SERVICE);

        String IMEINumber = tm.getDeviceId();
        if (IMEINumber == null || IMEINumber.length() == 0) {
            return "00000000000000000000";
        }
        return IMEINumber;
    }

    /**
     * This method is used to set spinner data with Icon
     *
     * @param context-Object of Context, context from where the activity is going
     *                       to start.
     * @param spn-name       of the spinner.
     * @param values-values  that to be displayed in dropdown.
     * @param msg-Message    String that represents dropdown message.
     */
    public static void setSpinnerDataWithIcon(Context context, Spinner spn,
                                              ArrayList<String> values, String msg) {
        final Context finalContext = context;
        ArrayAdapter<String> aa = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextColor(Color.WHITE);
                return v;
            }

            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                v.setBackgroundColor(Color.WHITE);
                //((TextView) v).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.next, 0);

                if (position == 0) {
                    ((TextView) v).setTextColor(finalContext.getResources()
                            .getColor(R.color.colorAccent));


                } else {
                    ((TextView) v).setTextColor(Color.BLACK);
                }
                if (position == 1) {
                    ((TextView) v).setCompoundDrawablesWithIntrinsicBounds(0, 0, (int) R.drawable.launcher, 0);
                } else if (position == 2) {
                    ((TextView) v).setCompoundDrawablesWithIntrinsicBounds(0, 0, (int) R.drawable.launcher, 0);
                } else if (position == 3) {
                    ((TextView) v).setCompoundDrawablesWithIntrinsicBounds(0, 0, (int) R.drawable.launcher, 0);
                } else if (position == 4) {
                    ((TextView) v).setCompoundDrawablesWithIntrinsicBounds(0, 0, (int) R.drawable.launcher, 0);
                }

                return v;
            }

        };
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        aa.add(msg);
        for (String name : values) {
            aa.add(name);
        }
        spn.setAdapter(aa);
        aa.notifyDataSetChanged();
    }

    /**
     * This method is used to get sim number
     *
     * @param context-Object of Context, context from where the activity is going
     *                       to start.
     * @return
     */
    public static String getSimNumber(Context context) {
        TelephonyManager info = (TelephonyManager) context
                .getApplicationContext().getSystemService(
                        Context.TELEPHONY_SERVICE);
        // "89914901040598510172";//
        // showToast(context, "Sim:" + info.getSimSerialNumber());

        return info.getSimSerialNumber();

    }

    /**
     * This method is used to set error using custom layout
     *
     * @param activity-Name    of the activity
     * @param viewAnchor-name  of the View Anchor
     * @param error_msg-String Message that represents error.
     * @param tf-required      Typeface
     */
    public static void setError(Activity activity, View viewAnchor,
                                String error_msg, Typeface tf) {

        if (viewAnchor instanceof EditText) {
            setViewFocus(((EditText) viewAnchor));
        } else if (viewAnchor instanceof Spinner) {
            // Error icon
            TextView tv = (TextView) ((Spinner) viewAnchor).getSelectedView();
            tv.setError("");
            setViewFocus(((Spinner) viewAnchor));
        }
        // Initialize a pop up window type
        PopupWindow popupWindow = new PopupWindow(activity);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));
        // Initialize Text View & set error msg
        View popupView = activity.getLayoutInflater().inflate(
                R.layout.textview_hint, null);
        TextView txtView = (TextView) popupView.findViewById(R.id.tv_errormsg);
        txtView.setText(error_msg);
        txtView.setTypeface(tf);
        txtView.setTextColor(Color.WHITE);
        // Add popupview to popupWindow & set width & heigth to popupwindow
        popupWindow.setContentView(popupView);
        popupWindow.setWidth(160);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        // Show popupwindow
        popupWindow.showAsDropDown(viewAnchor, viewAnchor.getWidth()
                - popupWindow.getWidth(), 0 - viewAnchor.getHeight() / 2);
        // Set Background image to popupView
        if (popupWindow.isAboveAnchor()) {
            popupView
                    .setBackgroundResource(R.drawable.popup_inline_error_above_holo_light);
        } else {
            popupView
                    .setBackgroundResource(R.drawable.popup_inline_error_holo_light);
        }
    }

    /**
     * This method is used to set error to spinner by using custom layout.
     *
     * @param activity-name of the activity.
     * @param spinner-name  of the spinner.
     */
    public static void setSPError(Activity activity, Spinner spinner) {
        // Focus
        spinner.setFocusable(true);
        spinner.setFocusableInTouchMode(true);
        spinner.requestFocus();
        // Error icon
        ((TextView) spinner.getSelectedView()).setError("");
        ((TextView) spinner.getSelectedView()).setTextColor(Color.WHITE);
        // Initialize a pop up window type
        PopupWindow popupWindow = new PopupWindow(activity);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));
        // Initialize Text View & set error msg
        View popupView = activity.getLayoutInflater().inflate(
                R.layout.textview_hint, null);
        TextView txtView = (TextView) popupView.findViewById(R.id.tv_errormsg);
        txtView.setTextColor(Color.WHITE);
        txtView.setText(spinner.getSelectedItem().toString().trim());
        // Add popupview to popupWindow & set width & heigth to popupwindow
        popupWindow.setContentView(popupView);
        popupWindow.setWidth(160);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        // Show popupwindow
        popupWindow.showAsDropDown(spinner,
                spinner.getWidth() - popupWindow.getWidth(),
                0 - spinner.getHeight() / 2);
        // Set Background image to popupView
        if (popupWindow.isAboveAnchor()) {
            popupView
                    .setBackgroundResource(R.drawable.popup_inline_error_above_holo_light);
        } else {
            popupView
                    .setBackgroundResource(R.drawable.popup_inline_error_holo_light);
        }
    }

    /**
     * This method is used to get Date after Days.
     *
     * @param str-String      in Date format.
     * @param in-required     days in between.
     * @param symbol-required symbol for Date Format.
     * @return
     */
    public static Long dateAfterDays(String str, int in, String symbol) {

        String arr[] = str.split("\\" + symbol);
        Calendar cal = Calendar.getInstance();
        if (arr[0].length() > 2) {
            cal.set(Calendar.YEAR, Integer.parseInt(arr[0]));
            cal.set(Calendar.MONTH, Integer.parseInt(arr[1]) - 1);
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(arr[2]));
        } else {
            cal.set(Calendar.YEAR, Integer.parseInt(arr[2]));
            cal.set(Calendar.MONTH, Integer.parseInt(arr[1]) - 1);
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(arr[0]));
        }
        cal.add(Calendar.DATE, in); // add how many days we want

        return cal.getTime().getTime();
    }

    public static void setSpinnerLabelValueData(final Activity activity, final Spinner spn,
                                                final String[] values, final String labelStr) {


        ArrayAdapter<String> aa = new ArrayAdapter<String>(activity,
                R.layout.spinnerwith_label) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = activity.getLayoutInflater().inflate(R.layout.spinnerwith_label, parent, false);
                TextView label = (TextView) v.findViewById(R.id.label);


                label.setText(labelStr + "");
                label.setTextColor(activity.getResources()
                        .getColor(R.color.colorAccent));
                TextView text1 = (TextView) v.findViewById(R.id.text1);
                spn.setTag(position + "");
                text1.setText(values[position]);

                return v;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                // TODO Auto-generated method stub
                View v = super.getDropDownView(position, convertView, parent);
                if (position == 0) {
                    ((TextView) v).setTextColor(activity.getResources()
                            .getColor(R.color.colorAccent));
                } else {
                    ((TextView) v).setTextColor(Color.BLACK);
                }
                return v;
            }
        };
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        for (int i = 0; i < values.length; i++) {
            aa.add(values[i]);
        }

        spn.setAdapter(aa);
        aa.notifyDataSetChanged();
    }

    /**
     * This method is used to show alert message with OK
     *
     * @param context-Object of Context, context from where the activity is going
     *                       to start.
     * @param msg-Message    String that represents alert message.
     */
    public static void AlertMesg(Context context, String msg) {
        Builder ab = new Builder(context, R.style.MyAlertDialogStyle);
//        ab.setTitle("Message!");
        ab.setMessage(msg);
        ab.setCancelable(false);
        ab.setPositiveButton(context.getString(R.string.alert_ok), null);
        ab.show();

    }

    public static void setSpinnerLabelValueData(final Activity activity, final Spinner spn,
                                                final List<List<String>> values, final String valueStr,
                                                final int idPos, final int valPos, final String labelStr) {


        ArrayAdapter<String> aa = new ArrayAdapter<String>(activity,
                R.layout.spinnerwith_label) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = activity.getLayoutInflater().inflate(R.layout.spinnerwith_label, parent, false);
                TextView label = (TextView) v.findViewById(R.id.label);

                label.setText(labelStr + "");
                label.setTextColor(activity.getResources()
                        .getColor(R.color.colorAccent));
                TextView text1 = (TextView) v.findViewById(R.id.text1);
                if (position == 0) {
                    text1.setText(valueStr);
                    spn.setTag("0");
                } else {
                    spn.setTag(values.get(position - 1).get(idPos).toString().trim());
                    text1.setText(values.get(position - 1).get(valPos).toString().trim());
                }

                return v;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                // TODO Auto-generated method stub
                View v = super.getDropDownView(position, convertView, parent);
                if (position == 0) {
                    ((TextView) v).setTextColor(activity.getResources()
                            .getColor(R.color.colorAccent));
                } else {
                    ((TextView) v).setTextColor(Color.BLACK);
                }
                return v;
            }
        };
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        aa.add(valueStr);

        for (int i = 0; i < values.size(); i++) {
            aa.add(values.get(i).get(valPos).toString().trim());
        }

        spn.setAdapter(aa);
        aa.notifyDataSetChanged();
    }


    public interface IL {

        void onSuccess();

        void onCancel();
    }

}
