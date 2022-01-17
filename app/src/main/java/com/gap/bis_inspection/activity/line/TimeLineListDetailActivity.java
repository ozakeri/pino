package com.gap.bis_inspection.activity.line;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.adapter.line.TimeLineDetailAdapter;
import com.gap.bis_inspection.common.HejriUtil;
import com.gap.bis_inspection.webservice.GetJsonService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TimeLineListDetailActivity extends AppCompatActivity {
    private String result;
    private String jsonData;
    RelativeLayout backIcon;
    ListView timeLineDetailList;
    TextView pathTypeEnTV, dayTypeOnHolidayEnTV, startTimeTV, endTimeTV, halfPathSumTV, StartDateTV, counterTV;
    LinearLayout layout_counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_time_line_detail);
        Bundle bundle = getIntent().getExtras();
        jsonData = bundle.getString("timeLineList");
        init();

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        new ResultTask().execute();
    }

    private void init() {
        pathTypeEnTV = (TextView) findViewById(R.id.pathTypeEn_TV);
        dayTypeOnHolidayEnTV = (TextView) findViewById(R.id.dayTypeOnHolidayEn_TV);
        startTimeTV = (TextView) findViewById(R.id.startTime_TV);
        endTimeTV = (TextView) findViewById(R.id.endTime_TV);
        halfPathSumTV = (TextView) findViewById(R.id.halfPathSum_TV);
        StartDateTV = (TextView) findViewById(R.id.StartDate_TV);
        counterTV = (TextView) findViewById(R.id.counter_TV);
        timeLineDetailList = (ListView) findViewById(R.id.timeLineDetailList);
        backIcon = (RelativeLayout) findViewById(R.id.back_Icon);
        layout_counter = (LinearLayout) findViewById(R.id.layout_counter);
    }

    ////******get ttime Line List from bundle*******////
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

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // progress.setVisibility(View.INVISIBLE);

            result = jsonData;
            //Toast.makeText(getActivity(),result,Toast.LENGTH_LONG).show();
            if (result != null) {
                try {
                    JSONObject resultJson = new JSONObject(result);
                    if (!resultJson.isNull("pathTypeEn")) {
                        int type = resultJson.getInt("pathTypeEn");
                        if (type == 0) {
                            pathTypeEnTV.setText(R.string.enumType_PathType_Main);
                        } else if (type == 1) {
                            pathTypeEnTV.setText(R.string.enumType_PathType_Returned);
                        }

                    }

                    if (!resultJson.isNull("dayTypeOnHolidayEn")) {
                        int type = resultJson.getInt("dayTypeOnHolidayEn");
                        if (type == 0) {
                            dayTypeOnHolidayEnTV.setText(R.string.enumType_DayTypeOnHolidayEn_NotHoliday);
                        } else if (type == 1) {
                            dayTypeOnHolidayEnTV.setText(R.string.enumType_DayTypeOnHolidayEn_Holiday);
                        } else if (type == 2) {
                            dayTypeOnHolidayEnTV.setText(R.string.enumType_DayTypeOnHolidayEn_SemiHoliday);
                        }
                    }

                    if (!resultJson.isNull("startTime")) {
                        startTimeTV.setText(resultJson.getString("startTime"));
                    }

                    if (!resultJson.isNull("endTime")) {
                        endTimeTV.setText(resultJson.getString("endTime"));
                    }

                    if (!resultJson.isNull("halfPathSum")) {
                        halfPathSumTV.setText(resultJson.getString("halfPathSum"));
                    }

                    if (!resultJson.isNull("startDate")) {
                        String date = resultJson.getString("startDate");
                        StartDateTV.setText(HejriUtil.chrisToHejri(date));
                    }

                    if (!resultJson.isNull("timeLineDetailList")) {
                        final JSONArray timeLineDetailListJsonArray = resultJson.getJSONArray("timeLineDetailList");
                        List<JSONObject> jsonObjectList = new ArrayList<>();
                        for (int i = 0; i < timeLineDetailListJsonArray.length(); i++) {
                            JSONObject timeLineDetailListJsonObject = timeLineDetailListJsonArray.getJSONObject(i);
                            jsonObjectList.add(timeLineDetailListJsonObject);
                        }
                        TimeLineDetailAdapter TimeLineListAdapter = new TimeLineDetailAdapter(getApplicationContext(), R.layout.fragment_time_line_item, jsonObjectList);
                        timeLineDetailList.setAdapter(TimeLineListAdapter);

                        timeLineDetailList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                try {
                                    JSONObject linePathList = timeLineDetailListJsonArray.getJSONObject(i);
                                    Intent intent = new Intent(getApplicationContext(), TimeLineDetailActivity.class);
                                    intent.putExtra("timeLineDetailList", linePathList.toString());
                                    startActivity(intent);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        layout_counter.setVisibility(View.VISIBLE);
                        counterTV.setText(String.valueOf(timeLineDetailList.getCount()));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @SuppressLint("LongLogTag")
    public void logLargeString(String str) {
        String Tag = "jsonResultLineTimeLineDetail = ";
        if(str.length() > 3000) {
            Log.i(Tag, str.substring(0, 3000));
            logLargeString(str.substring(3000));
        } else {
            Log.i(Tag, str); // continuation
        }
    }
}
