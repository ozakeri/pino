package com.gap.bis_inspection.fragment.setting;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.activity.HomeActivity;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.CommonUtil;
import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.common.ShaPasswordEncoder;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfirmFragment extends Fragment {

    TextView usernameTextView;
    EditText passwordEditTExt;
    Button confirm;
    String userName;
    //Context context = getActivity();
    CoreService coreService;
    IDatabaseManager databaseManager;


    public ConfirmFragment() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_confirm, container, false);
        usernameTextView = (TextView) view.findViewById(R.id.username2_TV);
        passwordEditTExt = (EditText) view.findViewById(R.id.password2_TV);
        confirm = (Button) view.findViewById(R.id.btn_confirm);

        databaseManager = new DatabaseManager(getActivity());
        coreService = new CoreService(databaseManager);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String passwordEdit = passwordEditTExt.getText().toString();
                if (TextUtils.isEmpty(passwordEdit)) {
                    passwordEditTExt.setError(getResources().getString(R.string.label_reportStrTv_NotNull));
                } else {
                    passwordEditTExt = CommonUtil.farsiNumberReplacement(passwordEditTExt);
                    new ASync().execute();
                }
            }
        });

        AppController application = (AppController) Objects.requireNonNull(getActivity()).getApplication();
        usernameTextView.setText(" ( " + application.getCurrentUser().getUsername() + " ) " + application.getCurrentUser().getName() + " " + application.getCurrentUser().getFamily());
        userName = application.getCurrentUser().getUsername();
        usernameTextView.requestFocus();

        return view;
    }

    private class ASync extends AsyncTask<Void, Void, Void> {
        private String result;
        private String errorMsg;
        private String username;
        private String password;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            username = userName;
            password = passwordEditTExt.getText().toString();
            System.out.println("username=" + username + password);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
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
                            if (!resultJson.isNull(Constants.RESULT_KEY)) {
                                JSONObject jsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                                Map<String, String> userPermissionMap = coreService.getUserPermissionMap(user.getId());
                                Map<String, String> newUserPermissionMap = new HashMap<String, String>();
                                if (!jsonObject.isNull("userPermissionList")) {
                                    JSONArray permissionJsonArray = jsonObject.getJSONArray("userPermissionList");
                                    for (int i = 0; i < permissionJsonArray.length(); i++) {
                                        String permissionName = permissionJsonArray.getString(i);
                                        newUserPermissionMap.put(permissionName, permissionName);
/*
                                        if(!userPermissionMap.containsKey(permissionName)) {
                                            UserPermission userPermission = new UserPermission();
                                            userPermission.setUserId(user.getId());
                                            userPermission.setPermissionName(permissionName);
                                            databaseManager.insertPermission(userPermission);
                                        }
*/

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

                            user.setBisPassword(ShaPasswordEncoder.SHA1(password));
                            coreService.updateUser(user);
                            application.setPermissionMap(coreService.getUserPermissionMap(user.getId()));

                            showHomePage();
                        } else {
                            if (errorMsg == null) {
                                errorMsg = resultJson.getString(Constants.ERROR_KEY);
                            }
                            Toast toast = Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG);
                            CommonUtil.showToast(toast,getActivity());
                            toast.show();
                        }
                    } catch (JSONException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
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
                String encryptedPassword = ShaPasswordEncoder.SHA1(password);
                jsonObject.put("username", username);
                jsonObject.put("tokenPass", encryptedPassword);
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

            } catch (JSONException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
                Log.d("SettingActivity", e.getMessage());
            }
            return null;
        }

        public void showHomePage() {
            Intent i = new Intent(getActivity(), HomeActivity.class);
            startActivity(i);
        }

    }

}
