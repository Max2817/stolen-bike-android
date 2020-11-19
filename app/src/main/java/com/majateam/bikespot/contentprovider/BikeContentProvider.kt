package com.majateam.bikespot.contentprovider

import android.content.ContentProvider
import android.content.ContentResolver
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.text.TextUtils
import com.majateam.bikespot.database.BikeTable
import com.majateam.bikespot.helper.BikeLocationDbHelper
import java.util.*

class BikeContentProvider : ContentProvider() {
    // database
    private var database: BikeLocationDbHelper? = null

    companion object {
        // used for the UriMacher
        private const val BIKES = 10
        private const val BIKE_ID = 20
        private const val AUTHORITY = "com.majateam.bikespot.contenprovider"
        private const val BASE_PATH = "bikes"
        val CONTENT_URI = Uri.parse("content://" + AUTHORITY
                + "/" + BASE_PATH)
        const val CONTENT_TYPE = (ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/bikes")
        const val CONTENT_ITEM_TYPE = (ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/bike")
        private val sURIMatcher = UriMatcher(UriMatcher.NO_MATCH)

        init {
            sURIMatcher.addURI(AUTHORITY, BASE_PATH, BIKES)
            sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", BIKE_ID)
        }
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val uriType = sURIMatcher.match(uri)
        val sqlDB = database!!.writableDatabase
        var rowsDeleted = 0
        rowsDeleted = when (uriType) {
            BIKES -> sqlDB.delete(BikeTable.TABLE_NAME, selection,
                selectionArgs)
            BIKE_ID -> {
                val id = uri.lastPathSegment
                if (TextUtils.isEmpty(selection)) {
                    sqlDB.delete(BikeTable.TABLE_NAME,
                        BikeTable.COLUMN_ID + "=" + id,
                        null)
                } else {
                    sqlDB.delete(BikeTable.TABLE_NAME,
                        BikeTable.COLUMN_ID + "=" + id
                                + " and " + selection,
                        selectionArgs)
                }
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        context!!.contentResolver.notifyChange(uri, null)
        return rowsDeleted
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val uriType = sURIMatcher.match(uri)
        val sqlDB = database!!.writableDatabase
        var id: Long = 0
        id = when (uriType) {
            BIKES -> sqlDB.insert(BikeTable.TABLE_NAME, null, values)
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        context!!.contentResolver.notifyChange(uri, null)
        return Uri.parse(BASE_PATH + "/" + id)
    }

    override fun onCreate(): Boolean {
        database = BikeLocationDbHelper(context)
        return false
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?,
                       selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        // Uisng SQLiteQueryBuilder instead of query() method
        val queryBuilder = SQLiteQueryBuilder()

        // check if the caller has requested a column which does not exists
        checkColumns(projection)

        // Set the table
        queryBuilder.tables = BikeTable.TABLE_NAME
        val uriType = sURIMatcher.match(uri)
        when (uriType) {
            BIKES -> {
            }
            BIKE_ID ->                 // adding the ID to the original query
                queryBuilder.appendWhere(BikeTable.COLUMN_ID + "="
                        + uri.lastPathSegment)
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        val db = database!!.writableDatabase
        val cursor = queryBuilder.query(db, projection, selection,
            selectionArgs, null, null, sortOrder)
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(context!!.contentResolver, uri)
        return cursor
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?,
                        selectionArgs: Array<String>?): Int {
        val uriType = sURIMatcher.match(uri)
        val sqlDB = database!!.writableDatabase
        var rowsUpdated = 0
        rowsUpdated = when (uriType) {
            BIKES -> sqlDB.update(BikeTable.TABLE_NAME,
                values,
                selection,
                selectionArgs)
            BIKE_ID -> {
                val id = uri.lastPathSegment
                if (TextUtils.isEmpty(selection)) {
                    sqlDB.update(BikeTable.TABLE_NAME,
                        values,
                        BikeTable.COLUMN_ID + "=" + id,
                        null)
                } else {
                    sqlDB.update(BikeTable.TABLE_NAME,
                        values,
                        BikeTable.COLUMN_ID + "=" + id
                                + " and "
                                + selection,
                        selectionArgs)
                }
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        context!!.contentResolver.notifyChange(uri, null)
        return rowsUpdated
    }

    private fun checkColumns(projection: Array<String>?) {
        val available = arrayOf(BikeTable.COLUMN_NAME_LAT,
            BikeTable.COLUMN_NAME_LNG, BikeTable.COLUMN_ID)
        if (projection != null) {
            val requestedColumns = HashSet(Arrays.asList(*projection))
            val availableColumns = HashSet(Arrays.asList(*available))
            // check if all columns which are requested are available
            require(availableColumns.containsAll(requestedColumns)) { "Unknown columns in projection" }
        }
    }
}