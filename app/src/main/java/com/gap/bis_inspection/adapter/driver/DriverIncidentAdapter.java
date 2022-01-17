package com.gap.bis_inspection.adapter.driver;

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
public class DriverIncidentAdapter extends ArrayAdapter<JSONObject> {
    private List<JSONObject> list;
    private Integer resourceId;

    public DriverIncidentAdapter(Context context, int resourceId,
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
                    JSONObject typeJsonObject = incidentEntityJsonObject.getJSONObject("incident");

                        if (!typeJsonObject.isNull("incTypeEn")) {
                            int type = typeJsonObject.getInt("incTypeEn");
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
                        }else {
                            ((TextView) convertView.findViewById(R.id.incidentType_TV)).setText("---");
                        }

                    if (!typeJsonObject.isNull("date")) {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        String incidentDate = typeJsonObject.getString("date");
                        Date startDate = simpleDateFormat.parse(incidentDate);
                        String hejriStartDate = HejriUtil.chrisToHejri(startDate);
                        ((TextView) convertView.findViewById(R.id.incidentDate_TV)).setText(hejriStartDate);
                    }else {
                        ((TextView) convertView.findViewById(R.id.incidentDate_TV)).setText("---");
                    }
                }

                if (!incidentEntityJsonObject.isNull("incDamageTypeEn")) {
                    int type = incidentEntityJsonObject.getInt("incDamageTypeEn");
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
            Log.i(DriverIncidentAdapter.class.toString(), e.getMessage());
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
