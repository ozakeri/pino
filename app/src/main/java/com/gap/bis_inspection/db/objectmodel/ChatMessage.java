package com.gap.bis_inspection.db.objectmodel;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

import java.io.File;
import java.util.Date;

/**
 * Entity mapped to table "CHAT_MESSAGE".
 */
public class ChatMessage {

    private Long id;
    private Long serverMessageId;
    private java.util.Date validUntilDate;
    private String message;
    private String attachFileLocalPath;
    private String attachFileUserFileName;
    private String attachFileRemoteUrl;
    private java.util.Date sendDate;
    private java.util.Date dateCreation;
    private Boolean deliverIs;
    private java.util.Date deliverDate;
    private Boolean readIs;
    private Boolean isCreateNewPvChatGroup;
    private java.util.Date readDate;
    private Integer sendingStatusEn;
    private java.util.Date sendingStatusDate;
    private Integer attachFileSize;
    private Integer attachFileSentSize;
    private Integer attachFileReceivedSize;
    private Long senderAppUserId;
    private Long receiverAppUserId;
    private Long chatGroupId;
    private transient AppUser senderAppUser;
    private transient boolean localAttachFileExist;
    private transient java.util.Date readDateFrom;
    private transient Long senderAppUserIdNot;

    public ChatMessage() {
    }

    public ChatMessage(Long id) {
        this.id = id;
    }

