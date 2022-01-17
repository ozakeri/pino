package com.gap.bis_inspection.fragment.login;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

//import com.google.firebase.analytics.FirebaseAnalytics;

public class RegistrationFragment extends Fragment {

    ProgressDialog progressBar;
    EditText mobileNoEditText;
    private IDatabaseManager databaseManager;
    private CoreService coreService;
    private static final int MY_PERMISSIONS_REQUEST = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration, container, false);

        mobileNoEditText = (EditText) view.findViewById(R.id.mobileNo);
        Typeface customFont = FontCache.getTypeface("IRANSansMobile(FaNum)_Bold.ttf", getActivity());
        mobileNoEditText.setTypeface(customFont);
        TextView btnConfirm = view.findViewById(R.id.btn_confirm);
        //mobileNoEditText.requestFocus();
        mobileNoEditText.setFocusableInTouchMode(true);

        databaseManager = new DatabaseManager(getActivity());
        coreService = new CoreService(databaseManager);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mobileNoEditText = CommonUtil.farsiNumberReplacement(mobileNoEditText);
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    new ASync().execute();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST);
                    }
                }

            }
        });


        return view;
    }

    private class ASync extends AsyncTask<Void, Void, Void> {
        private String result;
        private String errorMsg;
        private String mobileNo;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressBar.setVisibility(View.VISIBLE);
            progressBar = ProgressDialog.show(getActivity(), null, getActivity().getResources().getString(R.string.label_progress_dialog), true);
            mobileNo = mobileNoEditText.getText().toString();
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (result != null) {
                try {
                    JSONObject resultJson = new JSONObject(result);
                    if (errorMsg == null && !resultJson.isNull(Constants.SUCCESS_KEY)) {

                        User user = databaseManager.getUserByMobileNo(mobileNo);
                        if (user != null) {
                        } else {
                            user = new User();
                            user.setMobileNo(mobileNo);
                            user.setLoginStatus(LoginStatusEn.Init.ordinal());
                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.MINUTE, Constants.ACTIVATION_CODE_VALIDATION_TIME_DURATION_MIN);
                            user.setExpireDate(calendar.getTime());

                            databaseManager.insertOrUpdateUser(user);
                        }
                        AppController application = (AppController) Objects.requireNonNull(getActivity()).getApplication();
                        application.setCurrentUser(user);
                        Toast toast = Toast.makeText(getActivity(), R.string.send_code_activation_Toast, Toast.LENGTH_LONG);
                        CommonUtil.showToast(toast,getActivity());
                        toast.show();
                        showActivationPage();
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
            // progressBar.setVisibility(View.GONE);
            progressBar.dismiss();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if (isDeviceDateTimeValid()) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("mobileNo", mobileNo);
                    MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, getActivity());
                    try {
                        result = postJsonService.sendData("mobileNoConfirmation", jsonObject, true);
                    } catch (SocketTimeoutException e) {
                        errorMsg = getResources().getString(R.string.Some_error_accor_contact_admin);
                    } catch (SocketException e) {
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

        public void showActivationPage() {
            FragmentManager fragmentManager = getActivity().getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_place, new ActivationFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            /*Intent i = new Intent(getActivity(), ActivationFragment.class);
            startActivity(i);*/
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new ASync().execute();
                } else {
                    getActivity().finish();
                }
        }
    }
}
