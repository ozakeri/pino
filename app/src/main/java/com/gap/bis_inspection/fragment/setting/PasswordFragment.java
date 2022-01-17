package com.gap.bis_inspection.fragment.setting;


import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.ShaPasswordEncoder;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.objectmodel.User;
import com.gap.bis_inspection.service.CoreService;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

/**
 * A simple {@link Fragment} subclass.
 */
public class PasswordFragment extends Fragment {

    SwitchCompat switch_Button;
    RelativeLayout changeLocalPassword, changeBisPassword;
    AppController appController;
    CoreService coreService;
    DatabaseManager databaseManager;

    public PasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_password, container, false);
        switch_Button = (SwitchCompat) view.findViewById(R.id.switch_Button);
        changeLocalPassword = (RelativeLayout) view.findViewById(R.id.changeLocalPassword1_TV);
        changeBisPassword = (RelativeLayout) view.findViewById(R.id.changeBisPassword1_TV);

        databaseManager = new DatabaseManager(getActivity());
        coreService = new CoreService(databaseManager);

        appController = (AppController) getActivity().getApplication();
        final User user = appController.getCurrentUser();
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
                    } else if (!switch_Button.isChecked()) {
                        user.setAutoLogin(false);
                    }
                }
                coreService.updateUser(user);
            }
        });

        changeLocalPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_setting_changepassword_layout);

                RelativeLayout local_layout = (RelativeLayout) dialog.findViewById(R.id.local_layout);
                RelativeLayout bis_layout = (RelativeLayout) dialog.findViewById(R.id.bis_layout);
                final EditText changeLocalPassword = (EditText) dialog.findViewById(R.id.changeLocalPassword_ET);
                Button localAction = (Button) dialog.findViewById(R.id.local_action);
                RelativeLayout close = (RelativeLayout) dialog.findViewById(R.id.close_Button);
                dialog.show();

                local_layout.setVisibility(View.VISIBLE);
                bis_layout.setVisibility(View.GONE);

                localAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String password = changeLocalPassword.getText().toString();
                        if (TextUtils.isEmpty(password)) {
                            changeLocalPassword.setError(getResources().getString(R.string.label_reportStrTv_NotNull));
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

        changeBisPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(getActivity());
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

        return view;
    }

}
