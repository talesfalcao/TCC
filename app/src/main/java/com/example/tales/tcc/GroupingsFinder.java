package com.example.tales.tcc;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.example.tales.tcc.db.AveragesModel;
import com.example.tales.tcc.db.GroupingModel;
import com.example.tales.tcc.db.LocationModel;
import com.example.tales.tcc.db.PatternsModel;
import com.example.tales.tcc.services.LocationService;

import java.util.ArrayList;

/**
 * Created by tales on 24/05/2017.
 */

public class GroupingsFinder {
    private Context mContext;
    private static GroupingsFinder instance = null;

    private GroupingsFinder(Context c) {
        mContext = c;
    }

    public static GroupingsFinder getInstance(Context c) {
        if(instance == null) {
            return new GroupingsFinder(c);
        }
        return instance;
    }

    public void findGroupings() {
        //EveryDay ->
        for(int i = 0; i < 7; i++) {
            //Every quarter hour
            int bottom;
            for(int j = 0; j <= 94; j++) {
                bottom = j*15;
                ArrayList<AveragesModel> allModels = AveragesModel.getAveragesByWeekdayHour(mContext, LocationService.days[i], String.valueOf(bottom));

                for (AveragesModel model: allModels) {
                    ArrayList<GroupingModel> groups = GroupingModel.getGroupingsByWeekdayHour(mContext, LocationService.days[i],  String.valueOf(bottom));
                    if(groups.isEmpty()) {
                        Log.d("GroupingsFinder", "Groupings.isEmpty");
                        GroupingModel group = new GroupingModel(model.getStart(), model.getEnd(), model.getLatitude(), model.getLongitude(), 1, model.getDay());
                        group.insertGrouping(mContext);
                    } else {
                        Location modelLocation = new Location("");
                        modelLocation.setLatitude(model.getLatitude());
                        modelLocation.setLongitude(model.getLongitude());
                        Location groupLocation = new Location("");
                        boolean isAdded = false;
                        for(GroupingModel grouping : groups) {
                            groupLocation.setLatitude(grouping.getLatitude());
                            groupLocation.setLongitude(grouping.getLongitude());
                            if(groupLocation.distanceTo(modelLocation) <= 100) {
                                Log.d("GroupingsFinder", grouping.getCount() + "");
                                Log.d("GroupingsFinder", "Add to average " + model.getDay() + " at" + bottom / 60 + ":" + bottom % 60);
                                //Add model to group
                                double lat = ((grouping.getLatitude() * grouping.getCount()) + model.getLatitude()) / (grouping.getCount() + 1);
                                double lon = ((grouping.getLongitude() * grouping.getCount()) + model.getLongitude()) / (grouping.getCount() + 1);
                                grouping.setCount(grouping.getCount() + 1);
                                grouping.setLatitude(lat);
                                grouping.setLongitude(lon);
                                grouping.updateGrouping(mContext);
                                isAdded = true;
                                break;
                            }
                        }
                        if(!isAdded) {
                            Log.d("GroupingsFinder", "!isAdded");
                            GroupingModel group = new GroupingModel(model.getStart(), model.getEnd(), model.getLatitude(), model.getLongitude(), 1, model.getDay());
                            group.insertGrouping(mContext);
                        }
                    }
                }
            }
        }

    }

    public void addToGroup(AveragesModel average) {
        ArrayList<GroupingModel> groups = GroupingModel.getGroupingsByWeekdayHour(mContext, average.getDay(), String.valueOf(average.getStart()));
        if(groups.isEmpty()) {
            Log.d("GroupingsFinder", "Groupings.isEmpty");
            GroupingModel group = new GroupingModel(average.getStart(), average.getEnd(), average.getLatitude(), average.getLongitude(), 1, average.getDay());
            group.insertGrouping(mContext);
        } else {
            Location modelLocation = new Location("");
            modelLocation.setLatitude(average.getLatitude());
            modelLocation.setLongitude(average.getLongitude());
            Location groupLocation = new Location("");
            boolean isAdded = false;
            for (GroupingModel grouping : groups) {
                groupLocation.setLatitude(grouping.getLatitude());
                groupLocation.setLongitude(grouping.getLongitude());
                if (groupLocation.distanceTo(modelLocation) <= 100) {
                    Log.d("GroupingsFinder", grouping.getCount() + "");
                    Log.d("GroupingsFinder", "Add to average " + average.getDay() + " at" + average.getStart() / 60 + ":" + average.getStart() % 60);
                    //Add model to group
                    double lat = ((grouping.getLatitude() * grouping.getCount()) + average.getLatitude()) / (grouping.getCount() + 1);
                    double lon = ((grouping.getLongitude() * grouping.getCount()) + average.getLongitude()) / (grouping.getCount() + 1);
                    grouping.setCount(grouping.getCount() + 1);
                    grouping.setLatitude(lat);
                    grouping.setLongitude(lon);
                    grouping.updateGrouping(mContext);
                    isAdded = true;
                    break;
                }
            }
            if (!isAdded) {
                Log.d("GroupingsFinder", "!isAdded");
                GroupingModel group = new GroupingModel(average.getStart(), average.getEnd(), average.getLatitude(), average.getLongitude(), 1, average.getDay());
                group.insertGrouping(mContext);
            }
        }
    }
}