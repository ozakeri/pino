package com.gap.bis_inspection.db.objectmodel;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "ATTACH_FILE".
 */
public class AttachFile {

    private Long id;
    private String attachFileLocalPath;
    private String attachFileUserFileName;
    private String attachFileRemoteUrl;
    private java.util.Date dateCreation;
    private Integer sendingStatusEn;
    private java.util.Date sendingStatusDate;
    private Integer attachFileSize;
    private Integer attachFileSentSize;
    private Integer attachFileReceivedSize;
    private Integer entityNameEn;//183
    private Long entityId;//complaint report id
    private Long serverAttachFileId;
    private Long serverEntityId;//server complaint report id
    private Long serverAttachFileSettingId;

    public AttachFile() {
    }

    public AttachFile(Long id) {
        this.id = id;
    }

    public AttachFile(Long id, String attachFileLocalPath, String attachFileUserFileName, String attachFileRemoteUrl, java.util.Date dateCreation, Integer sendingStatusEn, java.util.Date sendingStatusDate, Integer attachFileSize, Integer attachFileSentSize, Integer attachFileReceivedSize, Integer entityNameEn, Long entityId, Long serverAttachFileId, Long serverEntityId, Long serverAttachFileSettingId) {
        this.id = id;
        this.attachFileLocalPath = attachFileLocalPath;
        this.attachFileUserFileName = attachFileUserFileName;
        this.attachFileRemoteUrl = attachFileRemoteUrl;
        this.dateCreation = dateCreation;
        this.sendingStatusEn = sendingStatusEn;
        this.sendingStatusDate = sendingStatusDate;
        this.attachFileSize = attachFileSize;
        this.attachFileSentSize = attachFileSentSize;
        this.attachFileReceivedSize = attachFileReceivedSize;
        this.entityNameEn = entityNameEn;
        this.entityId = entityId;
        this.serverAttachFileId = serverAttachFileId;
        this.serverEntityId = serverEntityId;
        this.serverAttachFileSettingId = serverAttachFileSettingId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public java.util.Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(java.util.Date dateCreation) {
        this.dateCreation = dateCreation;
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

    public Integer getEntityNameEn() {
        return entityNameEn;
    }

    public void setEntityNameEn(Integer entityNameEn) {
        this.entityNameEn = entityNameEn;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getServerAttachFileId() {
        return serverAttachFileId;
    }

    public void setServerAttachFileId(Long serverAttachFileId) {
        this.serverAttachFileId = serverAttachFileId;
    }

    public Long getServerEntityId() {
        return serverEntityId;
    }

    public void setServerEntityId(Long serverEntityId) {
        this.serverEntityId = serverEntityId;
    }

    public Long getServerAttachFileSettingId() {
        return serverAttachFileSettingId;
    }

    public void setServerAttachFileSettingId(Long serverAttachFileSettingId) {
        this.serverAttachFileSettingId = serverAttachFileSettingId;
    }
}
