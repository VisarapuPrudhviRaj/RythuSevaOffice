package nk.bluefrog.rythusevaoffice.utils;


/**
 * Created by nagendra on 23/12/16.
 */

public final class DBTables {


    public static class Register {

        public static final String TABLE_NAME = "Register";


        public static final String user_Id = "user_Id";
        public static final String district_Name = "district_Name";
        public static final String district_Code = "district_Code";
        public static final String mandal_Name = "mandal_Name";
        public static final String mandal_Code = "mandal_Code";//5

        public static final String user_type = "user_type";
        public static final String user_name = "user_name";//7
        public static final String user_phone = "user_phone";//8
        public static final String user_aadharid = "user_aadharid";//9
        public static final String user_password = "user_password";
        public static final String tokenID = "tokenID";

        public static final String gp_Name = "gp_Name";
        public static final String gp_Code = "gp_Code";

        public static final String village_Name = "village_Name";
        public static final String village_Code = "village_Code";
        public static final String user_farmerfathername = "user_farmerfathername";
        public static final String gender_Code = "gender_Code";//17 image
        public static final String caste_Code = "caste_Code";//gender
        public static final String farmercategory_Code = "farmercategory_Code";//Age


        public static final String[] register = new String[]{user_Id, district_Name,
                district_Code, mandal_Name, mandal_Code, user_type, user_name,
                user_phone, user_aadharid, user_password, tokenID, gp_Name, gp_Code, village_Name,
                village_Code, user_farmerfathername, gender_Code, caste_Code, farmercategory_Code};


    }

    public static class Category {
        public static final String TABLE_NAME = "Category";
        public static final String EQUIPMENT_ID = "EQUIPMENT_ID";
        public static final String EQUIPMENT_NAME = "EQUIPMENT_Name";
        public static final String CID = "CID";
        public static final String CNAME = "CName";

        public static final String[] category_columns = new String[]{CID, CNAME, EQUIPMENT_ID, EQUIPMENT_NAME};
    }

    public static class UserOrders {
        //9 url
        //[{"Equipment_Id":"1","Barcode":"123456789632","Serial_No":"1234564566","Equipment_Rent":"500",
        //"Equipment_Image_Path":"http://125.62.194.228/mEquipment_Live_Images/0305000001/0305000001_170303175930791180.JPG",
        //        "Equipment_GPS":"17.7118-83.99799999999999","Address":"hyb","User_Name":"kumar",
        //       "User_Id":"0305000001","Owner_Mobile_No":"7207286365","Equipment_Status_Id":"1",
        //       "Distance_in_Km":"0"},

        // {"BookEquipmentDetails":[{"Owner_User_Id":"01020001","Order_Status_Id":"1",
        // "Equipment_Id":"1","Barcode":"42342",
        // "Customer_Name":"sdfsdf","Mobile_No":"2342","Address":"sdfdfsd",
        // "Customer_GPS":"gps","Start_Date":"01-03-2017","End_Date":"01-03-2017","Purpose":"1"}

        //{[{"Booking_Id":"eq1",
        // "Customer_Name":"23423423","Equipment_Id":"24234",
        // "Barcode":"231","Mobile_No":"9676786786","Address":"dgdfgdf",
        // "Start_Date":"2017-01-02", End_Date:"2017-02-20",Purpose:"werwe",
        // "Order_Status":"sdfsd"},
//Extra Col
        //

        public static final String TABLE_NAME_ = "UserOrders";

        //Equipment Details with Owner

        public static final String Equipment_Id = "Equipment_Id";//1
        public static final String Barcode = "Barcode";//2
        public static final String Serial_No = "Serial_No";
        public static final String Equipment_Rent = "Equipment_Rent";
        public static final String Equipment_Image_Path = "Equipment_Image_Path";//5
        public static final String Equipment_GPS = "Equipment_GPS";//6
        public static final String Equipment_Address = "Equipment_Address";
        public static final String Owner_Name = "Owner_Name";
        public static final String Owner_Id = "Owner_Id";
        public static final String Owner_Mobile_No = "Owner_Mobile_No";//10
        public static final String Equipment_Status_Id = "Equipment_Status_Id";
        //
        //Booking Details

