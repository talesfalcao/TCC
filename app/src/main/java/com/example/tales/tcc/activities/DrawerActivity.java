package com.example.tales.tcc.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tales.tcc.db.UserSetPatternModel;
import com.example.tales.tcc.dialogs.ChooseDialog;
import com.example.tales.tcc.Constants;
import com.example.tales.tcc.DrawerAdapter;
import com.example.tales.tcc.PatternFinder;
import com.example.tales.tcc.R;
import com.example.tales.tcc.db.AveragesModel;
import com.example.tales.tcc.db.PatternsModel;
import com.example.tales.tcc.db.UserLocModel;
import com.example.tales.tcc.dialogs.TimeDialog;
import com.example.tales.tcc.services.LocationService;
import com.facebook.stetho.server.http.HttpStatus;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by tales on 08/06/2017.
 */

public class DrawerActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    AlertDialog alertDialog;
    private ListView mDrawerList;
    private DrawerAdapter mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;
    private GoogleMap mMap;
    ArrayList<String> array = new ArrayList<>();
    SupportMapFragment mapFragment;
    public ArrayList<UserLocModel> selected = new ArrayList<>();
    public static DrawerActivity instance = null;
    private LatLng lastLatLng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_activity);
        instance = this;
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (getIntent().getBooleanExtra("OUTSIDE", false)) {
                Toast.makeText(this, "OUTSIDE", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("Drawer Activity", "No intent");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.getBooleanExtra("OUTSIDE", false)) {
            Toast.makeText(this, "OUTSIDE", Toast.LENGTH_SHORT).show();
        }
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
        array.add("Disable inspection");
        array.add("Block Device");
        array.add("Insert Pattern");
        array.add("Share family code");
        array.add("Logout");

        mAdapter = new DrawerAdapter(this, array);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        ChooseDialog choose1 = new ChooseDialog(DrawerActivity.this);
                        choose1.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                new SweetAlertDialog(DrawerActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Are you sure?")
                                        .setContentText("Enable and disable inspections based on the selections made?")
                                        .setConfirmText("Do it!")
                                        .setCancelText("Not now")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                sDialog.dismiss();
                                                disableInspection();
                                            }
                                        })
                                        .show();


                            }
                        });
                        choose1.show();

                        break;
                    case 1:
                        ChooseDialog choose = new ChooseDialog(DrawerActivity.this);
                        choose.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                if(!selected.isEmpty()) {
                                    LayoutInflater li = LayoutInflater.from(DrawerActivity.this);
                                    View promptsView = li.inflate(R.layout.new_password, null);

                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DrawerActivity.this);

                                    alertDialogBuilder.setView(promptsView);

                                    final EditText userEt = (EditText) promptsView.findViewById(R.id.et_pw);
                                    final TextView register = (TextView) promptsView.findViewById(R.id.register_button);

                                    register.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            lockPw(userEt.getText().toString().trim());
                                        }
                                    });

                                    alertDialogBuilder.setCancelable(true);
                                    alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();
                                } else {
                                    new SweetAlertDialog(DrawerActivity.this, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText("Whoops!")
                                            .setContentText("Select at least one child")
                                            .setConfirmText("OK")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    sDialog.dismiss();
                                                }
                                            })
                                            .show();
                                }
                            }
                        });
                        choose.show();
                        break;
                    case 2:
                        new SweetAlertDialog(DrawerActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Insert Pattern")
                                .setContentText("In order to force a new pattern, perform a long click on the map where you want it to take place. To remove it, click on it and follow the instructions!")
                                .setConfirmText("Gotcha!")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismiss();
                                    }
                                })
                                .show();
                        break;
                    case 3:
                        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFS, Context.MODE_PRIVATE);
                        LayoutInflater li2 = LayoutInflater.from(DrawerActivity.this);
                        View promptsView2 = li2.inflate(R.layout.qrdialog_layout, null);

                        AlertDialog.Builder alertDialogBuilder2 = new AlertDialog.Builder(DrawerActivity.this);

                        alertDialogBuilder2.setView(promptsView2);

                        Typeface regularFont = Typeface.createFromAsset(getAssets(), "Quicksand-Regular.otf");
                        Typeface bold = Typeface.createFromAsset(getAssets(), "Quicksand-Bold.otf");

                        ImageView qr = (ImageView) promptsView2.findViewById(R.id.qr);
                        TextView title = (TextView) promptsView2.findViewById(R.id.qr_title);
                        TextView done = (TextView) promptsView2.findViewById(R.id.done_button);
                        title.setTypeface(regularFont);
                        done.setTypeface(bold);

                        done.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                            }
                        });

                        Log.d("AAAAAAAAAAAAAAAAAA", "ASDaSD " + sharedPreferences.getString(Constants.family, ""));

                        try {
                            qr.setImageBitmap(encodeAsBitmap(sharedPreferences.getString(Constants.family, "")));
                        } catch (WriterException e) {
                            e.printStackTrace();
                        }

                        alertDialogBuilder2.setCancelable(true);
                        alertDialog = alertDialogBuilder2.create();
                        alertDialog.show();
                        break;
                    case 4:
                        new SweetAlertDialog(DrawerActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Are you sure?")
                                .setConfirmText("Logout!")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        Intent newIntent = new Intent(DrawerActivity.this, LoginActivity.class);
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

    private void disableInspection() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.family, Context.MODE_PRIVATE);
        String family = sharedPreferences.getString(Constants.family, "");
        if(!family.isEmpty()) {
            ArrayList<UserLocModel> array = UserLocModel.getAllLocations(this);
            ArrayList<String> allIds = new ArrayList<>();
            ArrayList<String> selectedIds = new ArrayList<>();
            for(UserLocModel user : array) {
                allIds.add(user.id);
            }
            for(UserLocModel u : selected) {
                selectedIds.add(u.id);
            }

            for(String u : allIds) {
                database.child(Constants.family).child(family).child(Constants.child).child(u).child(Constants.disable).setValue(selectedIds.contains(u));
            }

            new SweetAlertDialog(DrawerActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Done!")
                    .setContentText("Inspections preferences successfully changed")
                    .setConfirmText("OK")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismiss();
                        }
                    })
                    .show();
        } else {
            new SweetAlertDialog(DrawerActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Whoops!")
                    .setContentText("Unexpected error")
                    .setConfirmText("OK!")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismiss();
                        }
                    })
                    .show();
        }
        selected.clear();
    }

    private void lockPw(String pw) {
        if(pw.length() < 4 && !pw.isEmpty()) {
            new SweetAlertDialog(DrawerActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Whoops!")
                    .setContentText("Passwords must have 4 or more characters")
                    .setConfirmText("OK!")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismiss();
                        }
                    })
                    .show();
        } else {
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            SharedPreferences sharedPreferences = getSharedPreferences(Constants.family, Context.MODE_PRIVATE);
            String family = sharedPreferences.getString(Constants.family, "");
            if(!family.isEmpty()) {
                for(UserLocModel user : selected) {
                    database.child(Constants.family).child(family).child(Constants.child).child(user.id).child(Constants.password).setValue(pw);
                }

                new SweetAlertDialog(DrawerActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Done!")
                        .setContentText(pw + " has been successfully set")
                        .setConfirmText("OK")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                                alertDialog.dismiss();
                            }
                        })
                        .show();
            } else {
                new SweetAlertDialog(DrawerActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Whoops!")
                        .setContentText("Unexpected error")
                        .setConfirmText("OK!")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                            }
                        })
                        .show();
            }
        }
        selected.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
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
        mMap.setOnMarkerClickListener(this);
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
                mMap.addMarker(new MarkerOptions().position(pat).title("User-set Pattern").icon(vectorToBitmap(R.drawable.ic_locale, Color.parseColor("#AA00AA"))).draggable(false));
            }
        } else {

            final ArrayList<PatternsModel> patterns = PatternsModel.getPattern(this, stamp[3], String.valueOf(bottom));
            if (!patterns.isEmpty()) {
                for (PatternsModel pattern : patterns) {
                    LatLng pat = new LatLng(Double.parseDouble(pattern.getLatitude()), Double.parseDouble(pattern.getLongitude()));
                    mMap.addMarker(new MarkerOptions().position(pat).title("Pattern").icon(vectorToBitmap(R.drawable.ic_locale, Color.parseColor("#4433AA"))).draggable(false));
                }
            }
        }

        final ArrayList<UserLocModel> userLocs = UserLocModel.getAllLocations(DrawerActivity.this);
        if (!userLocs.isEmpty()) {
            for (UserLocModel userLoc : userLocs) {
                LatLng pat = new LatLng(Double.parseDouble(userLoc.mLatitude), Double.parseDouble(userLoc.mLongitude));

                mMap.addMarker(new MarkerOptions().position(pat).title(userLoc.mName).icon(vectorToBitmap(R.drawable.ic_locale, Color.parseColor("#FF8888"))).draggable(false));
            }
        }

        /*final ArrayList<AveragesModel> averagesModels = AveragesModel.getAveragesByWeekdayHour(DrawerActivity.this, stamp[3], String.valueOf(bottom));
        if (!averagesModels.isEmpty()) {
            for (AveragesModel avg : averagesModels) {
                LatLng pat = new LatLng(avg.getLatitude(), avg.getLongitude());
                mMap.addMarker(new MarkerOptions().position(pat).title("Average").icon(vectorToBitmap(R.drawable.ic_locale, Color.parseColor("#0000FF"))));
            }
        }*/

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                lastLatLng = latLng;
                TimeDialog picker = new TimeDialog(DrawerActivity.this);
                picker.show();
            }
        });
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

    public void setPatternDateTime(final String day, final String starthours, final String startminutes, final String endhours, final String endminutes) {
        ChooseDialog choose = new ChooseDialog(DrawerActivity.this);
        choose.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(!selected.isEmpty()) {
                        Log.d(day, starthours + ":" + startminutes + " TO " + endhours + ":" + endminutes);
                        Log.d(lastLatLng.latitude + "", lastLatLng.longitude + "");

                        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                        SharedPreferences sharedPreferences = getSharedPreferences(Constants.family, Context.MODE_PRIVATE);
                        final String family = sharedPreferences.getString(Constants.family, "");
                        if(!family.isEmpty()) {
                            for(final UserLocModel user : selected) {
                                int start = Integer.valueOf(starthours) * 60;
                                start += Integer.valueOf(startminutes);
                                int end = Integer.valueOf(endhours) * 60;
                                end += Integer.valueOf(endminutes);
                                String save = "USR=" + user.id + "LAT=" + lastLatLng.latitude + "LONG=" + lastLatLng.longitude + "DAY=" + day + "START=" + start + "END=" + end;
                                database.child(Constants.family).child(family).child(Constants.child).child(user.id).child(Constants.setPattern).setValue(save);
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        database.child(Constants.family).child(family).child(Constants.child).child(user.id).child(Constants.setPattern).removeValue();                                    }
                                }, 100);
                            }

                            new SweetAlertDialog(DrawerActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Done!")
                                    .setContentText("Pattern has been successfully set")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismiss();
                                        }
                                    })
                                    .show();
                        } else {
                            new SweetAlertDialog(DrawerActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Whoops!")
                                    .setContentText("Unexpected error")
                                    .setConfirmText("OK!")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismiss();
                                        }
                                    })
                                    .show();
                        }
                    } else {
                        new SweetAlertDialog(DrawerActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Whoops!")
                                .setContentText("Select at least one child")
                                .setConfirmText("OK")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismiss();
                                    }
                                })
                                .show();
                    }
                selected.clear();
            }
        });
        choose.show();

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        if (marker.getTitle().equals("User-set Pattern")) {
            new SweetAlertDialog(DrawerActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Delete this pattern?")
                    .setContentText("Since this is one of the patterns created by you (or other guardian), you can select to remove it from the child device!")
                    .setConfirmText("Remove it!")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            final String[] stamp = LocationService.parseTimeStamp();

                            final ArrayList<UserSetPatternModel> userSet = UserSetPatternModel.getLocationsDay(DrawerActivity.this, stamp[3]);
                            for(final UserSetPatternModel m : userSet) {

                                if(Double.parseDouble(m.mLatitude) == marker.getPosition().latitude && Double.parseDouble(m.mLongitude) == marker.getPosition().longitude) {
                                    final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                                    SharedPreferences sharedPreferences = getSharedPreferences(Constants.family, Context.MODE_PRIVATE);
                                    final String family = sharedPreferences.getString(Constants.family, "");
                                    if(!family.isEmpty()) {
                                        String remove = "USR=" + m.mUser + "LAT=" + marker.getPosition().latitude + "LONG=" + marker.getPosition().longitude + "DAY=" + stamp[3] + "START=" + m.mStart + "END=" + m.mEnd;
                                        database.child(Constants.family).child(family).child(Constants.child).child(m.mUser).child(Constants.removePattern).setValue(remove);
                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                database.child(Constants.family).child(family).child(Constants.child).child(m.mUser).child(Constants.removePattern).removeValue();                         }
                                        }, 600);

                                    }
                                }
                            }
                            sDialog.dismiss();
                        }
                    })
                    .setCancelText("Keep it!")
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                        }
                    })
                    .show();

            return true;
        }
        return false;
    }
}