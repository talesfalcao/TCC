package com.example.tales.tcc.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.tales.tcc.Constants;

import java.util.ArrayList;

/**
 * Created by tales on 10/08/2017.
 */

public class UserLocDatasource {
    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;
    private String[] allColumns = {Constants.name,  Constants.latitude, Constants.longitude };

    public UserLocDatasource(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(UserLocModel location) {
        ContentValues values = new ContentValues();
        values.put(Constants.name, location.mName);
        values.put(Constants.latitude, location.mLatitude);
        values.put(Constants.longitude, location.mLongitude);

        database.insertWithOnConflict(Constants.userLoc, Constants.name, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public ArrayList<UserLocModel> getLocations(String selection, String[] args) {
        ArrayList<UserLocModel> locations = new ArrayList<>();

        Cursor cursor = database.query(Constants.userLoc, allColumns, selection, args, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            UserLocModel location = new UserLocModel(cursor.getString(cursor.getColumnIndex(Constants.name)),
                    cursor.getString(cursor.getColumnIndex(Constants.latitude)),
                    cursor.getString(cursor.getColumnIndex(Constants.longitude)));
            locations.add(location);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return locations;
    }
}
