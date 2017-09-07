package com.example.tales.tcc;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tales.tcc.activities.DrawerActivity;
import com.example.tales.tcc.adapters.ChooseAdapter;
import com.example.tales.tcc.db.UserLocModel;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by tales on 07/09/2017.
 */

public class ChooseDialog extends Dialog {
    public Activity c;
    public Dialog d;
    public Button yes;

    public ChooseDialog(Activity a) {
        super(a);
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.device_picker);

        ArrayList<UserLocModel> array = UserLocModel.getAllLocations(c);
        ListView lv = (ListView)findViewById(R.id.lv);
        final ChooseAdapter adapter = new ChooseAdapter(array, c);
        lv.setAdapter(adapter);

        yes = (Button) findViewById(R.id.register_button);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adapter.getSelected().isEmpty()) {
                    new SweetAlertDialog(c, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Whoops!")
                            .setContentText("Select at least one child")
                            .setConfirmText("OK")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismiss();
                                }
                            })
                            .show();
                } else {
                    DrawerActivity.instance.selected.clear();
                    DrawerActivity.instance.selected.addAll(adapter.getSelected());
                    ChooseDialog.this.dismiss();
                }
            }
        });

        Typeface regularFont = Typeface.createFromAsset(c.getAssets(), "Quicksand-Regular.otf");
        Typeface bold = Typeface.createFromAsset(c.getAssets(), "Quicksand-Bold.otf");

        yes.setTypeface(bold);
        ((TextView)findViewById(R.id.tv_pw)).setTypeface(regularFont);

        setListViewHeight(lv);
    }

    private void setListViewHeight(ListView listview) {
        ListAdapter listadp = listview.getAdapter();
        if (listadp != null) {
            int totalHeight = 0;
            for (int i = 0; i < listadp.getCount(); i++) {
                View listItem = listadp.getView(i, null, listview);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = listview.getLayoutParams();
            params.height = totalHeight + (listview.getDividerHeight() * (listadp.getCount() - 1));
            listview.setLayoutParams(params);
            listview.requestLayout();
        }
    }
}