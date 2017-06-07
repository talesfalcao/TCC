package com.example.tales.tcc.db;

import android.content.Context;
import android.util.Log;

import com.example.tales.tcc.Constants;

import java.util.ArrayList;

/**
 * Created by tales on 24/05/2017.
 */

public class GroupingModel {
    private double mLatitude;
    private double mLongitude;
    private int mStart, mEnd;
    private String mDay;
    int mCount;

    public int getCount() {
        return mCount;
    }

    public void setCount(int count) {
        this.mCount = count;
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


    public GroupingModel(int start, int end, double latitude, double longitude, int count, String day) {
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mStart = start;
        this.mEnd = end;
        this.mCount = count;
        this.mDay = day;
    }

    public void insertGrouping(Context context) {
        GroupingDatasource groupingDatasource = new GroupingDatasource(context);
        groupingDatasource.open();
        groupingDatasource.insert(this);
        groupingDatasource.close();
    }

    public static ArrayList<GroupingModel> getAllGroupings(Context context) {
        ArrayList<GroupingModel> patterns;
        GroupingDatasource groupingDatasource = new GroupingDatasource(context);
        groupingDatasource.open();
        patterns = groupingDatasource.getGroupings(null, null);
        groupingDatasource.close();
        return patterns;
    }

    public static ArrayList<GroupingModel> getGroupingsByWeekday(Context context, String weekday) {
        ArrayList<GroupingModel> patterns;
        GroupingDatasource groupingDatasource = new GroupingDatasource(context);
        groupingDatasource.open();
        patterns = groupingDatasource.getGroupings(Constants.weekday + "=?", new String[]{weekday});
        groupingDatasource.close();
        return patterns;
    }

    public static ArrayList<GroupingModel> getGroupingsByWeekdayHour(Context context, String weekday, String hour) {
        ArrayList<GroupingModel> patterns;
        GroupingDatasource groupingDatasource = new GroupingDatasource(context);
        groupingDatasource.open();
        patterns = groupingDatasource.getGroupings(Constants.weekday + "=? AND " + Constants.start_time + "<=? AND " + Constants.end_time + ">=?", new String[]{weekday, hour, hour});
        groupingDatasource.close();
        return patterns;
    }

    public void updateGrouping(Context context) {
        GroupingDatasource groupingDatasource = new GroupingDatasource(context);
        groupingDatasource.open();
        int a = groupingDatasource.update(this);
        groupingDatasource.close();
    }

    public static void deleteAll(Context context) {
        GroupingDatasource groupingDatasource = new GroupingDatasource(context);
        groupingDatasource.open();
        int a = groupingDatasource.delete(null, null);
        groupingDatasource.close();
        Log.d("Testing delete all", "deleted " + a);
    }

    @Override
    public String toString() {
        return this.mDay + " from " + this.getStart() + " to " + this.getEnd() + " @ [" + this.getLatitude() + ", " + this.getLongitude() + "] -> " + this.getCount() + " times";
    }
}
