package com.example.tales.tcc.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.tales.tcc.R;
import com.example.tales.tcc.activities.DrawerActivity;

import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by tales on 11/09/2017.
 */

public class TimeDialog extends Dialog {
    public Activity c;
    public Dialog d;

    public TimeDialog(Activity a) {
        super(a);
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.time_date_picker);
        Typeface regularFont = Typeface.createFromAsset(c.getAssets(), "Quicksand-BoldItalic.otf");
        Typeface bold = Typeface.createFromAsset(c.getAssets(), "Quicksand-Bold.otf");

        ((TextView) findViewById(R.id.title)).setTypeface(bold);
        ((TextView) findViewById(R.id.start_time)).setTypeface(regularFont);
        ((TextView) findViewById(R.id.end_time)).setTypeface(regularFont);
        ((TextView) findViewById(R.id.day)).setTypeface(regularFont);
        ((TextView) findViewById(R.id.register_button)).setTypeface(bold);

        final EditText start = (EditText) findViewById(R.id.et_start);
        final EditText end = (EditText) findViewById(R.id.et_end);
        start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                final int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(c, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String hours, minutes;
                        if(selectedMinute >= 53 || selectedMinute < 8) {
                            minutes = "00";
                        } else if (selectedMinute >= 8 && selectedMinute < 24) {
                            minutes = "15";
                        } else if (selectedMinute >= 24 && selectedMinute < 37) {
                            minutes = "30";
                        } else {
                            minutes = "45";
                        }
                        if(selectedHour < 10) {
                            hours = "0" + selectedHour;
                        } else {
                            hours = selectedHour + "";
                        }
                        start.setText( hours + ":" + minutes);
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select the starting time");
                mTimePicker.show();
            }
        });

        end.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                final int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(c, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String hours, minutes;
                        if(selectedMinute >= 53 || selectedMinute < 8) {
                            minutes = "00";
                        } else if (selectedMinute >= 8 && selectedMinute < 24) {
                            minutes = "15";
                        } else if (selectedMinute >= 24 && selectedMinute < 37) {
                            minutes = "30";
                        } else {
                            minutes = "45";
                        }
                        if(selectedHour < 10) {
                            hours = "0" + selectedHour;
                        } else {
                            hours = selectedHour + "";
                        }
                        end.setText( hours + ":" + minutes);
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select the ending time");
                mTimePicker.show();
            }
        });

        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setSelection(0);

        findViewById(R.id.register_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(start.getText().toString().isEmpty() || end.getText().toString().isEmpty()) {
                    new SweetAlertDialog(c, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Whoops")
                            .setContentText("Please, input both end and start times")
                            .setConfirmText("OK")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();
                                }
                            })
                            .show();
                } else {
                    String[] split = start.getText().toString().split(":");
                    String[] split2 = end.getText().toString().split(":");
                    if(Integer.parseInt(split[0]) > Integer.parseInt(split2[0])) {
                        new SweetAlertDialog(c, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Whoops")
                                .setContentText("Ending hour must be later than starting hours")
                                .setConfirmText("OK")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismiss();
                                    }
                                })
                                .show();
                    } else if(Integer.parseInt(split[0]) == Integer.parseInt(split2[0])) {
                        if(Integer.parseInt(split[1]) >= Integer.parseInt(split2[1])) {
                            new SweetAlertDialog(c, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Whoops")
                                    .setContentText("Ending hour must be later than starting hours")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismiss();
                                        }
                                    })
                                    .show();
                        } else {
                            DrawerActivity.instance.setPatternDateTime((String) spinner.getSelectedItem(), split[0], split[1], split2[0], split2[1]);
                            dismiss();
                        }
                    } else {
                        DrawerActivity.instance.setPatternDateTime((String) spinner.getSelectedItem(), split[0], split[1], split2[0], split2[1]);
                        dismiss();
                    }
                }
            }
        });
    }
}