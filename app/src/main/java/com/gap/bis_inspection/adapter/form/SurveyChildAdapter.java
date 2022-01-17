package com.gap.bis_inspection.adapter.form;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.db.enumtype.GeneralEnum;
import com.gap.bis_inspection.db.enumtype.SurveyFormStatusEn;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.objectmodel.SurveyForm;
import com.gap.bis_inspection.db.objectmodel.SurveyFormQuestion;
import com.gap.bis_inspection.db.objectmodel.SurveyFormQuestionTemp;
import com.gap.bis_inspection.service.CoreService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Mohamad Cheraghi on 09/10/2016.
 */
public class SurveyChildAdapter extends RecyclerView.Adapter<SurveyChildAdapter.MyViewHolder> {
    private Context m_context;
    private SurveyForm surveyForm;
    private ArrayList<String> spinnerDataList;
    private List<SurveyFormQuestion> surveyFormQuestionList;
    private LinearLayout linearLayout;
    private CoreService coreService;
    private DatabaseManager databaseManager;
    private List<SurveyFormQuestionTemp> surveyFormQuestionTemps;

    public SurveyChildAdapter(Context context, List<SurveyFormQuestion> surveyFormQuestionList, List<SurveyFormQuestionTemp> surveyFormQuestionTemps, SurveyForm surveyForm, LinearLayout linearLayout) {
        m_context = context;
        this.surveyForm = surveyForm;
        this.surveyFormQuestionList = surveyFormQuestionList;
        this.linearLayout = linearLayout;
        this.surveyFormQuestionTemps = surveyFormQuestionTemps;
        initSpinnerArrayList();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.survey_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final SurveyFormQuestionTemp surveyFormQuestionTemp = surveyFormQuestionTemps.get(position);

        databaseManager = new DatabaseManager(m_context);
        coreService = new CoreService(databaseManager);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(m_context, R.layout.text_spinner, spinnerDataList);
        holder.spinner.setAdapter(adapter);

        holder.ref = position;

        SurveyFormQuestion surveyFormQuestion = surveyFormQuestionList.get(position);

        if (surveyFormQuestion != null) {
            System.out.println("surveyFormQuestion.getAnswerTypeEn()==" + surveyFormQuestion.getAnswerTypeEn());

            if (surveyFormQuestion.getAnswerTypeEn().equals(GeneralEnum.Val1.ordinal())) {
                linearLayout.setVisibility(View.VISIBLE);
                holder.linearLayout.setVisibility(View.VISIBLE);
                holder.leftLayout.setVisibility(View.VISIBLE);
                holder.leftLayoutSpinner.setVisibility(View.VISIBLE);
                holder.layout_type1.setVisibility(View.GONE);
                holder.leftLayoutRadioButton.setVisibility(View.GONE);
                holder.Question_VT.setText(surveyFormQuestion.getQuestion());
                holder.count_VT.setText(String.valueOf(position + 1));

            } else if (surveyFormQuestion.getAnswerTypeEn().equals(GeneralEnum.Val2.ordinal())) {
                linearLayout.setVisibility(View.GONE);
                holder.linearLayout.setVisibility(View.GONE);
                holder.layout_type1.setVisibility(View.VISIBLE);
                holder.description.setText(surveyFormQuestion.getQuestion());
                holder.answer.setText(surveyFormQuestion.getAnswerStr());


            } else if (surveyFormQuestion.getAnswerTypeEn().equals(GeneralEnum.Val3.ordinal())) {
                linearLayout.setVisibility(View.VISIBLE);
                holder.linearLayout.setVisibility(View.VISIBLE);
                holder.leftLayoutRadioButton.setVisibility(View.VISIBLE);
                holder.leftLayout.setVisibility(View.VISIBLE);
                holder.layout_type1.setVisibility(View.GONE);
                holder.leftLayoutSpinner.setVisibility(View.GONE);
                createDynamicRadioGroup(holder, surveyFormQuestion, surveyFormQuestionTemp);
                holder.Question_VT.setText(surveyFormQuestion.getQuestion());
                holder.count_VT.setText(String.valueOf(position + 1));

            } else if (surveyFormQuestion.getAnswerTypeEn().equals(GeneralEnum.Val4.ordinal())) {
                System.out.println("GeneralEnum.Val4" + surveyFormQuestion.getInputValuesDefault());
                linearLayout.setVisibility(View.VISIBLE);
                holder.linearLayout.setVisibility(View.VISIBLE);
                holder.leftLayoutRadioButton.setVisibility(View.VISIBLE);
                holder.leftLayout.setVisibility(View.VISIBLE);
                holder.layout_type1.setVisibility(View.GONE);
                holder.leftLayoutSpinner.setVisibility(View.GONE);
                createDynamicRadioGroup(holder, surveyFormQuestion, surveyFormQuestionTemp);
                holder.Question_VT.setText(surveyFormQuestion.getQuestion());
                holder.count_VT.setText(String.valueOf(position + 1));

            }

            for (int i = 0; i < spinnerDataList.size(); i++) {
                String value = spinnerDataList.get(i);
                if (surveyFormQuestion.getAnswerInt() != null) {
                    if (value.equals(surveyFormQuestion.getAnswerInt().toString())) {
                        holder.spinner.setSelection(i);
                        break;
                    }
                }
            }

        }
        if (surveyForm.getStatusEn().equals(SurveyFormStatusEn.Complete.ordinal())) {
            holder.spinner.setEnabled(false);
            holder.answer.setEnabled(false);
        }

        holder.answer.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                surveyFormQuestionTemp.setAnswerStr(arg0.toString());
                coreService.updateSurveyFormQuestionTemp(surveyFormQuestionTemp);
                System.out.println("getAnswerStr2==" + surveyFormQuestionTemp.getAnswerStr());
            }
        });

        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedValue = holder.spinner.getSelectedItem().toString();
                if (!selectedValue.isEmpty()) {
                    surveyFormQuestionList.get(holder.ref).setAnswerInt(Integer.valueOf(selectedValue));
                    surveyFormQuestionTemp.setAnswerInt(Integer.valueOf(selectedValue));
                    coreService.updateSurveyFormQuestionTemp(surveyFormQuestionTemp);
                } else {
                    surveyFormQuestionList.get(holder.ref).setAnswerInt(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        /*if (surveyFormQuestion.getAnswerTypeEn().equals(GeneralEnum.Val1.ordinal())) {
            holder.spinnerSP.setVisibility(View.VISIBLE);
            holder.answerET.setVisibility(View.GONE);
            holder.description.setVisibility(View.GONE);

        } else if (surveyFormQuestion.getAnswerTypeEn().equals(GeneralEnum.Val2.ordinal())) {
            holder.answerET.setVisibility(View.VISIBLE);
            holder.description.setVisibility(View.VISIBLE);
            holder.linearLayout.setVisibility(View.GONE);
        }

        holder.questionTV.setText(surveyFormQuestion.getQuestion());
        holder.countVT.setText(String.valueOf(position + 1));
        holder.description.setText(surveyFormQuestion.getQuestion());

        if (surveyFormQuestion.getAnswerInt() != null) {

            for (int i = 0; i < spinnerDataList.size(); i++) {
                String value = spinnerDataList.get(i);
                if (value.equals(surveyFormQuestion.getAnswerInt().toString())) {
                    holder.spinnerSP.setSelection(i);
                    break;
                }
            }
        } else if (surveyFormQuestion.getAnswerStr() != null) {
            holder.answerET.setText(surveyFormQuestion.getAnswerStr());
        } else {
            holder.spinnerSP.setSelection(0);
            holder.answerET.setText("");
        }

        if (surveyForm.getStatusEn().equals(SurveyFormStatusEn.Complete.ordinal())) {
            holder.spinnerSP.setEnabled(false);
            holder.answerET.setEnabled(false);
        }

        holder.spinnerSP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedValue = holder.spinnerSP.getSelectedItem().toString();
                if (!selectedValue.isEmpty()) {
                    surveyFormQuestionList.get(holder.ref).setAnswerInt(Integer.valueOf(selectedValue));
                } else {
                    surveyFormQuestionList.get(holder.ref).setAnswerInt(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        holder.answerET.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                if (arg0 != null) {
                    surveyFormQuestionList.get(holder.ref).setAnswerStr(arg0.toString());
                } else {
                    surveyFormQuestionList.get(holder.ref).setAnswerStr(null);
                }
            }
        });*/

    }

    @SuppressLint("ResourceAsColor")
    private void createDynamicRadioGroup(MyViewHolder holder, SurveyFormQuestion surveyFormQuestion, final SurveyFormQuestionTemp surveyFormQuestionTemp) {
        RadioGroup radioGroup = new RadioGroup(m_context);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        holder.leftLayoutRadioButton.addView(radioGroup, p);

        //JSONObject inputValuesDefaultJson = null;
        try {
            if (surveyFormQuestion.getInputValuesDefault() != null) {
                System.out.println("getInputValuesDefault===" + surveyFormQuestion.getInputValuesDefault());
                JSONObject inputValuesDefaultJson = new JSONObject(surveyFormQuestion.getInputValuesDefault());
                Iterator<String> iterator = inputValuesDefaultJson.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    if (inputValuesDefaultJson.get(key) instanceof JSONObject) {
                        String getDefault = inputValuesDefaultJson.getString("default");

                        JSONObject xx = new JSONObject(inputValuesDefaultJson.get(key).toString());
                        Iterator<String> iteratorX = xx.keys();
                        while (iteratorX.hasNext()) {
                            String keyX = iteratorX.next();
                            String values = (String) xx.get(keyX);
                            final RadioButton radioButtonView = new RadioButton(m_context);
                            radioButtonView.setText(xx.get(keyX).toString());
                            radioButtonView.setId(Integer.parseInt(keyX));
                            radioButtonView.setButtonDrawable(R.drawable.check_box);
                            radioButtonView.setTextColor(R.color.black);

                           /* if (radioButtonView.getId() == Integer.valueOf(getDefault)) {
                                radioButtonView.setChecked(true);
                            } else {
                                radioButtonView.setChecked(false);
                            }*/

                            radioButtonView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    int id = ((RadioButton) view).getId();
                                    if (surveyFormQuestionTemp != null) {
                                        surveyFormQuestionTemp.setAnswerInt(id);
                                        coreService.updateSurveyFormQuestionTemp(surveyFormQuestionTemp);
                                    }
                                }
                            });

                            if (surveyFormQuestion.getAnswerInt() != null) {
                                if (surveyFormQuestion.getAnswerInt().equals(radioButtonView.getId())) {
                                    radioButtonView.setChecked(true);
                                } else {
                                    radioButtonView.setChecked(false);
                                }
                            }

                            if (surveyForm.getStatusEn().equals(SurveyFormStatusEn.Complete.ordinal())) {
                                radioButtonView.setEnabled(false);
                            }
                            radioGroup.addView(radioButtonView, p);
                        }
                    }

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return surveyFormQuestionList.size();
    }

    public ArrayList<String> initSpinnerArrayList() {
        spinnerDataList = new ArrayList<String>();
        spinnerDataList.add("");
        for (int i = surveyForm.getMinScore(); i <= surveyForm.getMaxScore(); i++) {
            spinnerDataList.add(Integer.valueOf(i).toString());
        }
        return spinnerDataList;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView Question_VT, count_VT;
        EditText answer;
        TextView description;
        LinearLayout linearLayout, leftLayoutRadioButton, leftLayoutSpinner, layout_type1, leftLayout;
        Spinner spinner;
        int ref;

        public MyViewHolder(View itemView) {
            super(itemView);
            Question_VT = (TextView) itemView.findViewById(R.id.Question_VT);//change Here
            count_VT = (TextView) itemView.findViewById(R.id.count_VT);//change Here
            answer = (EditText) itemView.findViewById(R.id.answer_ET);
            description = (TextView) itemView.findViewById(R.id.description_VT);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
            leftLayoutRadioButton = (LinearLayout) itemView.findViewById(R.id.leftLayoutRadioButton);
            leftLayout = (LinearLayout) itemView.findViewById(R.id.leftLayout);
            leftLayoutSpinner = (LinearLayout) itemView.findViewById(R.id.leftLayoutSpinner);
            layout_type1 = (LinearLayout) itemView.findViewById(R.id.layout_type1);
            spinner = (Spinner) itemView.findViewById(R.id.Spinner_SP);
        }
    }

}

