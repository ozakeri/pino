package com.gap.bis_inspection.fragment.line;


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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.activity.report.ReportActivity;
import com.gap.bis_inspection.adapter.LinePathAdapter;
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

public class LinePathFragment extends Fragment {
    ImageView backIcon;
    String lineId, code;
    private IDatabaseManager databaseManager;
    private CoreService coreService;
    ProgressBar progressBar;
    ListView listView;
    TextView lineCodeTitleTV, counterTV;
    LinearLayout layoutCounter;
    String displayName = null;
    RelativeLayout addIcon;
    String addIcon1 = "line";
    ASync myTask = null;

    public LinePathFragment() {
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_line_path, container, false);
        init(view);
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

        myTask = new ASync();
        myTask.execute();

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
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        databaseManager = new DatabaseManager(getActivity());
        coreService = new CoreService(databaseManager);
        lineCodeTitleTV = (TextView) view.findViewById(R.id.lineCodeTitle_TV);
        counterTV = (TextView) view.findViewById(R.id.counter_TV);
        listView = (ListView) view.findViewById(R.id.linePathList);
        layoutCounter = (LinearLayout) view.findViewById(R.id.layout_counter);
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
                try {
                    JSONObject resultJson = new JSONObject(result);
                    if (errorMsg == null && !resultJson.isNull(Constants.SUCCESS_KEY)) {
                        if (!resultJson.isNull(Constants.RESULT_KEY)) {
                            JSONObject jsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                            if (!jsonObject.isNull("linePathList")) {
                                final JSONArray linePathListJsonArray = jsonObject.getJSONArray("linePathList");
                                List<JSONObject> jsonObjectList = new ArrayList<>();
                                for (int i = 0; i < linePathListJsonArray.length(); i++) {
                                    JSONObject linePathListJsonObject = linePathListJsonArray.getJSONObject(i);
                                    jsonObjectList.add(linePathListJsonObject);
                                }
                                LinePathAdapter LineListAdapter = new LinePathAdapter(getActivity(), R.layout.fragment_line_path_, jsonObjectList);
                                listView.setAdapter(LineListAdapter);
                              /*  listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        try {
                                            JSONObject linePathList = linePathListJsonArray.getJSONObject(i);
                                            Intent intent = new Intent(getActivity(), LinePathDetailActivity.class);
                                            intent.putExtra("linePathList", linePathList.toString());
                                            startActivity(intent);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });*/
                                layoutCounter.setVisibility(View.VISIBLE);
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

                    MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, getActivity());
                    try {
                        result = postJsonService.sendData("getLinePathList", jsonObject, true);
                    } catch (SocketTimeoutException e) {
                        errorMsg = getResources().getString(R.string.Some_error_accor_contact_admin);
                    } catch (SocketException e) {
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
}
