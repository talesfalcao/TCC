package com.example.tales.tcc.db;

import android.content.Context;

import com.example.tales.tcc.Constants;

import java.util.ArrayList;

/**
 * Created by tales on 10/08/2017.
 */

public class UserLocModel {
    public String mLatitude, mLongitude, mName;

    public UserLocModel(String name, String latitude, String longitude) {
        this.mName = name;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
    }

    public void insertLocation(Context context) {
        UserLocDatasource locationDatasource = new UserLocDatasource(context);
        locationDatasource.open();
        locationDatasource.insert(this);
        locationDatasource.close();
    }

    public static ArrayList<UserLocModel> getAllLocations(Context context) {
        ArrayList<UserLocModel> locations;
        UserLocDatasource locationDatasource = new UserLocDatasource(context);
        locationDatasource.open();
        locations = locationDatasource.getLocations(null, null);
        locationDatasource.close();
        return locations;
    }
}