        public static final String Booking_Id = "Booking_Id";//12
        public static final String Customer_Name = "Customer_Name";
        public static final String Customer_Mobile_No = "Customer_Mobile_No";
        public static final String Customer_Address = "Customer_Address";//15
        public static final String Customer_GPS = "Customer_GPS";
        public static final String Start_Date = "Start_Date";
        public static final String End_Date = "End_Date";//18
        public static final String Purpose = "Purpose";

        public static final String Order_Status_ID = "Order_Status";//20
        public static final String Customer_ID = "Customer_ID";
        public static final String Equipment_Rent_Arc = "Equipment_Rent_Arc";
        public static final String booking_date = "booking_date";
        public static final String commodity = "commodity";
        public static final String shopName = "shopName";
        public static final String userType = "userType";


        public static final String[] equipOders_cols = new String[]{Equipment_Id, Barcode, Serial_No,
                Equipment_Rent, Equipment_Image_Path, Equipment_GPS,
                Equipment_Address, Owner_Name, Owner_Id, Owner_Mobile_No, Equipment_Status_Id,
                Booking_Id, Customer_Name, Customer_Mobile_No, Customer_Address, Customer_GPS,
                Start_Date, End_Date, Purpose, Order_Status_ID, Customer_ID, Equipment_Rent_Arc,
                booking_date, commodity, shopName, userType};


    }


    public static class SeedsRegister {

        public static final String TABLE_NAME = "Seed_Register";

        public static final String dist = "dist";
        public static final String mandal = "mandal";
        public static final String gp = "gp";
        public static final String village = "village";
        public static final String typeofSeed = "typeofSeed";
        public static final String seedSubType = "seedSubType";

        public static final String quantity = "quantity";
        public static final String cost = "cost";
        public static final String address = "address";
        public static final String gps = "gps";
        public static final String image = "image";


        public static final String sendFlag = "sendFlag";

        public static final String[] cols = new String[]{dist, mandal, gp, village, typeofSeed, seedSubType,
                quantity, cost, address, gps, image, sendFlag};
        public static final String[] colsWithID = new String[]{DBHelper.UID, dist, mandal, gp, village, typeofSeed, seedSubType,
                quantity, cost, address, gps, image, sendFlag};


    }

    public static class MPOTable {

        public static final String TABLE_NAME = "mpo";

        public static final String NAME = "name";
        public static final String MOBILE = "mobile";
        public static final String DID = "dist_id";
        public static final String DNAME = "dist_name";
        public static final String MID = "mandal_id";
        public static final String MNAME = "mandal_name";
        public static final String DATE = "login_date";
        public static final String GP_ID = "gp_id";
        public static final String GP_NAME = "gp_name";
        public static final String OFFICER_TYPE = "officer_type";
        public static final String PASSWORD = "Password";
        public static final String UNIQUE_ID = "Unique_Id";


        public static final String[] cols = new String[]{NAME, MOBILE, DID, DNAME, MID, MNAME, DATE, GP_ID, GP_NAME, OFFICER_TYPE, PASSWORD, UNIQUE_ID};

        //Extra Village ID, Name(Key Names as to change)


    }


    public static class FarmerTable {

        public static final String TABLE_NAME = "farmer";

        public static final String GP_ID = "gp_id";
        public static final String MOBILE = "mobile";
        public static final String NAME = "name";
        public static final String AADHAR = "aadhar";
        public static final String DATE = "created_date";
        public static final String IMAGE_URL = "image_url";
        public static final String GP_NAME = "gp_name";
        public static final String DIST_ID = "dist_id";
        public static final String DIST_NAME = "dist_name";
        public static final String MANDAL_ID = "mandal_id";
        public static final String MANDAL_NAME = "mandal_name";
        public static final String[] cols = new String[]{GP_ID, MOBILE, NAME, AADHAR, DATE, IMAGE_URL, GP_NAME
                , DIST_ID, DIST_NAME, MANDAL_ID, MANDAL_NAME};


    }

    public static class ICM {
        public static final String TABLE_NAME = "TABLE_ICM";

