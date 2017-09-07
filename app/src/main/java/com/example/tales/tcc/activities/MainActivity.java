package com.example.tales.tcc.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.tales.tcc.AveragesFinder;
import com.example.tales.tcc.Constants;
import com.example.tales.tcc.CustomAdapter;
import com.example.tales.tcc.CustomDialog;
import com.example.tales.tcc.GroupingsFinder;
import com.example.tales.tcc.PatternFinder;
import com.example.tales.tcc.R;
import com.example.tales.tcc.db.AveragesModel;
import com.example.tales.tcc.db.GroupingModel;
import com.example.tales.tcc.db.LocationModel;
import com.example.tales.tcc.db.PatternsModel;
import com.example.tales.tcc.db.UserModel;
import com.example.tales.tcc.receivers.AdminReceiver;
import com.example.tales.tcc.services.LocationService;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {
    private static MainActivity instance = null;
    static final int RESULT_ENABLE = 1;
    DevicePolicyManager deviceManger;
    ActivityManager activityManager;
    ComponentName compName;

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

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
}
