package nk.bluefrog.rythusevaoffice.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import nk.bluefrog.library.utils.SQLiteHelper;
import nk.bluefrog.rythusevaoffice.activities.ICM.ICMClass;
import nk.bluefrog.rythusevaoffice.activities.mycluster.FarmerProfileActivity;
import nk.bluefrog.rythusevaoffice.activities.search.SearchModel;
import nk.bluefrog.rythusevaoffice.activities.seeds.SeedClass;


/**
 * Created by nagendra on 21/9/17.
 */
/*
 * new Col Add in MyLand :col Name:reg_type
 * new Col Add in KouluLandT :col Name:reg_type
 * */

public class DBHelper extends SQLiteHelper {

    public static final String DB_NAME = "RythuSevaOffice.db";
    private static final String TAG = "DBHelper";
    private static final int DB_VERSION = 1;


    public DBHelper(Context context) {
        super(context, DB_NAME, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        super.onCreate(db);
        CreateTablesHere(db);
    }


    public void CreateTablesHere(SQLiteDatabase db) {
        // 1.Register
        db.execSQL(CreateTableStringByID(DBTables.MPOTable.TABLE_NAME,
                UID, DBTables.MPOTable.cols));
        db.execSQL(CreateTableStringByID(DBTables.Register.TABLE_NAME,
                UID, DBTables.Register.register));
        db.execSQL(CreateTableStringByID(DBTables.UserOrders.TABLE_NAME_,
                UID, DBTables.UserOrders.equipOders_cols));
        db.execSQL(CreateTableStringByID(DBTables.SeedsRegister.TABLE_NAME, UID,
                DBTables.SeedsRegister.cols));

        db.execSQL(CreateTableStringByID(DBTables.FarmerTable.TABLE_NAME, UID,
                DBTables.FarmerTable.cols));
        db.execSQL(CreateTableStringByID(DBTables.ICM.TABLE_NAME,
                UID, DBTables.ICM.cols));
        db.execSQL(CreateTableStringByID(DBTables.ICMREPLY.TABLE_NAME,
                UID, DBTables.ICMREPLY.cols));
        db.execSQL(CreateTableStringByID(DBTables.ScheduleMeetings.TABLE_NAME, UID,
                DBTables.ScheduleMeetings.cols));
        db.execSQL(CreateTableStringByID(DBTables.NOTIFICATIONS.TABLE_NAME,
                UID, DBTables.NOTIFICATIONS.cols));
        db.execSQL(CreateTableStringByID(DBTables.FarmerCommunication.TABLE_NAME,
                UID, DBTables.FarmerCommunication.cols));
        db.execSQL(CreateTableStringByID(DBTables.MeetingReview.TABLE_NAME,
                UID, DBTables.MeetingReview.cols));
        db.execSQL(CreateTableStringByID(DBTables.Category.TABLE_NAME,
                UID, DBTables.Category.category_columns));
        db.execSQL(CreateTableStringByID(DBTables.CategoryAndVarities.TABLE_NAME,
                UID, DBTables.CategoryAndVarities.cols));
        db.execSQL(CreateTableStringByID(DBTables.BulkBookingTracker.TABLE_NAME,
                UID, DBTables.BulkBookingTracker.cols));
        db.execSQL(CreateTableStringByID(DBTables.BulkBookStatus.TABLE_NAME,
                UID, DBTables.BulkBookStatus.cols));
        db.execSQL(CreateTableStringByID(DBTables.EquipBulkBookStatus.TABLE_NAME,
                UID, DBTables.EquipBulkBookStatus.cols));


        Log.d(TAG, "Tables created!");

    }

    public String getSplitMandalId(List<String> mandals) {

        String mandalIds = "";



        for (int i = 0; i < mandals.size(); i++) {
            mandalIds = mandalIds + "'" + mandals.get(i) + "'" + ",";
        }

        return mandalIds.substring(0, mandalIds.length() - 1);
    }


    public List<List<String>> getGpIds() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("Select distinct " + DBTables.FarmerTable.GP_ID + "," +
                DBTables.FarmerTable.GP_NAME  +"," + DBTables.FarmerTable.MANDAL_NAME  + " from " + DBTables.FarmerTable.TABLE_NAME + " order by " + DBTables.FarmerTable.GP_NAME, null);
        List<List<String>> list = new ArrayList<List<String>>();
        if (cur.getCount() > 0)
            list = cursorToListArr(cur);
        cur.close();
        db.close();
        return list;

    }

