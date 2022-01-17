package com.gap.bis_inspection.common;

import com.gap.bis_inspection.app.AppController;

/**
 * Created by root on 10/1/15.
 */
public class Constants {
    //public static final String DEFAULT_WEB_SERVICE_URL = "http://91.98.112.159:8082/rfServices/";
    //public static final String DEFAULT_WEB_SERVICE_URL = "http://192.168.1.100:8082/rfServices/";
    //public static final String DEFAULT_WEB_SERVICE_URL = "http://192.168.2.197:8080/rfServices/";
    //public static final String DEFAULT_WEB_SERVICE_URL = "http://192.168.3.119:8080/rfServices/";
    //public static final String DEFAULT_WEB_SERVICE_URL = "http://31.24.233.169/rfServices/";
    //public static final String DEFAULT_WEB_SERVICE_URL = "http://172.22.226.28/rfServices/";
    //public static final String DEFAULT_WEB_SERVICE_URL = "http://bis.isfahanptc.ir/rfServices/";

    //public static final String SITE = "http://192.168.2.72:8080";
   // public static final String SITE = "https://bis.isfahanptc.ir/";
   //public static final String SITE = "https://bis.tehran.ir";
   public static final String SITE = "http://192.168.2.180";
    public static final String WS = SITE + "/rfServices/";

    public static final String DOMAIN_WEB_SERVICE_URL = "DOMAIN_WEB_SERVICE_URL";
    public static final String DEFAULT_WEB_SERVICE_URL = "http://"
            + AppController.getInstance().getSharedPreferences().getString(Constants.DOMAIN_WEB_SERVICE_URL, null)
            + "/rfServices/";

    public static final String DEFAULT_OUT_PUT_DIR = "/BisInspection";
    public static final String DEFAULT_IMG_OUT_PUT_DIR = "/images";
    public static final String DEFAULT_USER_IMG_OUT_PUT_DIR = "/userImages";
    public static final String DEFAULT_APK_OUT_PUT_DIR = "/apkFile";

    public static final String SUCCESS_KEY = "SUCCESS";
    public static final String ERROR_KEY = "ERROR";
    public static final String HIGH_SECURITY_ERROR_KEY = "HIGH_SECURITY_ERROR";
    public static final String RESULT_KEY = "RESULT";

    public static final Integer ACTIVATION_CODE_VALIDATION_TIME_DURATION_MIN = 5;
    public static final Integer LOGIN_EXPIRE_VALIDATION_TIME_DURATION_DAY = 10;
    public static final String DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss";

    public static final String DEVICE_SETTING_WEB_SERVICE_URL_BASE = "WEB_SERVICE_URL_BASE";
    public static final String DEVICE_SETTING_KEY_LAST_CHANGE_DATE = "LAST_CHANGE_DATE";
    public static final String DEVICE_SETTING_KEY_LAST_TOTAL_SYNC_DATE = "LAST_TOTAL_SYNC_DATE";
    public static final String DEVICE_SETTING_KEY_LAST_USER_PERMISSION_SYNC_DATE = "LAST_USER_PERMISSION_SYNC_DATE";
    public static final String DEVICE_SETTING_KEY_LAST_APP_USER_SYNC_DATE = "LAST_APP_USER_SYNC_DATE";
    public static final String DEVICE_SETTING_KEY_LAST_CHAT_MESSAGE_SEND_SYNC_DATE = "KEY_LAST_CHAT_MESSAGE_SEND_SYNC_DATE";
    public static final String DEVICE_SETTING_KEY_LAST_ATTACH_FILE_RESUMING_CHAT_MESSAGE_SEND_SYNC_DATE = "LAST_ATTACH_FILE_RESUMING_CHAT_MESSAGE_SEND_SYNC_DATE";
    public static final String DEVICE_SETTING_KEY_LAST_CHAT_GROUP_SYNC_DATE = "LAST_CHAT_GROUP_SYNC_DATE";
    public static final String DEVICE_SETTING_KEY_LAST_CHAT_MESSAGE_RECEIVE_SYNC_DATE = "LAST_CHAT_MESSAGE_RECEIVE_SYNC_DATE";
    public static final String DEVICE_SETTING_KEY_LAST_COMPLAINT_REPORT_SYNC_DATE = "LAST_COMPLAINT_REPORT_SYNC_DATE";
    public static final String DEVICE_SETTING_KEY_LAST_GET_CHAT_MESSAGE_STATUS_LIST = "LAST_GET_CHAT_MESSAGE_STATUS_LIST";
    public static final String DEVICE_SETTING_KEY_LAST_SEND_CHAT_MESSAGE_READ_REPORT = "LAST_SEND_CHAT_MESSAGE_READ_REPORT";
    public static final String DEVICE_SETTING_KEY_LAST_GET_SURVEY_FORM_LIST = "LAST_GET_SURVEY_FORM_LIST";
    public static final String DEVICE_SETTING_KEY_LAST_GET_CHECK_FORM_LIST = "LAST_GET_CHECK_FORM_LIST";
    public static final String DEVICE_SETTING_KEY_LAST_SURVEY_FORM_SEND_SYNC_DATE = "KEY_LAST_CHAT_MESSAGE_SEND_SYNC_DATE";
    public static final String DEVICE_SETTING_KEY_LAST_ATTACH_FILE_SEND_SYNC_DATE = "KEY_LAST_ATTACH_FILE_SEND_SYNC_DATE";
    public static final String FORGOT_PASSWORD = "FORGOT_PASSWORD";
    public static final String ON_PROPERTY_CODE = "ON_PROPERTY_CODE";
    public static final String JSON_PICTURE_BYTE = "JSON_PICTURE_BYTE";
    public static final String DATE_SHOW_NOTIFICATION = "DATE_SHOW_NOTIFICATION";
    public static final String SAVE_LAST_POSITION = "SAVE_LAST_POSITION";
    public static final String FIRE_BASE_TOKEN = "FIRE_BASE_TOKEN";
    public static final String FIRE_BASE_DATA = "FIRE_BASE_DATA";
    public static final String JSON_DATA = "jsonData";
    public static final int TIMEOUT = 15000;
    public static final String PREF_TOKEN = "PREF_TOKEN";

    public static Long VALID_SERVER_AND_DEVICE_TIME_DIFF = (long) 300000;

    public static String DOCUMENT_USERNAME = "inspection";
    public static String DOCUMENT_PASSWORD = "inspect!gap@1395";

    public static String NOTIFICATION_PURPOSE = "newChatMessage";
}
