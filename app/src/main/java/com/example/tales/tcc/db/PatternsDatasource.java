package com.example.tales.tcc.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.tales.tcc.Constants;

import java.util.ArrayList;

/**
 * Created by tales on 10/05/2017.
 */

public class PatternsDatasource {
    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;
    private String[] allColumns = {Constants.start_time, Constants.end_time, Constants.weekday, Constants.latitude, Constants.longitude };

    public PatternsDatasource(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(PatternsModel patternsModel) {
        ContentValues values = new ContentValues();
        values.put(Constants.end_time, patternsModel.getEnd());
        values.put(Constants.start_time, patternsModel.getStart());
        values.put(Constants.weekday, patternsModel.getDay());
        values.put(Constants.latitude, patternsModel.getLatitude());
        values.put(Constants.longitude, patternsModel.getLongitude());

        database.insert(Constants.patterns, null, values);
    }

    public ArrayList<PatternsModel> getPatterns(String selection, String[] args) {
        ArrayList<PatternsModel> patternsModels = new ArrayList<>();

        Cursor cursor = database.query(Constants.patterns, allColumns, selection, args, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            PatternsModel pattern = new PatternsModel(cursor.getString(cursor.getColumnIndex(Constants.weekday)),
                    cursor.getString(cursor.getColumnIndex(Constants.start_time)),
                    cursor.getString(cursor.getColumnIndex(Constants.end_time)),
                    cursor.getString(cursor.getColumnIndex(Constants.latitude)),
                    cursor.getString(cursor.getColumnIndex(Constants.longitude)));
            patternsModels.add(pattern);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return patternsModels;
    }

    public int delete(String selection, String[] args) {
        return database.delete(Constants.patterns, selection, args);
    }
}
