package nk.bluefrog.rythusevaoffice.activities.search;

import android.content.Intent;

/**
 * Created by rajkumar on 24/12/18.
 */

public class SearchModel {

    String adharID;
    String name;
    String mobile;
    String gpId;
    String date;
    String imgUrl;
    Intent intent;


    public String getAdharID() {
        return adharID;
    }

    public void setAdharID(String adharID) {
        this.adharID = adharID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getGpId() {
        return gpId;
    }

    public void setGpId(String gpId) {
        this.gpId = gpId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }
}
