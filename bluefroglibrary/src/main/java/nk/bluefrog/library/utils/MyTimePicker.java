package nk.bluefrog.library.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by nagendra on 22/3/17.
 */

@SuppressLint("ValidFragment")
public class MyTimePicker extends DialogFragment {
    int hours, mins;
    Handler hand;



    public MyTimePicker(Handler hand) {
        this.hand = hand;
    }


    @Override
    public void onCancel(DialogInterface dialog) {
        // TODO Auto-generated method stub
        super.onCancel(dialog);
        dialog.dismiss();
        Message msg = new Message();
        Bundle extra = new Bundle();
        extra.putString("time", "");
        msg.setData(extra);
        hand.sendMessage(msg);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Calendar cal = Calendar.getInstance();
        hours = cal.get(Calendar.HOUR_OF_DAY);
        mins = cal.get(Calendar.MINUTE);
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // TODO Auto-generated method stub

                String shours, smins, format;
                mins = minute;

                if (hourOfDay == 0) {
                    hourOfDay += 12;
                    format = "AM";
                } else if (hourOfDay == 12) {
                    format = "PM";
                } else if (hourOfDay > 12) {
                    hourOfDay -= 12;
                    format = "PM";
                } else {
                    format = "AM";
                }

                hours = hourOfDay;

                if (hours < 10)
                    shours = "0" + hours;
                else
                    shours = hours + "".trim();


                if (mins < 10)
                    smins = "0" + mins;
                else
                    smins = mins + "".trim();

                Message msg = new Message();
                Bundle extra = new Bundle();
                extra.putString("time", shours + ":" + smins + format);
                msg.setData(extra);
                hand.sendMessage(msg);
            }


        };

        TimePickerDialog dialog = new TimePickerDialog(getActivity(), listener, hours, mins, false);

        return dialog;
    }
}
