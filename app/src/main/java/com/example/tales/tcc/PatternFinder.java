package com.example.tales.tcc;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.example.tales.tcc.activities.MainActivity;
import com.example.tales.tcc.db.AveragesModel;
import com.example.tales.tcc.db.GroupingModel;
import com.example.tales.tcc.db.LocationModel;
import com.example.tales.tcc.db.PatternsModel;
import com.example.tales.tcc.db.UserSetPatternModel;
import com.example.tales.tcc.services.LocationService;

import java.util.ArrayList;

/**
 * Created by tales on 10/05/2017.
 */

public class PatternFinder {
    private Context mContext;
    private static PatternFinder instance = null;

    private PatternFinder(Context c) {
        mContext = c;
    }

    public static PatternFinder getInstance(Context c) {
        if (instance == null) {
            return new PatternFinder(c);
        }
        return instance;
    }

    public void findPatterns() {
        Log.d("PatternFinder", "Deleted: " + PatternsModel.deleteAll(mContext));

        //EveryDay ->
        for (int i = 0; i < 7; i++) {
            //Every quarter hour
            int bottom;
            for (int j = 0; j <= 94; j++) {
                boolean inserted = false;
                bottom = j * 15;
                ArrayList<AveragesModel> lastThreeAveragesByDayHour = AveragesFinder.getInstance(mContext).getLastThreeAveragesByDayHour(LocationService.days[i], String.valueOf(bottom));
                ArrayList<Location> threeLocations = new ArrayList<>();
                for (AveragesModel model : lastThreeAveragesByDayHour) {
                    Location location = new Location("");
                    location.setLatitude(model.getLatitude());
                    location.setLongitude(model.getLongitude());
                    threeLocations.add(location);
                }
                if (threeLocations.size() >= 3) {
                    if (threeLocations.get(0).distanceTo(threeLocations.get(1)) <= 150 &&
                            threeLocations.get(1).distanceTo(threeLocations.get(2)) <= 150 &&
                            threeLocations.get(0).distanceTo(threeLocations.get(2)) <= 150) {
                        Double avgLat = (threeLocations.get(0).getLatitude() + threeLocations.get(1).getLatitude() + threeLocations.get(2).getLatitude()) / 3;
                        Double avgLon = (threeLocations.get(0).getLongitude() + threeLocations.get(1).getLongitude() + threeLocations.get(2).getLongitude()) / 3;
                        PatternsModel pattern = new PatternsModel(LocationService.days[i], String.valueOf(bottom), String.valueOf(bottom + 14), String.valueOf(avgLat), String.valueOf(avgLon));
                        pattern.insertPattern(mContext);
                        inserted = true;
                    }
                }

                if (!inserted) {
                    ArrayList<GroupingModel> allModels = GroupingModel.getGroupingsByWeekdayHour(mContext, LocationService.days[i], String.valueOf(bottom));
                    ArrayList<GroupingModel> possiblePatterns = new ArrayList<>();
                    for (GroupingModel model : allModels) {
                        if (possiblePatterns.size() > 0 && possiblePatterns.get(0) != null) {
                            GroupingModel grouping = possiblePatterns.get(0);
                            if (grouping.getCount() < model.getCount()) {
                                possiblePatterns.clear();
                                possiblePatterns.add(model);
                            } else if (grouping.getCount() == model.getCount()) {
                                possiblePatterns.add(model);
                            }
                        } else {
                            possiblePatterns.add(model);
                        }
                    }
                    if (!possiblePatterns.isEmpty()) {
                        for (GroupingModel gm : possiblePatterns) {
                            PatternsModel pattern = new PatternsModel(LocationService.days[i], String.valueOf(bottom), String.valueOf(bottom + 14), String.valueOf(gm.getLatitude()), String.valueOf(gm.getLongitude()));
                            pattern.insertPattern(mContext);
                        }
                    }
                }
            }
        }
    }

