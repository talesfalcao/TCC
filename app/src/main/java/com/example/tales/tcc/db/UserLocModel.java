package com.example.tales.tcc.db;

import android.content.Context;

import com.example.tales.tcc.Constants;

import java.util.ArrayList;

/**
 * Created by tales on 10/08/2017.
 */

public class UserLocModel {
    public String id, mLatitude, mLongitude, mName, mInside;

    public UserLocModel(String id, String name, String latitude, String longitude, String inside) {
        this.id = id;
        this.mName = name;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mInside = inside;
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

    public static int deleteName(Context context, String id) {
        int deleted;
        UserLocDatasource locationDatasource = new UserLocDatasource(context);
        locationDatasource.open();
        deleted = locationDatasource.delete(Constants.id + "=?", new String[]{id});
        locationDatasource.close();
        return deleted;
    }
}
