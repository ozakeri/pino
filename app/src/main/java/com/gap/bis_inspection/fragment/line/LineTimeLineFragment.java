package com.gap.bis_inspection.fragment.line;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.activity.line.TimeLineListDetailActivity;
import com.gap.bis_inspection.activity.report.ReportActivity;
import com.gap.bis_inspection.adapter.line.TimeLineAdapter;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.db.enumtype.EntityNameEn;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.manager.IDatabaseManager;
import com.gap.bis_inspection.db.objectmodel.DeviceSetting;
import com.gap.bis_inspection.exception.WebServiceException;
import com.gap.bis_inspection.service.CoreService;
import com.gap.bis_inspection.util.DateUtils;
import com.gap.bis_inspection.webservice.MyPostJsonService;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static android.view.View.VISIBLE;


public class LineTimeLineFragment extends Fragment {
    ImageView backIcon, searchIcon;
    String lineId, code;
    int dayTypeOnHolidayEn;
    private IDatabaseManager databaseManager;
    private CoreService coreService;
    ProgressBar progressBar;
    TextView lineCodeTitleTV, counterTV;
    ListView listView;
    RadioGroup timeLineTypeOp;
    LinearLayout layoutCounter,title_layout;
    String displayName = null;
    RelativeLayout addIcon;
    String addIcon1 = "line";
    ASync myTask = null;

    public LineTimeLineFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time_line, container, false);
        init(view);

        myTask = new ASync();
        myTask.execute();

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.motion, R.anim.motion2);
            }
        });

        String getLineInfo = getArguments().getString("line");
        try {
            JSONObject lineInfoJsonObject = new JSONObject(getLineInfo);
            lineId = lineInfoJsonObject.getString("id");
            code = lineInfoJsonObject.getString("code");
            displayName = lineInfoJsonObject.getString("name");
            if (displayName == null) {
                displayName = lineInfoJsonObject.getString("code");
            } else {
                displayName += " " + lineInfoJsonObject.getString("code");
            }
            String rialStr = getActivity().getResources().getString(R.string.lineCode_label);
            lineCodeTitleTV.setText(rialStr + " " + code);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myTask = new ASync();
                myTask.execute();
            }
        });

        timeLineTypeOp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.timeLineList_one) {
                    dayTypeOnHolidayEn = 0;

                } else if (i == R.id.timeLineList_two) {
                    dayTypeOnHolidayEn = 1;

                } else if (i == R.id.timeLineList_three) {
                    dayTypeOnHolidayEn = 2;
                }
            }
        });

        addIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ReportActivity.class);
                intent.putExtra("entityNameEn", EntityNameEn.Line.ordinal());
                intent.putExtra("entityId", Long.valueOf(lineId));
                intent.putExtra("displayName", displayName);
                intent.putExtra("addIcon", addIcon1);
                startActivity(intent);
            }
        });

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    if (myTask != null && myTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
                        myTask.cancel(true);
                    }
                    getActivity().finish();
                    return true;
                }

                return false;
            }
        });

        return view;
    }

    private void init(View view) {
        backIcon = (ImageView) view.findViewById(R.id.backIcon);
        addIcon = (RelativeLayout) view.findViewById(R.id.addIcon);
        searchIcon = (ImageView) view.findViewById(R.id.search_Icon);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        databaseManager = new DatabaseManager(getActivity());
        coreService = new CoreService(databaseManager);
        lineCodeTitleTV = (TextView) view.findViewById(R.id.lineCodeTitle_TV);
        counterTV = (TextView) view.findViewById(R.id.counter_TV);
        listView = (ListView) view.findViewById(R.id.timeLineList);
        timeLineTypeOp = (RadioGroup) view.findViewById(R.id.timeLineType_OP);
        layoutCounter = (LinearLayout) view.findViewById(R.id.layout_counter);
        title_layout = (LinearLayout) view.findViewById(R.id.rel);
    }

    private class ASync extends AsyncTask<Void, Void, Void> {
        private String result;
        private String errorMsg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.INVISIBLE);
            if (result != null) {
                logLargeString(result);
                try {
                    JSONObject resultJson = new JSONObject(result);
                    if (errorMsg == null && !resultJson.isNull(Constants.SUCCESS_KEY)) {
                        if (!resultJson.isNull(Constants.RESULT_KEY)) {
                            JSONObject jsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                            if (!jsonObject.isNull("timeLineList")) {
                                final JSONArray mainTimeLineJsonArray = jsonObject.getJSONArray("timeLineList");
                                List<JSONObject> jsonObjectList = new ArrayList<>();
                                for (int i = 0; i < mainTimeLineJsonArray.length(); i++) {
                                    JSONObject mainTimeLinJsonObject = mainTimeLineJsonArray.getJSONObject(i);
                                    jsonObjectList.add(mainTimeLinJsonObject);
                                }

                                if (jsonObjectList.size() == 0){
                                    title_layout.setVisibility(View.GONE);
                                }else {
                                    title_layout.setVisibility(VISIBLE);
                                }
                                TimeLineAdapter TimeLineListAdapter = new TimeLineAdapter(getActivity(), R.layout.fragment_time_line_, jsonObjectList);
                                listView.setAdapter(TimeLineListAdapter);
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        try {
                                            JSONObject linePathList = mainTimeLineJsonArray.getJSONObject(i);
                                            Intent intent = new Intent(getActivity(), TimeLineListDetailActivity.class);
                                            intent.putExtra("timeLineList", linePathList.toString());
                                            startActivity(intent);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                layoutCounter.setVisibility(VISIBLE);
                                counterTV.setText(String.valueOf(listView.getCount()));
                            }
                        }
                    } else {
                        if (errorMsg == null) {
                            errorMsg = resultJson.getString(Constants.ERROR_KEY);
                        }
                        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.d("CarUsageFragment", e.getMessage());
                    Toast.makeText(getActivity(), getResources().getString(R.string.Some_error_accor_contact_admin), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getActivity(), (errorMsg != null) ? errorMsg : getResources().getString(R.string.Some_error_accor_contact_admin), Toast.LENGTH_LONG).show();
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
                    jsonObject.put("lineId", lineId);
                    jsonObject.put("dayTypeOnHolidayEn", dayTypeOnHolidayEn);

                    MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, getActivity());
                    try {
                        result = postJsonService.sendData("getTimeLineList", jsonObject, true);
                    } catch (SocketTimeoutException | SocketException e) {
                        errorMsg = getResources().getString(R.string.Some_error_accor_contact_admin);
                    } catch (WebServiceException e) {
                        Log.d("CarUsageFragment", e.getMessage());
                    }

                } catch (JSONException e) {
                    Log.d("CarUsageFragment", e.getMessage());
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
            } catch (SocketTimeoutException | SocketException e) {
                errorMsg = getResources().getString(R.string.Some_error_accor_contact_admin);
            } catch (JSONException | WebServiceException | ParseException e) {
                errorMsg = getResources().getString(R.string.Some_error_accor_contact_admin);
                Log.d("SyncActivity", e.getMessage());
            }
            return false;
        }
    }

    @SuppressLint("LongLogTag")
    public void logLargeString(String str) {
        String Tag = "jsonResultLineTimeLine = ";
        if (str.length() > 3000) {
            Log.i(Tag, str.substring(0, 3000));
            logLargeString(str.substring(3000));
        } else {
            Log.i(Tag, str); // continuation
        }
    }
}
