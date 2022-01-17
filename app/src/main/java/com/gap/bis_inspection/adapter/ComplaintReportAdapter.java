package com.gap.bis_inspection.adapter;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.common.HejriUtil;
import com.gap.bis_inspection.db.enumtype.SendingStatusEn;
import com.gap.bis_inspection.db.objectmodel.ComplaintReport;

import java.util.List;

/**
 * Created by Mohamad Cheraghi on 09/10/2016.
 */
public class ComplaintReportAdapter extends ArrayAdapter<ComplaintReport> {
    private LayoutInflater inflater;

    public ComplaintReportAdapter(Context context, int resource, List<ComplaintReport> object) {

        super(context, resource, object);
        inflater = LayoutInflater.from(context);
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @NonNull
    @Override
    public View getView(final int position, View view, @NonNull ViewGroup parent) {
        view = inflater.inflate(R.layout.activity_complaint_report_item, null);
        TextView name = (TextView) view.findViewById(R.id.name_VT);
        TextView reportDate = (TextView) view.findViewById(R.id.reportDate_VT);
        TextView report = (TextView) view.findViewById(R.id.report_VT);
        ImageView img_status = (ImageView) view.findViewById(R.id.img_status);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.rel);
        ComplaintReport complaintReport = getItem(position);

        if (complaintReport != null) {
            name.setText(complaintReport.getDisplayName());
            reportDate.setText(HejriUtil.chrisToHejriDateTime(complaintReport.getReportDate()));
            String reportStrSummary = null;
            if (complaintReport.getReportStr() != null) {
                if (complaintReport.getReportStr().length() > 30) {
                    reportStrSummary = complaintReport.getReportStr().substring(0, 30);
                } else {
                    reportStrSummary = complaintReport.getReportStr();
                }
            }
            report.setText(HejriUtil.chrisToHejriDateTime(complaintReport.getDeliverDate()));

            System.out.println("----------" + complaintReport.getDeliverIs());
            System.out.println("----------" + complaintReport.getSendingStatusEn());

            if (complaintReport.getDeliverIs().equals(true) && complaintReport.getSendingStatusEn().equals(SendingStatusEn.Sent.ordinal())) {
                img_status.setBackgroundResource(R.mipmap.sent);

            } else if (complaintReport.getSendingStatusEn().equals(SendingStatusEn.Pending.ordinal())) {
                img_status.setBackgroundResource(R.mipmap.pending);

            } else if (complaintReport.getDeliverIs().equals(false)) {
                img_status.setBackgroundResource(R.mipmap.faild);
            }
        }

        return view;
    }
}
