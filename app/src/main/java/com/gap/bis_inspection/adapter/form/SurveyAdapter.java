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
import com.gap.bis_inspection.db.objectmodel.FormQuestionGroup;
import com.gap.bis_inspection.db.objectmodel.SurveyForm;
import com.gap.bis_inspection.db.objectmodel.SurveyFormQuestion;
import com.gap.bis_inspection.db.objectmodel.SurveyFormQuestionTemp;
import com.gap.bis_inspection.service.CoreService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mohamad Cheraghi on 09/10/2016.
 */
public class SurveyAdapter extends RecyclerView.Adapter<SurveyAdapter.MyViewHolder> {
    private Context m_context;
    private SurveyForm surveyForm;
    private ArrayList<String> spinnerDataList;
    private List<SurveyFormQuestion> surveyFormQuestionList;
    private List<FormQuestionGroup> formQuestionGroupList;
    private DatabaseManager databaseManager;
    private CoreService coreService;
    private LinearLayout linearLayout;
    private List<SurveyFormQuestionTemp> formTempList;
    private HashMap<Integer, Boolean> operations = new HashMap<>();

    public SurveyAdapter(Context context, List<SurveyFormQuestion> surveyFormQuestionList, List<FormQuestionGroup> formQuestionGroupList, List<SurveyFormQuestionTemp> formTempList, LinearLayout linearLayout, SurveyForm surveyForm) {
        m_context = context;
        this.surveyForm = surveyForm;
        this.surveyFormQuestionList = surveyFormQuestionList;
        this.formQuestionGroupList = formQuestionGroupList;
        this.linearLayout = linearLayout;
        this.formTempList = formTempList;
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
        databaseManager = new DatabaseManager(m_context);
        coreService = new CoreService(databaseManager);

        if (operations.containsKey(position)) {
            //Do nothing..
        } else {
            //Do operations...
            if (formQuestionGroup != null) {
                holder.txt_groupName.setText(formQuestionGroup.getGroupName());
                surveyFormQuestionList = coreService.getSurveyFormQuestionListByGroupId(formQuestionGroup.getGroupId(), surveyForm.getId());
                formTempList = coreService.getSurveyFormQuestionTempListByGroupId(formQuestionGroup.getGroupId());
            }
                LinearLayoutManager layoutManager = new LinearLayoutManager(m_context, LinearLayoutManager.VERTICAL, false);
                holder.recyclerView.setHasFixedSize(true);
                holder.recyclerView.setLayoutManager(layoutManager);
                SurveyChildAdapter adapter = new SurveyChildAdapter(m_context, surveyFormQuestionList, formTempList, surveyForm, linearLayout);
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

        public MyViewHolder(View itemView) {
            super(itemView);
            description = (TextView) itemView.findViewById(R.id.description_VT);
            txt_groupName = (TextView) itemView.findViewById(R.id.txt_groupName);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.adapterRecyclerView);
        }
    }

}

