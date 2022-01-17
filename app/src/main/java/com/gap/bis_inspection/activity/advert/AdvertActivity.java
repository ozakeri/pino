package com.gap.bis_inspection.activity.advert;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.gap.bis_inspection.R;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AdvertActivity extends AppCompatActivity {

    private ImageView btnSearchCar;
    private EditText edt_search;
    private IDatabaseManager databaseManager;
    private CoreService coreService;
    private String plateText = "";
    private AppCompatButton searchAdvert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advert);

        edt_search = findViewById(R.id.edt_search);
        btnSearchCar = findViewById(R.id.btn_search);
        searchAdvert = findViewById(R.id.searchAdvert);
        databaseManager = new DatabaseManager(this);
        coreService = new CoreService(databaseManager);

        btnSearchCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                plateText = edt_search.getText().toString();
                if (plateText.matches("")) {
                    edt_search.setError("NULL");
                    return;
                }

                new GetAd().execute();

            }
        });


        edt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    plateText = edt_search.getText().toString();
                    if (plateText.matches("") || plateText.length() == 0 || plateText.equals("")) {
                        edt_search.setError("پلاک را وارد کنید");
                    } else {
                        new GetAd().execute();
                        return true;
                    }

                }
                return false;
            }
        });

        searchAdvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdvertActivity.this, AdvertListActivity.class));
            }
        });

        findViewById(R.id.closeIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private class GetAd extends AsyncTask<Void, Void, Void> {
        private String result;
        private String errorMsg;
        private ProgressDialog progressDialog = null;

        @SuppressLint("StringFormatInvalid")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressBar.setVisibility(View.VISIBLE);
            progressDialog = new ProgressDialog(AdvertActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.label_progress_dialog));
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(true);
            progressDialog.show();

            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    GetAd.this.cancel(true);
                    progressDialog.dismiss();
                }
            });
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();

            try {
                System.out.println("result======" + result);
                if (result != null) {

                    if (result.length() == 0) {
                        Toast toast = Toast.makeText(AdvertActivity.this, "تبلیغات یافت نشد", Toast.LENGTH_LONG);
                        CommonUtil.showToast(toast,AdvertActivity.this);
                        toast.show();
                        return;
                    }

                    JSONObject resultJson = new JSONObject(result);
                    if (errorMsg == null && !resultJson.isNull(Constants.SUCCESS_KEY)) {
                        Intent intent = new Intent(AdvertActivity.this, SearchAdvertActivity.class);
                        intent.putExtra("result", result);
                        startActivity(intent);
                    } else {
                        Toast toast = Toast.makeText(AdvertActivity.this, "تبلیغات یافت نشد", Toast.LENGTH_LONG);
                        CommonUtil.showToast(toast,AdvertActivity.this);
                        toast.show();
                    }
                } else {
                    Toast toast = Toast.makeText(AdvertActivity.this, "تبلیغات یافت نشد", Toast.LENGTH_LONG);
                    CommonUtil.showToast(toast,AdvertActivity.this);
                    toast.show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
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
                    jsonObject.put("plateText", plateText);
                    //jsonObject.put("carInfoType", carInfoType);
                    MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, AdvertActivity.this);
                    try {
                        result = postJsonService.sendData("getCarAdvertisement", jsonObject, true);
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
                MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, AdvertActivity.this);
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

}