package com.gap.bis_inspection.db.manager;

import com.gap.bis_inspection.db.enumtype.EntityNameEn;
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

import java.util.Date;
import java.util.List;

/**
 * Interface that provides methods for managing the database inside the Application.
 *
 * @author Octa
 */
public interface IDatabaseManager {

    void closeDbConnections();

    void createDatabase();

    void dropDatabase();

    User insertUser(User user);

    UserPermission insertPermission(UserPermission userPermission);

    void deleteUserPermission(Long userId, String permissionName);

    List<User> listUsers();

    void updateUser(User user);

    User insertOrUpdateUser(User user);

    void deleteUserByUsername(String username);

    boolean deleteUserById(Long userId);

    User getUserById(Long userId);

    User getUserByMobileNo(String mobileNo);

    User getUserByDriverCode(String drivercode);

    void deleteUsers();

    DeviceSetting insertOrUpdateDeviceSetting(DeviceSetting deviceSetting);

    DeviceSetting getDeviceSettingByKey(String key);

    List<UserPermission> getUserPermissionListByUserId(Long userId);

    ChatGroup getChatGroupById(Long id);

    List<ChatGroup> getChatGroupList();

    List<FormTemp> getFormTempList();

    List<FormQuestionGroup> getFormQuestionGroupList();

    List<ChatGroup> getChatGroupListByParam(ChatGroup tmpChatGroupFS);

    List<ChatGroup> getActiveChatGroupList();

    List<ChatGroupMember> getChatGroupMemberList();

    ChatGroup insertChatGroup(ChatGroup chatGroup);

    void deleteChatGroup(ChatGroup chatGroup);

    void updateChatGroup(ChatGroup chatGroup);

    List<ChatGroupMember> getChatGroupMemberListByParam(ChatGroupMember tmpChatGroupMemberFS);

    Integer getChatGroupMemberCountByParam(ChatGroupMember tmpChatGroupMemberFS);

    ChatGroupMember insertChatGroupMember(ChatGroupMember chatGroupMember);

    void updateChatGroupMember(ChatGroupMember chatGroupMember);

    void deleteChatGroupMember(ChatGroupMember chatGroupMember);

    void deleteFormTemp();

    void deleteFormQuestionGroup();

    ChatMessage getChatMessageById(Long chatMessageId);

    void deleteChatMessage(ChatMessage chatMessage);

    List<ChatMessage> getChatMessagesByRemoteMessageId(Long remoteMessageId);

    ChatMessage insertChatMessage(ChatMessage chatMessage);

    void updateChatMessage(ChatMessage chatMessage);

    void updateChatMessageAsReadByParam(ChatMessage tmpChatMessageFS);

    ChatMessage getLastChatMessageByGroup(Long groupId);

    Integer getCountOfUnreadMessageByGroup(Long groupId, Long senderUserId);

    Integer getCountOfUnreadMessage(Long senderUserId);

    List<ChatMessage> getChatMessageListByParam(ChatMessage chatMessageFS);

    List<ChatMessage> getChatMessageListByParamLimit(ChatMessage chatMessageFS, Integer limitSize);

    List<ChatMessage> getUnSentChatMessageList(ChatMessage tmpChatMessageFS);

    List<ChatMessage> getAttachmentResumingChatMessageList(ChatMessage tmpChatMessageFS);

    List<ChatMessage> getChatGroupListNotReadNotDelivered(Long userId);

    ComplaintReport getComplaintReportById(Long complaintReportId);

    ComplaintReport insertComplaintReport(ComplaintReport complaintReport);

    void updateComplaintReport(ComplaintReport complaintReport);

    List<ComplaintReport> getComplaintReportListByParam(ComplaintReport complaintReportFS);

    List<ComplaintReport> getUnSentComplaintReportList(ComplaintReport complaintReportFS);

    AppUser insertAppUser(AppUser appUser);

    void updateAppUser(AppUser appUser);

    void saveAppUserList(List<AppUser> appUserList);

    List<ComplaintReport> getComplaintReportListByDate(Long userReportId, Date fromDate, Date toDate);

    AppUser getAppUserById(Long id);

    List<SurveyForm> getSurveyFormListByParam(SurveyForm tmpSurveyFormFS);

    SurveyForm getSurveyFormById(Long id);

    SurveyForm insertOrUpdateSurveyForm(SurveyForm surveyForm);

    void updateSurveyForm(SurveyForm surveyForm);

    SurveyFormQuestion getSurveyFormQuestionById(Long id);

    SurveyFormQuestionTemp getSurveyFormQuestionTempById(Long id);

