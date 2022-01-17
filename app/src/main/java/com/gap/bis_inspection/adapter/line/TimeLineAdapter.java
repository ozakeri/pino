package com.gap.bis_inspection.adapter.line;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gap.bis_inspection.R;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by Mohamad Cheraghi on 08/29/2016.
 */
public class TimeLineAdapter extends ArrayAdapter<JSONObject> {
    private List<JSONObject> list;
    private Integer resourceId;

    public TimeLineAdapter(Context context, int resourceId,
                           List<JSONObject> list) {
        super(context, resourceId);
        this.list = list;
        this.resourceId = resourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try {

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            }
            final JSONObject mainTimeLineJsonObject = list.get(position);
            if (mainTimeLineJsonObject != null) {
                if (!mainTimeLineJsonObject.isNull("pathTypeEn")) {
                    int type = mainTimeLineJsonObject.getInt("pathTypeEn");
                    if (type == 0) {
                        ((TextView) convertView.findViewById(R.id.pathTypeEn_TV)).setText(R.string.enumType_PathType_Main);
                    } else if (type == 1) {
                        ((TextView) convertView.findViewById(R.id.pathTypeEn_TV)).setText(R.string.enumType_PathType_Returned);
                    }

                } else {
                    ((TextView) convertView.findViewById(R.id.pathTypeEn_TV)).setText("---");
                }

                /*if (!mainTimeLineJsonObject.isNull("dayTypeOnHolidayEn")) {
                    int type = mainTimeLineJsonObject.getInt("dayTypeOnHolidayEn");
                    if (type == 0) {
                        ((TextView) convertView.findViewById(R.id.dayTypeOnHolidayEn_TV)).setText(R.string.enumType_DayTypeOnHolidayEn_NotHoliday);
                    }else if (type == 1) {
                        ((TextView) convertView.findViewById(R.id.dayTypeOnHolidayEn_TV)).setText(R.string.enumType_DayTypeOnHolidayEn_Holiday);
                    }else if (type == 2) {
                        ((TextView) convertView.findViewById(R.id.dayTypeOnHolidayEn_TV)).setText(R.string.enumType_DayTypeOnHolidayEn_SemiHoliday);
                    }
                } else {
                    ((TextView) convertView.findViewById(R.id.dayTypeOnHolidayEn_TV)).setText("---");
                }*/

                if (!mainTimeLineJsonObject.isNull("startTime")) {
                    ((TextView) convertView.findViewById(R.id.startTime_TV)).setText(mainTimeLineJsonObject.getString("startTime"));
                }else {
                    ((TextView) convertView.findViewById(R.id.startTime_TV)).setText("---");
                }

                if (!mainTimeLineJsonObject.isNull("endTime")) {
                    ((TextView) convertView.findViewById(R.id.endTime_TV)).setText(mainTimeLineJsonObject.getString("endTime"));
                }else {
                    ((TextView) convertView.findViewById(R.id.endTime_TV)).setText("---");
                }

            }
        }catch (Exception e) {
            Log.i(TimeLineAdapter.class.toString(), e.getMessage());
        }
        if (position % 2 == 1) {
            convertView.setBackgroundColor(Color.parseColor("#d2eaf1"));
        } else {
            convertView.setBackgroundColor(Color.parseColor("#eeeeee"));
        }
        return convertView;
    }
}
