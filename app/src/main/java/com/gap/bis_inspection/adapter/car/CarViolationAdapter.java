package com.gap.bis_inspection.adapter.car;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.adapter.ViolationListAdapter;
import com.gap.bis_inspection.common.HejriUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Mohamad Cheraghi on 08/28/2016.
 */
public class CarViolationAdapter extends ArrayAdapter<JSONObject> {
    private List<JSONObject> list;
    private Integer resourceId;

    public CarViolationAdapter(Context context, int resourceId,
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
                    // JSONObject volationObject = carJsonObject.getJSONObject("code");
                    //((TextView) convertView.findViewById(R.id.violationtcode)).setText(carJsonObject.getString("code"));
                }

                if (!violationCodeJsonObject.isNull("violationBaseAction")) {
                    JSONObject carJsonObject = violationCodeJsonObject.getJSONObject("violationBaseAction");
                    // JSONObject volationObject = carJsonObject.getJSONObject("code");
                    //((TextView) convertView.findViewById(R.id.violationtext)).setText(carJsonObject.getString("actionText"));
                }

                if(!violationCodeJsonObject.isNull("violation")){
                    JSONObject incidentJsonObject = violationCodeJsonObject.getJSONObject("violation");
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    String incidentdate = incidentJsonObject.getString("watchingDate");
                    Date startDate = simpleDateFormat.parse(incidentdate);
                    String hejriStartDate = HejriUtil.chrisToHejri(startDate);
                    //((TextView) convertView.findViewById(R.id.violationtDate)).setText(hejriStartDate);
                }
            }
        } catch (Exception e) {
            Log.i(ViolationListAdapter.class.toString(), e.getMessage());
        }

        if (position % 2 == 1) {
            convertView.setBackgroundColor(Color.parseColor("#efd3d2"));
        } else {
            convertView.setBackgroundColor(Color.parseColor("#eeeeee"));
        }

        return convertView;
    }

    @Override
    public long getItemId(int position) {

        try {
            JSONObject jsonObject=list.get(position);
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

