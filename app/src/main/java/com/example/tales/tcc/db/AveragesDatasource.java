package com.example.tales.tcc.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.tales.tcc.Constants;

import java.security.Key;
import java.util.ArrayList;

/**
 * Created by tales on 10/05/2017.
 */

public class AveragesDatasource {
    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;
    private String[] allColumns = {"_id", Constants.start_time, Constants.end_time, Constants.weekday, Constants.latitude, Constants.longitude, Constants.count, Constants.date };

    public AveragesDatasource(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(AveragesModel averagesModel) {
        ContentValues values = new ContentValues();
        values.put(Constants.end_time, averagesModel.getEnd());
        values.put(Constants.start_time, averagesModel.getStart());
        values.put(Constants.weekday, averagesModel.getDay());
        values.put(Constants.latitude, averagesModel.getLatitude());
        values.put(Constants.longitude, averagesModel.getLongitude());
        values.put(Constants.count, averagesModel.getCount());
        values.put(Constants.date, averagesModel.getDate());

        database.insert(Constants.averages, null, values);
    }

    public int update(AveragesModel average) {
        ContentValues values = new ContentValues();

        values.put(Constants.start_time, average.getStart());
        values.put(Constants.end_time, average.getEnd());
        values.put(Constants.weekday, average.getDay());
        values.put(Constants.latitude, average.getLatitude());
        values.put(Constants.longitude, average.getLongitude());
        values.put(Constants.count, average.getCount());
        return database.update(Constants.averages, values, Constants.date + "=? AND " + Constants.start_time + "=? AND " + Constants.end_time + "=? AND " + Constants.weekday + "=?", new String[]{average.getDate(), String.valueOf(average.getStart()), String.valueOf(average.getEnd()), average.getDay()});
    }


    public ArrayList<AveragesModel> getAverages(String selection, String[] args) {
        ArrayList<AveragesModel> averagesModels = new ArrayList<>();

        Cursor cursor = database.query(Constants.averages, allColumns, selection, args, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            AveragesModel average = new AveragesModel(cursor.getString(cursor.getColumnIndex(Constants.weekday)),
                    cursor.getInt(cursor.getColumnIndex(Constants.start_time)),
                    cursor.getInt(cursor.getColumnIndex(Constants.end_time)),
                    cursor.getDouble(cursor.getColumnIndex(Constants.latitude)),
                    cursor.getDouble(cursor.getColumnIndex(Constants.longitude)),
                    cursor.getInt(cursor.getColumnIndex(Constants.count)),
                    cursor.getString(cursor.getColumnIndex(Constants.date)));
            averagesModels.add(average);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return averagesModels;
    }

    public AveragesModel getLastAverage() {
        AveragesModel averageModel = null;

        Cursor cursor = database.rawQuery("SELECT * FROM " + Constants.averages + " ORDER BY _id DESC LIMIT 1", new String[]{});

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            averageModel = new AveragesModel(cursor.getString(cursor.getColumnIndex(Constants.weekday)),
                    cursor.getInt(cursor.getColumnIndex(Constants.start_time)),
                    cursor.getInt(cursor.getColumnIndex(Constants.end_time)),
                    cursor.getDouble(cursor.getColumnIndex(Constants.latitude)),
                    cursor.getDouble(cursor.getColumnIndex(Constants.longitude)),
                    cursor.getInt(cursor.getColumnIndex(Constants.count)),
                    cursor.getString(cursor.getColumnIndex(Constants.date)));
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return averageModel;
    }

    public int delete(String selection, String[] args) {
       return database.delete(Constants.averages, selection, args);
    }
}
