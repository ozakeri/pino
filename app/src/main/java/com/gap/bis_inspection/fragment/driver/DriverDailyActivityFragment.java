package com.gap.bis_inspection.fragment.driver;


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
import com.gap.bis_inspection.adapter.driver.DailyEventListAdapter;
import com.gap.bis_inspection.adapter.driver.DriverDailyEventListAdapter;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.manager.IDatabaseManager;
import com.gap.bis_inspection.db.objectmodel.DeviceSetting;
import com.gap.bis_inspection.exception.WebServiceException;
import com.gap.bis_inspection.service.CoreService;
import com.gap.bis_inspection.util.DateUtils;
import com.gap.bis_inspection.util.RecyclerItemClickListener;
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
public class DriverDailyActivityFragment extends Fragment {

    private AppCompatTextView txt_companyName, txt_driverShift, txt_status, txt_car, txt_line, txt_companyNameChanged, txt_carChanged, txt_lineChanged, txt_driverShiftChanged;
    private RecyclerView recyclerView, driverList;
    private ProgressBar progressBar;
    private String driverId, code;
    private ASync myTask;
    private IDatabaseManager databaseManager;
    private CoreService coreService;
    private LinearLayout layout_company, layout_line, layout_car, layout_shift, event_layout;
    private RelativeLayout relativeLayoutDetail, layout_hamShift;
    private boolean openClose = false;
    private ImageView iv_fab;
    private List<JSONObject> jsonObjectList;


