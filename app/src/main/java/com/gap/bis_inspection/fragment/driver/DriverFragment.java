package com.gap.bis_inspection.fragment.driver;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.common.CalendarUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Mohamad Cheraghi on 07/05/2016.
 */
public class DriverFragment extends Fragment {

    TextView nameTV, ageFVTV, licenceNoTV, mobileNoTV, companyTV, usageTypeEnTV, usageTypeEnTitleTV;
    ImageView imageUser;
    ProgressBar progressBar;
    Long driverId = null;
    String displayName = null;
    String addIcon1 = "driver";
    private String driverCode, name, family, licGroupEn, expireDateStr;
    String companyName, strCompanyType;
    int companyType;

    public DriverFragment() {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver, container, false);

        init(view);


        if (getArguments() != null) {
            String driverProfile = getArguments().getString("driverProfile");
            logLargeString(driverProfile);
            try {
                JSONObject driverProfileJsonObject = new JSONObject(driverProfile);


                if (!driverProfileJsonObject.isNull("id")) {
                    driverId = driverProfileJsonObject.getLong("id");
                }

                if (!driverProfileJsonObject.isNull("driverCode")) {
                    driverCode = driverProfileJsonObject.getString("driverCode");
                }

                if (!driverProfileJsonObject.isNull("company")) {
                    JSONObject companyJsonObject = driverProfileJsonObject.getJSONObject("company");
                    if (!companyJsonObject.isNull("nameFv")) {
                        companyName = companyJsonObject.getString("nameFv");
                    }

                    if (!companyJsonObject.isNull("companyType")) {
                        companyType = companyJsonObject.getInt("companyType");
                        if (companyType == 3) {
                            strCompanyType = "خصوصی";
                        } else {
                            strCompanyType = "دولتی";
                        }
                    }

                    companyTV.setText(strCompanyType + " - " + companyName);
                }

                if (!driverProfileJsonObject.isNull("person")) {
                    JSONObject personJsonObject = driverProfileJsonObject.getJSONObject("person");

                    if (!personJsonObject.isNull("name")) {
                        name = personJsonObject.getString("name");
                        displayName = personJsonObject.getString("name");
                    }

                    if (!personJsonObject.isNull("family")) {
                        family = personJsonObject.getString("family");
                        if (displayName == null) {
                            displayName = personJsonObject.getString("family");
                        } else {
                            displayName += " " + personJsonObject.getString("family");
                        }
                    }

                    nameTV.setText(name + " " + family + " " + (driverCode));

                    if (!personJsonObject.isNull("ageFV")) {
                        ageFVTV.setText(personJsonObject.getString("ageFV"));
                    } else {
                        ageFVTV.setText("---");
                    }

                    if (!personJsonObject.isNull("address")) {
                        JSONObject addressJsonObject = personJsonObject.getJSONObject("address");
                        if (!addressJsonObject.isNull("mobileNo")) {
                            mobileNoTV.setText(addressJsonObject.getString("mobileNo"));
                        }
                    } else {
                        mobileNoTV.setText("---");
                    }

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

                    if (!driverProfileJsonObject.isNull("drivingLicence")) {
                        JSONObject drivingLicenceJsonObject = driverProfileJsonObject.getJSONObject("drivingLicence");

                        /*if (!drivingLicenceJsonObject.isNull("licenceNo")) {
                            licenceNoTV.setText(drivingLicenceJsonObject.getString("licenceNo"));
                        } else {
                            licenceNoTV.setText("---");
                        }*/

                        if (!drivingLicenceJsonObject.isNull("licGroupEn")) {
                            int type = drivingLicenceJsonObject.getInt("licGroupEn");
                            switch (type) {
                                case 0:
                                    licGroupEn = getResources().getString(R.string.enumType_DrivingLicTypeEn_Grade1);
                                    break;
                                case 1:
                                    licGroupEn = getResources().getString(R.string.enumType_DrivingLicTypeEn_Grade2);
                                    break;
                                case 2:
                                    licGroupEn = getResources().getString(R.string.enumType_DrivingLicTypeEn_CivilCard);
                                    break;
                                case 3:
                                    licGroupEn = getResources().getString(R.string.enumType_DrivingLicTypeEn_TmpCertificate);
                                    break;
                                case 4:
                                    licGroupEn = getResources().getString(R.string.enumType_DrivingLicTypeEn_MotorDrivingLic);
                                    break;
                                case 5:
                                    licGroupEn = getResources().getString(R.string.enumType_DrivingLicTypeEn_Grade1_Special);
                                    break;
                                case 6:
                                    licGroupEn = getResources().getString(R.string.enumType_DrivingLicTypeEn_Grade3);
                                    break;
                            }
                        }

                        if (!drivingLicenceJsonObject.isNull("expireDate")) {
                            String strExpireDate = drivingLicenceJsonObject.getString("expireDate");
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            Date expireDate = simpleDateFormat.parse(strExpireDate);
                            Date currentDate = new Date(System.currentTimeMillis());
                            expireDateStr = CalendarUtil.datesDiff(getActivity(), currentDate, expireDate, "yMd");
                        }
                        licenceNoTV.setText(licGroupEn + " - " + getResources().getString(R.string.label_drivingLicence_expireDate) + " - " + expireDateStr);
                    }
                }

                String nameFv = "";
                if (!driverProfileJsonObject.isNull("driverJob")) {
                    JSONObject driverJobJsonObject = driverProfileJsonObject.getJSONObject("driverJob");
                    if (!driverJobJsonObject.isNull("car")) {
                        usageTypeEnTitleTV.setText("کاربری  ");
                        JSONObject carJsonObject = driverJobJsonObject.getJSONObject("car");
                        int carUsage = carJsonObject.getInt("usageTypeEn");
                        nameFv = carJsonObject.getString("nameFv");
                        // String plateText = carJsonObject.getString("plateText");

                   /*     String[] split = plateText.split("-");
                        String firstSubString = split[0];
                        String secondSubString = split[1];
                        System.out.println("firstSubString===" + firstSubString);
                        System.out.println("secondSubString===" + secondSubString);
                        String plate = secondSubString +" - "+ firstSubString;*/


                        switch (carUsage) {
                            case 0:
                                usageTypeEnTV.setText(getResources().getString(R.string.enumType_BusUsageType_Line) + " - " + nameFv);
                                break;
                            case 1:
                                usageTypeEnTV.setText(getResources().getString(R.string.enumType_BusUsageType_DoorToDoor) + " - " + nameFv);
                                break;
                            case 2:
                                usageTypeEnTV.setText(getResources().getString(R.string.enumType_BusUsageType_Services) + " - " + nameFv);
                                break;
                            case 3:
                                usageTypeEnTV.setText(getResources().getString(R.string.enumType_BusUsageType_SOS) + " - " + nameFv);
                                break;
                            case 4:
                                usageTypeEnTV.setText(getResources().getString(R.string.enumType_BusUsageType_BusFleetReserve) + " - " + nameFv);
                                break;
                        }

                    } else if (!driverJobJsonObject.isNull("driverJobTypeEn")) {
                        usageTypeEnTitleTV.setText("نوع شغل : ");

                        int driverJobTypeEn = driverJobJsonObject.getInt("driverJobTypeEn");
                        switch (driverJobTypeEn) {
                            case 0:
                                usageTypeEnTV.setText(getResources().getString(R.string.driver_driverJobTypeEn_DetermineCarForDriver));
                                break;

                            case 1:
                                usageTypeEnTV.setText(getResources().getString(R.string.driver_driverJobTypeEn_RotatoryDriverInLine));
                                break;

                            case 2:
                                usageTypeEnTV.setText(getResources().getString(R.string.driver_driverJobTypeEn_DriverInParking));
                                break;

                            case 3:
                                usageTypeEnTV.setText(getResources().getString(R.string.driver_driverJobTypeEn_RescuerSOS));
                                break;

                            case 4:
                                usageTypeEnTV.setText(getResources().getString(R.string.driver_driverJobTypeEn_AssistantRescuerSOS));
                                break;

                            case 5:
                                usageTypeEnTV.setText(getResources().getString(R.string.driver_driverJobTypeEn_WorkOnContract));
                                break;
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return view;
    }

    private void init(View view) {
        nameTV = (TextView) view.findViewById(R.id.name_TV);
        //familyTV = (TextView) view.findViewById(R.id.family_TV);
        //codeTV = (TextView) view.findViewById(R.id.code_TV);
        ageFVTV = (TextView) view.findViewById(R.id.ageFV_TV);
        licenceNoTV = (TextView) view.findViewById(R.id.licenceNo_TV);
        //licGroupEnTV = (TextView) view.findViewById(R.id.licGroupEn_TV);
        //remainingCreditTV = (TextView) view.findViewById(R.id.remainingCredit_TV);
        mobileNoTV = (TextView) view.findViewById(R.id.mobileNo_TV);
        companyTV = (TextView) view.findViewById(R.id.company_TV);
        usageTypeEnTV = (TextView) view.findViewById(R.id.usageTypeEn_TV);
        usageTypeEnTitleTV = (TextView) view.findViewById(R.id.usageTypeEnTitle_TV);
        imageUser = (ImageView) view.findViewById(R.id.image_User);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        //driverNameTV = (TextView) view.findViewById(R.id.driverName_TV);
    }

    public void logLargeString(String str) {
        String Tag = "DriverFragment=";
        if (str.length() > 3000) {
            Log.i(Tag, str.substring(0, 3000));
            logLargeString(str.substring(3000));
        } else {
            Log.i(Tag, str); // continuation
        }
    }


}
