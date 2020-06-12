package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbOpenHelper {

    private static final String DATABASE_NAME = "InnerDatabase(SQLite).db";
    private static final int DATABASE_VERSION = 8;
    public static SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private Context mCtx;

    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(DataBases.CreateDB._CREATE0);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            db.execSQL("DROP TABLE IF EXISTS "+DataBases.CreateDB._TABLENAME0);
            onCreate(db);
        }
    }

    public DbOpenHelper(Context context){
        this.mCtx = context;
    }

    public DbOpenHelper open() throws SQLException {
        Log.i("DBtest","open Db");
        mDBHelper = new DatabaseHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }

    public void create(){
        mDBHelper.onCreate(mDB);
    }

    public void close(){
        mDB.close();
    }

    //talbe에 데이터 삽
    public long insertColumn(String alramId, String hours, String minute,String ampm,String nextDayOfWeek,String place, String penalty, String penaltyLine,String OnOff){
        Log.i("DBtest","open inserColumn ");
        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB.ALARMID, alramId);
        values.put(DataBases.CreateDB.HOURS, hours);
        values.put(DataBases.CreateDB.MINUTE, minute);
        values.put(DataBases.CreateDB.NEXTDAYOFWEEK, nextDayOfWeek);
        values.put(DataBases.CreateDB.ONOFF, OnOff);
        values.put(DataBases.CreateDB.DAYOFAMPM, ampm);
        values.put(DataBases.CreateDB.PLACE, place);
        values.put(DataBases.CreateDB.PENALTY, penalty);
        values.put(DataBases.CreateDB.PENALTY_LINE, penaltyLine);
        //values.put(DataBases.CreateDB._ID, id);
        Log.i("DBID","ID : " + values.get(DataBases.CreateDB._ID));
        return mDB.insert(DataBases.CreateDB._TABLENAME0, null, values);
    }

    public Cursor selectColumns(){
        return mDB.query(DataBases.CreateDB._TABLENAME0, null, null, null, null, null, null);
    }

    public boolean updateColumn(long id, String alramId, String hours, String minute,String ampm, String nextDayOfWeek,String place, String penalty, String penaltyLine,String OnOff){
        ContentValues values = new ContentValues();
        values.put(DataBases.CreateDB.ALARMID, alramId);
        values.put(DataBases.CreateDB.HOURS, hours);
        values.put(DataBases.CreateDB.MINUTE, minute);
        values.put(DataBases.CreateDB.ONOFF, OnOff);
        values.put(DataBases.CreateDB.NEXTDAYOFWEEK, nextDayOfWeek);
        values.put(DataBases.CreateDB.DAYOFAMPM, ampm);
        values.put(DataBases.CreateDB.PLACE, place);
        values.put(DataBases.CreateDB.PENALTY, penalty);
        values.put(DataBases.CreateDB.PENALTY_LINE, penalty);

        return mDB.update(DataBases.CreateDB._TABLENAME0, values, "_id=" + id, null) > 0;
    }

    // Delete All
    public void deleteAllColumns() {
        Log.i("DBtest","open deleteAllcolumns");
        mDB.delete(DataBases.CreateDB._TABLENAME0, null, null);
    }

    // Delete Column
    public boolean deleteColumn(long id){
        return mDB.delete(DataBases.CreateDB._TABLENAME0, "_id="+id, null) > 0;
    }
}