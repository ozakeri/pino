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
import com.gap.bis_inspection.common.HejriUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Mohamad Cheraghi on 08/28/2016.
 */
public class CarIncidentAdapter extends ArrayAdapter<JSONObject> {
    private List<JSONObject> list;
    private Integer resourceId;

    public CarIncidentAdapter(Context context, int resourceId,
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
            final JSONObject incidentEntityJsonObject = list.get(position);

            if (incidentEntityJsonObject != null) {
                if (!incidentEntityJsonObject.isNull("incident")) {
                    JSONObject TypeJsonObject = incidentEntityJsonObject.getJSONObject("incident");
                    System.out.println("TypeJsonObject==" + TypeJsonObject);


                    if (!TypeJsonObject.isNull("incTypeEn_text")) {
                        ((TextView) convertView.findViewById(R.id.incidentType_TV)).setText(TypeJsonObject.getString("incTypeEn_text"));
                    }


                    if (!TypeJsonObject.isNull("date")) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        String incidentdate = TypeJsonObject.getString("date");
                        Date startDate = simpleDateFormat.parse(incidentdate);
                        String hejriStartDate = HejriUtil.chrisToHejri(startDate);
                        ((TextView) convertView.findViewById(R.id.incidentDate_TV)).setText(hejriStartDate);
                    }

                    if (!TypeJsonObject.isNull("incDamageTypeEn_text")) {
                        ((TextView) convertView.findViewById(R.id.incidentDamageType_TV)).setText(TypeJsonObject.getString("incDamageTypeEn_text"));
                    }

                }
            }
        } catch (Exception e) {
            //Log.i(DriverIncidentAdapter.class.toString(), e.getMessage());
            return convertView;
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
