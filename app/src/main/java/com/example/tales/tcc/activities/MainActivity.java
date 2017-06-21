package com.example.tales.tcc.activities;

import android.Manifest;
import android.app.ActivityManager;
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
import com.example.tales.tcc.services.LocationService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity implements CustomDialog.EditNameDialogListener {
    private static MainActivity instance = null;

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;

        //test();

        init();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
             ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        } else {
            if(!isMyServiceRunning(LocationService.class)) {
                startService(new Intent(getBaseContext(), LocationService.class));
            }
        }
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
                CustomDialog fragment1 = new CustomDialog();
                Bundle args = new Bundle();
                args.putString("title", "Input your password to logout");
                fragment1.setArguments(args);
                fragment1.show(getSupportFragmentManager(), "tag");
            }
        });

        try {
            backupDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }


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

    public static void backupDatabase() throws IOException {
        //Open your local db as the input stream
        String inFileName = "/data/data/com.example.tales.tcc/databases/location_database.db";
        File dbFile = new File(inFileName);
        FileInputStream fis = new FileInputStream(dbFile);

        String outFileName = Environment.getExternalStorageDirectory()+"/location_database.db";
        //Open the empty db as the output stream
        OutputStream output = new FileOutputStream(outFileName);
        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer))>0){
            output.write(buffer, 0, length);
        }
        //Close the streams
        output.flush();
        output.close();
        fis.close();
    }

    @Override
    public void onFinishEditDialog(String inputText) {
        Log.d("Testing return", inputText);

        ArrayList<UserModel> user = UserModel.getUser(MainActivity.this);
        if(inputText.equals(user.get(0).getPassword())) {
            UserModel.deleteUsers(MainActivity.this);
            Intent newIntent = new Intent(MainActivity.this, LoginActivity.class);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            newIntent.putExtra("LOGOUT", true);
            MainActivity.this.startActivity(newIntent);
        } else {
            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Wrong password!")
                    .show();
        }
    }
}
