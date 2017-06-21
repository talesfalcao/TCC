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

import com.example.tales.tcc.Constants;
import com.example.tales.tcc.R;
import com.example.tales.tcc.db.UserModel;

import java.util.ArrayList;

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

        ArrayList<UserModel> users = UserModel.getUser(LoginActivity.this);
        if(!users.isEmpty()) {
            if(users.get(0).getType().equals(Constants.parent)) {
                Intent i = new Intent(LoginActivity.this, DrawerActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                LoginActivity.getInstance().finish();
                finish();
            } else {
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
            }
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
                if(login.getText().toString().isEmpty()) {
                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Please insert your login information")
                            .show();
                } else if (pw.getText().toString().isEmpty()) {
                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Please enter your password")
                            .show();
                } else {
                    ArrayList<UserModel> users = UserModel.getUserByLogin(LoginActivity.this, login.getText().toString());
                    if (users.isEmpty()) {
                        Bundle args = new Bundle();
                        args.putString(Constants.login, login.getText().toString());
                        args.putString(Constants.password, pw.getText().toString());
                        Intent i = new Intent(LoginActivity.this, DecideActivity.class);
                        i.putExtras(args);
                        startActivity(i);
                    } else {
                        UserModel user = users.get(0);
                        if (user.getPassword().equals(pw.getText().toString())) {
                            if (user.getType().equals(Constants.parent)) {
                                Intent i = new Intent(LoginActivity.this, DrawerActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                LoginActivity.getInstance().finish();
                                finish();
                            } else {
                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(i);
                            }
                        } else {
                            new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops...")
                                    .setContentText("Please check your password")
                                    .show();
                        }
                    }
                }
            }
        });
    }
}
