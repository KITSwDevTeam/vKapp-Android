package com.example.admin.vkclub;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by admin on 7/21/2017.
 */

class DataBaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";

    private static final String TABLE_NAME = "history_table";
    private static final String ID = "ID";
    private static final String EXTENSION = "extension";
    private static final String USERNAME = "username";
    private static final String DURATION = "duration";
    private static final String TIME = "time";
    private static final String STATUS = "status";

    private static final String NOTIFICATION_TABLE = "notification_table";
    private static final String MESSAGE = "message";
    private static final String TITLE = "title";

    public DataBaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + EXTENSION + " TEXT, " +
                USERNAME + " TEXT, " + DURATION + " TEXT, " + TIME + " TEXT, " + STATUS + " TEXT)";
        String createNotification = "CREATE TABLE " + NOTIFICATION_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + MESSAGE + " TEXT, " + TITLE + " TEXT, " + TIME + " TEXT)";
        db.execSQL(createTable);
        db.execSQL(createNotification);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        db.execSQL("DROP IF TABLE EXISTS " + NOTIFICATION_TABLE);
        onCreate(db);
    }

    public boolean addNotificationData(String message, String title, String time){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TITLE, title);
        contentValues.put(MESSAGE, message);
        contentValues.put(TIME, time);

        Log.d(TAG, "addData: Adding to NOTIFICATION_TABLE");
        long result = db.insert(NOTIFICATION_TABLE, null, contentValues);

        // if data are inserted incorrectly it will return -1
        if (result == -1){
            return false;
        }else {
            return true;
        }
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getNotificationID(String message){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + ID + " FROM " + NOTIFICATION_TABLE + " WHERE " + MESSAGE + " = '" + message + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public void deleteNotificationItem(int id, String message){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + NOTIFICATION_TABLE + " WHERE " + ID + "='" + id + "'" + " AND " + MESSAGE + " = '" + message + "'";
        Log.d(TAG, "deleteHistory: query " + query);
        Log.d(TAG, "deleteHistory: Deleting " + id + " " + message + " from database.");
        db.execSQL(query);
    }

    public boolean addData(String extension, String username, String duration, String time, String callstatus){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(EXTENSION, extension);
        contentValues.put(USERNAME, username);
        contentValues.put(DURATION, duration);
        contentValues.put(TIME, time);
        contentValues.put(STATUS, callstatus);

        Log.d(TAG, "addData: Adding to TABLE_NAME");
        long result = db.insert(TABLE_NAME, null, contentValues);

        // if data are inserted incorrectly it will return -1
        if (result == -1){
            return false;
        }else {
            return true;
        }
    }

    public Cursor getNotificationData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + NOTIFICATION_TABLE;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public void deleteAllNotification(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE * FROM " + NOTIFICATION_TABLE;
        Log.d(TAG, "deleteHistory: query " + query);
        Log.d(TAG, "deleteHistory: Deleting all from " + NOTIFICATION_TABLE);
        db.execSQL(query);
    }

    public Cursor getItemID(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + ID + " FROM " + TABLE_NAME + " WHERE " + EXTENSION + " = '" + name + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public void deleteSpecificItem(int id, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + ID + "='" + id + "'" + " AND " + EXTENSION + " = '" + name + "'";
        Log.d(TAG, "deleteHistory: query " + query);
        Log.d(TAG, "deleteHistory: Deleting " + id + " " + name + " from database.");
        db.execSQL(query);
    }

    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE * FROM " + TABLE_NAME;
        Log.d(TAG, "deleteHistory: query " + query);
        Log.d(TAG, "deleteHistory: Deleting all from " + TABLE_NAME);
        db.execSQL(query);
    }

}
