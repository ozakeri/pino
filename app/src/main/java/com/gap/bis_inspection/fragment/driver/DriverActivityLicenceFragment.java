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
import com.gap.bis_inspection.common.CalendarUtil;
import com.gap.bis_inspection.common.Constants;
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
public class DriverActivityLicenceFragment extends Fragment {

    private IDatabaseManager databaseManager;
    private CoreService coreService;
    TextView licenceNoTV, driveLicenceTypeEnTV, hejriStartDateTV, hejriExpireDateTV, driverName1TV;
    ProgressBar progressBar;
    String code, driverId;
    ImageView backIcon;
    RelativeLayout addIcon;
    String displayName = null;
    String addIcon1 = "driver";
    ASync myTask = null;
    LinearLayout liceNumber_layout, liceType_layout, liceExp_layout;

    public DriverActivityLicenceFragment() {

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_activity_licence, container, false);

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
        licenceNoTV = (TextView) view.findViewById(R.id.licenceNo_TV);
        driveLicenceTypeEnTV = (TextView) view.findViewById(R.id.driveLicenceTypeEn_TV);
        hejriStartDateTV = (TextView) view.findViewById(R.id.hejriStartDate_TV);
        hejriExpireDateTV = (TextView) view.findViewById(R.id.hejriExpireDate_TV);
        driverName1TV = (TextView) view.findViewById(R.id.driverName_TV);
        backIcon = (ImageView) view.findViewById(R.id.backIcon);
        addIcon = (RelativeLayout) view.findViewById(R.id.addIcon);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        liceNumber_layout = (LinearLayout) view.findViewById(R.id.liceNumber_layout);
        liceType_layout = (LinearLayout) view.findViewById(R.id.liceType_layout);
        liceExp_layout = (LinearLayout) view.findViewById(R.id.liceExp_layout);
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


                            if (!jsonObject.isNull("licence")) {
                                JSONObject licenceJsonObject = jsonObject.getJSONObject("licence");

                                String strLicenceNo = "";
                                if (!licenceJsonObject.isNull("licenceNo")) {
                                    liceNumber_layout.setVisibility(View.VISIBLE);
                                    strLicenceNo = licenceJsonObject.getString("licenceNo");
                                    licenceNoTV.setText(strLicenceNo);
                                } else {
                                    licenceNoTV.setText("---");
                                }

                                int LicenceTypeEn;
                                if (!licenceJsonObject.isNull("driveLicenceTypeEn")) {
                                    LicenceTypeEn = licenceJsonObject.getInt("driveLicenceTypeEn");
                                    liceType_layout.setVisibility(View.VISIBLE);
                                    try {

                                        switch (LicenceTypeEn) {
                                            case 0:
                                                driveLicenceTypeEnTV.setText(getResources().getString(R.string.driver_DriverLicenceTypeEn_BusDriverOrganization));
                                                break;

                                            case 1:
                                                driveLicenceTypeEnTV.setText(getResources().getString(R.string.driver_DriverLicenceTypeEn_BusDriverPrivateCo));
                                                break;

                                            case 2:
                                                driveLicenceTypeEnTV.setText(getResources().getString(R.string.driver_DriverLicenceTypeEn_MiniBousDriverPrivateCo));
                                                break;
                                        }

                                    } catch (Exception e) {
                                        e.getMessage();
                                    }


                                } else {
                                    driveLicenceTypeEnTV.setText("---");
                                }

                              /*  String strHejriStartDate = "";
                                if (!licenceJsonObject.isNull("hejriStartDate")) {
                                    strHejriStartDate = licenceJsonObject.getString("hejriStartDate");
                                    hejriStartDate.setText(strHejriStartDate);
                                }*/
                                String strHejriExpireDate = "";
                                if (!licenceJsonObject.isNull("expireDate")) {
                                    liceExp_layout.setVisibility(View.VISIBLE);
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                    strHejriExpireDate = licenceJsonObject.getString("expireDate");
                                    //hejriExpireDate.setText(strHejriExpireDate);
                                    Date expireDate = simpleDateFormat.parse(strHejriExpireDate);
                                    Date currentDate = new Date(System.currentTimeMillis());
                                    hejriExpireDateTV.setText(CalendarUtil.datesDiff(getActivity(), currentDate, expireDate, "yMd"));

                                } else {
                                    hejriExpireDateTV.setText("---");
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
                    Log.d("RegistrationFragment", e.getMessage());
                    Toast.makeText(getActivity(), getResources().getString(R.string.Some_error_accor_contact_admin), Toast.LENGTH_LONG).show();
                } catch (ParseException e) {
                    e.printStackTrace();
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
                        result = postJsonService.sendData("getDriverLicence", jsonObject, true);
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
