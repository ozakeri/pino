package com.gap.bis_inspection.activity.graph;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.adapter.advert.GraphAdapter;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.CalendarUtil;
import com.gap.bis_inspection.common.CommonUtil;
import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.common.HejriUtil;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.objectmodel.DeviceSetting;
import com.gap.bis_inspection.exception.WebServiceException;
import com.gap.bis_inspection.service.CoreService;
import com.gap.bis_inspection.service.Services;
import com.gap.bis_inspection.util.DateUtils;
import com.gap.bis_inspection.util.OnSwipeTouchListener;
import com.gap.bis_inspection.util.Util;
import com.gap.bis_inspection.webservice.MyPostJsonService;
import com.gap.bis_inspection.widget.DatePicker.DatePicker;
import com.gap.bis_inspection.widget.DatePicker.interfaces.DateSetListener;
import com.github.mikephil.charting.charts.BarChart;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class GraphActivity extends AppCompatActivity {

    private RecyclerView graph_recyclerView;
    private Services services;
    private BarChart chart;
    private List<JSONObject> graphArray;
    private TextView txt_selectDate;
    private LinearLayout selectDate_layout;
    private HejriUtil hejriUtil;
    private RelativeLayout backIcon;
    private ProgressBar processBar;
    private CoreService coreService;
    private DatabaseManager databaseManager;
    private int year, month, day = 0;
    private String dateStr = "";
    private String strDateForTextView = "";
    private Date date;
    GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garph);

        graph_recyclerView = findViewById(R.id.graph_recyclerView);
        txt_selectDate = findViewById(R.id.txt_selectDate);
        selectDate_layout = findViewById(R.id.selectDate_layout);
        backIcon = findViewById(R.id.backIcon);
        processBar = findViewById(R.id.processBar);
        services = new Services(getApplicationContext());
        graph_recyclerView.setLayoutManager(new LinearLayoutManager(this));
        hejriUtil = new HejriUtil();
        databaseManager = new DatabaseManager(getApplicationContext());
        coreService = new CoreService(databaseManager);

        processBar.setVisibility(View.VISIBLE);
        date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        dateStr = year + "-" + (month + 1) + "-" + day;
        strDateForTextView = CalendarUtil.convertPersianDateTime(date, "yyyy/MM/dd");
        txt_selectDate.setText(CommonUtil.latinNumberToPersian(strDateForTextView));
        getChartList(dateStr);




        /*try {
            JSONObject resultJson = new JSONObject(services.getChartValue(String.valueOf(new Date())));
            if (!resultJson.isNull(Constants.RESULT_KEY)) {
                processBar.setVisibility(View.GONE);
                graph_recyclerView.setVisibility(View.VISIBLE);
                JSONObject jsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                if (!jsonObject.isNull("timeSeriesJsonArrayTest")) {
                    graphArray = new ArrayList<>();
                    JSONArray array = (JSONArray) jsonObject.getJSONArray("timeSeriesJsonArrayTest");
                    for (int i = 0; i < array.length(); i++) {
                        System.out.println("timeSeriesJsonArrayTest====" + i);
                        JSONObject jsonObject1 = (JSONObject) array.get(i);
                        graphArray.add(jsonObject1);
                        graph_recyclerView.setAdapter(new GraphAdapter(graphArray));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
*/
        selectDate_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar minDate = Calendar.getInstance();
                Calendar maxDate = Calendar.getInstance();
                minDate.set(Calendar.MONTH, minDate.get(Calendar.MONTH) - 6);
                //maxDate.set(Calendar.MONTH, maxDate.get(Calendar.MONTH) + 1);

                new DatePicker.Builder()
                        .id(1)
                        .minDate(minDate)
                        .maxDate(maxDate)
                        .build(new DateSetListener() {
                            @Override
                            public void onDateSet(int id, @Nullable Calendar calendar, int day, int month, int year) {
                                setDate(calendar);
                            }
                        })
                        .show(getSupportFragmentManager(), "");
            }
        });

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        graph_recyclerView.setOnTouchListener(new OnSwipeTouchListener() {

            @Override
            public boolean onSwipeLeft() {
                overridePendingTransition(R.anim.left_out, R.anim.right_in);
                cal.add(Calendar.DAY_OF_MONTH, -1);
                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                day = cal.get(Calendar.DAY_OF_MONTH);
                dateStr = year + "-" + (month + 1) + "-" + day;
                getChartList(dateStr);
                System.out.println("========onSwipeLeft=========" + dateStr);
                return true;
            }

            @Override
            public boolean onSwipeRight() {
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                Date date1 = new Date();

                System.out.println("getTime=========" + date1.getTime());
                System.out.println("getTime=========" + cal.getTime());

                if (Util.compareDates(date1, cal.getTime()) >= 1) {

                    cal.add(Calendar.DAY_OF_MONTH, +1);
                    Date date = cal.getTime();
                    year = cal.get(Calendar.YEAR);
                    month = cal.get(Calendar.MONTH);
                    day = cal.get(Calendar.DAY_OF_MONTH);

                    dateStr = year + "-" + (month + 1) + "-" + day;
                    getChartList(dateStr);
                    System.out.println("========onSwipeRight=========" + dateStr);
                    return true;
                }
                return false;

            }

            @Override
            public boolean onSwipeBottom() {
                // TODO Auto-generated method stub
                return true;
            }

            @Override
            public boolean onSwipeTop() {
                // TODO Auto-generated method stub
                return true;
            }

        });


        /*graph_recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                recyclerView.removeOnScrollListener(this);
            }
        });*/

    }

    private void getChartList(String dateStr) {

        processBar.setVisibility(View.VISIBLE);

        class GetChartList extends AsyncTask<Void, Void, Void> {
            private String result;
            private String errorMsg;

            @SuppressLint("StringFormatInvalid")
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                processBar.setVisibility(View.GONE);

                date = cal.getTime();
                strDateForTextView = CalendarUtil.convertPersianDateTime(date, "yyyy/MM/dd");
                txt_selectDate.setText(CommonUtil.latinNumberToPersian(strDateForTextView));

                graph_recyclerView.setVisibility(View.VISIBLE);
                if (result != null) {
                    JSONObject resultJson = null;
                    try {
                        resultJson = new JSONObject(result);
                        if (!resultJson.isNull(Constants.RESULT_KEY)) {
                            processBar.setVisibility(View.GONE);
                            graph_recyclerView.setVisibility(View.VISIBLE);
                            JSONObject jsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                            if (!jsonObject.isNull("timeSeriesJsonArrayTest")) {
                                graphArray = new ArrayList<>();
                                JSONArray array = (JSONArray) jsonObject.getJSONArray("timeSeriesJsonArrayTest");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject jsonObject1 = (JSONObject) array.get(i);
                                    graphArray.add(jsonObject1);
                                    graph_recyclerView.setAdapter(new GraphAdapter(graphArray, getApplicationContext()));
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            protected Void doInBackground(Void... voids) {
                if (isDeviceDateTimeValid()) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        AppController application = (AppController) getApplication();
                        jsonObject.put("username", application.getCurrentUser().getUsername());
                        jsonObject.put("tokenPass", application.getCurrentUser().getBisPassword());
                        jsonObject.put("reportDate", dateStr);
                        //jsonObject.put("carInfoType", carInfoType);
                        MyPostJsonService postJsonService = new MyPostJsonService(null, GraphActivity.this);
                        try {
                            result = postJsonService.sendData("getMngFleetReportForTariff", jsonObject, true);
                        } catch (SocketTimeoutException | SocketException e) {
                            errorMsg = getResources().getString(R.string.Some_error_accor_contact_admin);
                        } catch (WebServiceException e) {
                            Log.d("RegistrationFragment", e.getMessage());
                        }

                    } catch (JSONException e) {
                        Log.d("RegistrationFragment", e.getMessage());
                    }
                }
                return null;
            }


            ////******getServerDateTime*******////

            private boolean isDeviceDateTimeValid() {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);
                try {
                    JSONObject jsonObjectParam = new JSONObject();
                    MyPostJsonService postJsonService = new MyPostJsonService(null, GraphActivity.this);
                    result = postJsonService.sendData("getServerDateTime", jsonObjectParam, true);

                    if (result != null) {
                        JSONObject resultJson = new JSONObject(result);
                        if (!resultJson.isNull(Constants.SUCCESS_KEY)) {
                            JSONObject jsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                            Date serverDateTime = simpleDateFormat.parse(jsonObject.getString("serverDateTime"));
                            if (DateUtils.isValidDateDiff(new Date(), serverDateTime, Constants.VALID_SERVER_AND_DEVICE_TIME_DIFF)) {
                                DeviceSetting deviceSetting = coreService.getDeviceSettingByKey(Constants.DEVICE_SETTING_KEY_LAST_CHANGE_DATE);
                                if (deviceSetting == null) {
                                    deviceSetting = new DeviceSetting();
                                    deviceSetting.setKey(Constants.DEVICE_SETTING_KEY_LAST_CHANGE_DATE);
                                }
                                deviceSetting.setValue(simpleDateFormat.format(new Date()));
                                deviceSetting.setDateLastChange(new Date());
                                coreService.saveOrUpdateDeviceSetting(deviceSetting);
                                return true;
                            } else {
                                errorMsg = getResources().getString(R.string.Invalid_Device_Date_Time);
                            }
                        }
                    }
                } catch (SocketTimeoutException | SocketException e) {
                    errorMsg = getResources().getString(R.string.Some_error_accor_contact_admin);
                } catch (JSONException | ParseException | WebServiceException e) {
                    errorMsg = getResources().getString(R.string.Some_error_accor_contact_admin);
                    Log.d("SyncActivity", e.getMessage());
                }
                return false;
            }
        }

        new GetChartList().execute();
    }

    /*
     * set date for custom date piker
     * */
    private void setDate(final Calendar calendar) {
        if (calendar == null)
            return;

        cal.setTime(calendar.getTime());
        date = cal.getTime();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);
        System.out.println("year=====" + year);
        System.out.println("month=====" + (month + 1));
        System.out.println("day=====" + day);
        dateStr = year + "-" + (month + 1) + "-" + day;
        getChartList(dateStr);
    }

}