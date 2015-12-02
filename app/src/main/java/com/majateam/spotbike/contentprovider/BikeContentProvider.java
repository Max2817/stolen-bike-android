package com.majateam.spotbike.contentprovider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.majateam.spotbike.database.BikeTable;
import com.majateam.spotbike.helper.BikeLocationDbHelper;

import java.util.Arrays;
import java.util.HashSet;

public class BikeContentProvider extends ContentProvider {

    // database
    private BikeLocationDbHelper database;

    // used for the UriMacher
    private static final int BIKES = 10;
    private static final int BIKE_ID = 20;

    private static final String AUTHORITY = "com.majateam.spotbike.contenprovider";

    private static final String BASE_PATH = "bikes";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/bikes";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/bike";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, BIKES);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", BIKE_ID);
    }

    public BikeContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case BIKES:
                rowsDeleted = sqlDB.delete(BikeTable.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case BIKE_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(BikeTable.TABLE_NAME,
                            BikeTable.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(BikeTable.TABLE_NAME,
                            BikeTable.COLUMN_ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        long id = 0;
        switch (uriType) {
            case BIKES:
                id = sqlDB.insert(BikeTable.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public boolean onCreate() {
        database = new BikeLocationDbHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // Uisng SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // check if the caller has requested a column which does not exists
        checkColumns(projection);

        // Set the table
        queryBuilder.setTables(BikeTable.TABLE_NAME);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case BIKES:
                break;
            case BIKE_ID:
                // adding the ID to the original query
                queryBuilder.appendWhere(BikeTable.COLUMN_ID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case BIKES:
                rowsUpdated = sqlDB.update(BikeTable.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case BIKE_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(BikeTable.TABLE_NAME,
                            values,
                            BikeTable.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(BikeTable.TABLE_NAME,
                            values,
                            BikeTable.COLUMN_ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private void checkColumns(String[] projection) {
        String[] available = { BikeTable.COLUMN_NAME_LAT,
                BikeTable.COLUMN_NAME_LNG, BikeTable.COLUMN_ID};
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<>(Arrays.asList(available));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }
}
