package com.gap.bis_inspection.util.volly;

import com.android.volley.Request;
import com.android.volley.Response;
import com.gap.bis_inspection.app.AppController;
import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.common.ImageUtil;
import com.gap.bis_inspection.db.enumtype.SendingStatusEn;
import com.gap.bis_inspection.db.objectmodel.AttachFile;
import com.gap.bis_inspection.db.objectmodel.ChatMessage;
import com.gap.bis_inspection.service.CoreService;
import com.gap.bis_inspection.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class VollyService {
    private static VollyService instance = null;
    public static Integer MAX_ATTACH_FILE_PACKET_SIZE = 8192;

    public static synchronized VollyService getInstance() {
        if (instance == null)
            instance = new VollyService();
        return instance;
    }

    private VollyService() {

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


    public void getMaxSizeAttachFileList(AppController application, String ProcessBisDataVOId,String attachFileSettingId, Response.Listener listener,
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


    public void resumeAttachFile(AppController application, AttachFile attachFile, Response.Listener listener,
                                 Response.ErrorListener errorListener) throws Exception {

        JSONObject attachFileJsonObject = new JSONObject();
        attachFileJsonObject.put("id", attachFile.getId());
        attachFileJsonObject.put("serverId", attachFile.getServerAttachFileId());
        attachFileJsonObject.put("entityNameEn", attachFile.getEntityNameEn());
        attachFileJsonObject.put("entityId", attachFile.getServerEntityId());
        attachFileJsonObject.put("attachFileSettingId", attachFile.getServerAttachFileSettingId());
        attachFileJsonObject.put("attachFileUserFileName", attachFile.getAttachFileUserFileName());

        String username = application.getCurrentUser().getUsername();
        String ws = Constants.WS + "saveEntityAttachFileResumable";
        ArrayList<Util.WSParameter> wsParameters = new ArrayList<>();
        wsParameters.add(new Util.WSParameter("username", username));
        wsParameters.add(new Util.WSParameter("tokenPass", application.getCurrentUser().getBisPassword()));
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


    public void resumeChatMessageAttachFile(AppController application, ChatMessage chatMessage, Response.Listener listener,
                                            Response.ErrorListener errorListener) throws Exception {


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
            }

            String username = application.getCurrentUser().getUsername();
            String ws = Constants.WS + "resumeChatMessageAttachFile";
            ArrayList<Util.WSParameter> wsParameters = new ArrayList<>();
            wsParameters.add(new Util.WSParameter("username", username));
            wsParameters.add(new Util.WSParameter("tokenPass", application.getCurrentUser().getBisPassword()));
            wsParameters.add(new Util.WSParameter("chatMessage", chatMessageJsonObject));

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
    }
}
