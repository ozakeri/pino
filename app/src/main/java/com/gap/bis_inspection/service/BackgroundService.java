package com.gap.bis_inspection.service;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import com.gap.bis_inspection.BuildConfig;
import com.gap.bis_inspection.R;
import com.gap.bis_inspection.activity.MainActivity;
import com.gap.bis_inspection.activity.NotificationReceiverActivity;
import com.gap.bis_inspection.activity.SettingActivity;
import com.gap.bis_inspection.activity.message.ChatGroupListActivity;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.common.ImageUtil;
import com.gap.bis_inspection.db.enumtype.EntityNameEn;
import com.gap.bis_inspection.db.enumtype.GeneralStatus;
import com.gap.bis_inspection.db.enumtype.LoginStatusEn;
import com.gap.bis_inspection.db.enumtype.SendingStatusEn;
import com.gap.bis_inspection.db.enumtype.SurveyFormStatusEn;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.objectmodel.AppUser;
import com.gap.bis_inspection.db.objectmodel.AttachFile;
import com.gap.bis_inspection.db.objectmodel.ChatGroup;
import com.gap.bis_inspection.db.objectmodel.ChatGroupMember;
import com.gap.bis_inspection.db.objectmodel.ChatMessage;
import com.gap.bis_inspection.db.objectmodel.ComplaintReport;
import com.gap.bis_inspection.db.objectmodel.DeviceSetting;
import com.gap.bis_inspection.db.objectmodel.Form;
import com.gap.bis_inspection.db.objectmodel.FormAnswer;
import com.gap.bis_inspection.db.objectmodel.FormItemAnswer;
import com.gap.bis_inspection.db.objectmodel.FormQuestion;
import com.gap.bis_inspection.db.objectmodel.FormQuestionGroup;
import com.gap.bis_inspection.db.objectmodel.FormQuestionGroupForm;
import com.gap.bis_inspection.db.objectmodel.FormTemp;
import com.gap.bis_inspection.db.objectmodel.SurveyForm;
import com.gap.bis_inspection.db.objectmodel.SurveyFormQuestion;
import com.gap.bis_inspection.db.objectmodel.SurveyFormQuestionTemp;
import com.gap.bis_inspection.db.objectmodel.User;
import com.gap.bis_inspection.db.objectmodel.UserPermission;
import com.gap.bis_inspection.exception.WebServiceException;
import com.gap.bis_inspection.util.DateUtils;
import com.gap.bis_inspection.webservice.MyPostJsonService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class BackgroundService extends JobIntentService {
    public static Integer notificationId = 1;

    Context context;
    CoreService coreService;
    DatabaseManager databaseManager;
    AppController application;
    public static Integer MAX_ATTACH_FILE_PACKET_SIZE = 8192;
    public static boolean SEND_MESSAGE_IN_PROGRESS = false;
    private static int count = 0;
    public static final int JOB_ID = 1;
    private Intent intent;
    //public static Integer MAX_ATTACH_FILE_PACKET_SIZE = 131072;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, BackgroundService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        context = getApplicationContext();
        databaseManager = new DatabaseManager(context);
        coreService = new CoreService(databaseManager);
        application = (AppController) context.getApplicationContext();
        if (application.getCurrentUser() == null) {
            List<User> userList = databaseManager.listUsers();
            User user = null;
            if (!userList.isEmpty()) {
                user = userList.get(0);
                if (user.getLoginStatus().equals(LoginStatusEn.Registered.ordinal())) {
                    application.setCurrentUser(user);
                }
            }
        }

        //***************************************************************
        if (application.getCurrentUser() != null && application.getCurrentUser().getServerUserId() != null && application.getCurrentUser().getBisPassword() != null) {
            DatabaseManager.SERVER_USER_ID = application.getCurrentUser().getServerUserId();

            //getUserPermissionList(context, application.getCurrentUser());
           // getDocumnetUserList(context, application.getCurrentUser());
           // sendChatMessageList(context, application.getCurrentUser());
            //resumeChatMessageAttachFileList(context, application.getCurrentUser());
            //getChatGroupMemberList(context, application.getCurrentUser());
           // sendComplaintReportList(context, application.getCurrentUser());
            //sendChatMessageReadReport(context, application.getCurrentUser());
            //getSurveyFormList(context, application.getCurrentUser());
            //sendSurveyFormList(context, application.getCurrentUser());
           // getCheckFormList(context, application.getCurrentUser());
           // sendCheckFormList(context, application.getCurrentUser());
            //resumeAttachFileList(context, application.getCurrentUser());
            //getLastDocumentVersion(context, application.getCurrentUser());
            //updateDeviceSettingByKey(deviceSetting);

            //getChatMessageStatusList(context, application.getCurrentUser());
           // getChatMessageList(context, application.getCurrentUser());
            //getChatGroupList(context, application.getCurrentUser());

        }
    }

    protected void showToast(final String msg) {
        //gets the main thread
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                // run this code in the main thread
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private DeviceSetting getDeviceSettingByKey(String key) {
        DeviceSetting deviceSetting = coreService.getDeviceSettingByKey(key);
        if (deviceSetting == null) {
            deviceSetting = new DeviceSetting();
            deviceSetting.setKey(key);
        }
        deviceSetting.setBeforeSyncDate(new Date());
        return deviceSetting;
    }

    private void updateDeviceSettingByKey(DeviceSetting deviceSetting) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);
        deviceSetting.setValue(simpleDateFormat.format(deviceSetting.getBeforeSyncDate()));
        deviceSetting.setDateLastChange(deviceSetting.getBeforeSyncDate());
        coreService.saveOrUpdateDeviceSetting(deviceSetting);
    }

    public void getUserPermissionList(Context context, User user) {
        DeviceSetting deviceSetting = getDeviceSettingByKey(Constants.DEVICE_SETTING_KEY_LAST_USER_PERMISSION_SYNC_DATE);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", user.getUsername());
            jsonObject.put("tokenPass", user.getBisPassword());

            //***************************************************************
            if (deviceSetting.getValue() != null) {
                jsonObject.put("lastUpdateDate", deviceSetting.getValue());
            }

            MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, context);
            String result = postJsonService.sendData("getUserPermissionList", jsonObject, true);
            System.out.println("====result=" + result);
            if (result != null) {
                JSONObject resultJson = new JSONObject(result);
                if (!resultJson.isNull(Constants.HIGH_SECURITY_ERROR_KEY)) {
                    //// TODO: remove all database data and local file and exit application
                } else if (!resultJson.isNull(Constants.SUCCESS_KEY)) {
                    //// TODO:
                    if (!resultJson.isNull(Constants.RESULT_KEY)) {
                        JSONObject resultJsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                        Map<String, String> userPermissionMap = coreService.getUserPermissionMap(user.getId());
                        Map<String, String> newUserPermissionMap = new HashMap<String, String>();
                        if (!resultJsonObject.isNull("userPermissionList")) {
                            JSONArray permissionJsonArray = resultJsonObject.getJSONArray("userPermissionList");
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

                    application.setPermissionMap(coreService.getUserPermissionMap(user.getId()));
                }
            }

        } catch (JSONException e) {
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = "ChatMessageReceiver";
            }
            Log.d(errorMsg, errorMsg);
        } catch (SocketTimeoutException e) {
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = "SocketTimeoutException";
            }
            Log.d(errorMsg, errorMsg);
        } catch (WebServiceException e) {
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = "WebServiceException";
            }
            Log.d(errorMsg, errorMsg);
        } catch (SocketException e) {
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = "SocketException";
            }
            Log.d(errorMsg, errorMsg);
        }
        updateDeviceSettingByKey(deviceSetting);
    }

    public void getLastDocumentVersion(Context context, User user) {
        DeviceSetting deviceSetting = getDeviceSettingByKey(Constants.DEVICE_SETTING_KEY_LAST_USER_PERMISSION_SYNC_DATE);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", user.getUsername());
            jsonObject.put("tokenPass", user.getBisPassword());

            //***************************************************************
            if (deviceSetting.getValue() != null) {
                jsonObject.put("lastUpdateDate", deviceSetting.getValue());
            }

            MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, context);
            String result = postJsonService.sendData("getLastDocumentVersion", jsonObject, true);
            System.out.println("getLastDocumentVersion=" + result);
            if (result != null) {
                JSONObject resultJson = new JSONObject(result);
                if (!resultJson.isNull(Constants.HIGH_SECURITY_ERROR_KEY)) {
                    //// TODO: remove all database data and local file and exit application
                } else if (!resultJson.isNull(Constants.SUCCESS_KEY)) {
                    //// TODO:
                    if (!resultJson.isNull(Constants.RESULT_KEY)) {
                        int versionNo = 0;
                        String pathUrl = null;
                        int versionCode = BuildConfig.VERSION_CODE;
                        String name = null;
                        String Body = getResources().getString(R.string.notification_application_newVersion);
                        JSONObject resultJsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                        if (!resultJsonObject.isNull("document")) {
                            JSONObject documentJsonObject = resultJsonObject.getJSONObject("document");

                            if (!documentJsonObject.isNull("lastDocumentVersion")) {
                                JSONObject lastVersionJsonObject = documentJsonObject.getJSONObject("lastDocumentVersion");
                                if (!lastVersionJsonObject.isNull("versionNo")) {
                                    versionNo = Integer.parseInt(lastVersionJsonObject.getString("versionNo"));
                                    System.out.println("versionNoIS===" + versionNo);
                                }
                                if (!lastVersionJsonObject.isNull("pathUrl")) {
                                    pathUrl = lastVersionJsonObject.getString("pathUrl");
                                }
                                if (!lastVersionJsonObject.isNull("nameFv")) {
                                    name = lastVersionJsonObject.getString("nameFv");
                                }

                                if (versionCode < versionNo) {
                                    SimpleDateFormat mdformat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                    Date currentDate = new Date();
                                    String strOldDate = AppController.getInstance().getSharedPreferences().getString(Constants.DATE_SHOW_NOTIFICATION, "");
                                    Date oldDate = null;
                                    System.out.println("------strOldDate=" + strOldDate);
                                    if (strOldDate != null && !strOldDate.isEmpty()) {
                                        try {
                                            oldDate = mdformat.parse(strOldDate);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if (oldDate == null || DateUtils.dateDiff(currentDate, oldDate, Calendar.HOUR_OF_DAY).compareTo((long) 24) >= 0) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                            // showNotification(context, name, Body);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (JSONException e) {
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = "ChatMessageReceiver";
            }
            Log.d(errorMsg, errorMsg);
        } catch (SocketTimeoutException e) {
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = "SocketTimeoutException";
            }
            Log.d(errorMsg, errorMsg);
        } catch (WebServiceException e) {
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = "WebServiceException";
            }
            Log.d(errorMsg, errorMsg);
        } catch (SocketException e) {
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = "SocketException";
            }
            Log.d(errorMsg, errorMsg);
        }
        updateDeviceSettingByKey(deviceSetting);
    }

    public void getDocumnetUserList(Context context, User user) {
        DeviceSetting deviceSetting = getDeviceSettingByKey(Constants.DEVICE_SETTING_KEY_LAST_APP_USER_SYNC_DATE);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", user.getUsername());
            jsonObject.put("tokenPass", user.getBisPassword());
            jsonObject.put("lastUpdateDate", deviceSetting.getValue());

            //***************************************************************
            if (deviceSetting.getValue() != null) {
                jsonObject.put("lastUpdateDate", deviceSetting.getValue());
            }

            MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, context);
            String result = postJsonService.sendData("getDocumentUserList", jsonObject, true);
            System.out.println("====result=" + result);
            if (result != null) {
                JSONObject resultJson = new JSONObject(result);
                if (!resultJson.isNull(Constants.SUCCESS_KEY)) {
                    if (!resultJson.isNull(Constants.RESULT_KEY)) {
                        JSONObject resultJsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                        if (!resultJsonObject.isNull("userList")) {
                            JSONArray userJsonArray = resultJsonObject.getJSONArray("userList");
                            List<AppUser> appUserList = new ArrayList<AppUser>();
                            for (int i = 0; i < userJsonArray.length(); i++) {
                                JSONObject userJsonObject = userJsonArray.getJSONObject(i);
                                AppUser appUser = new AppUser();
                                if (!userJsonObject.isNull("id")) {
                                    appUser.setId(userJsonObject.getLong("id"));
                                }
                                if (!userJsonObject.isNull("name")) {
                                    appUser.setName(userJsonObject.getString("name"));
                                }
                                if (!userJsonObject.isNull("family")) {
                                    appUser.setFamily(userJsonObject.getString("family"));
                                }
                                appUserList.add(appUser);
                            }
                            if (!appUserList.isEmpty()) {
                                coreService.saveAppUserList(appUserList);
                            }
                        }
                    }
                }
            }

        } catch (JSONException e) {
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = "ChatMessageReceiver";
            }
            Log.d(errorMsg, errorMsg);
        } catch (SocketTimeoutException e) {
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = "SocketTimeoutException";
            }
            Log.d(errorMsg, errorMsg);
        } catch (WebServiceException e) {
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = "WebServiceException";
            }
            Log.d(errorMsg, errorMsg);
        } catch (SocketException e) {
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = "SocketException";
            }
            Log.d(errorMsg, errorMsg);
        }
        updateDeviceSettingByKey(deviceSetting);
    }

    public void sendChatMessageList(Context context, User user) {
        SEND_MESSAGE_IN_PROGRESS = true;
        DeviceSetting deviceSetting = getDeviceSettingByKey(Constants.DEVICE_SETTING_KEY_LAST_CHAT_MESSAGE_SEND_SYNC_DATE);
        ChatMessage chatMessageFS = new ChatMessage();
        chatMessageFS.setSenderAppUserId(user.getServerUserId());
        List<ChatMessage> chatMessageList = coreService.getUnSentChatMessageList(chatMessageFS);
        if (!chatMessageList.isEmpty()) {
            for (ChatMessage chatMessage : chatMessageList) {
                sendChatMessage(context, coreService, chatMessage, user);
            }

        }
        updateDeviceSettingByKey(deviceSetting);
        SEND_MESSAGE_IN_PROGRESS = false;
    }

    public void sendChatMessage(Context context, CoreService coreService, ChatMessage chatMessage, User user) {
        chatMessage.setSendingStatusEn(SendingStatusEn.InProgress.ordinal());
        chatMessage.setSendingStatusDate(new Date());
        coreService.updateChatMessage(chatMessage);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", user.getUsername());
            jsonObject.put("tokenPass", user.getBisPassword());
            JSONObject chatMessageJsonObject = new JSONObject();
            chatMessageJsonObject.put("id", chatMessage.getId());
            chatMessageJsonObject.put("senderUserId", chatMessage.getSenderAppUserId());

            if (chatMessage.getMessage() != null) {
                chatMessageJsonObject.put("message", chatMessage.getMessage());
            }
            if (chatMessage.getAttachFileUserFileName() != null) {
                chatMessageJsonObject.put("attachFileUserFileName", chatMessage.getAttachFileUserFileName());
            }
            if (chatMessage.getAttachFileLocalPath() != null) {
                File attachedFile = new File(chatMessage.getAttachFileLocalPath());
                if (attachedFile.exists()) {
                    FileInputStream inputStream = new FileInputStream(attachedFile);
                    byte[] fileBytes = new byte[MAX_ATTACH_FILE_PACKET_SIZE];


                    int res = inputStream.read(fileBytes);
                    byte[] fixedFileBytes = Arrays.copyOf(fileBytes, res);

                    JSONArray attachmentByteJsonArray = new JSONArray();
                    for (int i = 0; i < fixedFileBytes.length; i++) {
                        byte fileByte = fixedFileBytes[i];
                        attachmentByteJsonArray.put(fileByte);
                    }
                    chatMessageJsonObject.put("attachmentBytes", attachmentByteJsonArray);
                    chatMessageJsonObject.put("attachmentChecksum", ImageUtil.getMD5Checksum(chatMessage.getAttachFileLocalPath()));
                }
            }
            if (chatMessage.getValidUntilDate() != null) {
                chatMessageJsonObject.put("validUntilDate", simpleDateFormat.format(chatMessage.getValidUntilDate()));
            }
            if (chatMessage.getReceiverAppUserId() != null) {
                chatMessageJsonObject.put("receiverUserId", chatMessage.getReceiverAppUserId());
            }
            if (chatMessage.getChatGroupId() != null) {
                chatMessageJsonObject.put("chatGroupId", coreService.getChatGroupById(chatMessage.getChatGroupId()).getServerGroupId());
            }
            jsonObject.put("chatMessage", chatMessageJsonObject);
            MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, context);
            String result = postJsonService.sendData("saveChatMessage", jsonObject, true);
            if (result != null) {
                JSONObject resultJson = new JSONObject(result);
                if (!resultJson.isNull(Constants.SUCCESS_KEY)) {
                    if (!resultJson.isNull(Constants.RESULT_KEY)) {
                        JSONObject resultJsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                        if (!resultJsonObject.isNull("savedChatMessage")) {
                            JSONObject savedChatMessageJsonObject = resultJsonObject.getJSONObject("savedChatMessage");
                            if (!savedChatMessageJsonObject.isNull("id")) {
                                chatMessage.setServerMessageId(savedChatMessageJsonObject.getLong("id"));
                            }

                            if (!savedChatMessageJsonObject.isNull("dateCreation")) {
                                chatMessage.setDeliverDate(simpleDateFormat.parse(savedChatMessageJsonObject.getString("dateCreation")));
                            }
                            if (!savedChatMessageJsonObject.isNull("totalReceivedBytes")) {
                                chatMessage.setAttachFileSentSize(savedChatMessageJsonObject.getInt("totalReceivedBytes"));
                            }
                        }
                        if (chatMessage.getAttachFileSize() == null || chatMessage.getAttachFileSentSize() == null || chatMessage.getAttachFileSize().equals(chatMessage.getAttachFileSentSize())) {
                            chatMessage.setSendingStatusEn(SendingStatusEn.Sent.ordinal());
                            chatMessage.setSendingStatusDate(new Date());
                            chatMessage.setSendDate(new Date());
                            coreService.updateChatMessage(chatMessage);
                        } else {
                            chatMessage.setSendingStatusEn(SendingStatusEn.AttachmentResuming.ordinal());
                            chatMessage.setSendingStatusDate(new Date());
                            coreService.updateChatMessage(chatMessage);
                            resumeChatMessageAttachFile(context, coreService, chatMessage, user);
                        }
                    }
                } else {
                    chatMessage.setSendingStatusEn(SendingStatusEn.Fail.ordinal());
                    chatMessage.setSendingStatusDate(new Date());
                    coreService.updateChatMessage(chatMessage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = "ChatMessageReceiver";
            }
            Log.d(errorMsg, errorMsg);
            chatMessage.setSendingStatusEn(SendingStatusEn.Pending.ordinal());
            chatMessage.setSendingStatusDate(new Date());
            coreService.updateChatMessage(chatMessage);
        }
    }

    public void resumeChatMessageAttachFileList(Context context, User user) {
        DeviceSetting deviceSetting = getDeviceSettingByKey(Constants.DEVICE_SETTING_KEY_LAST_CHAT_MESSAGE_SEND_SYNC_DATE);
        ChatMessage chatMessageFS = new ChatMessage();
        chatMessageFS.setSenderAppUserId(user.getServerUserId());
        List<ChatMessage> chatMessageList = coreService.getAttachmentResumingChatMessageList(chatMessageFS);
        if (!chatMessageList.isEmpty()) {
            for (ChatMessage chatMessage : chatMessageList) {
                resumeChatMessageAttachFile(context, coreService, chatMessage, user);
            }
        }
        updateDeviceSettingByKey(deviceSetting);
    }

    public void resumeChatMessageAttachFile(Context context, CoreService coreService, ChatMessage chatMessage, User user) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", user.getUsername());
            jsonObject.put("tokenPass", user.getBisPassword());
            JSONObject chatMessageJsonObject = new JSONObject();
            chatMessageJsonObject.put("id", chatMessage.getId());
            chatMessageJsonObject.put("senderUserId", chatMessage.getSenderAppUserId());

            if (chatMessage.getAttachFileLocalPath() != null) {
                File attachedFile = new File(chatMessage.getAttachFileLocalPath());
                if (attachedFile.exists()) {
                    FileInputStream inputStream = new FileInputStream(attachedFile);
                    Integer fileSize = inputStream.available();
                    //skip already sent bytes
                    long skipRes = inputStream.skip(chatMessage.getAttachFileSentSize());

                    byte[] fileBytes = new byte[MAX_ATTACH_FILE_PACKET_SIZE];
                    int res = inputStream.read(fileBytes);

                    byte[] fixedFileBytes = Arrays.copyOf(fileBytes, res);

                    JSONArray attachmentByteJsonArray = new JSONArray();
                    for (int i = 0; i < fixedFileBytes.length; i++) {
                        byte fileByte = fixedFileBytes[i];
                        attachmentByteJsonArray.put(fileByte);
                    }
                    chatMessageJsonObject.put("attachmentBytes", attachmentByteJsonArray);
                    chatMessageJsonObject.put("attachmentChecksum", ImageUtil.getMD5Checksum(chatMessage.getAttachFileLocalPath()));

                    jsonObject.put("chatMessage", chatMessageJsonObject);
                    MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, context);
                    String result = postJsonService.sendData("resumeChatMessageAttachFile", jsonObject, true);
                    if (result != null) {
                        JSONObject resultJson = new JSONObject(result);
                        if (!resultJson.isNull(Constants.SUCCESS_KEY)) {
                            if (!resultJson.isNull(Constants.RESULT_KEY)) {
                                JSONObject resultJsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                                if (!resultJsonObject.isNull("savedAttachFile")) {
                                    JSONObject savedAttachFileJsonObject = resultJsonObject.getJSONObject("savedAttachFile");
                                    if (!savedAttachFileJsonObject.isNull("id")) {
                                        chatMessage.setServerMessageId(savedAttachFileJsonObject.getLong("id"));
                                    }


                                    if (!savedAttachFileJsonObject.isNull("totalReceivedBytes")) {
                                        chatMessage.setAttachFileSentSize(savedAttachFileJsonObject.getInt("totalReceivedBytes"));
                                    }
                                }
                                if (chatMessage.getAttachFileSize() != null && chatMessage.getAttachFileSentSize() != null && chatMessage.getAttachFileSentSize().compareTo(chatMessage.getAttachFileSize()) > 0) {
                                    chatMessage.setSendingStatusEn(SendingStatusEn.Fail.ordinal());
                                    chatMessage.setSendingStatusDate(new Date());
                                    coreService.updateChatMessage(chatMessage);
                                } else if (chatMessage.getAttachFileSize() == null || chatMessage.getAttachFileSentSize() == null || chatMessage.getAttachFileSize().equals(chatMessage.getAttachFileSentSize())) {
                                    chatMessage.setSendingStatusEn(SendingStatusEn.Sent.ordinal());
                                    chatMessage.setSendingStatusDate(new Date());
                                    chatMessage.setSendDate(new Date());
                                    coreService.updateChatMessage(chatMessage);
                                } else {
                                    chatMessage.setSendingStatusEn(SendingStatusEn.AttachmentResuming.ordinal());
                                    chatMessage.setSendingStatusDate(new Date());
                                    coreService.updateChatMessage(chatMessage);
                                    resumeChatMessageAttachFile(context, coreService, chatMessage, user);
                                }
                            }
                        } else {
                            chatMessage.setSendingStatusEn(SendingStatusEn.Fail.ordinal());
                            chatMessage.setSendingStatusDate(new Date());
                            coreService.updateChatMessage(chatMessage);
                        }
                    }

                } else {
                    chatMessage.setSendingStatusEn(SendingStatusEn.Fail.ordinal());
                    chatMessage.setSendingStatusDate(new Date());
                    coreService.updateChatMessage(chatMessage);
                }
            } else {
                chatMessage.setSendingStatusEn(SendingStatusEn.Fail.ordinal());
                chatMessage.setSendingStatusDate(new Date());
                coreService.updateChatMessage(chatMessage);
            }
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = "ChatMessageReceiver";
            }
            Log.d(errorMsg, errorMsg);
            chatMessage.setSendingStatusEn(SendingStatusEn.Fail.ordinal());
            chatMessage.setSendingStatusDate(new Date());
            coreService.updateChatMessage(chatMessage);
        }
    }

    public void getChatGroupList(Context context, User user) {
        DeviceSetting deviceSetting = getDeviceSettingByKey(Constants.DEVICE_SETTING_KEY_LAST_CHAT_GROUP_SYNC_DATE);
        List<Long> longList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", user.getUsername());
            jsonObject.put("tokenPass", user.getBisPassword());

            MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, context);
            try {
                String result = postJsonService.sendData("getUserChatGroupList", jsonObject, true);
                System.out.println("====resultGetChatGroupList=" + result);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                if (result != null) {
                    try {
                        JSONObject resultJson = new JSONObject(result);
                        if (!resultJson.isNull(Constants.SUCCESS_KEY)) {
                            if (!resultJson.isNull(Constants.RESULT_KEY)) {
                                JSONObject resultJsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                                List<Long> serverGroupIdList = new ArrayList<Long>();
                                if (!resultJsonObject.isNull("chatGroupList")) {
                                    JSONArray chatGroupJsonArray = resultJsonObject.getJSONArray("chatGroupList");
                                    for (int i = 0; i < chatGroupJsonArray.length(); i++) {
                                        JSONObject chatGroupJsonObject = chatGroupJsonArray.getJSONObject(i);
                                        if (!chatGroupJsonObject.isNull("id")) {
                                            Long serverGroupId = chatGroupJsonObject.getLong("id");
                                            longList.add(serverGroupId);
                                            serverGroupIdList.add(serverGroupId);
                                            ChatGroup tmpChatGroupFS = new ChatGroup();
                                            tmpChatGroupFS.setServerGroupId(serverGroupId);

                                            ChatGroup chatGroup = coreService.getChatGroupByServerGroupId(tmpChatGroupFS);
                                            if (chatGroup == null) {
                                                chatGroup = new ChatGroup();
                                                chatGroup.setServerGroupId(serverGroupId);
                                            }
                                            if (!chatGroupJsonObject.isNull("name")) {
                                                chatGroup.setName(chatGroupJsonObject.getString("name"));
                                            }
                                            if (!chatGroupJsonObject.isNull("maxMember")) {
                                                chatGroup.setMaxMember(chatGroupJsonObject.getInt("maxMember"));
                                            }
                                            if (!chatGroupJsonObject.isNull("notifyAct")) {
                                                if (chatGroup.getId() == null) {
                                                    chatGroup.setNotifyAct(chatGroupJsonObject.getBoolean("notifyAct"));
                                                }
                                            }
                                            if (!chatGroupJsonObject.isNull("status")) {
                                                chatGroup.setStatusEn(chatGroupJsonObject.getInt("status"));
                                            }
                                            if (chatGroup.getId() == null) {
                                                chatGroup = coreService.saveChatGroup(chatGroup);
                                            } else {
                                                coreService.updateChatGroup(chatGroup);
                                            }

                                            /*List<Long> userIdList = new ArrayList<Long>();
                                            if (!chatGroupJsonObject.isNull("chatGroupMemberList")) {
                                                JSONArray chatGroupMemberJsonArray = chatGroupJsonObject.getJSONArray("chatGroupMemberList");
                                                for (int j = 0; j < chatGroupMemberJsonArray.length(); j++) {
                                                    JSONObject chatGroupMemberJsonObject = chatGroupMemberJsonArray.getJSONObject(j);
                                                    System.out.println("chatGroupMemberJsonObject===" + chatGroupMemberJsonObject);
                                                    if (!chatGroupMemberJsonObject.isNull("userId")) {
                                                        Long userId = chatGroupMemberJsonObject.getLong("userId");
                                                        userIdList.add(userId);
                                                        ChatGroupMember tmpChatGroupMemberFS = new ChatGroupMember();
                                                        tmpChatGroupMemberFS.setAppUserId(userId);
                                                        tmpChatGroupMemberFS.setChatGroupId(chatGroup.getId());
                                                        ChatGroupMember chatGroupMember = coreService.getChatGroupMemberByUserAndGroup(tmpChatGroupMemberFS);
                                                        if (chatGroupMember == null) {
                                                            chatGroupMember = new ChatGroupMember();
                                                            chatGroupMember.setAppUserId(userId);
                                                            chatGroupMember.setChatGroupId(chatGroup.getId());
                                                        }
                                                        if (!chatGroupMemberJsonObject.isNull("privilegeTypeEn")) {
                                                            chatGroupMember.setPrivilegeTypeEn(chatGroupMemberJsonObject.getInt("privilegeTypeEn"));
                                                        }
                                                        if (!chatGroupMemberJsonObject.isNull("adminIs")) {
                                                            chatGroupMember.setAdminIs(chatGroupMemberJsonObject.getBoolean("adminIs"));
                                                        }
                                                        if (chatGroupMember.getId() == null) {
                                                            coreService.saveChatGroupMember(chatGroupMember);
                                                        } else {
                                                            coreService.updateChatGroupMember(chatGroupMember);
                                                        }

                                                        AppUser appUser = coreService.getAppUserById(chatGroupMember.getAppUserId());
                                                        if (appUser == null) {
                                                            getUserById(context, user, chatGroupMember.getAppUserId());
                                                        }
                                                    }
                                                }
                                            }*/
                                            /*ChatGroupMember tmpChatGroupMemberFS = new ChatGroupMember();
                                            tmpChatGroupMemberFS.setChatGroupId(chatGroup.getId());
                                            tmpChatGroupMemberFS.setNotAppUserIdList(userIdList);
                                            List<ChatGroupMember> ChatGroupMemberRemovedList = coreService.getChatGroupMemberListByParam(tmpChatGroupMemberFS);
                                            for (ChatGroupMember ChatGroupMemberRemoved : ChatGroupMemberRemovedList) {
                                                coreService.deleteChatGroupMember(ChatGroupMemberRemoved);
                                            }*/
                                        }
                                    }
                                }
                                ChatGroup tmpChatGroupFS = new ChatGroup();
                                tmpChatGroupFS.setNotServerGroupIdList(serverGroupIdList);
                                List<ChatGroup> chatGroupUserRemovedList = coreService.getChatGroupListByParam(tmpChatGroupFS);
                                for (ChatGroup chatGroupUserRemoved : chatGroupUserRemovedList) {
                                    chatGroupUserRemoved.setStatusEn(GeneralStatus.Inactive.ordinal());
                                    coreService.updateChatGroup(chatGroupUserRemoved);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        String errorMsg = e.getMessage();
                        if (errorMsg == null) {
                            errorMsg = "ChatMessageReceiver";
                        }
                        Log.d(errorMsg, errorMsg);
                        Toast.makeText(context, getResources().getString(R.string.Some_error_accor_contact_admin), Toast.LENGTH_LONG).show();
                    }
                }

            } catch (SocketTimeoutException e) {
                String errorMsg = e.getMessage();
                if (errorMsg == null) {
                    errorMsg = "SocketTimeoutException";
                }
                Log.d(errorMsg, errorMsg);
            } catch (SocketException | WebServiceException e) {
                String errorMsg = e.getMessage();
                if (errorMsg == null) {
                    errorMsg = "ChatMessageReceiver";
                }
                Log.d(errorMsg, errorMsg);
            }

        } catch (JSONException e) {
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = "RegistrationFragment";
            }
            Log.d(errorMsg, errorMsg);
        }
        updateDeviceSettingByKey(deviceSetting);
    }

    public void getChatGroupMemberList(Context context, User user) {
        // DeviceSetting deviceSetting = getDeviceSettingByKey(Constants.DEVICE_SETTING_KEY_LAST_CHAT_GROUP_SYNC_DATE);
        List<Long> longList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", user.getUsername());
            jsonObject.put("tokenPass", user.getBisPassword());

            MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, context);
            try {
                String result = postJsonService.sendData("getUserChatGroupMemberList", jsonObject, true);
                System.out.println("====resultGetChatGroupList=" + result);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                if (result != null) {
                    try {
                        JSONObject resultJson = new JSONObject(result);
                        if (!resultJson.isNull(Constants.SUCCESS_KEY)) {
                            if (!resultJson.isNull(Constants.RESULT_KEY)) {
                                JSONObject resultJsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                                List<Long> serverGroupIdList = new ArrayList<Long>();
                                if (!resultJsonObject.isNull("chatGroupList")) {
                                    JSONArray chatGroupJsonArray = resultJsonObject.getJSONArray("chatGroupList");
                                    for (int i = 0; i < chatGroupJsonArray.length(); i++) {
                                        JSONObject chatGroupJsonObject = chatGroupJsonArray.getJSONObject(i);
                                        if (!chatGroupJsonObject.isNull("id")) {
                                            Long serverGroupId = chatGroupJsonObject.getLong("id");
                                            longList.add(serverGroupId);
                                            serverGroupIdList.add(serverGroupId);
                                            ChatGroup tmpChatGroupFS = new ChatGroup();
                                            tmpChatGroupFS.setServerGroupId(serverGroupId);

                                            ChatGroup chatGroup = coreService.getChatGroupByServerGroupId(tmpChatGroupFS);
                                            if (chatGroup == null) {
                                                chatGroup = new ChatGroup();
                                                chatGroup.setServerGroupId(serverGroupId);
                                            }
                                            if (!chatGroupJsonObject.isNull("name")) {
                                                chatGroup.setName(chatGroupJsonObject.getString("name"));
                                            }
                                            if (!chatGroupJsonObject.isNull("maxMember")) {
                                                chatGroup.setMaxMember(chatGroupJsonObject.getInt("maxMember"));
                                            }
                                            if (!chatGroupJsonObject.isNull("notifyAct")) {
                                                if (chatGroup.getId() == null) {
                                                    chatGroup.setNotifyAct(chatGroupJsonObject.getBoolean("notifyAct"));
                                                }
                                            }
                                            if (!chatGroupJsonObject.isNull("status")) {
                                                chatGroup.setStatusEn(chatGroupJsonObject.getInt("status"));
                                            }
                                            if (chatGroup.getId() == null) {
                                                chatGroup = coreService.saveChatGroup(chatGroup);
                                            } else {
                                                coreService.updateChatGroup(chatGroup);
                                            }

                                            List<Long> userIdList = new ArrayList<Long>();
                                            if (!chatGroupJsonObject.isNull("chatGroupMembers")) {
                                                JSONArray chatGroupMemberJsonArray = chatGroupJsonObject.getJSONArray("chatGroupMembers");
                                                for (int j = 0; j < chatGroupMemberJsonArray.length(); j++) {
                                                    JSONObject chatGroupMemberJsonObject = chatGroupMemberJsonArray.getJSONObject(j);
                                                    System.out.println("chatGroupMemberJsonObject===" + chatGroupMemberJsonObject);
                                                    if (!chatGroupMemberJsonObject.isNull("userId")) {
                                                        Long userId = chatGroupMemberJsonObject.getLong("userId");
                                                        userIdList.add(userId);
                                                        ChatGroupMember tmpChatGroupMemberFS = new ChatGroupMember();
                                                        tmpChatGroupMemberFS.setAppUserId(userId);
                                                        tmpChatGroupMemberFS.setChatGroupId(chatGroup.getId());
                                                        ChatGroupMember chatGroupMember = coreService.getChatGroupMemberByUserAndGroup(tmpChatGroupMemberFS);
                                                        if (chatGroupMember == null) {
                                                            chatGroupMember = new ChatGroupMember();
                                                            chatGroupMember.setAppUserId(userId);
                                                            chatGroupMember.setChatGroupId(chatGroup.getId());
                                                        }
                                                        if (!chatGroupMemberJsonObject.isNull("privilegeTypeEn")) {
                                                            chatGroupMember.setPrivilegeTypeEn(chatGroupMemberJsonObject.getInt("privilegeTypeEn"));
                                                        }
                                                        if (!chatGroupMemberJsonObject.isNull("adminIs")) {
                                                            chatGroupMember.setAdminIs(chatGroupMemberJsonObject.getBoolean("adminIs"));
                                                        }
                                                        if (chatGroupMember.getId() == null) {
                                                            coreService.saveChatGroupMember(chatGroupMember);
                                                        } else {
                                                            coreService.updateChatGroupMember(chatGroupMember);
                                                        }

                                                        AppUser appUser = coreService.getAppUserById(chatGroupMember.getAppUserId());
                                                        if (appUser == null) {
                                                            getUserById(context, user, chatGroupMember.getAppUserId());
                                                        }
                                                    }
                                                }
                                            }
                                            ChatGroupMember tmpChatGroupMemberFS = new ChatGroupMember();
                                            tmpChatGroupMemberFS.setChatGroupId(chatGroup.getId());
                                            tmpChatGroupMemberFS.setNotAppUserIdList(userIdList);
                                            List<ChatGroupMember> ChatGroupMemberRemovedList = coreService.getChatGroupMemberListByParam(tmpChatGroupMemberFS);
                                            for (ChatGroupMember ChatGroupMemberRemoved : ChatGroupMemberRemovedList) {
                                                coreService.deleteChatGroupMember(ChatGroupMemberRemoved);
                                            }
                                        }
                                    }
                                }
                                ChatGroup tmpChatGroupFS = new ChatGroup();
                                tmpChatGroupFS.setNotServerGroupIdList(serverGroupIdList);
                                if (tmpChatGroupFS != null) {
                                    List<ChatGroup> chatGroupUserRemovedList = coreService.getChatGroupListByParam(tmpChatGroupFS);
                                    for (ChatGroup chatGroupUserRemoved : chatGroupUserRemovedList) {
                                        chatGroupUserRemoved.setStatusEn(GeneralStatus.Inactive.ordinal());
                                        coreService.updateChatGroup(chatGroupUserRemoved);
                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {
                        String errorMsg = e.getMessage();
                        if (errorMsg == null) {
                            errorMsg = "ChatMessageReceiver";
                        }
                        Log.d(errorMsg, errorMsg);
                        Toast.makeText(context, getResources().getString(R.string.Some_error_accor_contact_admin), Toast.LENGTH_LONG).show();
                    }
                }

            } catch (SocketTimeoutException e) {
                String errorMsg = e.getMessage();
                if (errorMsg == null) {
                    errorMsg = "SocketTimeoutException";
                }
                Log.d(errorMsg, errorMsg);
            } catch (SocketException | WebServiceException e) {
                String errorMsg = e.getMessage();
                if (errorMsg == null) {
                    errorMsg = "ChatMessageReceiver";
                }
                Log.d(errorMsg, errorMsg);
            }

        } catch (JSONException e) {
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = "RegistrationFragment";
            }
            Log.d(errorMsg, errorMsg);
        }
        //updateDeviceSettingByKey(deviceSetting);
    }

    public void getUserById(Context context, User user, Long userId) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", user.getUsername());
            jsonObject.put("tokenPass", user.getBisPassword());
            jsonObject.put("id", userId);

            MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, context);
            try {
                String result = postJsonService.sendData("getUserInfoById", jsonObject, true);
                System.out.println("====result=" + result);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                if (result != null) {
                    try {
                        JSONObject resultJson = new JSONObject(result);
                        if (!resultJson.isNull(Constants.SUCCESS_KEY)) {
                            if (!resultJson.isNull(Constants.RESULT_KEY)) {
                                JSONObject resultJsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                                List<Long> serverGroupIdList = new ArrayList<Long>();
                                if (!resultJsonObject.isNull("user")) {
                                    JSONObject userJsonObject = resultJsonObject.getJSONObject("user");
                                    AppUser appUser = new AppUser();
                                    if (!userJsonObject.isNull("id")) {
                                        appUser.setId(userJsonObject.getLong("id"));
                                    }
                                    if (!userJsonObject.isNull("name")) {
                                        appUser.setName(userJsonObject.getString("name"));
                                    }
                                    if (!userJsonObject.isNull("family")) {
                                        appUser.setFamily(userJsonObject.getString("family"));
                                    }
                                    appUser = coreService.insertAppUser(appUser);

                                }

                            }
                        }
                    } catch (JSONException e) {
                        String errorMsg = e.getMessage();
                        if (errorMsg == null) {
                            errorMsg = "ChatMessageReceiver";
                        }
                        Log.d(errorMsg, errorMsg);
                        Toast.makeText(context, getResources().getString(R.string.Some_error_accor_contact_admin), Toast.LENGTH_LONG).show();
                    }
                }

            } catch (SocketTimeoutException e) {
                String errorMsg = e.getMessage();
                if (errorMsg == null) {
                    errorMsg = "SocketTimeoutException";
                }
                Log.d(errorMsg, errorMsg);
            } catch (SocketException | WebServiceException e) {
                String errorMsg = e.getMessage();
                if (errorMsg == null) {
                    errorMsg = "ChatMessageReceiver";
                }
                Log.d(errorMsg, errorMsg);
            }

        } catch (JSONException e) {
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = "RegistrationFragment";
            }
            Log.d(errorMsg, errorMsg);
        }
    }

    public void getChatMessageList(Context context, User user) {
        DeviceSetting deviceSetting = getDeviceSettingByKey(Constants.DEVICE_SETTING_KEY_LAST_CHAT_MESSAGE_RECEIVE_SYNC_DATE);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", user.getUsername());
            jsonObject.put("tokenPass", user.getBisPassword());

            MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, context);
            try {
                String result = postJsonService.sendData("getUserChatMessageList", jsonObject, true);
                System.out.println("====result=" + result);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);
                if (result != null) {
                    try {
                        JSONObject resultJson = new JSONObject(result);
                        if (!resultJson.isNull(Constants.SUCCESS_KEY)) {
                            if (!resultJson.isNull(Constants.RESULT_KEY)) {
                                //EventBus.getDefault().post(new EventBusModel(false));
                                JSONObject resultJsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                                if (!resultJsonObject.isNull("chatMessageReceiverList")) {
                                    JSONArray chatMessageReceiverJsonArray = resultJsonObject.getJSONArray("chatMessageReceiverList");
                                    List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
                                    if (chatMessageReceiverJsonArray.length() > 0) {
                                        List<ChatGroup> chatGroupList = coreService.getChatGroupList();
                                        Map<Long, ChatGroup> chatGroupMap = new HashMap<>();
                                        for (ChatGroup chatGroup : chatGroupList) {
                                            chatGroupMap.put(chatGroup.getServerGroupId(), chatGroup);
                                        }

                                        for (int i = 0; i < chatMessageReceiverJsonArray.length(); i++) {
                                            JSONObject chatMessageReceiverJsonObject = chatMessageReceiverJsonArray.getJSONObject(i);
                                            ChatMessage chatMessage = new ChatMessage();

                                            chatMessage.setReadIs(false);
                                            chatMessage.setDeliverIs(true);
                                            chatMessage.setDeliverDate(new Date());
                                            chatMessage.setAttachFileSize(0);
                                            chatMessage.setAttachFileReceivedSize(0);


                                            if (!chatMessageReceiverJsonObject.isNull("chatMessage")) {
                                                JSONObject chatMessageJsonObject = chatMessageReceiverJsonObject.getJSONObject("chatMessage");
                                                if (!chatMessageJsonObject.isNull("id")) {
                                                    chatMessage.setServerMessageId(chatMessageJsonObject.getLong("id"));
                                                }
                                                if (!chatMessageJsonObject.isNull("senderUserId")) {
                                                    chatMessage.setSenderAppUserId(chatMessageJsonObject.getLong("senderUserId"));
                                                }

                                                if (!chatMessageJsonObject.isNull("validUntilDate")) {
                                                    try {
                                                        chatMessage.setValidUntilDate(simpleDateFormat.parse(chatMessageJsonObject.getString("validUntilDate")));
                                                    } catch (ParseException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                if (!chatMessageJsonObject.isNull("message")) {
                                                    chatMessage.setMessage(chatMessageJsonObject.getString("message"));
                                                }

                                                if (!chatMessageJsonObject.isNull("sendDate")) {
                                                    try {
                                                        String sendDateStr = chatMessageJsonObject.getString("sendDate");
                                                        chatMessage.setSendDate(simpleDateFormat.parse(sendDateStr));
                                                    } catch (ParseException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                if (!chatMessageJsonObject.isNull("dateCreation")) {
                                                    try {
                                                        String dateCreationStr = chatMessageJsonObject.getString("dateCreation");
                                                        chatMessage.setDateCreation(simpleDateFormat.parse(dateCreationStr));
                                                    } catch (ParseException e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                                                if (!chatMessageJsonObject.isNull("chatGroupId")) {
                                                    Long remoteChatGroupId = chatMessageJsonObject.getLong("chatGroupId");
                                                    if (chatGroupMap.containsKey(remoteChatGroupId)) {
                                                        chatMessage.setChatGroupId(chatGroupMap.get(remoteChatGroupId).getId());
                                                    } else {
                                                        continue;
                                                    }
                                                }
                                                if (!chatMessageJsonObject.isNull("attachFileUserFileName")) {
                                                    chatMessage.setAttachFileUserFileName(chatMessageJsonObject.getString("attachFileUserFileName"));
                                                }
                                                if (!chatMessageJsonObject.isNull("attachFileSize")) {
                                                    chatMessage.setAttachFileSize(chatMessageJsonObject.getInt("attachFileSize"));
                                                }
                                            }
                                            chatMessageList.add(chatMessage);
                                        }
                                        JSONArray idJsonArray = new JSONArray();

                                        for (ChatMessage chatMessage : chatMessageList) {
                                            List<ChatMessage> tmpChatMessageList = coreService.getChatMessagesByServerMessageId(chatMessage.getServerMessageId());
                                            if (tmpChatMessageList.isEmpty()) {
                                                chatMessage = coreService.insertChatMessage(chatMessage);
                                                if (chatMessage != null) {
                                                    //createNotification("newChatMessage", chatMessage.getId(), "", chatMessage.getMessage());
                                                }
                                            }
                                            idJsonArray.put(chatMessage.getServerMessageId());
                                        }
                                        jsonObject = new JSONObject();

                                        jsonObject.put("username", user.getUsername());
                                        jsonObject.put("tokenPass", user.getBisPassword());
                                        jsonObject.put("messageIdList", idJsonArray);

                                        postJsonService = new MyPostJsonService(databaseManager, context);
                                        result = postJsonService.sendData("chatMessageDeliveredReport", jsonObject, true);
                                    }
                                    //EventBus.getDefault().post(new EventBusModel(true));
                                }


                            }
                        }

                    } catch (JSONException e) {
                        String errorMsg = e.getMessage();
                        if (errorMsg == null) {
                            errorMsg = "ChatMessageReceiver";
                        }
                        Log.d(errorMsg, errorMsg);
                        Toast.makeText(context, getResources().getString(R.string.Some_error_accor_contact_admin), Toast.LENGTH_LONG).show();
                    }
                }
            } catch (SocketTimeoutException e) {
                String errorMsg = e.getMessage();
                if (errorMsg == null) {
                    errorMsg = "SocketTimeoutException";
                }
                Log.d(errorMsg, errorMsg);
            } catch (SocketException e) {
                String errorMsg = e.getMessage();
                if (errorMsg == null) {
                    errorMsg = "ChatMessageReceiver";
                }
                Log.d(errorMsg, errorMsg);
            } catch (WebServiceException e) {
                String errorMsg = e.getMessage();
                if (errorMsg == null) {
                    errorMsg = "WebServiceException";
                }
                Log.d(errorMsg, errorMsg);
            }

        } catch (JSONException e) {
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = "RegistrationFragment";
            }
            Log.d(errorMsg, errorMsg);
        }
        updateDeviceSettingByKey(deviceSetting);
    }

    public void getChatMessageListFromNotify(Context context, User user) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", user.getUsername());
            jsonObject.put("tokenPass", user.getBisPassword());

            MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, context);
            try {
                String result = postJsonService.sendData("getUserChatMessageList", jsonObject, true);
                System.out.println("====result=" + result);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);
                if (result != null) {
                    try {
                        JSONObject resultJson = new JSONObject(result);
                        if (!resultJson.isNull(Constants.SUCCESS_KEY)) {
                            if (!resultJson.isNull(Constants.RESULT_KEY)) {
                                //EventBus.getDefault().post(new EventBusModel(false));
                                JSONObject resultJsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                                if (!resultJsonObject.isNull("chatMessageReceiverList")) {
                                    JSONArray chatMessageReceiverJsonArray = resultJsonObject.getJSONArray("chatMessageReceiverList");
                                    List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
                                    if (chatMessageReceiverJsonArray.length() > 0) {
                                        Map<Long, ChatGroup> chatGroupMap = new HashMap<>();


                                        try {
                                            List<ChatGroup> chatGroupList = coreService.getChatGroupList();
                                            for (ChatGroup chatGroup : chatGroupList) {
                                                chatGroupMap.put(chatGroup.getServerGroupId(), chatGroup);
                                            }
                                        } catch (Exception e) {
                                            e.getMessage();
                                        }


                                        for (int i = 0; i < chatMessageReceiverJsonArray.length(); i++) {
                                            JSONObject chatMessageReceiverJsonObject = chatMessageReceiverJsonArray.getJSONObject(i);
                                            ChatMessage chatMessage = new ChatMessage();

                                            chatMessage.setReadIs(false);
                                            chatMessage.setDeliverIs(true);
                                            chatMessage.setDeliverDate(new Date());
                                            chatMessage.setAttachFileSize(0);
                                            chatMessage.setAttachFileReceivedSize(0);


                                            if (!chatMessageReceiverJsonObject.isNull("chatMessage")) {
                                                JSONObject chatMessageJsonObject = chatMessageReceiverJsonObject.getJSONObject("chatMessage");
                                                if (!chatMessageJsonObject.isNull("id")) {
                                                    chatMessage.setServerMessageId(chatMessageJsonObject.getLong("id"));
                                                }
                                                if (!chatMessageJsonObject.isNull("senderUserId")) {
                                                    chatMessage.setSenderAppUserId(chatMessageJsonObject.getLong("senderUserId"));
                                                }

                                                if (!chatMessageJsonObject.isNull("validUntilDate")) {
                                                    try {
                                                        chatMessage.setValidUntilDate(simpleDateFormat.parse(chatMessageJsonObject.getString("validUntilDate")));
                                                    } catch (ParseException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                if (!chatMessageJsonObject.isNull("message")) {
                                                    chatMessage.setMessage(chatMessageJsonObject.getString("message"));
                                                }

                                                if (!chatMessageJsonObject.isNull("sendDate")) {
                                                    try {
                                                        String sendDateStr = chatMessageJsonObject.getString("sendDate");
                                                        chatMessage.setSendDate(simpleDateFormat.parse(sendDateStr));
                                                    } catch (ParseException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                if (!chatMessageJsonObject.isNull("dateCreation")) {
                                                    try {
                                                        String dateCreationStr = chatMessageJsonObject.getString("dateCreation");
                                                        chatMessage.setDateCreation(simpleDateFormat.parse(dateCreationStr));
                                                    } catch (ParseException e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                                                if (!chatMessageJsonObject.isNull("chatGroupId")) {
                                                    Long remoteChatGroupId = chatMessageJsonObject.getLong("chatGroupId");
                                                    if (chatGroupMap.containsKey(remoteChatGroupId)) {
                                                        chatMessage.setChatGroupId(chatGroupMap.get(remoteChatGroupId).getId());
                                                    } else {
                                                        continue;
                                                    }
                                                }
                                                if (!chatMessageJsonObject.isNull("attachFileUserFileName")) {
                                                    chatMessage.setAttachFileUserFileName(chatMessageJsonObject.getString("attachFileUserFileName"));
                                                }
                                                if (!chatMessageJsonObject.isNull("attachFileSize")) {
                                                    chatMessage.setAttachFileSize(chatMessageJsonObject.getInt("attachFileSize"));
                                                }
                                            }
                                            chatMessageList.add(chatMessage);
                                        }
                                        JSONArray idJsonArray = new JSONArray();

                                        for (ChatMessage chatMessage : chatMessageList) {
                                            List<ChatMessage> tmpChatMessageList = coreService.getChatMessagesByServerMessageId(chatMessage.getServerMessageId());
                                            if (tmpChatMessageList.isEmpty()) {
                                                chatMessage = coreService.insertChatMessage(chatMessage);
                                                if (chatMessage != null) {
                                                    // createNotification("newChatMessage", chatMessage.getId(), "", chatMessage.getMessage());
                                                }
                                            }
                                            idJsonArray.put(chatMessage.getServerMessageId());
                                        }
                                        jsonObject = new JSONObject();

                                        jsonObject.put("username", user.getUsername());
                                        jsonObject.put("tokenPass", user.getBisPassword());
                                        jsonObject.put("messageIdList", idJsonArray);

                                        postJsonService = new MyPostJsonService(databaseManager, context);
                                        result = postJsonService.sendData("chatMessageDeliveredReport", jsonObject, true);
                                    }
                                    //EventBus.getDefault().post(new EventBusModel(true));
                                }


                            }
                        }

                    } catch (JSONException e) {
                        String errorMsg = e.getMessage();
                        if (errorMsg == null) {
                            errorMsg = "ChatMessageReceiver";
                        }
                        Log.d(errorMsg, errorMsg);
                        Toast.makeText(context, getResources().getString(R.string.Some_error_accor_contact_admin), Toast.LENGTH_LONG).show();
                    }
                }
            } catch (SocketTimeoutException e) {
                String errorMsg = e.getMessage();
                if (errorMsg == null) {
                    errorMsg = "SocketTimeoutException";
                }
                Log.d(errorMsg, errorMsg);
            } catch (SocketException e) {
                String errorMsg = e.getMessage();
                if (errorMsg == null) {
                    errorMsg = "ChatMessageReceiver";
                }
                Log.d(errorMsg, errorMsg);
            } catch (WebServiceException e) {
                String errorMsg = e.getMessage();
                if (errorMsg == null) {
                    errorMsg = "WebServiceException";
                }
                Log.d(errorMsg, errorMsg);
            }

        } catch (JSONException e) {
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = "RegistrationFragment";
            }
            Log.d(errorMsg, errorMsg);
        }
    }

    public void sendChatMessageReadReport(Context context, User user) {
        DeviceSetting deviceSetting = getDeviceSettingByKey(Constants.DEVICE_SETTING_KEY_LAST_SEND_CHAT_MESSAGE_READ_REPORT);
        try {
            JSONObject jsonObject = new JSONObject();
            ChatMessage tmpChatMessageFS = new ChatMessage();
            tmpChatMessageFS.setReadIs(Boolean.TRUE);
            tmpChatMessageFS.setReadDateFrom(deviceSetting.getDateLastChange());
            tmpChatMessageFS.setSenderAppUserIdNot(user.getServerUserId());
            List<ChatMessage> chatMessageList = coreService.getChatMessageListByParam(tmpChatMessageFS);

            if (!chatMessageList.isEmpty()) {
                jsonObject.put("username", user.getUsername());
                jsonObject.put("tokenPass", user.getBisPassword());
                JSONArray chatMessageJsonArray = new JSONArray();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                for (ChatMessage chatMessage : chatMessageList) {
                    JSONObject chatMessageJsonObject = new JSONObject();
                    chatMessageJsonObject.put("id", chatMessage.getServerMessageId());
                    chatMessageJsonObject.put("readDate", simpleDateFormat.format(chatMessage.getReadDate()));
                    chatMessageJsonArray.put(chatMessageJsonObject);
                }

                jsonObject.put("chatMessageList", chatMessageJsonArray);
                MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, context);
                String result = postJsonService.sendData("chatMessageReadReport", jsonObject, true);
                if (result != null) {
                    JSONObject resultJson = new JSONObject(result);
                    if (!resultJson.isNull(Constants.SUCCESS_KEY)) {
                        updateDeviceSettingByKey(deviceSetting);
                    }
                }
            } else {
                updateDeviceSettingByKey(deviceSetting);
            }
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = "ChatMessageReceiver";
            }
            Log.d(errorMsg, errorMsg);
        }
    }

    public void getChatMessageStatusList(Context context, User user) {
        DeviceSetting deviceSetting = getDeviceSettingByKey(Constants.DEVICE_SETTING_KEY_LAST_GET_CHAT_MESSAGE_STATUS_LIST);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        try {
            JSONObject jsonObject = new JSONObject();
            List<ChatMessage> chatMessageList = coreService.getChatGroupListNotReadNotDelivered(user.getServerUserId());

            jsonObject.put("username", user.getUsername());
            jsonObject.put("tokenPass", user.getBisPassword());
            JSONArray serverMessageIdList = new JSONArray();
            Map<Long, ChatMessage> chatMessageMap = new HashMap<Long, ChatMessage>();
            for (ChatMessage chatMessage : chatMessageList) {
                serverMessageIdList.put(chatMessage.getServerMessageId());
                chatMessageMap.put(chatMessage.getServerMessageId(), chatMessage);
            }

            jsonObject.put("messageIdList", serverMessageIdList);
            MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, context);
            String result = postJsonService.sendData("getChatMessageStatusList", jsonObject, true);
            if (result != null) {
                JSONObject resultJson = new JSONObject(result);
                if (!resultJson.isNull(Constants.SUCCESS_KEY)) {
                    if (!resultJson.isNull(Constants.RESULT_KEY)) {
                        JSONObject resultJsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                        if (!resultJsonObject.isNull("chatMessageStatusList")) {
                            JSONArray chatMessageStatusJsonArray = resultJsonObject.getJSONArray("chatMessageStatusList");
                            for (int i = 0; i < chatMessageStatusJsonArray.length(); i++) {
                                JSONObject chatMessageStatusJsonObject = chatMessageStatusJsonArray.getJSONObject(i);
                                if (!chatMessageStatusJsonObject.isNull("id")) {
                                    Long serverMessageId = chatMessageStatusJsonObject.getLong("id");
                                    JSONObject statusesJsonObject = chatMessageStatusJsonObject.getJSONObject("statuses");
                                    if (chatMessageMap.containsKey(serverMessageId)) {
                                        ChatMessage chatMessage = chatMessageMap.get(serverMessageId);
                                        chatMessage.setDeliverIs(statusesJsonObject.getBoolean("deliverIs"));
                                        chatMessage.setReadIs(statusesJsonObject.getBoolean("readIs"));
                                        if (!statusesJsonObject.isNull("deliverDate")) {
                                            chatMessage.setDeliverDate(simpleDateFormat.parse(statusesJsonObject.getString("deliverDate")));
                                        }
                                        if (!statusesJsonObject.isNull("readDate")) {
                                            chatMessage.setReadDate(simpleDateFormat.parse(statusesJsonObject.getString("readDate")));
                                        }
                                        coreService.updateChatMessage(chatMessage);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            updateDeviceSettingByKey(deviceSetting);
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = "ChatMessageReceiver";
            }
            Log.d(errorMsg, errorMsg);
        }
    }

    public void getChatMessageStatusListFromNotify(Context context, User user) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        try {
            JSONObject jsonObject = new JSONObject();
            List<ChatMessage> chatMessageList = coreService.getChatGroupListNotReadNotDelivered(user.getServerUserId());

            jsonObject.put("username", user.getUsername());
            jsonObject.put("tokenPass", user.getBisPassword());
            JSONArray serverMessageIdList = new JSONArray();
            Map<Long, ChatMessage> chatMessageMap = new HashMap<Long, ChatMessage>();
            for (ChatMessage chatMessage : chatMessageList) {
                serverMessageIdList.put(chatMessage.getServerMessageId());
                chatMessageMap.put(chatMessage.getServerMessageId(), chatMessage);
            }

            jsonObject.put("messageIdList", serverMessageIdList);
            MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, context);
            String result = postJsonService.sendData("getChatMessageStatusList", jsonObject, true);
            if (result != null) {
                JSONObject resultJson = new JSONObject(result);
                if (!resultJson.isNull(Constants.SUCCESS_KEY)) {
                    if (!resultJson.isNull(Constants.RESULT_KEY)) {
                        JSONObject resultJsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                        if (!resultJsonObject.isNull("chatMessageStatusList")) {
                            JSONArray chatMessageStatusJsonArray = resultJsonObject.getJSONArray("chatMessageStatusList");
                            for (int i = 0; i < chatMessageStatusJsonArray.length(); i++) {
                                JSONObject chatMessageStatusJsonObject = chatMessageStatusJsonArray.getJSONObject(i);
                                if (!chatMessageStatusJsonObject.isNull("id")) {
                                    Long serverMessageId = chatMessageStatusJsonObject.getLong("id");
                                    JSONObject statusesJsonObject = chatMessageStatusJsonObject.getJSONObject("statuses");
                                    if (chatMessageMap.containsKey(serverMessageId)) {
                                        ChatMessage chatMessage = chatMessageMap.get(serverMessageId);
                                        chatMessage.setDeliverIs(statusesJsonObject.getBoolean("deliverIs"));
                                        chatMessage.setReadIs(statusesJsonObject.getBoolean("readIs"));
                                        if (!statusesJsonObject.isNull("deliverDate")) {
                                            chatMessage.setDeliverDate(simpleDateFormat.parse(statusesJsonObject.getString("deliverDate")));
                                        }
                                        if (!statusesJsonObject.isNull("readDate")) {
                                            chatMessage.setReadDate(simpleDateFormat.parse(statusesJsonObject.getString("readDate")));
                                        }
                                        coreService.updateChatMessage(chatMessage);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = "ChatMessageReceiver";
            }
            Log.d(errorMsg, errorMsg);
        }
    }

    public void downloadAttachFile(Context context, CoreService coreService, ChatMessage chatMessage, User user) {
        String path = Environment.getExternalStorageDirectory().toString() + Constants.DEFAULT_OUT_PUT_DIR + Constants.DEFAULT_IMG_OUT_PUT_DIR;
        File dir = new File(path);
        if (!dir.exists()) {
            boolean b = dir.mkdirs();
        }

        String filePostfix = chatMessage.getAttachFileUserFileName().substring(chatMessage.getAttachFileUserFileName().indexOf("."), chatMessage.getAttachFileUserFileName().length());

        String filePathUrl = path + "/" + chatMessage.getId() + filePostfix;

        File file = new File(filePathUrl); // the File to save to

        if (!file.exists()) {
            chatMessage.setAttachFileReceivedSize(0);
            coreService.updateChatMessage(chatMessage);
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", user.getUsername());
            jsonObject.put("tokenPass", user.getBisPassword());
            jsonObject.put("id", chatMessage.getServerMessageId());
            jsonObject.put("downloadedSize", chatMessage.getAttachFileReceivedSize());
            System.out.println("chatMessage.getServerMessageId()=" + chatMessage.getServerMessageId());

            MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, context);
            String result = postJsonService.sendData("downloadAttachFile", jsonObject, true);
            if (result != null) {
                JSONObject resultJson = new JSONObject(result);
                if (!resultJson.isNull(Constants.SUCCESS_KEY)) {
                    if (!resultJson.isNull(Constants.RESULT_KEY)) {
                        JSONObject resultJsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                        if (!resultJsonObject.isNull("fileBytes")) {
                            String attacheFileChecksum = resultJsonObject.getString("attacheFileChecksum");
                            Integer attachFileSize = resultJsonObject.getInt("attachFileSize");
                            byte[] bytes = new byte[0];
                            try {
                                JSONArray fileBytesJsonArray = resultJsonObject.getJSONArray("fileBytes");
                                bytes = new byte[fileBytesJsonArray.length()];
                                for (int i = 0; i < fileBytesJsonArray.length(); i++) {
                                    bytes[i] = Integer.valueOf(fileBytesJsonArray.getInt(i)).byteValue();
                                }


                                OutputStream outputStream = new FileOutputStream(file, true);

                                outputStream.write(bytes);
                                outputStream.flush();
                                outputStream.close();
                                chatMessage.setAttachFileLocalPath(filePathUrl);
                                chatMessage.setAttachFileSize(attachFileSize);
                                chatMessage.setAttachFileReceivedSize(Long.valueOf(new File(filePathUrl).length()).intValue());

                                String newChecksum = ImageUtil.getMD5Checksum(chatMessage.getAttachFileLocalPath());

                                coreService.updateChatMessage(chatMessage);

                                if (!attacheFileChecksum.equals(newChecksum)) {
                                    downloadAttachFile(context, coreService, chatMessage, user);
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("ChatMessageReceiver", e.getMessage());
        }
    }

    public void sendComplaintReportList(Context context, User user) {
        DeviceSetting deviceSetting = getDeviceSettingByKey(Constants.DEVICE_SETTING_KEY_LAST_COMPLAINT_REPORT_SYNC_DATE);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        ComplaintReport complaintReportFS = new ComplaintReport();
        complaintReportFS.setUserReportId(user.getServerUserId());
        List<ComplaintReport> complaintReportList = coreService.getUnSentComplaintReportList(complaintReportFS);
        if (!complaintReportList.isEmpty()) {
            for (ComplaintReport complaintReport : complaintReportList) {
                sendComplaintReport(context, coreService, complaintReport, user);
            }

        }
        updateDeviceSettingByKey(deviceSetting);
    }

    public void sendComplaintReport(Context context, CoreService coreService, ComplaintReport complaintReport, User user) {
        complaintReport.setSendingStatusEn(SendingStatusEn.InProgress.ordinal());
        complaintReport.setSendingStatusDate(new Date());
        coreService.updateComplaintReport(complaintReport);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", user.getUsername());
            jsonObject.put("tokenPass", user.getBisPassword());
            JSONObject complaintReportJsonObject = new JSONObject();
            complaintReportJsonObject.put("id", complaintReport.getId());

            if (complaintReport.getEntityId() != null && !complaintReport.getEntityId().equals((long) 0)) {
                complaintReportJsonObject.put("entityId", complaintReport.getEntityId());
            } else if (complaintReport.getIdentifier() != null && !complaintReport.getIdentifier().isEmpty()) {
                complaintReportJsonObject.put("identifier", complaintReport.getIdentifier());
                System.out.println("identifier = " + complaintReport.getIdentifier());
            }
            complaintReportJsonObject.put("entityNameEn", complaintReport.getEntityNameEn());
            complaintReportJsonObject.put("reportStr", complaintReport.getReportStr());
            complaintReportJsonObject.put("reportCode", complaintReport.getReportCode());
            complaintReportJsonObject.put("reportDate", simpleDateFormat.format(complaintReport.getReportDate()));
            complaintReportJsonObject.put("xLatitude", complaintReport.getXLatitude());
            complaintReportJsonObject.put("yLongitude", complaintReport.getYLongitude());
            jsonObject.put("complaintReport", complaintReportJsonObject);
            System.out.println("jsonObject==" + jsonObject);
            MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, context);
            String result = postJsonService.sendData("saveComplaintReport", jsonObject, true);
            if (result != null) {
                JSONObject resultJson = new JSONObject(result);
                if (!resultJson.isNull(Constants.SUCCESS_KEY)) {
                    if (!resultJson.isNull(Constants.RESULT_KEY)) {
                        JSONObject resultJsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                        if (!resultJsonObject.isNull("savedComplaintReport")) {
                            JSONObject savedComplaintReportJsonObject = resultJsonObject.getJSONObject("savedComplaintReport");
                            if (!savedComplaintReportJsonObject.isNull("id")) {
                                complaintReport.setServerId(savedComplaintReportJsonObject.getLong("id"));
                            }

                            if (!savedComplaintReportJsonObject.isNull("dateCreation")) {
                                complaintReport.setDeliverDate(simpleDateFormat.parse(savedComplaintReportJsonObject.getString("dateCreation")));
                            }
                        }
                        complaintReport.setDeliverIs(Boolean.TRUE);
                        complaintReport.setSendingStatusEn(SendingStatusEn.Sent.ordinal());
                        complaintReport.setSendingStatusDate(new Date());
                        coreService.updateComplaintReport(complaintReport);
                        System.out.println("getDeliverIs====" + complaintReport.getDeliverIs());

                        List<AttachFile> attachFileList = coreService.getPendingAttachFileByEntityId(EntityNameEn.ComplaintReport, complaintReport.getId());

                        for (AttachFile attachFile : attachFileList) {
                            attachFile.setServerEntityId(complaintReport.getServerId());
                            attachFile.setSendingStatusEn(SendingStatusEn.InProgress.ordinal());
                            coreService.updateAttachFile(attachFile);
                            //resumeAttachFile(context, coreService, attachFile, user);
                            System.out.println("attachFile====" + attachFile);
                        }
                    }
                } else {
                    complaintReport.setSendingStatusEn(SendingStatusEn.Fail.ordinal());
                    complaintReport.setSendingStatusDate(new Date());
                    coreService.updateComplaintReport(complaintReport);
                }
            }
        } catch (Exception e) {
            Log.d("ChatMessageReceiver", e.getMessage());
            complaintReport.setSendingStatusEn(SendingStatusEn.Pending.ordinal());
            complaintReport.setSendingStatusDate(new Date());
            coreService.updateComplaintReport(complaintReport);
        }
    }

    public void getSurveyFormList(Context context, User user) {
        DeviceSetting deviceSetting = getDeviceSettingByKey(Constants.DEVICE_SETTING_KEY_LAST_GET_SURVEY_FORM_LIST);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", user.getUsername());
            jsonObject.put("tokenPass", user.getBisPassword());
            //jsonObject.put("lastUpdateDate", deviceSetting.getValue());
            jsonObject.put("formType", 0);
            if (deviceSetting.getValue() != null) {
                jsonObject.put("lastUpdateDate", deviceSetting.getValue());
            }

            MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, context);
            String result = postJsonService.sendData("getUserSurveyFormList", jsonObject, true);
            System.out.println("====resultGetSurveyFormListForm1111=" + result);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            if (result != null) {
                logLargeString(result);
                JSONObject resultJson = new JSONObject(result);
                if (!resultJson.isNull(Constants.SUCCESS_KEY)) {
                    if (!resultJson.isNull(Constants.RESULT_KEY)) {
                        JSONObject resultJsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                        if (!resultJsonObject.isNull("surveyFormList")) {
                            JSONArray surveyFormJsonArray = resultJsonObject.getJSONArray("surveyFormList");
                            for (int i = 0; i < surveyFormJsonArray.length(); i++) {
                                JSONObject surveyFormJsonObject = surveyFormJsonArray.getJSONObject(i);
                                if (!surveyFormJsonObject.isNull("id")) {
                                    Long surveyFormId = surveyFormJsonObject.getLong("id");
                                    SurveyForm surveyForm = coreService.getSurveyFormById(surveyFormId);
                                    if (surveyForm == null) {
                                        surveyForm = new SurveyForm();
                                        surveyForm.setId(surveyFormId);
                                        surveyForm.setStatusEn(SurveyFormStatusEn.New.ordinal());
                                        surveyForm.setStatusDate(new Date());
                                    }

                                    if (!surveyFormJsonObject.isNull("name")) {
                                        surveyForm.setName(surveyFormJsonObject.getString("name"));
                                    }
                                    if (!surveyFormJsonObject.isNull("startDate")) {
                                        String strStartDate = surveyFormJsonObject.getString("startDate");
                                        try {
                                            surveyForm.setStartDate(simpleDateFormat.parse(strStartDate));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if (!surveyFormJsonObject.isNull("endDate")) {
                                        String strEndDate = surveyFormJsonObject.getString("endDate");
                                        try {
                                            surveyForm.setEndDate(simpleDateFormat.parse(strEndDate));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if (!surveyFormJsonObject.isNull("minScore")) {
                                        surveyForm.setMinScore(surveyFormJsonObject.getInt("minScore"));
                                    }
                                    if (!surveyFormJsonObject.isNull("maxScore")) {
                                        surveyForm.setMaxScore(surveyFormJsonObject.getInt("maxScore"));
                                    }
                                    if (!surveyFormJsonObject.isNull("status")) {
                                        surveyForm.setFormStatus(surveyFormJsonObject.getInt("status"));
                                    }
                                    if (surveyForm.getStatusEn() == null) {
                                        surveyForm.setStatusEn(SurveyFormStatusEn.New.ordinal());
                                        surveyForm.setStatusDate(new Date());
                                    }

                                    if (!surveyFormJsonObject.isNull("inputValuesDefault")) {
                                        System.out.println("inputValuesDefault===" + surveyFormJsonObject.getString("inputValuesDefault"));
                                        String inputValuesDefault = surveyFormJsonObject.getString("inputValuesDefault");
                                        surveyForm.setInputValuesDefault(inputValuesDefault);
                                        //JSONObject defaultJSONObject = surveyFormJsonObject.getJSONObject("default");
                                        /*JSONObject inputValuesDefaultJson = new JSONObject(inputValuesDefault);
                                        Iterator<String> iterator =  inputValuesDefaultJson.keys();
                                        while (iterator.hasNext()) {
                                            String key = iterator.next();
                                            System.out.println("-----key="+key+"---values="+inputValuesDefaultJson.get(key));
                                        }*/
                                        /*if (!defaultJSONObject.isNull("default")) {
                                            System.out.println("values===" + defaultJSONObject.getInt("default"));
                                        }*/
                                    }
                                    coreService.saveOrUpdateSurveyForm(surveyForm);

                                    FormQuestionGroup formQuestionGroup;
                                    if (!surveyFormJsonObject.isNull("surveyFormQuestionList")) {
                                        JSONArray surveyFormQuestionJsonArray = surveyFormJsonObject.getJSONArray("surveyFormQuestionList");
                                        for (int j = 0; j < surveyFormQuestionJsonArray.length(); j++) {
                                            JSONObject surveyFormQuestionJSONObject = surveyFormQuestionJsonArray.getJSONObject(j);
                                            if (!surveyFormQuestionJSONObject.isNull("id")) {

                                                Long surveyFormQuestionId = surveyFormQuestionJSONObject.getLong("id");
                                                Long surveyFormQuestionGroupId = surveyFormQuestionJSONObject.getLong("groupId");
                                                String surveyFormQuestionGroupName = surveyFormQuestionJSONObject.getString("groupName");

                                                long id = Long.parseLong(surveyForm.getId() + "" + surveyFormQuestionGroupId);
                                                formQuestionGroup = coreService.getCheckListFormQuestionGroupById(id);
                                                if (formQuestionGroup == null) {
                                                    formQuestionGroup = new FormQuestionGroup();
                                                    formQuestionGroup.setGroupName(surveyFormQuestionGroupName);
                                                    formQuestionGroup.setId(id);
                                                    formQuestionGroup.setGroupId(surveyFormQuestionGroupId);
                                                    formQuestionGroup.setFormId(surveyForm.getId());
                                                    coreService.saveOrUpdateCheckListFormQuestionGroup(formQuestionGroup);
                                                    System.out.println("formQuestionGroup====One=====" + formQuestionGroup.getId());
                                                }

                                                SurveyFormQuestion surveyFormQuestion = coreService.getSurveyFormQuestionById(surveyFormQuestionId);
                                                SurveyFormQuestionTemp surveyFormQuestionTemp = coreService.getSurveyFormQuestionTempById(surveyFormQuestionId);

                                                if (surveyFormQuestion == null) {
                                                    surveyFormQuestion = new SurveyFormQuestion();
                                                    surveyFormQuestion.setId(surveyFormQuestionId);
                                                    surveyFormQuestion.setSurveyFormId(surveyForm.getId());
                                                    surveyFormQuestion.setInputValuesDefault(surveyForm.getInputValuesDefault());
                                                    surveyFormQuestion.setFormQuestionGroupId(surveyFormQuestionGroupId);
                                                }
                                                if (!surveyFormQuestionJSONObject.isNull("surveyQuestions")) {
                                                    JSONObject surveyQuestionJSONObject = surveyFormQuestionJSONObject.getJSONObject("surveyQuestions");
                                                    if (!surveyQuestionJSONObject.isNull("question")) {
                                                        surveyFormQuestion.setQuestion(surveyQuestionJSONObject.getString("question"));
                                                    }
                                                    if (!surveyQuestionJSONObject.isNull("answerTypeEn")) {
                                                        surveyFormQuestion.setAnswerTypeEn(surveyQuestionJSONObject.getInt("answerTypeEn"));
                                                    }
                                                }
                                                coreService.saveOrUpdateSurveyFormQuestion(surveyFormQuestion);

                                                if (surveyFormQuestionTemp == null) {
                                                    surveyFormQuestionTemp = new SurveyFormQuestionTemp();
                                                    surveyFormQuestionTemp.setId(surveyFormQuestionId);
                                                    surveyFormQuestionTemp.setSurveyFormId(surveyForm.getId());
                                                    surveyFormQuestionTemp.setFormQuestionGroupId(surveyFormQuestionGroupId);

                                                    if (!surveyFormQuestionJSONObject.isNull("surveyQuestions")) {
                                                        JSONObject surveyQuestionJSONObject = surveyFormQuestionJSONObject.getJSONObject("surveyQuestions");
                                                        if (!surveyQuestionJSONObject.isNull("question")) {
                                                            surveyFormQuestionTemp.setQuestion(surveyQuestionJSONObject.getString("question"));
                                                        }
                                                        if (!surveyQuestionJSONObject.isNull("answerTypeEn")) {
                                                            surveyFormQuestionTemp.setAnswerTypeEn(surveyQuestionJSONObject.getInt("answerTypeEn"));
                                                        }
                                                    }

                                                    coreService.saveOrUpdateSurveyFormQuestionTemp(surveyFormQuestionTemp);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (JSONException e) {
            Log.d("ChatMessageReceiver", e.getMessage());
        } catch (SocketTimeoutException e) {
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = "SocketTimeoutException";
            }
            Log.d(errorMsg, errorMsg);

        } catch (WebServiceException | SocketException e) {
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = "ChatMessageReceiver";
            }
            Log.d(errorMsg, errorMsg);
        }
        updateDeviceSettingByKey(deviceSetting);
    }

    public void sendSurveyFormList(Context context, User user) {
        DeviceSetting deviceSetting = getDeviceSettingByKey(Constants.DEVICE_SETTING_KEY_LAST_SURVEY_FORM_SEND_SYNC_DATE);
        SurveyForm tmpSurveyFormFS = new SurveyForm();
        tmpSurveyFormFS.setStatusEn(SurveyFormStatusEn.Complete.ordinal());
        List<SurveyForm> surveyFormList = coreService.getUnSentSurveyFormList(tmpSurveyFormFS);
        if (!surveyFormList.isEmpty()) {
            for (SurveyForm surveyForm : surveyFormList) {
                sendSurveyForm(context, coreService, surveyForm, user);
            }
        }
        updateDeviceSettingByKey(deviceSetting);
    }

    public void sendSurveyForm(Context context, CoreService coreService, SurveyForm surveyForm, User user) {
        surveyForm.setSendingStatusEn(SendingStatusEn.InProgress.ordinal());
        surveyForm.setSendingStatusDate(new Date());
        coreService.updateSurveyForm(surveyForm);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", user.getUsername());
            jsonObject.put("tokenPass", user.getBisPassword());
            JSONObject surveyFormJsonObject = new JSONObject();
            surveyFormJsonObject.put("id", surveyForm.getId());

            if (surveyForm.getServerAnswerInfoId() != null) {
                surveyFormJsonObject.put("serverAnswerInfoId", surveyForm.getServerAnswerInfoId());
            }
            if (surveyForm.getStatusDate() != null) {
                surveyFormJsonObject.put("completeDate", simpleDateFormat.format(surveyForm.getStatusDate()));
            }
            if (surveyForm.getXLatitude() != null) {
                surveyFormJsonObject.put("xLatitude", surveyForm.getXLatitude());
            }
            if (surveyForm.getYLongitude() != null) {
                surveyFormJsonObject.put("yLongitude", surveyForm.getYLongitude());
            }

            List<SurveyFormQuestion> surveyFormQuestionList = surveyForm.getSurveyFormQuestionList();
            JSONArray surveyFormQuestionJsonArray = new JSONArray();
            for (SurveyFormQuestion surveyFormQuestion : surveyFormQuestionList) {
                JSONObject surveyFormQuestionJsonObject = new JSONObject();
                surveyFormQuestionJsonObject.put("id", surveyFormQuestion.getId());
                if (surveyFormQuestion.getServerAnswerId() != null) {
                    surveyFormQuestionJsonObject.put("serverAnswerId", surveyFormQuestion.getServerAnswerId());
                }
                if (surveyFormQuestion.getAnswerInt() != null) {
                    surveyFormQuestionJsonObject.put("answerInt", surveyFormQuestion.getAnswerInt());
                }
                if (surveyFormQuestion.getAnswerStr() != null) {
                    surveyFormQuestionJsonObject.put("answerStr", surveyFormQuestion.getAnswerStr());
                }
                surveyFormQuestionJsonArray.put(surveyFormQuestionJsonObject);
            }
            surveyFormJsonObject.put("surveyFormQuestionList", surveyFormQuestionJsonArray);
            jsonObject.put("surveyForm", surveyFormJsonObject);
            MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, context);
            String result = postJsonService.sendData("updateSurveyFormAnswer", jsonObject, true);
            if (result != null) {
                JSONObject resultJson = new JSONObject(result);
                if (!resultJson.isNull(Constants.SUCCESS_KEY)) {
                    if (!resultJson.isNull(Constants.RESULT_KEY)) {
                        JSONObject resultJsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                        if (!resultJsonObject.isNull("savedSurveyForm")) {
                            JSONObject savedSurveyFormJsonObject = resultJsonObject.getJSONObject("savedSurveyForm");
                            if (!savedSurveyFormJsonObject.isNull("id")) {
                                surveyForm.setServerAnswerInfoId(savedSurveyFormJsonObject.getLong("id"));

                                if (!savedSurveyFormJsonObject.isNull("savedSurveyFormQuestionList")) {
                                    JSONArray savedSurveyFormQuestionJsonArray = savedSurveyFormJsonObject.getJSONArray("savedSurveyFormQuestionList");
                                    for (int i = 0; i < savedSurveyFormQuestionJsonArray.length(); i++) {
                                        JSONObject savedSurveyFormQuestionJsonObject = savedSurveyFormQuestionJsonArray.getJSONObject(i);
                                        if (!savedSurveyFormQuestionJsonObject.isNull("id")) {
                                            Long serverAnswerId = savedSurveyFormQuestionJsonObject.getLong("id");
                                            Long surveyFormQuestionId = savedSurveyFormQuestionJsonObject.getLong("surveyFormQuestionId");
                                            for (SurveyFormQuestion surveyFormQuestion : surveyFormQuestionList) {
                                                if (surveyFormQuestion.getId().equals(surveyFormQuestionId)) {
                                                    surveyFormQuestion.setServerAnswerId(serverAnswerId);
                                                    coreService.updateSurveyFormQuestion(surveyFormQuestion);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }

                                List<AttachFile> attachFileList = coreService.getPendingAttachFileByEntityId(EntityNameEn.SurveyFormAnswerInfo, surveyForm.getId());
                                surveyForm.setSendingStatusEn(SendingStatusEn.Sent.ordinal());
                                surveyForm.setSendingStatusDate(new Date());
                                coreService.updateSurveyForm(surveyForm);

                                for (AttachFile attachFile : attachFileList) {
                                    attachFile.setServerEntityId(surveyForm.getServerAnswerInfoId());
                                    attachFile.setSendingStatusEn(SendingStatusEn.InProgress.ordinal());
                                    coreService.updateAttachFile(attachFile);
                                    //resumeAttachFile(context, coreService, attachFile, user);
                                }
                            }
                        }
                    }
                } else {
                    surveyForm.setSendingStatusEn(SendingStatusEn.Fail.ordinal());
                    surveyForm.setSendingStatusDate(new Date());
                    coreService.updateSurveyForm(surveyForm);
                }
            }
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = "SurveyFormReceiver";
            }
            Log.d(errorMsg, errorMsg);
            surveyForm.setSendingStatusEn(SendingStatusEn.Pending.ordinal());
            surveyForm.setSendingStatusDate(new Date());
            coreService.updateSurveyForm(surveyForm);
        }
    }

    public void logLargeString(String str) {
        String Tag = "tagtag";
        if (str.length() > 3000) {
            Log.i(Tag, str.substring(0, 3000));
            logLargeString(str.substring(3000));
        } else {
            Log.i(Tag, str); // continuation
        }
    }

    public void getCheckFormList(Context context, User user) {
        //coreService.deleteFormQuestionGroup();
        DeviceSetting deviceSetting = getDeviceSettingByKey(Constants.DEVICE_SETTING_KEY_LAST_GET_CHECK_FORM_LIST);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", user.getUsername());
            jsonObject.put("tokenPass", user.getBisPassword());
            //jsonObject.put("lastUpdateDate", deviceSetting.getValue());
            jsonObject.put("formType", 1);

            if (deviceSetting.getValue() != null) {
                jsonObject.put("lastUpdateDate", deviceSetting.getValue());
            }

            MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, context);
            String result = postJsonService.sendData("getUserSurveyFormList", jsonObject, true);
            System.out.println("====resultGetSurveyFormList=" + result);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            if (result != null) {
                JSONObject resultJson = new JSONObject(result);
                if (!resultJson.isNull(Constants.SUCCESS_KEY)) {
                    if (!resultJson.isNull(Constants.RESULT_KEY)) {
                        JSONObject resultJsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                        if (!resultJsonObject.isNull("surveyFormList")) {
                            JSONArray surveyFormJsonArray = resultJsonObject.getJSONArray("surveyFormList");
                            for (int i = 0; i < surveyFormJsonArray.length(); i++) {
                                JSONObject surveyFormJsonObject = surveyFormJsonArray.getJSONObject(i);
                                if (!surveyFormJsonObject.isNull("id")) {
                                    System.out.println("id=====" + result);
                                    Long surveyFormId = surveyFormJsonObject.getLong("id");
                                    Form form = coreService.getCheckListFormById(surveyFormId);
                                    if (form == null) {
                                        form = new Form();
                                        form.setId(surveyFormId);
                                        form.setStatusEn(SurveyFormStatusEn.New.ordinal());
                                        form.setStatusDate(new Date());
                                    }

                                    if (!surveyFormJsonObject.isNull("name")) {
                                        form.setName(surveyFormJsonObject.getString("name"));
                                    }
                                    if (!surveyFormJsonObject.isNull("startDate")) {
                                        String strStartDate = surveyFormJsonObject.getString("startDate");
                                        try {
                                            form.setStartDate(simpleDateFormat.parse(strStartDate));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if (!surveyFormJsonObject.isNull("endDate")) {
                                        String strEndDate = surveyFormJsonObject.getString("endDate");
                                        try {
                                            form.setEndDate(simpleDateFormat.parse(strEndDate));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if (!surveyFormJsonObject.isNull("minScore")) {
                                        form.setMinScore(surveyFormJsonObject.getInt("minScore"));
                                    }
                                    if (!surveyFormJsonObject.isNull("maxScore")) {
                                        form.setMaxScore(surveyFormJsonObject.getInt("maxScore"));
                                    }

                                    if (!surveyFormJsonObject.isNull("statusEn")) {
                                        form.setFormStatus(surveyFormJsonObject.getInt("statusEn"));
                                    }

                                    if (!surveyFormJsonObject.isNull("inputValuesDefault")) {
                                        System.out.println("inputValuesDefault===" + surveyFormJsonObject.getString("inputValuesDefault"));
                                        String inputValuesDefault = surveyFormJsonObject.getString("inputValuesDefault");
                                        form.setInputValuesDefault(inputValuesDefault);
                                        //JSONObject defaultJSONObject = surveyFormJsonObject.getJSONObject("default");
                                        /*JSONObject inputValuesDefaultJson = new JSONObject(inputValuesDefault);
                                        Iterator<String> iterator =  inputValuesDefaultJson.keys();
                                        while (iterator.hasNext()) {
                                            String key = iterator.next();
                                            System.out.println("-----key="+key+"---values="+inputValuesDefaultJson.get(key));
                                        }*/
                                        /*if (!defaultJSONObject.isNull("default")) {
                                            System.out.println("values===" + defaultJSONObject.getInt("default"));
                                        }*/
                                    }

                                    if (form.getStatusEn() == null) {
                                        form.setStatusEn(SurveyFormStatusEn.New.ordinal());
                                        form.setStatusDate(new Date());
                                    }
                                    form = coreService.saveOrUpdateCheckListForm(form);
                                    FormQuestionGroup formQuestionGroup;
                                    FormQuestionGroupForm formQuestionGroupForm;

                                    if (!surveyFormJsonObject.isNull("surveyFormQuestionList")) {
                                        JSONArray surveyFormQuestionJsonArray = surveyFormJsonObject.getJSONArray("surveyFormQuestionList");
                                        for (int j = 0; j < surveyFormQuestionJsonArray.length(); j++) {
                                            JSONObject surveyFormQuestionJSONObject = surveyFormQuestionJsonArray.getJSONObject(j);
                                            if (!surveyFormQuestionJSONObject.isNull("id")) {

                                                Long surveyFormQuestionId = surveyFormQuestionJSONObject.getLong("id");
                                                Long surveyFormQuestionGroupId = surveyFormQuestionJSONObject.getLong("groupId");
                                                String surveyFormQuestionGroupName = surveyFormQuestionJSONObject.getString("groupName");
                                                FormQuestion formQuestion = coreService.getCheckListFormQuestionById(surveyFormQuestionId);
                                                FormTemp formTemp = coreService.getCheckListFormTempById(surveyFormQuestionId);

                                                long id = Long.parseLong(form.getId() + "" + surveyFormQuestionGroupId);
                                                System.out.println(" long id===" + id);
                                                formQuestionGroup = coreService.getCheckListFormQuestionGroupById(id);
                                                if (formQuestionGroup == null) {
                                                    formQuestionGroup = new FormQuestionGroup();
                                                    formQuestionGroup.setGroupName(surveyFormQuestionGroupName);
                                                    formQuestionGroup.setId(id);
                                                    formQuestionGroup.setGroupId(surveyFormQuestionGroupId);
                                                    formQuestionGroup.setFormId(form.getId());
                                                    coreService.saveOrUpdateCheckListFormQuestionGroup(formQuestionGroup);
                                                    System.out.println("formQuestionGroup====One=====" + formQuestionGroup.getId());
                                                }

                                                if (formQuestion == null) {
                                                    formQuestion = new FormQuestion();
                                                    formQuestion.setId(surveyFormQuestionId);
                                                    formQuestion.setFormId(form.getId());
                                                    formQuestion.setInputValuesDefault(form.getInputValuesDefault());

                                                    if (!surveyFormQuestionJSONObject.isNull("surveyQuestions")) {
                                                        JSONObject surveyQuestionJSONObject = surveyFormQuestionJSONObject.getJSONObject("surveyQuestions");
                                                        if (!surveyQuestionJSONObject.isNull("question")) {
                                                            formQuestion.setQuestion(surveyQuestionJSONObject.getString("question"));
                                                            formQuestion.setFormQuestionGroupId(surveyFormQuestionGroupId);
                                                        }
                                                        if (!surveyQuestionJSONObject.isNull("answerTypeEn")) {
                                                            formQuestion.setAnswerTypeEn(surveyQuestionJSONObject.getInt("answerTypeEn"));
                                                            // formQuestion.setAnswerTypeEn(0);
                                                        }
                                                    }
                                                    coreService.saveOrUpdateCheckListFormQuestion(formQuestion);
                                                }

                                                if (formTemp == null) {
                                                    formTemp = new FormTemp();
                                                    formTemp.setId(surveyFormQuestionId);
                                                    formTemp.setFormId(form.getId());
                                                    formTemp.setInputValuesDefault(form.getInputValuesDefault());
                                                    //formTemp.setAnswerInt(1);

                                                    if (!surveyFormQuestionJSONObject.isNull("surveyQuestions")) {
                                                        JSONObject surveyQuestionJSONObject = surveyFormQuestionJSONObject.getJSONObject("surveyQuestions");
                                                        if (!surveyQuestionJSONObject.isNull("question")) {
                                                            formTemp.setQuestion(surveyQuestionJSONObject.getString("question"));
                                                            formTemp.setFormQuestionGroupId(surveyFormQuestionGroupId);
                                                        }
                                                        if (!surveyQuestionJSONObject.isNull("answerTypeEn")) {
                                                            formTemp.setAnswerTypeEn(surveyQuestionJSONObject.getInt("answerTypeEn"));
                                                            //formTemp.setAnswerTypeEn(0);
                                                        }
                                                    }
                                                    coreService.saveOrUpdateCheckListFormTemp(formTemp);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (JSONException e) {
            Log.d("ChatMessageReceiver", e.getMessage());
        } catch (SocketTimeoutException e) {
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = "SocketTimeoutException";
            }
            Log.d(errorMsg, errorMsg);

        } catch (WebServiceException | SocketException e) {
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = "ChatMessageReceiver";
            }
            Log.d(errorMsg, errorMsg);
        }
        updateDeviceSettingByKey(deviceSetting);
    }

    public void sendCheckFormList(Context context, User user) {

        DeviceSetting deviceSetting = getDeviceSettingByKey(Constants.DEVICE_SETTING_KEY_LAST_SURVEY_FORM_SEND_SYNC_DATE);
        FormAnswer tmpFormAnswerFS = new FormAnswer();
        tmpFormAnswerFS.setStatusEn(SurveyFormStatusEn.Complete.ordinal());
        List<FormAnswer> formAnswerList = coreService.getUnSentFormAnswerList(tmpFormAnswerFS);
        if (!formAnswerList.isEmpty()) {
            for (FormAnswer formAnswer : formAnswerList) {
                sendCheckForm(context, coreService, formAnswer, user);
            }
        }
        updateDeviceSettingByKey(deviceSetting);
    }

    public void sendCheckForm(Context context, CoreService coreService, FormAnswer formAnswer, User user) {
        formAnswer.setSendingStatusEn(SendingStatusEn.InProgress.ordinal());
        formAnswer.setSendingStatusDate(new Date());
        coreService.updateFormAnswer(formAnswer);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", user.getUsername());
            jsonObject.put("tokenPass", user.getBisPassword());
            JSONObject surveyFormJsonObject = new JSONObject();
            surveyFormJsonObject.put("id", formAnswer.getFormId());

            //***************************************************
            if (formAnswer.getCarId() != null) {
                surveyFormJsonObject.put("plateText", formAnswer.getCarId());
            }

            if (formAnswer.getLineId() != null) {
                surveyFormJsonObject.put("lineCode", formAnswer.getLineId());
            }
            //***************************************************

            if (formAnswer.getServerAnswerInfoId() != null) {
                surveyFormJsonObject.put("serverAnswerInfoId", formAnswer.getServerAnswerInfoId());
            }
            if (formAnswer.getStatusDate() != null) {
                surveyFormJsonObject.put("completeDate", simpleDateFormat.format(formAnswer.getStatusDate()));
            }
            if (formAnswer.getXLatitude() != null) {
                surveyFormJsonObject.put("xLatitude", formAnswer.getXLatitude());
            }
            if (formAnswer.getYLongitude() != null) {
                surveyFormJsonObject.put("yLongitude", formAnswer.getYLongitude());
            }

            List<FormItemAnswer> formItemAnswerList = formAnswer.getFormItemAnswerListFormAnswer();
            JSONArray surveyFormQuestionJsonArray = new JSONArray();
            for (FormItemAnswer formItemAnswer : formItemAnswerList) {
                JSONObject surveyFormQuestionJsonObject = new JSONObject();
                surveyFormQuestionJsonObject.put("id", formItemAnswer.getFormQuestionId());
                if (formItemAnswer.getServerAnswerId() != null) {
                    surveyFormQuestionJsonObject.put("serverAnswerId", formItemAnswer.getServerAnswerId());
                }
                if (formItemAnswer.getAnswerInt() != null) {
                    surveyFormQuestionJsonObject.put("answerInt", formItemAnswer.getAnswerInt());
                }
                if (formItemAnswer.getAnswerStr() != null) {
                    surveyFormQuestionJsonObject.put("answerStr", formItemAnswer.getAnswerStr());
                }
                surveyFormQuestionJsonArray.put(surveyFormQuestionJsonObject);
            }
            surveyFormJsonObject.put("surveyFormQuestionList", surveyFormQuestionJsonArray);
            jsonObject.put("surveyForm", surveyFormJsonObject);
            MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, context);
            String result = postJsonService.sendData("updateSurveyFormAnswer", jsonObject, true);
            if (result != null) {
                JSONObject resultJson = new JSONObject(result);
                if (!resultJson.isNull(Constants.SUCCESS_KEY)) {
                    if (!resultJson.isNull(Constants.RESULT_KEY)) {
                        JSONObject resultJsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                        if (!resultJsonObject.isNull("savedSurveyForm")) {
                            JSONObject savedSurveyFormJsonObject = resultJsonObject.getJSONObject("savedSurveyForm");
                            if (!savedSurveyFormJsonObject.isNull("id")) {
                                formAnswer.setServerAnswerInfoId(savedSurveyFormJsonObject.getLong("id"));

                                if (!savedSurveyFormJsonObject.isNull("savedSurveyFormQuestionList")) {
                                    JSONArray savedSurveyFormQuestionJsonArray = savedSurveyFormJsonObject.getJSONArray("savedSurveyFormQuestionList");
                                    for (int i = 0; i < savedSurveyFormQuestionJsonArray.length(); i++) {
                                        JSONObject savedSurveyFormQuestionJsonObject = savedSurveyFormQuestionJsonArray.getJSONObject(i);
                                        if (!savedSurveyFormQuestionJsonObject.isNull("id")) {
                                            Long serverAnswerId = savedSurveyFormQuestionJsonObject.getLong("id");
                                            Long surveyFormQuestionId = savedSurveyFormQuestionJsonObject.getLong("surveyFormQuestionId");
                                            for (FormItemAnswer formItemAnswer : formItemAnswerList) {
                                                if (formItemAnswer.getId().equals(surveyFormQuestionId)) {
                                                    formItemAnswer.setServerAnswerId(serverAnswerId);
                                                    coreService.updateFormItemAnswer(formItemAnswer);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                List<AttachFile> attachFileList = coreService.getPendingAttachFileByEntityId(EntityNameEn.SurveyFormAnswerInfo, formAnswer.getId());
                                formAnswer.setSendingStatusEn(SendingStatusEn.Sent.ordinal());
                                formAnswer.setSendingStatusDate(new Date());
                                coreService.updateFormAnswer(formAnswer);

                                for (AttachFile attachFile : attachFileList) {
                                    attachFile.setServerEntityId(formAnswer.getServerAnswerInfoId());
                                    attachFile.setSendingStatusEn(SendingStatusEn.InProgress.ordinal());
                                    coreService.updateAttachFile(attachFile);
                                    //resumeAttachFile(context, coreService, attachFile, user);
                                }

                            }
                        }
                    }
                } else {
                    formAnswer.setSendingStatusEn(SendingStatusEn.Fail.ordinal());
                    formAnswer.setSendingStatusDate(new Date());
                    coreService.updateFormAnswer(formAnswer);
                }
            }
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = "SurveyFormReceiver";
            }
            Log.d(errorMsg, errorMsg);
            formAnswer.setSendingStatusEn(SendingStatusEn.Pending.ordinal());
            formAnswer.setSendingStatusDate(new Date());
            coreService.updateFormAnswer(formAnswer);
        }
    }

    public void resumeAttachFileList(Context context, User user) {
        DeviceSetting deviceSetting = getDeviceSettingByKey(Constants.DEVICE_SETTING_KEY_LAST_ATTACH_FILE_SEND_SYNC_DATE);
        List<AttachFile> attachFileList = coreService.getUnSentAttachFileList();
        if (!attachFileList.isEmpty()) {
            for (AttachFile attachFile : attachFileList) {
                resumeAttachFile(context, coreService, attachFile, user);
            }
        }
        updateDeviceSettingByKey(deviceSetting);
    }

    public void resumeAttachFile(Context context, CoreService coreService, AttachFile attachFile, User user) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", user.getUsername());
            jsonObject.put("tokenPass", user.getBisPassword());
            JSONObject attachFileJsonObject = new JSONObject();
            attachFileJsonObject.put("id", attachFile.getId());
            attachFileJsonObject.put("serverId", attachFile.getServerAttachFileId());
            attachFileJsonObject.put("entityNameEn", attachFile.getEntityNameEn());

            attachFileJsonObject.put("entityId", attachFile.getServerEntityId());
            System.out.println("getServerEntityId=" + attachFile.getServerEntityId());

            attachFileJsonObject.put("attachFileSettingId", attachFile.getServerAttachFileSettingId());
            System.out.println("getServerAttachFileSettingId=" + attachFile.getServerAttachFileSettingId());

            attachFileJsonObject.put("attachFileUserFileName", attachFile.getAttachFileUserFileName());
            System.out.println("getAttachFileUserFileName=" + attachFile.getAttachFileUserFileName());

            if (attachFile.getAttachFileLocalPath() != null) {
                File attachedFile = new File(attachFile.getAttachFileLocalPath());
                if (attachedFile.exists()) {
                    if (attachFile.getAttachFileSentSize() == null) {
                        attachFile.setAttachFileSentSize(0);
                    }
                    FileInputStream inputStream = new FileInputStream(attachedFile);
                    Integer fileSize = inputStream.available();
                    attachFile.setAttachFileSize(fileSize);
                    //skip already sent bytes
                    long skipRes = inputStream.skip(attachFile.getAttachFileSentSize());

                    byte[] fileBytes = new byte[MAX_ATTACH_FILE_PACKET_SIZE];
                    int res = inputStream.read(fileBytes);

                    byte[] fixedFileBytes = Arrays.copyOf(fileBytes, res);

                    JSONArray attachmentByteJsonArray = new JSONArray();
                    for (int i = 0; i < fixedFileBytes.length; i++) {
                        byte fileByte = fixedFileBytes[i];
                        attachmentByteJsonArray.put(fileByte);
                    }
                    attachFileJsonObject.put("attachmentBytes", attachmentByteJsonArray);
                    String attachmentChecksum = ImageUtil.getMD5Checksum(attachFile.getAttachFileLocalPath());
                    attachFileJsonObject.put("attachmentChecksum", ImageUtil.getMD5Checksum(attachFile.getAttachFileLocalPath()));

                    jsonObject.put("attachFile", attachFileJsonObject);
                    MyPostJsonService postJsonService = new MyPostJsonService(databaseManager, context);
                    String result = postJsonService.sendData("saveEntityAttachFileResumable", jsonObject, true);
                    System.out.println("result==saveEntityAttachFileResumable==" + result);
                    if (result != null) {
                        JSONObject resultJson = new JSONObject(result);
                        if (!resultJson.isNull(Constants.SUCCESS_KEY)) {
                            if (!resultJson.isNull(Constants.RESULT_KEY)) {
                                JSONObject resultJsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                                if (!resultJsonObject.isNull("savedAttachFile")) {
                                    JSONObject savedAttachFileJsonObject = resultJsonObject.getJSONObject("savedAttachFile");
                                    if (!savedAttachFileJsonObject.isNull("id")) {
                                        attachFile.setServerAttachFileId(savedAttachFileJsonObject.getLong("id"));
                                    }

                                    if (!savedAttachFileJsonObject.isNull("totalReceivedBytes")) {
                                        attachFile.setAttachFileSentSize(savedAttachFileJsonObject.getInt("totalReceivedBytes"));
                                    }
                                }
                                if (attachFile.getAttachFileSize() != null && attachFile.getAttachFileSentSize() != null && attachFile.getAttachFileSentSize().compareTo(attachFile.getAttachFileSize()) > 0) {
                                    attachFile.setSendingStatusEn(SendingStatusEn.Fail.ordinal());
                                    attachFile.setSendingStatusDate(new Date());
                                    coreService.updateAttachFile(attachFile);
                                } else if (attachFile.getAttachFileSize() == null || attachFile.getAttachFileSentSize() == null || attachFile.getAttachFileSize().equals(attachFile.getAttachFileSentSize())) {
                                    attachFile.setSendingStatusEn(SendingStatusEn.Sent.ordinal());
                                    attachFile.setSendingStatusDate(new Date());
                                    coreService.updateAttachFile(attachFile);
                                } else {
                                    attachFile.setSendingStatusEn(SendingStatusEn.AttachmentResuming.ordinal());
                                    attachFile.setSendingStatusDate(new Date());
                                    coreService.updateAttachFile(attachFile);
                                    resumeAttachFile(context, coreService, attachFile, user);
                                }
                            }
                        } else {
                            attachFile.setSendingStatusEn(SendingStatusEn.Fail.ordinal());
                            attachFile.setSendingStatusDate(new Date());
                            coreService.updateAttachFile(attachFile);
                        }
                    }

                } else {
                    attachFile.setSendingStatusEn(SendingStatusEn.Fail.ordinal());
                    attachFile.setSendingStatusDate(new Date());
                    coreService.updateAttachFile(attachFile);
                }
            } else {
                attachFile.setSendingStatusEn(SendingStatusEn.Fail.ordinal());
                attachFile.setSendingStatusDate(new Date());
                coreService.updateAttachFile(attachFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = e.getMessage();
            if (errorMsg == null) {
                errorMsg = "AttachFileReceiver";
            }
            Log.d(errorMsg, errorMsg);
            attachFile.setSendingStatusEn(SendingStatusEn.Fail.ordinal());
            attachFile.setSendingStatusDate(new Date());
            coreService.updateAttachFile(attachFile);
        }
    }

   /* @Override
    public void onCreate() {
        super.onCreate();
        startForeground(1, new Notification());
    }*/

    // Notification Function

    private void createNotification(String purpose, Long notificationId, String subject, String content) {
        Uri notificationSound = null;
        final int NOTIFY_ID = 1;

       /* try {
            notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notificationSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        subject = getString(R.string.app_name);

        if (!application.getCurrentUser().getAutoLogin() && (application.getCurrentUser().getLoginIs() == null || !application.getCurrentUser().getLoginIs())) {
            content = count + "پیام جدید";
        }


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        if (application.getCurrentUser().getAutoLogin()) {
            intent = new Intent(this, ChatGroupListActivity.class);
        } else {
            intent = new Intent(this, NotificationReceiverActivity.class);
            intent.putExtra("notificationId", notificationId);
            intent.putExtra("purpose", purpose);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //PendingIntent intent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);

        if (application.getCurrentUser().getAutoLogin()) {
            if (count > 1) {
                content = count + "پیام جدید";
            }
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.message_notify)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle(subject)
                .setSound(notificationSound)
                .setAutoCancel(true)
                .setContentText(content);


        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(true);
        notificationManager.notify(NOTIFY_ID, mBuilder.build());
        count++;
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void showNotification(Context context, String title, String body) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = 0;
        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle(title)
                .setContentText(body);

        User user = application.getCurrentUser();
        if (user.getAutoLogin() && (user.getLoginIs() != null || !user.getLoginIs())) {
            intent = new Intent(this, SettingActivity.class);
        } else if (!user.getAutoLogin()) {
            intent = new Intent(this, MainActivity.class);
            if (user.getLoginIs()) {
                intent = new Intent(this, SettingActivity.class);
            }
        }

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(true);
        notificationManager.notify(notificationId, mBuilder.build());


        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String strDate = mdformat.format(new Date());

        SharedPreferences.Editor editor = AppController.getInstance().getSharedPreferences().edit();
        editor.putString(Constants.DATE_SHOW_NOTIFICATION, strDate);
        editor.apply();
    }

    private boolean hasInternetAccess() {
        try {
            HttpURLConnection urlc = (HttpURLConnection)
                    (new URL("http://clients3.google.com/generate_204")
                            .openConnection());
            urlc.setRequestProperty("User-Agent", "Android");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(1500);
            urlc.connect();
            return (urlc.getResponseCode() == 204 &&
                    urlc.getContentLength() == 0);
        } catch (IOException e) {
            Log.e("TAG", "Error checking internet connection", e);
        }
        return false;
    }

    @SuppressLint("StaticFieldLeak")
    private class Checker extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            if (hasInternetAccess() &&
                    hasInternetAccess())
                return true;
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                //Log.i(TAG, "ارتباط برقرار است");
            } else {
                //Log.i(TAG, "عدم ارتباط با سرور");
            }
        }
    }
}
