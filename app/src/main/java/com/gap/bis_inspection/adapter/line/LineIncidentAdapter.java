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
import com.gap.bis_inspection.common.HejriUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class LineIncidentAdapter extends ArrayAdapter<JSONObject> {
    private List<JSONObject> list;
    private Integer resourceId;

    public LineIncidentAdapter(Context context, int resourceId,
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
            String requestDescription = null;
            final JSONObject incidentJsonObject = list.get(position);
            if (incidentJsonObject != null) {

                if (!incidentJsonObject.isNull("incTypeEn")) {
                    int type = incidentJsonObject.getInt("incTypeEn");
                    switch (type) {
                        case 0:
                            ((TextView) convertView.findViewById(R.id.incidentType_TV)).setText(R.string.enumType_IncidentTypeEn_Crash);
                            break;
                        case 1:
                            ((TextView) convertView.findViewById(R.id.incidentType_TV)).setText(R.string.enumType_IncidentTypeEn_Conflict);
                            break;
                        case 2:
                            ((TextView) convertView.findViewById(R.id.incidentType_TV)).setText(R.string.enumType_IncidentTypeEn_NaturalDisasters);
                            break;
                        case 3:
                            ((TextView) convertView.findViewById(R.id.incidentType_TV)).setText(R.string.enumType_IncidentTypeEn_Riot);
                            break;
                        case 4:
                            ((TextView) convertView.findViewById(R.id.incidentType_TV)).setText(R.string.enumType_IncidentTypeEn_Theft);
                            break;
                        case 5:
                            ((TextView) convertView.findViewById(R.id.incidentType_TV)).setText(R.string.enumType_IncidentTypeEn_TechnicalDamages);
                            break;
                        case 6:
                            ((TextView) convertView.findViewById(R.id.incidentType_TV)).setText(R.string.enumType_IncidentTypeEn_Fires);
                            break;
                        case 7:
                            ((TextView) convertView.findViewById(R.id.incidentType_TV)).setText(R.string.enumType_IncidentTypeEn_Lost);
                            break;
                        case 8:
                            ((TextView) convertView.findViewById(R.id.incidentType_TV)).setText(R.string.enumType_IncidentTypeEn_BusInternalIncidents);
                            break;
                        case 9:
                            ((TextView) convertView.findViewById(R.id.incidentType_TV)).setText(R.string.enumType_IncidentTypeEn_DisorderInLine);
                            break;
                        case 10:
                            ((TextView) convertView.findViewById(R.id.incidentType_TV)).setText(R.string.enumType_IncidentTypeEn_BusBodyIncidents);
                            break;
                        case 11:
                            ((TextView) convertView.findViewById(R.id.incidentType_TV)).setText(R.string.enumType_IncidentTypeEn_LineReport);
                            break;

                    }
                } else {
                    ((TextView) convertView.findViewById(R.id.incidentType_TV)).setText("---");
                }

                if (!incidentJsonObject.isNull("date")) {
                    requestDescription = incidentJsonObject.getString("date");
                    System.out.println("requestDescription1==" + requestDescription);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date startDate = simpleDateFormat.parse(requestDescription);
                    String hejriStartDate = HejriUtil.chrisToHejri(startDate);
                    ((TextView) convertView.findViewById(R.id.incidentDate_TV)).setText(hejriStartDate);
                } else {
                    ((TextView) convertView.findViewById(R.id.incidentDate_TV)).setText("---");
                }

                if (!incidentJsonObject.isNull("incDamageTypeEn")) {
                    int type = incidentJsonObject.getInt("incDamageTypeEn");
                    switch (type) {
                        case 0:
                            ((TextView) convertView.findViewById(R.id.incidentDamageType_TV)).setText(R.string.enumType_IncidentDamageTypeEn_Damaging);
                            break;
                        case 1:
                            ((TextView) convertView.findViewById(R.id.incidentDamageType_TV)).setText(R.string.enumType_IncidentDamageTypeEn_Injury);
                            break;
                        case 2:
                            ((TextView) convertView.findViewById(R.id.incidentDamageType_TV)).setText(R.string.enumType_IncidentDamageTypeEn_Death);
                            break;
                    }
                }else {
                    ((TextView) convertView.findViewById(R.id.incidentDamageType_TV)).setText("---");
                }
            }

        } catch (Exception e) {
            return convertView;
        }

        if (position % 2 == 1) {
            convertView.setBackgroundColor(Color.parseColor("#d2eaf1"));
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
