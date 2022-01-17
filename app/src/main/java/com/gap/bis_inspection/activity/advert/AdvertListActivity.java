package com.gap.bis_inspection.activity.advert;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.adapter.advert.AdvertListAdapter;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.CommonUtil;
import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.manager.IDatabaseManager;
import com.gap.bis_inspection.db.objectmodel.DeviceSetting;
import com.gap.bis_inspection.exception.WebServiceException;
import com.gap.bis_inspection.service.CoreService;
import com.gap.bis_inspection.util.DateUtils;
import com.gap.bis_inspection.util.RecyclerItemClickListener;
import com.gap.bis_inspection.webservice.MyPostJsonService;
import com.jaredrummler.materialspinner.MaterialSpinner;

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

public class AdvertListActivity extends AppCompatActivity {

    List<JSONObject> advertisementList;
    private IDatabaseManager databaseManager;
    private CoreService coreService;
    String propertyCode = "";
    private static final String[] statusProcess = {
            " منتظر بازدید خودرو ",
            " منتظر مراجعه ",
            " در حال اجرا در سوله ",
    };
    private RecyclerView recyclerView;
    private String processName = "1";
    private GetAdList getAdList = null;
    private TextView txt_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advert_list);

        txt_count = findViewById(R.id.txt_count);
        databaseManager = new DatabaseManager(this);
        coreService = new CoreService(databaseManager);
        MaterialSpinner spinner = findViewById(R.id.spinner);
        spinner.setItems(statusProcess);

        txt_count.setText(" تعداد نتایج جستجو: " + CommonUtil.latinNumberToPersian("0"));

        if (getAdList != null) {
            getAdList.cancel(true);
            getAdList = null;
        }

        getAdList = new GetAdList();
        getAdList.execute();


        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                System.out.println("item=====" + position);
                switch (position) {
                    case 0:
                        processName = "1";
                        break;

                    case 1:
                        processName = "6";
                        break;

                    case 2:
                        processName = "7";
                        break;
                }
                if (getAdList != null) {
                    getAdList.cancel(true);
                    getAdList = null;
                }

                getAdList = new GetAdList();
                getAdList.execute();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                JSONObject json = advertisementList.get(position);

                try {
                    if (!json.isNull("car")) {
                        JSONObject jsonDate = json.getJSONObject("car");
                        propertyCode = jsonDate.getString("propertyCode");
                        System.out.println("propertyCode====" + propertyCode);
                        new GetAd().execute();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }));

        findViewById(R.id.closeIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private class GetAdList extends AsyncTask<Void, Void, Void> {
        private String result = "";
        private String errorMsg;
        private ProgressDialog progressDialog = null;

        @SuppressLint("StringFormatInvalid")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressBar.setVisibility(View.VISIBLE);
            progressDialog = new ProgressDialog(AdvertListActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.label_progress_dialog));
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();

            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    GetAdList.this.cancel(true);
                    progressDialog.dismiss();
                }
            });
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();

            try {
                System.out.println("======result======" + result);
                advertisementList = new ArrayList<>();
                if (result != null && !result.isEmpty()){
                    JSONObject resultJson = new JSONObject(result);
                    if (errorMsg == null && !resultJson.isNull(Constants.SUCCESS_KEY)) {
                        System.out.println("result======" + result);
                        if (!resultJson.isNull(Constants.RESULT_KEY)) {
                            JSONObject jsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                            if (!jsonObject.isNull("advertisementList")) {
                                JSONArray advertisementListJSONArray = jsonObject.getJSONArray("advertisementList");

                                System.out.println("length=-=-=-=-" + advertisementListJSONArray.length());
                                for (int i = 0; i < advertisementListJSONArray.length(); i++) {
                                    JSONObject advertisementListJsonObject = (JSONObject) advertisementListJSONArray.get(i);
                                    advertisementList.add(advertisementListJsonObject);
                                }
                            }
                        }
                    } else {
                        Toast toast = Toast.makeText(AdvertListActivity.this, "result is Null", Toast.LENGTH_LONG);
                        CommonUtil.showToast(toast,AdvertListActivity.this);
                        toast.show();
                    }
                }

                AdvertListAdapter adapter = new AdvertListAdapter(advertisementList);
                recyclerView.setAdapter(adapter);
                txt_count.setText(" تعداد نتایج جستجو: " + CommonUtil.latinNumberToPersian(String.valueOf(advertisementList.size())));

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
                    System.out.println("processName--======" + processName);
                    jsonObject.put("processName", processName);
                    MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, AdvertListActivity.this);
                    try {
                        result = postJsonService.sendData("getAdvertisementList", jsonObject, true);
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
                MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, AdvertListActivity.this);
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

    private class GetAd extends AsyncTask<Void, Void, Void> {
        private String result;
        private String errorMsg;
        private ProgressDialog progressDialog = null;

        @SuppressLint("StringFormatInvalid")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressBar.setVisibility(View.VISIBLE);
            progressDialog = new ProgressDialog(AdvertListActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.label_progress_dialog));
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();

            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    AdvertListActivity.GetAd.this.cancel(true);
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
                        Toast toast = Toast.makeText(AdvertListActivity.this, "تبلیغات یافت نشد", Toast.LENGTH_LONG);
                        CommonUtil.showToast(toast,AdvertListActivity.this);
                        toast.show();
                        return;
                    }
                    JSONObject resultJson = new JSONObject(result);
                    if (errorMsg == null && !resultJson.isNull(Constants.SUCCESS_KEY)) {
                        Intent intent = new Intent(AdvertListActivity.this, SearchAdvertActivity.class);
                        intent.putExtra("result", result);
                        startActivity(intent);
                    } else {
                        Toast toast = Toast.makeText(AdvertListActivity.this, "تبلیغات یافت نشد", Toast.LENGTH_LONG);
                        CommonUtil.showToast(toast,AdvertListActivity.this);
                        toast.show();
                    }
                } else {
                    Toast toast = Toast.makeText(AdvertListActivity.this, "تبلیغات یافت نشد", Toast.LENGTH_LONG);
                    CommonUtil.showToast(toast,AdvertListActivity.this);
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
                    jsonObject.put("propertyCode", propertyCode);
                    //jsonObject.put("carInfoType", carInfoType);
                    MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, AdvertListActivity.this);
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
                MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, AdvertListActivity.this);
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