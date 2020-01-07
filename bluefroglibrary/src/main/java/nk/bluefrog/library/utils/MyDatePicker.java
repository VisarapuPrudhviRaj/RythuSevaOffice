package nk.bluefrog.library.utils;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

import static nk.bluefrog.library.utils.Helper.getTodayDate;

/**
 * Created by nagendra on 22/3/17.
 */


@SuppressLint("ValidFragment")
public class MyDatePicker extends DialogFragment {

    int day, mon, year;
    Handler hand;
    char del = '-';
    Long minDate, maxDate;


    public MyDatePicker(Handler hand, Long minDate) {
        this.hand = hand;
        this.minDate = minDate;
    }

    public MyDatePicker(Handler hand, Long minDate, Long maxDate) {
        this.hand = hand;
        this.minDate = minDate;
        this.maxDate = maxDate;
    }

    public MyDatePicker(Handler hand) {
        this.hand = hand;
    }

    public MyDatePicker(Handler hand, char del) {
        this.hand = hand;
        this.del = del;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        // TODO Auto-generated method stub
        super.onCancel(dialog);
        dialog.dismiss();
        //
        Message msg = new Message();
        Bundle extra = new Bundle();
        extra.putString("date", getTodayDate("dd-MM-yyyy"));
        msg.setData(extra);
        hand.sendMessage(msg);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Calendar cal = Calendar.getInstance();
        day = cal.get(Calendar.DAY_OF_MONTH);
        mon = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);

        OnDateSetListener listener = new OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                String sday, smon;
                if (dayOfMonth < 10)
                    sday = "0" + dayOfMonth;
                else
                    sday = "".trim() + dayOfMonth;
                if (monthOfYear < 9)
                    smon = "0" + (monthOfYear + 1);
                else
                    smon = "".trim() + (monthOfYear + 1);

                Message msg = new Message();
                Bundle extra = new Bundle();
                extra.putString("date",
                        sday + "-" + smon + "-" + year + "".trim());
                msg.setData(extra);
                hand.sendMessage(msg);
            }
        };

        DatePickerDialog dpdialog = new DatePickerDialog(getActivity(), listener,
                year, mon, day);
        if (minDate != null)
            dpdialog.getDatePicker().setMinDate(minDate);
        if ((maxDate != null))
            dpdialog.getDatePicker().setMaxDate(maxDate);

        return dpdialog;
    }

    public Long dateAfterDays(String str, int in) {

        String arr[] = str.split("\\-");
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
}
