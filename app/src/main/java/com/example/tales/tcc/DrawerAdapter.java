package com.example.tales.tcc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tales.tcc.activities.LoginActivity;
import com.example.tales.tcc.db.PatternsModel;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by tales on 11/06/2017.
 */

public class DrawerAdapter extends BaseAdapter {
    private ArrayList<String> mArray;
    private Context mContext;

    public DrawerAdapter(Context context, ArrayList<String> array) {
        mContext = context;
        mArray = array;
    }

    @Override
    public int getCount() {
        return mArray.size();
    }

    @Override
    public Object getItem(int position) {
        return mArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.drawer_cell, parent, false);

        TextView tv = (TextView) rowView.findViewById(R.id.tv_cell);
        ImageView iv = (ImageView) rowView.findViewById(R.id.iv_cell);

        tv.setText(mArray.get(position));
        if(position != (getCount() - 1)) {
            iv.setVisibility(View.GONE);
        } else {
            tv.setTextColor(Color.parseColor("#AA0000"));
            iv.setImageResource(R.drawable.ic_logout);
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Are you sure?")
                            .setConfirmText("Logout!")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    Intent newIntent = new Intent(mContext,LoginActivity.class);
                                    newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    newIntent.putExtra("LOGOUT", true);
                                    mContext.startActivity(newIntent);
                                }
                            })
                            .show();
                }
            });
        }
        return rowView;
    }
}
