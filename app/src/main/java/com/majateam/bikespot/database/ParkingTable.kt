package com.majateam.bikespot.database

import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.majateam.bikespot.model.Bike

/**
 * Created by Nicolas Martino on 29/06/15.
 */
object ParkingTable {
    const val TABLE_NAME = "parking"
    const val COLUMN_ID = "_id"
    const val COLUMN_NAME_LAT = "lat"
    const val COLUMN_NAME_LNG = "lng"
    private const val TEXT_TYPE = " TEXT"
    private const val COMMA_SEP = ","
    private const val DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY," +
            COLUMN_NAME_LAT + TEXT_TYPE + COMMA_SEP +
            COLUMN_NAME_LNG + TEXT_TYPE + " )"
    private const val INSERT_STMT = "INSERT INTO " + TABLE_NAME + " (" + COLUMN_NAME_LAT + ", " + COLUMN_NAME_LNG + ") VALUES (?, ?);"
    fun onCreate(database: SQLiteDatabase) {
        database.execSQL(DATABASE_CREATE)
    }

    private const val DATABASE_DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME
    fun onDelete(database: SQLiteDatabase) {
        database.execSQL(DATABASE_DELETE)
    }

    fun onUpgrade(database: SQLiteDatabase, oldVersion: Int,
                  newVersion: Int) {
        Log.w(BikeTable::class.java.name, "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data")
        database.execSQL(DATABASE_DELETE)
        onCreate(database)
    }

    fun onInsertList(database: SQLiteDatabase, bikes: List<Bike>): Boolean {
        val insStmt = database.compileStatement(INSERT_STMT)
        database.beginTransaction()
        try {
            for (bike in bikes) {
                insStmt.bindString(1, bike.lat)
                insStmt.bindString(2, bike.lng)
                insStmt.executeInsert() //  should really check value here!
            }
            database.setTransactionSuccessful()
        } finally {
            database.endTransaction()
        }
        return true
    }

    interface BikeTableListener {
        fun onInsertFinish()
    }
}