package com.gap.bis_inspection.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gap.bis_inspection.R;
import com.gap.bis_inspection.activity.message.ChatActivity;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.db.enumtype.SendingStatusEn;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.manager.IDatabaseManager;
import com.gap.bis_inspection.db.objectmodel.AttachFile;
import com.gap.bis_inspection.db.objectmodel.ChatGroup;
import com.gap.bis_inspection.db.objectmodel.DeviceSetting;
import com.gap.bis_inspection.db.objectmodel.User;
import com.gap.bis_inspection.service.CoreService;
import com.gap.bis_inspection.service.Services;
import com.gap.bis_inspection.util.volly.ResponseBean;
import com.gap.bis_inspection.util.volly.VollyService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private Services services;
    private boolean userIsNull = false;
    private SharedPreferences.Editor editor;
    private CoreService coreService;
    private String action = null;
    private String groupId = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        coreService = new CoreService(new DatabaseManager(this));

        action = AppController.getInstance().getSharedPreferences().getString("action", "");
        groupId = AppController.getInstance().getSharedPreferences().getString("groupId", "");

        SharedPreferences.Editor editor = AppController.getInstance().getSharedPreferences().edit();
        editor.putString("action", null);
        editor.putString("groupId", null);
        editor.apply();
        AppController.getInstance().setNewMessage(false);

        IDatabaseManager databaseManager = new DatabaseManager(this);
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
                            services = new Services(getApplicationContext());

                            if (action.equals("newChatMessage") && groupId != null) {

                                AppController.getInstance().setNewMessage(true);
                                services.getChatMessageList();

                                ChatGroup tmp = new ChatGroup();
                                tmp.setServerGroupId(Long.valueOf(groupId));
                                ChatGroup chatGroup = coreService.getChatGroupByServerGroupId(tmp);
                                if (chatGroup != null) {
                                    services.getUserPermissionList();
                                    services.getDocumentUserList();
                                    Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                                    intent.putExtra("chatGroupId", chatGroup.getId());
                                    intent.putExtra("chatGroupName", chatGroup.getName());
                                    startActivity(intent);
                                    finish();
                                }
                                return;
                            }

                            services.getUserPermissionList();
                            services.getDocumentUserList();
                            services.getLastDocumentVersion();
                            services.getChatMessageList();
                            services.sendChatMessageReadReport();


                            sleep(4 * 1000);
                            Intent i = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();

                            services.resumeChatMessageAttachFileList();
                            services.resumeAttachFileList("");
                            services.getChatGroupList();
                            services.getChatMessageStatusList();
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

    public boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void resumeAttachFileList() throws Exception {
        DeviceSetting deviceSetting = getDeviceSettingByKey(Constants.DEVICE_SETTING_KEY_LAST_ATTACH_FILE_SEND_SYNC_DATE);
        List<AttachFile> attachFileList = coreService.getUnSentAttachFileList();
        if (!attachFileList.isEmpty()) {
            for (AttachFile attachFile : attachFileList) {
                resumeAttachFile(coreService, attachFile);
            }
        }
        updateDeviceSettingByKey(deviceSetting);
    }

    private void resumeAttachFile(final CoreService coreService, final AttachFile attachFile) throws Exception {
        AppController application = (AppController) getApplication();
        VollyService.getInstance().resumeAttachFile(application, attachFile, new Response.Listener<ResponseBean>() {
            @Override
            public void onResponse(ResponseBean responseBean) {
                if (responseBean.rESULT.savedAttachFile != null) {
                    if (responseBean.rESULT.savedAttachFile.id != null) {
                        attachFile.setServerAttachFileId(responseBean.rESULT.savedAttachFile.id);
                    }
                    if (responseBean.rESULT.savedAttachFile.totalReceivedBytes != null) {
                        attachFile.setAttachFileSentSize(responseBean.rESULT.savedAttachFile.totalReceivedBytes);
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
                    try {
                        resumeAttachFile(coreService, attachFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                attachFile.setSendingStatusEn(SendingStatusEn.Fail.ordinal());
                attachFile.setSendingStatusDate(new Date());
                coreService.updateAttachFile(attachFile);
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
}
