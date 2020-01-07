package nk.bluefrog.library.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = SmsReceiver.class.getSimpleName();
    // SMS provider identification
    // It should match with your SMS gateway origin
    // You can use MSGIND, TESTER and ALERTS as sender ID
    // If you want custom sender Id, approve MSG91 to get one
    public static String SMS_ORIGIN = "BLUFRG";
    private static OTPListener otpListener;
    private static String receiverString;

    public static void bind(OTPListener listener, String sender) {
        otpListener = listener;
        receiverString = sender;
    }

    public static void unbind() {
        otpListener = null;
        receiverString = null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (Object aPdusObj : pdusObj) {
                    SmsMessage currentMessage = SmsMessage
                            .createFromPdu((byte[]) aPdusObj);
                    String senderAddress = currentMessage
                            .getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();
                    // Received SMS: Your OTP For Post Survey Registration is
                    // :7229, Sender: VM-BLUFRG

                    Log.e(TAG, "Received SMS: " + message + ", Sender: "
                            + senderAddress);

                    // if the SMS is not from our gateway, ignore the message
                    if (!senderAddress.toLowerCase().contains(
                            SMS_ORIGIN.toLowerCase())) {
                        Log.e(TAG, "SMS is not for our app!");
                        return;
                    }

                    try {
                        Pattern pattern = Pattern.compile("[0-9]+");
                        Matcher matcher = pattern.matcher(message);

                        if (matcher.find()) {
                            String msg = matcher.group(0);
                            if (msg.length() >= 3) {
                                if (otpListener != null) {
                                    otpListener.otpReceived(msg);
                                }
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }

                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }


    public interface OTPListener {
        public void otpReceived(String messageText);
    }

}

