package com.example.tales.tcc.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tales.tcc.Constants;
import com.example.tales.tcc.DrawerAdapter;
import com.example.tales.tcc.PatternFinder;
import com.example.tales.tcc.R;
import com.example.tales.tcc.db.AveragesModel;
import com.example.tales.tcc.db.LocationModel;
import com.example.tales.tcc.db.PatternsModel;
import com.example.tales.tcc.db.UserLocModel;
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
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by tales on 08/06/2017.
 */

public class DrawerActivity extends FragmentActivity implements OnMapReadyCallback {
    private ListView mDrawerList;
    private DrawerAdapter mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;
    private GoogleMap mMap;
    ArrayList<String> array = new ArrayList<>();
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_activity);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        init();
    }

    private void init() {
        getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE).edit().putBoolean(Constants.logged, true).apply();
        getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE).edit().putString(Constants.type, Constants.parent).apply();
        /*String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child(Constants.users).child(id).child(Constants.firebase_token).setValue(FirebaseInstanceId.getInstance().getToken());
        System.out.println(FirebaseInstanceId.getInstance().getToken() + " --------------");*/

        mDrawerList = (ListView)findViewById(R.id.navList);
        addDrawerItems();

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        ImageView refresh = (ImageView)findViewById(R.id.iv_refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

                DatabaseReference  database = FirebaseDatabase.getInstance().getReference();
                database.child(Constants.users).child(id).child(Constants.pattern).setValue(Math.random());
                mapFragment.getMapAsync(DrawerActivity.this);
            }
        });
    }

    private void addDrawerItems() {
        if(LocationService.en) {
            array.add("Enable inspection");
        } else {
            array.add("Disable inspection");
        }
        array.add("Block Device");
        array.add("Insert Pattern");
        array.add("Wipe Device");
        array.add("Logout");
        mAdapter = new DrawerAdapter(this, array);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        LocationService.en = !LocationService.en;
                        if(!LocationService.en) {
                            ((TextView)view.findViewById(R.id.tv_cell)).setText("Enable inspection");
                            new SweetAlertDialog(DrawerActivity.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Enable inspection?")
                                    .setContentText("Once done, you will receive updates from this child again!")
                                    .setConfirmText("Enable!")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog
                                                    .setTitleText("Done!")
                                                    .setContentText("Inspections enabled")
                                                    .setConfirmText("OK")
                                                    .setConfirmClickListener(null)
                                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                        }
                                    })
                                    .show();
                        } else {
                            ((TextView)view.findViewById(R.id.tv_cell)).setText("Disable inspection");
                            new SweetAlertDialog(DrawerActivity.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Disable inspection?")
                                    .setContentText("Once done, you will no longer receive updates from this child!")
                                    .setConfirmText("Disable!")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog
                                                    .setTitleText("Done!")
                                                    .setContentText("Inspections disabled")
                                                    .setConfirmText("OK")
                                                    .setConfirmClickListener(null)
                                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                        }
                                    })
                                    .show();
                        }
                        break;
                    case 1:
                        new SweetAlertDialog(DrawerActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Block device")
                                .setContentText("Block the child device with a password")
                                .setConfirmText("Block it!")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog
                                                .setTitleText("Done!")
                                                .setContentText("Device blocked!")
                                                .setConfirmText("OK")
                                                .setConfirmClickListener(null)
                                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                    }
                                })
                                .show();
                        break;
                    case 2:
                        new SweetAlertDialog(DrawerActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Insert Pattern")
                                .setContentText("Force a new pattern into the child's routine")
                                .setConfirmText("Insert it!")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog
                                                .setTitleText("Done!")
                                                .setContentText("Pattern inserted!")
                                                .setConfirmText("OK")
                                                .setConfirmClickListener(null)
                                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                    }
                                })
                                .show();
                        break;
                    case 3:
                        new SweetAlertDialog(DrawerActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Wipe device?")
                                .setContentText("Are you sure you want to wipe out the device? Once done, all not synced data will be lost forever")
                                .setConfirmText("Wipe it!")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog
                                                .setTitleText("Done!")
                                                .setContentText("The selected device has been successfully wiped")
                                                .setConfirmText("OK")
                                                .setConfirmClickListener(null)
                                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                    }
                                })
                                .show();
                        break;
                    case 4:
                        new SweetAlertDialog(DrawerActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Are you sure?")
                                .setConfirmText("Logout!")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        Intent newIntent = new Intent(DrawerActivity.this,LoginActivity.class);
                                        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        newIntent.putExtra("LOGOUT", true);
                                        DrawerActivity.this.startActivity(newIntent);
                                    }
                                })
                                .show();
                        break;
                }
            }
        });
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
        AveragesModel lastOne = AveragesModel.getLastAverage(DrawerActivity.this);
        if(lastOne != null) {
            LatLng pos = new LatLng(lastOne.getLatitude(), lastOne.getLongitude());

            mMap.addMarker(new MarkerOptions().position(pos).title("Last location").icon(vectorToBitmap(R.drawable.ic_person_pin_circle_black, Color.parseColor("#FF7755"))));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(13.5f));
        }
        final String[] stamp = LocationService.parseTimeStamp();
        final int bottom = (Integer.parseInt(stamp[2]) / 15) * 15;

        final ArrayList<PatternsModel> patterns = PatternFinder.getInstance(DrawerActivity.this).findPattern(stamp[3], bottom);

        if (!patterns.isEmpty()) {
            for (PatternsModel pattern : patterns) {
                LatLng pat = new LatLng(Double.parseDouble(pattern.getLatitude()), Double.parseDouble(pattern.getLongitude()));
                mMap.addMarker(new MarkerOptions().position(pat).title("Pattern").icon(vectorToBitmap(R.drawable.ic_locale, Color.parseColor("#FF0000"))));
            }
        }

        final ArrayList<UserLocModel> userLocs = UserLocModel.getAllLocations(DrawerActivity.this);
        if (!userLocs.isEmpty()) {
            for (UserLocModel userLoc : userLocs) {
                LatLng pat = new LatLng(Double.parseDouble(userLoc.mLatitude), Double.parseDouble(userLoc.mLongitude));
                mMap.addMarker(new MarkerOptions().position(pat).title(userLoc.mName).icon(vectorToBitmap(R.drawable.ic_locale, Color.parseColor("#FF8888"))));
            }
        }

        final ArrayList<AveragesModel> averagesModels = AveragesModel.getAveragesByWeekdayHour(DrawerActivity.this, stamp[3], String.valueOf(bottom));
        if (!averagesModels.isEmpty()) {
            for (AveragesModel avg : averagesModels) {
                LatLng pat = new LatLng(avg.getLatitude(), avg.getLongitude());
                mMap.addMarker(new MarkerOptions().position(pat).title("Average").icon(vectorToBitmap(R.drawable.ic_locale, Color.parseColor("#0000FF"))));
            }
        }



        final ArrayList<LocationModel> locations = LocationModel.getLocationsByWeekdayHourRange(DrawerActivity.this, stamp[3], String.valueOf(bottom), String.valueOf(bottom));
        for (LocationModel avg : locations) {
            Log.d("Testing here", avg.toString());
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
}
