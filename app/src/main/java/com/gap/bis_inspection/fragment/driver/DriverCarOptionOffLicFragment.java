package com.gap.bis_inspection.fragment.driver;

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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.activity.report.ReportActivity;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.common.HejriUtil;
import com.gap.bis_inspection.db.enumtype.EntityNameEn;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.manager.IDatabaseManager;
import com.gap.bis_inspection.db.objectmodel.DeviceSetting;
import com.gap.bis_inspection.exception.WebServiceException;
import com.gap.bis_inspection.service.CoreService;
import com.gap.bis_inspection.util.DateUtils;
import com.gap.bis_inspection.webservice.MyPostJsonService;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * Created by Mohamad Cheraghi on 07/05/2016.
 */
public class DriverCarOptionOffLicFragment extends Fragment {
    private IDatabaseManager databaseManager;
    private CoreService coreService;
    ProgressBar progressBar;
    String code, driverId;
    TextView generalEnumTV, driverTypeEnTV, optionNoTV, dateConfirmTV, startDateTV, expireDateTV, driverName1TV;
    ImageView backIcon;
    RelativeLayout addIcon;
    String displayName = null;
    String addIcon1 = "driver";
    ASync myTask = null;
    private LinearLayout generalEnum_layout, driverTypeEn_layout, optionNo_layout, dateConfirm_layout, startDate_layout, expireDate_layout;


    public DriverCarOptionOffLicFragment() {

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_driver_car_option_off_lic, container, false);
        databaseManager = new DatabaseManager(getActivity());
        coreService = new CoreService(databaseManager);

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

        String driverProfile = getArguments().getString("driverProfile");
        try {
            JSONObject driverProfileJsonObject = new JSONObject(driverProfile);
            JSONObject personJsonObject = driverProfileJsonObject.getJSONObject("person");
            code = driverProfileJsonObject.getString("driverCode");
            driverId = driverProfileJsonObject.getString("id");
            displayName = personJsonObject.getString("name");

            if (displayName == null) {
                displayName = personJsonObject.getString("family");
            } else {
                displayName += " " + personJsonObject.getString("family");
            }
            String driverCodeStr = getActivity().getResources().getString(R.string.label_driverCode_val2);
            driverName1TV.setText(driverCodeStr + " " + code);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        addIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ReportActivity.class);
                intent.putExtra("entityNameEn", EntityNameEn.DriverProfile.ordinal());
                intent.putExtra("entityId", Long.valueOf(driverId));
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
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        generalEnumTV = (TextView) view.findViewById(R.id.generalEnum_TV);
        driverTypeEnTV = (TextView) view.findViewById(R.id.driverTypeEn_TV);
        optionNoTV = (TextView) view.findViewById(R.id.optionNo_TV);
        dateConfirmTV = (TextView) view.findViewById(R.id.dateConfirm_TV);
        startDateTV = (TextView) view.findViewById(R.id.startDate_TV);
        expireDateTV = (TextView) view.findViewById(R.id.expireDate_TV);
        driverName1TV = (TextView) view.findViewById(R.id.driverName_TV);
        backIcon = (ImageView) view.findViewById(R.id.backIcon);
        addIcon = (RelativeLayout) view.findViewById(R.id.addIcon);
        generalEnum_layout = (LinearLayout) view.findViewById(R.id.generalEnum_layout);
        driverTypeEn_layout = (LinearLayout) view.findViewById(R.id.driverTypeEn_layout);
        optionNo_layout = (LinearLayout) view.findViewById(R.id.optionNo_layout);
        dateConfirm_layout = (LinearLayout) view.findViewById(R.id.dateConfirm_layout);
        startDate_layout = (LinearLayout) view.findViewById(R.id.startDate_layout);
        expireDate_layout = (LinearLayout) view.findViewById(R.id.expireDate_layout);
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

