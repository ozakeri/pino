package com.gap.bis_inspection.fragment.login;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatTextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
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
import com.gap.bis_inspection.service.CoreService;
import com.gap.bis_inspection.util.volly.VollyService;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PasswordCreationFragment extends Fragment {

    EditText usernameET;
    EditText passwordEditText;
    EditText confirmPasswordEditText;
    private IDatabaseManager databaseManager;
    private CoreService coreService;
    private String password, username = null;
    private AppController application;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_password_creatin, container, false);

        usernameET = view.findViewById(R.id.username);
        passwordEditText = view.findViewById(R.id.password);
        confirmPasswordEditText = view.findViewById(R.id.confirmPassword);
        application = (AppController) getActivity().getApplication();
        usernameET.setEnabled(false);
        usernameET.setText(application.getCurrentUser().getUsername());
        AppCompatTextView btnConfirm = view.findViewById(R.id.btn_confirm);
        databaseManager = new DatabaseManager(getActivity());
        coreService = new CoreService(databaseManager);

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
                        CommonUtil.showToast(toast, getActivity());
                        toast.show();
                    }
                } else {
                    if (usernameET.getText() != null && !usernameET.getText().toString().isEmpty() &&
                            passwordEditText.getText() != null && !passwordEditText.getText().toString().isEmpty() &&
                            confirmPasswordEditText.getText() != null && !confirmPasswordEditText.getText().toString().isEmpty()) {
                        if (passwordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString())) {
                            passwordEditText = CommonUtil.farsiNumberReplacement(passwordEditText);
                            confirmPasswordEditText = CommonUtil.farsiNumberReplacement(confirmPasswordEditText);


                            if (CommonUtil.isConnect(getActivity())) {
                                username = usernameET.getText().toString();
                                VollyService.getInstance().getUserPermissionList(username, application.getCurrentUser().getBisPassword()
                                        , new Response.Listener() {
                                            @Override
                                            public void onResponse(Object result) {
                                                if (result != null) {
                                                    try {
                                                        JSONObject resultJson = new JSONObject(result.toString());
                                                        if (!resultJson.isNull(Constants.SUCCESS_KEY)) {
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

                                                            user.setPassword(ShaPasswordEncoder.SHA1(password));
                                                            user.setLoginStatus(LoginStatusEn.Registered.ordinal());
                                                            user.setLoginIs(Boolean.TRUE);
                                                            user.setLastLoginDate(new Date());
                                                            user.setAutoLogin(Boolean.FALSE);
                                                            user.setLoginIs(Boolean.FALSE);
                                                            databaseManager.updateUser(user);
                                                            application.setPermissionMap(coreService.getUserPermissionMap(user.getId()));
                                                            Snackbar.make(view, R.string.success_login_Toast, Snackbar.LENGTH_SHORT)
                                                                    .show();
                                                            showHomePage();
                                                        }
                                                    } catch (Exception e) {

                                                    }
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError volleyError) {
                                                Snackbar.make(view, volleyError.toString(), Snackbar.LENGTH_SHORT)
                                                        .show();
                                            }
                                        });
                            }


                        } else {
                            Toast toast = Toast.makeText(getActivity(), R.string.label_reportStrTv_NotNull, Toast.LENGTH_LONG);
                            CommonUtil.showToast(toast, getActivity());
                            toast.show();
                        }
                    } else {
                        Toast toast = Toast.makeText(getActivity(), R.string.label_reportStrTv_NotNull, Toast.LENGTH_LONG);
                        CommonUtil.showToast(toast, getActivity());
                        toast.show();
                    }
                }
            }
        });

        return view;
    }

    public void showHomePage() {
        Intent i = new Intent(getActivity(), HomeActivity.class);
        i.putExtra("isCreatePass", true);
        startActivity(i);
    }
}
