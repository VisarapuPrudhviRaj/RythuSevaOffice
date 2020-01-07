package nk.bluefrog.rythusevaoffice.utils;

import android.widget.EditText;
import android.widget.TextView;


import java.util.regex.Pattern;

import nk.bluefrog.library.utils.Helper;

/**
 * Created by nagendra on 2/3/17.
 */

public class ValidationClass {
    private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String PHONE_REGEX = "^[789]\\d{9}$";
    private static final String REQUIRED_MSG = "required";
    private static final String ERROR_EMAIL_MSG = "invalid email";
    private static final String ERROR_PHONE_MSG = "invalid phone";

    public static boolean isEmailAddress(EditText editText, boolean required) {
        return isValid(editText, EMAIL_REGEX, ERROR_EMAIL_MSG, required);
    }

    public static boolean isPhoneNumber(EditText editText, boolean required) {
        return isValid(editText, PHONE_REGEX, ERROR_PHONE_MSG, required);
    }

    public static boolean isValid(EditText editText, String regex, String errMsg, boolean required) {
        String text = editText.getText().toString().trim();
        editText.setError(null);
        if (required && !hasText(editText)) return false;
        if (required && !Pattern.matches(regex, text)) {
            editText.setError(errMsg);
            return false;
        }
        ;
        return true;
    }

    public static boolean hasText(EditText editText) {
        String text = editText.getText().toString().trim();
        editText.setError(null);
        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError(REQUIRED_MSG);
            Helper.setViewFocus(editText);
            return false;
        }
        return true;
    }

    public static boolean hasYear(EditText editText) {
        String text = editText.getText().toString().trim();
        editText.setError(null);
        // length 0 means there is no text
        int year = Integer.parseInt(text);
        if (text.length() == 4 && (year >= 1800 && year <= 2017)) {
            return true;
        } else {
            editText.setError("year >=1800 && year <=2017");
            return false;

        }

    }

    public static boolean hasSelected(TextView textView, int id) {
        System.out.println("id:" + id);
        textView.setError(null);
        if (id == 0) {
            textView.setError("Select");
            Helper.setViewFocus(textView);
            return false;
        }
        return true;
    }

    double distanceBetweenTwoPoint(double srcLat, double srcLng, double desLat, double desLng) {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(desLat - srcLat);
        double dLng = Math.toRadians(desLng - srcLng);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(srcLat))
                * Math.cos(Math.toRadians(desLat)) * Math.sin(dLng / 2)
                * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;

        double meterConversion = 1609;

        return (int) (dist * meterConversion);
    }

    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == "K") {
            dist = dist * 1.609344;
        } else if (unit == "N") {
            dist = dist * 0.8684;
        }

        return (dist);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::	This function converts decimal degrees to radians						 :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::	This function converts radians to decimal degrees						 :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }


}
