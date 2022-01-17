package com.gap.bis_inspection.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.FileProvider;

import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gap.bis_inspection.BuildConfig;
import com.gap.bis_inspection.R;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.CommonUtil;
import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.common.ShaPasswordEncoder;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.objectmodel.DeviceSetting;
import com.gap.bis_inspection.db.objectmodel.User;
import com.gap.bis_inspection.exception.WebServiceException;
import com.gap.bis_inspection.service.CoreService;
import com.gap.bis_inspection.util.DateUtils;
import com.gap.bis_inspection.webservice.MyPostJsonService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SettingActivity extends AppCompatActivity {

    private SwitchCompat switch_Button;
    private CoreService coreService;
    private DatabaseManager databaseManager;
    private ProgressDialog progressBar;
    private ASyncGetUpdate aSyncGetUpdate = null;
    private AppController appController;
    ProgressDialog progressdialog;
    public static final int Progress_Dialog_Progress = 0;
    private String updateUrl;
    private long apk_DownloadId;
    private DownloadManager downloadManager;
    private ProgressBar horizonProgressBar;
    private TextView stateLabel;
    private String appFileName;
    private DpdateWork downloadWork;
    private int versionCode = 0;
    private Button btnDownloadAndUpdate, btnUpdateLocal;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        switch_Button = (SwitchCompat) findViewById(R.id.switch_Button);
        RelativeLayout changeLocalPassword = (RelativeLayout) findViewById(R.id.changeLocalPassword1_TV);
        RelativeLayout changeBisPassword = (RelativeLayout) findViewById(R.id.changeBisPassword1_TV);
        RelativeLayout deleteAccount = (RelativeLayout) findViewById(R.id.layout_deleteAccount);
        RelativeLayout backIcon = (RelativeLayout) findViewById(R.id.backIcon);
        TextView txtDeleteAccount = (TextView) findViewById(R.id.deleteAccount_txt);
       // progressBar1 = findViewById(R.id.ProgressIcon);
        horizonProgressBar = findViewById(R.id.horizonProgressBar);
        btnDownloadAndUpdate = findViewById(R.id.BtnDownloadAndUpdate);
        btnUpdateLocal = findViewById(R.id.BtnUpdateLocal);
        stateLabel = findViewById(R.id.StateLabel);


        databaseManager = new DatabaseManager(this);
        coreService = new CoreService(databaseManager);
        appController = (AppController) this.getApplication();
        final User user = appController.getCurrentUser();
        downloadManager = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);



        if (appController.getPermissionMap().containsKey("ROLE_APP_DEVELOPER_OPTION")) {
            deleteAccount.setVisibility(View.VISIBLE);
            txtDeleteAccount.setVisibility(View.VISIBLE);
        } else {
            deleteAccount.setVisibility(View.GONE);
            txtDeleteAccount.setVisibility(View.GONE);
        }

        if (user != null) {
            if (user.getAutoLogin()) {
                switch_Button.setChecked(true);
            } else if (!user.getAutoLogin()) {
                switch_Button.setChecked(false);
            }
        }

        switch_Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (user != null) {
                    if (switch_Button.isChecked()) {
                        user.setAutoLogin(true);
                        user.setLoginIs(Boolean.FALSE);
                        coreService.updateUser(user);
                    } else if (!switch_Button.isChecked()) {
                        user.setAutoLogin(Boolean.FALSE);
                        user.setLoginIs(Boolean.FALSE);
                        coreService.updateUser(user);
                    }
                }
            }
        });


        ////****** change Local Password *******////

        changeLocalPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(SettingActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_setting_changepassword_layout);

                RelativeLayout local_layout = (RelativeLayout) dialog.findViewById(R.id.local_layout);
                RelativeLayout bis_layout = (RelativeLayout) dialog.findViewById(R.id.bis_layout);
                final EditText changeLocalPassword = (EditText) dialog.findViewById(R.id.changeLocalPassword_ET);
                final EditText changeLocalPasswordConfirm = (EditText) dialog.findViewById(R.id.changeLocalPasswordConfirm_ET);
                Button localAction = (Button) dialog.findViewById(R.id.local_action);
                RelativeLayout close = (RelativeLayout) dialog.findViewById(R.id.close_Button);
                dialog.show();

                local_layout.setVisibility(View.VISIBLE);
                bis_layout.setVisibility(View.GONE);

                localAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String password = changeLocalPassword.getText().toString();
                        String confirmPassword = changeLocalPasswordConfirm.getText().toString();
                        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                            changeLocalPassword.setError(getResources().getString(R.string.label_reportStrTv_NotNull));
                            changeLocalPasswordConfirm.setError(getResources().getString(R.string.label_reportStrTv_NotNull));

                        } else if (!changeLocalPassword.getText().toString().equals(changeLocalPasswordConfirm.getText().toString())) {
                            Toast toast = Toast.makeText(getApplicationContext(), R.string.changeLocal_password_checkConfirm, Toast.LENGTH_SHORT);
                            CommonUtil.showToast(toast,SettingActivity.this);
                            toast.show();
                        } else {
                            if (user != null) {
                                try {
                                    user.setPassword(ShaPasswordEncoder.SHA1(password));
                                } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                            coreService.updateUser(user);
                            dialog.dismiss();

                            Toast toast = Toast.makeText(getApplicationContext(), R.string.label_SettingActivity_changePassword, Toast.LENGTH_SHORT);
                            CommonUtil.showToast(toast,SettingActivity.this);
                            toast.show();
                        }
                    }
                });

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });


        ////****** change Bis Password *******////

        changeBisPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(SettingActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_setting_changepassword_layout);

                RelativeLayout local_layout = (RelativeLayout) dialog.findViewById(R.id.local_layout);
                RelativeLayout bis_layout = (RelativeLayout) dialog.findViewById(R.id.bis_layout);
                final EditText changeBisPassword = (EditText) dialog.findViewById(R.id.changeBisPassword_ET);
                Button bisAction = (Button) dialog.findViewById(R.id.bis_action);
                RelativeLayout close = (RelativeLayout) dialog.findViewById(R.id.close_Button);
                dialog.show();

                bis_layout.setVisibility(View.VISIBLE);
                local_layout.setVisibility(View.GONE);

                bisAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String password = changeBisPassword.getText().toString();
                        if (TextUtils.isEmpty(password)) {
                            changeBisPassword.setError(getResources().getString(R.string.label_reportStrTv_NotNull));
                        } else {
                            if (user != null) {
                                try {
                                    user.setBisPassword(ShaPasswordEncoder.SHA1(password));
                                } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                            coreService.updateUser(user);
                            dialog.dismiss();

                            Toast toast = Toast.makeText(getApplicationContext(), R.string.label_SettingActivity_changePassword, Toast.LENGTH_SHORT);
                            CommonUtil.showToast(toast,SettingActivity.this);
                            toast.show();
                        }
                    }
                });

                close.setOnClickListener(new View.OnClickListener() {
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
                finish();
            }
        });


        ////****** delete Account *******////

        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(SettingActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_delete_account_layout);
                Button actionYes = (Button) dialog.findViewById(R.id.action_YES);
                Button actionNo = (Button) dialog.findViewById(R.id.action_NO);
                RelativeLayout close = (RelativeLayout) dialog.findViewById(R.id.close_Button);
                dialog.show();

                actionYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        databaseManager.dropDatabase();
                        SharedPreferences.Editor editor = AppController.getInstance().getSharedPreferences().edit();
                        editor.putString(Constants.DOMAIN_WEB_SERVICE_URL, null);
                        editor.apply();
                        showDomainFragmentPage();
                        dialog.dismiss();
                    }
                });

                actionNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });

        btnDownloadAndUpdate.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                AskForPermission();
                aSyncGetUpdate = new ASyncGetUpdate();
                aSyncGetUpdate.execute();
                btnDownloadAndUpdate.setVisibility(View.GONE);

            }
        });

        btnUpdateLocal = findViewById(R.id.BtnUpdateLocal);
        btnUpdateLocal.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                DoInstall();
            }
        });

    }

    private void exitAll() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.motion, R.anim.motion2);
    }

    public void showDomainFragmentPage() {
        Intent intent = new Intent(SettingActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
            /*Intent i = new Intent(getActivity(), ActivationFragment.class);
            startActivity(i);*/
    }


    ////****** get last Document Version *******////
    private class ASyncGetUpdate extends AsyncTask<Void, Void, Void> {
        private String result;
        private String errorMsg;

        @SuppressLint("StringFormatInvalid")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = ProgressDialog.show(SettingActivity.this, null, getResources().getString(R.string.label_progress_dialog, true), true);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.dismiss();
            int versionCode = 0;
            int currentVersion = BuildConfig.VERSION_CODE;
            if (result != null) {
                try {
                    JSONObject resultJson = new JSONObject(result);
                    if (errorMsg == null && !resultJson.isNull(Constants.SUCCESS_KEY)) {
                        if (!resultJson.isNull(Constants.RESULT_KEY)) {
                            JSONObject jsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                            if (!jsonObject.isNull("document")) {
                                JSONObject documentJsonObject = jsonObject.getJSONObject("document");
                                if (!documentJsonObject.isNull("lastDocumentVersion")) {
                                    JSONObject lastVersionJsonObject = documentJsonObject.getJSONObject("lastDocumentVersion");

                                    if (!lastVersionJsonObject.isNull("versionNo")) {
                                        versionCode = Integer.parseInt(lastVersionJsonObject.getString("versionNo"));

                                        if (!lastVersionJsonObject.isNull("pathUrl")) {
                                            updateUrl = lastVersionJsonObject.getString("pathUrl");
                                            if (currentVersion < versionCode) {
                                                final Dialog dialog = new Dialog(SettingActivity.this);
                                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                dialog.setContentView(R.layout.update_application_layout);
                                                Button actionYes = (Button) dialog.findViewById(R.id.action_YES);

                                                Button actionNo = (Button) dialog.findViewById(R.id.action_NO);
                                                RelativeLayout close = (RelativeLayout) dialog.findViewById(R.id.close_Button);
                                                dialog.show();

                                                //updateUrl = "http://91.98.112.159:4000/s/WwHT8DJrQn1ltYX/download";
                                                System.out.println("updateUrl====" + updateUrl);

                                                actionYes.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        if (updateUrl != null) {
                                                            appFileName = GetFileNameFromUrl(updateUrl);
                                                            if (CommonUtil.isConnected(SettingActivity.this)) {
                                                                dialog.dismiss();

                                                                if (appFileName == null || appFileName == "")
                                                                    return;
                                                                horizonProgressBar.setVisibility(View.VISIBLE);
                                                                //progressBar1.setVisibility(View.VISIBLE);
                                                                btnDownloadAndUpdate.setVisibility(View.GONE);
                                                                ResetDownloadUI();
                                                                DoUpdate();

                                                                Uri uri = Uri.parse(updateUrl);

                                                            } else {
                                                                Toast toast = Toast.makeText(getApplicationContext(), R.string.Some_error_accor_contact_admin, Toast.LENGTH_SHORT);
                                                                CommonUtil.showToast(toast,SettingActivity.this);
                                                                toast.show();
                                                            }
                                                        }
                                                    }
                                                });

                                                actionNo.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        dialog.dismiss();
                                                    }
                                                });

                                                close.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                            } else {
                                                Toast toast = Toast.makeText(getApplicationContext(), R.string.result_update_Toast, Toast.LENGTH_SHORT);
                                                CommonUtil.showToast(toast,SettingActivity.this);
                                                toast.show();
                                                btnDownloadAndUpdate.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    } else {
                        if (errorMsg == null) {
                            errorMsg = resultJson.getString(Constants.ERROR_KEY);
                        }
                        Toast toast = Toast.makeText(SettingActivity.this, errorMsg, Toast.LENGTH_LONG);
                        CommonUtil.showToast(toast,SettingActivity.this);
                        toast.show();
                    }
                } catch (JSONException e) {
                    Log.d("RegistrationFragment", e.getMessage());
                    Toast toast = Toast.makeText(SettingActivity.this, getResources().getString(R.string.Some_error_accor_contact_admin), Toast.LENGTH_LONG);
                    CommonUtil.showToast(toast,SettingActivity.this);
                    toast.show();
                }
            } else {
                Toast toast = Toast.makeText(SettingActivity.this, (errorMsg != null) ? errorMsg : getResources().getString(R.string.Some_error_accor_contact_admin), Toast.LENGTH_LONG);
                CommonUtil.showToast(toast,SettingActivity.this);
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
                    MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, SettingActivity.this);
                    try {
                        result = postJsonService.sendData("getLastDocumentVersion", jsonObject, true);
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
                MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, SettingActivity.this);
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

    protected void onResume() {
        super.onResume();
        appController.setCurrentActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        appController.setCurrentActivity(null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        appController.setCurrentActivity(null);
    }

    private void viewAllDownloads() {
        Intent intent = new Intent();
        intent.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
        startActivity(intent);
    }
    private void ResetDownloadUI() {
        stateLabel.setText("");
        horizonProgressBar.setMax(100);
        horizonProgressBar.setProgress(0);
        SwitchBusyIcon(true);
    }

    private void SwitchBusyIcon(boolean flag) {
        int visible = flag ? View.VISIBLE : View.INVISIBLE;
       // progressBar1.setVisibility(visible);
    }

    private void DoUpdate() {
        downloadWork = new DpdateWork();
        downloadWork.execute(updateUrl);
    }


    public String GetFileNameFromUrl(String urlString) {
        return urlString.substring(urlString.lastIndexOf('/') + 1).split("\\?")[0].split("#")[0];
    }

    class DpdateWork extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SetStateLabel("در حال دانلود ... ");
        }

        @Override
        protected String doInBackground(String... params) {

            try {

                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setUseCaches(false);
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();
                }

                File downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File outputFile = new File(downloadPath.getPath(), appFileName);
                if (outputFile.exists()) {
                    outputFile.delete();
                }

                int lenghtOfFile = connection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream(), 1024);
                OutputStream output = new FileOutputStream(downloadPath.toString() + "/" + appFileName);
                byte data[] = new byte[1024];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    int percent = (int) (total * 100 / lenghtOfFile); //0~100
                    publishProgress(percent);
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
            } catch (IOException e) {
                Log.d("mark", "Download io Error:" + e.getMessage());
            } catch (SecurityException e) {
                Log.d("mark", "Download security Error:" + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            horizonProgressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null) {
                SetStateLabel("دانلود با موفقیت انجام شد");
                SwitchBusyIcon(false);
                btnUpdateLocal.setVisibility(View.VISIBLE);
                horizonProgressBar.setVisibility(View.GONE);
                btnDownloadAndUpdate.setVisibility(View.GONE);
               // progressBar1.setVisibility(View.GONE);
                // DoInstall();
            } else {
                SetStateLabel(result);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

            SwitchBusyIcon(false);
            SetStateLabel("downloadCancel");
        }


    }


    private void SetStateLabel(String msg) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        stateLabel.setText(msg);
    }

    private void DoInstall() {

        File downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(downloadPath.getPath(), appFileName);
        if (!file.exists()) {
            SetStateLabel("fileNotExist");
            return;
        }

        Uri fileUri = Uri.fromFile(file);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fileUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
        }

        Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
        intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);

        this.startActivity(intent);
    }

    private void AskForPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int REQUEST_CODE = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    this.requestPermissions(permissions, REQUEST_CODE);
                    return;
                }
            }
        }
    }

}
