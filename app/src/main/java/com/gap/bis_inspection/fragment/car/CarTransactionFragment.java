package com.gap.bis_inspection.fragment.car;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.activity.driver.DriverChartComplaintActivity;
import com.gap.bis_inspection.adapter.driver.DriverChartViolationAdapter;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.CommonUtil;
import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.common.Data;
import com.gap.bis_inspection.common.HejriUtil;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.manager.IDatabaseManager;
import com.gap.bis_inspection.service.CoreService;
import com.gap.bis_inspection.util.PersianDate;
import com.gap.bis_inspection.widget.persiandatepicker.PersianDatePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

import static android.view.View.VISIBLE;


public class CarTransactionFragment extends Fragment {

    private Spinner spinner;
    EditText startET, endET, dailyET;
    RelativeLayout layoutDate, layoutSpinner, linearLayout, layoutDaily, layoutAddIcon, layoutOptionText;
    LinearLayout layoutMonthly, rel, layoutCounter, layoutReportStatistical;
    private IDatabaseManager databaseManager;
    private CoreService coreService;
    ProgressDialog progressBar;
    String carId, plateText;
    String strFromDate, strToDate;
    TextView driverNameTV, selectSpinnerDateTV, selectSpinnerDate1TV, plateTextTV, counterTV;
    ListView listView;
    ImageView backIcon, searchIcon, statistical;
    RadioGroup incidentReportTypeOP;
    Date fromDate = null;
    Date toDate = null;
    String selectedMonth = null;
    String selectedYear = null;
    //CarTransactionFragment.ASync myTask = null;
    String displayName = null;
    String addIcon1 = "car";
    String jsonStr = "carId";
    private String reportHejriStrDate;
    DriverChartViolationAdapter pagerAdapter;
    boolean recognize = true;
    private String activityName = "CarTransactionFragment";

    //****************************************

    public CarTransactionFragment() {

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_car_transaction, container, false);

        init(view);
        Spinner yearSpinner = (Spinner) view.findViewById(R.id.spinner_year);
        Spinner monthSpinner = (Spinner) view.findViewById(R.id.spinner_month);
        getCarId();

        PersianDate date = new PersianDate();
        dailyET.setText(date.todayShamsi());

