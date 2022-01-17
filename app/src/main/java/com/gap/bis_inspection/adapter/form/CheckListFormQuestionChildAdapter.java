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
import com.gap.bis_inspection.db.objectmodel.Form;
import com.gap.bis_inspection.db.objectmodel.FormQuestion;
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

public class CheckListFormQuestionChildAdapter extends RecyclerView.Adapter<CheckListFormQuestionChildAdapter.MyViewHolder> {
    Context context;
    private List<FormQuestion> formQuestionList;
    private List<FormTemp> formTempList;
    private LinearLayout linearLayout;
    private DatabaseManager databaseManager;
    private CoreService coreService;
    private Form form;
    private ArrayList<String> spinnerDataList;
    //long formAnswerId;


    public CheckListFormQuestionChildAdapter(Context context, List<FormQuestion> formQuestionList, List<FormTemp> formTempList, Form form, LinearLayout linearLayout) {
        this.context = context;
        this.formQuestionList = formQuestionList;
        this.linearLayout = linearLayout;
        this.formTempList = formTempList;
        this.form = form;
        initSpinnerArrayList();
        //this.formAnswerId = formAnswerId;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_list_question_item, parent, false);

        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final FormQuestion formQuestion = formQuestionList.get(position);
        final FormTemp formTemp = formTempList.get(position);

        databaseManager = new DatabaseManager(context);
        coreService = new CoreService(databaseManager);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.text_spinner, spinnerDataList);
        holder.spinner.setAdapter(adapter);
        holder.description.setText("");
        holder.spinner.setSelection(1);
        formTemp.setAnswerInt(null);
        formTemp.setAnswerStr(null);
        coreService.updateFormTemp(formTemp);

        if (formQuestion != null) {
            System.out.println("getInputValuesDefault===" + formQuestion.getAnswerTypeEn());

            //==================Val1.SurveyQuestions.answerTypeEn = عددی

            if (formQuestion.getAnswerTypeEn().equals(GeneralEnum.Val1.ordinal())) {
                System.out.println("=====Val1======" + formQuestion.getAnswerTypeEn());
                linearLayout.setVisibility(View.VISIBLE);
                holder.linearLayout.setVisibility(View.VISIBLE);
                holder.leftLayout.setVisibility(View.VISIBLE);
                holder.leftLayoutSpinner.setVisibility(View.VISIBLE);
                holder.layout_type1.setVisibility(View.GONE);
                holder.leftLayoutRadioButton.setVisibility(View.GONE);
                holder.Question_VT.setText(formQuestion.getQuestion());
                holder.count_VT.setText(String.valueOf(position + 1));

                //==================Val2.SurveyQuestions.answerTypeEn = متن

            } else if (formQuestion.getAnswerTypeEn().equals(GeneralEnum.Val2.ordinal())) {
                System.out.println("=====Val2======" + formQuestion.getAnswerTypeEn());
                linearLayout.setVisibility(View.GONE);
                holder.linearLayout.setVisibility(View.GONE);
                holder.layout_type1.setVisibility(View.VISIBLE);
                holder.description.setText(formQuestion.getQuestion());
                holder.answer.setText(formQuestion.getAnswerStr());
                holder.count_VT.setText(String.valueOf(position + 1));
                holder.count1_VT.setText(String.valueOf(position + 1));

                //==================Val3.SurveyQuestions.answerTypeEn = چهار گزینه ای

            } else if (formQuestion.getAnswerTypeEn().equals(GeneralEnum.Val3.ordinal())) {
                System.out.println("=====Val3======" + formQuestion.getAnswerTypeEn());
                linearLayout.setVisibility(View.VISIBLE);
                holder.linearLayout.setVisibility(View.VISIBLE);
                holder.leftLayoutRadioButton.setVisibility(View.VISIBLE);
                holder.leftLayout.setVisibility(View.VISIBLE);
                holder.layout_type1.setVisibility(View.GONE);
                holder.leftLayoutSpinner.setVisibility(View.GONE);
                System.out.println("getInputValuesDefault=" + formQuestion.getInputValuesDefault());
                createDynamicRadioGroup(holder, formQuestion, formTemp);
                holder.Question_VT.setText(formQuestion.getQuestion());
                holder.count_VT.setText(String.valueOf(position + 1));

                //==================Val4.SurveyQuestions.answerTypeEn = چک باکس

            } else if (formQuestion.getAnswerTypeEn().equals(GeneralEnum.Val4.ordinal())) {
                System.out.println("=====Val4======" + formQuestion.getAnswerTypeEn());
                linearLayout.setVisibility(View.VISIBLE);
                holder.linearLayout.setVisibility(View.VISIBLE);
                holder.leftLayoutRadioButton.setVisibility(View.VISIBLE);
                holder.leftLayout.setVisibility(View.VISIBLE);
                holder.layout_type1.setVisibility(View.GONE);
                holder.leftLayoutSpinner.setVisibility(View.GONE);
                createDynamicRadioGroup(holder, formQuestion, formTemp);
                holder.Question_VT.setText(formQuestion.getQuestion());
                holder.count_VT.setText(String.valueOf(position + 1));
            }


            for (int i = 0; i < spinnerDataList.size(); i++) {
                String value = spinnerDataList.get(i);
                if (formQuestion.getAnswerInt() != null) {
                    if (value.equals(formQuestion.getAnswerInt().toString())) {
                        holder.spinner.setSelection(i);
                        break;
                    }
                }
            }

            if (form.getStatusEn().equals(SurveyFormStatusEn.Complete.ordinal())) {
                holder.spinner.setEnabled(false);
                holder.answer.setEnabled(false);
            }

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

            holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String selectedValue = holder.spinner.getSelectedItem().toString();
                    if (!selectedValue.isEmpty()) {
                        formQuestionList.get(holder.ref).setAnswerInt(Integer.valueOf(selectedValue));
                        formTemp.setAnswerInt(Integer.valueOf(selectedValue));
                        coreService.updateFormTemp(formTemp);
                    } else {
                        formQuestionList.get(holder.ref).setAnswerInt(null);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });

