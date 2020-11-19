package com.majateam.bikespot.helper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.majateam.bikespot.database.BikeTable
import com.majateam.bikespot.model.Bike

/**
 * Created by Nicolas Martino on 11/06/15.
 */
class BikeLocationDbHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(database: SQLiteDatabase) {
        BikeTable.onCreate(database)
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        BikeTable.onUpgrade(database, oldVersion, newVersion)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    fun onInsertList(db: SQLiteDatabase?, bikes: List<Bike>) {
        BikeTable.onInsertList(db!!, bikes)
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "bikelocation.db"
    }
}