    public ChatMessage(Long id, Long serverMessageId, java.util.Date validUntilDate, String message, String attachFileLocalPath, String attachFileUserFileName, String attachFileRemoteUrl, java.util.Date sendDate, java.util.Date dateCreation, Boolean deliverIs, java.util.Date deliverDate, Boolean readIs,Boolean isCreateNewPvChatGroup, java.util.Date readDate, Integer sendingStatusEn, java.util.Date sendingStatusDate, Integer attachFileSize, Integer attachFileSentSize, Integer attachFileReceivedSize, Long senderAppUserId, Long receiverAppUserId, Long chatGroupId) {
        this.id = id;
        this.serverMessageId = serverMessageId;
        this.validUntilDate = validUntilDate;
        this.message = message;
        this.attachFileLocalPath = attachFileLocalPath;
        this.attachFileUserFileName = attachFileUserFileName;
        this.attachFileRemoteUrl = attachFileRemoteUrl;
        this.sendDate = sendDate;
        this.dateCreation = dateCreation;
        this.deliverIs = deliverIs;
        this.deliverDate = deliverDate;
        this.readIs = readIs;
        this.isCreateNewPvChatGroup = isCreateNewPvChatGroup;
        this.readDate = readDate;
        this.sendingStatusEn = sendingStatusEn;
        this.sendingStatusDate = sendingStatusDate;
        this.attachFileSize = attachFileSize;
        this.attachFileSentSize = attachFileSentSize;
        this.attachFileReceivedSize = attachFileReceivedSize;
        this.senderAppUserId = senderAppUserId;
        this.receiverAppUserId = receiverAppUserId;
        this.chatGroupId = chatGroupId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getServerMessageId() {
        return serverMessageId;
    }

    public void setServerMessageId(Long serverMessageId) {
        this.serverMessageId = serverMessageId;
    }

    public java.util.Date getValidUntilDate() {
        return validUntilDate;
    }

    public void setValidUntilDate(java.util.Date validUntilDate) {
        this.validUntilDate = validUntilDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAttachFileLocalPath() {
        return attachFileLocalPath;
    }

    public void setAttachFileLocalPath(String attachFileLocalPath) {
        this.attachFileLocalPath = attachFileLocalPath;
    }

    public String getAttachFileUserFileName() {
        return attachFileUserFileName;
    }

    public void setAttachFileUserFileName(String attachFileUserFileName) {
        this.attachFileUserFileName = attachFileUserFileName;
    }

    public String getAttachFileRemoteUrl() {
        return attachFileRemoteUrl;
    }

    public void setAttachFileRemoteUrl(String attachFileRemoteUrl) {
        this.attachFileRemoteUrl = attachFileRemoteUrl;
    }

    public java.util.Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(java.util.Date sendDate) {
        this.sendDate = sendDate;
    }

    public java.util.Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(java.util.Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Boolean getDeliverIs() {
        return deliverIs;
    }

    public void setDeliverIs(Boolean deliverIs) {
        this.deliverIs = deliverIs;
    }

    public java.util.Date getDeliverDate() {
        return deliverDate;
    }

    public void setDeliverDate(java.util.Date deliverDate) {
        this.deliverDate = deliverDate;
    }

    public Boolean getReadIs() {
        return readIs;
    }

    public void setReadIs(Boolean readIs) {
        this.readIs = readIs;
    }

    public java.util.Date getReadDate() {
        return readDate;
    }

    public void setReadDate(java.util.Date readDate) {
        this.readDate = readDate;
    }

    public Integer getSendingStatusEn() {
        return sendingStatusEn;
    }

    public void setSendingStatusEn(Integer sendingStatusEn) {
        this.sendingStatusEn = sendingStatusEn;
    }

    public java.util.Date getSendingStatusDate() {
        return sendingStatusDate;
    }

    public void setSendingStatusDate(java.util.Date sendingStatusDate) {
        this.sendingStatusDate = sendingStatusDate;
    }

    public Integer getAttachFileSize() {
        return attachFileSize;
    }

    public void setAttachFileSize(Integer attachFileSize) {
        this.attachFileSize = attachFileSize;
    }

    public Integer getAttachFileSentSize() {
        return attachFileSentSize;
    }

    public void setAttachFileSentSize(Integer attachFileSentSize) {
        this.attachFileSentSize = attachFileSentSize;
    }

    public Integer getAttachFileReceivedSize() {
        return attachFileReceivedSize;
    }

    public void setAttachFileReceivedSize(Integer attachFileReceivedSize) {
        this.attachFileReceivedSize = attachFileReceivedSize;
    }

    public Long getSenderAppUserId() {
        return senderAppUserId;
    }

    public void setSenderAppUserId(Long senderAppUserId) {
        this.senderAppUserId = senderAppUserId;
    }

    public Long getReceiverAppUserId() {
        return receiverAppUserId;
    }

    public void setReceiverAppUserId(Long receiverAppUserId) {
        this.receiverAppUserId = receiverAppUserId;
    }

    public Long getChatGroupId() {
        return chatGroupId;
    }

    public void setChatGroupId(Long chatGroupId) {
        this.chatGroupId = chatGroupId;
    }

    public AppUser getSenderAppUser() {
        return senderAppUser;
    }

    public void setSenderAppUser(AppUser senderAppUser) {
        this.senderAppUser = senderAppUser;
    }

    public Date getReadDateFrom() {
        return readDateFrom;
    }

    public void setReadDateFrom(Date readDateFrom) {
        this.readDateFrom = readDateFrom;
    }

    public Long getSenderAppUserIdNot() {
        return senderAppUserIdNot;
    }

    public void setSenderAppUserIdNot(Long senderAppUserIdNot) {
        this.senderAppUserIdNot = senderAppUserIdNot;
    }

    public boolean isLocalAttachFileExist() {
        localAttachFileExist = false;
        if (attachFileUserFileName != null && attachFileLocalPath != null) {
            File file = new File(attachFileLocalPath);
            return file.exists();
        }
        return localAttachFileExist;
    }

    public void setLocalAttachFileExist(boolean localAttachFileExist) {
        this.localAttachFileExist = localAttachFileExist;
    }

    public Boolean getCreateNewPvChatGroup() {
        return isCreateNewPvChatGroup;
    }

    public void setCreateNewPvChatGroup(Boolean createNewPvChatGroup) {
        isCreateNewPvChatGroup = createNewPvChatGroup;
    }
}
