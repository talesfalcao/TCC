package com.example.tales.tcc;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.example.tales.tcc.db.AveragesModel;
import com.example.tales.tcc.db.LocationModel;
import com.example.tales.tcc.services.LocationService;

import java.util.ArrayList;

/**
 * Created by tales on 10/05/2017.
 */

public class AveragesFinder {

    private Context mContext;
    private static AveragesFinder instance = null;

    private AveragesFinder(Context c) {
        mContext = c;
    }

    public static AveragesFinder getInstance(Context c) {
        if(instance == null) {
            return new AveragesFinder(c);
        }
        return instance;
    }

    public void findAverages() {
        int deleted = AveragesModel.deleteAllAverages(mContext);
        Log.d("Deleted", deleted + "");

        ArrayList<LocationModel> allLocations = LocationModel.getAllLocations(mContext);
        String date = "";
        int bottom = 0;
        String day = "";
        double lastLat = 0, lastLon = 0;
        double latAvg = 0, lonAvg = 0;
        int count = 0;

        for(LocationModel location : allLocations) {

            Log.d("Testing Service", location.toString());

            if(!date.equals(location.getDate())) {
                if(!day.isEmpty()) {
                    if(count != 0) {
                        lastLat = latAvg/count;
                        lastLon = lonAvg/count;
                    }
                    AveragesModel avg = new AveragesModel(day, bottom, bottom + 14, lastLat, lastLon, count, date);
                    avg.insertAverage(mContext);
                }

                date = location.getDate();
                latAvg = 0;
                lonAvg = 0;
                count = 0;
                day = location.getDay();
                bottom = location.getBottom();
            }
            if(bottom != location.getBottom()) {
                if(count != 0) {
                    lastLat = latAvg/count;
                    lastLon = lonAvg/count;
                }
                AveragesModel avg = new AveragesModel(day, bottom, bottom + 14, lastLat, lastLon, count, date);
                avg.insertAverage(mContext);

                bottom = location.getBottom();
                latAvg = 0;
                lonAvg = 0;
                count = 0;
            }
            count++;
            latAvg += Double.parseDouble(location.getLatitude());
            lonAvg += Double.parseDouble(location.getLongitude());
        }

        if(latAvg != 0) {
            if(count != 0) {
                lastLat = latAvg/count;
                lastLon = lonAvg/count;
            }
            AveragesModel avg = new AveragesModel(day, bottom, bottom + 14, lastLat, lastLon, count, date);
            avg.insertAverage(mContext);
        }
    }

    public void addToAverages(LocationModel location) {
        ArrayList<AveragesModel> allAverages = AveragesModel.getAveragesByDateHour(mContext, location.getDate(), String.valueOf(location.getBottom()), String.valueOf(location.getTop()));

        if(allAverages.isEmpty()) {
            AveragesModel lastAverage = AveragesModel.getLastAverage(mContext);
            if(lastAverage != null) {
                Log.d("Averages", "LastAverage != null -> " + lastAverage.getStart() + " " + lastAverage.getEnd() + " " + lastAverage.getDate());
                GroupingsFinder.getInstance(mContext).addToGroup(lastAverage);
            } else {
                Log.d("Averages", "LastAverage == null");
            }
            AveragesModel average = new AveragesModel(location.getDay(), location.getBottom(), location.getTop(), Double.parseDouble(location.getLatitude()), Double.parseDouble(location.getLongitude()), 1, location.getDate());
            average.insertAverage(mContext);
        } else {
            for (AveragesModel average : allAverages) {

                Log.d("Averages", "--------- FROM " + average.getStart() + " TO " + average.getEnd() + " ---------" + " COUNT = " + allAverages.size());
                Log.d("Averages", "Average " + average.getLatitude() + ", " + average.getLongitude());
                Log.d("Averages", "Location " + location.getLatitude() + ", " + location.getLongitude());

                double latAverage = ((Double.parseDouble(location.getLatitude()) + average.getLatitude()) / 2);
                double lonAverage = ((Double.parseDouble(location.getLongitude()) + average.getLongitude()) / 2);

                Log.d("Averages", "New Average " + latAverage + ", " + lonAverage + " - COUNT " + average.getCount());
                average.setLatitude(latAverage);
                average.setLongitude(lonAverage);
                average.setCount(average.getCount() + 1);
                average.updateAverage(mContext);
            }
        }
    }

    public ArrayList<AveragesModel> getLastThreeAveragesByDayHour(String day, String hour) {
        ArrayList<AveragesModel> allAverages = AveragesModel.getAveragesLastThree(mContext, day, hour);
        ArrayList<AveragesModel> toReturn = new ArrayList<>();
        for (int i = allAverages.size() - 1; i >= 0; i--) {
            toReturn.add(allAverages.get(i));
        }
        return toReturn;
    }
}
