package com.gap.bis_inspection.fragment.chartstatistical;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.common.HejriUtil;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.manager.IDatabaseManager;
import com.gap.bis_inspection.db.objectmodel.DeviceSetting;
import com.gap.bis_inspection.exception.WebServiceException;
import com.gap.bis_inspection.service.CoreService;
import com.gap.bis_inspection.util.DateUtils;
import com.gap.bis_inspection.util.MyValueFormatter;
import com.gap.bis_inspection.webservice.MyPostJsonService;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

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
import java.util.List;
import java.util.Objects;

/**
 * Created by Mohamad Cheraghi on 07/23/2016.
 */
public class DriverViolationDayFragment extends Fragment {
    TextView textDateTV,numberDateTV, monthTV, yearTV;
    private IDatabaseManager databaseManager;
    private CoreService coreService;
    String id, jsonStr;
    Date reportDate;
    String reportHejriStrDate;
    JSONArray timeSeriesListArray;
    JSONObject timeSeriesJsonObject;
    BarChart chart;
    ProgressBar progressBar;
    BarData barData;
    int i = 0;
    ASync myTask = null;
    private ViewFlipper viewFlipper;
    private final GestureDetector detector = new GestureDetector(new SwipeGestureDetector());

    public DriverViolationDayFragment() {

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day, container, false);
        databaseManager = new DatabaseManager(getActivity());
        coreService = new CoreService(databaseManager);

        id = getArguments().getString("Id");
        jsonStr = getArguments().getString("jsonStr");

        reportDate = new Date();
        reportDate = HejriUtil.add(reportDate, Calendar.DAY_OF_MONTH, 0);

        chart = (BarChart) view.findViewById(R.id.chart);
        textDateTV = (TextView) view.findViewById(R.id.textDate_TV);
        numberDateTV = (TextView) view.findViewById(R.id.numberDate_TV);
        monthTV = (TextView) view.findViewById(R.id.month_TV);
        yearTV = (TextView) view.findViewById(R.id.year_TV);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        viewFlipper = (ViewFlipper) view.findViewById(R.id.viewflipper);
        yearTV.setText(reportHejriStrDate = Integer.valueOf(HejriUtil.getYear(reportDate)).toString());
        numberDateTV.setText(reportHejriStrDate = Integer.valueOf(HejriUtil.getDay(reportDate)).toString());
        getMonthName();
        getDayName();

