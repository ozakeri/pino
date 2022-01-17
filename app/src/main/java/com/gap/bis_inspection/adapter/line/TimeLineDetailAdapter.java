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



public class TimeLineDetailAdapter extends ArrayAdapter<JSONObject> {
    private List<JSONObject> list;
    private Integer resourceId;

    public TimeLineDetailAdapter(Context context, int resourceId,
                                 List<JSONObject> list) {
        super(context, resourceId, list);
        this.list = list;
        this.resourceId = resourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try {

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            }
            final JSONObject timeLineDetailListJsonObject = list.get(position);
            if (timeLineDetailListJsonObject != null) {
                if (!timeLineDetailListJsonObject.isNull("startTime")) {
                    ((TextView) convertView.findViewById(R.id.startDate_TV)).setText(timeLineDetailListJsonObject.getString("startTime"));
                } else {
                    ((TextView) convertView.findViewById(R.id.startDate_TV)).setText("---");
                }

                if (!timeLineDetailListJsonObject.isNull("endTime")) {
                    ((TextView) convertView.findViewById(R.id.endDate_TV)).setText(timeLineDetailListJsonObject.getString("endTime"));
                }else {
                    ((TextView) convertView.findViewById(R.id.endDate_TV)).setText("---");
                }

                if (!timeLineDetailListJsonObject.isNull("moveDistanceOnSec")) {
                    ((TextView) convertView.findViewById(R.id.moveDistanceOnSec_TV)).setText(timeLineDetailListJsonObject.getString("moveDistanceOnSec"));
                }else {
                    ((TextView) convertView.findViewById(R.id.moveDistanceOnSec_TV)).setText("---");
                }

                if (!timeLineDetailListJsonObject.isNull("halfPathSum")) {
                    ((TextView) convertView.findViewById(R.id.halfPathSum_TV)).setText(timeLineDetailListJsonObject.getString("halfPathSum"));
                }else {
                    ((TextView) convertView.findViewById(R.id.halfPathSum_TV)).setText("---");
                }

            }
        }catch (Exception e) {
            Log.i(TimeLineDetailAdapter.class.toString(), e.getMessage());
        }
        if (position % 2 == 1) {
            convertView.setBackgroundColor(Color.parseColor("#d2eaf1"));
        } else {
            convertView.setBackgroundColor(Color.parseColor("#eeeeee"));
        }
        return convertView;
    }
}
