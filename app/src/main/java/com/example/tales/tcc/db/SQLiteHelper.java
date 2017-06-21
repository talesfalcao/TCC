package com.example.tales.tcc.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.tales.tcc.Constants;

/**
 * Created by tales on 02/05/2017.
 */

public class SQLiteHelper  extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "database.db";
    private static final int VERSAO = 15;

    public SQLiteHelper(Context context){
        super(context, DATABASE_NAME, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createLocation = "CREATE TABLE " + Constants.location + " (" + Constants.timestamp + " text, " + Constants.date + " text, " + Constants.hour + " text, " + Constants.weekday + " text, " + Constants.latitude + " text, " + Constants.longitude + " text, " + Constants.bottom + " integer, " + Constants.top + " integer " + " )";
        String createAverages = "CREATE TABLE " + Constants.averages + " ( _id INTEGER PRIMARY KEY, " + Constants.start_time + " integer, " + Constants.end_time + " integer, " + Constants.weekday + " text, " + Constants.latitude + " text, " + Constants.longitude + " text, " + Constants.count + " text, " + Constants.date + " text" + " )";
        String createPattern = "CREATE TABLE " + Constants.patterns + " (" + Constants.start_time + " integer, " + Constants.end_time + " integer, " + Constants.weekday + " text, " + Constants.latitude + " text, " + Constants.type + " text, " + Constants.count + " integer, " + Constants.longitude + " text" + " )";
        String createGrouping = "CREATE TABLE " + Constants.grouping + " (" + Constants.start_time + " integer, " + Constants.end_time + " integer, " + Constants.latitude + " text, " + Constants.longitude + " text, " + Constants.count + " integer, " + Constants.weekday + " text )";
        String createUser = "CREATE TABLE " + Constants.user + " ( _id INTEGER PRIMARY KEY, " +  Constants.name + " text, " + Constants.login + " text, " + Constants.password + " text, " + Constants.type + " text, " + Constants.parent + " text )";


        db.execSQL(createLocation);
        db.execSQL(createAverages);
        db.execSQL(createPattern);
        db.execSQL(createGrouping);
        db.execSQL(createUser);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS " + Constants.averages);
        //db.execSQL("DROP TABLE IF EXISTS " + Constants.patterns);
        //db.execSQL("DROP TABLE IF EXISTS " + Constants.grouping);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.user);

        //String createAverages = "CREATE TABLE " + Constants.averages + " ( _id INTEGER PRIMARY KEY, " + Constants.start_time + " integer, " + Constants.end_time + " integer, " + Constants.weekday + " text, " + Constants.latitude + " text, " + Constants.longitude + " text, " + Constants.count + " text, " + Constants.date + " text" + " )";
        //String createPattern = "CREATE TABLE " + Constants.patterns + " (" + Constants.start_time + " integer, " + Constants.end_time + " integer, " + Constants.weekday + " text, " + Constants.latitude + " text, " + Constants.type + " text, " + Constants.count + " integer, " + Constants.longitude + " text" + " )";
        //String createGrouping = "CREATE TABLE " + Constants.grouping + " (" + Constants.start_time + " integer, " + Constants.end_time + " integer, " + Constants.latitude + " text, " + Constants.longitude + " text, " + Constants.count + " integer, " + Constants.weekday + " text )";
        String createUser = "CREATE TABLE " + Constants.user + " ( _id INTEGER PRIMARY KEY, " +  Constants.name + " text, " + Constants.login + " text, " + Constants.password + " text, " + Constants.type + " text, " + Constants.parent + " text )";

        //db.execSQL(createAverages);
        //db.execSQL(createPattern);
        //db.execSQL(createGrouping);
        db.execSQL(createUser);

    }
}