package com.gap.bis_inspection.util;

import java.util.ArrayList;
import java.util.List;

public class EventBusModel {

    private boolean readIs = false;
    private boolean isMessage = false;
    private int position;
    private List<Integer> integerList = new ArrayList<>();
    private List<Long> serverGroupIdList = new ArrayList<>();
    private boolean isNewMessage = false;
    private boolean isDownloadAttachFile = false;
    private boolean isSendAttachFile = false;
    private boolean t = true;
    private String result;
    private boolean isComplete;
    private boolean test;
    private boolean isEdit = false;
    private int isMaxCorrect = 0;

    public EventBusModel(boolean isDownloadAttachFile, boolean t) {
        this.isDownloadAttachFile = isDownloadAttachFile;
        this.t = t;
    }

    public EventBusModel(int isMaxCorrect) {
        this.isMaxCorrect = isMaxCorrect;
    }

    public EventBusModel(boolean isComplete, boolean test1, boolean test2) {
        this.isComplete = isComplete;
        this.test = test1;
        this.test = test2;
    }

    public EventBusModel(String result) {
        this.result = result;
    }


    public EventBusModel(boolean isNewMessage) {
        this.isNewMessage = isNewMessage;
    }

    public boolean isReadIs() {
        return readIs;
    }

    public void setReadIs(boolean readIs) {
        this.readIs = readIs;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public List<Integer> getIntegerList() {
        return integerList;
    }

    public List<Long> getServerGroupIdList() {
        return serverGroupIdList;
    }

    public void setServerGroupIdList(List<Long> serverGroupIdList) {
        this.serverGroupIdList = serverGroupIdList;
    }

    public boolean isNewMessage() {
        return isNewMessage;
    }

    public void setNewMessage(boolean newMessage) {
        isNewMessage = newMessage;
    }

    public boolean isMessage() {
        return isMessage;
    }

    public void setMessage(boolean message) {
        isMessage = message;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public boolean isDownloadAttachFile() {
        return isDownloadAttachFile;
    }

    public void setDownloadAttachFile(boolean downloadAttachFile) {
        isDownloadAttachFile = downloadAttachFile;
    }

    public boolean isSendAttachFile() {
        return isSendAttachFile;
    }

    public void setSendAttachFile(boolean sendAttachFile) {
        isSendAttachFile = sendAttachFile;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public int getIsMaxCorrect() {
        return isMaxCorrect;
    }

    public void setIsMaxCorrect(int isMaxCorrect) {
        this.isMaxCorrect = isMaxCorrect;
    }
}
