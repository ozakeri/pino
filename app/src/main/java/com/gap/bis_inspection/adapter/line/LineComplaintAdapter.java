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
import com.gap.bis_inspection.adapter.ViolationListAdapter;
import com.gap.bis_inspection.common.HejriUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Mohamad Cheraghi on 08/29/2016.
 */
public class LineComplaintAdapter extends ArrayAdapter<JSONObject> {
    private List<JSONObject> list;
    private Integer resourceId;

    public LineComplaintAdapter(Context context, int resourceId,
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
            final JSONObject complaintListJsonObject = list.get(position);
            String requestDescription = null;
            if (complaintListJsonObject != null) {
                if (!complaintListJsonObject.isNull("requestType_text")) {
                    requestDescription = complaintListJsonObject.getString("requestType_text");
                   // ((TextView) convertView.findViewById(R.id.violationtcode)).setText(requestDescription);
                }

                if (!complaintListJsonObject.isNull("requestDescription")) {
                    requestDescription = complaintListJsonObject.getString("requestDescription");
                    //((TextView) convertView.findViewById(R.id.violationtDate)).setText(requestDescription);
                }

                if (!complaintListJsonObject.isNull("requestDate")) {

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    String commplaintdate = complaintListJsonObject.getString("requestDate");
                    Date startDate = simpleDateFormat.parse(commplaintdate);
                    String hejriStartDate = HejriUtil.chrisToHejri(startDate);
                   // ((TextView) convertView.findViewById(R.id.violationtext)).setText(hejriStartDate);
                }
            }
        } catch (Exception e) {
            Log.i(ViolationListAdapter.class.toString(), e.getMessage());
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