            //createDynamicRadioGroup(holder, formQuestion, formTemp);
        }
    }

    private void initSpinnerArrayList() {
        spinnerDataList = new ArrayList<String>();
        spinnerDataList.add("");
        if (form != null) {
            for (int i = form.getMinScore(); i <= form.getMaxScore(); i++) {
                spinnerDataList.add(Integer.valueOf(i).toString());
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    private void createDynamicRadioGroup(MyViewHolder holder, FormQuestion formQuestion, final FormTemp formTemp) {
        RadioGroup radioGroup = new RadioGroup(context);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        holder.leftLayoutRadioButton.addView(radioGroup, p);

        //JSONObject inputValuesDefaultJson = null;
        try {
            if (formQuestion.getInputValuesDefault() != null) {
                System.out.println("getInputValuesDefault===" + formQuestion.getInputValuesDefault());
                JSONObject inputValuesDefaultJson = new JSONObject(formQuestion.getInputValuesDefault());
                Iterator<String> iterator = inputValuesDefaultJson.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    if (inputValuesDefaultJson.get(key) instanceof JSONObject) {
                        System.out.println("-----key=  " + key + "  ---values=  " + inputValuesDefaultJson.get(key));
                        String getDefault = inputValuesDefaultJson.getString("default");
                        System.out.println("getDefault=1==" + getDefault);

                        JSONObject xx = new JSONObject(inputValuesDefaultJson.get(key).toString());
                        Iterator<String> iteratorX = xx.keys();
                        while (iteratorX.hasNext()) {
                            String keyX = iteratorX.next();
                            System.out.println("keyX =" + keyX + "valuesX =" + xx.get(keyX));
                            RadioButton radioButtonView = new RadioButton(context);
                            radioButtonView.setText(xx.get(keyX).toString());
                            radioButtonView.setTextColor(R.color.black);
                            radioButtonView.setId(Integer.parseInt(keyX));

                            radioButtonView.setButtonDrawable(R.drawable.check_box);

                           /* if (radioButtonView.getId() == Integer.valueOf(getDefault)) {
                                radioButtonView.setChecked(true);
                                if (formTemp != null) {
                                    formTemp.setAnswerInt(radioButtonView.getId());
                                    coreService.updateFormTemp(formTemp);
                                    System.out.println("formTemp.getAnswerInt1=" + formTemp.getAnswerInt());
                                }
                            } else {
                                radioButtonView.setChecked(false);
                            }*/

                            radioButtonView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    int id = ((RadioButton) view).getId();
                                    System.out.println("id=" + id);
                                    if (formTemp != null) {
                                        formTemp.setAnswerInt(id);
                                        coreService.updateFormTemp(formTemp);
                                    }
                                }
                            });

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
        return formQuestionList.size();
    }

    public Object getItem(int position) {
        return formQuestionList.get(position);
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
            leftLayout = (LinearLayout) itemView.findViewById(R.id.leftLayout);
            leftLayoutSpinner = (LinearLayout) itemView.findViewById(R.id.leftLayoutSpinner);
            layout_type1 = (RelativeLayout) itemView.findViewById(R.id.layout_type1);
            spinner = (Spinner) itemView.findViewById(R.id.Spinner_SP);

        }
    }
}
