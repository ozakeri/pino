package com.gap.bis_inspection.fragment.login;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatTextView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.activity.HomeActivity;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.CommonUtil;
import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.common.ShaPasswordEncoder;
import com.gap.bis_inspection.db.enumtype.LoginStatusEn;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.manager.IDatabaseManager;
import com.gap.bis_inspection.db.objectmodel.User;
import com.gap.bis_inspection.db.objectmodel.UserPermission;
import com.gap.bis_inspection.exception.WebServiceException;
import com.gap.bis_inspection.service.CoreService;
import com.gap.bis_inspection.webservice.MyPostJsonService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PasswordCreationFragment extends Fragment {

    ProgressDialog progressBar;
    EditText usernameET;
    //EditText bisPasswordET;
    EditText passwordEditText;
    EditText confirmPasswordEditText;
    private IDatabaseManager databaseManager;
    private CoreService coreService;
    private String password;
    private AppController application;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_password_creatin, container, false);

        //progressBar = (ProgressBar) view.findViewById(R.id.progress);

        usernameET = (EditText) view.findViewById(R.id.username);
        //bisPasswordET = (EditText) view.findViewById(R.id.bisPassword);
        passwordEditText = (EditText) view.findViewById(R.id.password);
        confirmPasswordEditText = (EditText) view.findViewById(R.id.confirmPassword);

        application = (AppController) getActivity().getApplication();
        usernameET.setEnabled(false);
        usernameET.setText(application.getCurrentUser().getUsername());
        //bisPasswordET.requestFocus();
        AppCompatTextView btnConfirm = view.findViewById(R.id.btn_confirm);
        databaseManager = new DatabaseManager(getActivity());
        coreService = new CoreService(databaseManager);

