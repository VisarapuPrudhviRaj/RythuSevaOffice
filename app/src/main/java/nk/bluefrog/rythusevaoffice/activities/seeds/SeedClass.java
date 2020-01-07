package nk.bluefrog.rythusevaoffice.activities.seeds;

import java.io.Serializable;


public class SeedClass implements Serializable {
    String UID;
    String dist;
    String mandal;

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getDist() {
        return dist;
    }

    public void setDist(String dist) {
        this.dist = dist;
    }

    public String getMandal() {
        return mandal;
    }

    public void setMandal(String mandal) {
        this.mandal = mandal;
    }

    public String getGp() {
        return gp;
    }

    public void setGp(String gp) {
        this.gp = gp;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getTypeofSeed() {
        return typeofSeed;
    }

    public void setTypeofSeed(String typeofSeed) {
        this.typeofSeed = typeofSeed;
    }

    public String getSeedSubType() {
        return seedSubType;
    }

    public void setSeedSubType(String seedSubType) {
        this.seedSubType = seedSubType;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGps() {
        return gps;
    }

    public void setGps(String gps) {
        this.gps = gps;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSendFlag() {
        return sendFlag;
    }

    public void setSendFlag(String sendFlag) {
        this.sendFlag = sendFlag;
    }

    String gp;
    String village;
    String typeofSeed;
    String seedSubType;
    String quantity;
    String cost;
    String address;
    String gps;
    String image;
    String sendFlag;


}


