package com.gap.bis_inspection.activity.car;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.CommonUtil;
import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.common.HejriUtil;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.manager.IDatabaseManager;
import com.gap.bis_inspection.exception.WebServiceException;
import com.gap.bis_inspection.webservice.GetJsonService;
import com.gap.bis_inspection.webservice.MyPostJsonService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CarDriverDetailActivity extends AppCompatActivity {
    private String result;
    private String jsonData;
    ImageView backIcon, imageUser;
    LinearLayout shiftLayout;
    TextView typeTV, typeEnTV, lineNameTV, providerCompanyTV,
            startDateTV, finishDateTV, shiftTypeEnTV, driverTV, mobileNoTV;
    private IDatabaseManager databaseManager;
    String driverCode;
    ASync myTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_car_driver_detail);
        Bundle bundle = getIntent().getExtras();
        jsonData = bundle.getString("driverJobList");

        init();

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        new ResultTask().execute();
    }

    private void init() {
        typeTV = (TextView) findViewById(R.id.type_TV);
        typeEnTV = (TextView) findViewById(R.id.typeEn_TV);
        lineNameTV = (TextView) findViewById(R.id.txt_lineName);
        providerCompanyTV = (TextView) findViewById(R.id.providerCompany_TV);
        startDateTV = (TextView) findViewById(R.id.startDate_TV);
        finishDateTV = (TextView) findViewById(R.id.finishDate_TV);
        shiftTypeEnTV = (TextView) findViewById(R.id.shiftTypeEn_TV);
        driverTV = (TextView) findViewById(R.id.driver_TV);
        mobileNoTV = (TextView) findViewById(R.id.mobileNumber_TV);
        backIcon = (ImageView) findViewById(R.id.backIcon);
        shiftLayout = (LinearLayout) findViewById(R.id.shift_Layout);
        imageUser = (ImageView) findViewById(R.id.image_User);
        databaseManager = new DatabaseManager(this);
    }


    ////******get driver profile*******////
    private class ResultTask extends AsyncTask<Void, Void, Void> {

        ResultTask() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {

            GetJsonService getJson = new GetJsonService();
            result = getJson.JsonReguest("");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // progress.setVisibility(View.INVISIBLE);

            result = jsonData;
            if (result != null) {
                try {
                    JSONObject resultJson = new JSONObject(result);

                    if (!resultJson.isNull("driver")) {
                        String nameFv = "";
                        JSONObject driverJsonObject = resultJson.getJSONObject("driver");
                        driverCode = driverJsonObject.getString("driverCode");
                        if (!driverJsonObject.isNull("person")) {
                            JSONObject personJsonObject = driverJsonObject.getJSONObject("person");
                            nameFv = personJsonObject.getString("nameFv");
                        }
                        driverTV.setText(nameFv + " - " + driverCode);
                    } else {
                        driverTV.setText("---");
                    }

                    if (!resultJson.isNull("type")) {
                        int driverType = resultJson.getInt("type");
                        switch (driverType) {
                            case 0:
                                typeTV.setText(R.string.enumType_DriverType_MainDriver);
                                break;
                            case 1:
                                typeTV.setText(R.string.enumType_DriverType_AssistantDriver);
                                break;
                            case 2:
                                typeTV.setText(R.string.enumType_DriverType_OrganizationDriver);
                                break;
                        }
                    } else {
                        typeTV.setText("---");
                    }

                    if (!resultJson.isNull("driverJobTypeEn")) {
                        int type = resultJson.getInt("driverJobTypeEn");
                        switch (type) {
                            case 0:
                                typeEnTV.setText(R.string.enumType_DriverJobTypeEn_DetermineCarForDriver);
                                break;
                            case 1:
                                typeEnTV.setText(R.string.enumType_DriverJobTypeEn_RotatoryDriverInLine);
                                break;
                            case 2:
                                typeEnTV.setText(R.string.enumType_DriverJobTypeEn_DriverInParking);
                                break;
                            case 3:
                                typeEnTV.setText(R.string.enumType_DriverJobTypeEn_RescuerSOS);
                                break;
                            case 4:
                                typeEnTV.setText(R.string.enumType_DriverJobTypeEn_AssistantRescuerSOS);
                                break;
                            case 5:
                                typeEnTV.setText(R.string.enumType_DriverJobTypeEn_WorkOnContract);
                                break;
                        }

                    } else {
                        typeEnTV.setText("---");
                    }

                    if (!resultJson.isNull("driver")) {
                        JSONObject driverJsonObject = resultJson.getJSONObject("driver");
                        if (!driverJsonObject.isNull("person")) {
                            JSONObject personJsonObject = driverJsonObject.getJSONObject("person");
                            if (!personJsonObject.isNull("address")) {
                                JSONObject addressJsonObject = personJsonObject.getJSONObject("address");
                                if (!addressJsonObject.isNull("mobileNo")) {
                                    mobileNoTV.setText(addressJsonObject.getString("mobileNo"));
                                }
                            }
                        }
                    } else {
                        mobileNoTV.setText("---");
                    }

                    if (!resultJson.isNull("lineCompany")) {
                        JSONObject lineCompanyJsonObject = resultJson.getJSONObject("lineCompany");
                        if (!lineCompanyJsonObject.isNull("line")) {
                            JSONObject lineJsonObject = lineCompanyJsonObject.getJSONObject("line");
                            lineNameTV.setText(lineJsonObject.getString("nameFv"));
                        }
                    } else {
                        lineNameTV.setText("---");
                    }

                    if (!resultJson.isNull("providerCompany")) {
                        JSONObject providerCompanyJsonObject = resultJson.getJSONObject("providerCompany");
                        providerCompanyTV.setText(providerCompanyJsonObject.getString("name"));
                    } else {
                        providerCompanyTV.setText("---");
                    }

                    if (!resultJson.isNull("startDate")) {
                        String startDate = resultJson.getString("startDate");
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date startDate1 = simpleDateFormat.parse(startDate);
                        String hejriStartDate = HejriUtil.chrisToHejri(startDate1);
                        startDateTV.setText(hejriStartDate);
                    } else {
                        startDateTV.setText("---");
                    }

                    if (!resultJson.isNull("finishDate")) {
                        String finishDate = resultJson.getString("finishDate");
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date startDate = simpleDateFormat.parse(finishDate);
                        String hejriFinishDate = HejriUtil.chrisToHejri(startDate);
                        finishDateTV.setText(hejriFinishDate);
                    } else {
                        finishDateTV.setText("---");
                    }

                    myTask = new ASync();
                    myTask.execute();

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private class ASync extends AsyncTask<Void, Void, Void> {
        private String result;
        private String errorMsg;

        @SuppressLint("StringFormatInvalid")
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (result != null) {
                logLargeString(result);
                try {
                    JSONObject resultJson = new JSONObject(result);
                    if (errorMsg == null && !resultJson.isNull(Constants.SUCCESS_KEY)) {
                        if (!resultJson.isNull(Constants.RESULT_KEY)) {
                            JSONObject jsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                            if (!jsonObject.isNull("driverProfile")) {

                                JSONObject driverProfileJsonObject = jsonObject.getJSONObject("driverProfile");
                                JSONObject personJsonObject = driverProfileJsonObject.getJSONObject("person");
                                if (!personJsonObject.isNull("pictureBytes")) {
                                    JSONArray pictureBytesJsonArray = personJsonObject.getJSONArray("pictureBytes");
                                    byte[] bytes = new byte[pictureBytesJsonArray.length()];
                                    for (int i = 0; i < pictureBytesJsonArray.length(); i++) {
                                        bytes[i] = Integer.valueOf(pictureBytesJsonArray.getInt(i)).byteValue();
                                    }
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    imageUser.setImageBitmap(bitmap);
                                } else {
                                    imageUser.setBackgroundResource(R.drawable.driver_image_null);
                                }

                            }
                        }
                    } else {
                        if (errorMsg == null) {
                            errorMsg = resultJson.getString(Constants.ERROR_KEY);
                        }
                        Toast toast = Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG);
                        CommonUtil.showToast(toast,CarDriverDetailActivity.this);
                        toast.show();
                    }
                } catch (JSONException e) {
                    Log.d("RegistrationFragment", e.getMessage());
                    Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.Some_error_accor_contact_admin), Toast.LENGTH_LONG);
                    CommonUtil.showToast(toast,CarDriverDetailActivity.this);
                    toast.show();
                }
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), (errorMsg != null) ? errorMsg : getResources().getString(R.string.Some_error_accor_contact_admin), Toast.LENGTH_LONG);
                CommonUtil.showToast(toast,CarDriverDetailActivity.this);
                toast.show();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                JSONObject jsonObject = new JSONObject();
                AppController application = (AppController) getApplication();
                jsonObject.put("username", application.getCurrentUser().getUsername());
                jsonObject.put("tokenPass", application.getCurrentUser().getBisPassword());
                jsonObject.put("driverCode", driverCode);
                System.out.println("driverCode===" + driverCode);
                MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, getApplicationContext());
                try {
                    result = postJsonService.sendData("getDriverProfile", jsonObject, true);
                } catch (SocketTimeoutException e) {
                    errorMsg = getResources().getString(R.string.Some_error_accor_contact_admin);
                } catch (SocketException e) {
                    errorMsg = getResources().getString(R.string.Some_error_accor_contact_admin);
                } catch (WebServiceException e) {
                    Log.d("RegistrationFragment", e.getMessage());
                }

            } catch (JSONException e) {
                Log.d("RegistrationFragment", e.getMessage());
                return null;
            }
            return null;
        }
    }

    @SuppressLint("LongLogTag")
    public void logLargeString(String str) {
        String Tag = "CarDriverDetailActivity=";
        if (str.length() > 3000) {
            Log.i(Tag, str.substring(0, 3000));
            logLargeString(str.substring(3000));
        } else {
            Log.i(Tag, str); // continuation
        }
    }
}
