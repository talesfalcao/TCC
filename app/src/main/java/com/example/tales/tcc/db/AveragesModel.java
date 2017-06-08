package com.example.tales.tcc.db;

import android.content.Context;
import android.util.Log;

import com.example.tales.tcc.Constants;

import java.util.ArrayList;

/**
 * Created by tales on 10/05/2017.
 */

public class AveragesModel {
    private double mLatitude, mLongitude;
    private int mStart, mEnd, mCount;
    private String mDay, mDate;

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        this.mDate = date;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        this.mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        this.mLongitude = longitude;
    }

    public int getStart() {
        return mStart;
    }

    public void setStart(int start) {
        this.mStart = start;
    }

    public int getEnd() {
        return mEnd;
    }

    public void setEnd(int end) {
        this.mEnd = end;
    }

    public String getDay() {
        return mDay;
    }

    public void setDay(String day) {
        this.mDay = day;
    }

    public int getCount() {
        return mCount;
    }

    public void setCount(int count) {
        this.mCount = count;
    }


    public AveragesModel(String day, int start, int end, double latitude, double longitude, int count, String date) {
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mDay = day;
        this.mStart = start;
        this.mEnd = end;
        this.mCount = count;
        this.mDate = date;
    }

    public void insertAverage(Context context) {
        AveragesDatasource averagesDatasource = new AveragesDatasource(context);
        averagesDatasource.open();
        averagesDatasource.insert(this);
        averagesDatasource.close();
    }

    public void updateAverage(Context context) {
        AveragesDatasource averagesDatasource = new AveragesDatasource(context);
        averagesDatasource.open();
        int a = averagesDatasource.update(this);
        averagesDatasource.close();
        Log.d("AveragesModel", "Updated " + a);
    }

    public static ArrayList<AveragesModel> getAllAverages(Context context) {
        ArrayList<AveragesModel> averages;
        AveragesDatasource averagesDatasource = new AveragesDatasource(context);
        averagesDatasource.open();
        averages = averagesDatasource.getAverages(null, null);
        averagesDatasource.close();
        return averages;
    }

    public static ArrayList<AveragesModel> getAveragesByWeekday(Context context, String weekday) {
        ArrayList<AveragesModel> averages;
        AveragesDatasource averagesDatasource = new AveragesDatasource(context);
        averagesDatasource.open();
        averages = averagesDatasource.getAverages(Constants.weekday + "=?", new String[]{weekday});
        averagesDatasource.close();
        return averages;
    }

    public static ArrayList<AveragesModel> getAveragesByWeekdayHour(Context context, String weekday, String hour) {
        ArrayList<AveragesModel> averages;
        AveragesDatasource averagesDatasource = new AveragesDatasource(context);
        averagesDatasource.open();
        averages = averagesDatasource.getAverages(Constants.weekday + "=? AND " + Constants.start_time + "<=? AND " + Constants.end_time + ">=?", new String[]{weekday, hour, hour});
        averagesDatasource.close();
        return averages;
    }

    public static ArrayList<AveragesModel> getAveragesLastThree(Context context, String weekday, String hour) {
        ArrayList<AveragesModel> averages;
        AveragesDatasource averagesDatasource = new AveragesDatasource(context);
        averagesDatasource.open();
        averages = averagesDatasource.getAverages(Constants.weekday + "=? AND " + Constants.start_time + "<=? AND " + Constants.end_time + ">=? ORDER BY _id DESC LIMIT 3", new String[]{weekday, hour, hour});
        averagesDatasource.close();
        return averages;
    }

    public static ArrayList<AveragesModel> getAveragesByDateHour(Context context, String date, String start, String end) {
        ArrayList<AveragesModel> averages;
        AveragesDatasource averagesDatasource = new AveragesDatasource(context);
        averagesDatasource.open();
        averages = averagesDatasource.getAverages(Constants.date + "=? AND " + Constants.start_time + "=? AND " + Constants.end_time + "=?", new String[]{date, start, end});
        averagesDatasource.close();
        return averages;
    }

    public static int deleteAllAverages(Context context) {
        AveragesDatasource averagesDatasource = new AveragesDatasource(context);
        averagesDatasource.open();
        int deleted = averagesDatasource.delete(null,null);
        averagesDatasource.close();
        return deleted;
    }

    public static AveragesModel getLastAverage(Context context) {
        AveragesDatasource averagesDatasource = new AveragesDatasource(context);
        averagesDatasource.open();
        AveragesModel average = averagesDatasource.getLastAverage();
        averagesDatasource.close();
        return average;
    }

    @Override
    public String toString() {
        return this.getDate() + " from " + this.getStart() + " to " + this.getEnd() + " @ [" + this.getLatitude() + ", " + this.getLongitude() + "]";
    }
}
