package com.gap.bis_inspection.activity.driver;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.common.HejriUtil;
import com.gap.bis_inspection.webservice.GetJsonService;

import org.json.JSONException;
import org.json.JSONObject;

public class DriverJobDetailActivity extends AppCompatActivity {
    private String result;
    private String jsonData;
    ImageView backIcon;
    LinearLayout shiftLayout;
    TextView typeTV, typeEnTV, driverNameFvTV, mobileNoTV, carNameFvTV, lineNameFvTV, providerCompanyTV, shiftTypeEnTV, startDateTV, finishDateTV;
    String shiftTypeEn, timeFrom, timeTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.detail_driverjob_list_activity_);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            jsonData = bundle.getString("driverJobList");
            init();
            new ResultTask().execute();
        }

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void init() {
        typeTV = (TextView) findViewById(R.id.type_TV);
        typeEnTV = (TextView) findViewById(R.id.typeEn_TV);
        lineNameFvTV = (TextView) findViewById(R.id.lineNameFv_TV);
        providerCompanyTV = (TextView) findViewById(R.id.providerCompany_TV);
        startDateTV = (TextView) findViewById(R.id.startDate_TV);
        finishDateTV = (TextView) findViewById(R.id.finishDate_TV);
        shiftTypeEnTV = (TextView) findViewById(R.id.shiftTypeEn_TV);
        carNameFvTV = (TextView) findViewById(R.id.carNameFv_TV);
        mobileNoTV = (TextView) findViewById(R.id.mobileNo_TV);
        driverNameFvTV = (TextView) findViewById(R.id.driverNameFv_TV);
        backIcon = (ImageView) findViewById(R.id.backIcon);
        shiftLayout = (LinearLayout) findViewById(R.id.rel_shift);
    }

    ////******get driverJobList from bundle*******////
    private class ResultTask extends AsyncTask<Void, Void, Void> {

        ResultTask() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            GetJsonService getJson = new GetJsonService();
            result = getJson.JsonReguest("");
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            result = jsonData;
            if (result != null) {
                logLargeString(result);
                try {
                    JSONObject resultJson = new JSONObject(result);

                    if (!resultJson.isNull("car")) {
                        JSONObject carJsonObject = resultJson.getJSONObject("car");
                        carNameFvTV.setText(carJsonObject.getString("nameFv"));
                    } else {
                        carNameFvTV.setText("---");
                    }

                    if (!resultJson.isNull("driver")) {
                        JSONObject driverJsonObject = resultJson.getJSONObject("driver");
                        String driverCode = driverJsonObject.getString("driverCode");
                        if (!driverJsonObject.isNull("person")) {
                            String nameFv = "";
                            JSONObject personJsonObject = driverJsonObject.getJSONObject("person");
                            nameFv = personJsonObject.getString("nameFv");
                            if (!personJsonObject.isNull("address")) {
                                JSONObject addressJsonObject = personJsonObject.getJSONObject("address");
                                if (!addressJsonObject.isNull("mobileNo")) {
                                    mobileNoTV.setText(addressJsonObject.getString("mobileNo"));
                                }
                            }
                            driverNameFvTV.setText(nameFv + " - " + driverCode);
                        }
                    } else {
                        mobileNoTV.setText("---");
                        driverNameFvTV.setText("---");
                    }

                    if (!resultJson.isNull("type")) {
                        int driverType = resultJson.getInt("type");
                        switch (driverType) {
                            case 0:
                                typeTV.setText(R.string.enumType_DriverType_MainDriver);
                                break;
                            case 1:
                                typeTV.setText(R.string.enumType_DriverType_AssistantDriver);
                                break;
                            case 2:
                                typeTV.setText(R.string.enumType_DriverType_OrganizationDriver);
                                break;
                        }
                    } else {
                        typeTV.setText("---");
                    }

                    if (!resultJson.isNull("driverJobTypeEn")) {
                        int type = resultJson.getInt("driverJobTypeEn");
                        switch (type) {
                            case 0:
                                typeEnTV.setText(R.string.enumType_DriverJobTypeEn_DetermineCarForDriver);
                                break;
                            case 1:
                                typeEnTV.setText(R.string.enumType_DriverJobTypeEn_RotatoryDriverInLine);
                                break;
                            case 2:
                                typeEnTV.setText(R.string.enumType_DriverJobTypeEn_DriverInParking);
                                break;
                            case 3:
                                typeEnTV.setText(R.string.enumType_DriverJobTypeEn_RescuerSOS);
                                break;
                            case 4:
                                typeEnTV.setText(R.string.enumType_DriverJobTypeEn_AssistantRescuerSOS);
                                break;
                            case 5:
                                typeEnTV.setText(R.string.enumType_DriverJobTypeEn_WorkOnContract);
                                break;
                        }
                    } else {
                        typeEnTV.setText("---");
                    }

                    if (!resultJson.isNull("lineCompany")) {
                        JSONObject lineCompanyJsonObject = resultJson.getJSONObject("lineCompany");
                        if (!lineCompanyJsonObject.isNull("line")) {
                            JSONObject lineJsonObject = lineCompanyJsonObject.getJSONObject("line");
                            lineNameFvTV.setText(lineJsonObject.getString("nameFv"));
                        }
                    } else {
                        lineNameFvTV.setText("---");
                    }

                    if (!resultJson.isNull("providerCompany")) {
                        JSONObject providerCompanyJsonObject = resultJson.getJSONObject("providerCompany");
                        providerCompanyTV.setText(providerCompanyJsonObject.getString("name"));
                    } else {
                        providerCompanyTV.setText("---");
                    }

                    if (!resultJson.isNull("entityShift")) {
                        JSONObject entityShiftJsonObject = resultJson.getJSONObject("entityShift");
                        if (!entityShiftJsonObject.isNull("shift")) {
                            JSONObject shiftJsonObject = entityShiftJsonObject.getJSONObject("shift");
                            shiftLayout.setVisibility(View.VISIBLE);
                            int type = shiftJsonObject.getInt("shiftTypeEn");
                            switch (type) {
                                case 0:
                                    shiftTypeEn = getResources().getString(R.string.enumType_ShiftType_Morning);
                                    break;
                                case 1:
                                    shiftTypeEn = getResources().getString(R.string.enumType_ShiftType_Afternoon);
                                    break;
                                case 2:
                                    shiftTypeEn = getResources().getString(R.string.enumType_ShiftType_Night);
                                    break;
                                case 3:
                                    shiftTypeEn = getResources().getString(R.string.enumType_ShiftType_Other);
                                    break;
                                case 4:
                                    shiftTypeEn = getResources().getString(R.string.enumType_ShiftType_General);
                                    break;
                                case 5:
                                    shiftTypeEn = getResources().getString(R.string.enumType_ShiftType_MidDay);
                                    break;
                            }
                        }

                        if (!entityShiftJsonObject.isNull("timeTo")) {
                            timeTo = entityShiftJsonObject.getString("timeTo");
                        }

                        if (!entityShiftJsonObject.isNull("timeFrom")) {
                            timeFrom = entityShiftJsonObject.getString("timeFrom");
                        }

                        shiftTypeEnTV.setText(shiftTypeEn + " " + getResources().getString(R.string.label_timeFrom) + timeFrom + getResources().getString(R.string.label_timeTo) + timeTo);
                    }
                    //startDateTV.setText(R.string.label_NullStartDate);
                    if (!resultJson.isNull("startDate")) {
                        String startDate = HejriUtil.chrisToHejri(resultJson.getString("startDate"));
                        startDateTV.setText(startDate);
                    } else {
                        startDateTV.setText("---");
                    }

                    //finishDateTV.setText(R.string.label_NullFinishDate);
                    if (!resultJson.isNull("finishDate")) {
                        String finishDate = HejriUtil.chrisToHejri(resultJson.getString("finishDate"));
                        finishDateTV.setText(finishDate);
                    } else {
                        finishDateTV.setText("---");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @SuppressLint("LongLogTag")
    public void logLargeString(String str) {
        String Tag = "DriverJobDetailActivity = ";
        if (str.length() > 3000) {
            Log.i(Tag, str.substring(0, 3000));
            logLargeString(str.substring(3000));
        } else {
            Log.i(Tag, str); // continuation
        }
    }
}
