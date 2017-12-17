package com.example.evpru.assignment1_graph;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.evpru.assignment1_graph.AccelerometerDBContract.AccelerometerEntry;

/**
 * Created by evpru on 2/26/2017.
 */

public class AccelerometerReaderDbHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "Group11.db";
    public static final int DATABASE_VERSION = 1;

    public static AccelerometerReaderDbHelper accReaderDbHelper;

    public AccelerometerReaderDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Log.d("MyAPP2", "onCreate");
    }

    public static AccelerometerReaderDbHelper getInstance(Context context)
    {
        if ( null == accReaderDbHelper)
        {
            accReaderDbHelper = new AccelerometerReaderDbHelper(context);
        }
        return accReaderDbHelper;
    }

    public void createDatabase()
    {
        SQLiteDatabase sqlitedb;
        try {
            sqlitedb = SQLiteDatabase.openOrCreateDatabase(MainActivity.DB_PATH, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createTable(String tableName)
    {
        SQLiteDatabase db = getWritableDatabase();
        try {
            String SQL_CREATE_ENTRIES =
                    "CREATE TABLE " + tableName + " (" +
                            AccelerometerEntry.COLUMN_NAME_TIMESTAMP + " TEXT," +
                            AccelerometerEntry.COLUMN_NAME_XVALUES + " REAL," +
                            AccelerometerEntry.COLUMN_NAME_YVALUES + " REAL," +
                            AccelerometerEntry.COLUMN_NAME_ZVALUES + " REAL)";
            db.execSQL(SQL_CREATE_ENTRIES);
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }

    public void addEntry(String table, String timeStamp, float x, float y, float z){
        SQLiteDatabase sqlitedb = getWritableDatabase();
        try{

            ContentValues values = new ContentValues();
            values.put(AccelerometerDBContract.AccelerometerEntry.COLUMN_NAME_TIMESTAMP, timeStamp);
            values.put(AccelerometerDBContract.AccelerometerEntry.COLUMN_NAME_XVALUES, x);
            values.put(AccelerometerDBContract.AccelerometerEntry.COLUMN_NAME_YVALUES, y);
            values.put(AccelerometerDBContract.AccelerometerEntry.COLUMN_NAME_ZVALUES, z);
            long newRowId = sqlitedb.insert(table, null, values);
            Log.d("MyAPP1", "x = " + x + ", y = " + y + ", z = " + z + ". Inserted to Row: " + newRowId);
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }

    public Cursor getLast10data(String tableName)
    {
        SQLiteDatabase sqlitedb = getReadableDatabase();
        Cursor cur = null;
        try{
             cur = sqlitedb.rawQuery("SELECT * FROM " + tableName + " ORDER BY " + AccelerometerDBContract.AccelerometerEntry.COLUMN_NAME_TIMESTAMP +" DESC limit 10",null);
        }catch (SQLiteException e){
            e.printStackTrace();
        }
        return cur;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
