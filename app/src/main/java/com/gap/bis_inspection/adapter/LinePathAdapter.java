package com.gap.bis_inspection.adapter;

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

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Mohamad Cheraghi on 08/29/2016.
 */
public class LinePathAdapter extends ArrayAdapter<JSONObject> {
    private List<JSONObject> list;
    private Integer resourceId;

    public LinePathAdapter(Context context, int resourceId,
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
            final JSONObject linePathListJsonObject = list.get(position);
            if (linePathListJsonObject != null) {
                if (!linePathListJsonObject.isNull("pathType")) {
                    int type = linePathListJsonObject.getInt("pathType");
                    if (type == 0) {
                        ((TextView) convertView.findViewById(R.id.pathType_TV))
                                .setText(getContext().getResources().getString(R.string.enumType_PathType_Main));
                    } else if (type == 1) {
                        ((TextView) convertView.findViewById(R.id.pathType_TV))
                                .setText(getContext().getResources().getString(R.string.enumType_PathType_Returned));
                    }

                } else {
                    ((TextView) convertView.findViewById(R.id.pathType_TV)).setText("---");
                }

                if (!linePathListJsonObject.isNull("pathText")) {
                    String pathText = linePathListJsonObject.getString("pathText");
                    ((TextView) convertView.findViewById(R.id.pathText_TV)).setText(pathText);
                } else {
                    ((TextView) convertView.findViewById(R.id.pathText_TV)).setText("---");
                }


                if (!linePathListJsonObject.isNull("pathKm")) {
                    String pathKm = linePathListJsonObject.getString("pathKm");
                    double amount = Double.parseDouble(pathKm);
                    DecimalFormat formatter = new DecimalFormat("#,###");
                    ((TextView) convertView.findViewById(R.id.pathKm_TV)).setText(formatter.format(amount));
                } else {
                    ((TextView) convertView.findViewById(R.id.pathKm_TV)).setText("---");
                }

                if (!linePathListJsonObject.isNull("stationNo")) {
                    String stationNo = linePathListJsonObject.getString("stationNo");
                    ((TextView) convertView.findViewById(R.id.stationNo_TV)).setText(stationNo);
                } else {
                    ((TextView) convertView.findViewById(R.id.stationNo_TV)).setText("---");
                }

                if (!linePathListJsonObject.isNull("counterNo")) {
                    String counterNo = linePathListJsonObject.getString("counterNo");
                    ((TextView) convertView.findViewById(R.id.convertNo_TV)).setText(counterNo);
                } else {
                    ((TextView) convertView.findViewById(R.id.convertNo_TV)).setText("---");
                }
            }
        } catch (Exception e) {
            Log.i(LinePathAdapter.class.toString(), e.getMessage());
        }
       /* if (position % 2 == 1) {
            convertView.setBackgroundColor(Color.parseColor("#d2eaf1"));
        } else {
            convertView.setBackgroundColor(Color.parseColor("#eeeeee"));
        }*/
        return convertView;
    }
}
