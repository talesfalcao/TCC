package com.example.tales.tcc.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.example.tales.tcc.Constants;
import com.example.tales.tcc.dialogs.CustomDialog;
import com.example.tales.tcc.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by tales on 07/06/2017.
 */

public class DecideActivity extends AppCompatActivity implements CustomDialog.EditNameDialogListener{

    boolean parent;

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
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();



        DatabaseReference  database = FirebaseDatabase.getInstance().getReference();
        database.child(Constants.users).child(id).child(Constants.name).setValue(inputText);
        database.child(Constants.users).child(id).child(Constants.logged).setValue("1");
        database.child(Constants.users).child(id).child(Constants.firebase_token).setValue(FirebaseInstanceId.getInstance().getToken());

        if(parent) {
            database.child(Constants.users).child(id).child(Constants.type).setValue(Constants.parent);

            Intent i = new Intent(DecideActivity.this, DrawerActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            LoginActivity.getInstance().finish();
            finish();
        } else {
            database.child(Constants.users).child(id).child(Constants.type).setValue(Constants.child);

            Intent i = new Intent(DecideActivity.this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            LoginActivity.getInstance().finish();
            finish();
        }
    }
}
