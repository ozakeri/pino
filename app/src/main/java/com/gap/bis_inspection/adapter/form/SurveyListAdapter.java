package com.gap.bis_inspection.adapter.form;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.db.enumtype.SendingStatusEn;
import com.gap.bis_inspection.db.objectmodel.SurveyForm;

import java.util.Date;
import java.util.List;

/**
 * Created by Mohamad Cheraghi on 09/10/2016.
 */
public class SurveyListAdapter extends ArrayAdapter<SurveyForm> {
    private LayoutInflater inflater;
    Date date;

    public SurveyListAdapter(Context context, int resource, List<SurveyForm> surveyFormList) {
        super(context, resource, surveyFormList);
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(final int position, View view, @NonNull ViewGroup parent) {
        view = inflater.inflate(R.layout.survey_list_item, null);

        TextView name;
        ImageView img_status;
        name = (TextView) view.findViewById(R.id.name_VT);
        img_status = (ImageView) view.findViewById(R.id.img_status);
        SurveyForm surveyForm = getItem(position);
        date = new Date();

        if (surveyForm != null) {
            name.setText(surveyForm.getName());
            date = surveyForm.getStartDate();
            //credit.setText(CalendarUtil.convertPersianDateTime(date, "yyyy/MM/dd"));

            if (surveyForm.getStatusEn().equals(0)) {
                img_status.setBackgroundResource(R.mipmap.formadd);
            } else if (surveyForm.getStatusEn().equals(1)) {
                img_status.setBackgroundResource(R.mipmap.formdoing);
            } else if (surveyForm.getStatusEn().equals(2)) {
                img_status.setBackgroundResource(R.mipmap.formcomplet);
            } else if (surveyForm.getSendingStatusEn().equals(SendingStatusEn.Sent.ordinal())) {
                img_status.setBackgroundResource(R.mipmap.formsend);
            }

           /* if (surveyForm.getEndDate().compareTo(new Date()) < 0 || surveyForm.getFormStatus().equals(0)) {
                rel.setVisibility(View.GONE);
            }*/
        }
        //}
//surveyForm.setSendingStatusEn(SendingStatusEn.Sent.ordinal()
        return view;
    }
}