        chart.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View arg0, MotionEvent arg1) {
                return true;
            }
        });

        myTask = new ASync();
        myTask.execute();
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    myTask.cancel(true);
                    getActivity().finish();
                    return true;
                }

                return false;
            }
        });

        viewFlipper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                detector.onTouchEvent(event);
                return true;
            }
        });
        return view;
    }

    class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                // right to left swipe
                if (e1.getX() - e2.getX() > i && Math.abs(velocityX) > i) {
                    viewFlipper.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_to_left));
                    viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_to_left));
                    if (i != 0){
                        prevMethod();
                        viewFlipper.showNext();
                    }

                    return true;
                } else if (e2.getX() - e1.getX() > i && Math.abs(velocityX) > i) {
                    viewFlipper.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_to_right));
                    viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_to_left));
                    nextMethod();
                    viewFlipper.showPrevious();
                    return true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }
    }

    private void prevMethod() {
        i--;
        reportDate = HejriUtil.add(reportDate, Calendar.DAY_OF_MONTH, 1);
        myTask = new ASync();
        myTask.execute();
        yearTV.setText(reportHejriStrDate = Integer.valueOf(HejriUtil.getYear(reportDate)).toString());
        numberDateTV.setText(reportHejriStrDate = Integer.valueOf(HejriUtil.getDay(reportDate)).toString());
        getMonthName();
        getDayName();
    }

    private void nextMethod() {
        i++;
        reportDate = HejriUtil.add(reportDate, Calendar.DAY_OF_MONTH, -1);
        myTask = new ASync();
        myTask.execute();
        yearTV.setText(reportHejriStrDate = Integer.valueOf(HejriUtil.getYear(reportDate)).toString());
        numberDateTV.setText(reportHejriStrDate = Integer.valueOf(HejriUtil.getDay(reportDate)).toString());
        getMonthName();
        getDayName();
    }

    private void getDayName() {
        switch (reportDate.getDay()) {

            case 0:
                textDateTV.setText(R.string.label_Statistically_2);
                break;
            case 1:
                textDateTV.setText(R.string.label_Statistically_3);
                break;
            case 2:
                textDateTV.setText(R.string.label_Statistically_4);
                break;
            case 3:
                textDateTV.setText(R.string.label_Statistically_5);
                break;
            case 4:
                textDateTV.setText(R.string.label_Statistically_6);
                break;
            case 5:
                textDateTV.setText(R.string.label_Statistically_7);
                break;
            case 6:
                textDateTV.setText(R.string.label_Statistically_1);
                break;
        }
    }

    private class ASync extends AsyncTask<Void, Void, Void> {
        private String result;
        private String errorMsg;
        private String reportHejriStrDate;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            HejriUtil hejriUtil = new HejriUtil(reportDate);
            reportHejriStrDate = Integer.valueOf(hejriUtil.getYear()).toString() + "-" +
                    Integer.valueOf(hejriUtil.getMonth()).toString() + "-" +
                    Integer.valueOf(hejriUtil.getDay()).toString();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.INVISIBLE);
            System.out.println("====result=" + result);
            if (result != null) {
                try {
                    JSONObject resultJson = new JSONObject(result);
                    if (errorMsg == null && !resultJson.isNull(Constants.SUCCESS_KEY)) {
                        if (!resultJson.isNull(Constants.RESULT_KEY)) {
                            JSONObject jsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                            if (!jsonObject.isNull("timeSeriesList")) {

                                timeSeriesListArray = jsonObject.getJSONArray("timeSeriesList");
                                List<BarEntry> entries = new ArrayList<BarEntry>();
                                for (int i = 0; i < timeSeriesListArray.length(); i++) {
                                    timeSeriesJsonObject = timeSeriesListArray.getJSONObject(i);
                                    int count = timeSeriesJsonObject.getInt("count");
                                    entries.add(new BarEntry(count, i));
                                }

                                BarDataSet dataSet = new BarDataSet(entries, "Label"); // add entries to dataset
                                if (jsonStr != null) {
                                    switch (jsonStr) {
                                        case "driverId":
                                            dataSet.setColor(Color.rgb(58, 170, 53));
                                            dataSet.setValueTextColor(Color.rgb(58, 170, 53));
                                            break;
                                        case "carId":
                                            dataSet.setColor(Color.rgb(237, 50, 55));
                                            dataSet.setValueTextColor(Color.rgb(237, 50, 55));
                                            break;
                                        case "lineId":
                                            dataSet.setColor(Color.rgb(145, 216, 247));
                                            dataSet.setValueTextColor(Color.rgb(145, 216, 247));
                                            break;
                                    }
                                }
                                dataSet.setValueFormatter(new MyValueFormatter());

                                barData = new BarData(x_Values(), dataSet);
                                chart.setData(barData);
                                chart.setDescription("");
                                chart.invalidate();
                                chart.animateXY(1000, 1000);
                                chart.setDrawBarShadow(false);
                                chart.setDrawValueAboveBar(true);
                                chart.setPinchZoom(true);
                                chart.setDrawGridBackground(false);
                                chart.getAxisRight().setEnabled(false);
                                chart.getAxisLeft().setEnabled(false);
                                chart.enableScroll();
                                chart.setHorizontalScrollBarEnabled(true);

                                XAxis xl = chart.getXAxis();
                                xl.setPosition(XAxis.XAxisPosition.BOTTOM);
                                xl.setDrawLabels(true);
                                xl.setDrawAxisLine(true);
                                xl.setDrawGridLines(false);
                                xl.setSpaceBetweenLabels(0);

                                Legend l = chart.getLegend();
                                l.setEnabled(false);
                                l.setXEntrySpace(0);
                            }
                        }
                    } else {
                        if (errorMsg == null) {
                            errorMsg = resultJson.getString(Constants.ERROR_KEY);
                        }
                        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.d("RegistrationFragment", e.getMessage());
                    Toast.makeText(getActivity(), "License Activity1 - Some error accor, contact admin", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getActivity(), (errorMsg != null) ? errorMsg : "License Activity2 - Some error accor, contact admin", Toast.LENGTH_LONG).show();
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(Void... voids) {
            if (isDeviceDateTimeValid()) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    AppController application = (AppController) Objects.requireNonNull(getActivity()).getApplication();
                    jsonObject.put("username", application.getCurrentUser().getUsername());
                    jsonObject.put("tokenPass", application.getCurrentUser().getBisPassword());
                    jsonObject.put(jsonStr, id);
                    jsonObject.put("reportDate", reportHejriStrDate);
                    MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, getActivity());
                    try {
                        result = postJsonService.sendData("getCarInvoiceEtCardReportList", jsonObject, true);
                    } catch (SocketTimeoutException e) {
                        errorMsg = getResources().getString(R.string.Some_error_accor_contact_admin);
                    } catch (SocketException e) {
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

        private boolean isDeviceDateTimeValid() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);
            try {
                JSONObject jsonObjectParam = new JSONObject();
                MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, getActivity());
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
            } catch (SocketTimeoutException e) {
                errorMsg = getResources().getString(R.string.Some_error_accor_contact_admin);
            } catch (SocketException e) {
                errorMsg = getResources().getString(R.string.Some_error_accor_contact_admin);
            } catch (JSONException e) {
                errorMsg = getResources().getString(R.string.Some_error_accor_contact_admin);
                Log.d("SyncActivity", e.getMessage());
            } catch (WebServiceException e) {
                errorMsg = getResources().getString(R.string.Some_error_accor_contact_admin);
                Log.d("SyncActivity", e.getMessage());
            } catch (ParseException e) {
                errorMsg = getResources().getString(R.string.Some_error_accor_contact_admin);
                Log.d("SyncActivity", e.getMessage());
            }
            return false;
        }
    }

    private void getMonthName() {
        switch (HejriUtil.getMonth(reportDate)) {
            case 1:
                monthTV.setText(R.string.label_Statistically_month1);
                break;
            case 2:
                monthTV.setText(R.string.label_Statistically_month2);
                break;
            case 3:
                monthTV.setText(R.string.label_Statistically_month3);
                break;
            case 4:
                monthTV.setText(R.string.label_Statistically_month4);
                break;
            case 5:
                monthTV.setText(R.string.label_Statistically_month5);
                break;
            case 6:
                monthTV.setText(R.string.label_Statistically_month6);
                break;
            case 7:
                monthTV.setText(R.string.label_Statistically_month7);
                break;
            case 8:
                monthTV.setText(R.string.label_Statistically_month8);
                break;
            case 9:
                monthTV.setText(R.string.label_Statistically_month9);
                break;
            case 10:
                monthTV.setText(R.string.label_Statistically_month10);
                break;
            case 11:
                monthTV.setText(R.string.label_Statistically_month11);
                break;
            case 12:
                monthTV.setText(R.string.label_Statistically_month12);
                break;
        }
    }

    private ArrayList<String> x_Values() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("1");
        xAxis.add("2");
        xAxis.add("3");
        xAxis.add("4");
        xAxis.add("5");
        xAxis.add("6");
        xAxis.add("7");
        xAxis.add("8");
        xAxis.add("9");
        xAxis.add("10");
        xAxis.add("11");
        xAxis.add("12");
        xAxis.add("13");
        xAxis.add("14");
        xAxis.add("15");
        xAxis.add("16");
        xAxis.add("17");
        xAxis.add("18");
        xAxis.add("19");
        xAxis.add("20");
        xAxis.add("21");
        xAxis.add("22");
        xAxis.add("23");
        xAxis.add("24");
        return xAxis;
    }
}