    public List<List<String>> getGpBasedonIdSelection(String selectedIds) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select distinct " + DBTables.FarmerTable.GP_ID + ","+   DBTables.FarmerTable.GP_NAME +"," + DBTables.FarmerTable.MANDAL_NAME + " from  " + DBTables.FarmerTable.TABLE_NAME + " where " + DBTables.FarmerTable.MANDAL_ID + " IN " + "( "  + selectedIds + " )" + " order by " + DBTables.FarmerTable.GP_NAME ;
        Cursor cur = db.rawQuery(query, null);
        List<List<String>> list = new ArrayList<List<String>>();
        if (cur.getCount() > 0)
            list = cursorToListArr(cur);
        cur.close();
        db.close();
        return list;

    }

    public List<List<String>> getMandalIds() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query="Select distinct " + DBTables.FarmerTable.MANDAL_ID + "," +
                DBTables.FarmerTable.MANDAL_NAME + " from " + DBTables.FarmerTable.TABLE_NAME + " order by " + DBTables.FarmerTable.MANDAL_NAME;
        Cursor cur = db.rawQuery(query, null);
        List<List<String>> list = new ArrayList<List<String>>();
        if (cur.getCount() > 0)
            list = cursorToListArr(cur);

        cur.close();
        db.close();
        return list;

    }

    public List<List<String>> getMandalIdsOnSelection(String selectedIds) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select distinct " + DBTables.FarmerTable.MANDAL_ID + ","+   DBTables.FarmerTable.MANDAL_NAME+ " from  " + DBTables.FarmerTable.TABLE_NAME + " where " + DBTables.FarmerTable.MANDAL_ID + " IN " + "( " + selectedIds + " )" + " order by " + DBTables.FarmerTable.MANDAL_NAME ;
        Cursor cur = db.rawQuery(query,null);
        List<List<String>> list = new ArrayList<List<String>>();
        if (cur.getCount() > 0)
            list = cursorToListArr(cur);
        cur.close();
        db.close();
        return list;

    }



    public int getCountByValues(String tableName, String colName[],
                                String colValue[]) {

        SQLiteDatabase db = this.getReadableDatabase();
        int cnt = 0;
        String countQuery = "SELECT  COUNT(*) FROM " + tableName + " WHERE ";
        for (int k = 0; k < colName.length; k++)
            countQuery = countQuery + colName[k] + "='" + colValue[k]
                    + "' AND ";
        countQuery = countQuery.substring(0, countQuery.length() - 5);
        // System.out.println("countQuery:" + countQuery);
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        cnt = Integer.parseInt(cursor.getString(0));
        cursor.close();
        db.close();
        return cnt;
    }

    public ArrayList<SeedClass> getSeedData(String value) {
        ArrayList<SeedClass> al = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c;
        if (value.trim().equals("0")) {
            c = db.query(DBTables.SeedsRegister.TABLE_NAME, DBTables.SeedsRegister.colsWithID, null, null, null, null, null, null);
        } else {
            c = db.query(DBTables.SeedsRegister.TABLE_NAME, DBTables.SeedsRegister.colsWithID, DBTables.SeedsRegister.typeofSeed + "=" + value, null, null, null, null, null);
        }
        //DBHelper.UID, dist, mandal, gp, village, typeofSeed, seedSubType,  quantity, cost, address, gps, image, sendFlag
        if (c != null) {
            while (c.moveToNext()) {
                SeedClass sc = new SeedClass();
                sc.setUID(c.getString(0));
                sc.setDist(c.getString(1));
                sc.setMandal(c.getString(2));
                sc.setGp(c.getString(3));
                sc.setVillage(c.getString(4));
                sc.setTypeofSeed(c.getString(5));
                sc.setSeedSubType(c.getString(6));
                sc.setQuantity(c.getString(7));
                sc.setCost(c.getString(8));
                sc.setAddress(c.getString(9));
                sc.setGps(c.getString(10));
                sc.setImage(c.getString(11));
                sc.setSendFlag(c.getString(12));
                al.add(sc);
            }
        }
        c.close();
        db.close();
        return al;
    }


    public List<SearchModel> getSearchResults(Context context) {
        List<SearchModel> al = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();


        Cursor c;

        c = db.query(DBTables.FarmerTable.TABLE_NAME, DBTables.FarmerTable.cols, null, null, null, null, null);
        if (c != null) {
            while (c.moveToNext()) {
                SearchModel model = new SearchModel();
                model.setGpId(c.getString(0));
                model.setMobile(c.getString(1));
                model.setName(c.getString(2));
                model.setAdharID(c.getString(3));
                model.setImgUrl(c.getString(4));
                model.setDate(c.getString(5));
                Intent intent = new Intent(context, FarmerProfileActivity.class);
                intent.putExtra("aadhaar", c.getString(3));
                model.setIntent(intent);
                al.add(model);
            }
        }

        c.close();
        db.close();

        return al;


    }

    public ArrayList<ICMClass> getReplyMessagesData(String notificationID) {
        ArrayList<ICMClass> al = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c;

        c = db.query(DBTables.ICMREPLY.TABLE_NAME, DBTables.ICMREPLY.cols, DBTables.ICMREPLY.NOTIFICATIONID + "=" + notificationID, null, null, null, DBTables.ICMREPLY.TIMESTAMP + " desc");

        if (c != null) {
            while (c.moveToNext()) {
                ICMClass notificationClass = new ICMClass();

                notificationClass.setNotificationID(c.getString(0));
                notificationClass.setReplyMessage(c.getString(1));
                notificationClass.setLastUpdated(c.getString(2));

                al.add(notificationClass);
            }
        }
        c.close();
        db.close();
        return al;
    }

}
