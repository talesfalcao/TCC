package com.example.tales.tcc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tales.tcc.db.PatternsModel;

import java.util.ArrayList;

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
        }
        return rowView;
    }
}
