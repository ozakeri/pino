package com.gap.bis_inspection.adapter.form;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.db.objectmodel.Form;

import java.util.List;

/**
 * Created by Mohamad Cheraghi on 7/16/2017.
 */

public class CheckListFormAdapter extends RecyclerView.Adapter<CheckListFormAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private List<Form> list;
    Context context;
    private Form form;

    public CheckListFormAdapter(List<Form> list, Context context) {
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_list_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        form = list.get(position);
        holder.formName.setText(form.getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView formName, count_VT;

        public MyViewHolder(View itemView) {
            super(itemView);
            formName = (TextView) itemView.findViewById(R.id.formName_VT);//change Here
            //count_VT = (TextView) itemView.findViewById(R.id.count_VT);//change Here
        }
    }
}
