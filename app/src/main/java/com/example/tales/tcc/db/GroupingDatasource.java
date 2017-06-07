package com.example.tales.tcc.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.tales.tcc.Constants;

import java.util.ArrayList;

/**
 * Created by tales on 24/05/2017.
 */

public class GroupingDatasource {
    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;
    private String[] allColumns = {Constants.start_time, Constants.end_time, Constants.latitude, Constants.longitude, Constants.count, Constants.weekday };

    public GroupingDatasource(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(GroupingModel groupingModel) {
        ContentValues values = new ContentValues();
        values.put(Constants.end_time, groupingModel.getEnd());
        values.put(Constants.start_time, groupingModel.getStart());
        values.put(Constants.latitude, groupingModel.getLatitude());
        values.put(Constants.longitude, groupingModel.getLongitude());
        values.put(Constants.count, groupingModel.getCount());
        values.put(Constants.weekday, groupingModel.getDay());

        database.insert(Constants.grouping, null, values);
    }

    public ArrayList<GroupingModel> getGroupings(String selection, String[] args) {
        ArrayList<GroupingModel> groupingModels = new ArrayList<>();

        Cursor cursor = database.query(Constants.grouping, allColumns, selection, args, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            GroupingModel pattern = new GroupingModel(cursor.getInt(cursor.getColumnIndex(Constants.start_time)),
                    cursor.getInt(cursor.getColumnIndex(Constants.end_time)),
                    cursor.getDouble(cursor.getColumnIndex(Constants.latitude)),
                    cursor.getDouble(cursor.getColumnIndex(Constants.longitude)),
                    cursor.getInt(cursor.getColumnIndex(Constants.count)),
                    cursor.getString(cursor.getColumnIndex(Constants.weekday)));
            groupingModels.add(pattern);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return groupingModels;
    }

    public int update(GroupingModel groupingModel) {
        ContentValues values = new ContentValues();

        values.put(Constants.start_time, groupingModel.getStart());
        values.put(Constants.end_time, groupingModel.getEnd());
        values.put(Constants.weekday, groupingModel.getDay());
        values.put(Constants.latitude, groupingModel.getLatitude());
        values.put(Constants.longitude, groupingModel.getLongitude());
        values.put(Constants.count, groupingModel.getCount());
        return database.update(Constants.grouping, values, Constants.start_time + "=? AND " + Constants.end_time + "=? AND " + Constants.weekday + "=?", new String[]{String.valueOf(groupingModel.getStart()), String.valueOf(groupingModel.getEnd()), groupingModel.getDay()});
    }

    public int delete(String selection, String[] args) {
        return database.delete(Constants.grouping, selection, args);
    }
}
