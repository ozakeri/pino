package com.gap.bis_inspection.fragment.car;

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
import android.widget.ImageView;
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
 * Created by Mohamad Cheraghi on 08/20/2016.
 */
public class CarActivityLicenceFragment extends Fragment {
    ImageView backIcon;
    String carId, plateText;
    private IDatabaseManager databaseManager;
    private CoreService coreService;
    ProgressBar progressBar;
    TextView driverNameFvTV, lineNameFvTV, hejriStartDateTV, hejriExpireDateTV, plateTextTV, carOptionIdTV;
    String displayName = null;
    RelativeLayout addIcon;
    String addIcon1 = "car";
    ASync myTask = null;

    public CarActivityLicenceFragment() {

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_car_activity_licence, container, false);
        backIcon = (ImageView) view.findViewById(R.id.backIcon);
        addIcon = (RelativeLayout) view.findViewById(R.id.addIcon);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        databaseManager = new DatabaseManager(getActivity());
        coreService = new CoreService(databaseManager);
        init(view);

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.motion, R.anim.motion2);
            }
        });

        String carInfo = getArguments().getString("car");
        try {
            JSONObject carInfoJsonObject = new JSONObject(carInfo);
            JSONObject vehicleJsonObject = carInfoJsonObject.getJSONObject("vehicle");
            carId = carInfoJsonObject.getString("id");
            plateText = carInfoJsonObject.getString("plateText");
            displayName = carInfoJsonObject.getString("plateText");

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
        myTask = new ASync();
        myTask.execute();

        addIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ReportActivity.class);
                intent.putExtra("entityNameEn", EntityNameEn.Car.ordinal());
                intent.putExtra("entityId", Long.valueOf(carId));
                intent.putExtra("displayName", displayName);
                intent.putExtra("addIcon", addIcon1);
                startActivity(intent);
            }
        });


        /**
         * end task when on back pressed
         * */
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
        driverNameFvTV = (TextView) view.findViewById(R.id.driverNameFv_TV);
        lineNameFvTV = (TextView) view.findViewById(R.id.lineNameFv_TV);
        hejriStartDateTV = (TextView) view.findViewById(R.id.hejriStartDate_TV);
        hejriExpireDateTV = (TextView) view.findViewById(R.id.hejriExpireDate_TV);
        plateTextTV = (TextView) view.findViewById(R.id.txt_plateText);
        carOptionIdTV = (TextView) view.findViewById(R.id.id_TV);
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
                                carOptionIdTV.setText(carOptionJsonObject.getString("id"));
                            } else {
                                carOptionIdTV.setText("---");
                            }

                            if (!jsonObject.isNull("carOption")) {
                                JSONObject carOptionJsonObject = jsonObject.getJSONObject("carOption");
                                JSONObject carJsonObject = carOptionJsonObject.getJSONObject("driver");
                                String nameFv = carJsonObject.getString("nameFv");
                                driverNameFvTV.setText(nameFv);
                            } else {
                                driverNameFvTV.setText("---");
                            }


                            if (!jsonObject.isNull("carOption")) {
                                JSONObject carOptionJsonObject = jsonObject.getJSONObject("carOption");
                                JSONObject lineJsonObject = carOptionJsonObject.getJSONObject("line");
                                String name = lineJsonObject.getString("nameFv");
                                lineNameFvTV.setText(name);
                            } else {
                                lineNameFvTV.setText("---");
                            }

                            if (!jsonObject.isNull("carOption")) {
                                JSONObject carOptionJsonObject = jsonObject.getJSONObject("carOption");
                                String startDate = carOptionJsonObject.getString("startDate");
                                //HejriUtil.chrisToHejri(startDate);
                                hejriStartDateTV.setText(HejriUtil.chrisToHejri(startDate));
                            } else {
                                hejriStartDateTV.setText("---");
                            }

                            if (!jsonObject.isNull("carOption")) {
                                JSONObject carOptionJsonObject = jsonObject.getJSONObject("carOption");
                                String expireDate = carOptionJsonObject.getString("expireDate");
                                hejriExpireDateTV.setText(HejriUtil.chrisToHejri(expireDate));
                            } else {
                                hejriExpireDateTV.setText("---");
                            }
                        }
                    } else {
                        errorMsg = resultJson.getString(Constants.ERROR_KEY);
                        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.d("Car_Licence_Fragment", e.getMessage());
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
                    //jsonObject.put("carId", carId);
                    jsonObject.put("carId", carId);
                    MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, getActivity());
                    try {
                        result = postJsonService.sendData("getCarOptionActivityLicence", jsonObject, true);
                    } catch (SocketTimeoutException e) {
                        errorMsg = getResources().getString(R.string.Some_error_accor_contact_admin);
                    } catch (SocketException e) {
                        errorMsg = getResources().getString(R.string.Some_error_accor_contact_admin);
                    } catch (WebServiceException e) {
                        Log.d("Car_Licence_Fragment", e.getMessage());
                    }

                } catch (JSONException e) {
                    Log.d("Car_Licence_Fragment", e.getMessage());
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

    @SuppressLint("LongLogTag")
    public void logLargeString(String str) {
        String Tag = "CarActivityLicenceFragment = ";
        if (str.length() > 3000) {
            Log.i(Tag, str.substring(0, 3000));
            logLargeString(str.substring(3000));
        } else {
            Log.i(Tag, str); // continuation
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        myTask.cancel(true);
    }
}
