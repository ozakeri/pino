package com.gap.bis_inspection.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gap.bis_inspection.R;
import com.gap.bis_inspection.activity.message.ChatActivity;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.CommonUtil;
import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.db.enumtype.GeneralStatus;
import com.gap.bis_inspection.db.enumtype.SendingStatusEn;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.manager.IDatabaseManager;
import com.gap.bis_inspection.db.objectmodel.AppUser;
import com.gap.bis_inspection.db.objectmodel.AttachFile;
import com.gap.bis_inspection.db.objectmodel.ChatGroup;
import com.gap.bis_inspection.db.objectmodel.ChatMessage;
import com.gap.bis_inspection.db.objectmodel.DeviceSetting;
import com.gap.bis_inspection.db.objectmodel.User;
import com.gap.bis_inspection.db.objectmodel.UserPermission;
import com.gap.bis_inspection.service.CoreService;
import com.gap.bis_inspection.service.Services;
import com.gap.bis_inspection.util.EventBusModel;
import com.gap.bis_inspection.util.volly.VollyService;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {

    private boolean userIsNull = false;
    private SharedPreferences.Editor editor;
    private CoreService coreService;
    private String action = null;
    private String groupId = null;
    private AppController application;
    private IDatabaseManager databaseManager;
    private int counter = 1;
    private List<AttachFile> attachFileList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        application = (AppController) getApplication();

        coreService = new CoreService(new DatabaseManager(this));

        action = AppController.getInstance().getSharedPreferences().getString("action", "");
        groupId = AppController.getInstance().getSharedPreferences().getString("groupId", "");

        SharedPreferences.Editor editor = AppController.getInstance().getSharedPreferences().edit();
        editor.putString("action", null);
        editor.putString("groupId", null);
        editor.apply();
        AppController.getInstance().setNewMessage(false);

        databaseManager = new DatabaseManager(this);
        List<User> userList = databaseManager.listUsers();

        if (!userList.isEmpty()) {
            userIsNull = false;
        } else {
            userIsNull = true;
        }

        Thread background = new Thread() {
            public void run() {

                try {
                    // Thread will sleep for 5 seconds
                    if (!userIsNull) {
                        if (isConnected()) {

                            if (action.equals("newChatMessage") && groupId != null) {

                                AppController.getInstance().setNewMessage(true);
                                getChatMessageList();

                                ChatGroup tmp = new ChatGroup();
                                tmp.setServerGroupId(Long.valueOf(groupId));
                                ChatGroup chatGroup = coreService.getChatGroupByServerGroupId(tmp);
                                if (chatGroup != null) {
                                    getUserPermissionList();
                                    getDocumentUserList();
                                    Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                                    intent.putExtra("chatGroupId", chatGroup.getId());
                                    intent.putExtra("chatGroupName", chatGroup.getName());
                                    startActivity(intent);
                                    finish();
                                }
                                return;
                            }

                            getUserPermissionList();
                            getDocumentUserList();
                            //.getLastDocumentVersion();
                            getChatMessageList();
                            sendChatMessageReadReport();


                            sleep(4 * 1000);
                            Intent i = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();

                            resumeChatMessageAttachFileList();
                            resumeAttachFileList("");
                            getChatGroupList();
                            getChatMessageStatusList();
                            // services.getChatGroupMemberList();

                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.Some_error_accor_contact_admin), Toast.LENGTH_LONG).show();
                        }
                    } else {

                        sleep(4 * 1000);
                        Intent i = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }

                } catch (Exception e) {

                }
            }
        };

        // start thread
        background.start();

    }

    private void getChatMessageStatusList() {

        List<ChatMessage> chatMessageList = coreService.getChatGroupListNotReadNotDelivered(application.getCurrentUser().getServerUserId());
        if (chatMessageList.isEmpty())
            return;

        Map<Long, ChatMessage> chatMessageMap = new HashMap<Long, ChatMessage>();
        for (ChatMessage chatMessage : chatMessageList) {
            chatMessageMap.put(chatMessage.getServerMessageId(), chatMessage);
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);

        DeviceSetting deviceSetting = getDeviceSettingByKey(Constants.DEVICE_SETTING_KEY_LAST_GET_CHAT_MESSAGE_STATUS_LIST);
        VollyService.getInstance().getChatMessageStatusList(new Response.Listener() {
            @Override
            public void onResponse(Object result) {
                if (result != null) {
                    JSONObject resultJson = null;
                    try {
                        resultJson = new JSONObject((String) result);
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
                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }
                }
                updateDeviceSettingByKey(deviceSetting);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
    }

    public boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
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

    public void getChatMessageList() {
        VollyService.getInstance().getUserChatMessageList(application.getCurrentUser().getUsername(), application.getCurrentUser().getBisPassword(), new Response.Listener() {
            @Override
            public void onResponse(Object result) {
                if (result != null) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);
                    try {
                        JSONObject resultJson = new JSONObject((String) result);
                        if (!resultJson.isNull(Constants.SUCCESS_KEY)) {
                            if (!resultJson.isNull(Constants.RESULT_KEY)) {
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
                                                    //EventBus.getDefault().post(new EventBusModel(true));
                                                    /*if (title!= null || body != null){
                                                        sendNotification(context,title,body);
                                                    }*/
                                                }
                                            }
                                            idJsonArray.put(chatMessage.getServerMessageId());
                                        }

                                        VollyService.getInstance().chatMessageDeliveredReport(idJsonArray, application.getCurrentUser().getUsername(), application.getCurrentUser().getBisPassword()
                                                , new Response.Listener() {
                                                    @Override
                                                    public void onResponse(Object o) {

                                                    }
                                                }, new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError volleyError) {
                                                        Toast toast = Toast.makeText(SplashActivity.this, volleyError.toString(), Toast.LENGTH_LONG);
                                                        CommonUtil.showToast(toast, SplashActivity.this);
                                                        toast.show();
                                                    }
                                                });
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        String errorMsg = e.getMessage();
                        Log.d(errorMsg, errorMsg);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                String errorMsg = volleyError.getMessage();
                Log.d(errorMsg, errorMsg);
                Toast toast = Toast.makeText(SplashActivity.this, volleyError.toString(), Toast.LENGTH_LONG);
                CommonUtil.showToast(toast, SplashActivity.this);
                toast.show();
            }
        });
    }

    public void getUserPermissionList() {

        User user = application.getCurrentUser();
        VollyService.getInstance().getUserPermissionList(application.getCurrentUser().getUsername(), application.getCurrentUser().getBisPassword()
                , new Response.Listener() {
                    @Override
                    public void onResponse(Object result) {
                        try {
                            if (result != null) {
                                JSONObject resultJson = new JSONObject((String) result);
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
                        } catch (Exception e) {
                            Toast toast = Toast.makeText(SplashActivity.this, e.getMessage(), Toast.LENGTH_LONG);
                            CommonUtil.showToast(toast, SplashActivity.this);
                            toast.show();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast toast = Toast.makeText(SplashActivity.this, volleyError.toString(), Toast.LENGTH_LONG);
                        CommonUtil.showToast(toast, SplashActivity.this);
                        toast.show();
                    }
                });
    }

    public void getDocumentUserList() {
        VollyService.getInstance().getDocumentUserList(application.getCurrentUser().getUsername(), application.getCurrentUser().getBisPassword(), new Response.Listener() {
            @Override
            public void onResponse(Object result) {
                if (result != null) {
                    JSONObject resultJson = null;
                    try {
                        resultJson = new JSONObject((String) result);
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    DeviceSetting deviceSetting = getDeviceSettingByKey(Constants.DEVICE_SETTING_KEY_LAST_APP_USER_SYNC_DATE);
                    updateDeviceSettingByKey(deviceSetting);

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast toast = Toast.makeText(SplashActivity.this, volleyError.toString(), Toast.LENGTH_LONG);
                CommonUtil.showToast(toast, SplashActivity.this);
                toast.show();
            }
        });
    }

    public void sendChatMessageReadReport() throws JSONException {
        VollyService.getInstance().sendChatMessageReadReport(application.getCurrentUser().getUsername(), application.getCurrentUser().getBisPassword()
                , new Response.Listener() {
                    @Override
                    public void onResponse(Object result) {
                        if (result != null) {
                            JSONObject resultJson = null;
                            try {
                                resultJson = new JSONObject((String) result);
                                if (!resultJson.isNull(Constants.SUCCESS_KEY)) {
                                    DeviceSetting deviceSetting = getDeviceSettingByKey(Constants.DEVICE_SETTING_KEY_LAST_APP_USER_SYNC_DATE);
                                    updateDeviceSettingByKey(deviceSetting);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast toast = Toast.makeText(SplashActivity.this, volleyError.toString(), Toast.LENGTH_LONG);
                        CommonUtil.showToast(toast, SplashActivity.this);
                        toast.show();
                    }
                });
    }

    public void resumeChatMessageAttachFileList() throws Exception {
        DeviceSetting deviceSetting = getDeviceSettingByKey(Constants.DEVICE_SETTING_KEY_LAST_CHAT_MESSAGE_SEND_SYNC_DATE);
        ChatMessage chatMessageFS = new ChatMessage();
        chatMessageFS.setSenderAppUserId(application.getCurrentUser().getServerUserId());
        List<ChatMessage> chatMessageList = coreService.getAttachmentResumingChatMessageList(chatMessageFS);
        if (!chatMessageList.isEmpty()) {
            for (ChatMessage chatMessage : chatMessageList) {
                resumeChatMessageAttachFile(chatMessage);
            }
        }
        updateDeviceSettingByKey(deviceSetting);
    }

    public void resumeChatMessageAttachFile(ChatMessage chatMessage) throws Exception {
        VollyService.getInstance().resumeChatMessageAttachFile(chatMessage, application.getCurrentUser().getUsername(), application.getCurrentUser().getBisPassword()
                , new Response.Listener() {
                    @Override
                    public void onResponse(Object result) {
                        if (result != null) {
                            JSONObject resultJson = null;
                            try {
                                resultJson = new JSONObject((String) result);
                                if (!resultJson.isNull(Constants.SUCCESS_KEY)) {
                                    if (!resultJson.isNull(Constants.RESULT_KEY)) {
                                        EventBus.getDefault().post(new EventBusModel(true, true));
                                        JSONObject resultJsonObject = resultJson.getJSONObject(Constants.RESULT_KEY);
                                        if (!resultJsonObject.isNull("savedAttachFile")) {
                                            JSONObject savedAttachFileJsonObject = resultJsonObject.getJSONObject("savedAttachFile");
                                            if (!savedAttachFileJsonObject.isNull("id")) {
                                                chatMessage.setServerMessageId(savedAttachFileJsonObject.getLong("id"));
                                            }

                                            if (!savedAttachFileJsonObject.isNull("totalReceivedBytes")) {
                                                chatMessage.setAttachFileSentSize(savedAttachFileJsonObject.getInt("totalReceivedBytes"));
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
                                                resumeChatMessageAttachFile(chatMessage);
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
                            }
                        } else {

                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast toast = Toast.makeText(SplashActivity.this, volleyError.toString(), Toast.LENGTH_LONG);
                        CommonUtil.showToast(toast, SplashActivity.this);
                        toast.show();
                    }
                });
    }

    public void resumeAttachFileList(String attachFileSettingId) throws Exception {
        counter = 1;
        DeviceSetting deviceSetting = getDeviceSettingByKey(Constants.DEVICE_SETTING_KEY_LAST_ATTACH_FILE_SEND_SYNC_DATE);
        attachFileList = coreService.getUnSentAttachFileList();
        if (!attachFileList.isEmpty()) {
            for (AttachFile attachFile : attachFileList) {
                resumeAttachFile(attachFile, attachFileSettingId);
                counter++;
            }
        }
        updateDeviceSettingByKey(deviceSetting);
    }

    public void resumeAttachFile(AttachFile attachFile, String attachFileSettingId) throws Exception {
        VollyService.getInstance().saveEntityAttachFileResumable(attachFile, application.getCurrentUser().getUsername(), application.getCurrentUser().getBisPassword()
                , new Response.Listener() {
                    @Override
                    public void onResponse(Object result) {
                        if (result != null) {
                            JSONObject resultJson = null;
                            try {
                                resultJson = new JSONObject((String) result);
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

                                            System.out.println("counter====" + counter);
                                            System.out.println("attachFileList.size()====" + attachFileList.size());

                                            if (counter == attachFileList.size()) {
                                                EventBus.getDefault().post(new EventBusModel(true, true, true));
                                            }

                                        } else {
                                            attachFile.setSendingStatusEn(SendingStatusEn.AttachmentResuming.ordinal());
                                            attachFile.setSendingStatusDate(new Date());
                                            coreService.updateAttachFile(attachFile);
                                            resumeAttachFile(attachFile, attachFileSettingId);
                                        }

                                    }
                                } else {
                                    attachFile.setSendingStatusEn(SendingStatusEn.Fail.ordinal());
                                    attachFile.setSendingStatusDate(new Date());
                                    coreService.updateAttachFile(attachFile);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {

                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast toast = Toast.makeText(SplashActivity.this, volleyError.toString(), Toast.LENGTH_LONG);
                        CommonUtil.showToast(toast, SplashActivity.this);
                        toast.show();
                    }
                });
    }

    public void getChatGroupList() {
        VollyService.getInstance().getChatGroupList(new Response.Listener() {
            @Override
            public void onResponse(Object result) {
                if (result != null) {
                    JSONObject resultJson = null;
                    List<Long> longList = new ArrayList<>();
                    try {
                        resultJson = new JSONObject((String) result);
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
                                            if (!chatGroupJsonObject.isNull("privateIs")) {
                                                chatGroup.setPrivateIs(chatGroupJsonObject.getBoolean("privateIs"));
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
                                                coreService.saveChatGroup(chatGroup);
                                            } else {
                                                coreService.updateChatGroup(chatGroup);
                                            }
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
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast toast = Toast.makeText(SplashActivity.this, volleyError.toString(), Toast.LENGTH_LONG);
                CommonUtil.showToast(toast, SplashActivity.this);
                toast.show();
            }
        });
    }
}
