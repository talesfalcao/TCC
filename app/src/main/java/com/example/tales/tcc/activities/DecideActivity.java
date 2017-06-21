package com.example.tales.tcc.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.tales.tcc.Constants;
import com.example.tales.tcc.CustomDialog;
import com.example.tales.tcc.R;
import com.example.tales.tcc.db.UserModel;

/**
 * Created by tales on 07/06/2017.
 */

public class DecideActivity extends AppCompatActivity implements CustomDialog.EditNameDialogListener{
    String login = "";
    String pw = "";
    boolean parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.decide_activity);
        Bundle extras = DecideActivity.this.getIntent().getExtras();
        if(extras != null) {
            login = extras.getString(Constants.login);
            pw = extras.getString(Constants.password);
        }
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
                parent = false;
                CustomDialog fragment1 = new CustomDialog();
                Bundle args = new Bundle();
                args.putString("title", "Hello! What should I call this child of yours?");
                fragment1.setArguments(args);
                fragment1.show(getSupportFragmentManager(), "tag");
            }
        });

        ivParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parent = true;
                CustomDialog fragment1 = new CustomDialog();
                Bundle args = new Bundle();
                args.putString("title", "Hello! What should I call you?");
                fragment1.setArguments(args);
                fragment1.show(getSupportFragmentManager(), "tag");
            }
        });
    }


    @Override
    public void onFinishEditDialog(String inputText) {
        Log.d("Testing return", inputText);
        if(parent) {
            UserModel user = new UserModel(inputText, login, pw, Constants.parent, "");
            user.insertUser(DecideActivity.this);
            Intent i = new Intent(DecideActivity.this, DrawerActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            LoginActivity.getInstance().finish();
            finish();
        } else {
            UserModel user = new UserModel(inputText, login, pw, Constants.child, login);
            user.insertUser(DecideActivity.this);
            Intent i = new Intent(DecideActivity.this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            LoginActivity.getInstance().finish();
            finish();
        }
    }
}