        public static final String ID = "ID";
        public static final String TITLE = "TITLE";
        public static final String MESSAGE = "MESSAGE";
        public static final String LINK = "LINK"; //NA or link
        public static final String IMAGEPATH = "IMAGEPATH"; //NA or image
        public static final String TIMESTAMP = "TIMESTAMP";
        /*
         * 1 - READ
         * 0 - UNREAD
         * */
        public static final String IS_READ = "IS_READ";
        public static final String TYPE = "TYPE";//N=Gen I,U=Equip CW,CS,CP=Crop
        public static final String VIDEOPATH = "VIDEOPATH"; //NA or video
        public static final String IS_IMG_VID = "IMG_VID";//NA,I=Image,V=Video
        public static final String DEPT_NAME = "DEPT_NAME";
        public static final String REPLY = "REPLY";//Y or N
        public static final String REPLY_URL = "REPLY_URL";


        public static final String[] cols = new String[]{ID, TITLE, MESSAGE, LINK, IMAGEPATH, TIMESTAMP, IS_READ, TYPE, VIDEOPATH, IS_IMG_VID, DEPT_NAME, REPLY, REPLY_URL};
    }

    public static class ICMREPLY {
        public static final String TABLE_NAME = "TABLE_ICMREPLY";

        public static final String NOTIFICATIONID = "NOTIFICATIONID";
        public static final String REPLYMESSAGE = "REPLYMESSAGE";
        public static final String TIMESTAMP = "TIMESTAMP";


        public static final String[] cols = new String[]{NOTIFICATIONID, REPLYMESSAGE, TIMESTAMP};
    }

    public static class ScheduleMeetings {

        public static final String TABLE_NAME = "schedule_meetings";

        public static final String MEETING_DATE = "date_meeting";
        public static final String GP_ID = "gp_ids";
        public static final String GP_NAME = "gp_names";
        public static final String REMARKS = "remarks";

        public static final String DATE = "created_date";

        public static final String[] cols = new String[]{MEETING_DATE, GP_ID, GP_NAME, REMARKS, DATE};


    }

    public static class NOTIFICATIONS {
        public static final String TABLE_NAME = "TABLE_NOTIFICATIONS";

        public static final String ID = "ID";
        public static final String TITLE = "TITLE";
        public static final String MESSAGE = "MESSAGE";
        public static final String LINK = "LINK"; //NA or link
        public static final String IMAGEPATH = "IMAGEPATH"; //NA or image
        public static final String TIMESTAMP = "TIMESTAMP";
        /*
         * 1 - READ
         * 0 - UNREAD
         * */
        public static final String IS_READ = "IS_READ";
        public static final String TYPE = "TYPE";//N=Gen I,U=Equip CW,CS,CP=Crop
        public static final String VIDEOPATH = "VIDEOPATH"; //NA or video
        public static final String IS_IMG_VID = "IMG_VID";//NA,I=Image,V=Video
        public static final String DEPT_NAME = "DEPT_NAME";
        public static final String REPLY = "REPLY";//Y or N
        public static final String REPLY_URL = "REPLY_URL";


        public static final String[] cols = new String[]{ID, TITLE, MESSAGE, LINK, IMAGEPATH, TIMESTAMP, IS_READ, TYPE, VIDEOPATH, IS_IMG_VID, DEPT_NAME, REPLY, REPLY_URL};
    }

    public static class FarmerCommunication {

        public static final String TABLE_NAME = "farmer_comm";

        public static final String GP_ID = "gp_id";
        public static final String GP_NAME = "gp_name";
        public static final String CROP_TYPE = "crop_type";
        public static final String TITLE = "title";
        public static final String DESCRIPTION = "description";
        public static final String UPLOAD_TYPE = "upload_type";
        public static final String IMAGE_PATH = "image_path";
        public static final String LINK = "link";
        public static final String VIDEO_LINK = "video_link";
        public static final String DATE = "created_date";
        public static final String DIST_ID = "dist_id";
        public static final String DIST_NAME = "dist_name";
        public static final String MANDAL_ID = "mandal_id";
        public static final String MANDAL_NAME = "mandal_name";


        public static final String[] cols = new String[]{GP_ID, GP_NAME, CROP_TYPE, TITLE,
                DESCRIPTION, UPLOAD_TYPE, IMAGE_PATH, LINK, VIDEO_LINK, DATE, DIST_ID
                , DIST_NAME, MANDAL_ID, MANDAL_NAME};


    }

