package com.example.tales.tcc.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tales.tcc.R;
import com.example.tales.tcc.db.UserLocModel;

import java.util.ArrayList;

/**
 * Created by tales on 07/09/2017.
 */

public class ChooseAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<UserLocModel> dataSet = new ArrayList<>();
    ArrayList<UserLocModel> selected = new ArrayList<>();

    public ChooseAdapter(ArrayList<UserLocModel> data, Context context) {
        dataSet.clear();
        dataSet.addAll(data);
        this.mContext=context;
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public Object getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.device_cell, parent, false);
        }

        final UserLocModel dataModel = (UserLocModel) getItem(position);
        Typeface regularFont = Typeface.createFromAsset(mContext.getAssets(), "Quicksand-Regular.otf");
        Typeface light= Typeface.createFromAsset(mContext.getAssets(), "Quicksand-Light.otf");

        ((TextView) convertView.findViewById(R.id.name)).setTypeface(regularFont);
        ((TextView) convertView.findViewById(R.id.status)).setTypeface(light);

        ((TextView) convertView.findViewById(R.id.name)).setText(dataModel.mName);
        if(dataModel.mInside.equals("false")) {
            ((TextView) convertView.findViewById(R.id.status)).setText(R.string.out);
            ((TextView) convertView.findViewById(R.id.status)).setTextColor(ContextCompat.getColor(mContext, R.color.red));

            ((ImageView) convertView.findViewById(R.id.checked)).setImageResource(R.drawable.checkbox_checked);
            selected.add(dataModel);
        } else {
            ((TextView) convertView.findViewById(R.id.status)).setText(R.string.inside);
            ((TextView) convertView.findViewById(R.id.status)).setTextColor(ContextCompat.getColor(mContext, R.color.text_green));

            ((ImageView) convertView.findViewById(R.id.checked)).setImageResource(R.drawable.checkbox_unchecked);
        }

        final View finalConvertView = convertView;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSelected(dataModel)) {
                    ((ImageView) finalConvertView.findViewById(R.id.checked)).setImageResource(R.drawable.checkbox_unchecked);
                    selected.remove(dataModel);
                } else {
                    ((ImageView) finalConvertView.findViewById(R.id.checked)).setImageResource(R.drawable.checkbox_checked);
                    selected.add(dataModel);
                }
            }
        });
        return convertView;
    }

    public ArrayList<UserLocModel> getSelected() {
        return selected;
    }

    public boolean isSelected(UserLocModel user) {
        return selected.contains(user);
    }
}