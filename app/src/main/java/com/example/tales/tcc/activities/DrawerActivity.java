package com.example.tales.tcc.activities;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.tales.tcc.CustomDialog;
import com.example.tales.tcc.DrawerAdapter;
import com.example.tales.tcc.PatternFinder;
import com.example.tales.tcc.R;
import com.example.tales.tcc.db.AveragesModel;
import com.example.tales.tcc.db.PatternsModel;
import com.example.tales.tcc.services.LocationService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_activity);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        init();
    }

    private void init() {
        mDrawerList = (ListView)findViewById(R.id.navList);
        addDrawerItems();

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CustomDialog fragment1 = new CustomDialog();
                Bundle args = new Bundle();
                args.putString("title", array.get(position));
                fragment1.setArguments(args);
                fragment1.show(getSupportFragmentManager(), "tag");
            }
        });

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
    }

    private void addDrawerItems() {

        array.add("Disable inspection");
        array.add("Option 2");
        array.add("Option 3");
        array.add("Option 4");
        array.add("Logout");
        mAdapter = new DrawerAdapter(this, array);
        mDrawerList.setAdapter(mAdapter);
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

        // Add a marker in Sydney and move the camera
        AveragesModel lastOne = AveragesModel.getLastAverage(DrawerActivity.this);
        LatLng pos = new LatLng(lastOne.getLatitude(), lastOne.getLongitude());
        mMap.addMarker(new MarkerOptions().position(pos).title("Last location").icon(vectorToBitmap(R.drawable.ic_person_pin_circle_black, Color.parseColor("#FF7755"))));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 13.5f ) );

        final String[] stamp = LocationService.parseTimeStamp();
        final int bottom = (Integer.parseInt(stamp[2]) / 15) * 15;

        final ArrayList<PatternsModel> patterns = PatternFinder.getInstance(DrawerActivity.this).findPattern(stamp[3], bottom);

        if (!patterns.isEmpty()) {
            for (PatternsModel pattern : patterns) {
                LatLng pat = new LatLng(Double.parseDouble(pattern.getLatitude()), Double.parseDouble(pattern.getLongitude()));
                mMap.addMarker(new MarkerOptions().position(pat).title("Pattern").icon(vectorToBitmap(R.drawable.ic_locale, Color.parseColor("#FF7755"))));
            }
        }

        final ArrayList<AveragesModel> averagesModels = AveragesModel.getAveragesByWeekdayHour(DrawerActivity.this, stamp[3], String.valueOf(bottom));
        if (!patterns.isEmpty()) {
            for (AveragesModel avg : averagesModels) {
                LatLng pat = new LatLng(avg.getLatitude(), avg.getLongitude());
                mMap.addMarker(new MarkerOptions().position(pat).title("Pattern").icon(vectorToBitmap(R.drawable.ic_poop_icon, Color.parseColor("#8B4513"))));
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
}
