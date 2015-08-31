package com.majateam.allocyclo.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.majateam.allocyclo.model.Bike;

import java.util.List;

/**
 * Created by Nicolas Martino on 16/06/15.
 */
public class BikeTable {

    public static final String TABLE_NAME = "bike";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME_LAT = "lat";
    public static final String COLUMN_NAME_LNG = "lng";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    private static final String DATABASE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY," +
                    COLUMN_NAME_LAT + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_LNG + TEXT_TYPE + " )";

    private static final String INSERT_STMT = "INSERT INTO " + TABLE_NAME + " (" + COLUMN_NAME_LAT + ", " + COLUMN_NAME_LNG + ") VALUES (?, ?);";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    private static final String DATABASE_DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME;



    public static void onDelete(SQLiteDatabase database) {
        database.execSQL(DATABASE_DELETE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(BikeTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL(DATABASE_DELETE);
        onCreate(database);
    }

    public static boolean onInsertList(SQLiteDatabase database, List<Bike> bikes)
    {
        SQLiteStatement insStmt = database.compileStatement(INSERT_STMT);
        database.beginTransaction();
        try {
            for(Bike bike : bikes) {
                insStmt.bindString(1, bike.getLat());
                insStmt.bindString(2, bike.getLng());
                insStmt.executeInsert();    //  should really check value here!
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }

        return true;
    }

    public interface BikeTableListener {
        void onInsertFinish();
    }
}
