package com.gap.bis_inspection.adapter.form;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.objectmodel.Form;
import com.gap.bis_inspection.db.objectmodel.FormAnswer;
import com.gap.bis_inspection.db.objectmodel.FormQuestion;
import com.gap.bis_inspection.db.objectmodel.FormQuestionGroup;
import com.gap.bis_inspection.db.objectmodel.FormTemp;
import com.gap.bis_inspection.service.CoreService;

import java.util.List;

/**
 * Created by Mohamad Cheraghi on 7/16/2017.
 */

public class CheckListFormQuestionAdapter extends RecyclerView.Adapter<CheckListFormQuestionAdapter.MyViewHolder> {
    Context context;
    FormQuestion formQuestion;
    LinearLayout linearLayout;
    List<FormQuestionGroup> formQuestionGroupList;
    private DatabaseManager databaseManager;
    private CoreService coreService;
    private List<FormTemp> formTempList;
    FormAnswer formAnswer;
    List<FormQuestion> formQuestionList;
    Form form;

    //long formAnswerId;


    public CheckListFormQuestionAdapter(Context context, List<FormQuestionGroup> formQuestionGroupList, List<FormTemp> formTempList, LinearLayout linearLayout, FormAnswer formAnswer, Form form) {
        this.context = context;
        this.formQuestionGroupList = formQuestionGroupList;
        this.linearLayout = linearLayout;
        this.formTempList = formTempList;
        this.formAnswer = formAnswer;
        this.form = form;
        //this.formAnswerId = formAnswerId;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_list_group_item, parent, false);

        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        FormQuestionGroup formQuestionGroup = formQuestionGroupList.get(position);
        databaseManager = new DatabaseManager(context);
        coreService = new CoreService(databaseManager);

        if (formQuestionGroup != null) {
            holder.txt_groupName.setText(formQuestionGroup.getGroupName());
            //arrayList = (ArrayList<FormQuestion>) formQuestionGroup.getFormQuestionList();
            formQuestionList = coreService.getFormQuestionListByGroupId(formQuestionGroup.getGroupId(), form.getId());
            formTempList = coreService.getFormTempListByGroupId(formQuestionGroup.getGroupId(), formAnswer.getId());
        }

        /*if (formQuestionGroup != null) {
            holder.txt_groupName.setText(formQuestionGroup.getGroupName());
            formQuestionList = coreService.getFormQuestionListByGroupId(formQuestionGroup.getId());
            //formTempArrayList = (ArrayList<FormTemp>) formQuestionGroup.getFormTempList();
            formTempArrayList = coreService.getFormTempListById(formQuestionGroup.getFormId());
            //formTempArrayList = coreService.getFormTempListByGroupId(formQuestionGroup.getId(), formAnswer.getId());
        }*/

       /* if (formQuestionGroup != null) {
            holder.txt_groupName.setText(formQuestionGroup.getGroupName());
            formQuestionList = coreService.getFormQuestionListByGroupId(formQuestionGroup.getFormId());
            formTempArrayList = (ArrayList<FormTemp>) formQuestionGroup.getFormTempList();
            //formTempArrayList = coreService.getFormTempListByGroupId(formQuestionGroup.getId(), formAnswer.getId());
        }*/

        if (holder.recyclerView != null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            holder.recyclerView.setHasFixedSize(true);
            holder.recyclerView.setLayoutManager(layoutManager);
            CheckListFormQuestionChildAdapter adapter = new CheckListFormQuestionChildAdapter(context, formQuestionList, formTempList, form, linearLayout);
            adapter.notifyDataSetChanged();
            holder.recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public int getItemCount() {
        return formQuestionGroupList.size();
    }

    public Object getItem(int position) {
        return formQuestionGroupList.get(position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView description, txt_groupName;
        RecyclerView recyclerView;

        public MyViewHolder(View itemView) {
            super(itemView);
            description = (TextView) itemView.findViewById(R.id.description_VT);
            txt_groupName = (TextView) itemView.findViewById(R.id.txt_groupName);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.adapterRecyclerView);

        }
    }
}