    public ArrayList<PatternsModel> findPattern(String day, int hour) {
        boolean inserted = false;
        ArrayList<PatternsModel> array = new ArrayList<>();
        int bottom = ((int) hour / 15) * 15;

        ArrayList<UserSetPatternModel> userSet = UserSetPatternModel.getLocationsDay(mContext, day);
        if(!userSet.isEmpty()) {
            ArrayList<PatternsModel> ret = new ArrayList<>();
            for(UserSetPatternModel u : userSet) {
                if(Integer.parseInt(u.mStart) <= bottom && Integer.parseInt(u.mEnd) > (bottom + 15)) {
                    PatternsModel pat = new PatternsModel(day, String.valueOf(bottom), String.valueOf(bottom + 14), u.mLatitude, u.mLongitude);
                    ret.add(pat);
                }
            }
            if(!ret.isEmpty()) {
                return ret;
            }
        }

        ArrayList<AveragesModel> lastThreeAveragesByDayHour = AveragesFinder.getInstance(mContext).getLastThreeAveragesByDayHour(day, String.valueOf(bottom));
        ArrayList<Location> threeLocations = new ArrayList<>();
        for (AveragesModel model : lastThreeAveragesByDayHour) {
            Location location = new Location("");
            location.setLatitude(model.getLatitude());
            location.setLongitude(model.getLongitude());
            threeLocations.add(location);
        }
        if (threeLocations.size() >= 3) {
            if (threeLocations.get(0).distanceTo(threeLocations.get(1)) <= 300 &&
                    threeLocations.get(1).distanceTo(threeLocations.get(2)) <= 300 &&
                    threeLocations.get(0).distanceTo(threeLocations.get(2)) <= 300) {
                Double avgLat = (threeLocations.get(0).getLatitude() + threeLocations.get(1).getLatitude() + threeLocations.get(2).getLatitude()) / 3;
                Double avgLon = (threeLocations.get(0).getLongitude() + threeLocations.get(1).getLongitude() + threeLocations.get(2).getLongitude()) / 3;
                PatternsModel pattern = new PatternsModel(day, String.valueOf(bottom), String.valueOf(bottom + 14), String.valueOf(avgLat), String.valueOf(avgLon));
                array.add(pattern);
                inserted = true;
            }
        }

        if (!inserted) {
            ArrayList<GroupingModel> allModels = GroupingModel.getGroupingsByWeekdayHour(mContext, day, String.valueOf(bottom));
            ArrayList<GroupingModel> possiblePatterns = new ArrayList<>();
            for (GroupingModel model : allModels) {
                if (possiblePatterns.size() > 0 && possiblePatterns.get(0) != null) {
                    GroupingModel grouping = possiblePatterns.get(0);
                    if (grouping.getCount() < model.getCount()) {
                        possiblePatterns.clear();
                        possiblePatterns.add(model);
                    } else if (grouping.getCount() == model.getCount()) {
                        possiblePatterns.add(model);
                    }
                } else {
                    possiblePatterns.add(model);
                }
            }
            if (!possiblePatterns.isEmpty()) {
                for (GroupingModel gm : possiblePatterns) {
                    PatternsModel pattern = new PatternsModel(day, String.valueOf(bottom), String.valueOf(bottom + 14), String.valueOf(gm.getLatitude()), String.valueOf(gm.getLongitude()));
                    array.add(pattern);
                    //pattern.insertPattern(mContext);
                }
            }
        }

        if(!array.isEmpty()) {
            return clusterPatters(array);
        }
        return array;
    }

    private ArrayList<PatternsModel> clusterPatters(ArrayList<PatternsModel> array) {
        ArrayList<PatternsModel> clusters = new ArrayList<>();
        PatternsModel last = array.get(0);
        clusters.add(last);

        for(PatternsModel p : array) {
            final Location a = new Location("");
            final Location b = new Location("");
            a.setLatitude(Double.parseDouble(last.getLatitude()));
            a.setLongitude(Double.parseDouble(last.getLongitude()));
            b.setLatitude(Double.parseDouble(p.getLatitude()));
            b.setLongitude(Double.parseDouble(p.getLongitude()));

            if(a.distanceTo(b) >= 300) {
                clusters.add(p);
            }

            last = p;
        }

        return clusters;
    }
}
