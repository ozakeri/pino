package com.gap.bis_inspection.activity.car;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.common.HejriUtil;
import com.gap.bis_inspection.webservice.GetJsonService;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CarIncidentDetailActivity extends AppCompatActivity {
    private String result;
    private String jsonData;
    TextView dateTV, incLocateEnTV, incDivParamTV, descriptionTV;
    ImageView backIcon;
    private String strType = "";
    private String incDivParam = "";
    private String incDamageTypeEn = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_car_incident_detail);
        Bundle bundle = getIntent().getExtras();
        jsonData = bundle.getString("incidentEntityObject");
        init();
        new ResultTask().execute();
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void init() {
        dateTV = (TextView) findViewById(R.id.date_TV);
        incDivParamTV = (TextView) findViewById(R.id.incDivParam_TV);
        incLocateEnTV = (TextView) findViewById(R.id.incLocateEn_TV);
        descriptionTV = (TextView) findViewById(R.id.description_TV);
        backIcon = (ImageView) findViewById(R.id.backIcon);
    }


    ////******get incidentEntityObject from bundle*******////
    private class ResultTask extends AsyncTask<Void, Void, Void> {

        ResultTask() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progress.setVisibility(View.VISIBLE);
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
            // progress.setVisibility(View.INVISIBLE);

            result = jsonData;
            if (result != null) {
                logLargeString(result);
                try {
                    JSONObject resultJson = new JSONObject(result);
                    if (!resultJson.isNull("incident")) {
                        JSONObject incidentJsonObject = resultJson.getJSONObject("incident");

                        if (!incidentJsonObject.isNull("date")) {
                            String strStartDate = incidentJsonObject.getString("date");
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            Date startDate = simpleDateFormat.parse(strStartDate);
                            String hejriStartDate = HejriUtil.chrisToHejriDateTime(startDate);
                            dateTV.setText(getResources().getString(R.string.incidentDate_label) + " " + hejriStartDate);
                        } else {
                            dateTV.setText("---");
                        }

                        if (!incidentJsonObject.isNull("incTypeEn")) {
                            int type = incidentJsonObject.getInt("incTypeEn");
                            switch (type) {
                                case 0:
                                    strType = getResources().getString(R.string.enumType_IncidentTypeEn_Crash);
                                    break;
                                case 1:
                                    strType = getResources().getString(R.string.enumType_IncidentTypeEn_Conflict);
                                    break;
                                case 2:
                                    strType = getResources().getString(R.string.enumType_IncidentTypeEn_NaturalDisasters);
                                    break;
                                case 3:
                                    strType =getResources().getString(R.string.enumType_IncidentTypeEn_Riot);
                                    break;
                                case 4:
                                    strType = getResources().getString(R.string.enumType_IncidentTypeEn_Theft);
                                    break;
                                case 5:
                                    strType = getResources().getString(R.string.enumType_IncidentTypeEn_TechnicalDamages);
                                    break;
                                case 6:
                                    strType = getResources().getString(R.string.enumType_IncidentTypeEn_Fires);
                                    break;
                                case 7:
                                    strType = getResources().getString(R.string.enumType_IncidentTypeEn_Lost);
                                    break;
                                case 8:
                                    strType = getResources().getString(R.string.enumType_IncidentTypeEn_BusInternalIncidents);
                                    break;
                                case 9:
                                    strType = getResources().getString(R.string.enumType_IncidentTypeEn_DisorderInLine);
                                    break;
                                case 10:
                                    strType = getResources().getString(R.string.enumType_IncidentTypeEn_BusBodyIncidents);
                                    break;
                                case 11:
                                    strType = getResources().getString(R.string.enumType_IncidentTypeEn_LineReport);
                                    break;

                            }
                        }

                        if (!incidentJsonObject.isNull("incDivParam")) {
                            JSONObject incDivParamJsonObject = incidentJsonObject.getJSONObject("incDivParam");
                            if (!incDivParamJsonObject.isNull("paramValue")) {
                                incDivParam = incDivParamJsonObject.getString("paramValue");
                            }
                        }

                        if (!incidentJsonObject.isNull("incDamageTypeEn")) {
                            int type = incidentJsonObject.getInt("incDamageTypeEn");
                            switch (type) {
                                case 0:
                                    incDamageTypeEn = getResources().getString(R.string.enumType_IncidentDamageTypeEn_Damaging);
                                    break;
                                case 1:
                                    incDamageTypeEn = getResources().getString(R.string.enumType_IncidentDamageTypeEn_Injury);
                                    break;
                                case 2:
                                    incDamageTypeEn = getResources().getString(R.string.enumType_IncidentDamageTypeEn_Death);
                                    break;
                            }
                        }

                        incDivParamTV.setText(getResources().getString(R.string.incDivParam_label) + " " + strType + " - " + incDivParam + " - " + incDamageTypeEn);


                        if (!incidentJsonObject.isNull("locateName")) {
                            incLocateEnTV.setText(getResources().getString(R.string.incidentLocate_label) + " " + incidentJsonObject.getString("locateName"));

                            /*if (incidentJsonObject.getString("incLocateEn").equals("0")) {
                                if (!incidentJsonObject.isNull("line")) {
                                    JSONObject lineJsonObject = incidentJsonObject.getJSONObject("line");
                                    if (!lineJsonObject.isNull("nameFv")) {
                                        locateNameTV.setText(lineJsonObject.getString("nameFv"));
                                        locateNameEn_label.setText(getApplicationContext().getResources().getString(R.string.locateNameEn_line_label));
                                    }
                                }
                            } else if (incidentJsonObject.getString("incLocateEn").equals("1")) {
                                if (!incidentJsonObject.isNull("terminal")) {
                                    JSONObject terminalJsonObject = incidentJsonObject.getJSONObject("terminal");
                                    if (!terminalJsonObject.isNull("nameFv")) {
                                        locateNameTV.setText(terminalJsonObject.getString("nameFv"));
                                        locateNameEn_label.setText(getApplicationContext().getResources().getString(R.string.locateNameEn_terminal_label));
                                    }
                                }
                            } else if (incidentJsonObject.getString("incLocateEn").equals("2")) {
                                if (!incidentJsonObject.isNull("parkingSysParam")) {
                                    JSONObject parkingSysParamJsonObject = incidentJsonObject.getJSONObject("parkingSysParam");
                                    if (!parkingSysParamJsonObject.isNull("paramValue")) {
                                        locateNameTV.setText(parkingSysParamJsonObject.getString("paramValue"));
                                        locateNameEn_label.setText(getApplicationContext().getResources().getString(R.string.locateNameEn_parking_label));
                                    }
                                }
                            } else if (incidentJsonObject.getString("incLocateEn").equals("3")) {
                                if (!incidentJsonObject.isNull("parkingSysParam")) {
                                    JSONObject parkingSysParamJsonObject = incidentJsonObject.getJSONObject("parkingSysParam");
                                    if (!parkingSysParamJsonObject.isNull("paramValue")) {
                                        locateNameTV.setText(parkingSysParamJsonObject.getString("paramValue"));
                                        locateNameEn_label.setText(getApplicationContext().getResources().getString(R.string.locateNameEn_workshop_label));
                                    }
                                }
                            }
                            //incLocateEn.setText(incidentJsonObject.getString("incLocateEn_text"));*/
                        } else {
                            incLocateEnTV.setText(getResources().getString(R.string.incidentLocate_label) + "---");
                        }

                        /*if (!incidentJsonObject.isNull("incidentDescSum")) {
                            incidentDescSumTV.setText(incidentJsonObject.getString("incidentDescSum"));
                        }*/

                        if (!incidentJsonObject.isNull("incidentDescSum")) {
                            descriptionTV.setText(getResources().getString(R.string.incidentDescription_label) + " " + incidentJsonObject.getString("incidentDescSum"));
                        } else {
                            descriptionTV.setText(getResources().getString(R.string.incidentDescription_label) + "---");
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressLint("LongLogTag")
    public void logLargeString(String str) {
        String Tag = "jsonResultCarIncidentDetail = ";
        if (str.length() > 3000) {
            Log.i(Tag, str.substring(0, 3000));
            logLargeString(str.substring(3000));
        } else {
            Log.i(Tag, str); // continuation
        }
    }
}
