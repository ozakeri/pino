package com.gap.bis_inspection.activity.car;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.activity.HomeActivity;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.CommonUtil;
import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.manager.IDatabaseManager;
import com.gap.bis_inspection.db.objectmodel.DeviceSetting;
import com.gap.bis_inspection.exception.WebServiceException;
import com.gap.bis_inspection.service.CoreService;
import com.gap.bis_inspection.util.DateUtils;
import com.gap.bis_inspection.webservice.MyPostJsonService;
import com.gap.bis_inspection.widget.VpnCheck;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CarActivity extends AppCompatActivity {
    Context context = this;
    TextView webSiteTV;
    String searchKey;
    private IDatabaseManager databaseManager;
    private CoreService coreService;
    ImageView searchButton;
    EditText searchET;
    RelativeLayout backIcon;
    Button getFuelCar_Button;
    ASync myTask = null;
    private String carInfoType = "";
    VpnCheck vpnCheck = new VpnCheck();
    private RadioGroup getCarInfoParamType_OP;
    private RadioButton plateTextType_OP,codeType_OP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);

        init();


        //carInfoType = String.valueOf(0);
        if (AppController.getInstance().getSharedPreferences().getBoolean(Constants.ON_PROPERTY_CODE, false)) {
            searchET.setHint(getResources().getString(R.string.car_propertyCode_Search_label));
            codeType_OP.setChecked(true);
            plateTextType_OP.setChecked(false);
            carInfoType = "propertyCode";
        } else {
            searchET.setHint(getResources().getString(R.string.car_plateText_Search_label));
            plateTextType_OP.setChecked(true);
            codeType_OP.setChecked(false);
            carInfoType = "plateText";
        }

        searchET.setHint(getResources().getString(R.string.car_propertyCode_Search_label));
        carInfoType = "propertyCode";
        getCarInfoParamType_OP.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.codeType_OP) {
                    searchET.setHint(getResources().getString(R.string.car_propertyCode_Search_label));
                    carInfoType = "propertyCode";
                } else if (checkedId == R.id.plateTextType_OP) {
                    searchET.setHint(getResources().getString(R.string.car_plateText_Search_label));
                    carInfoType = "plateText";
                }

            }
        });


        /**
         * search button click*/
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* if (searchET.getText().length() == 0 || searchET.getText().length() < 5 || searchET.getText().length() == 6 || searchET.getText().length() > 7) {

                    if (AppController.getInstance().getSharedPreferences().getBoolean(Constants.ON_PROPERTY_CODE, false)) {
                        Toast toast = Toast.makeText(getApplicationContext(), R.string.label_inputGetDriverInfo_propertyCode_NotNull, Toast.LENGTH_LONG);
                        CommonUtil.showToast(toast);
                        toast.show();
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(), R.string.label_inputGetDriverInfo_plateText_NotNull, Toast.LENGTH_LONG);
                        CommonUtil.showToast(toast);
                        toast.show();
                    }
                } */

                searchET = CommonUtil.farsiNumberReplacement(searchET);
                searchKey = searchET.getText().toString();

                if (vpnCheck.VpnConnectionCheck(getApplicationContext())) {
                    Toast.makeText(context, "لطفا vpn خود را خاموش نمایید.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        myTask = new ASync();
                        myTask.execute();
                    } catch (Exception e) {

                    }
                }
            }
        });


        /**
         * back button click*/
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myTask == null) {
                    finish();
                } else {
                    myTask.cancel(true);
                    finish();
                }
            }
        });


        searchET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchET = CommonUtil.farsiNumberReplacement(searchET);
                    searchKey = searchET.getText().toString();

                    if (vpnCheck.VpnConnectionCheck(getApplicationContext())) {
                        Toast.makeText(context, "لطفا vpn خود را خاموش نمایید.", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            myTask = new ASync();
                            myTask.execute();
                        } catch (Exception e) {

                        }
                    }

                    return true;
                }
                return false;
            }
        });

        webSiteTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://www.gapcom.ir"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }


    /**
     * initialize ...
     */
    private void init() {
        searchButton = (ImageView) findViewById(R.id.btn_search);
        databaseManager = new DatabaseManager(this);
        coreService = new CoreService(databaseManager);
        backIcon = (RelativeLayout) findViewById(R.id.backIcon);
        searchET = (EditText) findViewById(R.id.search_ET);
        webSiteTV = (TextView) findViewById(R.id.webSite_TV);
        getFuelCar_Button = (Button) findViewById(R.id.getFuelCar_Button);
        getCarInfoParamType_OP = findViewById(R.id.getCarInfoParamType_OP);
        plateTextType_OP = findViewById(R.id.plateTextType_OP);
        codeType_OP = findViewById(R.id.codeType_OP);
        codeType_OP.setChecked(true);
    }

    public void mScan(View view) {
        try {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.setPackage("com.google.zxing.client.android");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            showDialog(CarActivity.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }

/*    @SuppressLint("MissingSuperCall")
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        try {
            if (requestCode == 0) {
                if (resultCode == RESULT_OK) {
                    //carInfoType = intent.getStringExtra("SCAN_RESULT");
                    if (vpnCheck.VpnConnectionCheck(getApplicationContext())) {
                        Toast.makeText(context, "لطفا vpn خود را خاموش نمایید.", Toast.LENGTH_SHORT).show();
                    } else {
                        myTask = new ASync();
                        myTask.execute();
                    }

                } else if (resultCode == RESULT_CANCELED) {
                    Toast toast = Toast.makeText(this, "Scan was Cancelled!", Toast.LENGTH_LONG);
                    CommonUtil.showToast(toast);
                    toast.show();
                }
            }
        } catch (Exception e) {
        }
    }*/

    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("http://www.dl.farsroid.com/app/Barcode-Scanner-4.7.5(FarsRoid.Com).apk");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException ignored) {
                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }


    ////******search car*******////
    private class ASync extends AsyncTask<Void, Void, Void> {
        private String result;
        private String errorMsg;
        private ProgressDialog progressDialog = null;

        @SuppressLint("StringFormatInvalid")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressBar.setVisibility(View.VISIBLE);
            progressDialog = new ProgressDialog(CarActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.label_progress_dialog));
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(true);
            progressDialog.show();

            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    ASync.this.cancel(true);
                    progressDialog.dismiss();
                }
            });

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if (result != null) {
                logLargeString(result);
                try {
                    JSONObject resultJson = new JSONObject(result);
                    if (errorMsg == null && !resultJson.isNull(Constants.SUCCESS_KEY)) {
                        if (!resultJson.isNull(Constants.RESULT_KEY)) {
                            JSONObject jsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                            if (!jsonObject.isNull("car")) {
                                JSONObject carInfoJsonObject = jsonObject.getJSONObject("car");
                                /*Bundle bundle = new Bundle();
                                bundle.putString("car", carInfoJsonObject.toString());
                                Intent intent = new Intent(context, CarTabActivity.class);
                                intent.putExtra("car", carInfoJsonObject.toString());
                                startActivity(intent);*/
                                SharedPreferences.Editor editor = AppController.getInstance().getSharedPreferences().edit();
                                editor.putString(Constants.JSON_DATA,  carInfoJsonObject.toString());
                                editor.apply();
                                startActivity(new Intent(context, CarTabActivity.class));
                            }
                        }
                    } else {
                        if (errorMsg == null) {
                            errorMsg = resultJson.getString(Constants.ERROR_KEY);
                        }
                        Toast toast = Toast.makeText(context, errorMsg, Toast.LENGTH_LONG);
                        CommonUtil.showToast(toast,CarActivity.this);
                        toast.show();
                    }
                } catch (JSONException e) {
                    Log.d("RegistrationFragment", e.getMessage());
                    Toast toast = Toast.makeText(context, getResources().getString(R.string.Some_error_accor_contact_admin), Toast.LENGTH_LONG);
                    CommonUtil.showToast(toast,CarActivity.this);
                    toast.show();
                }
            } else {
                Toast toast = Toast.makeText(context, (errorMsg != null) ? errorMsg : getResources().getString(R.string.Some_error_accor_contact_admin), Toast.LENGTH_LONG);
                CommonUtil.showToast(toast,CarActivity.this);
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
                    if (carInfoType.equals("plateText")) {
                        jsonObject.put("plateText", searchKey);
                    } else if (carInfoType.equals("propertyCode")) {
                        jsonObject.put("propertyCode", searchKey);
                    }
                    //jsonObject.put("carInfoType", carInfoType);
                    MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, context);
                    try {
                        result = postJsonService.sendData("getCarInfo", jsonObject, true);
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


        ////******getServerDateTime*******////

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
            } catch (JSONException | ParseException | WebServiceException e) {
                errorMsg = getResources().getString(R.string.Some_error_accor_contact_admin);
                Log.d("SyncActivity", e.getMessage());
            }
            return false;
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (myTask == null) {
            finish();
        } else {
            myTask.cancel(true);
            finish();
        }
    }

    private void exitAll() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.motion, R.anim.motion2);
    }


    ////******print log*******////
    @SuppressLint("LongLogTag")
    public void logLargeString(String str) {
        String Tag = "jsonResultCarActivity=";
        if (str.length() > 3000) {
            Log.i(Tag, str.substring(0, 3000));
            logLargeString(str.substring(3000));
        } else {
            Log.i(Tag, str); // continuation
        }
    }
}
