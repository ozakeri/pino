package com.gap.bis_inspection.service;

import com.gap.bis_inspection.common.Constants;
import com.gap.bis_inspection.db.enumtype.EntityNameEn;
import com.gap.bis_inspection.db.manager.IDatabaseManager;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 10/29/15.
 */
public class CoreService {
    private IDatabaseManager databaseManager;

    public CoreService() {
    }

    public CoreService(IDatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public IDatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public void setDatabaseManager(IDatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void updateUser(User user) {
        databaseManager.updateUser(user);
    }

    public User getUserById(Long userId) {
        return databaseManager.getUserById(userId);
    }

    public DeviceSetting saveOrUpdateDeviceSetting(DeviceSetting deviceSetting) {
        return databaseManager.insertOrUpdateDeviceSetting(deviceSetting);
    }

    public DeviceSetting getDeviceSettingByKey(String key) {
        return databaseManager.getDeviceSettingByKey(key);
    }

    public boolean isDeviceDateTimeGreaterThanLastChangeDate() {
        DeviceSetting deviceSetting = databaseManager.getDeviceSettingByKey(Constants.DEVICE_SETTING_KEY_LAST_CHANGE_DATE);
        if (deviceSetting != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_TIME_FORMAT);
            try {
                Date lastChangeDate = simpleDateFormat.parse(deviceSetting.getValue());
                if (lastChangeDate.compareTo(new Date()) <= 0) {
                    return true;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public Map<String, String> getUserPermissionMap(Long userId) {
        Map<String, String> permissionMap = new HashMap<String, String>();
        List<UserPermission> userPermissionList = databaseManager.getUserPermissionListByUserId(userId);
        for (UserPermission userPermission : userPermissionList) {
            permissionMap.put(userPermission.getPermissionName(), userPermission.getPermissionName());
        }
        return permissionMap;
    }

    public ChatGroup getChatGroupById(Long id) {
        return databaseManager.getChatGroupById(id);
    }

    public ChatGroup getChatGroupDetailById(Long id) {
        ChatGroup chatGroup = databaseManager.getChatGroupById(id);
        ChatGroupMember tmpChatGroupMemberFS = new ChatGroupMember();
        tmpChatGroupMemberFS.setChatGroupId(id);
        chatGroup.setCountOfMembers(databaseManager.getChatGroupMemberCountByParam(tmpChatGroupMemberFS));
        return chatGroup;
    }

    public List<FormTemp> getFormTempList() {
        List<FormTemp> formTemps = databaseManager.getFormTempList();
        return formTemps;
    }

    public List<FormQuestionGroup> getFormQuestionGroupList() {
        List<FormQuestionGroup> formQuestionGroups = databaseManager.getFormQuestionGroupList();
        return formQuestionGroups;
    }

    public List<ChatGroup> getChatGroupList() {
        List<ChatGroup> chatGroups = databaseManager.getChatGroupList();
        return chatGroups;
    }

    public List<ChatGroup> getChatGroupListByParam(ChatGroup tmpChatGroupFS) {
        List<ChatGroup> chatGroups = databaseManager.getChatGroupListByParam(tmpChatGroupFS);
        return chatGroups;
    }

    public ChatGroup getChatGroupByServerGroupId(ChatGroup tmpChatGroupFS) {
        List<ChatGroup> chatGroups = databaseManager.getChatGroupListByParam(tmpChatGroupFS);
        ChatGroup chatGroup = null;
        if (!chatGroups.isEmpty()) {
            chatGroup = chatGroups.get(0);
        }
        return chatGroup;
    }

    public void deleteChatGroup(ChatGroup chatGroup) {
        databaseManager.deleteChatGroup(chatGroup);
    }

    public List<ChatGroup> getActiveChatGroupList() {
        List<ChatGroup> chatGroups = databaseManager.getActiveChatGroupList();
        return chatGroups;
    }


    public List<ChatGroupMember> getChatGroupMemberList() {
        List<ChatGroupMember> chatGroupMembers = databaseManager.getChatGroupMemberList();
        return chatGroupMembers;
    }

    public ChatGroup saveChatGroup(ChatGroup chatGroup) {
        return databaseManager.insertChatGroup(chatGroup);
    }

    public void updateChatGroup(ChatGroup chatGroup) {
        databaseManager.updateChatGroup(chatGroup);
    }

    public List<ChatGroupMember> getChatGroupMemberListByParam(ChatGroupMember tmpChatGroupMemberFS) {
        List<ChatGroupMember> chatGroupMembers = databaseManager.getChatGroupMemberListByParam(tmpChatGroupMemberFS);
        return chatGroupMembers;
    }

    public ChatGroupMember getChatGroupMemberByUserAndGroup(ChatGroupMember tmpChatGroupMemberFS) {
        List<ChatGroupMember> chatGroupMembers = databaseManager.getChatGroupMemberListByParam(tmpChatGroupMemberFS);
        ChatGroupMember chatGroupMember = null;
        if (!chatGroupMembers.isEmpty()) {
            chatGroupMember = chatGroupMembers.get(0);
        }
        return chatGroupMember;
    }

    public ChatGroupMember saveChatGroupMember(ChatGroupMember chatGroupMember) {
        return databaseManager.insertChatGroupMember(chatGroupMember);
    }

    public void updateChatGroupMember(ChatGroupMember chatGroupMember) {
        databaseManager.updateChatGroupMember(chatGroupMember);
    }

    public void deleteChatGroupMember(ChatGroupMember chatGroupMember) {
        databaseManager.deleteChatGroupMember(chatGroupMember);
    }

    public void deleteFormTemp() {
        databaseManager.deleteFormTemp();
    }

    public void deleteFormQuestionGroup() {
        databaseManager.deleteFormQuestionGroup();
    }

    public ChatMessage insertChatMessage(ChatMessage chatMessage) {
        return databaseManager.insertChatMessage(chatMessage);
    }

    public void updateChatMessage(ChatMessage chatMessage) {
        databaseManager.updateChatMessage(chatMessage);
    }

    public void updateChatMessageAsReadByParam(ChatMessage tmpChatMessageFS) {
        databaseManager.updateChatMessageAsReadByParam(tmpChatMessageFS);
    }

    public ChatMessage getChatMessageById(Long chatMessageId) {
        return databaseManager.getChatMessageById(chatMessageId);
    }

    public void deleteChatMessage(ChatMessage chatMessage) {
        databaseManager.deleteChatMessage(chatMessage);
    }

    public List<ChatMessage> getChatMessagesByServerMessageId(Long remoteMessageId) {
        return databaseManager.getChatMessagesByRemoteMessageId(remoteMessageId);
    }

    public ChatMessage getLastChatMessageByGroup(Long groupId) {
        ChatMessage chatMessage = databaseManager.getLastChatMessageByGroup(groupId);
        if (chatMessage != null) {
            chatMessage.setSenderAppUser(databaseManager.getAppUserById(chatMessage.getSenderAppUserId()));
        }
        return chatMessage;
    }

    public Integer getCountOfUnreadMessageByGroup(Long groupId, Long senderUserId) {
        return databaseManager.getCountOfUnreadMessageByGroup(groupId, senderUserId);
    }

    public Integer getCountOfUnreadMessage(Long senderUserId) {
        return databaseManager.getCountOfUnreadMessage(senderUserId);
    }

    public List<ChatMessage> getChatMessageListByParam(ChatMessage chatMessageFS) {
        return databaseManager.getChatMessageListByParam(chatMessageFS);
    }

    public List<ChatMessage> getChatMessageListByParamLimit(ChatMessage chatMessageFS, Integer limitSize) {
        return databaseManager.getChatMessageListByParamLimit(chatMessageFS, limitSize);
    }

    public List<ChatMessage> getUnSentChatMessageList(ChatMessage chatMessageFS) {
        return databaseManager.getUnSentChatMessageList(chatMessageFS);
    }

    public List<ChatMessage> getAttachmentResumingChatMessageList(ChatMessage chatMessageFS) {
        return databaseManager.getUnSentChatMessageList(chatMessageFS);
    }

    public List<ChatMessage> getChatGroupListNotReadNotDelivered(Long userId) {
        return databaseManager.getChatGroupListNotReadNotDelivered(userId);
    }

    public ComplaintReport getComplaintReportById(Long complaintReportId) {
        return databaseManager.getComplaintReportById(complaintReportId);
    }

    public ComplaintReport insertComplaintReport(ComplaintReport complaintReport) {
        return databaseManager.insertComplaintReport(complaintReport);
    }

    public void updateComplaintReport(ComplaintReport complaintReport) {
        databaseManager.updateComplaintReport(complaintReport);
    }

    public List<ComplaintReport> getComplaintReportListByParam(ComplaintReport complaintReportFS) {
        return databaseManager.getComplaintReportListByParam(complaintReportFS);
    }

    public List<ComplaintReport> getUnSentComplaintReportList(ComplaintReport complaintReportFS) {
        return databaseManager.getUnSentComplaintReportList(complaintReportFS);
    }

    public List<ComplaintReport> getComplaintReportListByDate(Long userReportId, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date fromDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date toDate = calendar.getTime();
        return databaseManager.getComplaintReportListByDate(userReportId, fromDate, toDate);
    }

    public AppUser insertAppUser(AppUser appUser) {
        return databaseManager.insertAppUser(appUser);
    }

    public void updateAppUser(AppUser appUser) {
        databaseManager.updateAppUser(appUser);
    }

    public void saveAppUserList(List<AppUser> appUserList) {
        databaseManager.saveAppUserList(appUserList);
    }

    public AppUser getAppUserById(Long appUserId) {
        return databaseManager.getAppUserById(appUserId);
    }

    public List<SurveyForm> getSurveyFormListByParam(SurveyForm tmpSurveyFormFS) {
        return databaseManager.getSurveyFormListByParam(tmpSurveyFormFS);
    }

    public SurveyForm getSurveyFormById(Long surveyFormId) {
        return databaseManager.getSurveyFormById(surveyFormId);
    }

    public SurveyForm saveOrUpdateSurveyForm(SurveyForm surveyForm) {
        return databaseManager.insertOrUpdateSurveyForm(surveyForm);
    }

    public void updateSurveyForm(SurveyForm surveyForm) {
        databaseManager.updateSurveyForm(surveyForm);
    }

    public SurveyFormQuestion getSurveyFormQuestionById(Long surveyFormQuestionId) {
        return databaseManager.getSurveyFormQuestionById(surveyFormQuestionId);
    }

    public SurveyFormQuestionTemp getSurveyFormQuestionTempById(Long surveyFormQuestionId) {
        return databaseManager.getSurveyFormQuestionTempById(surveyFormQuestionId);
    }

    public SurveyFormQuestion saveOrUpdateSurveyFormQuestion(SurveyFormQuestion surveyFormQuestion) {
        return databaseManager.insertOrUpdateSurveyFormQuestion(surveyFormQuestion);
    }

    public void updateSurveyFormQuestion(SurveyFormQuestion surveyFormQuestion) {
        databaseManager.updateSurveyFormQuestion(surveyFormQuestion);
    }

    public List<SurveyFormQuestion> getSurveyFormQuestionListByParam(SurveyFormQuestion tmpSurveyFormQuestionFS) {
        return databaseManager.getSurveyFormQuestionListByParam(tmpSurveyFormQuestionFS);
    }

    public List<SurveyForm> getUnSentSurveyFormList(SurveyForm tmpSurveyFormFS) {
        return databaseManager.getUnSentSurveyFormList(tmpSurveyFormFS);
    }

    public List<Form> getCheckListFormByParam(Form tmpCheckListFormFS) {
        return databaseManager.getCheckListFormByParam(tmpCheckListFormFS);
    }

    public List<Form> listForms() {
        return databaseManager.listForms();
    }

    public Form getCheckListFormById(Long checkListId) {
        return databaseManager.getCheckListFormById(checkListId);
    }

    public Form saveOrUpdateCheckListForm(Form form) {
        return databaseManager.insertOrUpdateCheckListForm(form);
    }

    public FormQuestion getCheckListFormQuestionById(Long formQuestionId) {
        return databaseManager.getCheckListFormQuestionById(formQuestionId);
    }

    public FormTemp getCheckListFormTempById(Long formQuestionId) {
        return databaseManager.getCheckListFormTempById(formQuestionId);
    }

    public FormQuestionGroup getCheckListFormQuestionGroupById(Long formQuestionId) {
        return databaseManager.getCheckListFormQuestionGroupById(formQuestionId);
    }

    public FormQuestionGroupForm getFormQuestionGroupFormByFormId(Long id) {
        return databaseManager.getFormQuestionGroupFormByFormId(id);
    }

    public void saveOrUpdateCheckListFormQuestion(FormQuestion formQuestion) {
        databaseManager.insertOrUpdateChecklistFormQuestion(formQuestion);
    }

    public void saveOrUpdateCheckListFormTemp(FormTemp formTemp) {
        databaseManager.saveOrUpdateCheckListFormTemp(formTemp);
    }

    public void saveOrUpdateSurveyFormQuestionTemp(SurveyFormQuestionTemp surveyFormQuestionTemp) {
        databaseManager.saveOrUpdateSurveyFormQuestionTemp(surveyFormQuestionTemp);
    }

    public FormQuestionGroup saveOrUpdateCheckListFormQuestionGroup(FormQuestionGroup formQuestionGroup) {
        return databaseManager.insertOrUpdateChecklistFormQuestionGroup(formQuestionGroup);
    }

    public FormQuestionGroupForm saveOrUpdateFormQuestionGroupForm(FormQuestionGroupForm formQuestionGroupForm) {
        return databaseManager.saveOrUpdateFormQuestionGroupForm(formQuestionGroupForm);
    }

    public List<FormQuestion> getFormQuestionListByParam(FormQuestion tmpFormQuestionFS) {
        return databaseManager.getFormQuestionListByParam(tmpFormQuestionFS);
    }


    public List<FormTemp> getFormTempListByFormIdTest(FormTemp formTemp) {
        return databaseManager.getFormTempListByFormIdTest(formTemp);
    }

    public List<FormQuestionGroup> getFormQuestionGroupListByParam(FormQuestionGroup tmpFormQuestionGroupFS) {
        return databaseManager.getFormQuestionGroupListByParam(tmpFormQuestionGroupFS);
    }

    public void insertFormAnswer(FormAnswer formAnswer) {
        databaseManager.insertFormAnswer(formAnswer);
    }

    public List<FormAnswer> getFormAnswerListByParam(FormAnswer tmpFormAnswerFS) {
        return databaseManager.getFormAnswerListByParam(tmpFormAnswerFS);
    }

    public void insertNewFormAnswer(FormItemAnswer formItemAnswer) {
        databaseManager.insertNewFormAnswer(formItemAnswer);
    }

    public void insertNewFormQuestionGroup(FormQuestionGroup formQuestionGroup) {
        databaseManager.insertNewFormQuestionGroup(formQuestionGroup);
    }

    public void insertNewFormQuestionGroupForm(FormQuestionGroupForm formQuestionGroupForm) {
        databaseManager.insertNewFormQuestionGroupForm(formQuestionGroupForm);
    }

    public void insertNewFormTemp(FormTemp formTemp) {
        databaseManager.insertNewFormTemp(formTemp);
    }

    public List<FormItemAnswer> getFormItemAnswerListByParam(FormItemAnswer tmpFormItemAnswerFS) {
        return databaseManager.getFormItemAnswerListByParam(tmpFormItemAnswerFS);
    }

    public FormAnswer getFormAnswerById(Long id) {
        return databaseManager.getFormAnswerById(id);
    }

    public FormItemAnswer getFormItemAnswerById(Long formItemAnswerId) {
        return databaseManager.getFormItemAnswerById(formItemAnswerId);
    }

    public void updateFormItemAnswer(FormItemAnswer formItemAnswer) {
        databaseManager.updateFormItemAnswer(formItemAnswer);
    }

    public void updateFormTemp(FormTemp formTemp) {
        databaseManager.updateFormTemp(formTemp);
    }

    public void updateSurveyFormQuestionTemp(SurveyFormQuestionTemp surveyFormQuestionTemp) {
        databaseManager.updateSurveyFormQuestionTemp(surveyFormQuestionTemp);
    }

    public List<FormItemAnswer> getFormItemAnswerListById(Long id) {
        return databaseManager.getFormItemAnswerListById(id);
    }

    public List<FormAnswer> getFormAnswerListById(Long id) {
        return databaseManager.getFormAnswerListById(id);
    }

    public List<FormTemp> getFormTempListById(Long id) {
        return databaseManager.getFormTempListById(id);
    }

    public List<SurveyFormQuestionTemp> getSurveyFormQuestionTempListById(Long id) {
        return databaseManager.getSurveyFormQuestionTempListById(id);
    }

    public List<SurveyFormQuestionTemp> getSurveyFormQuestionTempListByGroupId(Long id) {
        return databaseManager.getSurveyFormQuestionTempListByGroupId(id);
    }

    public List<FormQuestionGroup> getFormQuestionGroupListById(Long id) {
        return databaseManager.getFormQuestionGroupListById(id);
    }

    public List<FormQuestionGroupForm> getFormQuestionGroupFormById(Long id) {
        return databaseManager.getFormQuestionGroupFormById(id);
    }

    public List<FormItemAnswer> getFormItemAnswerListByGroupId(Long groupId, Long formAnswerId) {
        return databaseManager.getFormItemAnswerListByGroupId(groupId, formAnswerId);
    }

    public List<FormTemp> getFormTempListByGroupId(Long groupId, Long formAnswerId) {
        return databaseManager.getFormTempListByGroupId(groupId, formAnswerId);
    }

    public List<FormTemp> getFormTempListByFormId(Long groupId, Long formId) {
        return databaseManager.getFormTempListByFormId(groupId, formId);
    }

    public void updateFormAnswer(FormAnswer formAnswer) {
        databaseManager.updateFormAnswer(formAnswer);
    }

    public List<FormAnswer> getUnSentFormAnswerList(FormAnswer tmpFormAnswerFS) {
        return databaseManager.getUnSentFormAnswerList(tmpFormAnswerFS);
    }

    public List<AttachFile> getPendingAttachFileByEntityId(EntityNameEn entityNameEn, Long entityId) {
        return databaseManager.getPendingAttachFileByEntityId(entityNameEn, entityId);
    }

    public AttachFile insertAttachFile(AttachFile attachFile) {
        return databaseManager.insertAttachFile(attachFile);
    }

    public List<AttachFile> getUnSentAttachFileList() {
        return databaseManager.getUnSentAttachFileList();
    }

    public void updateAttachFile(AttachFile attachFile) {
        databaseManager.updateAttachFile(attachFile);
    }

    public List<FormQuestion> getFormQuestionListByGroupId(Long id1, Long id2) {
        return databaseManager.getFormQuestionListByGroupId(id1, id2);
    }

    public List<SurveyFormQuestion> getSurveyFormQuestionListByGroupId(Long id1, Long id2) {
        return databaseManager.getSurveyFormQuestionListByGroupId(id1, id2);
    }

    public AttachFile getAttachFileById(Long id) {
        return databaseManager.getAttachFileById(id);
    }

    public List<AttachFile> getAttachFileListById(Long id) {
        return databaseManager.getAttachFileListById(id);
    }

    public void deleteAttachFile(AttachFile attachFile) {
        databaseManager.deleteAttachFile(attachFile);
    }

    public boolean deleteAttachFileById(Long attachId) {
        return databaseManager.deleteAttachFileById(attachId);
    }
}
