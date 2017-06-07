package com.example.tales.tcc.db;

import android.content.Context;

import com.example.tales.tcc.Constants;

import java.util.ArrayList;

/**
 * Created by tales on 02/05/2017.
 */

public class LocationModel {
    private String mLatitude, mLongitude;
    private String mTimestamp, mHour, mDay, mDate;
    private int mBottom, mTop;

    public LocationModel(String timestamp, String latitude, String longitude, String hour, String day, String date, int bottom, int top) {
        this.mLatitude = latitude;
        this.mBottom = bottom;
        this.mTop = top;
        this.mLongitude = longitude;
        this.mTimestamp = timestamp;
        this.mHour = hour;
        this.mDay= day;
        this.mDate = date;
    }



    public int getBottom() {
        return mBottom;
    }

    public void setBottom(int bottom) {
        this.mBottom = bottom;
    }

    public int getTop() {
        return mTop;
    }

    public void setTop(int top) {
        this.mTop = top;
    }

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

    public String getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(String timestamp) {
        this.mTimestamp = timestamp;
    }

    public String getHour() {
        return mHour;
    }

    public void setHour(String hour) {
        this.mHour = hour;
    }

    public String getDay() {
        return mDay;
    }

    public void setDay(String day) {
        this.mDay = day;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        this.mDate = date;
    }

    public void insertLocation(Context context) {
        LocationDatasource locationDatasource = new LocationDatasource(context);
        locationDatasource.open();
        locationDatasource.insert(this);
        locationDatasource.close();
    }

    public static ArrayList<LocationModel> getAllLocations(Context context) {
        ArrayList<LocationModel> locations;
        LocationDatasource locationDatasource = new LocationDatasource(context);
        locationDatasource.open();
        locations = locationDatasource.getLocations(null, null);
        locationDatasource.close();
        return locations;
    }

    public static ArrayList<LocationModel> getLocationsByWeekday(Context context, String weekday) {
        ArrayList<LocationModel> locations;
        LocationDatasource locationDatasource = new LocationDatasource(context);
        locationDatasource.open();
        locations = locationDatasource.getLocations(Constants.weekday + "=?", new String[]{weekday});
        locationDatasource.close();
        return locations;
    }

    public static ArrayList<LocationModel> getLocationsByWeekdayHourRange(Context context, String weekday, String min, String max) {
        ArrayList<LocationModel> locations;
        LocationDatasource locationDatasource = new LocationDatasource(context);
        locationDatasource.open();
        locations = locationDatasource.getLocations(Constants.weekday + "=? AND " + Constants.hour + ">=? AND " + Constants.hour + "<=?", new String[]{weekday, min, max});
        locationDatasource.close();
        return locations;
    }

    public static ArrayList<LocationModel> getLocationsByDateHourRange(Context context, String date, String min, String max) {
        ArrayList<LocationModel> locations;
        LocationDatasource locationDatasource = new LocationDatasource(context);
        locationDatasource.open();
        locations = locationDatasource.getLocations(Constants.date + "=? AND " + Constants.hour + ">=? AND " + Constants.hour + "<=?", new String[]{date, min, max});
        locationDatasource.close();
        return locations;
    }

    public void updateLocationDate(Context context) {
        LocationDatasource locationDatasource = new LocationDatasource(context);
        locationDatasource.open();
        locationDatasource.update(this);
        locationDatasource.close();
    }

    public int updateLocationBotTop(Context context) {
        LocationDatasource locationDatasource = new LocationDatasource(context);
        locationDatasource.open();
        int a = locationDatasource.updateBotTop(this);
        locationDatasource.close();
        return a;
    }

    @Override
    public String toString() {
        return this.mDay + ", " +this.mDate + " at " + this.mHour + "{" + mLatitude + ", " + mLongitude + ", " + mBottom + ", " + mTop + "}";
    }
}
