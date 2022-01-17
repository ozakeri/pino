package com.gap.bis_inspection.db.objectmodel.advert;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProcessBisDataVO {

    @SerializedName("entityStrForAttach")
    @Expose
    public String entityStrForAttach;
    @SerializedName("impunityIs")
    @Expose
    public Boolean impunityIs;
    @SerializedName("delayHour")
    @Expose
    public Integer delayHour;
    @SerializedName("processStatus")
    @Expose
    public Integer processStatus;
    @SerializedName("entityNameEn")
    @Expose
    public Integer entityNameEn;
    @SerializedName("showTimeRemainingUpForExpire")
    @Expose
    public Boolean showTimeRemainingUpForExpire;
    @SerializedName("financeTypeEn_text")
    @Expose
    public String financeTypeEnText;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("entityNameEn_text")
    @Expose
    public String entityNameEnText;
    @SerializedName("entityId")
    @Expose
    public Integer entityId;
    @SerializedName("impunityIsAllFS")
    @Expose
    public Boolean impunityIsAllFS;
    @SerializedName("financeTypeEn")
    @Expose
    public Integer financeTypeEn;
    @SerializedName("processStatus_text")
    @Expose
    public String processStatusText;
    @SerializedName("reasonSysParamStr")
    @Expose
    public String reasonSysParamStr;
    @SerializedName("entityNameStr")
    @Expose
    public String entityNameStr;
    @SerializedName("doSuccess")
    @Expose
    public Boolean doSuccess;
    @SerializedName("price")
    @Expose
    public Integer price;
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("status_text")
    @Expose
    public String statusText;
    @SerializedName("showCarReplaceForSave")
    @Expose
    public Boolean showCarReplaceForSave;
    @SerializedName("delayDay")
    @Expose
    public Integer delayDay;
    @SerializedName("startDate")
    @Expose
    public String startDate;
    @SerializedName("haveDelay")
    @Expose
    public Boolean haveDelay;
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("requestDateFrom")
    @Expose
    public String requestDateFrom;
    @SerializedName("requestDateTo")
    @Expose
    public String requestDateTo;


    public String getEntityStrForAttach() {
        return entityStrForAttach;
    }

    public void setEntityStrForAttach(String entityStrForAttach) {
        this.entityStrForAttach = entityStrForAttach;
    }

    public Boolean getImpunityIs() {
        return impunityIs;
    }

    public void setImpunityIs(Boolean impunityIs) {
        this.impunityIs = impunityIs;
    }

    public Integer getDelayHour() {
        return delayHour;
    }

    public void setDelayHour(Integer delayHour) {
        this.delayHour = delayHour;
    }

    public Integer getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(Integer processStatus) {
        this.processStatus = processStatus;
    }

    public Integer getEntityNameEn() {
        return entityNameEn;
    }

    public void setEntityNameEn(Integer entityNameEn) {
        this.entityNameEn = entityNameEn;
    }

    public Boolean getShowTimeRemainingUpForExpire() {
        return showTimeRemainingUpForExpire;
    }

    public void setShowTimeRemainingUpForExpire(Boolean showTimeRemainingUpForExpire) {
        this.showTimeRemainingUpForExpire = showTimeRemainingUpForExpire;
    }

    public String getFinanceTypeEnText() {
        return financeTypeEnText;
    }

    public void setFinanceTypeEnText(String financeTypeEnText) {
        this.financeTypeEnText = financeTypeEnText;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEntityNameEnText() {
        return entityNameEnText;
    }

    public void setEntityNameEnText(String entityNameEnText) {
        this.entityNameEnText = entityNameEnText;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Boolean getImpunityIsAllFS() {
        return impunityIsAllFS;
    }

    public void setImpunityIsAllFS(Boolean impunityIsAllFS) {
        this.impunityIsAllFS = impunityIsAllFS;
    }

    public Integer getFinanceTypeEn() {
        return financeTypeEn;
    }

    public void setFinanceTypeEn(Integer financeTypeEn) {
        this.financeTypeEn = financeTypeEn;
    }

    public String getProcessStatusText() {
        return processStatusText;
    }

    public void setProcessStatusText(String processStatusText) {
        this.processStatusText = processStatusText;
    }

    public String getReasonSysParamStr() {
        return reasonSysParamStr;
    }

    public void setReasonSysParamStr(String reasonSysParamStr) {
        this.reasonSysParamStr = reasonSysParamStr;
    }

    public String getEntityNameStr() {
        return entityNameStr;
    }

    public void setEntityNameStr(String entityNameStr) {
        this.entityNameStr = entityNameStr;
    }

    public Boolean getDoSuccess() {
        return doSuccess;
    }

    public void setDoSuccess(Boolean doSuccess) {
        this.doSuccess = doSuccess;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public Boolean getShowCarReplaceForSave() {
        return showCarReplaceForSave;
    }

    public void setShowCarReplaceForSave(Boolean showCarReplaceForSave) {
        this.showCarReplaceForSave = showCarReplaceForSave;
    }

    public Integer getDelayDay() {
        return delayDay;
    }

    public void setDelayDay(Integer delayDay) {
        this.delayDay = delayDay;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public Boolean getHaveDelay() {
        return haveDelay;
    }

    public void setHaveDelay(Boolean haveDelay) {
        this.haveDelay = haveDelay;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRequestDateFrom() {
        return requestDateFrom;
    }

    public void setRequestDateFrom(String requestDateFrom) {
        this.requestDateFrom = requestDateFrom;
    }

    public String getRequestDateTo() {
        return requestDateTo;
    }

    public void setRequestDateTo(String requestDateTo) {
        this.requestDateTo = requestDateTo;
    }
}