    SurveyFormQuestion insertOrUpdateSurveyFormQuestion(SurveyFormQuestion surveyFormQuestion);

    void updateSurveyFormQuestion(SurveyFormQuestion surveyFormQuestion);

    List<SurveyFormQuestion> getSurveyFormQuestionListByParam(SurveyFormQuestion tmpSurveyFormQuestionFS);

    List<SurveyForm> getUnSentSurveyFormList(SurveyForm surveyFormFS);

    List<Form> getCheckListFormByParam(Form tmpCheckListFormFS);

    List<Form> listForms();

    Form getCheckListFormById(Long id);

    Form insertOrUpdateCheckListForm(Form form);

    FormQuestion getCheckListFormQuestionById(Long id);

    FormTemp getCheckListFormTempById(Long id);

    FormQuestionGroup getCheckListFormQuestionGroupById(Long id);

    FormQuestionGroupForm getFormQuestionGroupFormByFormId(Long id);

    FormQuestion insertOrUpdateChecklistFormQuestion(FormQuestion formQuestion);

    FormTemp saveOrUpdateCheckListFormTemp(FormTemp formTemp);

    SurveyFormQuestionTemp saveOrUpdateSurveyFormQuestionTemp(SurveyFormQuestionTemp surveyFormQuestionTemp);

    FormQuestionGroup insertOrUpdateChecklistFormQuestionGroup(FormQuestionGroup formQuestionGroup);

    FormQuestionGroupForm saveOrUpdateFormQuestionGroupForm(FormQuestionGroupForm formQuestionGroupForm);

    List<FormQuestion> getFormQuestionListByParam(FormQuestion tmpFormQuestionFS);

    List<FormTemp> getFormTempListByFormIdTest(FormTemp formTemp);

    List<FormQuestionGroup> getFormQuestionGroupListByParam(FormQuestionGroup tmpFormQuestionGroupFS);

    FormAnswer insertFormAnswer(FormAnswer formAnswer);

    List<FormAnswer> getFormAnswerListByParam(FormAnswer tmpFormAnswerFS);

    FormItemAnswer insertNewFormAnswer(FormItemAnswer formItemAnswer);

    FormQuestionGroup insertNewFormQuestionGroup(FormQuestionGroup formQuestionGroup);

    FormQuestionGroupForm insertNewFormQuestionGroupForm(FormQuestionGroupForm formQuestionGroupForm);

    FormTemp insertNewFormTemp(FormTemp formTemp);

    List<FormItemAnswer> getFormItemAnswerListByParam(FormItemAnswer tmpFormItemAnswerFS);

    FormAnswer getFormAnswerById(Long id);

    FormItemAnswer getFormItemAnswerById(Long id);

    void updateFormItemAnswer(FormItemAnswer formItemAnswer);

    void updateFormTemp(FormTemp formTemp);

    void updateSurveyFormQuestionTemp(SurveyFormQuestionTemp surveyFormQuestionTemp);

    List<FormItemAnswer> getFormItemAnswerListById(Long id);

    List<FormAnswer> getFormAnswerListById(Long id);

    List<FormTemp> getFormTempListById(Long id);

    List<SurveyFormQuestionTemp> getSurveyFormQuestionTempListById(Long id);

    List<SurveyFormQuestionTemp> getSurveyFormQuestionTempListByGroupId(Long id);

    List<FormQuestionGroup> getFormQuestionGroupListById(Long id);

    List<FormQuestionGroupForm> getFormQuestionGroupFormById(Long id);

    List<FormItemAnswer> getFormItemAnswerListByGroupId(Long groupId, Long formAnswerId);

    List<FormTemp> getFormTempListByGroupId(Long groupId, Long formAnswerId);

    List<FormTemp> getFormTempListByFormId(Long groupId, Long formId);

    void updateFormAnswer(FormAnswer formAnswer);

    List<FormAnswer> getUnSentFormAnswerList(FormAnswer formAnswerFS);

    AttachFile insertAttachFile(AttachFile attachFile);

    List<AttachFile> getPendingAttachFileByEntityId(EntityNameEn entityNameEn, Long entityId);

    List<AttachFile> getUnSentAttachFileList();

    void updateAttachFile(AttachFile attachFile);

    AttachFile getAttachFileById(Long id);

    List<AttachFile> getAttachFileListById(Long id);

    void deleteAttachFile(AttachFile attachFile);

    boolean deleteAttachFileById(Long attachId);

    List<FormQuestion> getFormQuestionListByGroupId(Long id1, Long id2);

    List<SurveyFormQuestion> getSurveyFormQuestionListByGroupId(Long id1, Long id2);

}
