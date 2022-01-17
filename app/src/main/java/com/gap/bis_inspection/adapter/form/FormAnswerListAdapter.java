package com.gap.bis_inspection.adapter.form;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.common.HejriUtil;
import com.gap.bis_inspection.db.enumtype.SendingStatusEn;
import com.gap.bis_inspection.db.objectmodel.FormAnswer;
import com.gap.bis_inspection.db.objectmodel.FormItemAnswer;

import java.util.List;

/**
 * Created by Mohamad Cheraghi on 7/16/2017.
 */

public class FormAnswerListAdapter extends RecyclerView.Adapter<FormAnswerListAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private List<FormAnswer> arrayList;
    Context context;
    private FormAnswer formAnswer;
    private FormItemAnswer formItemAnswer;

    public FormAnswerListAdapter(Context context,List<FormAnswer> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.form_answer_list_item, parent, false);

        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        formAnswer = arrayList.get(position);
        holder.count.setText(String.valueOf(position + 1));
        holder.plateText.setText(String.valueOf(formAnswer.getCarId()));
        holder.visitDate.setText(String.valueOf(HejriUtil.chrisToHejriDateTime(formAnswer.getStatusDate())));
        holder.locateVisit.setText(String.valueOf(formAnswer.getLineId()));

        if (formAnswer.getStatusEn().equals(0)) {
            holder.status.setBackgroundResource(R.mipmap.formadd);
        } else if (formAnswer.getStatusEn().equals(1)) {
            holder.status.setBackgroundResource(R.mipmap.formdoing);
        } else if (formAnswer.getStatusEn().equals(2)) {
            holder.status.setBackgroundResource(R.mipmap.formcomplet);
        } else if (formAnswer.getSendingStatusEn().equals(SendingStatusEn.Sent.ordinal())) {
            holder.status.setBackgroundResource(R.mipmap.formsend);
        }

        /*if (formAnswer.getEndDate().compareTo(new Date()) < 0 || formAnswer.getStatusEn().equals(0)) {
            holder.linearLayout.setVisibility(View.GONE);
        }*/
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView count, plateText, visitDate, locateVisit;
        ImageView status;
        LinearLayout linearLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            count = (TextView) itemView.findViewById(R.id.count_TV);//change Here
            plateText = (TextView) itemView.findViewById(R.id.plateText_TV);//change Here
            visitDate = (TextView) itemView.findViewById(R.id.visitDate_TV);//change Here
            locateVisit = (TextView) itemView.findViewById(R.id.locateVisit_TV);//change Here
            status = (ImageView) itemView.findViewById(R.id.status_Icon);//change Here
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);

        }
    }
}
