package com.example.tales.tcc.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.tales.tcc.Constants;

import java.util.ArrayList;

/**
 * Created by tales on 12/09/2017.
 */

public class UserSetPatternDatasource {
    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;
    private String[] allColumns = {Constants.start_time, Constants.end_time, Constants.weekday, Constants.latitude, Constants.longitude };

    public UserSetPatternDatasource(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(UserSetPatternModel location) {
        ContentValues values = new ContentValues();
        values.put(Constants.start_time, location.mStart);
        values.put(Constants.end_time, location.mEnd);
        values.put(Constants.weekday, location.mDay);
        values.put(Constants.latitude, location.mLatitude);
        values.put(Constants.longitude, location.mLongitude);

        database.insertWithOnConflict(Constants.userSetPatterns, Constants.id, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public ArrayList<UserSetPatternModel> getLocations(String selection, String[] args) {
        ArrayList<UserSetPatternModel> locations = new ArrayList<>();

        Cursor cursor = database.query(Constants.userSetPatterns, allColumns, selection, args, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            UserSetPatternModel location = new UserSetPatternModel(cursor.getString(cursor.getColumnIndex(Constants.start_time)),
                    cursor.getString(cursor.getColumnIndex(Constants.end_time)),
                    cursor.getString(cursor.getColumnIndex(Constants.weekday)),
                    cursor.getString(cursor.getColumnIndex(Constants.latitude)),
                    cursor.getString(cursor.getColumnIndex(Constants.longitude)));
            locations.add(location);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return locations;
    }

    public int delete(String selection, String[] args) {
        return database.delete(Constants.userSetPatterns, selection, args);
    }
}
