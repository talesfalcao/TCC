package com.example.tales.tcc.activities;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
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
import com.example.tales.tcc.CustomAdapter;
import com.example.tales.tcc.GroupingsFinder;
import com.example.tales.tcc.PatternFinder;
import com.example.tales.tcc.R;
import com.example.tales.tcc.db.AveragesModel;
import com.example.tales.tcc.db.GroupingModel;
import com.example.tales.tcc.db.LocationModel;
import com.example.tales.tcc.db.PatternsModel;
import com.example.tales.tcc.services.LocationService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    String selected;
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



       /*GroupingsFinder finder = GroupingsFinder.getInstance(this);
        GroupingModel.deleteAll(MainActivity.this);
        finder.findGroupings();*/


        AveragesModel lastAverage = AveragesModel.getLastAverage(this);
        Log.d("Averages", "LastAverage != null -> " + lastAverage.getStart() + " " + lastAverage.getEnd() + " " + lastAverage.getDate());
        final ListView list = (ListView) findViewById(R.id.list);
        Button button = (Button) findViewById(R.id.button);
        Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        final EditText et = (EditText) findViewById(R.id.et);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*ArrayList<AveragesModel> array = AveragesModel.getAveragesByWeekday(MainActivity.this, selected);
                list.setAdapter(new CustomAdapter(MainActivity.this, array));*/

                /*ArrayList<AveragesModel> array = AveragesModel.getAveragesByWeekdayHour(MainActivity.this, selected, et.getText().toString());
                ArrayList<AveragesModel> aux = new ArrayList<>();
                for(AveragesModel avg: array) {
                    if(avg.getStart() <= Integer.parseInt(et.getText().toString()) && avg.getEnd() >= Integer.parseInt(et.getText().toString())) {
                        aux.add(avg);
                    }
                }
                list.setAdapter(new CustomAdapter(MainActivity.this, aux));*/

                //PatternFinder.getInstance(MainActivity.this).findPattern(selected, Integer.parseInt(et.getText().toString()));

                /*ArrayList<PatternsModel> array = PatternsModel.getPatternsByWeekday(MainActivity.this, selected);
                list.setAdapter(new CustomAdapter(MainActivity.this, array));*/
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(MainActivity.this, "Select something", Toast.LENGTH_SHORT).show();
            }
        });

        try {
            backupDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }



        //AveragesFinder.getInstance(this).findAverages();
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
}
