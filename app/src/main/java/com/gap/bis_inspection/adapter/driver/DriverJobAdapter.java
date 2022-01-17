package com.gap.bis_inspection.adapter.driver;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gap.bis_inspection.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Mohamad Cheraghi on 08/25/2016.
 */
public class DriverJobAdapter extends ArrayAdapter<JSONObject> {
    private List<JSONObject> list;
    private Integer resourceId;
    private String shiftTypeEn, timeFrom, timeTo = null;

    public DriverJobAdapter(Context context, int resourceId,
                            List<JSONObject> list) {
        super(context, resourceId, list);
        this.list = list;
        this.resourceId = resourceId;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            }
            JSONObject driverJobListJsonObject = list.get(position);
            if (driverJobListJsonObject != null) {

                if (!driverJobListJsonObject.isNull("type")) {
                    ((LinearLayout) convertView.findViewById(R.id.typeDriver_layout)).setVisibility(View.VISIBLE);

                    int driverType = driverJobListJsonObject.getInt("type");
                    switch (driverType) {
                        case 0:
                            ((TextView) convertView.findViewById(R.id.type_TV)).setText(R.string.enumType_DriverType_MainDriver);
                            break;
                        case 1:
                            ((TextView) convertView.findViewById(R.id.type_TV)).setText(R.string.enumType_DriverType_AssistantDriver);
                            break;
                        case 2:
                            ((TextView) convertView.findViewById(R.id.type_TV)).setText(R.string.enumType_DriverType_OrganizationDriver);
                            break;
                    }
                } else {
                    ((TextView) convertView.findViewById(R.id.type_TV)).setText("---");
                }

                if (!driverJobListJsonObject.isNull("driverJobTypeEn")) {
                    int type = driverJobListJsonObject.getInt("driverJobTypeEn");
                    switch (type) {
                        case 0:
                            ((TextView) convertView.findViewById(R.id.driverJobTypeEn_TV)).setText(R.string.enumType_DriverJobTypeEn_DetermineCarForDriver);
                            break;
                        case 1:
                            ((TextView) convertView.findViewById(R.id.driverJobTypeEn_TV)).setText(R.string.enumType_DriverJobTypeEn_RotatoryDriverInLine);
                            break;
                        case 2:
                            ((TextView) convertView.findViewById(R.id.driverJobTypeEn_TV)).setText(R.string.enumType_DriverJobTypeEn_DriverInParking);
                            break;
                        case 3:
                            ((TextView) convertView.findViewById(R.id.driverJobTypeEn_TV)).setText(R.string.enumType_DriverJobTypeEn_RescuerSOS);
                            break;
                        case 4:
                            ((TextView) convertView.findViewById(R.id.driverJobTypeEn_TV)).setText(R.string.enumType_DriverJobTypeEn_AssistantRescuerSOS);
                            break;
                        case 5:
                            ((TextView) convertView.findViewById(R.id.driverJobTypeEn_TV)).setText(R.string.enumType_DriverJobTypeEn_WorkOnContract);
                            break;
                    }
                } else {
                    ((TextView) convertView.findViewById(R.id.driverJobTypeEn_TV)).setText("---");
                }

                if (!driverJobListJsonObject.isNull("lineCompany")) {
                    ((LinearLayout) convertView.findViewById(R.id.line_layout)).setVisibility(View.VISIBLE);
                    JSONObject lineCompanyJsonObject = driverJobListJsonObject.getJSONObject("lineCompany");
                    if (!lineCompanyJsonObject.isNull("line")) {
                        JSONObject lineJsonObject = lineCompanyJsonObject.getJSONObject("line");
                        ((TextView) convertView.findViewById(R.id.lineNameFvTV)).setText(lineJsonObject.getString("nameFv"));
                    }
                }

                if (!driverJobListJsonObject.isNull("car")) {
                    ((LinearLayout) convertView.findViewById(R.id.car_layout)).setVisibility(View.VISIBLE);
                    JSONObject driverJsonObject = driverJobListJsonObject.getJSONObject("car");
                    String plateText = driverJsonObject.getString("plateText");

                    String[] split = plateText.split("-");
                    String firstSubString = split[0];
                    String secondSubString = split[1];
                    String plate = secondSubString + " - " + firstSubString;

                    ((TextView) convertView.findViewById(R.id.plateText_TV)).setText(plate);
                } else {
                    ((TextView) convertView.findViewById(R.id.plateText_TV)).setText("---");
                }


                if (!driverJobListJsonObject.isNull("entityShift")) {
                    JSONObject entityShiftJsonObject = driverJobListJsonObject.getJSONObject("entityShift");
                    if (!entityShiftJsonObject.isNull("shift")) {
                        JSONObject shiftJsonObject = entityShiftJsonObject.getJSONObject("shift");
                        //shiftLayout.setVisibility(View.VISIBLE);
                        int type = shiftJsonObject.getInt("shiftTypeEn");
                        switch (type) {
                            case 0:
                                shiftTypeEn = getContext().getResources().getString(R.string.enumType_ShiftType_Morning);
                                break;
                            case 1:
                                shiftTypeEn = getContext().getResources().getString(R.string.enumType_ShiftType_Afternoon);
                                break;
                            case 2:
                                shiftTypeEn = getContext().getResources().getString(R.string.enumType_ShiftType_Night);
                                break;
                            case 3:
                                shiftTypeEn = getContext().getResources().getString(R.string.enumType_ShiftType_Other);
                                break;
                            case 4:
                                shiftTypeEn = getContext().getResources().getString(R.string.enumType_ShiftType_General);
                                break;
                            case 5:
                                shiftTypeEn = getContext().getResources().getString(R.string.enumType_ShiftType_MidDay);
                                break;
                        }
                    }

                    if (!entityShiftJsonObject.isNull("timeTo")) {
                        timeTo = entityShiftJsonObject.getString("timeTo");
                    }

                    if (!entityShiftJsonObject.isNull("timeFrom")) {
                        timeFrom = entityShiftJsonObject.getString("timeFrom");
                    }
                    ((LinearLayout) convertView.findViewById(R.id.job_shift_layout)).setVisibility(View.VISIBLE);
                    ((TextView) convertView.findViewById(R.id.shift_TV)).setText(shiftTypeEn + " " + getContext().getResources().getString(R.string.label_timeFrom) + timeFrom + getContext().getResources().getString(R.string.label_timeTo) + timeTo);
                }

            }
        } catch (Exception e) {
            System.out.println(e);
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
