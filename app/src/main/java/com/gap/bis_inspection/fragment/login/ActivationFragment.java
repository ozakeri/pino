package com.gap.bis_inspection.fragment.login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.CommonUtil;
import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.db.enumtype.LoginStatusEn;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.manager.IDatabaseManager;
import com.gap.bis_inspection.db.objectmodel.DeviceSetting;
import com.gap.bis_inspection.db.objectmodel.User;
import com.gap.bis_inspection.exception.WebServiceException;
import com.gap.bis_inspection.service.CoreService;
import com.gap.bis_inspection.util.DateUtils;
import com.gap.bis_inspection.util.FontCache;
import com.gap.bis_inspection.webservice.MyPostJsonService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class ActivationFragment extends Fragment {

    ProgressDialog progressBar;
    EditText activationCodeEditText;
    private IDatabaseManager databaseManager;
    private CoreService coreService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activation, container, false);
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                100);

        // progressBar = (ProgressBar) view.findViewById(R.id.progress);
        activationCodeEditText = (EditText) view.findViewById(R.id.activationCode);
        AppCompatTextView btnConfirm = view.findViewById(R.id.btn_confirm);
        databaseManager = new DatabaseManager(getActivity());
        coreService = new CoreService(databaseManager);

        Typeface customFont = FontCache.getTypeface("IRANSansMobile(FaNum)_Bold.ttf", getActivity());
        activationCodeEditText.setTypeface(customFont);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activationCodeEditText = CommonUtil.farsiNumberReplacement(activationCodeEditText);
                new ASync().execute();
            }
        });

        return view;
    }

    private class ASync extends AsyncTask<Void, Void, Void> {
        private String result;
        private String errorMsg;
        private String activationCode;


        @SuppressLint("StringFormatInvalid")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressBar.setVisibility(View.VISIBLE);
            progressBar = ProgressDialog.show(getActivity(), null, getResources().getString(R.string.label_progress_dialog, true), true);
            activationCode = activationCodeEditText.getText().toString();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            System.out.println("====result=" + result);
            if (result != null) {
                try {
                    JSONObject resultJson = new JSONObject(result);
                    if (errorMsg == null && !resultJson.isNull(Constants.SUCCESS_KEY)) {
                        AppController application = (AppController) Objects.requireNonNull(getActivity()).getApplication();

                        User user = application.getCurrentUser();
                        if (!resultJson.isNull(Constants.RESULT_KEY)) {
                            JSONObject jsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                            if (!jsonObject.isNull("name")) {
                                user.setName(jsonObject.getString("name"));
                            }
                            if (!jsonObject.isNull("userId")) {
                                user.setServerUserId(jsonObject.getLong("userId"));
                            }
                            if (!jsonObject.isNull("family")) {
                                user.setFamily(jsonObject.getString("family"));
                            }
                            if (!jsonObject.isNull("username")) {
                                user.setUsername(jsonObject.getString("username"));
                            }
                            if (!jsonObject.isNull("tokenPass")) {
                                user.setBisPassword(jsonObject.getString("tokenPass"));
                            }
                            if (!jsonObject.isNull("companyName")) {
                                user.setCompanyName(jsonObject.getString("companyName"));
                            }


                            if (!jsonObject.isNull("actSearchCarOnPropertyCode")) {
                                boolean OnPropertyCode = jsonObject.getBoolean("actSearchCarOnPropertyCode");
                                SharedPreferences.Editor editor = AppController.getInstance().getSharedPreferences().edit();
                                editor.putBoolean(Constants.ON_PROPERTY_CODE, OnPropertyCode);
                                editor.apply();
                            }

                            //***************************=================**************************
                            String baseService = AppController.getInstance().getSharedPreferences().getString(Constants.DOMAIN_WEB_SERVICE_URL, null);
                            if (baseService != null) {
                                if (baseService.trim().equals("https://bis.tehran.ir")) {
                                    SharedPreferences.Editor editor = AppController.getInstance().getSharedPreferences().edit();
                                    editor.putBoolean(Constants.ON_PROPERTY_CODE, false);
                                    editor.apply();
                                } else {
                                    SharedPreferences.Editor editor = AppController.getInstance().getSharedPreferences().edit();
                                    editor.putBoolean(Constants.ON_PROPERTY_CODE, true);
                                    editor.apply();
                                }
                            }
                            //****************************====================*************************

                            if (!jsonObject.isNull("pictureBytes")) {
                                String pictureBytes = jsonObject.getString("pictureBytes");
                                SharedPreferences.Editor editor = AppController.getInstance().getSharedPreferences().edit();
                                editor.putString(Constants.JSON_PICTURE_BYTE, pictureBytes);
                                editor.apply();

                                byte[] bytes = new byte[0];
                                JSONArray pictureBytesJsonArray = jsonObject.getJSONArray("pictureBytes");
                                bytes = new byte[pictureBytesJsonArray.length()];
                                for (int i = 0; i < pictureBytesJsonArray.length(); i++) {
                                    bytes[i] = Integer.valueOf(pictureBytesJsonArray.getInt(i)).byteValue();
                                }

                                String path = Environment.getExternalStorageDirectory().toString() + Constants.DEFAULT_OUT_PUT_DIR + Constants.DEFAULT_USER_IMG_OUT_PUT_DIR;
                                File dir = new File(path);

                                try {
                                    int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                    if (permissionCheck == PackageManager.PERMISSION_GRANTED) {

                                        if (!dir.exists()) {
                                            boolean b = dir.mkdirs();
                                        }
                                        String picturePathUrl = path + "/user-pic.jpg";
                                        OutputStream outputStream = null;
                                        File file = new File(picturePathUrl); // the File to save to
                                        outputStream = new FileOutputStream(file);
                                        outputStream.write(bytes);
                                        user.setPicturePathUrl(picturePathUrl);

                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.DAY_OF_MONTH, Constants.LOGIN_EXPIRE_VALIDATION_TIME_DURATION_DAY);
                            user.setLoginStatus(LoginStatusEn.PasswordCreation.ordinal());
                            user.setExpireDate(calendar.getTime());
                            databaseManager.updateUser(user);
                            DatabaseManager.SERVER_USER_ID = user.getServerUserId();
                            application.setCurrentUser(user);
                            showPasswordCreationPage();
                        } else {
                            Toast toast = Toast.makeText(getActivity(), getResources().getString(R.string.Some_error_accor_contact_admin), Toast.LENGTH_LONG);
                            CommonUtil.showToast(toast,getActivity());
                            toast.show();
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
                    Log.d("ActivationFragment", e.getMessage());
                    Toast toast = Toast.makeText(getActivity(), getResources().getString(R.string.Some_error_accor_contact_admin), Toast.LENGTH_LONG);
                    CommonUtil.showToast(toast,getActivity());
                    toast.show();
                }
            } else {
                Toast toast = Toast.makeText(getActivity(), getResources().getString(R.string.Some_error_accor_contact_admin), Toast.LENGTH_LONG);
                CommonUtil.showToast(toast,getActivity());
                toast.show();
            }
            //progressBar.setVisibility(View.GONE);
            progressBar.dismiss();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Void doInBackground(Void... voids) {
            if (isDeviceDateTimeValid()) {
                try {
                    AppController application = (AppController) Objects.requireNonNull(getActivity()).getApplication();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("mobileNo", application.getCurrentUser().getMobileNo());
                    jsonObject.put("activationCode", activationCode);
                    MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, getActivity());
                    try {
                        result = postJsonService.sendData("activationCodeValidation", jsonObject, true);
                    } catch (SocketTimeoutException e) {
                        errorMsg = getResources().getString(R.string.Some_error_accor_contact_admin);
                    } catch (SocketException e) {
                        errorMsg = getResources().getString(R.string.Some_error_accor_contact_admin);
                    } catch (WebServiceException e) {
                        Log.d("ActivationFragment", e.getMessage());
                    }

                } catch (JSONException e) {
                    Log.d("ActivationFragment", e.getMessage());
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

        public void showPasswordCreationPage() {
            FragmentManager fragmentManager = getActivity().getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_place, new PasswordCreationFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
}
