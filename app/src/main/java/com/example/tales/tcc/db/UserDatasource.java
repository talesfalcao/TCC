package com.example.tales.tcc.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.tales.tcc.Constants;

import java.util.ArrayList;

/**
 * Created by tales on 19/06/2017.
 */

public class UserDatasource {
        // Database fields
        private SQLiteDatabase database;
        private SQLiteHelper dbHelper;
        private String[] allColumns = {"_id", Constants.name, Constants.login, Constants.password, Constants.type, Constants.parent};

        public UserDatasource(Context context) {
            dbHelper = new SQLiteHelper(context);
        }

        public void open() throws SQLException {
            database = dbHelper.getWritableDatabase();
        }

        public void close() {
            dbHelper.close();
        }

        public void update(UserModel user) {
            /*ContentValues values = new ContentValues();

            values.put(Constants.hour, user.getHour());

            database.update(Constants.user, values, Constants.timestamp + "=" + user.getTimestamp(), null);*/
        }

        public void insert(UserModel user) {
            ContentValues values = new ContentValues();
            values.put(Constants.name, user.getName());
            values.put(Constants.login, user.getLogin());
            values.put(Constants.password, user.getPassword());
            values.put(Constants.type, user.getType());
            values.put(Constants.parent, user.getParent());

            database.insert(Constants.user, null, values);
        }

        public ArrayList<UserModel> getLocations(String selection, String[] args) {
            ArrayList<UserModel> locations = new ArrayList<>();

            Cursor cursor = database.query(Constants.user, allColumns, selection, args, null, null, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                UserModel Location = new UserModel(cursor.getString(cursor.getColumnIndex(Constants.name)),
                        cursor.getString(cursor.getColumnIndex(Constants.login)),
                        cursor.getString(cursor.getColumnIndex(Constants.password)),
                        cursor.getString(cursor.getColumnIndex(Constants.type)),
                        cursor.getString(cursor.getColumnIndex(Constants.parent)));
                locations.add(Location);
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
            return locations;
        }

    public int delete(String selection, String[] args) {
        return database.delete(Constants.user, selection, args);
    }
}
