package com.gap.bis_inspection.fragment.car;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.gap.bis_inspection.adapter.form.EntityShiftListAdapter;
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

public class CarUsageFragment extends Fragment {
    ImageView backIcon;
    String carId, plateText;
    private IDatabaseManager databaseManager;
    private CoreService coreService;
    ProgressBar progressBar;
    TextView typeTV, hejriStartDateTV, endDateTV, lineNameTV,
            companyProfitTV, companyControllerTV, plateTextTV, priceCoFindTV, eCardPriceTV;
    RecyclerView shiftList;
    String displayName = null;
    RelativeLayout addIcon;
    String addIcon1 = "car";
    ASync myTask = null;

    public CarUsageFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_car_usage, container, false);

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

        if (getArguments() != null) {
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
            } catch (JSONException e) {
                e.printStackTrace();
            }
            myTask = new ASync();
            myTask.execute();
        }


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
        backIcon = (ImageView) view.findViewById(R.id.back_Icon);
        addIcon = (RelativeLayout) view.findViewById(R.id.add_Icon);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        typeTV = (TextView) view.findViewById(R.id.type_TV);
        hejriStartDateTV = (TextView) view.findViewById(R.id.hejriStartDate_TV);
        endDateTV = (TextView) view.findViewById(R.id.endDate_TV);
        lineNameTV = (TextView) view.findViewById(R.id.lineName_TV);
        companyProfitTV = (TextView) view.findViewById(R.id.companyProfit_TV);
        companyControllerTV = (TextView) view.findViewById(R.id.companyController_TV);
        plateTextTV = (TextView) view.findViewById(R.id.plateText_TV);
        priceCoFindTV = (TextView) view.findViewById(R.id.priceCoFind_TV);
        eCardPriceTV = (TextView) view.findViewById(R.id.eCardPrice_TV);

        shiftList = view.findViewById(R.id.shift_List);
        shiftList.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        shiftList.setLayoutManager(layoutManager);
    }

    private class ASync extends AsyncTask<Void, Void, Void> {
        private String result;
        private String errorMsg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.INVISIBLE);
            if (result != null) {
                try {
                    logLargeString(result);
                    JSONObject resultJson = new JSONObject(result);
                    if (errorMsg == null && !resultJson.isNull(Constants.SUCCESS_KEY)) {
                        if (!resultJson.isNull(Constants.RESULT_KEY)) {
                            JSONObject jsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            JSONObject carUsageJsonObject = jsonObject.getJSONObject("carUsage");
                            JSONArray entityShiftListJsonArray = jsonObject.getJSONArray("entityShiftList");

                            Date startDateConfirm;
                            Date finishDateConfirm;
                            String hejriStartDate;
                            String hejriFinishDate;
                            String name;
                            String codeLast = "";

                            if (!carUsageJsonObject.isNull("usageType")) {
                                int type = carUsageJsonObject.getInt("usageType");
                                switch (type) {
                                    case 0:
                                        typeTV.setText(R.string.enumType_BusUsageType_Line);
                                        break;
                                    case 1:
                                        typeTV.setText(R.string.enumType_BusUsageType_DoorToDoor);
                                        break;
                                    case 2:
                                        typeTV.setText(R.string.enumType_BusUsageType_Services);
                                        break;
                                    case 3:
                                        typeTV.setText(R.string.enumType_BusUsageType_SOS);
                                        break;
                                    case 4:
                                        typeTV.setText(R.string.enumType_BusUsageType_BusFleetReserve);
                                        break;
                                }

                            } else {
                                typeTV.setText("---");
                            }

                            if (!carUsageJsonObject.isNull("startDate")) {
                                String startDate = carUsageJsonObject.getString("startDate");
                                startDateConfirm = simpleDateFormat.parse(startDate);
                                hejriStartDate = HejriUtil.chrisToHejri(startDateConfirm);
                                hejriStartDateTV.setText(hejriStartDate);
                            } else {
                                hejriStartDateTV.setText("---");
                            }

                            if (!carUsageJsonObject.isNull("finishDate")) {
                                String finishDate = carUsageJsonObject.getString("finishDate");
                                finishDateConfirm = simpleDateFormat.parse(finishDate);
                                hejriFinishDate = HejriUtil.chrisToHejri(finishDateConfirm);
                                endDateTV.setText(hejriFinishDate);
                            } else {
                                endDateTV.setText("---");
                            }

                            if (!carUsageJsonObject.isNull("line")) {
                                JSONObject lineJsonObject = carUsageJsonObject.getJSONObject("line");
                                name = lineJsonObject.getString("nameFv");
                                lineNameTV.setText(name);

                                if (!lineJsonObject.isNull("lineCompanyController")) {
                                    JSONObject lineCompanyControllerJsonObject = lineJsonObject.getJSONObject("lineCompanyController");
                                    if (!lineCompanyControllerJsonObject.isNull("company")) {
                                        JSONObject companyJsonObject = lineCompanyControllerJsonObject.getJSONObject("company");
                                        name = companyJsonObject.getString("name");
                                        companyControllerTV.setText(name);
                                    }
                                } else {
                                    companyControllerTV.setText("---");
                                }

                                if (!lineJsonObject.isNull("codeLast")) {
                                    codeLast = lineJsonObject.getString("codeLast");
                                }
                            } else {
                                lineNameTV.setText("---");
                            }

                            if (!carUsageJsonObject.isNull("lineCompanyProfit")) {
                                JSONObject lineCompanyProfitJsonObject = carUsageJsonObject.getJSONObject("lineCompanyProfit");
                                if (!lineCompanyProfitJsonObject.isNull("company")) {
                                    JSONObject companyJsonObject = lineCompanyProfitJsonObject.getJSONObject("company");
                                    if (!companyJsonObject.isNull("name")) {
                                        name = companyJsonObject.getString("name");
                                        try {
                                            companyProfitTV.setText(name + " ( " + getActivity().getResources().getString(R.string.label_codeLast) + " " + codeLast + " )");
                                        } catch (Exception e) {
                                            e.getMessage();
                                        }
                                    }
                                }
                            } else {
                                companyProfitTV.setText("---");
                            }

                            if (!carUsageJsonObject.isNull("line")) {
                                JSONObject lineJsonObject = carUsageJsonObject.getJSONObject("line");
                                if (!lineJsonObject.isNull("linePrice")) {
                                    JSONObject linePriceJsonObject = lineJsonObject.getJSONObject("linePrice");
                                    String rialStr = getActivity().getResources().getString(R.string.rial_label);

                                    String getpriceCoFinal = linePriceJsonObject.getString("priceCoFinal");
                                    priceCoFindTV.setText(getpriceCoFinal + " " + rialStr);

                                    if (!linePriceJsonObject.isNull("eCardPrice")) {
                                        String geteCardPrice_TV = linePriceJsonObject.getString("eCardPrice");
                                        eCardPriceTV.setText(geteCardPrice_TV + " " + rialStr);
                                    } else {
                                        eCardPriceTV.setText("---");
                                    }

                                }
                            } else {
                                priceCoFindTV.setText("---");
                            }


                            if (entityShiftListJsonArray != null) {
                                List<JSONObject> jsonObjectList = new ArrayList<>();
                                for (int i = 0; i < entityShiftListJsonArray.length(); i++) {
                                    JSONObject entityShiftListJsonObject = entityShiftListJsonArray.getJSONObject(i);
                                    int type = entityShiftListJsonArray.getJSONObject(i).getInt("entityBaseEn");
                                    if (type == 2) {
                                        jsonObjectList.add(entityShiftListJsonObject);
                                    }
                                }

                                EntityShiftListAdapter entityShiftListAdapter = new EntityShiftListAdapter(jsonObjectList);
                                shiftList.setAdapter(entityShiftListAdapter);
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
                    jsonObject.put("carId", carId);

                    MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, getActivity());
                    try {
                        result = postJsonService.sendData("getCarUsage", jsonObject, true);
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
        String Tag = "jsonResultCarUsageFragment=";
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
