package com.example.tales.tcc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.tales.tcc.db.AveragesModel;
import com.example.tales.tcc.db.GroupingModel;
import com.example.tales.tcc.db.LocationModel;
import com.example.tales.tcc.db.PatternsModel;

import java.util.ArrayList;

/**
 * Created by tales on 02/05/2017.
 */

public class CustomAdapter extends BaseAdapter {
    private ArrayList<PatternsModel> mArray;
    private Context mContext;

    public CustomAdapter(Context context, ArrayList<PatternsModel> array) {
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
        View rowView = inflater.inflate(R.layout.list_item, parent, false);

        TextView tv = (TextView) rowView.findViewById(R.id.tv);
        String finalStr;
        String start, end;
        String startHours = String.valueOf(Integer.parseInt(mArray.get(position).getStart()) / 60);
        String startMinutes = String.valueOf(Integer.parseInt(mArray.get(position).getStart()) % 60);
        if(Integer.parseInt(startHours) < 10) {
            startHours = "0".concat(startHours);
        }
        if(Integer.parseInt(startMinutes) < 10) {
            startMinutes = "0".concat(startMinutes);
        }
        start = startHours + ":" + startMinutes;

        String endHours = String.valueOf(Integer.parseInt(mArray.get(position).getEnd()) / 60);
        String endMinutes = String.valueOf(Integer.parseInt(mArray.get(position).getEnd()) % 60);
        if(Integer.parseInt(endHours) < 10) {
            endHours = "0".concat(endHours);
        }
        if(Integer.parseInt(endMinutes) < 10) {
            endMinutes = "0".concat(endMinutes);
        }
        end = endHours + ":" + endMinutes;

        finalStr = start + " - " + end + " " + mArray.get(position).getLatitude() + ", " + mArray.get(position).getLongitude();

        tv.setText(finalStr);
        return rowView;
    }
}