                            if (!jsonObject.isNull("carOption")) {

                                JSONObject carOptionJsonObject = jsonObject.getJSONObject("carOption");
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

                                String strStartDate = null;
                                if (!carOptionJsonObject.isNull("startDate")) {
                                    startDate_layout.setVisibility(View.VISIBLE);
                                    strStartDate = carOptionJsonObject.getString("startDate");
                                    Date startDate1 = simpleDateFormat.parse(strStartDate);
                                    String hejriStartDate = HejriUtil.chrisToHejri(startDate1);
                                    startDateTV.setText(hejriStartDate);
                                } else {
                                    startDateTV.setText("---");
                                }
                                String strDateConfirm = null;
                                if (!carOptionJsonObject.isNull("dateConfirm")) {
                                    dateConfirm_layout.setVisibility(View.VISIBLE);
                                    strDateConfirm = carOptionJsonObject.getString("dateConfirm");
                                    Date dateConfirm1 = simpleDateFormat.parse(strDateConfirm);
                                    String hejriStartDate1 = HejriUtil.chrisToHejri(dateConfirm1);
                                    dateConfirmTV.setText(hejriStartDate1);
                                } else {
                                    dateConfirmTV.setText("---");
                                }

                                String strOptionNo = null;
                                if (!carOptionJsonObject.isNull("optionNo")) {
                                    optionNo_layout.setVisibility(View.VISIBLE);
                                    strOptionNo = carOptionJsonObject.getString("optionNo");
                                    optionNoTV.setText(strOptionNo);
                                } else {
                                    optionNoTV.setText("---");
                                }

                                if (!carOptionJsonObject.isNull("generalEnum")) {
                                    generalEnum_layout.setVisibility(View.VISIBLE);
                                    int type = carOptionJsonObject.getInt("generalEnum");
                                    switch (type) {
                                        case 0:
                                            generalEnumTV.setText(R.string.enumType_CarOptionDriverOfficialLic_jobType1);
                                            break;
                                        case 1:
                                            generalEnumTV.setText(R.string.enumType_CarOptionDriverOfficialLic_jobType2);
                                            break;
                                        case 2:
                                            generalEnumTV.setText(R.string.enumType_CarOptionDriverOfficialLic_jobType3);
                                            break;
                                    }
                                } else {
                                    generalEnumTV.setText("---");
                                }

                                int driverTypeEn;
                                if (!carOptionJsonObject.isNull("driverTypeEn")) {
                                    driverTypeEn_layout.setVisibility(View.VISIBLE);
                                    driverTypeEn = carOptionJsonObject.getInt("driverTypeEn");

                                    switch (driverTypeEn) {
                                        case 0:
                                            driverTypeEnTV.setText(getResources().getString(R.string.driver_OrganizationDriverJobTypeEn_DriverBus));
                                            break;

                                        case 1:
                                            driverTypeEnTV.setText(getResources().getString(R.string.driver_OrganizationDriverJobTypeEn_DriverMotor));
                                            break;

                                        case 2:
                                            driverTypeEnTV.setText(getResources().getString(R.string.driver_OrganizationDriverJobTypeEn_RescuerSOS));
                                            break;

                                        case 3:
                                            driverTypeEnTV.setText(getResources().getString(R.string.driver_OrganizationDriverJobTypeEn_AssistantRescuerSOS));
                                            break;

                                        case 4:
                                            driverTypeEnTV.setText(getResources().getString(R.string.driver_OrganizationDriverJobTypeEn_OfficialWork));
                                            break;

                                        case 5:
                                            driverTypeEnTV.setText(getResources().getString(R.string.driver_OrganizationDriverJobTypeEn_DriverMiniBus));
                                            break;

                                        case 6:
                                            driverTypeEnTV.setText(getResources().getString(R.string.driver_OrganizationDriverJobTypeEn_DriverVeterans));
                                            break;
                                    }


                                } else {
                                    driverTypeEnTV.setText("---");
                                }

                                String strExpireDate = null;
                                if (!carOptionJsonObject.isNull("expireDate")) {
                                    strExpireDate = carOptionJsonObject.getString("expireDate");
                                    Date startDate1 = simpleDateFormat.parse(strExpireDate);
                                    String hejriStartDate = HejriUtil.chrisToHejri(startDate1);
                                    expireDateTV.setText(hejriStartDate);
                                } else {
                                    expireDateTV.setText("---");
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
                    Log.d("DriverOffLicFragment", e.getMessage());
                    Toast.makeText(getActivity(), getResources().getString(R.string.Some_error_accor_contact_admin), Toast.LENGTH_LONG).show();
                } catch (ParseException e) {
                    Log.d("DriverOffLicFragment", e.getMessage());
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
                    jsonObject.put("driverId", driverId);

                    MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, getActivity());
                    try {
                        result = postJsonService.sendData("getDriverCarOptionOfficialLicence", jsonObject, true);
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

    @Override
    public void onStop() {
        super.onStop();
        myTask.cancel(true);
    }

}