        /**
         * spinner for select violation_report_type_multi_month
         * */
        List<Integer> categories = CommonUtil.easyIntegerList(1, 2, 3);
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_spinner_item, categories);
        spinner.setAdapter(adapter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //***************************

        /**
         * spinner for select violation_report_type_monthly ==== month
         * */
        HejriUtil hejriUtil = new HejriUtil();
        hejriUtil.decodeHejriDate(new Date());
        Integer currentMonth = hejriUtil.getMonth();
        Integer currentYear = hejriUtil.getYear();
        final List<Data> monthDataList = CommonUtil.getMonthDataList(getActivity());
        ArrayAdapter<Data> adapter_month = new ArrayAdapter<Data>(getActivity(), android.R.layout.simple_spinner_item, monthDataList);
        monthSpinner.setAdapter(adapter_month);
        monthSpinner.setSelection(currentMonth - 1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {
                        Data d = monthDataList.get(position);
                        selectedMonth = d.getKey();
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );


        /**
         * spinner for select violation_report_type_monthly ==== year
         * */
        final List<Integer> yearList = CommonUtil.getNYearBeforeList(currentYear, 5);
        ArrayAdapter<Integer> adapter_year = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_spinner_item, yearList);
        yearSpinner.setAdapter(adapter_year);
        yearSpinner.setSelection(4);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {
                        Integer d = yearList.get(position);
                        selectedYear = d.toString();
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                }
        );

        /**
         * select violation type option button*/
        incidentReportTypeOP.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.incident_report_type_multi_month) {
                    linearLayout.setVisibility(VISIBLE);
                    layoutSpinner.setVisibility(VISIBLE);
                    layoutDate.setVisibility(View.INVISIBLE);
                    layoutMonthly.setVisibility(View.INVISIBLE);
                    layoutDaily.setVisibility(View.INVISIBLE);

                } else if (i == R.id.incident_report_type_periodic) {
                    linearLayout.setVisibility(VISIBLE);
                    layoutSpinner.setVisibility(View.INVISIBLE);
                    layoutDate.setVisibility(VISIBLE);
                    layoutMonthly.setVisibility(View.INVISIBLE);
                    layoutDaily.setVisibility(View.INVISIBLE);

                } else if (i == R.id.incident_report_type_daily) {
                    linearLayout.setVisibility(VISIBLE);
                    layoutDaily.setVisibility(VISIBLE);
                    layoutDate.setVisibility(View.INVISIBLE);
                    layoutSpinner.setVisibility(View.INVISIBLE);
                    layoutMonthly.setVisibility(View.INVISIBLE);

                } else if (i == R.id.incident_report_type_monthly) {
                    linearLayout.setVisibility(VISIBLE);
                    layoutMonthly.setVisibility(VISIBLE);
                    layoutDate.setVisibility(View.INVISIBLE);
                    layoutSpinner.setVisibility(View.INVISIBLE);
                    layoutDaily.setVisibility(View.INVISIBLE);
                }
            }
        });
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.motion, R.anim.motion2);
            }
        });

        statistical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCarId();
                Intent intent = new Intent(getActivity(), DriverChartComplaintActivity.class);
                intent.putExtra("Id", carId);
                intent.putExtra("Code", plateText);
                intent.putExtra("activityName", activityName);
                intent.putExtra("jsonStr", jsonStr);
                intent.putExtra("recognize", recognize);
                intent.putExtra("complaintLabel", getActivity().getResources().getString(R.string.line_label_transaction));
                //intent.putExtra("driver_label", getActivity().getResources().getString(R.string.driver_label));
                startActivity(intent);
            }
        });

        layoutReportStatistical.setVisibility(VISIBLE);
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //layoutReportStatistical.setVisibility(VISIBLE);
            }
                /*layoutOptionText.setVisibility(View.GONE);
                layoutReportStatistical.setVisibility(VISIBLE);
                fromDate = null;
                toDate = null;
                int selectedIncidentType = incidentReportTypeOP.getCheckedRadioButtonId();
                if (selectedIncidentType == R.id.incident_report_type_monthly) {
                    String selectedDate = selectedYear + "/" + selectedMonth + "/01";
                    fromDate = HejriUtil.hejriToChris(selectedDate);
                    HejriUtil currentHejriUtil = new HejriUtil(new Date());
                    if (Integer.valueOf(selectedYear).equals(currentHejriUtil.getYear()) && Integer.valueOf(selectedMonth).equals(currentHejriUtil.getMonth())) {
                        toDate = CalendarUtil.midnight(new Date());
                    } else {
                        toDate = HejriUtil.add(fromDate, Calendar.MONTH, 1);
                    }

                } else if (selectedIncidentType == R.id.incident_report_type_daily) {
                    if (dailyET.getText() != null) {
                        //toDate = HejriUtil.hejriToChris(dailyET.getText().toString());
                        //fromDate = HejriUtil.add(toDate, Calendar.DAY_OF_MONTH, -1);
                        fromDate = HejriUtil.hejriToChris(startET.getText().toString());
                        toDate = HejriUtil.hejriToChris(endET.getText().toString());
                    }

                } else if (selectedIncidentType == R.id.incident_report_type_periodic) {
                    if (startET.getText() != null && endET.getText() != null) {
                        fromDate = HejriUtil.hejriToChris(startET.getText().toString());
                        toDate = HejriUtil.hejriToChris(endET.getText().toString());
                    }
                } else if (selectedIncidentType == R.id.incident_report_type_multi_month) {
                    toDate = CalendarUtil.firstDayOfMonth(new Date(), 0);
                    selectSpinnerDate1TV.setVisibility(VISIBLE);
                    if (spinner.getSelectedItem().equals(1)) {
                        fromDate = CalendarUtil.firstDayOfMonth(new Date(), -1);
                    } else if (spinner.getSelectedItem().equals(2)) {
                        fromDate = CalendarUtil.firstDayOfMonth(new Date(), -2);
                    } else if (spinner.getSelectedItem().equals(3)) {
                        fromDate = CalendarUtil.firstDayOfMonth(new Date(), -3);
                    } else {
                        Toast.makeText(getActivity(), "Please selected report type", Toast.LENGTH_LONG).show();
                        return;
                    }
                    long diff = toDate.getTime() - fromDate.getTime();
                    selectSpinnerDateTV.setText(String.valueOf(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)));
                }

                if (fromDate != null && toDate != null) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    strFromDate = simpleDateFormat.format(fromDate);
                    strToDate = simpleDateFormat.format(toDate);
                    Calendar startCalendar = new GregorianCalendar();
                    startCalendar.setTime(fromDate);
                    Calendar endCalendar = new GregorianCalendar();
                    endCalendar.setTime(toDate);
                    int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
                    int diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);

                    boolean hasError = false;
                    if (fromDate.compareTo(new Date()) > 0 || toDate.compareTo(new Date()) > 0) {
                        showErrorDialog(getActivity().getResources().getString(R.string.error_dateIsGreaterThanCurrent));
                        hasError = true;
                    } else {
                        if (DateUtils.dateDiff(toDate, fromDate, Calendar.DAY_OF_MONTH) > 93) {
                            showErrorDialog(getActivity().getResources().getString(R.string.error_selectedMonthIsBigerThanTwo));
                            hasError = true;
                        }
                    }

                    if (!hasError) {
                        String carInfo = getArguments().getString("car");
                        try {
                            JSONObject carInfoJsonObject = new JSONObject(carInfo);
                            carId = carInfoJsonObject.getString("id");
                            plateText = carInfoJsonObject.getString("plateText");
                            plateTextTV.setText(plateText);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //myTask = new ASync();
                        //myTask.execute();

                        rel.setVisibility(VISIBLE);
                    }
                }
            }*/
        });
        startET.setFocusableInTouchMode(false);
        endET.setFocusableInTouchMode(false);
        dailyET.setFocusableInTouchMode(false);
        startET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PersianDatePicker.showDatePicker(getActivity(), inflater, startET);
            }
        });
        startET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                PersianDatePicker.showDatePicker(getActivity(), inflater, startET);
            }
        });

        endET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PersianDatePicker.showDatePicker(getActivity(), inflater, endET);
            }
        });
        endET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                PersianDatePicker.showDatePicker(getActivity(), inflater, endET);
            }
        });
        dailyET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PersianDatePicker.showDatePicker(getActivity(), inflater, dailyET);
            }
        });
        dailyET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                PersianDatePicker.showDatePicker(getActivity(), inflater, dailyET);
            }
        });

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    /*if (myTask != null && myTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
                        myTask.cancel(true);
                    }*/
                    getActivity().finish();
                    return true;
                }

                return false;
            }
        });

        return view;
    }

    private void setUpViewPager(TabLayout tabLayout, final ViewPager viewPager) {
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void init(View view) {
        backIcon = (ImageView) view.findViewById(R.id.backIcon);
        layoutAddIcon = (RelativeLayout) view.findViewById(R.id.addIcon);
        startET = (EditText) view.findViewById(R.id.start_ET);
        endET = (EditText) view.findViewById(R.id.end_ET);
        layoutSpinner = (RelativeLayout) view.findViewById(R.id.layout_Spinner);
        layoutOptionText = (RelativeLayout) view.findViewById(R.id.layout_OptionText);
        layoutDaily = (RelativeLayout) view.findViewById(R.id.layout_Daily);
        layoutMonthly = (LinearLayout) view.findViewById(R.id.layout_Monthly);
        layoutReportStatistical = (LinearLayout) view.findViewById(R.id.layout_ReportStatistical);
        rel = (LinearLayout) view.findViewById(R.id.rel);
        layoutCounter = (LinearLayout) view.findViewById(R.id.layout_counter);
        linearLayout = (RelativeLayout) view.findViewById(R.id.linearLayout);
        layoutDate = (RelativeLayout) view.findViewById(R.id.layout_Date);
        searchIcon = (ImageView) view.findViewById(R.id.search_Icon);
        databaseManager = new DatabaseManager(getActivity());
        coreService = new CoreService(databaseManager);
        driverNameTV = (TextView) view.findViewById(R.id.driverName_TV);
        dailyET = (EditText) view.findViewById(R.id.daily_TV);
        statistical = (ImageView) view.findViewById(R.id.statistical_img);
        counterTV = (TextView) view.findViewById(R.id.counter_TV);
        plateTextTV = (TextView) view.findViewById(R.id.plateText_TV);
        selectSpinnerDateTV = (TextView) view.findViewById(R.id.selectSpinnerDate_TV);
        selectSpinnerDate1TV = (TextView) view.findViewById(R.id.selectSpinnerDate1_TV);
        incidentReportTypeOP = (RadioGroup) view.findViewById(R.id.incidentReportType_OP);
        spinner = (Spinner) view.findViewById(R.id.spinner);
    }

    private void getCarId() {
        String carInfo = getArguments().getString("car");
        try {
            JSONObject carInfoJsonObject = new JSONObject(carInfo);
            JSONObject vehicleJsonObject = carInfoJsonObject.getJSONObject("vehicle");
            carId = carInfoJsonObject.getString("id");
            //plateText = carInfoJsonObject.getString("plateText");
            //displayName = carInfoJsonObject.getString("plateText");

            if (AppController.getInstance().getSharedPreferences().getBoolean(Constants.ON_PROPERTY_CODE, false)) {
                if (!carInfoJsonObject.isNull("propertyCode")) {
                    plateText = carInfoJsonObject.getString("propertyCode");
                    displayName = carInfoJsonObject.getString("propertyCode");
                }
            } else {
                if (!carInfoJsonObject.isNull("plateText")) {
                    plateText = carInfoJsonObject.getString("plateText");
                    displayName = carInfoJsonObject.getString("plateText");
                }
            }


            if (displayName == null) {
                displayName = vehicleJsonObject.getString("vehicleType_text");
                displayName = vehicleJsonObject.getString("name");
            } else {
                displayName += " " + vehicleJsonObject.getString("vehicleType_text") + " " + vehicleJsonObject.getString("name");
            }
            plateTextTV.setText(plateText);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void showErrorDialog(String errorMsg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(getActivity().getResources().getString(R.string.label_error));
        alertDialogBuilder.setMessage(errorMsg);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(getActivity().getResources().getString(R.string.label_close), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
