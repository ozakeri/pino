package com.gap.bis_inspection.fragment.car;


import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.adapter.driver.CarDailyEventListAdapter;
import com.gap.bis_inspection.adapter.driver.CarEDAListAdapter;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.Constants;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class CarDailyActivityFragment extends Fragment {

    private AppCompatTextView txt_propertyCode, txt_platText, txt_companyName, txt_companyController, txt_carUsage, txt_activityStatus, txt_carStatusInPark;
    private RecyclerView driverList, eventList, openEventList;
    private ProgressBar progressBar;
    private String driverId, code;
    private IDatabaseManager databaseManager;
    private CoreService coreService;
    private LinearLayout event_layout, driver_layout, openEvent_layout;
    private RelativeLayout relativeLayout1, relativeLayout2, relativeLayout3;
    private boolean openClose = false;
    private ImageView iv_fab1, iv_fab2, iv_fab3;
    private List<JSONObject> jsonObjectList;
    private ASync myTask;
    String carId, plateText, displayName;
    private String propertyCode;
    private List<JSONObject> driverEDAListJsonObjectList;


    public CarDailyActivityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_car_daily_activity, container, false);

        txt_companyName = view.findViewById(R.id.txt_companyName);
        txt_propertyCode = view.findViewById(R.id.txt_propertyCode);
        txt_platText = view.findViewById(R.id.txt_platText);
        txt_companyController = view.findViewById(R.id.txt_companyController);
        txt_carUsage = view.findViewById(R.id.txt_carUsage);
        txt_activityStatus = view.findViewById(R.id.txt_activityStatus);
        txt_carStatusInPark = view.findViewById(R.id.txt_carStatusInPark);
        progressBar = view.findViewById(R.id.progress);

        relativeLayout1 = view.findViewById(R.id.relativeLayout1);
        relativeLayout2 = view.findViewById(R.id.relativeLayout2);
        relativeLayout3 = view.findViewById(R.id.relativeLayout3);
        iv_fab1 = view.findViewById(R.id.iv_fab1);
        iv_fab2 = view.findViewById(R.id.iv_fab2);
        iv_fab3 = view.findViewById(R.id.iv_fab3);
        driverList = view.findViewById(R.id.driverList);
        eventList = view.findViewById(R.id.eventList);
        openEventList = view.findViewById(R.id.openEventList);
        event_layout = view.findViewById(R.id.event_layout);
        driver_layout = view.findViewById(R.id.driver_layout);
        openEvent_layout = view.findViewById(R.id.openEvent_layout);
        databaseManager = new DatabaseManager(getActivity());
        coreService = new CoreService(databaseManager);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        driverList.setHasFixedSize(true);
        eventList.setHasFixedSize(true);
        openEventList.setHasFixedSize(true);
        driverList.setLayoutManager(layoutManager1);
        eventList.setLayoutManager(layoutManager2);
        openEventList.setLayoutManager(layoutManager3);

        event_layout.setVisibility(View.GONE);
        driver_layout.setVisibility(View.GONE);
        openEvent_layout.setVisibility(View.GONE);


        if (getArguments() != null) {
            String carInfo = getArguments().getString("car");
            try {
                JSONObject carInfoJsonObject = new JSONObject(carInfo);
                propertyCode = carInfoJsonObject.getString("propertyCode");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            myTask = new ASync();
            myTask.execute();
        }

        relativeLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!openClose) {
                    openEvent_layout.setVisibility(View.VISIBLE);
                    openClose = true;
                    iv_fab1.setBackgroundResource(R.drawable.negative_icon);

                } else {
                    openEvent_layout.setVisibility(View.GONE);
                    openClose = false;
                    iv_fab1.setBackgroundResource(R.drawable.plus_icon);
                }

            }
        });

        relativeLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!openClose) {
                    driver_layout.setVisibility(View.VISIBLE);
                    openClose = true;
                    iv_fab2.setBackgroundResource(R.drawable.negative_icon);

                } else {
                    driver_layout.setVisibility(View.GONE);
                    openClose = false;
                    iv_fab2.setBackgroundResource(R.drawable.plus_icon);
                }

            }
        });

        relativeLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!openClose) {
                    event_layout.setVisibility(View.VISIBLE);
                    openClose = true;
                    iv_fab3.setBackgroundResource(R.drawable.negative_icon);

                } else {
                    event_layout.setVisibility(View.GONE);
                    openClose = false;
                    iv_fab3.setBackgroundResource(R.drawable.plus_icon);
                }

            }
        });


        return view;
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
                            if (!jsonObject.isNull("entityDailyActivity")) {
                                JSONObject entityDailyActivityJSONObject = jsonObject.getJSONObject("entityDailyActivity");

                                if (!entityDailyActivityJSONObject.isNull("propertyCode")) {
                                    String propertyCode = entityDailyActivityJSONObject.getString("propertyCode");
                                    txt_propertyCode.setText(propertyCode);
                                }

                                if (!entityDailyActivityJSONObject.isNull("plateText")) {
                                    String plateText = entityDailyActivityJSONObject.getString("plateText");
                                    txt_platText.setText(plateText);
                                }

                                if (!entityDailyActivityJSONObject.isNull("activityStatus")) {
                                    int activityStatus = entityDailyActivityJSONObject.getInt("activityStatus");
                                    switch (activityStatus) {
                                        case 0:
                                            txt_activityStatus.setText("عدم اشتغال");
                                            break;
                                        case 1:
                                            txt_activityStatus.setText("شاغل");
                                            break;
                                    }

                                }

                                if (!entityDailyActivityJSONObject.isNull("carStatusInParkStrFV")) {
                                    String carStatusInParkStrFV = entityDailyActivityJSONObject.getString("carStatusInParkStrFV");
                                    txt_carStatusInPark.setText(carStatusInParkStrFV);
                                }

                                if (!entityDailyActivityJSONObject.isNull("company")) {
                                    JSONObject companyJSONObject = entityDailyActivityJSONObject.getJSONObject("company");
                                    String company = companyJSONObject.getString("name");
                                    txt_companyName.setText(company);
                                }

                                if (!entityDailyActivityJSONObject.isNull("carUsage")) {
                                    JSONObject carUsageJSONObject = entityDailyActivityJSONObject.getJSONObject("carUsage");
                                    String carUsage = carUsageJSONObject.getString("nameFv");
                                    txt_carUsage.setText(carUsage);
                                }

                                if (!entityDailyActivityJSONObject.isNull("dailyEventList")) {
                                    relativeLayout3.setVisibility(View.VISIBLE);
                                    JSONArray dailyEventListJsonArray = entityDailyActivityJSONObject.getJSONArray("dailyEventList");
                                    List<JSONObject> dailyEventListJsonObjectList = new ArrayList<>();
                                    for (int i = 0; i < dailyEventListJsonArray.length(); i++) {
                                        JSONObject driverEDAJsonObject = dailyEventListJsonArray.getJSONObject(i);
                                        dailyEventListJsonObjectList.add(driverEDAJsonObject);
                                    }
                                    System.out.println("dailyEventListJsonObjectList===" + dailyEventListJsonObjectList.size());
                                    CarDailyEventListAdapter adapter = new CarDailyEventListAdapter(getContext(), dailyEventListJsonObjectList);
                                    eventList.setAdapter(adapter);
                                }


                                if (!entityDailyActivityJSONObject.isNull("driverEDAVOList")) {
                                    relativeLayout2.setVisibility(View.VISIBLE);
                                    JSONArray driverEDAListJsonArray = entityDailyActivityJSONObject.getJSONArray("driverEDAVOList");
                                    List<JSONObject> driverEDAListJsonObjectList = new ArrayList<>();
                                    for (int i = 0; i < driverEDAListJsonArray.length(); i++) {
                                        JSONObject driverEDAJsonObject = driverEDAListJsonArray.getJSONObject(i);
                                        driverEDAListJsonObjectList.add(driverEDAJsonObject);
                                    }

                                    System.out.println("driverEDAListJsonObjectList===" + driverEDAListJsonObjectList.size());
                                    CarEDAListAdapter adapter = new CarEDAListAdapter(getContext(), driverEDAListJsonObjectList);
                                    driverList.setAdapter(adapter);
                                }

                            }

                        }
                    } else {
                        if (errorMsg == null) {
                            errorMsg = resultJson.getString(Constants.ERROR_KEY);
                        }
                        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.d("getDriverDailyActivity", e.getMessage());
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
                    jsonObject.put("propertyCode", propertyCode);

                    MyPostJsonService postJsonService = new MyPostJsonService(null, getActivity());
                    try {
                        result = postJsonService.sendData("getCarDailyActivity", jsonObject, true);
                    } catch (SocketTimeoutException e) {
                        errorMsg = getResources().getString(R.string.Some_error_accor_contact_admin);
                    } catch (SocketException e) {
                        errorMsg = "Connection SocketException";
                    } catch (WebServiceException e) {
                        Log.d("DriverJobFragment", e.getMessage());
                    }

                } catch (JSONException e) {
                    Log.d("DriverJobFragment", e.getMessage());
                }
            }
            return null;
        }

        private boolean isDeviceDateTimeValid() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);
            try {
                JSONObject jsonObjectParam = new JSONObject();
                MyPostJsonService postJsonService = new MyPostJsonService(null, getActivity());
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

    @Override
    public void onStop() {
        super.onStop();
        myTask.cancel(true);
    }
}
