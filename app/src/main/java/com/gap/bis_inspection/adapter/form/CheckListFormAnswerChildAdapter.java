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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.db.enumtype.GeneralEnum;
import com.gap.bis_inspection.db.enumtype.SurveyFormStatusEn;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.objectmodel.FormAnswer;
import com.gap.bis_inspection.db.objectmodel.FormItemAnswer;
import com.gap.bis_inspection.db.objectmodel.FormTemp;
import com.gap.bis_inspection.service.CoreService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Mohamad Cheraghi on 7/16/2017.
 */

public class CheckListFormAnswerChildAdapter extends RecyclerView.Adapter<CheckListFormAnswerChildAdapter.MyViewHolder> {
    Context context;
    //Form form;
    private List<FormItemAnswer> formItemAnswerList;
    private List<FormTemp> formTempList;
    private LinearLayout linearLayout;
    private FormAnswer formAnswer;
    private DatabaseManager databaseManager;
    private CoreService coreService;
    private ArrayList<String> spinnerDataList;

    public CheckListFormAnswerChildAdapter(Context context, List<FormItemAnswer> formItemAnswerList, List<FormTemp> formTempList, LinearLayout linearLayout, FormAnswer formAnswer) {
        this.context = context;
        this.formItemAnswerList = formItemAnswerList;
        this.formTempList = formTempList;
        this.linearLayout = linearLayout;
        this.formAnswer = formAnswer;
        initSpinnerArrayList();
        //this.form = form;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_list_question_item, parent, false);

        return new MyViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "ResourceType"})
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        FormItemAnswer formItemAnswers = formItemAnswerList.get(position);
        final FormTemp formTemp = formTempList.get(position);
        databaseManager = new DatabaseManager(context);
        coreService = new CoreService(databaseManager);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.text_spinner, spinnerDataList);
        holder.spinner.setAdapter(adapter);

        if (formItemAnswers != null) {

            System.out.println("formItemAnswers.getAnswerTypeEn()==" + formItemAnswers.getAnswerTypeEn());
            System.out.println("formItemAnswers.getAnswerStr()==" + formItemAnswers.getAnswerStr());

            if (formItemAnswers.getAnswerTypeEn().equals(GeneralEnum.Val1.ordinal())) {
                System.out.println("=====0000======");
                linearLayout.setVisibility(View.VISIBLE);
                holder.linearLayout.setVisibility(View.VISIBLE);
                holder.leftLayoutSpinner.setVisibility(View.VISIBLE);
                holder.leftLayout.setVisibility(View.VISIBLE);
                holder.layout_type1.setVisibility(View.GONE);
                holder.leftLayoutRadioButton.setVisibility(View.GONE);
                holder.Question_VT.setText(formItemAnswers.getQuestion());
                holder.count_VT.setText(String.valueOf(position + 1));

            } else if (formItemAnswers.getAnswerTypeEn().equals(GeneralEnum.Val2.ordinal())) {
                System.out.println("=====111======");
                linearLayout.setVisibility(View.GONE);
                holder.linearLayout.setVisibility(View.GONE);
                holder.layout_type1.setVisibility(View.VISIBLE);
                holder.description.setText(formItemAnswers.getQuestion());
                holder.answer.setText(formItemAnswers.getAnswerStr());
                holder.count1_VT.setText(String.valueOf(position + 1));

            } else if (formItemAnswers.getAnswerTypeEn().equals(GeneralEnum.Val3.ordinal())) {
                System.out.println("=====222======");
                linearLayout.setVisibility(View.VISIBLE);
                holder.linearLayout.setVisibility(View.VISIBLE);
                holder.leftLayoutRadioButton.setVisibility(View.VISIBLE);
                holder.layout_type1.setVisibility(View.GONE);
                holder.leftLayoutSpinner.setVisibility(View.GONE);
                System.out.println("getInputValuesDefault=" + formItemAnswers.getInputValuesDefault());
                createDynamicRadioGroup(holder, formItemAnswers, formTemp);
                holder.Question_VT.setText(formItemAnswers.getQuestion());
                holder.count_VT.setText(String.valueOf(position + 1));

            } else if (formItemAnswers.getAnswerTypeEn().equals(GeneralEnum.Val4.ordinal())) {
                System.out.println("=====333======");
                linearLayout.setVisibility(View.VISIBLE);
                holder.linearLayout.setVisibility(View.VISIBLE);
                holder.leftLayoutRadioButton.setVisibility(View.VISIBLE);
                holder.layout_type1.setVisibility(View.GONE);
                holder.leftLayoutSpinner.setVisibility(View.GONE);
                createDynamicRadioGroup(holder, formItemAnswers, formTemp);
                holder.Question_VT.setText(formItemAnswers.getQuestion());
                holder.count_VT.setText(String.valueOf(position + 1));
            }
        }

        for (int i = 0; i < spinnerDataList.size(); i++) {
            String value = spinnerDataList.get(i);
            if (value.equals(String.valueOf(formItemAnswers.getAnswerInt()))) {
                holder.spinner.setSelection(i);
                break;
            }
        }

        if (formAnswer.getStatusEn().equals(SurveyFormStatusEn.Complete.ordinal())) {
            holder.spinner.setEnabled(false);
            holder.answer.setEnabled(false);
        }

        /*holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedValue = holder.spinner.getSelectedItem().toString();
                formTemp.setAnswerInt(Integer.valueOf(selectedValue));
                coreService.updateFormTemp(formTemp);
               *//* if (!selectedValue.isEmpty()) {
                    formItemAnswerList.get(holder.ref).setAnswerInt(Integer.valueOf(selectedValue));
                } else {
                    formItemAnswerList.get(holder.ref).setAnswerInt(null);
                }*//*
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });*/

        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedValue = holder.spinner.getSelectedItem().toString();
                if (!selectedValue.isEmpty()) {
                    formItemAnswerList.get(holder.ref).setAnswerInt(Integer.valueOf(selectedValue));
                    formTemp.setAnswerInt(Integer.valueOf(selectedValue));
                    coreService.updateFormTemp(formTemp);
                } else {
                    formItemAnswerList.get(holder.ref).setAnswerInt(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        holder.answer.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                //formTemp.setAnswerStr("onTextChanged");
                //coreService.updateFormTemp(formTemp);
                //System.out.println("getAnswerStr1=="+formTemp.getAnswerStr());
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                formTemp.setAnswerStr(arg0.toString());
                coreService.updateFormTemp(formTemp);
                System.out.println("getAnswerStr2==" + formTemp.getAnswerStr());
                  /*  if (arg0 != null) {
                        surveyFormQuestionList.get(holder.ref).setAnswerStr(arg0.toString());
                    } else {
                        surveyFormQuestionList.get(holder.ref).setAnswerStr(null);
                    }*/
            }
        });
    }

    private void initSpinnerArrayList() {
        spinnerDataList = new ArrayList<String>();
        //spinnerDataList.add("");
        if (formAnswer != null) {
            for (int i = formAnswer.getMinScore(); i <= formAnswer.getMaxScore(); i++) {
                spinnerDataList.add(Integer.valueOf(i).toString());
            }
        }
    }

    @SuppressLint("ResourceType")
    private void createDynamicRadioGroup(MyViewHolder holder, final FormItemAnswer formItemAnswers, final FormTemp formTemp) {
        RadioGroup radioGroup = new RadioGroup(context);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        holder.leftLayoutRadioButton.addView(radioGroup, p);
        try {
            if (formItemAnswers.getInputValuesDefault() != null) {
                System.out.println("getInputValuesDefault===" + formItemAnswers.getInputValuesDefault());
                JSONObject inputValuesDefaultJson = new JSONObject(formItemAnswers.getInputValuesDefault());
                Iterator<String> iterator = inputValuesDefaultJson.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    if (inputValuesDefaultJson.get(key) instanceof JSONObject) {
                        System.out.println("-----key=  " + key + "  ---values=  " + inputValuesDefaultJson.get(key));
                        //String getDefault = inputValuesDefaultJson.getString("default");
                        //<RadioButton style="@android:style/Widget.CompoundButton.CheckBox" />

                        JSONObject xx = new JSONObject(inputValuesDefaultJson.get(key).toString());
                        Iterator<String> iteratorX = xx.keys();
                        while (iteratorX.hasNext()) {
                            String keyX = iteratorX.next();
                            System.out.println("-----keyX=  " + keyX + "  ---valuesX=  " + xx.get(keyX));
                            String style = "@android:style/Widget.CompoundButton.CheckBox";
                            final RadioButton radioButtonView = new RadioButton(context);
                            radioButtonView.setText(xx.get(keyX).toString());
                            System.out.println("setText==" + xx.get(keyX).toString());
                            radioButtonView.setTextColor(R.color.black);
                            radioButtonView.setId(Integer.parseInt(keyX));
                            radioButtonView.setButtonDrawable(R.drawable.check_box);
                            radioButtonView.setOnClickListener(mThisButtonListener);

                            radioButtonView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    int id = ((RadioButton) view).getId();
                                    if (formTemp != null) {
                                        formTemp.setAnswerInt(id);
                                        coreService.updateFormTemp(formTemp);
                                    }
                                }
                            });

                            if (formItemAnswers.getAnswerInt() != null) {
                                if (formItemAnswers.getAnswerInt().equals(radioButtonView.getId())) {
                                    radioButtonView.setChecked(true);
                                } else {
                                    radioButtonView.setChecked(false);
                                }
                            }

                            if (formAnswer.getStatusEn().equals(SurveyFormStatusEn.Complete.ordinal())) {
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

    private View.OnClickListener mThisButtonListener = new View.OnClickListener() {
        public void onClick(View v) {

        }
    };

    @Override
    public int getItemCount() {
        return formItemAnswerList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView Question_VT, count_VT, count1_VT;
        EditText answer;
        TextView description;
        LinearLayout linearLayout, leftLayoutRadioButton, leftLayoutSpinner, leftLayout;
        RelativeLayout layout_type1;
        Spinner spinner;
        int ref;

        public MyViewHolder(View itemView) {
            super(itemView);
            Question_VT = (TextView) itemView.findViewById(R.id.Question_VT);//change Here
            count_VT = (TextView) itemView.findViewById(R.id.count_VT);//change Here
            count1_VT = (TextView) itemView.findViewById(R.id.count1_VT);//change Here
            answer = (EditText) itemView.findViewById(R.id.answer_ET);
            description = (TextView) itemView.findViewById(R.id.description_VT);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
            leftLayoutRadioButton = (LinearLayout) itemView.findViewById(R.id.leftLayoutRadioButton);
            leftLayoutSpinner = (LinearLayout) itemView.findViewById(R.id.leftLayoutSpinner);
            layout_type1 = (RelativeLayout) itemView.findViewById(R.id.layout_type1);
            spinner = (Spinner) itemView.findViewById(R.id.Spinner_SP);
            leftLayout = (LinearLayout) itemView.findViewById(R.id.leftLayout);
        }
    }
}