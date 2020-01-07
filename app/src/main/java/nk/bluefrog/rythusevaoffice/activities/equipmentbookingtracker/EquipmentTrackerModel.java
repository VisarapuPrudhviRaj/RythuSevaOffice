package nk.bluefrog.rythusevaoffice.activities.equipmentbookingtracker;

import com.google.android.gms.common.server.converter.StringToIntConverter;

import java.io.Serializable;
import java.util.List;

/**
 * Created by rajkumar on 8/1/19.
 */

public class EquipmentTrackerModel implements Serializable {

    /*{"Booking_Id":"1703080001","Customer_Name":"ravi","Equipment_Id":"1","Barcode":"123456789632",
    "Mobile_No":"9999998888","Address":"hyb","Start_Date":"3/8/2017 12:00:00 AM",
    "End_Date":"3/9/2017 12:00:00 AM","Equipment_Rent_Arce":"0","Purpose":"work",
    "Order_Status":"Accept","Customer_GPS":"17.7118-83.99799999999999","Equipment_Rent":"500",
    "Equipment_Image_Path":"0305000001_170303175930791180.JPG","Equipment_GPS":"17.7118-83.99799999999999",
    "Equipment_Status_Id":"1","Equipment_Address":"hyb","Owner_User_Id":"0305000001","User_Name":"raju",
    "Owner_Mobile_No":"7207286365","booking_date":""}*/

    private String commodity;
    private String Booking_Id;
    private String Customer_Name;
    private String Equipment_Id;
    private String Barcode;
    private String Mobile_No;
    private String Address;
    private String Start_Date;
    private String End_Date;
    private String Customer_GPS;
    private String Equipment_Rent_Arce;
    private String Purpose;
    private String Order_Status;
    private String Equipment_Rent;
    private String Equipment_Image_Path;
    private String Equipment_GPS;
    private String Equipment_Status_Id;
    private String Equipment_Address;
    private String Owner_User_Id;
    private String User_Name;
    private String Owner_Mobile_No;
    private String booking_date;
    private String serialNo;
    private String Order_Status_ID;
    private String Customer_ID;
    private String shopName;
    private String userType;

    private List<String> row_data;

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getCustomer_ID() {
        return Customer_ID;
    }

    public void setCustomer_ID(String customer_ID) {
        Customer_ID = customer_ID;
    }



    public String getOrder_Status_ID() {
        return Order_Status_ID;
    }

    public void setOrder_Status_ID(String order_Status_ID) {
        Order_Status_ID = order_Status_ID;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getCommodity() {
        return commodity;
    }

    public void setCommodity(String commodity) {
        this.commodity = commodity;
    }

    public String getBooking_Id() {
        return Booking_Id;
    }

    public void setBooking_Id(String booking_Id) {
        Booking_Id = booking_Id;
    }

    public String getCustomer_Name() {
        return Customer_Name;
    }

    public void setCustomer_Name(String customer_Name) {
        Customer_Name = customer_Name;
    }

    public String getEquipment_Id() {
        return Equipment_Id;
    }

    public void setEquipment_Id(String equipment_Id) {
        Equipment_Id = equipment_Id;
    }

    public String getBarcode() {
        return Barcode;
    }

    public void setBarcode(String barcode) {
        Barcode = barcode;
    }

    public String getMobile_No() {
        return Mobile_No;
    }

    public void setMobile_No(String mobile_No) {
        Mobile_No = mobile_No;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getStart_Date() {
        return Start_Date;
    }

    public void setStart_Date(String start_Date) {
        Start_Date = start_Date;
    }

    public String getEnd_Date() {
        return End_Date;
    }

    public void setEnd_Date(String end_Date) {
        End_Date = end_Date;
    }

    public String getCustomer_GPS() {
        return Customer_GPS;
    }

    public void setCustomer_GPS(String customer_GPS) {
        Customer_GPS = customer_GPS;
    }

    public String getEquipment_Rent_Arce() {
        return Equipment_Rent_Arce;
    }

    public void setEquipment_Rent_Arce(String equipment_Rent_Arce) {
        Equipment_Rent_Arce = equipment_Rent_Arce;
    }

    public String getPurpose() {
        return Purpose;
    }

    public void setPurpose(String purpose) {
        Purpose = purpose;
    }

    public String getOrder_Status() {
        return Order_Status;
    }

    public void setOrder_Status(String order_Status) {
        Order_Status = order_Status;
    }

    public String getEquipment_Rent() {
        return Equipment_Rent;
    }

    public void setEquipment_Rent(String equipment_Rent) {
        Equipment_Rent = equipment_Rent;
    }

    public String getEquipment_Image_Path() {
        return Equipment_Image_Path;
    }

    public void setEquipment_Image_Path(String equipment_Image_Path) {
        Equipment_Image_Path = equipment_Image_Path;
    }

    public String getEquipment_GPS() {
        return Equipment_GPS;
    }

    public void setEquipment_GPS(String equipment_GPS) {
        Equipment_GPS = equipment_GPS;
    }

    public String getEquipment_Status_Id() {
        return Equipment_Status_Id;
    }

    public void setEquipment_Status_Id(String equipment_Status_Id) {
        Equipment_Status_Id = equipment_Status_Id;
    }

    public String getEquipment_Address() {
        return Equipment_Address;
    }

    public void setEquipment_Address(String equipment_Address) {
        Equipment_Address = equipment_Address;
    }

    public String getOwner_User_Id() {
        return Owner_User_Id;
    }

    public void setOwner_User_Id(String owner_User_Id) {
        Owner_User_Id = owner_User_Id;
    }

    public String getUser_Name() {
        return User_Name;
    }

    public void setUser_Name(String user_Name) {
        User_Name = user_Name;
    }

    public String getOwner_Mobile_No() {
        return Owner_Mobile_No;
    }

    public void setOwner_Mobile_No(String owner_Mobile_No) {
        Owner_Mobile_No = owner_Mobile_No;
    }

    public String getBooking_date() {
        return booking_date;
    }

    public void setBooking_date(String booking_date) {
        this.booking_date = booking_date;
    }

    public List<String> getRow_data() {
        return row_data;
    }

    public void setRow_data(List<String> row_data) {
        this.row_data = row_data;
    }
}
