package com.example.tales.tcc.db;

import android.content.Context;

import com.example.tales.tcc.Constants;
import com.example.tales.tcc.services.LocationService;

import java.util.ArrayList;

/**
 * Created by tales on 12/09/2017.
 */

public class UserSetPatternModel {
    public String mStart, mEnd, mDay, mLatitude, mLongitude;

    public UserSetPatternModel(String start, String end, String day, String latitude, String longitude) {
        this.mStart = start;
        this.mEnd = end;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mDay = day;
    }

    public void insertLocation(Context context) {
        UserSetPatternDatasource locationDatasource = new UserSetPatternDatasource(context);
        locationDatasource.open();
        locationDatasource.insert(this);
        locationDatasource.close();
    }

    public static ArrayList<UserSetPatternModel> getAllLocations(Context context) {
        ArrayList<UserSetPatternModel> locations;
        UserSetPatternDatasource locationDatasource = new UserSetPatternDatasource(context);
        locationDatasource.open();
        locations = locationDatasource.getLocations(null, null);
        locationDatasource.close();
        return locations;
    }

    public static ArrayList<UserSetPatternModel> getLocationsDay(Context context, String s) {
        ArrayList<UserSetPatternModel> locations;
        UserSetPatternDatasource locationDatasource = new UserSetPatternDatasource(context);
        locationDatasource.open();
        locations = locationDatasource.getLocations(Constants.weekday + "=?", new String[]{s});
        locationDatasource.close();
        return locations;
    }
}
