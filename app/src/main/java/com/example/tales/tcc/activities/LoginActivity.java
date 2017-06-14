package com.example.tales.tcc.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tales.tcc.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Login to MainActivity
 */

public class LoginActivity extends AppCompatActivity {
    private static LoginActivity instance = null;

    public static LoginActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        instance = this;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }

        init();
    }

    private void init() {
        final EditText login = (EditText) findViewById(R.id.et_login);
        final EditText pw = (EditText) findViewById(R.id.et_password);

        if(getIntent() != null && getIntent().getBooleanExtra("LOGOUT", false)) {
            new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Logged out!")
                    .show();
        }
        Button button = (Button) findViewById(R.id.login_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(login.getText().toString().equals(pw.getText().toString())) {
                    Intent i = new Intent(LoginActivity.this, DecideActivity.class);
                    startActivity(i);
                } else {
                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Please check your login information")
                            .show();
                }
            }
        });
    }
}