//        if (AppController.getInstance().getSharedPreferences().getBoolean(Constants.FORGOT_PASSWORD, false)) {
//            bisPasswordET.setVisibility(View.GONE);
//        }

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (AppController.getInstance().getSharedPreferences().getBoolean(Constants.FORGOT_PASSWORD, false)) {
                    if (passwordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString())) {
                        passwordEditText = CommonUtil.farsiNumberReplacement(passwordEditText);
                        confirmPasswordEditText = CommonUtil.farsiNumberReplacement(confirmPasswordEditText);
                        password = passwordEditText.getText().toString();
                        User user = application.getCurrentUser();
                        user.setLoginIs(Boolean.TRUE);
                        user.setLoginStatus(LoginStatusEn.Registered.ordinal());
                        try {
                            user.setPassword(ShaPasswordEncoder.SHA1(password));
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        databaseManager.updateUser(user);
                        application.setPermissionMap(coreService.getUserPermissionMap(user.getId()));
                        showHomePage();
                    } else {
                        Toast toast = Toast.makeText(getActivity(), R.string.label_reportStrTv_NotNull, Toast.LENGTH_LONG);
                        CommonUtil.showToast(toast,getActivity());
                        toast.show();
                    }
                } else {
                    if (usernameET.getText() != null && !usernameET.getText().toString().isEmpty() &&
                            passwordEditText.getText() != null && !passwordEditText.getText().toString().isEmpty() &&
                            confirmPasswordEditText.getText() != null && !confirmPasswordEditText.getText().toString().isEmpty()) {
                        if (passwordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString())) {
                            passwordEditText = CommonUtil.farsiNumberReplacement(passwordEditText);
                            confirmPasswordEditText = CommonUtil.farsiNumberReplacement(confirmPasswordEditText);
                            new ASync().execute();
                        } else {
                            Toast toast = Toast.makeText(getActivity(), R.string.label_reportStrTv_NotNull, Toast.LENGTH_LONG);
                            CommonUtil.showToast(toast,getActivity());
                            toast.show();
                        }
                    } else {
                        Toast toast = Toast.makeText(getActivity(), R.string.label_reportStrTv_NotNull, Toast.LENGTH_LONG);
                        CommonUtil.showToast(toast,getActivity());
                        toast.show();
                    }
                }
            }
        });

        /*bisPasswordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (bisPasswordET.getText().toString().length() == 0) {
                    bisPasswordET.setGravity(Gravity.RIGHT);
                } else {
                    bisPasswordET.setGravity(Gravity.NO_GRAVITY);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/

       /* passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (passwordEditText.getText().toString().length() == 0) {
                    passwordEditText.setGravity(Gravity.RIGHT);
                } else {
                    passwordEditText.setGravity(Gravity.NO_GRAVITY);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/

     /*   confirmPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (confirmPasswordEditText.getText().toString().length() == 0) {
                    confirmPasswordEditText.setGravity(Gravity.RIGHT);
                } else {
                    confirmPasswordEditText.setGravity(Gravity.NO_GRAVITY);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/

        return view;
    }

    /*public void createPassword() {
        try {
            String password = searchKey;
            String confirmPassword = searchKey1;
            if (password.equals(confirmPassword)) {
                AppController application = (AppController) Objects.requireNonNull(getActivity()).getApplication();
                User user = application.getCurrentUser();
                user.setLoginStatus(LoginStatusEn.Registered.ordinal());
                user.setPassword(ShaPasswordEncoder.SHA1(password));
                user.setLastLoginDate(new Date());
                databaseManager.updateUser(user);

                showHomePage();
            } else {
                Toast.makeText(getActivity(), "The passwords are not matched", Toast.LENGTH_LONG).show();
            }
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            Log.d("PwdCreationActivity", e.getMessage());
            Toast.makeText(getActivity(), getResources().getString(R.string.Some_error_accor_contact_admin), Toast.LENGTH_LONG).show();
        }
    }*/

    public void showHomePage() {
        Intent i = new Intent(getActivity(), HomeActivity.class);
        i.putExtra("isCreatePass", true);
        startActivity(i);
    }

    private class ASync extends AsyncTask<Void, Void, Void> {
        private String result;
        private String errorMsg;
        private String username;
        //private String bisPassword;


        @SuppressLint("StringFormatInvalid")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            username = usernameET.getText().toString();
            //bisPassword = bisPasswordET.getText().toString();
            password = passwordEditText.getText().toString();
            //progressBar.setVisibility(View.VISIBLE);
            progressBar = ProgressDialog.show(getActivity(), null, getResources().getString(R.string.label_progress_dialog, true), true);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //progressBar.setVisibility(View.INVISIBLE);
            progressBar.dismiss();
            System.out.println("====result=" + result);
            AppController application = (AppController) Objects.requireNonNull(getActivity()).getApplication();
            if (errorMsg == null) {
                if (result != null) {
                    try {
                        JSONObject resultJson = new JSONObject(result);
                        if (!resultJson.isNull(Constants.HIGH_SECURITY_ERROR_KEY)) {
                            //// TODO: remove all database data and local file and exit application
                        } else if (errorMsg == null && !resultJson.isNull(Constants.SUCCESS_KEY)) {
                            //// TODO:
                            User user = application.getCurrentUser();
                            DatabaseManager.SERVER_USER_ID = user.getServerUserId();
                            if (!resultJson.isNull(Constants.RESULT_KEY)) {
                                JSONObject jsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                                Map<String, String> userPermissionMap = coreService.getUserPermissionMap(user.getId());
                                Map<String, String> newUserPermissionMap = new HashMap<String, String>();
                                if (!jsonObject.isNull("userPermissionList")) {
                                    JSONArray permissionJsonArray = jsonObject.getJSONArray("userPermissionList");
                                    for (int i = 0; i < permissionJsonArray.length(); i++) {
                                        String permissionName = permissionJsonArray.getString(i);
                                        newUserPermissionMap.put(permissionName, permissionName);
                                    }
                                }
                                for (String permissionName : newUserPermissionMap.keySet()) {
                                    if (!userPermissionMap.containsKey(permissionName)) {
                                        UserPermission userPermission = new UserPermission();
                                        userPermission.setUserId(user.getId());
                                        userPermission.setPermissionName(permissionName);
                                        databaseManager.insertPermission(userPermission);
                                    }
                                }
                                for (String permissionName : userPermissionMap.keySet()) {
                                    if (!newUserPermissionMap.containsKey(permissionName)) {
                                        UserPermission userPermission = new UserPermission();
                                        userPermission.setUserId(user.getId());
                                        userPermission.setPermissionName(permissionName);
                                        databaseManager.deleteUserPermission(user.getId(), permissionName);
                                    }
                                }
                            }

                            //user.setBisPassword(ShaPasswordEncoder.SHA1(bisPassword));
                            user.setPassword(ShaPasswordEncoder.SHA1(password));
                            user.setLoginStatus(LoginStatusEn.Registered.ordinal());
                            user.setLoginIs(Boolean.TRUE);
                            user.setLastLoginDate(new Date());
                            user.setAutoLogin(Boolean.FALSE);
                            user.setLoginIs(Boolean.FALSE);
                            databaseManager.updateUser(user);
                            application.setPermissionMap(coreService.getUserPermissionMap(user.getId()));
                            Toast toast = Toast.makeText(getActivity(), R.string.success_login_Toast, Toast.LENGTH_LONG);
                            CommonUtil.showToast(toast,getActivity());
                            toast.show();
                            showHomePage();

                        } else {
                            if (errorMsg == null) {
                                errorMsg = resultJson.getString(Constants.ERROR_KEY);
                            }
                            Toast toast = Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG);
                            CommonUtil.showToast(toast,getActivity());
                            toast.show();
                        }
                    } catch (JSONException | UnsupportedEncodingException | NoSuchAlgorithmException e) {
                        Log.d("SettingActivity", e.getMessage());
                    }
                }
            } else {
                Toast toast = Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG);
                CommonUtil.showToast(toast,getActivity());
                toast.show();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                JSONObject jsonObject = new JSONObject();
                //String encryptedPassword = ShaPasswordEncoder.SHA1(bisPassword);
                jsonObject.put("username", username);
                jsonObject.put("tokenPass", application.getCurrentUser().getBisPassword());
                System.out.println("username==" + username);
                System.out.println("tokenPass==" + application.getCurrentUser().getBisPassword());
                MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, getActivity());
                try {
                    result = postJsonService.sendData("getUserPermissionList", jsonObject, true);
                } catch (SocketTimeoutException | SocketException e) {
                    errorMsg = getResources().getString(R.string.Some_error_accor_contact_admin);
                } catch (WebServiceException e) {
                    Log.d("SettingActivity", e.getMessage());
                }
                /*result = "{\"SUCCESS\":\"success\",\n" +
                        "\"RESULT\":{\"userPermissionList\":[\"ROLE_APP_GET_USER_PERMISSION_LIST\",\"ROLE_APP_INSPECTION_LINE_VIEW_LIST\",\"ROLE_APP_INSPECTION_ENTITY_FORM_VIEW_LIST\",\"ROLE_APP_INSPECTION_CAR_VIEW_LIST\",\"ROLE_APP_INSPECTION_DRIVER_VIEW_LIST\"]}}\n";
                result = "{\"SUCCESS\":\"success\",\n" +
                        "\"RESULT\":{\"userPermissionList\":[\"ROLE_APP_GET_USER_PERMISSION_LIST\"]}}\n";
*/

            } catch (JSONException e) {
                Log.d("SettingActivity", e.getMessage());
            }
            return null;
        }

    }

}
