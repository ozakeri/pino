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

import java.text.DecimalFormat;

public class LinePathDetailActivity extends AppCompatActivity {
    private String result;
    private String jsonData;
    ImageView backIcon;
    TextView pathTypeTV, pathTextTV, pathKmTV, stationNoTV, counterNoTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_line_path_detail);
        Bundle bundle = getIntent().getExtras();
        jsonData = bundle.getString("linePathList");

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
        pathTypeTV = (TextView) findViewById(R.id.pathType_TV);
        pathTextTV = (TextView) findViewById(R.id.pathText_TV);
        pathKmTV = (TextView) findViewById(R.id.pathKm_TV);
        stationNoTV = (TextView) findViewById(R.id.stationNo_TV);
        counterNoTV = (TextView) findViewById(R.id.convertNo_TV);
        backIcon = (ImageView) findViewById(R.id.back_Icon);
    }

    ////******get linePathList from bundle*******////
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
                    if (!resultJson.isNull("pathType")) {
                        int type = resultJson.getInt("pathType");
                        if (type == 0) {
                            pathTypeTV.setText(R.string.enumType_PathType_Main);
                        } else if (type == 1) {
                            pathTypeTV.setText(R.string.enumType_PathType_Returned);
                        }
                    }else {
                        pathTypeTV.setText("---");
                    }

                    if (!resultJson.isNull("pathText")) {
                        pathTextTV.setText(resultJson.getString("pathText"));
                    }else {
                        pathTextTV.setText("---");
                    }

                    if (!resultJson.isNull("pathKm")) {
                        String pathKm1 = resultJson.getString("pathKm");
                        double amount = Double.parseDouble(pathKm1);
                        DecimalFormat formatter = new DecimalFormat("#,###");
                        pathKmTV.setText(formatter.format(amount));
                    }else {
                        pathKmTV.setText("---");
                    }

                    if (!resultJson.isNull("stationNo")) {
                        stationNoTV.setText(resultJson.getString("stationNo"));
                    }else {
                        stationNoTV.setText("---");
                    }

                    if (!resultJson.isNull("counterNo")) {
                        counterNoTV.setText(resultJson.getString("counterNo"));
                    }else {
                        counterNoTV.setText("---");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
