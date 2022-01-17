package com.gap.bis_inspection.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;

import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.util.volly.DeviceBean;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.net.NetworkInterface;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Util {

    public static class WSParameter {
        public String key;
        public Object value;

        public WSParameter(String key, Object value) {
            this.key = key;
            this.value = value;
        }
    }

    public static String createJson(ArrayList<WSParameter> wsParameters) {

        RequestBaseBean requestBaseBean = new RequestBaseBean();
        //System.out.println("getToken====" + requestBaseBean.getToken());
        JsonElement jsonElement = new Gson().toJsonTree(requestBaseBean);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        for (WSParameter wsParameter : wsParameters)
            jsonObject.addProperty(wsParameter.key, wsParameter.value + "");
        String json = jsonObject.toString();
        System.out.println("json======" +json);
        return json;
    }


    public static String getToken (){
        return AppController.getInstance().getSharedPreferences().getString(Constants.FIRE_BASE_TOKEN, "");
    }

    public static DeviceBean getDevice() {

        return new DeviceBean(getImei(AppController.getInstance().getApplicationContext()),
                getDeviceName(),
                "Android",
                getOSVersion());
    }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    @SuppressLint("HardwareIds")
    public static String getImei(Context context) {
        String imei = null;
        if (context != null) {


            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                    imei = android.provider.Settings.Secure.getString(
                            context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
                    System.out.println("imei===111===" + imei);

                } else {
                    TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    imei = telephonyManager.getDeviceId();
                    System.out.println("imei===222===" + imei);
                }
            }
        }
        return imei;
    }

    public static String getOSVersion() {
        String release = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
        return release + " (API: " + sdkVersion + ")";
    }


    private static final ThreadLocal<DateFormat> dateFormatThreadLocal = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMdd");
        }
    };

    public static int compareDates(Date date1, Date date2) {
        DateFormat dateFormat = dateFormatThreadLocal.get();
        System.out.println("compareDates=" + dateFormat.format(date1).compareTo(dateFormat.format(date2)));
        return dateFormat.format(date1).compareTo(dateFormat.format(date2));
    }

}
