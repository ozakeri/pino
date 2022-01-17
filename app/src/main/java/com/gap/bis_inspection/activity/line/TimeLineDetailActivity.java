package com.gap.bis_inspection.activity.line;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.webservice.GetJsonService;

import org.json.JSONException;
import org.json.JSONObject;

public class TimeLineDetailActivity extends AppCompatActivity {
    private String result;
    private String jsonData;
    ImageView backIcon;
    TextView StartTimeTV,endTimeTV,moveDistanceOnSecTV,halfPathTimeAvrOnMinTV,pasNoInHalfPathMaxTV,pasNoInHalfPathMinTV,pasNoInHalfPathAvrTV,halfPathSumTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_time_line_list_detail);
        Bundle bundle = getIntent().getExtras();
        jsonData = bundle.getString("timeLineDetailList");

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
        backIcon = (ImageView) findViewById(R.id.back_Icon);
        StartTimeTV = (TextView) findViewById(R.id.StartTime_TV);
        endTimeTV = (TextView) findViewById(R.id.endTime_TV);
        moveDistanceOnSecTV = (TextView) findViewById(R.id.moveDistanceOnSec_TV);
        halfPathTimeAvrOnMinTV = (TextView) findViewById(R.id.halfPathTimeAvrOnMin_TV);
        pasNoInHalfPathMaxTV = (TextView) findViewById(R.id.pasNoInHalfPathMax_TV);
        pasNoInHalfPathMinTV = (TextView) findViewById(R.id.pasNoInHalfPathMin_TV);
        pasNoInHalfPathAvrTV = (TextView) findViewById(R.id.pasNoInHalfPathAvr_TV);
        halfPathSumTV = (TextView) findViewById(R.id.halfPathSum_TV);
    }

    ////******get time Line Detail List from bundle*******////
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
                    if (!resultJson.isNull("startTime")) {
                        StartTimeTV.setText(resultJson.getString("startTime"));
                    }else {
                        StartTimeTV.setText("---");
                    }

                    if (!resultJson.isNull("endTime")) {
                        endTimeTV.setText(resultJson.getString("endTime"));
                    }else {
                        endTimeTV.setText("---");
                    }

                    if (!resultJson.isNull("moveDistanceOnSec")) {
                        moveDistanceOnSecTV.setText(resultJson.getString("moveDistanceOnSec"));
                    }else {
                        moveDistanceOnSecTV.setText("---");
                    }

                    if (!resultJson.isNull("endTime")) {
                        endTimeTV.setText(resultJson.getString("endTime"));
                    }else {
                        endTimeTV.setText("---");
                    }

                    if (!resultJson.isNull("halfPathTimeAvrOnMin")) {
                        halfPathTimeAvrOnMinTV.setText(resultJson.getString("halfPathTimeAvrOnMin"));
                    }else {
                        halfPathTimeAvrOnMinTV.setText("---");
                    }

                    if (!resultJson.isNull("pasNoInHalfPathMax")) {
                        pasNoInHalfPathMaxTV.setText(resultJson.getString("pasNoInHalfPathMax"));
                    }else {
                        pasNoInHalfPathMaxTV.setText("---");
                    }

                    if (!resultJson.isNull("pasNoInHalfPathMin")) {
                        pasNoInHalfPathMinTV.setText(resultJson.getString("pasNoInHalfPathMin"));
                    }else {
                        pasNoInHalfPathMinTV.setText("---");
                    }

                    if (!resultJson.isNull("pasNoInHalfPathAvr")) {
                        pasNoInHalfPathAvrTV.setText(resultJson.getString("pasNoInHalfPathAvr"));
                    }else {
                        pasNoInHalfPathAvrTV.setText("---");
                    }


                    if (!resultJson.isNull("halfPathSum")) {
                        halfPathSumTV.setText(resultJson.getString("halfPathSum"));
                    }else {
                        halfPathSumTV.setText("---");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
