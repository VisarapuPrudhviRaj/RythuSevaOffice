package nk.bluefrog.rythusevaoffice.activities.ICM;

import java.io.Serializable;


public class ICMClass implements Serializable {

    String NotificationID;
    String ReplyMessage;
    String LastUpdated;

    public String getNotificationID() {
        return NotificationID;
    }

    public void setNotificationID(String notificationID) {
        NotificationID = notificationID;
    }

    public String getReplyMessage() {
        return ReplyMessage;
    }

    public void setReplyMessage(String replyMessage) {
        ReplyMessage = replyMessage;
    }

    public String getLastUpdated() {
        return LastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        LastUpdated = lastUpdated;
    }
}


