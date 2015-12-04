package com.majateam.bikespot.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.majateam.bikespot.database.BikeTable;
import com.majateam.bikespot.model.Bike;

import java.util.List;

/**
 * Created by Nicolas Martino on 11/06/15.
 */
public class BikeLocationDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "bikelocation.db";

    public BikeLocationDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase database) {
        BikeTable.onCreate(database);
    }
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        BikeTable.onUpgrade(database, oldVersion, newVersion);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void onInsertList(SQLiteDatabase db, List<Bike> bikes) {
        BikeTable.onInsertList(db, bikes);
    }
}