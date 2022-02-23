package com.gap.bis_inspection.fragment.login;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import com.gap.bis_inspection.util.FontCache;
import com.gap.bis_inspection.util.volly.VollyService;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Calendar;

public class ActivationFragment extends Fragment {

    EditText activationCodeEditText;
    private IDatabaseManager databaseManager;

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

        activationCodeEditText = (EditText) view.findViewById(R.id.activationCode);
        AppCompatTextView btnConfirm = view.findViewById(R.id.btn_confirm);
        databaseManager = new DatabaseManager(getActivity());

        Typeface customFont = FontCache.getTypeface("IRANSansMobile(FaNum)_Bold.ttf", getActivity());
        activationCodeEditText.setTypeface(customFont);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String activationCode = CommonUtil.farsiNumberReplacement(activationCodeEditText.getText().toString());

                if (CommonUtil.isConnect(getActivity())) {
                    AppController application = (AppController) getActivity().getApplication();
                    VollyService.getInstance().activationCodeValidation(application.getCurrentUser().getMobileNo(), activationCode, new Response.Listener() {
                        @Override
                        public void onResponse(Object result) {
                            if (result != null) {
                                try {
                                    JSONObject resultJson = new JSONObject(result.toString());
                                    if (!resultJson.isNull(Constants.SUCCESS_KEY)) {
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

                                        }
                                    }
                                } catch (JSONException e) {

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
            }
        });

        return view;
    }

    public void showPasswordCreationPage() {
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_place, new PasswordCreationFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
