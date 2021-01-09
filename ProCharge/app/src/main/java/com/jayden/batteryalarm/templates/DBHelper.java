package com.jayden.batteryalarm.templates;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.jayden.batteryalarm.model.AlarmData;
import com.jayden.batteryalarm.model.IntruderData;
import com.jayden.batteryalarm.util.SharedPreferencesApplication;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private Context context;

    private String TABLE_NAME = "ALARMDATA";
    private String KEY_ID = "ID";
    private String UNIQUE_ID = "UNIQUE_ID" ;
    private String ALARM_TYPE = "ALARM_TYPE";
    private String CURRENT_DATE_TIME = "CURRENT_DATE_TIME";
    private String TARGET_PERCENTAGE = "TARGET_PERCENTAGE";
    private String CURRENT_PERCENTAGE = "CURRENT_PERCENTAGE";
    private String SELECTED_HOURS = "SELECTED_HOURS";
    private String SELECTED_MINUTE = "SELECTED_MINUTE";
    private String UNPLUGG_DATE_TIME = "UNPLUGG_DATE_TIME";
    private String UNPLUGG_PERCENTAGE = "UNPLUGG_PERCENTAGE";
    private String ALARM_STATUS = "ALARM_STATUS";

    // Contacts table name
    public static final String TABLE_INTRUDER = "intruderdata";
    // Contacts Table Columns names
    public static final String INTRUDER_ID = "_id";
    public static final String INTRUDER_DATE = "intruder_date";
    public static final String INTRUDER_TIME = "intruder_time";
    public static final String INTRUDER_IMAGE_PATH = "intruder_image_path";

    private Constant constant = Constant.getInstance();

    public DBHelper(@Nullable Context context) {
        super(context, "batteryalarm", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ALARM_TABLE = "CREATE TABLE "
                + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + UNIQUE_ID + " INTEGER DEFAULT 0 ,"
                + CURRENT_DATE_TIME + " INTEGER ,"
                + TARGET_PERCENTAGE + " INTEGER ,"
                + CURRENT_PERCENTAGE + " INTEGER ,"
                + SELECTED_HOURS + " INTEGER DEFAULT 0 ,"
                + SELECTED_MINUTE + " INTEGER DEFAULT 0 ,"
                + UNPLUGG_DATE_TIME + " INTEGER DEFAULT 0 , "
                + UNPLUGG_PERCENTAGE + " INTEGER DEFAULT 0 ,"
                + ALARM_STATUS + " TEXT  ,"
                + ALARM_TYPE + " TEXT "
                +")";

        String CREATE_INTRUDER_TABLE = "CREATE TABLE "
                + TABLE_INTRUDER + "("
                + INTRUDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + INTRUDER_DATE + " TEXT ,"
                + INTRUDER_TIME + " TEXT ,"
                + INTRUDER_IMAGE_PATH + " TEXT "
                +")";

        db.execSQL(CREATE_ALARM_TABLE);
        db.execSQL(CREATE_INTRUDER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void deleteAllData(){
        SQLiteDatabase db= this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
        db.close();
    }

    public void addAlarm(AlarmData alarmData) {
        Log.e("DBHelper" , "addAlarm : " + alarmData.toString());
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues cv= new ContentValues();

        cv.put(UNIQUE_ID,alarmData.getUnique_id());
        cv.put(CURRENT_DATE_TIME,alarmData.getCurrent_date_time());
        cv.put(TARGET_PERCENTAGE,alarmData.getTarget_percentage());
        cv.put(CURRENT_PERCENTAGE,alarmData.getCurrent_percentage());
        cv.put(SELECTED_HOURS,alarmData.getSelected_hour());
        cv.put(SELECTED_MINUTE,alarmData.getSelected_minute());
        cv.put(ALARM_STATUS ,  alarmData.getAlarm_states());
        cv.put(ALARM_TYPE ,  alarmData.getAlarm_type());

        long rowInsrted =  db.insert(TABLE_NAME,null, cv);
        Constant.lastID = alarmData.getUnique_id() ;
        new SharedPreferencesApplication().setLastSetAlarmID(context ,alarmData.getUnique_id() );

        Log.e("DBHelper","ID IN DATABASE ::" +  constant.lastID);

        db.close();
    }

    public void updateAlarmStatus(String val, int alarmID) {
        Log.e("DBHelper","updateAlarmStatus " + alarmID );
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ALARM_STATUS , val);

        // updating row
        db.update(TABLE_NAME, cv, UNIQUE_ID + " = ?",
                new String[]{String.valueOf(alarmID)});
    }

    public void updateLastUnpluggData(int alarmID , AlarmData alarmData , boolean status_add) {
        Log.e("DBHelper","updateLastUnpluggData " + alarmID + " DATA" + alarmData.getUnplagg_date_time()  +"  kxjf  " + status_add +  " fhsfk" + alarmData.getAlarm_states()  );
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        if (status_add) {
            cv.put(ALARM_STATUS, alarmData.getAlarm_states());
        }
        cv.put(UNPLUGG_DATE_TIME  , alarmData.getUnplagg_date_time());
        cv.put(UNPLUGG_PERCENTAGE , alarmData.getUnplugg_percentage());

        // updating row
        db.update(TABLE_NAME, cv, UNIQUE_ID + " = ?",
                new String[]{String.valueOf(alarmID)});
    }

    public AlarmData getPerticularAlarmData(String id) {
        List<AlarmData> alarmDataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        AlarmData alarmData = new AlarmData();
        Cursor cursor  = db.rawQuery(" SELECT * FROM  "+ TABLE_NAME +"  WHERE  " + UNIQUE_ID + " = ? ", new String[]{id});
        try {
            if (cursor.moveToFirst()) {
                do {
                    alarmData.setUnique_id(cursor.getInt(cursor.getColumnIndex(UNIQUE_ID)));
                    alarmData.setCurrent_date_time(cursor.getString(cursor.getColumnIndex(CURRENT_DATE_TIME)));
                    alarmData.setCurrent_percentage(cursor.getInt(cursor.getColumnIndex(CURRENT_PERCENTAGE)));
                    alarmData.setTarget_percentage(cursor.getInt(cursor.getColumnIndex(TARGET_PERCENTAGE)));
                    alarmData.setSelected_hour(cursor.getInt(cursor.getColumnIndex(SELECTED_HOURS)));
                    alarmData.setSelected_minute(cursor.getInt(cursor.getColumnIndex(SELECTED_MINUTE)));
                    alarmData.setAlarm_states(cursor.getString(cursor.getColumnIndex(ALARM_STATUS)));
                    alarmData.setAlarm_type(cursor.getString(cursor.getColumnIndex(ALARM_TYPE)));
                } while (cursor.moveToNext());
            }
        }
        finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return alarmData;
    }

    public String getPerticularAlarmType(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String alarm_type = null;
        Cursor cursor  = db.rawQuery(" SELECT * FROM  "+ TABLE_NAME +"  WHERE " + UNIQUE_ID + " = ? ", new String[]{id});
        try {
            if (cursor.moveToFirst()) {
                do {
                    alarm_type = cursor.getString(cursor.getColumnIndex(ALARM_TYPE));
                } while (cursor.moveToNext());
            }
        }
        finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return alarm_type;
    }

    public List<AlarmData> getAllAlarmData() {
        List<AlarmData> alarmDataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor  = db.rawQuery(" SELECT * FROM  "+ TABLE_NAME , null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    AlarmData alarmData = new AlarmData();
                    alarmData.setUnique_id(cursor.getInt(cursor.getColumnIndex(UNIQUE_ID)));
                    alarmData.setCurrent_date_time(cursor.getString(cursor.getColumnIndex(CURRENT_DATE_TIME)));
                    alarmData.setCurrent_percentage(cursor.getInt(cursor.getColumnIndex(CURRENT_PERCENTAGE)));
                    alarmData.setTarget_percentage(cursor.getInt(cursor.getColumnIndex(TARGET_PERCENTAGE)));
                    alarmData.setSelected_hour(cursor.getInt(cursor.getColumnIndex(SELECTED_HOURS)));
                    alarmData.setSelected_minute(cursor.getInt(cursor.getColumnIndex(SELECTED_MINUTE)));
                    alarmData.setAlarm_states(cursor.getString(cursor.getColumnIndex(ALARM_STATUS)));
                    alarmData.setAlarm_type(cursor.getString(cursor.getColumnIndex(ALARM_TYPE)));
                    alarmData.setUnplugg_percentage(cursor.getInt(cursor.getColumnIndex(UNPLUGG_PERCENTAGE)));
                    alarmData.setUnplagg_date_time(cursor.getString(cursor.getColumnIndex(UNPLUGG_DATE_TIME)));
                    alarmDataList.add(alarmData);
                    Log.e("All : " , alarmData.toString() );
                } while (cursor.moveToNext());
            }
        }
        finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
        return alarmDataList;
    }

    // Adding new Intruder Data
    public void addIntruderData(IntruderData intruder) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(INTRUDER_DATE, intruder.getIntruder_date());
        values.put(INTRUDER_TIME, intruder.getIntruder_time());
        values.put(INTRUDER_IMAGE_PATH, intruder.getIntruder_path());
        // Inserting Row
        db.insert(TABLE_INTRUDER, null, values);
    }

    //All Intruder Data Delate
    public void deleteAllIntriderData(){
        SQLiteDatabase db= this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_INTRUDER);
        db.close();
    }

    // Getting All Intruder Data
    public List<IntruderData> getAllInruderdata() {
        List<IntruderData> intruderDataList = new ArrayList<IntruderData>();
        Cursor cursor;
        String selectQuery = "SELECT  * FROM " + TABLE_INTRUDER + " ORDER BY "+ INTRUDER_ID +" DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        cursor = db.rawQuery(selectQuery, null);
        try{
            if (cursor.moveToFirst()) {
                do {
                    IntruderData intruderData = new IntruderData();
                    intruderData.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(INTRUDER_ID))));
                    intruderData.setIntruder_date(cursor.getString(cursor.getColumnIndex(INTRUDER_DATE)));
                    intruderData.setIntruder_time(cursor.getString(cursor.getColumnIndex(INTRUDER_TIME)));
                    intruderData.setIntruder_path(cursor.getString(cursor.getColumnIndex(INTRUDER_IMAGE_PATH)));
                    // Adding contact to list
                    intruderDataList.add(intruderData);
                } while (cursor.moveToNext());
            }
            // return contact list
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        // looping through all rows and adding to list
        return intruderDataList;
    }

    public void deleteIntruder(String filePath) {
        Log.e("deleteIntruder ", filePath );

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_INTRUDER, INTRUDER_IMAGE_PATH + " = ?",
                new String[]{filePath});
        db.close();
    }

    public void closeDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.close();
    }
}