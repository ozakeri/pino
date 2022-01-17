package com.gap.bis_inspection.fragment.chartstatistical;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
public class DayStatisticalFragment extends Fragment {
    TextView txt_datename,txt_datenumber,txt_monthname,txt_year;
    private IDatabaseManager databaseManager;
    private CoreService coreService;
    String  driverId;
    Date reportDate;
    String reportHejriStrDate;
    JSONArray timeSeriesListArray;
    JSONObject timeSeriesJsonObject;
    BarChart chart;
    RelativeLayout btn_next, btn_prev;
    ProgressBar progressBar;
    BarData barData;
    int i=0;
    ASync myTask=null;
    public DayStatisticalFragment(){
        
    }
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day, container, false);
        databaseManager = new DatabaseManager(getActivity());
        coreService = new CoreService(databaseManager);

        driverId = getArguments().getString("driverid");
        System.out.println("------fff---driverId=" + driverId);

        reportDate = new Date();
        reportDate = HejriUtil.add(reportDate, Calendar.DAY_OF_MONTH, 0);
        //reportDate = HejriUtil.encodeHejriDate(HejriUtil.getYear(reportDate), HejriUtil.getMonth(reportDate), HejriUtil.getDay(reportDate));

        chart = (BarChart)view.findViewById(R.id.chart);
        txt_datename=(TextView)view.findViewById(R.id.txt_datename);
        txt_datenumber=(TextView)view.findViewById(R.id.txt_datenumber);
        txt_monthname=(TextView)view.findViewById(R.id.txt_monthname);
        txt_year=(TextView)view.findViewById(R.id.txt_year);
        btn_next=(RelativeLayout)view.findViewById(R.id.btn_next);
        btn_prev=(RelativeLayout)view.findViewById(R.id.btn_prev);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        txt_year.setText(reportHejriStrDate = Integer.valueOf(HejriUtil.getYear(reportDate)).toString());
        txt_datenumber.setText(reportHejriStrDate = Integer.valueOf(HejriUtil.getDay(reportDate)).toString());
        getmonth_name();
        getday_name();
        System.out.println("reportdate" + reportDate.getDay());
        btn_prev.setEnabled(false);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i++;
                reportDate = HejriUtil.add(reportDate, Calendar.DAY_OF_MONTH, -1);
                myTask = new ASync();
                myTask.execute();
                txt_year.setText(reportHejriStrDate = Integer.valueOf(HejriUtil.getYear(reportDate)).toString());
                txt_datenumber.setText(reportHejriStrDate = Integer.valueOf(HejriUtil.getDay(reportDate)).toString());
                getmonth_name();
                getday_name();
                btn_prev.setEnabled(true);
            }
        });
        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i--;
                reportDate = HejriUtil.add(reportDate, Calendar.DAY_OF_MONTH, 1);
                myTask = new ASync();
                myTask.execute();
                txt_year.setText(reportHejriStrDate = Integer.valueOf(HejriUtil.getYear(reportDate)).toString());
                txt_datenumber.setText(reportHejriStrDate = Integer.valueOf(HejriUtil.getDay(reportDate)).toString());
                getmonth_name();
                getday_name();
                if (i == 0) {
                    btn_prev.setEnabled(false);
                }
            }
        });
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
        return view;
    }

    private void getday_name() {
        switch (reportDate.getDay()){

            case 0:
                txt_datename.setText("یکشنبه");
                break;
            case 1:
                txt_datename.setText("دوشنبه");
                break;
            case 2:
                txt_datename.setText("سه شنبه");
                break;
            case 3:
                txt_datename.setText("چهارشنبه");
                break;
            case 4:
                txt_datename.setText("پنج شنبه");
                break;
            case 5:
                txt_datename.setText("جمعه");
                break;
            case 6:
                txt_datename.setText("شنبه");
                break;
        }
    }

        private class ASync extends AsyncTask<Void, Void, Void> {
        private String result;
        private String errorMsg;
        private String drivercode;
        private String reportHejriStrDate;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //drivercode = txt_drivercode.getText().toString();
            progressBar.setVisibility(View.VISIBLE);
            HejriUtil hejriUtil = new HejriUtil(reportDate);
            reportHejriStrDate = Integer.valueOf(hejriUtil.getYear()).toString() + "-" +
                    Integer.valueOf(hejriUtil.getMonth()).toString()+ "-" +
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
                                    System.out.println("====timeSeriesList=" + timeSeriesListArray);
                                    List<BarEntry> entries = new ArrayList<BarEntry>();
                                    List<String> xValues = new ArrayList<String>();
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                    for (int i = 0; i < timeSeriesListArray.length(); i++) {
                                        timeSeriesJsonObject = timeSeriesListArray.getJSONObject(i);
                                        // System.out.println("======timeSeriesJsonObject" + timeSeriesJsonObject);
                                        int count = timeSeriesJsonObject.getInt("count");
                                        String strDate = timeSeriesJsonObject.getString("date");
                                        Date date = simpleDateFormat.parse(strDate);
                                        HejriUtil hejriUtil = new HejriUtil(date);
                                        System.out.println("count1======date" + count + date);
                                        entries.add(new BarEntry(count, i));
                                        xValues.add(Integer.valueOf(hejriUtil.getDay()).toString());
                                    }

                                    BarDataSet dataSet = new BarDataSet(entries, "Label"); // add entries to dataset
                                    dataSet.setColor(Color.rgb(0, 155, 0));
                                    dataSet.setValueTextColor(Color.rgb(0, 155, 0));
                                    dataSet.setValueFormatter(new MyValueFormatter());

                                    barData = new BarData(xValues, dataSet);
                                    chart.setData(barData);
                                    chart.setDescription("");
                                    chart.invalidate();
                                    chart.animateXY(1000, 1000);
                                   // chart.setBorderColor(getResources().getColor(R.color.primary_material_light));
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
                                    // l.setFormSize(8f);
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
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getActivity(), (errorMsg != null) ? errorMsg : "License Activity2 - Some error accor, contact admin", Toast.LENGTH_LONG).show();
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(Void... voids) {
            System.out.println("=====================LoginFragment.onClick=" + driverId);
            if (isDeviceDateTimeValid()) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    AppController application = (AppController) Objects.requireNonNull(getActivity()).getApplication();
                    jsonObject.put("username", application.getCurrentUser().getUsername());
                    jsonObject.put("tokenPass", application.getCurrentUser().getBisPassword());
                    //jsonObject.put("driverCode", code);
                    jsonObject.put("driverId", driverId);
                    jsonObject.put("reportDate", reportHejriStrDate);
                    MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, getActivity());
                    try {
                        result = postJsonService.sendData("getViolationCodeStatisticallyReportList", jsonObject, true);
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

    private void getmonth_name() {
        switch (HejriUtil.getMonth(reportDate)){
            case 1:
                txt_monthname.setText("فروردین");
                break;
            case 2:
                txt_monthname.setText("اردیبهشت");
                break;
            case 3:
                txt_monthname.setText("خرداد");
                break;
            case 4:
                txt_monthname.setText("تیر");
                break;
            case 5:
                txt_monthname.setText("مرداد");
                break;
            case 6:
                txt_monthname.setText("شهریور");
                break;
            case 7:
                txt_monthname.setText("مهر");
                break;
            case 8:
                txt_monthname.setText("آبان");
                break;
            case 9:
                txt_monthname.setText("آذر");
                break;
            case 10:
                txt_monthname.setText("دی");
                break;
            case 11:
                txt_monthname.setText("بهمن");
                break;
            case 12:
                txt_monthname.setText("اسفند");
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
