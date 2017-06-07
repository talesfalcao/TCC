package com.example.tales.tcc.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.tales.tcc.Constants;

import java.util.ArrayList;

/**
 * Created by tales on 02/05/2017.
 */

public class LocationDatasource {
    // Database fields
    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;
    private String[] allColumns = {Constants.timestamp, Constants.date, Constants.hour, Constants.weekday, Constants.latitude, Constants.longitude, Constants.bottom, Constants.top };

    public LocationDatasource(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void update(LocationModel location) {
        ContentValues values = new ContentValues();

        values.put(Constants.hour, location.getHour());

        database.update(Constants.location, values, Constants.timestamp + "=" + location.getTimestamp(), null);
    }

    public int updateBotTop(LocationModel location) {
        ContentValues values = new ContentValues();

        values.put(Constants.bottom, location.getBottom());
        values.put(Constants.top, location.getTop());

        return database.update(Constants.location, values, Constants.timestamp + "=? AND " + Constants.date + "=?", new String[] {location.getTimestamp(), location.getDate()});
    }

    public void insert(LocationModel location) {
        ContentValues values = new ContentValues();
        values.put(Constants.timestamp, location.getTimestamp());
        values.put(Constants.date, location.getDate());
        values.put(Constants.hour, location.getHour());
        values.put(Constants.weekday, location.getDay());
        values.put(Constants.latitude, location.getLatitude());
        values.put(Constants.longitude, location.getLongitude());
        values.put(Constants.bottom, location.getBottom());
        values.put(Constants.top, location.getTop());

        database.insert(Constants.location, null, values);
    }

    public ArrayList<LocationModel> getLocations(String selection, String[] args) {
        ArrayList<LocationModel> locations = new ArrayList<>();

        Cursor cursor = database.query(Constants.location, allColumns, selection, args, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            LocationModel Location = new LocationModel(cursor.getString(cursor.getColumnIndex(Constants.timestamp)),
                                                       cursor.getString(cursor.getColumnIndex(Constants.latitude)),
                                                       cursor.getString(cursor.getColumnIndex(Constants.longitude)),
                                                       cursor.getString(cursor.getColumnIndex(Constants.hour)),
                                                       cursor.getString(cursor.getColumnIndex(Constants.weekday)),
                                                       cursor.getString(cursor.getColumnIndex(Constants.date)),
                                                       cursor.getInt(cursor.getColumnIndex(Constants.bottom)),
                                                       cursor.getInt(cursor.getColumnIndex(Constants.top)));
            locations.add(Location);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return locations;
    }
}
