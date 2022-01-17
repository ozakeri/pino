package com.gap.bis_inspection.db.objectmodel.advert;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProcessBisSettingVO {

    @SerializedName("nameFv")
    @Expose
    public String nameFv;
    @SerializedName("processStatus")
    @Expose
    public Integer processStatus;
    @SerializedName("processEn")
    @Expose
    public Integer processEn;
    @SerializedName("actionTypeEn")
    @Expose
    public Integer actionTypeEn;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("entityNameEn_text")
    @Expose
    public String entityNameEnText;
    @SerializedName("psChangeSucTo")
    @Expose
    public Integer psChangeSucTo;
    @SerializedName("typeEn")
    @Expose
    public Integer typeEn;
    @SerializedName("haveAttachment")
    @Expose
    public Boolean haveAttachment;
    @SerializedName("psStart_text")
    @Expose
    public String psStartText;
    @SerializedName("checkReferDate")
    @Expose
    public Boolean checkReferDate;
    @SerializedName("checkTimeWorkCompany")
    @Expose
    public Boolean checkTimeWorkCompany;
    @SerializedName("codeInTree")
    @Expose
    public String codeInTree;
    @SerializedName("actionTypeEn_text")
    @Expose
    public String actionTypeEnText;
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("psChangeFailTo")
    @Expose
    public Integer psChangeFailTo;
    @SerializedName("psStart")
    @Expose
    public Integer psStart;
    @SerializedName("autoCompleteLabel")
    @Expose
    public String autoCompleteLabel;
    @SerializedName("entityNameEn")
    @Expose
    public Integer entityNameEn;
    @SerializedName("processReasonStr")
    @Expose
    public String processReasonStr;
    @SerializedName("showTimeRemainingUpForExpire")
    @Expose
    public Boolean showTimeRemainingUpForExpire;
    @SerializedName("changeEndDateToThis")
    @Expose
    public Boolean changeEndDateToThis;
    @SerializedName("processStatus_text")
    @Expose
    public String processStatusText;
    @SerializedName("psChangeSucTo_text")
    @Expose
    public String psChangeSucToText;
    @SerializedName("continueEndDateToThis")
    @Expose
    public Boolean continueEndDateToThis;
    @SerializedName("notConfirmIsPossible")
    @Expose
    public Boolean notConfirmIsPossible;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("status_text")
    @Expose
    public String statusText;
    @SerializedName("typeEn_text")
    @Expose
    public String typeEnText;
    @SerializedName("psChangeFailTo_text")
    @Expose
    public String psChangeFailToText;
    @SerializedName("newEntityDoSave")
    @Expose
    public Boolean newEntityDoSave;
    @SerializedName("canBeChangeDate")
    @Expose
    public Boolean canBeChangeDate;
    @SerializedName("status")
    @Expose
    public Integer status;
    @SerializedName("processEn_text")
    @Expose
    public String processEnText;
    @SerializedName("delayHour")
    @Expose
    public Integer delayHour;
    @SerializedName("canSaveDateBeforeMin")
    @Expose
    public Integer canSaveDateBeforeMin;
    @SerializedName("psActionChangeSucTo_text")
    @Expose
    public String psActionChangeSucToText;
    @SerializedName("costGroupEn_text")
    @Expose
    public String costGroupEnText;
    @SerializedName("delayMethodCal")
    @Expose
    public Integer delayMethodCal;
    @SerializedName("delayMethodCal_text")
    @Expose
    public String delayMethodCalText;
    @SerializedName("psActionChangeSucTo")
    @Expose
    public Integer psActionChangeSucTo;
    @SerializedName("delayDay")
    @Expose
    public Integer delayDay;
    @SerializedName("costGroupEn")
    @Expose
    public Integer costGroupEn;

    public Integer getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(Integer processStatus) {
        this.processStatus = processStatus;
    }

    public Integer getProcessEn() {
        return processEn;
    }

    public void setProcessEn(Integer processEn) {
        this.processEn = processEn;
    }

    public Integer getActionTypeEn() {
        return actionTypeEn;
    }

    public void setActionTypeEn(Integer actionTypeEn) {
        this.actionTypeEn = actionTypeEn;
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

    public Integer getPsChangeSucTo() {
        return psChangeSucTo;
    }

    public void setPsChangeSucTo(Integer psChangeSucTo) {
        this.psChangeSucTo = psChangeSucTo;
    }

    public Integer getTypeEn() {
        return typeEn;
    }

    public void setTypeEn(Integer typeEn) {
        this.typeEn = typeEn;
    }

    public Boolean getHaveAttachment() {
        return haveAttachment;
    }

    public void setHaveAttachment(Boolean haveAttachment) {
        this.haveAttachment = haveAttachment;
    }

    public String getPsStartText() {
        return psStartText;
    }

    public void setPsStartText(String psStartText) {
        this.psStartText = psStartText;
    }

    public Boolean getCheckReferDate() {
        return checkReferDate;
    }

    public void setCheckReferDate(Boolean checkReferDate) {
        this.checkReferDate = checkReferDate;
    }

    public Boolean getCheckTimeWorkCompany() {
        return checkTimeWorkCompany;
    }

    public void setCheckTimeWorkCompany(Boolean checkTimeWorkCompany) {
        this.checkTimeWorkCompany = checkTimeWorkCompany;
    }

    public String getCodeInTree() {
        return codeInTree;
    }

    public void setCodeInTree(String codeInTree) {
        this.codeInTree = codeInTree;
    }

    public String getActionTypeEnText() {
        return actionTypeEnText;
    }

    public void setActionTypeEnText(String actionTypeEnText) {
        this.actionTypeEnText = actionTypeEnText;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPsChangeFailTo() {
        return psChangeFailTo;
    }

    public void setPsChangeFailTo(Integer psChangeFailTo) {
        this.psChangeFailTo = psChangeFailTo;
    }

    public Integer getPsStart() {
        return psStart;
    }

    public void setPsStart(Integer psStart) {
        this.psStart = psStart;
    }

    public String getAutoCompleteLabel() {
        return autoCompleteLabel;
    }

    public void setAutoCompleteLabel(String autoCompleteLabel) {
        this.autoCompleteLabel = autoCompleteLabel;
    }

    public Integer getEntityNameEn() {
        return entityNameEn;
    }

    public void setEntityNameEn(Integer entityNameEn) {
        this.entityNameEn = entityNameEn;
    }

    public String getProcessReasonStr() {
        return processReasonStr;
    }

    public void setProcessReasonStr(String processReasonStr) {
        this.processReasonStr = processReasonStr;
    }

    public Boolean getShowTimeRemainingUpForExpire() {
        return showTimeRemainingUpForExpire;
    }

    public void setShowTimeRemainingUpForExpire(Boolean showTimeRemainingUpForExpire) {
        this.showTimeRemainingUpForExpire = showTimeRemainingUpForExpire;
    }

    public Boolean getChangeEndDateToThis() {
        return changeEndDateToThis;
    }

    public void setChangeEndDateToThis(Boolean changeEndDateToThis) {
        this.changeEndDateToThis = changeEndDateToThis;
    }

    public String getProcessStatusText() {
        return processStatusText;
    }

    public void setProcessStatusText(String processStatusText) {
        this.processStatusText = processStatusText;
    }

    public String getPsChangeSucToText() {
        return psChangeSucToText;
    }

    public void setPsChangeSucToText(String psChangeSucToText) {
        this.psChangeSucToText = psChangeSucToText;
    }

    public Boolean getContinueEndDateToThis() {
        return continueEndDateToThis;
    }

    public void setContinueEndDateToThis(Boolean continueEndDateToThis) {
        this.continueEndDateToThis = continueEndDateToThis;
    }

    public Boolean getNotConfirmIsPossible() {
        return notConfirmIsPossible;
    }

    public void setNotConfirmIsPossible(Boolean notConfirmIsPossible) {
        this.notConfirmIsPossible = notConfirmIsPossible;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getTypeEnText() {
        return typeEnText;
    }

    public void setTypeEnText(String typeEnText) {
        this.typeEnText = typeEnText;
    }

    public String getPsChangeFailToText() {
        return psChangeFailToText;
    }

    public void setPsChangeFailToText(String psChangeFailToText) {
        this.psChangeFailToText = psChangeFailToText;
    }

    public Boolean getNewEntityDoSave() {
        return newEntityDoSave;
    }

    public void setNewEntityDoSave(Boolean newEntityDoSave) {
        this.newEntityDoSave = newEntityDoSave;
    }

    public Boolean getCanBeChangeDate() {
        return canBeChangeDate;
    }

    public void setCanBeChangeDate(Boolean canBeChangeDate) {
        this.canBeChangeDate = canBeChangeDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getProcessEnText() {
        return processEnText;
    }

    public void setProcessEnText(String processEnText) {
        this.processEnText = processEnText;
    }

    public Integer getDelayHour() {
        return delayHour;
    }

    public void setDelayHour(Integer delayHour) {
        this.delayHour = delayHour;
    }

    public Integer getCanSaveDateBeforeMin() {
        return canSaveDateBeforeMin;
    }

    public void setCanSaveDateBeforeMin(Integer canSaveDateBeforeMin) {
        this.canSaveDateBeforeMin = canSaveDateBeforeMin;
    }

    public String getPsActionChangeSucToText() {
        return psActionChangeSucToText;
    }

    public void setPsActionChangeSucToText(String psActionChangeSucToText) {
        this.psActionChangeSucToText = psActionChangeSucToText;
    }

    public String getCostGroupEnText() {
        return costGroupEnText;
    }

    public void setCostGroupEnText(String costGroupEnText) {
        this.costGroupEnText = costGroupEnText;
    }

    public Integer getDelayMethodCal() {
        return delayMethodCal;
    }

    public void setDelayMethodCal(Integer delayMethodCal) {
        this.delayMethodCal = delayMethodCal;
    }

    public String getDelayMethodCalText() {
        return delayMethodCalText;
    }

    public void setDelayMethodCalText(String delayMethodCalText) {
        this.delayMethodCalText = delayMethodCalText;
    }

    public Integer getPsActionChangeSucTo() {
        return psActionChangeSucTo;
    }

    public void setPsActionChangeSucTo(Integer psActionChangeSucTo) {
        this.psActionChangeSucTo = psActionChangeSucTo;
    }

    public Integer getDelayDay() {
        return delayDay;
    }

    public void setDelayDay(Integer delayDay) {
        this.delayDay = delayDay;
    }

    public Integer getCostGroupEn() {
        return costGroupEn;
    }

    public void setCostGroupEn(Integer costGroupEn) {
        this.costGroupEn = costGroupEn;
    }
}
