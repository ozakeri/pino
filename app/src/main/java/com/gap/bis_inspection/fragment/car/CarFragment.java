package com.gap.bis_inspection.fragment.car;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
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
import com.gap.bis_inspection.common.CommonUtil;
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
 * Created by Mohamad Cheraghi on 08/20/2016.
 */
public class CarFragment extends Fragment {
    ImageView backIcon;
    TextView plateTextTV, nameTV, productionYearTV, colorTV, carOptionInsuranceFPTV, carOptionTechnicalCheckFPTV,txt_null,
            advertisementDetailFPTV, companyTV, nameFv1TV, movedKm, nameFvBefore, intParam2, parameter1, remainedFullValueFV, engineFuelTypeTV, usageTypeEnTV;
    Dialog dialog;
    ProgressBar progress;
    Long carId = null;
    String strNameFvBefore, strIntParam2, strMovedKm, strParameter1, strRemainedFullValueFV, displayName = null;
    RelativeLayout addIcon;
    String addIcon1 = "car";
    private IDatabaseManager databaseManager;
    private CoreService coreService;
    ASync myTask = null;
    ProgressDialog progressBar;
    Button getFuelCar_Button;
    LinearLayout linearLayout1, linearLayout2, linearLayout3, linearLayout4, linearLayout5,advertisement_layout;
    private String carInfo;
    private String strName, strType;
    private int type;

    public CarFragment() {
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_car, container, false);
        init(view);

