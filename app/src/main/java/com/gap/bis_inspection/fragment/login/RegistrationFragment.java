package com.gap.bis_inspection.fragment.login;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gap.bis_inspection.R;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.CommonUtil;
import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.db.enumtype.LoginStatusEn;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.manager.IDatabaseManager;
import com.gap.bis_inspection.db.objectmodel.User;
import com.gap.bis_inspection.service.CoreService;
import com.gap.bis_inspection.util.FontCache;
import com.gap.bis_inspection.util.volly.VollyService;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.Objects;

public class RegistrationFragment extends Fragment {

    EditText mobileNoEditText;
    private IDatabaseManager databaseManager;
    private static final int MY_PERMISSIONS_REQUEST = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration, container, false);

        mobileNoEditText = view.findViewById(R.id.mobileNo);
        Typeface customFont = FontCache.getTypeface("IRANSansMobile(FaNum)_Bold.ttf", getActivity());
        mobileNoEditText.setTypeface(customFont);
        TextView btnConfirm = view.findViewById(R.id.btn_confirm);
        mobileNoEditText.setFocusableInTouchMode(true);

        databaseManager = new DatabaseManager(getActivity());

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobileNo = CommonUtil.farsiNumberReplacement(mobileNoEditText.getText().toString());
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    if (CommonUtil.isConnect(getActivity())) {
                        VollyService.getInstance().mobileNoConfirmation(mobileNoEditText.getText().toString(), new Response.Listener<Object>() {
                            @Override
                            public void onResponse(Object s) {
                                User user = databaseManager.getUserByMobileNo(mobileNo);
                                if (user == null) {
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

                                Snackbar.make(view, R.string.send_code_activation_Toast, Snackbar.LENGTH_SHORT)
                                        .show();

                                showActivationPage();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                Snackbar.make(view, volleyError.toString(), Snackbar.LENGTH_SHORT)
                                        .show();
                            }
                        });
                    } else {
                        Snackbar.make(view, R.string.label_check_network, Snackbar.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST);
                    }
                }

            }
        });


        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    getActivity().finish();
                }
        }
    }

    public void showActivationPage() {
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_place, new ActivationFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
