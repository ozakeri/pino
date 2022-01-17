package com.gap.bis_inspection.activity.driver;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
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

public class DriverActivity extends AppCompatActivity {
    Context context = this;
    EditText txt_search, txt_name, txt_family;
    TextView webSiteTV;
    String driverCode;
    private IDatabaseManager databaseManager;
    private CoreService coreService;
    ImageView searchIcon;
    RelativeLayout backIcon;
    ASync myTask = null;
    ASyncByNameParam aSyncByNameParam = null;
    private VpnCheck vpnCheck = new VpnCheck();
    private RadioButton radioButton_Name, radioButton_Code;
    String searchType = "driverCode";
    List<String> driverList = new ArrayList<>();
    List<String> driverCodeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driverlayout);
        init();


        radioButton_Code.setChecked(true);
        txt_search.setVisibility(View.VISIBLE);
        txt_name.setVisibility(View.GONE);
        txt_family.setVisibility(View.GONE);

        radioButton_Name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButton_Code.setChecked(false);
                txt_search.setVisibility(View.GONE);
                txt_name.setVisibility(View.VISIBLE);
                txt_family.setVisibility(View.VISIBLE);
                searchType = "driverName";
            }
        });
        radioButton_Code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButton_Name.setChecked(false);
                txt_search.setVisibility(View.VISIBLE);
                txt_name.setVisibility(View.GONE);
                txt_family.setVisibility(View.GONE);
                searchType = "driverCode";
            }
        });
        ////******backIcon*******////
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myTask == null) {
                    //exitAll();
                    finish();
                } else {
                    myTask.cancel(true);
                    //exitAll();
                    finish();
                }
            }
        });


        ////******search Icon*******////
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.out.println("searchType======" + searchType);
                System.out.println("txt_search======" + txt_search.getText().toString());
                System.out.println("txt_name======" + txt_name.getText().toString());
                System.out.println("txt_family======" + txt_family.getText().toString());

                if ((searchType.equals("driverCode") && txt_search.getText().length() == 0)) {
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.label_reportStrTv_NotNull, Toast.LENGTH_LONG);
                    CommonUtil.showToast(toast,DriverActivity.this);
                    toast.show();
                    return;
                }
                if (searchType.equals("driverName") && (txt_name.getText().toString().trim().length() == 0 || txt_family.getText().toString().trim().length() == 0)) {
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.label_reportStrTv_NotNull, Toast.LENGTH_LONG);
                    CommonUtil.showToast(toast,DriverActivity.this);
                    toast.show();
                    return;
                }


                if (searchType.equals("driverCode")) {
                    CommonUtil.farsiNumberReplacement(txt_search);
                    driverCode = txt_search.getText().toString();
                    if (vpnCheck.VpnConnectionCheck(getApplicationContext())) {
                        Toast.makeText(context, "لطفا vpn خود را خاموش نمایید.", Toast.LENGTH_SHORT).show();
                    } else {
                        myTask = new ASync();
                        myTask.execute();
                    }
                } else if (searchType.equals("driverName")) {
                    if (vpnCheck.VpnConnectionCheck(getApplicationContext())) {
                        Toast.makeText(context, "لطفا vpn خود را خاموش نمایید.", Toast.LENGTH_SHORT).show();
                    } else {
                        aSyncByNameParam = new ASyncByNameParam();
                        aSyncByNameParam.execute();
                    }
                }


            }
        });


        ////******add search button on the phone key board*******////
        txt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    txt_search = CommonUtil.farsiNumberReplacement(txt_search);
                    driverCode = txt_search.getText().toString();
                    if (vpnCheck.VpnConnectionCheck(getApplicationContext())) {
                        Toast.makeText(context, "لطفا vpn خود را خاموش نمایید.", Toast.LENGTH_SHORT).show();
                    } else {
                        myTask = new ASync();
                        myTask.execute();
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

    private void init() {
        txt_search = (EditText) findViewById(R.id.txt_search);
        txt_name = findViewById(R.id.txt_name);
        txt_family = findViewById(R.id.txt_family);
        searchIcon = (ImageView) findViewById(R.id.btn_search);
        databaseManager = new DatabaseManager(this);
        coreService = new CoreService(databaseManager);
        backIcon = (RelativeLayout) findViewById(R.id.back_Icon);
        webSiteTV = (TextView) findViewById(R.id.webSite_TV);
        radioButton_Name = findViewById(R.id.radioButton_Name);
        radioButton_Code = findViewById(R.id.radioButton_Code);
        txt_name.setFocusable(true);
    }

    public void mScan(View view) {
        try {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.setPackage("com.google.zxing.client.android");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            //on catch, show the download dialo
            showDialog(DriverActivity.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }

    @SuppressLint("MissingSuperCall")
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        try {
            if (requestCode == 0) {
                if (resultCode == RESULT_OK) {
                    driverCode = intent.getStringExtra("SCAN_RESULT");
                    if (vpnCheck.VpnConnectionCheck(getApplicationContext())) {
                        Toast.makeText(context, "لطفا vpn خود را خاموش نمایید.", Toast.LENGTH_SHORT).show();
                    } else {
                        myTask = new ASync();
                        myTask.execute();
                    }

                } else if (resultCode == RESULT_CANCELED) {
                    Toast toast = Toast.makeText(this, "Scan was Cancelled!", Toast.LENGTH_LONG);
                    CommonUtil.showToast(toast,DriverActivity.this);
                    toast.show();
                }
            }
        } catch (Exception e) {
        }
    }


    ////******getDriverProfile*******////

    private class ASync extends AsyncTask<Void, Void, Void> {
        private String result;
        private String errorMsg;
        private ProgressDialog progressDialog = null;

        @SuppressLint("StringFormatInvalid")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //drivercode = txt_drivercode.getText().toString();
            //progressBar.setVisibility(View.VISIBLE);
            progressDialog = new ProgressDialog(DriverActivity.this);
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
            //progressBar.setVisibility(View.INVISIBLE);
            progressDialog.dismiss();
            System.out.println("====result=" + result);
            if (result != null) {
                logLargeString(result);
                try {
                    JSONObject resultJson = new JSONObject(result);
                    if (errorMsg == null && !resultJson.isNull(Constants.SUCCESS_KEY)) {
                        if (!resultJson.isNull(Constants.RESULT_KEY)) {
                            JSONObject jsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                            if (!jsonObject.isNull("driverProfile")) {

                                JSONObject driverProfileJsonObject = jsonObject.getJSONObject("driverProfile");

                                Bundle bundle = new Bundle();
                                bundle.putString("driverProfile", driverProfileJsonObject.toString());
                                Intent intent = new Intent(context, DriverTabActivity.class);
                                intent.putExtra("driverProfile", driverProfileJsonObject.toString());
                                startActivity(intent);

                            }
                        }
                    } else {
                        if (errorMsg == null) {
                            errorMsg = resultJson.getString(Constants.ERROR_KEY);
                        }
                        Toast toast = Toast.makeText(context, errorMsg, Toast.LENGTH_LONG);
                        CommonUtil.showToast(toast,DriverActivity.this);
                        toast.show();
                    }
                } catch (JSONException e) {
                    Log.d("RegistrationFragment", e.getMessage());
                    Toast toast = Toast.makeText(context, getResources().getString(R.string.Some_error_accor_contact_admin), Toast.LENGTH_LONG);
                    CommonUtil.showToast(toast,DriverActivity.this);
                    toast.show();
                }
            } else {
                Toast toast = Toast.makeText(context, (errorMsg != null) ? errorMsg : getResources().getString(R.string.Some_error_accor_contact_admin), Toast.LENGTH_LONG);
                CommonUtil.showToast(toast,DriverActivity.this);
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
                    jsonObject.put("driverCode", driverCode);
                    MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, context);
                    try {
                        result = postJsonService.sendData("getDriverProfile", jsonObject, true);
                    } catch (SocketTimeoutException e) {
                        errorMsg = getResources().getString(R.string.Some_error_accor_contact_admin);
                    } catch (SocketException e) {
                        errorMsg = getResources().getString(R.string.Some_error_accor_contact_admin);
                    } catch (WebServiceException e) {
                        Log.d("RegistrationFragment", e.getMessage());
                    }

                } catch (JSONException e) {
                    Log.d("RegistrationFragment", e.getMessage());
                    return null;
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

    private class ASyncByNameParam extends AsyncTask<Void, Void, Void> {
        private String result;
        private String errorMsg;
        private ProgressDialog progressDialog = null;

        @SuppressLint("StringFormatInvalid")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //drivercode = txt_drivercode.getText().toString();
            //progressBar.setVisibility(View.VISIBLE);
            progressDialog = new ProgressDialog(DriverActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.label_progress_dialog));
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(true);
            progressDialog.show();

            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    ASyncByNameParam.this.cancel(true);
                    progressDialog.dismiss();
                }
            });

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //progressBar.setVisibility(View.INVISIBLE);
            progressDialog.dismiss();
            System.out.println("====result=" + result);
            if (result != null) {
                logLargeString(result);
                try {
                    JSONObject resultJson = new JSONObject(result);
                    if (errorMsg == null && !resultJson.isNull(Constants.SUCCESS_KEY)) {
                        if (!resultJson.isNull(Constants.RESULT_KEY)) {
                            JSONObject jsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                            if (!jsonObject.isNull("driverProfileList")) {
                                JSONArray driverProfileListJsonObject = jsonObject.getJSONArray("driverProfileList");
                                driverList.clear();
                                driverCodeList.clear();
                                for (int i = 0; i < driverProfileListJsonObject.length(); i++) {
                                    JSONObject jsonObject1 = (JSONObject) driverProfileListJsonObject.get(i);

                                    if (!jsonObject1.isNull("nameFv")) {
                                        driverList.add(jsonObject1.getString("nameFv"));
                                        System.out.println("nameFv===" + jsonObject1.getString("nameFv"));
                                    }
                                    if (!jsonObject1.isNull("driverCode")) {
                                        driverCodeList.add(jsonObject1.getString("driverCode"));
                                        System.out.println("driverCode===" + jsonObject1.getString("driverCode"));
                                    }

                                }

                                System.out.println("driverList===" + driverList.size());
                                System.out.println("driverCodeList===" + driverCodeList.size());

                                if (driverList.size() > 0 && driverCodeList.size() > 0) {
                                    showDialog(driverList, driverCodeList);
                                }
                            }
                        }
                    } else {
                        if (errorMsg == null) {
                            errorMsg = resultJson.getString(Constants.ERROR_KEY);
                        }
                        Toast toast = Toast.makeText(context, errorMsg, Toast.LENGTH_LONG);
                        CommonUtil.showToast(toast,DriverActivity.this);
                        toast.show();
                    }
                } catch (JSONException e) {
                    Log.d("RegistrationFragment", e.getMessage());
                }
            } else {
                Toast toast = Toast.makeText(context, (errorMsg != null) ? errorMsg : "222222222222222222", Toast.LENGTH_LONG);
                CommonUtil.showToast(toast,DriverActivity.this);
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
                    jsonObject.put("name", txt_name.getText().toString().trim());
                    jsonObject.put("family", txt_family.getText().toString().trim());
                    MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, context);
                    try {
                        result = postJsonService.sendData("getDriverProfileListByParam", jsonObject, true);
                    } catch (SocketTimeoutException e) {
                        errorMsg = "333333333333333333";
                    } catch (SocketException e) {
                        errorMsg = "44444444444444444444";
                    } catch (WebServiceException e) {
                        Log.d("RegistrationFragment", e.getMessage());
                    }

                } catch (JSONException e) {
                    Log.d("RegistrationFragment", e.getMessage());
                    return null;
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

    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("http://www.dl.farsroid.com/app/Barcode-Scanner-4.7.5(FarsRoid.Com).apk");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                // act.finish();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (myTask == null) {
            //exitAll();         finish();
        } else {
            myTask.cancel(true);
            //exitAll();         finish();
        }
    }

    private void exitAll() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.motion, R.anim.motion2);
    }

    public void logLargeString(String str) {
        String Tag = "DriverActivity=";
        if (str.length() > 3000) {
            Log.i(Tag, str.substring(0, 3000));
            logLargeString(str.substring(3000));
        } else {
            Log.i(Tag, str); // continuation
        }
    }

    public void showDialog(List<String> driverList, final List<String> driverCodeList) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.driver_list_dialog);
        ListView list_driverName = dialog.findViewById(R.id.list_driverName);
        list_driverName.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, driverList));

        list_driverName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String driverCode1 = driverCodeList.get(position);
                driverCode = driverCode1;
                myTask = new ASync();
                myTask.execute();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
