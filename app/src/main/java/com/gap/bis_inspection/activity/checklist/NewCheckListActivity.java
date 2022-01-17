package com.gap.bis_inspection.activity.checklist;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.CommonUtil;
import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.common.HejriUtil;
import com.gap.bis_inspection.db.enumtype.SurveyFormStatusEn;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.objectmodel.DeviceSetting;
import com.gap.bis_inspection.db.objectmodel.Form;
import com.gap.bis_inspection.db.objectmodel.FormAnswer;
import com.gap.bis_inspection.exception.WebServiceException;
import com.gap.bis_inspection.service.CoreService;
import com.gap.bis_inspection.util.AppLocationService;
import com.gap.bis_inspection.util.DateUtils;
import com.gap.bis_inspection.util.InputFilterMinMax;
import com.gap.bis_inspection.webservice.MyPostJsonService;
import com.gap.bis_inspection.widget.GPSTracker;
import com.gap.bis_inspection.widget.VpnCheck;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewCheckListActivity extends AppCompatActivity {
    Button action;
    EditText plateTextP1, plateTextP2, lineCode;
    TextView dateAndTime, title, txt_title;
    RelativeLayout back_Icon;
    long formId;
    boolean recognize;
    CoreService coreService;
    DatabaseManager databaseManager;
    FormAnswer formAnswer;
    AppLocationService appLocationService;
    Double latitude;
    Double longitude;
    private static final int MY_PERMISSIONS_REQUEST = 100;
    private GPSTracker gps;
    Context context = this;
    ProgressDialog progressBar;
    private String strCode;
    private ASync myTask = null;
    private ASyncGetCarInfo myTaskGetCar = null;
    private String searchKey;
    private Form form;
    private VpnCheck vpnCheck = new VpnCheck();
    private String carInfoType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_new_check_list);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            formId = bundle.getLong("formId");
            recognize = bundle.getBoolean("recognize");
        }

        databaseManager = new DatabaseManager(this);
        coreService = new CoreService(databaseManager);
        form = coreService.getCheckListFormById(formId);

        action = (Button) findViewById(R.id.action_Button);
        plateTextP1 = (EditText) findViewById(R.id.plateTextP1_ET);
        plateTextP2 = (EditText) findViewById(R.id.plateTextP2_ET);
        lineCode = (EditText) findViewById(R.id.lineCode_ET);
        dateAndTime = (TextView) findViewById(R.id.dateAndTime_ET);
        title = (TextView) findViewById(R.id.title_VT);
        txt_title = (TextView) findViewById(R.id.txt_title);
        back_Icon = (RelativeLayout) findViewById(R.id.back_Icon);

        if (form != null) {
            title.setText(form.getName());
        }

        plateTextP1.setFilters(new InputFilter[]{new InputFilterMinMax(0, 99999)});
        plateTextP2.setFilters(new InputFilter[]{new InputFilterMinMax(0, 99)});
        lineCode.setFilters(new InputFilter[]{new InputFilterMinMax(0, 999)});
        dateAndTime.setText(HejriUtil.getCurDateTime());

        appLocationService = new AppLocationService(NewCheckListActivity.this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getGpsLocation();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST);
            }
        }

        if (AppController.getInstance().getSharedPreferences().getBoolean(Constants.ON_PROPERTY_CODE, false)) {
            carInfoType = String.valueOf(1);
            //searchET.setHint(getResources().getString(R.string.car_plateText_Search_label));
            plateTextP2.setVisibility(View.GONE);
            txt_title.setText("کد خودرو را وارد کنید : ");
        } else {
            carInfoType = String.valueOf(0);
            //searchET.setHint(getResources().getString(R.string.car_propertyCode_Search_label));
            txt_title.setText("پلاک خودرو را وارد کنید : ");
        }

        //========= click action
        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String strPlateTextP1 = plateTextP1.getText().toString();
                String strPlateTextP2 = plateTextP2.getText().toString();
                strCode = lineCode.getText().toString();

                if (carInfoType.equals("0")) {

                    if (TextUtils.isEmpty(strPlateTextP1) || TextUtils.getTrimmedLength(strPlateTextP1) < 5) {
                        plateTextP1.setError(getResources().getString(R.string.label_reportStrTv_NotNull));

                    } else if (TextUtils.isEmpty(strPlateTextP2) || TextUtils.getTrimmedLength(strPlateTextP2) < 2) {
                        plateTextP2.setError(getResources().getString(R.string.label_reportStrTv_NotNull));

                    } else if (TextUtils.isEmpty(strCode) || TextUtils.getTrimmedLength(strCode) < 3) {
                        lineCode.setError(getResources().getString(R.string.label_reportStrTv_NotNull));
                    } else {
                        searchKey = plateTextP1.getText().toString() + plateTextP2.getText().toString();
                        if (vpnCheck.VpnConnectionCheck(getApplicationContext())) {
                            Toast.makeText(context, "لطفا vpn خود را خاموش نمایید.", Toast.LENGTH_SHORT).show();
                        } else {
                            myTaskGetCar = new ASyncGetCarInfo();
                            myTaskGetCar.execute();
                        }

                    }

                } else if (carInfoType.equals("1")) {

                    if (TextUtils.isEmpty(strPlateTextP1) || TextUtils.getTrimmedLength(strPlateTextP1) < 3) {
                        plateTextP1.setError(getResources().getString(R.string.label_reportStrTv_NotNull));

                    } else if (TextUtils.isEmpty(strCode) || TextUtils.getTrimmedLength(strCode) < 3) {
                        lineCode.setError(getResources().getString(R.string.label_reportStrTv_NotNull));
                    } else {
                        searchKey = plateTextP1.getText().toString();
                        if (vpnCheck.VpnConnectionCheck(getApplicationContext())) {
                            Toast.makeText(context, "لطفا vpn خود را خاموش نمایید.", Toast.LENGTH_SHORT).show();
                        } else {
                            myTaskGetCar = new ASyncGetCarInfo();
                            myTaskGetCar.execute();
                        }

                    }

                }
            }
        });

        back_Icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        plateTextP1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (plateTextP1.getText().length() == 5) {
                    plateTextP2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        plateTextP2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (plateTextP2.getText().length() == 2) {
                    lineCode.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    //========= get Gps Location
    private void getGpsLocation() {
        gps = new GPSTracker(NewCheckListActivity.this);
        // check if GPS enabled
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            // \n is for new line
        } else {
            gps.showSettingsAlert();
        }
    }


    //========= auth line code
    private class ASync extends AsyncTask<Void, Void, Void> {
        private String result;
        private String errorMsg;

        @SuppressLint("StringFormatInvalid")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressBar.setVisibility(View.VISIBLE);
            progressBar = ProgressDialog.show(context, null, context.getResources().getString(R.string.label_progress_dialog, true), true);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //progressBar.setVisibility(View.INVISIBLE);
            progressBar.dismiss();
            if (result != null) {
                try {
                    JSONObject resultJson = new JSONObject(result);
                    if (errorMsg == null && !resultJson.isNull(Constants.SUCCESS_KEY)) {
                        if (!resultJson.isNull(Constants.RESULT_KEY)) {
                            JSONObject jsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);

                            getGpsLocation();
                            plateTextP1 = CommonUtil.farsiNumberReplacement(plateTextP1);
                            plateTextP2 = CommonUtil.farsiNumberReplacement(plateTextP2);
                            lineCode = CommonUtil.farsiNumberReplacement(lineCode);
                            formAnswer = new FormAnswer();
                            formAnswer.setCarId(Long.valueOf(searchKey));
                            formAnswer.setLineId(Long.valueOf(lineCode.getText().toString()));
                            formAnswer.setName(title.getText().toString());
                            formAnswer.setFormId(formId);
                            formAnswer.setXLatitude(latitude != null ? latitude.toString() : null);
                            formAnswer.setYLongitude(longitude != null ? longitude.toString() : null);
                            formAnswer.setStatusDate(new Date());
                            formAnswer.setMinScore(form.getMinScore());
                            formAnswer.setMaxScore(form.getMaxScore());
                            formAnswer.setStatusEn(SurveyFormStatusEn.Incomplete.ordinal());
                            coreService.insertFormAnswer(formAnswer);

                            Intent intent = new Intent(getApplicationContext(), CheckListFormQuestionActivity.class);
                            intent.putExtra("searchKey", searchKey);
                            intent.putExtra("lineCode", lineCode.getText().toString());
                            intent.putExtra("dateAndTime", dateAndTime.getText().toString());
                            if (formAnswer != null) {
                                intent.putExtra("formAnswerId", formAnswer.getId());
                            }
                            intent.putExtra("formId", formId);
                            intent.putExtra("recognize", recognize);
                            startActivity(intent);
                            finish();


                        }
                    } else {
                        if (errorMsg == null) {
                            errorMsg = resultJson.getString(Constants.ERROR_KEY);
                        }
                        Toast toast = Toast.makeText(context, errorMsg, Toast.LENGTH_LONG);
                        CommonUtil.showToast(toast,NewCheckListActivity.this);
                        toast.show();
                    }
                } catch (JSONException e) {
                    Log.d("RegistrationFragment", e.getMessage());
                    Toast toast = Toast.makeText(context, getResources().getString(R.string.Some_error_accor_contact_admin), Toast.LENGTH_LONG);
                    CommonUtil.showToast(toast,NewCheckListActivity.this);
                    toast.show();
                }
            } else {
                Toast toast = Toast.makeText(context, (errorMsg != null) ? errorMsg : getResources().getString(R.string.Some_error_accor_contact_admin), Toast.LENGTH_LONG);
                CommonUtil.showToast(toast,NewCheckListActivity.this);
                toast.show();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (isDeviceDateTimeValid()) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    AppController application = (AppController) getApplication();
                    jsonObject.put("username", application.getCurrentUser().getUsername());
                    jsonObject.put("tokenPass", application.getCurrentUser().getBisPassword());
                    jsonObject.put("lineCode", strCode);
                    MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, context);
                    try {
                        result = postJsonService.sendData("getLineNameByCode", jsonObject, true);
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
                MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, context);
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


    //========= auth car code
    private class ASyncGetCarInfo extends AsyncTask<Void, Void, Void> {
        private String result;
        private String errorMsg;

        @SuppressLint("StringFormatInvalid")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressBar.setVisibility(View.VISIBLE);
            progressBar = ProgressDialog.show(context, null, context.getResources().getString(R.string.label_progress_dialog, true), true);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //progressBar.setVisibility(View.INVISIBLE);
            progressBar.dismiss();
            if (result != null) {
                try {
                    JSONObject resultJson = new JSONObject(result);
                    if (errorMsg == null && !resultJson.isNull(Constants.SUCCESS_KEY)) {
                        if (!resultJson.isNull(Constants.RESULT_KEY)) {
                            JSONObject jsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);

                            myTask = new ASync();
                            myTask.execute();
                        }
                    } else {
                        if (errorMsg == null) {
                            errorMsg = resultJson.getString(Constants.ERROR_KEY);
                        }
                        Toast toast = Toast.makeText(context, errorMsg, Toast.LENGTH_LONG);
                        CommonUtil.showToast(toast,NewCheckListActivity.this);
                        toast.show();
                    }
                } catch (JSONException e) {
                    Log.d("RegistrationFragment", e.getMessage());
                    Toast toast = Toast.makeText(context, getResources().getString(R.string.Some_error_accor_contact_admin), Toast.LENGTH_LONG);
                    CommonUtil.showToast(toast,NewCheckListActivity.this);
                    toast.show();
                }
            } else {
                Toast toast = Toast.makeText(context, (errorMsg != null) ? errorMsg : getResources().getString(R.string.Some_error_accor_contact_admin), Toast.LENGTH_LONG);
                CommonUtil.showToast(toast,NewCheckListActivity.this);
                toast.show();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (isDeviceDateTimeValid()) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    AppController application = (AppController) getApplication();
                    jsonObject.put("username", application.getCurrentUser().getUsername());
                    jsonObject.put("tokenPass", application.getCurrentUser().getBisPassword());

                    if (carInfoType.equals("0")) {
                        jsonObject.put("plateText", searchKey);
                    } else if (carInfoType.equals("1")) {
                        jsonObject.put("propertyCode", searchKey);
                    }

                    // jsonObject.put("plateText", strPlateText);
                    MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, context);
                    try {
                        result = postJsonService.sendData("getCarNameByPlate", jsonObject, true);
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
                MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, context);
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
}
