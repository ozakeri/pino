package com.gap.bis_inspection.util.volly;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.common.ImageUtil;
import com.gap.bis_inspection.db.enumtype.SendingStatusEn;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.objectmodel.AttachFile;
import com.gap.bis_inspection.db.objectmodel.ChatMessage;
import com.gap.bis_inspection.db.objectmodel.DeviceSetting;
import com.gap.bis_inspection.service.CoreService;
import com.gap.bis_inspection.util.EventBusModel;
import com.gap.bis_inspection.util.Util;
import com.gap.bis_inspection.webservice.MyPostJsonService;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class VollyService {
    private static VollyService instance = null;
    public static Integer MAX_ATTACH_FILE_PACKET_SIZE = 8192;
    private CoreService coreService;
    private DatabaseManager databaseManager;
    private AppController appController;

    public static synchronized VollyService getInstance() {
        if (instance == null)
            instance = new VollyService();
        return instance;
    }

    private VollyService() {
        appController = AppController.getInstance();
        databaseManager = new DatabaseManager(appController);
        coreService = new CoreService(databaseManager);
    }

    public void getChatGroupMemberList(AppController application, Response.Listener listener,
                                       Response.ErrorListener errorListener) {
        String username = application.getCurrentUser().getUsername();
        String ws = Constants.WS + "getUserChatGroupMemberList";
        ArrayList<Util.WSParameter> wsParameters = new ArrayList<>();
        wsParameters.add(new Util.WSParameter("username", username));
        wsParameters.add(new Util.WSParameter("tokenPass", application.getCurrentUser().getBisPassword()));
        String json = Util.createJson(wsParameters);
        json = URLEncoder.encode(json);
        ws = ws + "?INPUT_PARAM=" + json;
        System.out.println("ws======" + ws);
        GsonRequest gsonRequest = new GsonRequest<>(
                Request.Method.GET,
                ws,
                ResponseBean.class,
                listener,
                errorListener,
                false
        );

        RestClient.getInstance().addToRequestQueue(gsonRequest);
    }

    public void downloadAttachFile(AppController application, CoreService coreService, ChatMessage chatMessage, Response.Listener listener,
                                   Response.ErrorListener errorListener) {

        String username = application.getCurrentUser().getUsername();
        String ws = Constants.WS + "downloadAttachFile";
        ArrayList<Util.WSParameter> wsParameters = new ArrayList<>();
        wsParameters.add(new Util.WSParameter("username", username));
        wsParameters.add(new Util.WSParameter("tokenPass", application.getCurrentUser().getBisPassword()));
        wsParameters.add(new Util.WSParameter("id", chatMessage.getServerMessageId()));
        wsParameters.add(new Util.WSParameter("downloadedSize", chatMessage.getAttachFileReceivedSize()));
        String json = Util.createJson(wsParameters);
        json = URLEncoder.encode(json);
        ws = ws + "?INPUT_PARAM=" + json;
        System.out.println("ws======" + ws);
        GsonRequest gsonRequest = new GsonRequest<>(
                Request.Method.GET,
                ws,
                ResponseBean.class,
                listener,
                errorListener,
                false
        );

        RestClient.getInstance().addToRequestQueue(gsonRequest);
    }


    public void getMaxSizeAttachFileList(AppController application, String ProcessBisDataVOId, String attachFileSettingId, Response.Listener listener,
                                         Response.ErrorListener errorListener) {

        String username = application.getCurrentUser().getUsername();
        String ws = Constants.WS + "getMaxSizeAttachFileList";
        ArrayList<Util.WSParameter> wsParameters = new ArrayList<>();
        wsParameters.add(new Util.WSParameter("username", username));
        wsParameters.add(new Util.WSParameter("tokenPass", application.getCurrentUser().getBisPassword()));
        wsParameters.add(new Util.WSParameter("id", ProcessBisDataVOId));
        wsParameters.add(new Util.WSParameter("attachFileSettingId", attachFileSettingId));
        String json = Util.createJson(wsParameters);
        json = URLEncoder.encode(json);
        ws = ws + "?INPUT_PARAM=" + json;
        System.out.println("ws======" + ws);
        GsonRequest gsonRequest = new GsonRequest<>(
                Request.Method.GET,
                ws,
                ResponseBean.class,
                listener,
                errorListener,
                false
        );

        RestClient.getInstance().addToRequestQueue(gsonRequest);
    }

    public void sendChatMessage(AppController application, CoreService coreService, ChatMessage chatMessage, Response.Listener listener,
                                Response.ErrorListener errorListener) throws Exception {

        chatMessage.setSendingStatusEn(SendingStatusEn.InProgress.ordinal());
        chatMessage.setSendingStatusDate(new Date());
        coreService.updateChatMessage(chatMessage);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");


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

        String username = application.getCurrentUser().getUsername();
        String ws = Constants.WS + "saveChatMessage";
        ArrayList<Util.WSParameter> wsParameters = new ArrayList<>();
        wsParameters.add(new Util.WSParameter("username", username));
        wsParameters.add(new Util.WSParameter("tokenPass", application.getCurrentUser().getBisPassword()));
        wsParameters.add(new Util.WSParameter("chatMessage", chatMessageJsonObject));

        System.out.println("chatMessageJsonObject===" + chatMessageJsonObject);

        String json = Util.createJson(wsParameters);
        json = URLEncoder.encode(json);
        ws = ws + "?INPUT_PARAM=" + json;
        System.out.println("ws======" + ws);
        GsonRequest gsonRequest = new GsonRequest<>(
                Request.Method.GET,
                ws,
                ResponseBean.class,
                listener,
                errorListener,
                false
        );

        RestClient.getInstance().addToRequestQueue(gsonRequest);
    }



    public void getServerDateTime(Response.Listener listener,
                                  Response.ErrorListener errorListener) {
        String ws = Constants.WS + "getServerDateTime";
        ArrayList<Util.WSParameter> wsParameters = new ArrayList<>();
        String json = Util.createJson(wsParameters);
        json = URLEncoder.encode(json);
        ws = ws + "?INPUT_PARAM=" + json;
        System.out.println("ws======" + ws);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, ws, listener, errorListener);
        RestClient.getInstance().addToRequestQueue2(stringRequest);
    }


    public void mobileNoConfirmation(String mobileNo, Response.Listener listener,
                                     Response.ErrorListener errorListener) {
        String ws = Constants.WS + "mobileNoConfirmation";
        ArrayList<Util.WSParameter> wsParameters = new ArrayList<>();
        wsParameters.add(new Util.WSParameter("mobileNo", mobileNo));
        String json = Util.createJson(wsParameters);
        json = URLEncoder.encode(json);
        ws = ws + "?INPUT_PARAM=" + json;
        System.out.println("ws======" + ws);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, ws, listener, errorListener);
        RestClient.getInstance().addToRequestQueue2(stringRequest);
    }

    public void activationCodeValidation(String mobileNo, String activationCode, Response.Listener listener,
                                         Response.ErrorListener errorListener) {
        String ws = Constants.WS + "activationCodeValidation";
        ArrayList<Util.WSParameter> wsParameters = new ArrayList<>();
        wsParameters.add(new Util.WSParameter("mobileNo", mobileNo));
        wsParameters.add(new Util.WSParameter("activationCode", activationCode));
        String json = Util.createJson(wsParameters);
        json = URLEncoder.encode(json);
        ws = ws + "?INPUT_PARAM=" + json;
        System.out.println("ws======" + ws);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, ws, listener, errorListener);
        RestClient.getInstance().addToRequestQueue2(stringRequest);
    }

    public void getUserPermissionList(String username, String tokenPass, Response.Listener listener,
                                      Response.ErrorListener errorListener) {
        String ws = Constants.WS + "getUserPermissionList";
        ArrayList<Util.WSParameter> wsParameters = new ArrayList<>();
        wsParameters.add(new Util.WSParameter("username", username));
        wsParameters.add(new Util.WSParameter("tokenPass", tokenPass));

        String json = Util.createJson(wsParameters);
        json = URLEncoder.encode(json);
        ws = ws + "?INPUT_PARAM=" + json;
        System.out.println("ws======" + ws);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, ws, listener, errorListener);
        RestClient.getInstance().addToRequestQueue2(stringRequest);
    }


    public void getUserChatMessageList(String username, String tokenPass, Response.Listener listener,
                                       Response.ErrorListener errorListener) {
        String ws = Constants.WS + "getUserChatMessageList";
        ArrayList<Util.WSParameter> wsParameters = new ArrayList<>();
        wsParameters.add(new Util.WSParameter("username", username));
        wsParameters.add(new Util.WSParameter("tokenPass", tokenPass));

        String json = Util.createJson(wsParameters);
        json = URLEncoder.encode(json);
        ws = ws + "?INPUT_PARAM=" + json;
        System.out.println("ws======" + ws);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, ws, listener, errorListener);
        RestClient.getInstance().addToRequestQueue2(stringRequest);
    }


    public void chatMessageDeliveredReport(JSONArray messageIdList, String username, String tokenPass, Response.Listener listener,
                                           Response.ErrorListener errorListener) {
        String ws = Constants.WS + "chatMessageDeliveredReport";
        ArrayList<Util.WSParameter> wsParameters = new ArrayList<>();
        wsParameters.add(new Util.WSParameter("username", username));
        wsParameters.add(new Util.WSParameter("tokenPass", tokenPass));
        wsParameters.add(new Util.WSParameter("messageIdList", messageIdList));

        String json = Util.createJson(wsParameters);
        json = URLEncoder.encode(json);
        ws = ws + "?INPUT_PARAM=" + json;
        System.out.println("ws======" + ws);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, ws, listener, errorListener);
        RestClient.getInstance().addToRequestQueue2(stringRequest);
    }

    public void getDocumentUserList(String username, String tokenPass, Response.Listener listener,
                                    Response.ErrorListener errorListener) {
        DeviceSetting deviceSetting = getDeviceSettingByKey(Constants.DEVICE_SETTING_KEY_LAST_APP_USER_SYNC_DATE);
        String ws = Constants.WS + "getDocumentUserList";
        ArrayList<Util.WSParameter> wsParameters = new ArrayList<>();
        wsParameters.add(new Util.WSParameter("username", username));
        wsParameters.add(new Util.WSParameter("tokenPass", tokenPass));
        wsParameters.add(new Util.WSParameter("lastUpdateDate", deviceSetting));
        if (deviceSetting.getValue() != null) {
            wsParameters.add(new Util.WSParameter("lastUpdateDate", deviceSetting.getValue()));
        }

        String json = Util.createJson(wsParameters);
        json = URLEncoder.encode(json);
        ws = ws + "?INPUT_PARAM=" + json;
        System.out.println("ws======" + ws);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, ws, listener, errorListener);
        RestClient.getInstance().addToRequestQueue2(stringRequest);
    }

    public void sendChatMessageReadReport(String username, String tokenPass, Response.Listener listener,
                                    Response.ErrorListener errorListener) throws JSONException {
        DeviceSetting deviceSetting = getDeviceSettingByKey(Constants.DEVICE_SETTING_KEY_LAST_APP_USER_SYNC_DATE);
        ChatMessage tmpChatMessageFS = new ChatMessage();
        tmpChatMessageFS.setReadIs(Boolean.TRUE);
        tmpChatMessageFS.setReadDateFrom(deviceSetting.getDateLastChange());
        tmpChatMessageFS.setSenderAppUserIdNot(appController.getCurrentUser().getServerUserId());
        List<ChatMessage> chatMessageList = coreService.getChatMessageListByParam(tmpChatMessageFS);

        String ws = Constants.WS + "chatMessageReadReport";
        ArrayList<Util.WSParameter> wsParameters = new ArrayList<>();

        if (!chatMessageList.isEmpty()) {
            wsParameters.add(new Util.WSParameter("username", username));
            wsParameters.add(new Util.WSParameter("tokenPass", tokenPass));
            JSONArray chatMessageJsonArray = new JSONArray();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            for (ChatMessage chatMessage : chatMessageList) {
                JSONObject chatMessageJsonObject = new JSONObject();
                chatMessageJsonObject.put("id", chatMessage.getServerMessageId());
                chatMessageJsonObject.put("readDate", simpleDateFormat.format(chatMessage.getReadDate()));
                chatMessageJsonArray.put(chatMessageJsonObject);
            }

            wsParameters.add(new Util.WSParameter("chatMessageList", chatMessageJsonArray));

        }else {
            updateDeviceSettingByKey(deviceSetting);
        }

        String json = Util.createJson(wsParameters);
        json = URLEncoder.encode(json);
        ws = ws + "?INPUT_PARAM=" + json;
        System.out.println("ws======" + ws);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, ws, listener, errorListener);
        RestClient.getInstance().addToRequestQueue2(stringRequest);
    }

    public void resumeChatMessageAttachFile(ChatMessage chatMessage, String username, String tokenPass, Response.Listener listener,
                                           Response.ErrorListener errorListener) throws Exception {

        ChatMessage chatMessageFS = new ChatMessage();
        chatMessageFS.setSenderAppUserId(appController.getCurrentUser().getServerUserId());

        String ws = Constants.WS + "resumeChatMessageAttachFile";
        ArrayList<Util.WSParameter> wsParameters = new ArrayList<>();
        wsParameters.add(new Util.WSParameter("username", username));
        wsParameters.add(new Util.WSParameter("tokenPass", tokenPass));
        JSONObject chatMessageJsonObject = new JSONObject();
        chatMessageJsonObject.put("id", chatMessage.getId());
        chatMessageJsonObject.put("senderUserId", chatMessage.getSenderAppUserId());

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

                wsParameters.add(new Util.WSParameter("chatMessage", chatMessageJsonObject));

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
        String json = Util.createJson(wsParameters);
        json = URLEncoder.encode(json);
        ws = ws + "?INPUT_PARAM=" + json;
        System.out.println("ws======" + ws);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, ws, listener, errorListener);
        RestClient.getInstance().addToRequestQueue2(stringRequest);
    }

    public void saveEntityAttachFileResumable(AttachFile attachFile, String username, String tokenPass, Response.Listener listener,
                                           Response.ErrorListener errorListener) throws Exception {
        String ws = Constants.WS + "saveEntityAttachFileResumable";
        ArrayList<Util.WSParameter> wsParameters = new ArrayList<>();
        wsParameters.add(new Util.WSParameter("username", username));
        wsParameters.add(new Util.WSParameter("tokenPass", tokenPass));
        JSONObject attachFileJsonObject = new JSONObject();
        attachFileJsonObject.put("id", attachFile.getId());
        attachFileJsonObject.put("serverId", attachFile.getServerAttachFileId());
        attachFileJsonObject.put("entityNameEn", attachFile.getEntityNameEn());
        attachFileJsonObject.put("entityId", attachFile.getServerEntityId());
        attachFileJsonObject.put("attachFileSettingId", attachFile.getServerAttachFileSettingId());
        attachFileJsonObject.put("attachFileUserFileName", attachFile.getAttachFileUserFileName());
        if (attachFile.getAttachFileLocalPath() != null) {
            File attachedFile = new File(attachFile.getAttachFileLocalPath());
            if (attachedFile.exists()) {
                if (attachFile.getAttachFileSentSize() == null) {
                    attachFile.setAttachFileSentSize(0);
                }

                FileInputStream inputStream = new FileInputStream(attachedFile);
                Integer fileSize = inputStream.available();
                attachFile.setAttachFileSize(fileSize);

                byte[] fileBytes = new byte[MAX_ATTACH_FILE_PACKET_SIZE];
                int res = inputStream.read(fileBytes);

                byte[] fixedFileBytes = Arrays.copyOf(fileBytes, res);

                JSONArray attachmentByteJsonArray = new JSONArray();
                for (int i = 0; i < fixedFileBytes.length; i++) {
                    byte fileByte = fixedFileBytes[i];
                    attachmentByteJsonArray.put(fileByte);
                }

                attachFileJsonObject.put("attachmentBytes", attachmentByteJsonArray);
                attachFileJsonObject.put("attachmentChecksum", ImageUtil.getMD5Checksum(attachFile.getAttachFileLocalPath()));

                wsParameters.add(new Util.WSParameter("attachFile", attachFileJsonObject));
            }
        }

        String json = Util.createJson(wsParameters);
        json = URLEncoder.encode(json);
        ws = ws + "?INPUT_PARAM=" + json;
        System.out.println("ws======" + ws);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, ws, listener, errorListener);
        RestClient.getInstance().addToRequestQueue2(stringRequest);
    }

    public void getChatGroupList(Response.Listener listener,
                                              Response.ErrorListener errorListener){
        String ws = Constants.WS + "getUserChatGroupList";
        ArrayList<Util.WSParameter> wsParameters = new ArrayList<>();
        wsParameters.add(new Util.WSParameter("username", appController.getCurrentUser().getUsername()));
        wsParameters.add(new Util.WSParameter("tokenPass", appController.getCurrentUser().getBisPassword()));


        String json = Util.createJson(wsParameters);
        json = URLEncoder.encode(json);
        ws = ws + "?INPUT_PARAM=" + json;
        System.out.println("ws======" + ws);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, ws, listener, errorListener);
        RestClient.getInstance().addToRequestQueue2(stringRequest);
    }

    public void getChatMessageStatusList(Response.Listener listener,
                                 Response.ErrorListener errorListener){
        List<ChatMessage> chatMessageList = coreService.getChatGroupListNotReadNotDelivered(appController.getCurrentUser().getServerUserId());
        if (chatMessageList.isEmpty())
            return;

        JSONArray serverMessageIdList = new JSONArray();

        String ws = Constants.WS + "getChatMessageStatusList";
        ArrayList<Util.WSParameter> wsParameters = new ArrayList<>();
        wsParameters.add(new Util.WSParameter("username", appController.getCurrentUser().getUsername()));
        wsParameters.add(new Util.WSParameter("tokenPass", appController.getCurrentUser().getBisPassword()));

        Map<Long, ChatMessage> chatMessageMap = new HashMap<Long, ChatMessage>();
        for (ChatMessage chatMessage : chatMessageList) {
            serverMessageIdList.put(chatMessage.getServerMessageId());
        }
        wsParameters.add(new Util.WSParameter("messageIdList", serverMessageIdList));


        String json = Util.createJson(wsParameters);
        json = URLEncoder.encode(json);
        ws = ws + "?INPUT_PARAM=" + json;
        System.out.println("ws======" + ws);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, ws, listener, errorListener);
        RestClient.getInstance().addToRequestQueue2(stringRequest);
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
