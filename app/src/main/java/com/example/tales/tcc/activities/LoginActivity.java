package com.example.tales.tcc.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.tales.tcc.Constants;
import com.example.tales.tcc.CustomDialog;
import com.example.tales.tcc.R;
import com.example.tales.tcc.User;
import com.facebook.stetho.Stetho;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.zxing.Result;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Login to MainActivity
 */

public class LoginActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    AlertDialog alertDialog;
    boolean parent;
    private static LoginActivity instance = null;
    private ZXingScannerView mScannerView;
    String name;
    
    private FirebaseAuth auth;
    FirebaseAuth.AuthStateListener mAuthListener;
    ValueEventListener valueEventListener;

    public static LoginActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.login_activity);

        instance = this;
        auth = FirebaseAuth.getInstance();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        }

        if(getIntent() != null && getIntent().getBooleanExtra("LOGOUT", false)) {
            try {
                String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
                System.out.println(user);

                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                database.child(Constants.users).child(user).child(Constants.logged).setValue("0");


                getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE).edit().putBoolean(Constants.logged, false).apply();

                auth.signOut();
            } catch (Exception e) {

            }
            new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Logged out!")
                    .show();
        }

        if(getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE).getBoolean(Constants.logged, false)) {
            if(getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE).getString(Constants.type, Constants.child).equals(Constants.child)) {
                System.out.println("Child logged");
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                LoginActivity.getInstance().finish();
                finish();
            } else {
                System.out.println("Parent logged");
                Intent i = new Intent(LoginActivity.this, DrawerActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                LoginActivity.getInstance().finish();
                finish();
            }
        }

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    String user = firebaseAuth.getCurrentUser().getUid();
                    System.out.println(user);

                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                    final DatabaseReference ref = database.child(Constants.users).child(user);
                    valueEventListener = ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            if(user != null) {
                                String type = user.type;
                                String logged = user.logged;
                                String name = user.name;
                                System.out.println(name + " " + type + " " + logged);
                                ref.removeEventListener(this);
                                if(logged.equals("1")) {
                                    if (type.equals(Constants.parent)) {
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
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            System.out.println("The read failed: " + databaseError.getCode());
                        }
                    });
                }
            }
        };
        init();
    }

    private void init() {
        final RelativeLayout myLayout = (RelativeLayout) findViewById(R.id.parent);
        myLayout.requestFocus();

        final EditText login = (EditText) findViewById(R.id.et_login);
        final EditText pw = (EditText) findViewById(R.id.et_password);
        TextView register = (TextView)findViewById(R.id.register);
        final Button button = (Button) findViewById(R.id.login_button);

        Typeface regularFont = Typeface.createFromAsset(getAssets(), "Quicksand-Regular.otf");
        Typeface bold = Typeface.createFromAsset(getAssets(), "Quicksand-Bold.otf");

        register.setTypeface(bold);
        login.setTypeface(regularFont);
        pw.setTypeface(regularFont);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(login.getText().toString().trim(), pw.getText().toString().trim());
            }
        });

        pw.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ( (actionId == EditorInfo.IME_ACTION_DONE) || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN ))){
                    button.performClick();
                    return true;
                }
                else{
                    return false;
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                LayoutInflater li = LayoutInflater.from(LoginActivity.this);
                View promptsView = li.inflate(R.layout.register_dialog, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);

                alertDialogBuilder.setView(promptsView);

                final EditText userEt = (EditText) promptsView.findViewById(R.id.et_register);
                final EditText pwEt = (EditText) promptsView.findViewById(R.id.et_password);
                final TextView register = (TextView) promptsView.findViewById(R.id.register_button);

                register.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        register(userEt.getText().toString().trim(), pwEt.getText().toString().trim());
                    }
                });

                // set dialog message
                alertDialogBuilder.setCancelable(true);

                // create alert dialog
                alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });
    }

    private void signIn(String email, final String password) {
        if (TextUtils.isEmpty(email)) {
            new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Please enter your email address!")
                    .show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Please enter your password!")
                    .show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            // there was an error
                            if (password.length() < 6) {
                                new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Oops...")
                                        .setContentText("Your password must be longer than 5 characters")
                                        .show();
                            } else {
                                new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Oops...")
                                        .setContentText("Please check your login information")
                                        .show();
                            }
                        } else {
                            final String user = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                            DatabaseReference ref = database.child(Constants.users).child(user);

                            ref.removeEventListener(valueEventListener);

                            LayoutInflater li = LayoutInflater.from(LoginActivity.this);
                            View promptsView = li.inflate(R.layout.new_password, null);

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);

                            alertDialogBuilder.setView(promptsView);

                            final TextView tv = (TextView) promptsView.findViewById(R.id.tv_pw);
                            final EditText userEt = (EditText) promptsView.findViewById(R.id.et_pw);
                            final Button register = (Button) promptsView.findViewById(R.id.register_button);

                            tv.setText(R.string.what_is_your_name);
                            userEt.setHint("Name");

                            register.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(userEt.getText().toString().trim().isEmpty()) {
                                        new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE)
                                                .setTitleText("Whoops")
                                                .setContentText("Make sure you've entered your name")
                                                .setConfirmText("OK")
                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sDialog) {
                                                        sDialog.dismiss();
                                                    }
                                                })
                                                .show();
                                    } else {
                                        alertDialog.dismiss();
                                        name = userEt.getText().toString();
                                        new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.BUTTON_NEUTRAL)
                                                .setTitleText("Is this a child or a parent device?")
                                                .setCancelText("Child")
                                                .setConfirmText("Parent")
                                                .showCancelButton(true)
                                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sDialog) {
                                                        parent = false;
                                            /*CustomDialog fragment1 = new CustomDialog();
                                            Bundle args = new Bundle();
                                            args.putString("title", "Enter the family code");
                                            fragment1.setArguments(args);
                                            fragment1.show(getSupportFragmentManager(), "tag");*/

                                                        LayoutInflater li = LayoutInflater.from(LoginActivity.this);
                                                        View myView = li.inflate(R.layout.qrreader, null);

                                                        AlertDialog.Builder cDialog = new AlertDialog.Builder(LoginActivity.this);
                                                        cDialog.setView(myView);
                                                        cDialog.create();
                                                        sDialog.dismiss();
                                                        QrScanner(myView);
                                                    }
                                                })
                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sDialog) {
                                                        sDialog
                                                                .setTitleText("Now what?")
                                                                .setContentText("Join a existing family or create a new one?")
                                                                .setCancelText("Join")
                                                                .setConfirmText("Create")
                                                                .showCancelButton(true)
                                                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                    @Override
                                                                    public void onClick(SweetAlertDialog sDialog) {
                                                                        parent = true;
                                                            /*CustomDialog fragment1 = new CustomDialog();
                                                            Bundle args = new Bundle();
                                                            args.putString("title", "Enter the family code");
                                                            fragment1.setArguments(args);
                                                            fragment1.show(getSupportFragmentManager(), "tag");*/

                                                                        LayoutInflater li = LayoutInflater.from(LoginActivity.this);
                                                                        View myView = li.inflate(R.layout.qrreader, null);

                                                                        AlertDialog.Builder cDialog = new AlertDialog.Builder(LoginActivity.this);
                                                                        cDialog.setView(myView);
                                                                        cDialog.create();
                                                                        sDialog.dismiss();
                                                                        QrScanner(myView);
                                                                    }
                                                                })
                                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                    @Override
                                                                    public void onClick(SweetAlertDialog sDialog) {
                                                                        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                                                        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

                                                                        database.child(Constants.users).child(id).child(Constants.family).setValue(id);
                                                                        database.child(Constants.users).child(id).child(Constants.type).setValue(Constants.parent);
                                                                        database.child(Constants.users).child(id).child(Constants.logged).setValue("1");
                                                                        database.child(Constants.users).child(id).child(Constants.name).setValue(name);

                                                                        database.child(Constants.family).child(id).child(Constants.parent).child(id).child(Constants.firebase_token).setValue(FirebaseInstanceId.getInstance().getToken());
                                                                        database.child(Constants.family).child(id).child(Constants.parent).child(id).child(Constants.name).setValue(name);

                                                                        getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE).edit().putBoolean(Constants.logged, true).apply();
                                                                        getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE).edit().putString(Constants.type, Constants.parent).apply();
                                                                        getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE).edit().putString(Constants.family, id).apply();
                                                                        Intent intent = new Intent(LoginActivity.this, DrawerActivity.class);
                                                                        startActivity(intent);
                                                                        finish();
                                                                    }
                                                                });
                                                    }
                                                })
                                                .show();
                                    }
                                }
                            });

                            alertDialogBuilder.setCancelable(true);
                            alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }
                    }
                });
    }

    private void register(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Please enter your email address!")
                    .show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Please enter your password!")
                    .show();
            return;
        }

        if (password.length() < 6) {
            new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Your password must be longer than 5 characters")
                    .show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                       Log.d("Login", "createUserWithEmail:onComplete: " + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops...")
                                    .setContentText(task.getException().getMessage())
                                    .show();
                        } else {
                            alertDialog.dismiss();
                            new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Registered!!")
                                    .show();
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            auth.removeAuthStateListener(mAuthListener);
        }
    }

    public void QrScanner(View view){
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();         // Start camera
    }
    @Override
    public void onPause() {
        super.onPause();
        try {
            mScannerView.stopCamera();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleResult(Result rawResult) {
        Log.e("handler", rawResult.getText()); // Prints scan results
        String inputText = rawResult.getText();

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.family, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.family, inputText);
        editor.apply();

        DatabaseReference  database = FirebaseDatabase.getInstance().getReference();
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        database.child(Constants.users).child(id).child(Constants.family).setValue(inputText);
        database.child(Constants.users).child(id).child(Constants.logged).setValue("1");
        database.child(Constants.users).child(id).child(Constants.name).setValue(name);
        getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE).edit().putBoolean(Constants.logged, true).apply();
        if(parent) {
            getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE).edit().putString(Constants.type, Constants.parent).apply();
            database.child(Constants.family).child(inputText).child(Constants.parent).child(id).child(Constants.firebase_token).setValue(FirebaseInstanceId.getInstance().getToken());
            database.child(Constants.family).child(inputText).child(Constants.parent).child(id).child(Constants.name).setValue(name);
            Intent i = new Intent(LoginActivity.this, DrawerActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            LoginActivity.getInstance().finish();
            finish();
        } else {
            getSharedPreferences(Constants.SHARED_PREFS, MODE_PRIVATE).edit().putString(Constants.type, Constants.child).apply();
            database.child(Constants.family).child(inputText).child(Constants.child).setValue(id);
            database.child(Constants.family).child(inputText).child(Constants.child).child(id).child(Constants.firebase_token).setValue(FirebaseInstanceId.getInstance().getToken());
            database.child(Constants.family).child(inputText).child(Constants.child).child(id).child(Constants.name).setValue(name);
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            LoginActivity.getInstance().finish();
            finish();
        }
    }

    private void copyDataBase() {
        try {
            String DB_PATH = "";
            String DB_NAME = "database.db";

            DB_PATH = "/data/data/" + getPackageName() + "/databases/";

            InputStream mInput = getAssets().open(DB_NAME);
            String outFileName = DB_PATH + DB_NAME;
            OutputStream mOutput = new FileOutputStream(outFileName);
            byte[] mBuffer = new byte[1024];
            int mLength;
            while ((mLength = mInput.read(mBuffer)) > 0) {
                mOutput.write(mBuffer, 0, mLength);
            }
            mOutput.flush();
            mOutput.close();
            mInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