        getFuelCar_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.get_car_fuel_by_id_layout);

                linearLayout1 = (LinearLayout) dialog.findViewById(R.id.linearLayout1);
                linearLayout2 = (LinearLayout) dialog.findViewById(R.id.linearLayout2);
                linearLayout3 = (LinearLayout) dialog.findViewById(R.id.linearLayout3);
                linearLayout4 = (LinearLayout) dialog.findViewById(R.id.linearLayout4);
                linearLayout5 = (LinearLayout) dialog.findViewById(R.id.linearLayout5);
                advertisement_layout = (LinearLayout) dialog.findViewById(R.id.advertisement_layout);
                nameFvBefore = (TextView) dialog.findViewById(R.id.nameFvBefore_TV);
                txt_null = (TextView) dialog.findViewById(R.id.txt_null);
                intParam2 = (TextView) dialog.findViewById(R.id.intParam2_TV);
                movedKm = (TextView) dialog.findViewById(R.id.movedKm_TV);
                parameter1 = (TextView) dialog.findViewById(R.id.parameter1_TV);
                remainedFullValueFV = (TextView) dialog.findViewById(R.id.remainedFullValueFV_TV);

                RelativeLayout close_Button = (RelativeLayout) dialog.findViewById(R.id.close_Button);

                myTask = new ASync();
                myTask.execute();

                close_Button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.motion, R.anim.motion2);
            }
        });

        if (getArguments() != null) {
            carInfo = getArguments().getString("car");
            logLargeString(carInfo);
        }

        try {
            JSONObject carInfoJsonObject = new JSONObject(carInfo);

            if (!carInfoJsonObject.isNull("plateText")) {

                String plateText = carInfoJsonObject.getString("plateText");
                String[] split = plateText.split("-");
                String firstSubString = split[0];
                String secondSubString = split[1];
                String plate = firstSubString + " - " + secondSubString;

                plateTextTV.setText(plate);
                displayName = plate;
            } else {
                plateTextTV.setText("---");
            }

            if (!carInfoJsonObject.isNull("nameFv")) {
                nameTV.setText(carInfoJsonObject.getString("nameFv"));
            } else {
                nameTV.setText("---");
            }

            if (!carInfoJsonObject.isNull("engineFuelType")) {
                int type = carInfoJsonObject.getInt("engineFuelType");
                switch (type) {
                    case 0:
                        engineFuelTypeTV.setText(getResources().getString(R.string.enumType_FuelType_GasCNG));
                        break;
                    case 1:
                        engineFuelTypeTV.setText(getResources().getString(R.string.enumType_FuelType_GasOil));
                        break;
                    case 2:
                        engineFuelTypeTV.setText(getResources().getString(R.string.enumType_FuelType_Electrical));
                        break;
                    case 3:
                        engineFuelTypeTV.setText(getResources().getString(R.string.enumType_FuelType_Gasoline));
                        break;
                    case 4:
                        engineFuelTypeTV.setText(getResources().getString(R.string.enumType_FuelType_GasLPG));
                        break;
                    case 5:
                        engineFuelTypeTV.setText(getResources().getString(R.string.enumType_FuelType_Hybrid));
                        break;
                    case 6:
                        engineFuelTypeTV.setText(getResources().getString(R.string.enumType_FuelType_DualFuel));
                        break;
                }

            } else {
                engineFuelTypeTV.setText("---");
            }

            /*if (!carInfoJsonObject.isNull("vehicle")) {
                JSONObject vehicleJsonObject = carInfoJsonObject.getJSONObject("nameFV");
                String nameFv = vehicleJsonObject.getString("vehicleType_text") + " - " + vehicleJsonObject.getString("name");
                nameTV.setText(nameFv);

                if (displayName == null) {
                    displayName = vehicleJsonObject.getString("vehicleType_text");
                    displayName = vehicleJsonObject.getString("name");
                } else {
                    displayName += " " + vehicleJsonObject.getString("vehicleType_text") + " " + vehicleJsonObject.getString("name");
                }

            }*/

            if (!carInfoJsonObject.isNull("id")) {
                carId = carInfoJsonObject.getLong("id");
            }

            if (!carInfoJsonObject.isNull("productionYear")) {
                productionYearTV.setText(carInfoJsonObject.getString("productionYear"));
            } else {
                productionYearTV.setText("---");
            }

            String sharePart = "";
            String name = "";
            if (!carInfoJsonObject.isNull("carProfitLegal")) {
                JSONObject carProfitLegalJsonObject = carInfoJsonObject.getJSONObject("carProfitLegal");
                sharePart = carProfitLegalJsonObject.getString("sharePart");
            }
            if (!carInfoJsonObject.isNull("company")) {
                JSONObject companyJsonObject = carInfoJsonObject.getJSONObject("company");
                name = companyJsonObject.getString("name");

                type = companyJsonObject.getInt("companyType");
                if (type == 3) {
                    strType = "خصوصی";
                } else {
                    strType = "دولتی";
                }
                companyTV.setText(strType + " - " + name + " ( " + sharePart + " )" + getResources().getString(R.string.label_sharePart));
            }

            if (!carInfoJsonObject.isNull("color")) {
                JSONObject colorJsonObject = carInfoJsonObject.getJSONObject("color");
                colorTV.setText(colorJsonObject.getString("name"));
            } else {
                colorTV.setText("---");
            }


            if (!carInfoJsonObject.isNull("carProfitOwner")) {
                JSONObject carProfitOwnerJsonObject = carInfoJsonObject.getJSONObject("carProfitOwner");
                if (!carProfitOwnerJsonObject.isNull("company")) {
                    JSONObject companyJsonObject = carProfitOwnerJsonObject.getJSONObject("company");
                    strName = companyJsonObject.getString("name");
                    nameFv1TV.setText(strName);
                }
            } else {
                nameFv1TV.setText("---");
            }

            if (!carInfoJsonObject.isNull("carOptionInsuranceFP")) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                JSONObject carOptionInsuranceFPJsonObject = carInfoJsonObject.getJSONObject("carOptionInsuranceFP");
                // String nameFv = carOptionInsuranceFPJsonObject.getString("paramStr1");


                if (!carOptionInsuranceFPJsonObject.isNull("expireDate")) {
                    String strExpireDate = carOptionInsuranceFPJsonObject.getString("expireDate");
                    Date expireDate = simpleDateFormat.parse(strExpireDate);
                    Date currentDate = new Date(System.currentTimeMillis());
                    if (currentDate.getTime() < expireDate.getTime()){
                        carOptionInsuranceFPTV.setText(getActivity().getResources().getString(R.string.label_durationCredit) + " " + CalendarUtil.datesDiff(getActivity(), currentDate, expireDate, "yMd"));
                    }else {
                        carOptionInsuranceFPTV.setText("0");
                    }


                }
            } else {
                carOptionInsuranceFPTV.setText("---");
            }

            if (!carInfoJsonObject.isNull("carOptionTechnicalCheckFP")) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                JSONObject carOptionTechnicalCheckFPPJsonObject = carInfoJsonObject.getJSONObject("carOptionTechnicalCheckFP");

                if (!carOptionTechnicalCheckFPPJsonObject.isNull("expireDate")) {
                    String strExpireDate = carOptionTechnicalCheckFPPJsonObject.getString("expireDate");
                    Date expireDate = simpleDateFormat.parse(strExpireDate);
                    Date currentDate = new Date(System.currentTimeMillis());
                    if (currentDate.getTime() < expireDate.getTime()){
                        carOptionTechnicalCheckFPTV.setText(getActivity().getResources().getString(R.string.label_durationCredit) + " " + CalendarUtil.datesDiff(getActivity(), currentDate, expireDate, "yMd"));
                    }else {
                        carOptionTechnicalCheckFPTV.setText("0");
                    }
                }
            } else {
                carOptionTechnicalCheckFPTV.setText("---");
            }

            if (!carInfoJsonObject.isNull("advertisementDetailFP")) {
                advertisement_layout.setVisibility(View.VISIBLE);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                String advertName = "";
                String advertParamValue = "";
                JSONObject advertisementDetailFPJsonObject = carInfoJsonObject.getJSONObject("advertisementDetailFP");

                if (!advertisementDetailFPJsonObject.isNull("advert")) {
                    JSONObject advertJsonObject = advertisementDetailFPJsonObject.getJSONObject("advert");
                    advertName = advertJsonObject.getString("name");

                    if (!advertJsonObject.isNull("companyBrandSP")) {
                        JSONObject companyBrandSPJsonObject = advertJsonObject.getJSONObject("companyBrandSP");
                        advertParamValue = companyBrandSPJsonObject.getString("paramValue");
                    }
                }

                String strEndDate = advertisementDetailFPJsonObject.getString("endDate");
                Date endDate = simpleDateFormat.parse(strEndDate);
                Date currentDate = new Date(System.currentTimeMillis());
                advertisementDetailFPTV.setText(advertName + " " + advertParamValue + " - " + getActivity().getResources().getString(R.string.label_durationCredit) + " " + CalendarUtil.datesDiff(getActivity(), currentDate, endDate, "yMd"));
            } else {
                advertisementDetailFPTV.setText("---");
            }

            String nameFv = "";
            if (!carInfoJsonObject.isNull("usageTypeEn")) {
                int usageTypeEn = carInfoJsonObject.getInt("usageTypeEn");
                JSONObject jsonObject = carInfoJsonObject.getJSONObject("carUsage");
                if (!jsonObject.isNull("line")) {
                    JSONObject lineJsonObject = jsonObject.getJSONObject("line");
                    // if (!lineJsonObject.isNull("line")) {
                    nameFv = lineJsonObject.getString("nameFv");
                    System.out.println("nameFv====" + nameFv);
                    //}
                }

                switch (usageTypeEn) {
                    case 0:
                        usageTypeEnTV.setText(getResources().getString(R.string.enumType_BusUsageType_Line) + " - " + nameFv);
                        break;
                    case 1:
                        usageTypeEnTV.setText(R.string.enumType_BusUsageType_DoorToDoor);
                        break;
                    case 2:
                        usageTypeEnTV.setText(R.string.enumType_BusUsageType_Services);
                        break;
                    case 3:
                        usageTypeEnTV.setText(R.string.enumType_BusUsageType_SOS);
                        break;
                    case 4:
                        usageTypeEnTV.setText(R.string.enumType_BusUsageType_BusFleetReserve);
                        break;
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
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

        return view;
    }

    private void init(View view) {
        backIcon = (ImageView) view.findViewById(R.id.backIcon);
        addIcon = (RelativeLayout) view.findViewById(R.id.addIcon);
        plateTextTV = (TextView) view.findViewById(R.id.plateText_TV);
        engineFuelTypeTV = (TextView) view.findViewById(R.id.engineFuelType_TV);
        nameTV = (TextView) view.findViewById(R.id.nameFv_TV);
        productionYearTV = (TextView) view.findViewById(R.id.productionYear_TV);
        colorTV = (TextView) view.findViewById(R.id.color_TV);
        carOptionInsuranceFPTV = (TextView) view.findViewById(R.id.carOptionInsuranceFP_TV);
        carOptionTechnicalCheckFPTV = (TextView) view.findViewById(R.id.carOptionTechnicalCheckFP_TV);
        advertisementDetailFPTV = (TextView) view.findViewById(R.id.advertisementDetailFP_TV);
        companyTV = (TextView) view.findViewById(R.id.company_TV);
        nameFv1TV = (TextView) view.findViewById(R.id.nameFv1_TV);
        progress = (ProgressBar) view.findViewById(R.id.progress);
        databaseManager = new DatabaseManager(getActivity());
        coreService = new CoreService(databaseManager);
        linearLayout1 = (LinearLayout) view.findViewById(R.id.linearLayout1);
        linearLayout2 = (LinearLayout) view.findViewById(R.id.linearLayout2);
        linearLayout3 = (LinearLayout) view.findViewById(R.id.linearLayout3);
        linearLayout4 = (LinearLayout) view.findViewById(R.id.linearLayout4);
        linearLayout5 = (LinearLayout) view.findViewById(R.id.linearLayout5);
        getFuelCar_Button = (Button) view.findViewById(R.id.getFuelCar_Button);
        usageTypeEnTV = view.findViewById(R.id.usageTypeEn_TV);
    }

    public class ASync extends AsyncTask<Void, Void, Void> {
        private String result;
        private String errorMsg;


        @SuppressLint("StringFormatInvalid")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = ProgressDialog.show(getActivity(), null, getActivity().getResources().getString(R.string.label_progress_dialog, true), true);
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.dismiss();
            dialog.show();
            if (result != null) {

                try {
                    JSONObject resultJson = new JSONObject(result);
                    if (errorMsg == null && !resultJson.isNull(Constants.SUCCESS_KEY)) {
                        if (!resultJson.isNull(Constants.RESULT_KEY)) {
                            JSONObject jsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                            if (!jsonObject.isNull("dailyEvent")) {
                                txt_null.setVisibility(View.GONE);
                                JSONObject dailyEventJsonObject = jsonObject.getJSONObject("dailyEvent");
                                if (!dailyEventJsonObject.isNull("movedKm")) {
                                    linearLayout3.setVisibility(View.VISIBLE);
                                    strMovedKm = dailyEventJsonObject.getString("movedKm");
                                    movedKm.setText(strMovedKm);
                                }

                              /*  if (!dailyEventJsonObject.isNull("nameFvBefore")) {
                                    linearLayout1.setVisibility(View.VISIBLE);
                                    strNameFvBefore = dailyEventJsonObject.getString("nameFvBefore");
                                    nameFvBefore.setText(strNameFvBefore);
                                }*/

                                if (!dailyEventJsonObject.isNull("dailyEventBefore")) {
                                    JSONObject dailyEventBeforeJsonObject = dailyEventJsonObject.getJSONObject("dailyEventBefore");
                                    if (!dailyEventBeforeJsonObject.isNull("nameFv")) {
                                        linearLayout1.setVisibility(View.VISIBLE);
                                        strNameFvBefore = dailyEventBeforeJsonObject.getString("nameFv");
                                        nameFvBefore.setText(getResources().getString(R.string.label_get_car_fuel_nameFvBefore) + " " + strNameFvBefore);
                                    }
                                }

                                if (!dailyEventJsonObject.isNull("parameter1")) {
                                    linearLayout4.setVisibility(View.VISIBLE);
                                    strParameter1 = dailyEventJsonObject.getString("parameter1");
                                    parameter1.setText(strParameter1);
                                }

                                if (!dailyEventJsonObject.isNull("remainedFullValueFV")) {
                                    linearLayout5.setVisibility(View.VISIBLE);
                                    strRemainedFullValueFV = dailyEventJsonObject.getString("remainedFullValueFV");
                                    remainedFullValueFV.setText(strRemainedFullValueFV);
                                }

                                if (!dailyEventJsonObject.isNull("dailyEventBefore")) {
                                    JSONObject dailyEventBeforeJsonObject = dailyEventJsonObject.getJSONObject("dailyEventBefore");
                                    if (!dailyEventBeforeJsonObject.isNull("intParam2")) {
                                        linearLayout2.setVisibility(View.VISIBLE);
                                        strIntParam2 = dailyEventBeforeJsonObject.getString("intParam2");
                                        intParam2.setText(strIntParam2);
                                    }
                                }
                            }else {
                                txt_null.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        if (errorMsg == null) {
                            errorMsg = resultJson.getString(Constants.ERROR_KEY);
                        }
                        Toast toast = Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG);
                        CommonUtil.showToast(toast,getActivity());
                        toast.show();
                    }
                } catch (JSONException e) {
                    Log.d("RegistrationFragment", e.getMessage());
                    Toast toast = Toast.makeText(getActivity(), getResources().getString(R.string.Some_error_accor_contact_admin), Toast.LENGTH_LONG);
                    CommonUtil.showToast(toast,getActivity());
                    toast.show();
                }
            } else {
                Toast toast = Toast.makeText(getActivity(), (errorMsg != null) ? errorMsg : getResources().getString(R.string.Some_error_accor_contact_admin), Toast.LENGTH_LONG);
                CommonUtil.showToast(toast,getActivity());
                toast.show();
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
                        result = postJsonService.sendData("getCarFuelRemained", jsonObject, true);
                    } catch (SocketTimeoutException | SocketException e) {
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
            } catch (SocketTimeoutException | SocketException e) {
                errorMsg = getResources().getString(R.string.Some_error_accor_contact_admin);
            } catch (JSONException | ParseException | WebServiceException e) {
                errorMsg = getResources().getString(R.string.Some_error_accor_contact_admin);
                Log.d("SyncActivity", e.getMessage());
            }
            return false;
        }
    }

    public void logLargeString(String str) {
        String Tag = "CarFragment====";
        if (str.length() > 3000) {
            Log.i(Tag, str.substring(0, 3000));
            logLargeString(str.substring(3000));
        } else {
            Log.i(Tag, str); // continuation
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            myTask.cancel(true);
        }catch (Exception e){
            e.getMessage();
        }
    }
}