    public DriverDailyActivityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_driver_daily_activity, container, false);

        txt_companyName = view.findViewById(R.id.txt_companyName);
        txt_companyNameChanged = view.findViewById(R.id.txt_companyNameChanged);
        txt_driverShift = view.findViewById(R.id.txt_driverShift);
        txt_driverShiftChanged = view.findViewById(R.id.txt_driverShiftChanged);
        txt_status = view.findViewById(R.id.txt_status);
        txt_car = view.findViewById(R.id.txt_car);
        txt_carChanged = view.findViewById(R.id.txt_carChanged);
        txt_line = view.findViewById(R.id.txt_line);
        txt_lineChanged = view.findViewById(R.id.txt_lineChanged);
        progressBar = view.findViewById(R.id.progress);
        layout_company = view.findViewById(R.id.layout_company);
        layout_line = view.findViewById(R.id.layout_line);
        layout_car = view.findViewById(R.id.layout_car);
        layout_shift = view.findViewById(R.id.layout_shift);
        layout_hamShift = view.findViewById(R.id.layout_hamShift);
        relativeLayoutDetail = view.findViewById(R.id.relativeLayout);
        iv_fab = view.findViewById(R.id.iv_fab);
        recyclerView = view.findViewById(R.id.eventList);
        driverList = view.findViewById(R.id.driverList);
        event_layout = view.findViewById(R.id.event_layout);
        databaseManager = new DatabaseManager(getActivity());
        coreService = new CoreService(databaseManager);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setHasFixedSize(true);
        driverList.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        driverList.setLayoutManager(layoutManager1);


        if (getArguments() != null) {
            String driverProfile = getArguments().getString("driverProfile");
            try {
                JSONObject driverProfileJsonObject = new JSONObject(driverProfile);
                driverId = driverProfileJsonObject.getString("id");
                code = driverProfileJsonObject.getString("driverCode");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            myTask = new ASync();
            myTask.execute();
        }

        relativeLayoutDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!openClose) {
                    event_layout.setVisibility(View.VISIBLE);
                    openClose = true;
                    iv_fab.setBackgroundResource(R.drawable.negative_icon);

                } else {
                    event_layout.setVisibility(View.GONE);
                    openClose = false;
                    iv_fab.setBackgroundResource(R.drawable.plus_icon);
                }

            }
        });


        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                JSONObject jsonObject = jsonObjectList.get(position);
                //Intent intent = new Intent(getContext(),)
            }
        }));

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
                                JSONObject companyProfitJSONObject = jsonObject.getJSONObject("entityDailyActivity");

                                if (!companyProfitJSONObject.isNull("companyNameFV")) {
                                    String companyNameFV = companyProfitJSONObject.getString("companyNameFV");
                                    txt_companyName.setText(companyNameFV);
                                }

                                if (!companyProfitJSONObject.isNull("companyIsChangedFV")) {
                                    boolean companyIsChangedFV = companyProfitJSONObject.getBoolean("companyIsChangedFV");
                                    if (companyIsChangedFV) {
                                        //TODO CHECK
                                        layout_company.setVisibility(View.VISIBLE);
                                        if (!companyProfitJSONObject.isNull("companyChangedFV")) {
                                            String companyChangedFV = companyProfitJSONObject.getString("companyChangedFV");
                                            txt_companyNameChanged.setText(companyChangedFV);
                                        }
                                    }
                                }

                                if (!companyProfitJSONObject.isNull("processStatus")) {
                                    int processStatus = companyProfitJSONObject.getInt("processStatus");
                                    switch (processStatus) {
                                        case 5:
                                            txt_status.setText(R.string.processStatus_State1);
                                            break;

                                        case 8:
                                            txt_status.setText(R.string.processStatus_State4);
                                            break;

                                        case 9:
                                            txt_status.setText(R.string.processStatus_State5);
                                            txt_status.setTextColor(getResources().getColor(R.color.toolbarDriver));
                                            break;

                                        case 10:
                                            txt_status.setText(R.string.processStatus_State6);
                                            break;

                                        case 20:
                                            txt_status.setText(R.string.processStatus_State7);
                                            break;

                                        case 21:
                                            txt_status.setText(R.string.processStatus_State8);
                                            break;

                                        case 22:
                                            txt_status.setText(R.string.processStatus_State9);
                                            break;

                                        case 24:
                                            txt_status.setText(R.string.processStatus_State11);
                                            break;

                                        case 25:
                                            txt_status.setText(R.string.processStatus_State12);
                                            break;

                                        case 11:
                                            txt_status.setText(R.string.processStatus_State50);
                                            break;

                                        case 27:
                                            txt_status.setText(R.string.processStatus_employeeDuty);
                                            break;

                                        case 6:
                                            txt_status.setText(R.string.processStatus_State2);
                                            break;

                                        case 7:
                                            txt_status.setText(R.string.processStatus_State3);
                                            break;

                                        case 23:
                                            txt_status.setText(R.string.processStatus_State10);
                                            break;

                                       /* case 5:
                                            txt_status.setText(R.string.processStatus_State1_car);
                                            break;

                                        case 6:
                                            txt_status.setText(R.string.processStatus_State2_car);
                                            break;
                                        case 7:
                                            txt_status.setText(R.string.processStatus_State3_car);
                                            break;*/

                                    }
                                }

                                if (!companyProfitJSONObject.isNull("driverShiftPlanStrFV")) {
                                    String driverShiftPlanStrFV = companyProfitJSONObject.getString("driverShiftPlanStrFV");
                                    txt_driverShift.setText(driverShiftPlanStrFV);
                                }

                                if (!companyProfitJSONObject.isNull("driverShiftPlanIsChangedFV")) {
                                    boolean driverShiftPlanIsChangedFV = companyProfitJSONObject.getBoolean("driverShiftPlanIsChangedFV");

                                    if (driverShiftPlanIsChangedFV) {
                                        //TODO CHECK
                                        layout_shift.setVisibility(View.VISIBLE);
                                        if (!companyProfitJSONObject.isNull("driverShiftChangedStrFV")) {
                                            String driverShiftChangedStrFV = companyProfitJSONObject.getString("driverShiftChangedStrFV");
                                            txt_driverShiftChanged.setText(driverShiftChangedStrFV);
                                        }
                                    }
                                }

                                if (!companyProfitJSONObject.isNull("carPlanStrFV")) {
                                    String carPlanStrFV = companyProfitJSONObject.getString("carPlanStrFV");
                                    txt_car.setText(carPlanStrFV);
                                }

                                if (!companyProfitJSONObject.isNull("carPlanIsChangedFV")) {
                                    boolean carPlanIsChangedFV = companyProfitJSONObject.getBoolean("carPlanIsChangedFV");

                                    if (carPlanIsChangedFV) {
                                        //TODO CHECK
                                        layout_car.setVisibility(View.VISIBLE);
                                        if (!companyProfitJSONObject.isNull("carChangedStrFV")) {
                                            String carChangedStrFV = companyProfitJSONObject.getString("carChangedStrFV");
                                            txt_carChanged.setText(carChangedStrFV);
                                        }
                                    }
                                }

                                if (!companyProfitJSONObject.isNull("linePlanStrFV")) {
                                    String linePlanStrFV = companyProfitJSONObject.getString("linePlanStrFV");
                                    txt_line.setText(linePlanStrFV);
                                }

                                if (!companyProfitJSONObject.isNull("linePlanIsChangedFV")) {
                                    boolean linePlanIsChangedFV = companyProfitJSONObject.getBoolean("linePlanIsChangedFV");

                                    if (linePlanIsChangedFV) {
                                        //TODO CHECK
                                        layout_line.setVisibility(View.VISIBLE);
                                        if (!companyProfitJSONObject.isNull("lineChangedStrFV")) {
                                            String lineChangedStrFV = companyProfitJSONObject.getString("lineChangedStrFV");
                                            txt_lineChanged.setText(lineChangedStrFV);
                                        }
                                    }
                                }

                                if (!companyProfitJSONObject.isNull("driverEDAList")) {
                                    layout_hamShift.setVisibility(View.VISIBLE);
                                    List<JSONObject> driverEDAListJsonObjectList = new ArrayList<>();
                                    JSONArray driverEDAListJsonArray = companyProfitJSONObject.getJSONArray("driverEDAList");
                                    for (int i = 0; i < driverEDAListJsonArray.length(); i++) {
                                        JSONObject driverEDAJsonObject = driverEDAListJsonArray.getJSONObject(i);
                                        driverEDAListJsonObjectList.add(driverEDAJsonObject);
                                    }

                                    System.out.println("driverEDAListJsonObjectList===" + driverEDAListJsonObjectList.size());
                                    DriverDailyEventListAdapter adapter = new DriverDailyEventListAdapter(driverEDAListJsonObjectList);
                                    driverList.setAdapter(adapter);
                                }


                                if (!companyProfitJSONObject.isNull("dailyEventList")) {
                                    jsonObjectList = new ArrayList<>();
                                    JSONArray dailyEventListJsonArray = companyProfitJSONObject.getJSONArray("dailyEventList");
                                    for (int i = 0; i < dailyEventListJsonArray.length(); i++) {
                                        JSONObject dailyEventJsonObject = dailyEventListJsonArray.getJSONObject(i);
                                        jsonObjectList.add(dailyEventJsonObject);
                                    }

                                    DailyEventListAdapter adapter = new DailyEventListAdapter(jsonObjectList);
                                    recyclerView.setAdapter(adapter);
                                } else {
                                    relativeLayoutDetail.setVisibility(View.GONE);
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
                    jsonObject.put("driverCode", code);

                    MyPostJsonService postJsonService = new MyPostJsonService(null, getActivity());
                    try {
                        result = postJsonService.sendData("getDriverDailyActivity", jsonObject, true);
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
