package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbMOpenHelper {

    private static final String DATABASE_NAME = "InnerDatabase(SQLite).db2";
    private static final int DATABASE_VERSION = 4;
    public static SQLiteDatabase mDB;
    private DatabaseHelper mDBHelper;
    private Context mCtx;

    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(DataBasesMessage.CreateDB._CREATE0);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            db.execSQL("DROP TABLE IF EXISTS "+DataBasesMessage.CreateDB._TABLENAME0);
            onCreate(db);
        }
    }

    public DbMOpenHelper(Context context){
        this.mCtx = context;
    }

    public DbMOpenHelper open() throws SQLException {
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

    public long insertColumn( String phoneNumber, String message, String name){
        Log.i("DBtest","open inserColumn ");
        ContentValues values = new ContentValues();
        values.put(DataBasesMessage.CreateDB.PHONENUMBER, phoneNumber);
        values.put(DataBasesMessage.CreateDB.MESSAGE, message);
        values.put(DataBasesMessage.CreateDB.NAME,name);
        Log.i("DBID","ID : " + values.get(DataBases.CreateDB._ID));
        return mDB.insert(DataBasesMessage.CreateDB._TABLENAME0, null, values);
    }

    public Cursor selectColumns(){
        return mDB.query(DataBasesMessage.CreateDB._TABLENAME0, null, null, null, null, null, null);
    }

    public boolean updateColumn(long id,String phoneNumber, String message, String name){
        ContentValues values = new ContentValues();
        values.put(DataBasesMessage.CreateDB.PHONENUMBER, phoneNumber);
        values.put(DataBasesMessage.CreateDB.MESSAGE, message);
        values.put(DataBasesMessage.CreateDB.NAME,name);

        return mDB.update(DataBasesMessage.CreateDB._TABLENAME0, values, "_id=" + id, null) > 0;
    }

    // Delete All
    public void deleteAllColumns() {
        Log.i("DBtest","open deleteAllcolumns");
        mDB.delete(DataBasesMessage.CreateDB._TABLENAME0, null, null);
    }

    // Delete Column
    public boolean deleteColumn(long id){
        return mDB.delete(DataBasesMessage.CreateDB._TABLENAME0, "_id="+id, null) > 0;
    }
}