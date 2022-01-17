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
import com.gap.bis_inspection.common.HejriUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by root on 7/17/16.
 */
public class ViolationListAdapter extends ArrayAdapter<JSONObject> {
    private List<JSONObject> list;
    private Integer resourceId;

    public ViolationListAdapter(Context context, int resourceId,
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
            final JSONObject violationCodeJsonObject = list.get(position);
            if (violationCodeJsonObject != null) {
                if (!violationCodeJsonObject.isNull("violationBase")) {
                    JSONObject carJsonObject = violationCodeJsonObject.getJSONObject("violationBase");
                    ((TextView) convertView.findViewById(R.id.violationCode_TV)).setText(carJsonObject.getString("code"));
                } else {
                    ((TextView) convertView.findViewById(R.id.violationCode_TV)).setText("---");
                }

                if (!violationCodeJsonObject.isNull("violationBaseAction")) {
                    JSONObject carJsonObject = violationCodeJsonObject.getJSONObject("violationBaseAction");
                    ((TextView) convertView.findViewById(R.id.violationActionText_TV)).setText(carJsonObject.getString("actionText"));
                } else {
                    ((TextView) convertView.findViewById(R.id.violationActionText_TV)).setText("---");
                }

                if (!violationCodeJsonObject.isNull("violation")) {
                    JSONObject incidentJsonObject = violationCodeJsonObject.getJSONObject("violation");
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    String incidentDate = incidentJsonObject.getString("watchingDate");
                    Date startDate = simpleDateFormat.parse(incidentDate);
                    String hejriStartDate = HejriUtil.chrisToHejri(startDate);
                    ((TextView) convertView.findViewById(R.id.violationDate_TV)).setText(hejriStartDate);
                } else {
                    ((TextView) convertView.findViewById(R.id.violationDate_TV)).setText("---");
                }
            }
        } catch (Exception e) {
            Log.i(ViolationListAdapter.class.toString(), e.getMessage());
        }
        if (position % 2 == 1) {
            convertView.setBackgroundColor(Color.parseColor("#e6eed5"));
        } else {
            convertView.setBackgroundColor(Color.parseColor("#eeeeee"));
        }

        return convertView;
    }

    @Override
    public long getItemId(int position) {

        try {
            JSONObject jsonObject = list.get(position);
            return jsonObject.getLong("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
