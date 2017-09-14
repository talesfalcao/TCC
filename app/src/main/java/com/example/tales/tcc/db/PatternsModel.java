package com.example.tales.tcc.db;

import android.content.Context;

import com.example.tales.tcc.Constants;
import com.example.tales.tcc.services.LocationService;

import java.util.ArrayList;

/**
 * Created by tales on 10/05/2017.
 */

public class PatternsModel {
    private String mLatitude;
    private String mLongitude;
    private String mStart, mEnd, mDay;

    public String getLatitude() {
        return mLatitude;
    }

    public void setLatitude(String latitude) {
        this.mLatitude = latitude;
    }

    public String getLongitude() {
        return mLongitude;
    }

    public void setLongitude(String longitude) {
        this.mLongitude = longitude;
    }

    public String getStart() {
        return mStart;
    }

    public void setStart(String start) {
        this.mStart = start;
    }

    public String getEnd() {
        return mEnd;
    }

    public void setEnd(String end) {
        this.mEnd = end;
    }

    public String getDay() {
        return mDay;
    }

    public void setDay(String day) {
        this.mDay = day;
    }


    public PatternsModel(String day, String start, String end, String latitude, String longitude) {
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mDay = day;
        this.mStart = start;
        this.mEnd = end;
    }

    public void insertPattern(Context context) {
        PatternsDatasource patternsDatasource = new PatternsDatasource(context);
        patternsDatasource.open();
        patternsDatasource.insert(this);
        patternsDatasource.close();
    }

    public static ArrayList<PatternsModel> getAllPatterns(Context context) {
        ArrayList<PatternsModel> patterns;
        PatternsDatasource patternsDatasource = new PatternsDatasource(context);
        patternsDatasource.open();
        patterns = patternsDatasource.getPatterns(null, null);
        patternsDatasource.close();
        return patterns;
    }

    public static ArrayList<PatternsModel> getPatternsByWeekday(Context context, String weekday) {
        ArrayList<PatternsModel> patterns;
        PatternsDatasource patternsDatasource = new PatternsDatasource(context);
        patternsDatasource.open();
        patterns = patternsDatasource.getPatterns(Constants.weekday + "=?", new String[]{weekday});
        patternsDatasource.close();
        return patterns;
    }

    public static int deleteAll(Context context) {
        int i;
        PatternsDatasource patternsDatasource = new PatternsDatasource(context);
        patternsDatasource.open();
        i = patternsDatasource.delete(null, null);
        patternsDatasource.close();
        return i;
    }

    public static int deleteAllByWeekdayHour(Context context, String weekday, String bottom) {
        int i;
        PatternsDatasource patternsDatasource = new PatternsDatasource(context);
        patternsDatasource.open();
        i = patternsDatasource.delete(Constants.weekday + "=? AND " + Constants.start_time + "=?", new String[]{weekday, bottom});
        patternsDatasource.close();
        return i;
    }

    public static ArrayList<PatternsModel> getPattern(Context context, String weekday, String bottom) {
        ArrayList<PatternsModel> patterns;
        PatternsDatasource patternsDatasource = new PatternsDatasource(context);
        patternsDatasource.open();
        patterns = patternsDatasource.getPatterns(Constants.weekday + "=? AND " + Constants.start_time + "=?", new String[]{weekday, bottom});
        patternsDatasource.close();
        return patterns;
    }
}
