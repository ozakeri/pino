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
import com.gap.bis_inspection.util.GroupingValueFormatter;
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
public class DriverComplaintMonthFragment extends Fragment {
    TextView yearTV, monthTV;
    private IDatabaseManager databaseManager;
    private CoreService coreService;
    String id, jsonStr;
    boolean recognize = false;
    boolean lineRecognize = false;
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

    public DriverComplaintMonthFragment() {

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistical_month, container, false);
        databaseManager = new DatabaseManager(getActivity());
        coreService = new CoreService(databaseManager);
        id = getArguments().getString("Id");
        jsonStr = getArguments().getString("jsonStr");
        recognize = getArguments().getBoolean("recognize");
        lineRecognize = getArguments().getBoolean("lineRecognize");
        reportDate = new Date();
        reportDate = HejriUtil.add(reportDate, Calendar.MONTH, 0);
        chart = (BarChart) view.findViewById(R.id.chart);
        yearTV = (TextView) view.findViewById(R.id.year_TV);
        monthTV = (TextView) view.findViewById(R.id.month_TV);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        viewFlipper = (ViewFlipper) view.findViewById(R.id.viewflipper);
        gatMonthName();
        yearTV.setText(reportHejriStrDate = Integer.valueOf(HejriUtil.getYear(reportDate)).toString());

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
                    if (i != 0) {
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
        reportDate = HejriUtil.add(reportDate, Calendar.MONTH, 1);
        myTask = new ASync();
        myTask.execute();
        yearTV.setText(reportHejriStrDate = Integer.valueOf(HejriUtil.getYear(reportDate)).toString());
        gatMonthName();
    }

    private void nextMethod() {
        i++;
        reportDate = HejriUtil.add(reportDate, Calendar.MONTH, -1);
        myTask = new ASync();
        myTask.execute();
        gatMonthName();
        yearTV.setText(reportHejriStrDate = Integer.valueOf(HejriUtil.getYear(reportDate)).toString());
    }

    private void gatMonthName() {
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
                    Integer.valueOf(hejriUtil.getMonth()).toString();
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
                                List<String> xValues = new ArrayList<String>();
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                for (int i = 0; i < timeSeriesListArray.length(); i++) {
                                   try {
                                       timeSeriesJsonObject = timeSeriesListArray.getJSONObject(i);
                                       int count = timeSeriesJsonObject.getInt("count");
                                       String strDate = timeSeriesJsonObject.getString("date");
                                       Date date = simpleDateFormat.parse(strDate);
                                       HejriUtil hejriUtil = new HejriUtil(date);
                                       //entries.add(new BarEntry(Float.parseFloat(CommonUtil.doubleToStringNoDecimal(count)), i));

                                       entries.add(new BarEntry(count, i));
                                       xValues.add(Integer.valueOf(hejriUtil.getDay()).toString());
                                   }catch (Exception e){
                                       e.getMessage();
                                   }

                                }

                                BarDataSet dataSet = new BarDataSet(entries, "Label"); // add entries to dataset
                                dataSet.setValueFormatter(new GroupingValueFormatter());
                                if (jsonStr != null) {
                                    switch (jsonStr) {
                                        case "driverId":
                                            dataSet.setColor(Color.rgb(58, 170, 53));
                                            dataSet.setValueTextColor(Color.rgb(0, 0, 0));
                                            break;
                                        case "carId":
                                            dataSet.setColor(Color.rgb(237, 50, 55));
                                            dataSet.setValueTextColor(Color.rgb(0, 0, 0));
                                            break;
                                        case "lineId":
                                            dataSet.setColor(Color.rgb(145, 216, 247));
                                            dataSet.setValueTextColor(Color.rgb(0, 0, 0));
                                            break;
                                    }
                                }
                                dataSet.setValueFormatter(new MyValueFormatter());

                                barData = new BarData(xValues, dataSet);
                                chart.setData(barData);
                                chart.setDescription("");
                                chart.invalidate();
                                chart.animateXY(1000, 1000);
                                chart.setDrawBarShadow(false);
                                chart.setDrawValueAboveBar(true);
                                chart.setPinchZoom(true);
                                chart.setDrawGridBackground(false);
                                chart.getAxisRight().setEnabled(true);
                                chart.getAxisLeft().setEnabled(true);
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
                            Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
                        }
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
                        System.out.println("recognize=====" + recognize);
                        if (recognize) {
                            result = postJsonService.sendData("getCarInvoiceEtCardReportList", jsonObject, true);
                        } else if (lineRecognize) {
                            result = postJsonService.sendData("getLineInvoiceEtCardReportList", jsonObject, true);
                        } else {
                            result = postJsonService.sendData("getComplaintStatisticallyReportList", jsonObject, true);
                        }
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
                errorMsg = "Some error accor, contact admin";
                Log.d("SyncActivity", e.getMessage());
            } catch (WebServiceException e) {
                errorMsg = "Some error accor, contact admin";
                Log.d("SyncActivity", e.getMessage());
            } catch (ParseException e) {
                errorMsg = "Some error accor, contact admin";
                Log.d("SyncActivity", e.getMessage());
            }
            return false;
        }
    }

}
