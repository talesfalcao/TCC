package com.example.tales.tcc.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.tales.tcc.R;

/**
 * Created by tales on 07/06/2017.
 */

public class DecideActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.decide_activity);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }

        init();
    }

    private void init() {
        ImageView ivChild = (ImageView) findViewById(R.id.iv_child);
        ImageView ivParent = (ImageView) findViewById(R.id.iv_parent);

        ivChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DecideActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        ivParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DecideActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

    }
}
