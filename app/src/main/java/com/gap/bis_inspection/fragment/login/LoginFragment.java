package com.gap.bis_inspection.fragment.login;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.activity.HomeActivity;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.CommonUtil;
import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.common.ShaPasswordEncoder;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.objectmodel.User;
import com.gap.bis_inspection.service.CoreService;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Objects;

public class LoginFragment extends Fragment {

    ProgressBar progressBar;
    TextView btnLogin;
    TextView txtForgotPass;
    EditText usernameEditText;
    EditText passwordEditText;
    private CoreService coreService;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        coreService = new CoreService(new DatabaseManager(getActivity()));

        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        btnLogin = view.findViewById(R.id.btn_login);
        txtForgotPass = (TextView) view.findViewById(R.id.txt_forgotPass);

        AppController application = (AppController) Objects.requireNonNull(getActivity()).getApplication();
        User user = application.getCurrentUser();

        usernameEditText = (EditText) view.findViewById(R.id.username);
        usernameEditText.setText(user.getUsername());
        usernameEditText.setEnabled(false);

        passwordEditText = (EditText) view.findViewById(R.id.password);
        passwordEditText.requestFocus();

      /*  passwordEditText.addTextChangedListener(new TextWatcher() {
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

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passwordEditText.getText().length() == 0) {
                    Toast toast = Toast.makeText(getActivity(), R.string.label_reportStrTv_NotNull, Toast.LENGTH_LONG);
                    CommonUtil.showToast(toast,getActivity());
                    toast.show();
                } else {
                    passwordEditText = CommonUtil.farsiNumberReplacement(passwordEditText);
                    doLogin();
                    passwordEditText.setText("");
                }
            }
        });

        txtForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRegistrationPage();
            }
        });

        return view;
    }

    public void showHomePage() {
        Intent i = new Intent(getActivity(), HomeActivity.class);
        startActivity(i);

    }

    public void showRegistrationPage() {
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_place, new RegistrationFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        SharedPreferences.Editor editor = AppController.getInstance().getSharedPreferences().edit();
        editor.putBoolean(Constants.FORGOT_PASSWORD, true);
        editor.apply();

            /*Intent i = new Intent(getActivity(), ActivationFragment.class);
            startActivity(i);*/
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void doLogin() {
        AppController application = (AppController) Objects.requireNonNull(getActivity()).getApplication();
        User user = application.getCurrentUser();
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        boolean loginPassed = false;
        if (user.getLastLoginDate() == null || user.getLastLoginDate().compareTo(new Date()) < 0) {
            try {
                String encPassword = ShaPasswordEncoder.SHA1(password);
                if (user.getUsername().equals(username) && user.getPassword().equals(encPassword)) {
                    loginPassed = true;
                    user.setLoginIs(Boolean.TRUE);
                    user.setLastLoginDate(new Date());
                    coreService.updateUser(user);
                    application.setPermissionMap(coreService.getUserPermissionMap(user.getId()));
                    DatabaseManager.SERVER_USER_ID = user.getServerUserId();
                }
            } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                Log.d("LoginFragment", e.getMessage());
            }
            if (loginPassed) {
                showHomePage();
            } else {
                Toast toast = Toast.makeText(getActivity(), R.string.label_login_toast, Toast.LENGTH_LONG);
                CommonUtil.showToast(toast,getActivity());
                toast.show();
            }
        } else {
            Toast toast = Toast.makeText(getActivity(), R.string.Invalid_Device_Date_Time, Toast.LENGTH_LONG);
            CommonUtil.showToast(toast,getActivity());
            toast.show();
        }
    }
}