    public static class MeetingReview {

        public static final String TABLE_NAME = "meeting_review";

        public static final String MEETING_DATE = "date_meeting";
        public static final String GP_ID = "gp_id";
        public static final String FARMER_COUNT = "farmer_count";
        //public static final String GP_NAME = "gp_name";
        public static final String DESCRIPTION = "description";
        public static final String REMARKS = "remarks";
        public static final String IMAGE_PATH = "image_path";
        public static final String DATE = "created_date";

        public static final String[] cols = new String[]{MEETING_DATE, GP_ID, FARMER_COUNT, DESCRIPTION, REMARKS, IMAGE_PATH, DATE};

    }

    public static class CategoryAndVarities {
        public static final String TABLE_NAME = "CategoryAndVarities";
        public static final String catID = "catID";
        public static final String catName = "catName";
        public static final String subCatID = "subCatID";
        public static final String subCatName = "subCatName";
        public static final String categoryType = "categoryType";
        public static final String[] cols = new String[]{catID, catName, subCatID, subCatName, categoryType};
    }

    /*{booking_id,commodity,category_id,subcategory_id,category_name,quantity,delivery_address,lat,lng,
            shop_id,shop_name,shop_owner_name,shop_phnumber,shop_image,shop_address,shop_gps,
            shop_discount,shop_mini_order_quantity,shop_category_quantity,shop_categroy_price}*/

    public static class BulkBookingTracker {
        public static final String TABLE_NAME = "BulkBookingTracker";
        public static final String booking_id = "booking_id";
        public static final String commodity = "commodity";
        public static final String category_id = "category_id";
        public static final String subcategory_id = "subcategory_id";
        public static final String category_name = "category_name";
        public static final String quantity = "quantity";
        public static final String delivery_address = "delivery_address";
        public static final String lat = "lat";
        public static final String lng = "lng";
        public static final String shop_id = "shop_id";//10
        public static final String shop_name = "shop_name";
        public static final String shop_owner_name = "shop_owner_name";
        public static final String shop_phnumber = "shop_phnumber";
        public static final String shop_image = "shop_image";
        public static final String shop_address = "shop_address";
        public static final String shop_gps = "shop_gps";
        public static final String shop_discount = "shop_discount";
        public static final String shop_mini_order_quantity = "shop_mini_order_quantity";
        public static final String shop_category_quantity = "shop_category_quantity";
        public static final String shop_categroy_price = "shop_categroy_price";
        public static final String MPEOName = "MPEOName";
        public static final String MPEOPhNo = "MPEOPhNo";
        public static final String DID = "DID";
        public static final String booking_date = "booking_date";


        public static final String[] cols = new String[]{booking_id, commodity, category_id, subcategory_id,
                category_name, quantity, delivery_address, lat, lng, shop_id, shop_name, shop_owner_name, shop_phnumber,
                shop_image, shop_address, shop_gps, shop_discount, shop_mini_order_quantity, shop_category_quantity, shop_categroy_price,
                MPEOName, MPEOPhNo, DID, booking_date};
    }

    public static class BulkBookStatus {
        public static final String TABLE_NAME = "BulkBookStatus";
        public static final String AutoID = "AutoID";
        public static final String BookingID = "BookingID";
        public static final String status_date = "status_date";
        public static final String status_index = "status_index";
        public static final String status_message = "status_message";
        public static final String[] cols = new String[]{AutoID, BookingID, status_date, status_index
                , status_message};
    }

    public static class EquipBulkBookStatus {
        public static final String TABLE_NAME = "EquipBulkBookStatus";
        public static final String Status = "Status";
        public static final String Dispatch_GPS = "Dispatch_GPS";
        public static final String Remarks = "Remarks";
        public static final String Trans_Date = "Trans_Date";
        public static final String No_of_Hours = "No_of_Hours";
        public static final String Booking_Id = "Booking_Id";
        public static final String[] cols = new String[]{Status, Dispatch_GPS, Remarks, Trans_Date, No_of_Hours, Booking_Id};
    }


}
