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
import com.gap.bis_inspection.db.objectmodel.FormAnswer;
import com.gap.bis_inspection.db.objectmodel.FormItemAnswer;
import com.gap.bis_inspection.db.objectmodel.FormQuestionGroup;
import com.gap.bis_inspection.db.objectmodel.FormTemp;
import com.gap.bis_inspection.service.CoreService;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Mohamad Cheraghi on 7/16/2017.
 */

public class CheckListFormAnswerAdapter extends RecyclerView.Adapter<CheckListFormAnswerAdapter.MyViewHolder> {
    Context context;
    private List<FormQuestionGroup> formQuestionGroupList;
    private List<FormItemAnswer> formItemAnswerList;
    private List<FormTemp> formTempList;
    private FormAnswer formAnswer;
    private LinearLayout linearLayout;
    private DatabaseManager databaseManager;
    private CoreService coreService;
    private HashMap<Integer, Boolean> operations = new HashMap<>();


    public CheckListFormAnswerAdapter(Context context, List<FormQuestionGroup> formQuestionGroupList, List<FormItemAnswer> formItemAnswerList, List<FormTemp> formTempList, FormAnswer formAnswer, LinearLayout linearLayout) {
        this.context = context;
        this.formQuestionGroupList = formQuestionGroupList;
        this.formAnswer = formAnswer;
        this.linearLayout = linearLayout;
        this.formItemAnswerList = formItemAnswerList;
        this.formTempList = formTempList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_list_group_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        FormQuestionGroup formQuestionGroup = formQuestionGroupList.get(position);
        databaseManager = new DatabaseManager(context);
        coreService = new CoreService(databaseManager);

        if (operations.containsKey(position)) {
            System.out.println("operations1====");
            //Do nothing..
        } else {
            //Do operations...
            System.out.println("operations2====");
            if (formQuestionGroup != null) {
                formItemAnswerList = coreService.getFormItemAnswerListByGroupId(formQuestionGroup.getGroupId(), formAnswer.getId());
                formTempList = coreService.getFormTempListByGroupId(formQuestionGroup.getGroupId(), formAnswer.getId());
                System.out.println("formTempList.size===" + formTempList.size());
                System.out.println("formItemAnswerList.size===" + formItemAnswerList.size());
                System.out.println("formAnswer.getId===" + formAnswer.getId());
                holder.txt_groupName.setText(formQuestionGroup.getGroupName());
            }

                LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                holder.recyclerView.setHasFixedSize(true);
                holder.recyclerView.setLayoutManager(layoutManager);
                CheckListFormAnswerChildAdapter adapter = new CheckListFormAnswerChildAdapter(context, formItemAnswerList, formTempList, linearLayout, formAnswer);
                holder.recyclerView.setAdapter(adapter);

            operations.put(position, true);
        }
    }

    @Override
    public int getItemCount() {
        return formQuestionGroupList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView description, txt_groupName;
        RecyclerView recyclerView;

        MyViewHolder(View itemView) {
            super(itemView);
            description = (TextView) itemView.findViewById(R.id.description_VT);
            txt_groupName = (TextView) itemView.findViewById(R.id.txt_groupName);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.adapterRecyclerView);

        }
    }
}
