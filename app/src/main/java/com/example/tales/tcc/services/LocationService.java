package com.example.tales.tcc.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.tales.tcc.AveragesFinder;
import com.example.tales.tcc.Constants;
import com.example.tales.tcc.PatternFinder;
import com.example.tales.tcc.activities.MainActivity;
import com.example.tales.tcc.db.LocationModel;
import com.example.tales.tcc.db.PatternsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by tales on 03/05/2017.
 */

public class LocationService extends Service {
    public static final String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    public static boolean en = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init();
        return START_STICKY;
    }

    private void init() {
        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Location currentLocation = getLastBestLocation();

                if(currentLocation != null) {
                    storeData(currentLocation);
                }
            }
        }, 0, 60000);
    }

    private void storeData(Location currentLocation) {
        final String[] stamp = parseTimeStamp();
        final int bottom = (Integer.parseInt(stamp[2]) / 15) * 15;

        final LocationModel model = new LocationModel(stamp[0], String.valueOf(currentLocation.getLatitude()), String.valueOf(currentLocation.getLongitude()), stamp[2], stamp[3], stamp[1], bottom, bottom + 14);

        final ArrayList<PatternsModel> pattern = PatternFinder.getInstance(LocationService.this).findPattern(stamp[3], bottom);
        MainActivity instance = MainActivity.getInstance();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.family, Context.MODE_PRIVATE);
        String result = sharedPreferences.getString(Constants.family, "");
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        boolean inside = false;

        if(instance != null) {
            if (pattern.isEmpty()) {
                //remove all patterns
                database.child(Constants.family).child(result).child(Constants.child).child(id).child(Constants.patterns).removeValue();
                inside = true;
                database.child(Constants.family).child(result).child(Constants.child).child(id).child(Constants.inside).setValue(inside);

                long time = System.currentTimeMillis()/1000;
                database.child(Constants.family).child(result).child(Constants.child).child(id).child(Constants.last_update).setValue(time);
                database.child(Constants.family).child(result).child(Constants.child).child(id).child(Constants.current_latitude).setValue(model.getLatitude());
                database.child(Constants.family).child(result).child(Constants.child).child(id).child(Constants.current_longitude).setValue(model.getLongitude());
            } else if (pattern.size() == 1) {
                final Location a = new Location("");
                final Location b = new Location("");
                a.setLatitude(Double.parseDouble(pattern.get(0).getLatitude()));
                a.setLongitude(Double.parseDouble(pattern.get(0).getLongitude()));
                b.setLatitude(Double.parseDouble(model.getLatitude()));
                b.setLongitude(Double.parseDouble(model.getLongitude()));
                String text;
                if(a.distanceTo(b) > 300) {
                    text = "Out of pattern by " + a.distanceTo(b) + " meters";
                } else {
                    inside = true;
                    text = "Pattern ok";
                }
                final String textAux = text;
                MainActivity.getInstance().runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(LocationService.this, textAux, Toast.LENGTH_SHORT).show();
                    }
                });

                database.child(Constants.family).child(result).child(Constants.child).child(id).child(Constants.patterns).removeValue();
                database.child(Constants.family).child(result).child(Constants.child).child(id).child(Constants.patterns).child("1").child(Constants.latitude).setValue(a.getLatitude());
                database.child(Constants.family).child(result).child(Constants.child).child(id).child(Constants.patterns).child("1").child(Constants.longitude).setValue(a.getLongitude());
                database.child(Constants.family).child(result).child(Constants.child).child(id).child(Constants.inside).setValue(inside);
                long time = System.currentTimeMillis()/1000;
                database.child(Constants.family).child(result).child(Constants.child).child(id).child(Constants.last_update).setValue(time);
                database.child(Constants.family).child(result).child(Constants.child).child(id).child(Constants.current_latitude).setValue(model.getLatitude());
                database.child(Constants.family).child(result).child(Constants.child).child(id).child(Constants.current_longitude).setValue(model.getLongitude());
            } else {
                MainActivity.getInstance().runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(LocationService.this, "More than one pattern registered for " + stamp[3] + " from " + bottom + " to " + (bottom + 14), Toast.LENGTH_SHORT).show();
                    }
                });

                int i = 1;
                for(PatternsModel p : pattern) {
                    database.child(Constants.family).child(result).child(Constants.child).child(id).child(Constants.patterns).removeValue();
                    database.child(Constants.family).child(result).child(Constants.child).child(id).child(Constants.patterns).child(i + "").child(Constants.latitude).setValue(p.getLatitude());
                    database.child(Constants.family).child(result).child(Constants.child).child(id).child(Constants.patterns).child(i + "").child(Constants.longitude).setValue(p.getLongitude());
                    i++;

                    final Location a = new Location("");
                    final Location b = new Location("");
                    a.setLatitude(Double.parseDouble(p.getLatitude()));
                    a.setLongitude(Double.parseDouble(p.getLongitude()));
                    b.setLatitude(Double.parseDouble(model.getLatitude()));
                    b.setLongitude(Double.parseDouble(model.getLongitude()));
                    if(!inside) {
                        if (a.distanceTo(b) <= 300) {
                            inside = true;
                        }
                    }
                }

                database.child(Constants.family).child(result).child(Constants.child).child(id).child(Constants.inside).setValue(inside);
                long time = System.currentTimeMillis()/1000;
                database.child(Constants.family).child(result).child(Constants.child).child(id).child(Constants.last_update).setValue(time);
                database.child(Constants.family).child(result).child(Constants.child).child(id).child(Constants.current_latitude).setValue(model.getLatitude());
                database.child(Constants.family).child(result).child(Constants.child).child(id).child(Constants.current_longitude).setValue(model.getLongitude());
            }
        }
        Log.d("Testing Service", model.toString());
        model.insertLocation(LocationService.this);
        AveragesFinder.getInstance(this).addToAverages(model);
    }

    private Location getLastBestLocation() {
        LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        Location locationGPS = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) {
            GPSLocationTime = locationGPS.getTime();
        }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if ( 0 < GPSLocationTime - NetLocationTime ) {
            return locationGPS;
        } else {
            return locationNet;
        }
    }

    public static String[] parseTimeStamp() {
        String[] stamp = new String[4];
        Long tsLong = System.currentTimeMillis();
        Date d = new Date(tsLong);
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int date = c.get(Calendar.DATE);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int dayNum = c.get(Calendar.DAY_OF_WEEK);

        stamp[0] = String.valueOf(tsLong);
        stamp[1] = date + "/" + month + "/" + year;

        stamp[2] = String.valueOf(minute + (hour * 60));
        stamp[3] = days[dayNum-1];
        return stamp;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}