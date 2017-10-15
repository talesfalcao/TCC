package com.example.tales.tcc.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Environment;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tales.tcc.Constants;
import com.example.tales.tcc.PatternFinder;
import com.example.tales.tcc.R;
import com.example.tales.tcc.db.AveragesModel;
import com.example.tales.tcc.db.LocationModel;
import com.example.tales.tcc.db.PatternsModel;
import com.example.tales.tcc.db.UserLocModel;
import com.example.tales.tcc.db.UserSetPatternModel;
import com.example.tales.tcc.dialogs.CustomDialog;
import com.example.tales.tcc.dialogs.TimeDialog;
import com.example.tales.tcc.receivers.AdminReceiver;
import com.example.tales.tcc.services.LocationService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity implements CustomDialog.EditNameDialogListener, OnMapReadyCallback {
    private static MainActivity instance = null;
    static final int RESULT_ENABLE = 1;
    DevicePolicyManager deviceManger;
    ActivityManager activityManager;
    ComponentName compName;
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    private boolean logout = false;

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
        getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE).edit().putString(Constants.type, Constants.child).apply();
        getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE).edit().putBoolean(Constants.logged, true).apply();

        deviceManger = (DevicePolicyManager)getSystemService(
                Context.DEVICE_POLICY_SERVICE);
        activityManager = (ActivityManager)getSystemService(
                Context.ACTIVITY_SERVICE);
        compName = new ComponentName(this, AdminReceiver.class);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setPw();

        //test();
        enableAdmin();
        init();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
             ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        } else {
            if(!isMyServiceRunning(LocationService.class)) {
                startService(new Intent(getBaseContext(), LocationService.class));
            }
        }
    }

    private void setPw() {
        logout = false;
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.family, Context.MODE_PRIVATE);
        if(sharedPreferences.getString("PW", "").isEmpty()) {
            CustomDialog fragment1 = new CustomDialog();
            Bundle args = new Bundle();
            args.putString("title", "Please input the desired safety code");
            fragment1.setArguments(args);
            fragment1.show(getSupportFragmentManager(), "tag");
        }
    }

    private void enableAdmin() {
        Intent intent = new Intent(DevicePolicyManager
                .ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                compName);
        startActivityForResult(intent, RESULT_ENABLE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_ENABLE:
                if (resultCode == Activity.RESULT_OK) {
                    Log.i("DeviceAdminSample", "Admin enabled!");
                } else {
                    enableAdmin();
                    Log.i("DeviceAdminSample", "Admin enable FAILED!");
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void test() {
        ArrayList<LocationModel> array = LocationModel.getAllLocations(this);
        for(LocationModel model : array) {
            Log.d("TESTING", model.toString());
            int bottom = (Integer.parseInt(model.getHour()) / 15) * 15;
            int top = bottom + 14;

            model.setBottom(bottom);
            model.setTop(top);

            model.updateLocationBotTop(this);
        }
    }

    private void init() {
        /*final PatternFinder finder = PatternFinder.getInstance(this);
        finder.findPatterns();*/

        //AveragesFinder.getInstance(this).findAverages();

        /*GroupingsFinder finder = GroupingsFinder.getInstance(this);
        GroupingModel.deleteAll(MainActivity.this);
        finder.findGroupings();*/

        ImageView button = (ImageView) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                database.child(Constants.users).child(id).child(Constants.pattern).setValue(Math.random());
                mapFragment.getMapAsync(MainActivity.this);
            }
        });
        ImageView button2 = (ImageView) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout = true;
                CustomDialog fragment1 = new CustomDialog();
                Bundle args = new Bundle();
                args.putString("title", "Please input the safety code to logout");
                fragment1.setArguments(args);
                fragment1.show(getSupportFragmentManager(), "tag");
            }
        });

        /*try {
            backupDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 100: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(!isMyServiceRunning(LocationService.class)) {
                        startService(new Intent(getBaseContext(), LocationService.class));
                    }
                }
            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void backupDatabase() throws IOException {
        try {
            File data = Environment.getDataDirectory();
            File sd = Environment.getExternalStorageDirectory();

            if (sd.canWrite()) {
                String  currentDBPath= "//data//" + getPackageName()
                        + "//databases//" + "database.db";
                String backupDBPath  = "/database.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(getBaseContext(), backupDB.toString(),
                        Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {

            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG)
                    .show();

        }
    }

    @Override
    public void onFinishEditDialog(String inputText) {
        if(!inputText.trim().isEmpty()) {
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.family, Context.MODE_PRIVATE);
            if(logout) {
                if(sharedPreferences.getString("PW", "").equals(inputText)) {
                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Are you sure?")
                            .setConfirmText("Logout!")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                Intent newIntent = new Intent(MainActivity.this,LoginActivity.class);
                                newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                newIntent.putExtra("LOGOUT", true);
                                getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE).edit().putBoolean(Constants.logged, false).apply();
                                MainActivity.this.startActivity(newIntent);
                                }
                            })
                            .show();
                } else {
                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Whoops")
                            .setContentText("Wrong password. Please try again!")
                            .setConfirmText("Ok")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                                }
                            })
                            .show();
                }
            } else {

                sharedPreferences.edit().putString("PW", inputText).apply();
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        Location lastOne = getDeviceLocation();
        if(lastOne != null) {
            LatLng pos = new LatLng(lastOne.getLatitude(), lastOne.getLongitude());

            mMap.addMarker(new MarkerOptions().position(pos).title("You").icon(vectorToBitmap(R.drawable.ic_person_pin_circle_black, Color.parseColor("#FF7755"))));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(13.5f));
        }
        final String[] stamp = LocationService.parseTimeStamp();
        final int bottom = (Integer.parseInt(stamp[2]) / 15) * 15;

        final ArrayList<UserSetPatternModel> userSet = UserSetPatternModel.getLocationsDay(this, stamp[3]);
        ArrayList<UserSetPatternModel> ret = new ArrayList<>();
        if(!userSet.isEmpty()) {
            for(UserSetPatternModel u : userSet) {
                if(Integer.parseInt(u.mStart) <= bottom && Integer.parseInt(u.mEnd) > (bottom + 15)) {
                    ret.add(u);
                }
            }
        }

        if (!ret.isEmpty()) {
            for (UserSetPatternModel pattern : ret) {
                LatLng pat = new LatLng(Double.parseDouble(pattern.mLatitude), Double.parseDouble(pattern.mLongitude));
                mMap.addMarker(new MarkerOptions().position(pat).title("User-set Pattern").icon(vectorToBitmap(R.drawable.ic_locale, Color.parseColor("#AA00AA"))));
            }
        } else {
            final ArrayList<PatternsModel> patterns = PatternFinder.getInstance(this).findPattern(stamp[3], (bottom));
            if (!patterns.isEmpty()) {
                for (PatternsModel pattern : patterns) {
                    LatLng pat = new LatLng(Double.parseDouble(pattern.getLatitude()), Double.parseDouble(pattern.getLongitude()));
                    mMap.addMarker(new MarkerOptions().position(pat).title("Pattern").icon(vectorToBitmap(R.drawable.ic_locale, Color.parseColor("#43AAA2"))));
                }
            }
        }

        final ArrayList<AveragesModel> averagesModels = AveragesModel.getAveragesByWeekdayHour(MainActivity.this, stamp[3], String.valueOf(bottom));
        if (!averagesModels.isEmpty()) {
            for (AveragesModel avg : averagesModels) {
                LatLng pat = new LatLng(avg.getLatitude(), avg.getLongitude());
                mMap.addMarker(new MarkerOptions().position(pat).title("Average").icon(vectorToBitmap(R.drawable.ic_locale, Color.parseColor("#0000FF"))));
            }
        }
    }


    /**
     * Demonstrates converting a {@link Drawable} to a {@link BitmapDescriptor},
     * for use as a marker icon.
     */
    private BitmapDescriptor vectorToBitmap(@DrawableRes int id, @ColorInt int color) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), id, null);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        DrawableCompat.setTint(vectorDrawable, color);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, 300, 300, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? 0xFF000000 : 0xFFFFFFFF;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, 300, 0, 0, w, h);
        return bitmap;
    }

    private Location getDeviceLocation() {
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
}
