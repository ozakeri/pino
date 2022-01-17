package com.gap.bis_inspection.db.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.gap.bis_inspection.db.dao.AppUserDao;
import com.gap.bis_inspection.db.dao.AttachFileDao;
import com.gap.bis_inspection.db.dao.ChatGroupDao;
import com.gap.bis_inspection.db.dao.ChatGroupMemberDao;
import com.gap.bis_inspection.db.dao.ChatMessageDao;
import com.gap.bis_inspection.db.dao.ComplaintReportDao;
import com.gap.bis_inspection.db.dao.DaoMaster;
import com.gap.bis_inspection.db.dao.DaoSession;
import com.gap.bis_inspection.db.dao.DeviceSettingDao;
import com.gap.bis_inspection.db.dao.FormAnswerDao;
import com.gap.bis_inspection.db.dao.FormDao;
import com.gap.bis_inspection.db.dao.FormItemAnswerDao;
import com.gap.bis_inspection.db.dao.FormQuestionDao;
import com.gap.bis_inspection.db.dao.FormQuestionGroupDao;
import com.gap.bis_inspection.db.dao.FormQuestionGroupFormDao;
import com.gap.bis_inspection.db.dao.FormTempDao;
import com.gap.bis_inspection.db.dao.SurveyFormDao;
import com.gap.bis_inspection.db.dao.SurveyFormQuestionDao;
import com.gap.bis_inspection.db.dao.SurveyFormQuestionTempDao;
import com.gap.bis_inspection.db.dao.UserDao;
import com.gap.bis_inspection.db.dao.UserPermissionDao;
import com.gap.bis_inspection.db.enumtype.EntityNameEn;
import com.gap.bis_inspection.db.enumtype.GeneralStatus;
import com.gap.bis_inspection.db.enumtype.SendingStatusEn;
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

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import de.greenrobot.dao.async.AsyncOperation;
import de.greenrobot.dao.async.AsyncOperationListener;
import de.greenrobot.dao.async.AsyncSession;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;

/**
 * @author Octa
 */
public class DatabaseManager implements IDatabaseManager, AsyncOperationListener {

    private final String DATABASE_NAME = "database.db";
    public static Long SERVER_USER_ID;
    /**
     * Class tag. Used for debug.
     */
    private static final String TAG = DatabaseManager.class.getCanonicalName();
    /**
     * Instance of DatabaseManager
     */
    private static DatabaseManager instance;
    /**
     * The Android Activity reference for access to DatabaseManager.
     */
    private Context context;
    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase database;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private AsyncSession asyncSession;
    private List<AsyncOperation> completedOperations;

    /**
     * Constructs a new DatabaseManager with the specified arguments.
     *
     * @param context The Android {@link android.content.Context}.
     */
    public DatabaseManager(final Context context) {
        this.context = context;
        mHelper = new DaoMaster.DevOpenHelper(this.context, DATABASE_NAME, null);
        completedOperations = new CopyOnWriteArrayList<AsyncOperation>();
    }

    /**
     * @param context The Android {@link android.content.Context}.
     * @return this.instance
     */
    public static DatabaseManager getInstance(Context context) {

        if (instance == null) {
            instance = new DatabaseManager(context);
        }

        return instance;
    }

    @Override
    public void onAsyncOperationCompleted(AsyncOperation operation) {
        completedOperations.add(operation);
    }

    private void assertWaitForCompletion1Sec() {
        asyncSession.waitForCompletion(1000);
        asyncSession.isCompleted();
    }

    /**
     * Query for readable DB
     */
    public void openReadableDb() throws SQLiteException {
        database = mHelper.getReadableDatabase();
        daoMaster = new DaoMaster(database);
        daoSession = daoMaster.newSession();
        asyncSession = daoSession.startAsyncSession();
        asyncSession.setListener(this);
    }

    /**
     * Query for writable DB
     */
    public void openWritableDb() throws SQLiteException {
        database = mHelper.getWritableDatabase();
        daoMaster = new DaoMaster(database);
        daoSession = daoMaster.newSession();
        asyncSession = daoSession.startAsyncSession();
        asyncSession.setListener(this);
    }

    @Override
    public void closeDbConnections() {
        if (daoSession != null) {
            daoSession.clear();
            daoSession = null;
        }
        if (database != null && database.isOpen()) {
            database.close();
        }
        if (mHelper != null) {
            mHelper.close();
            mHelper = null;
        }
        if (instance != null) {
            instance = null;
        }
    }

    @Override
    public synchronized void createDatabase() {
        try {
            openWritableDb();
            mHelper.onCreate(database);              // creates the tables
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void dropDatabase() {
        try {
            openWritableDb();
            Log.d(TAG, "Old db version: " + database.getVersion());

            DaoMaster.dropAllTables(database, true); // drops all tables
            mHelper.onCreate(database);              // creates the tables
            asyncSession.deleteAll(User.class);    // clear all elements from a table
            asyncSession.deleteAll(DeviceSetting.class);    // clear all elements from a table
//            context.deleteDatabase(DATABASE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Long generateNewId() {
        return Long.valueOf(new Date().getTime() + SERVER_USER_ID.toString());
    }

    @Override
    public synchronized User insertUser(User user) {
        try {
            if (user != null) {
                openWritableDb();
                UserDao userDao = daoSession.getUserDao();
                userDao.insert(user);
                Log.d(TAG, "Inserted user: " + user.getId() + " to the schema.");
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }


    @Override
    public synchronized UserPermission insertPermission(UserPermission userPermission) {
        try {
            if (userPermission != null) {
                openWritableDb();
                userPermission.setId(generateNewId());
                UserPermissionDao userPermissionDao = daoSession.getUserPermissionDao();
                userPermissionDao.insert(userPermission);
                Log.d(TAG, "Inserted userPermission: " + userPermission.getId() + " to the schema.");
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userPermission;
    }

    @Override
    public synchronized void deleteUserPermission(Long userId, String permissionName) {
        try {
            openWritableDb();
            UserPermissionDao userPermissionDao = daoSession.getUserPermissionDao();
            WhereCondition condition = userPermissionDao.queryBuilder().and(UserPermissionDao.Properties.UserId.eq(userId), UserPermissionDao.Properties.PermissionName.eq(permissionName));
            QueryBuilder<UserPermission> queryBuilder = userPermissionDao.queryBuilder().where(condition);
            List<UserPermission> userPermissionToDelete = queryBuilder.list();
            for (UserPermission userPermission1 : userPermissionToDelete) {
                userPermissionDao.delete(userPermission1);
            }
            daoSession.clear();
            Log.d(TAG, userPermissionToDelete.size() + " entry. " + "Deleted userPermission: " + permissionName + "-" + userId + " from the schema.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized List<User> listUsers() {
        List<User> users = null;
        try {
            openReadableDb();
            UserDao userDao = daoSession.getUserDao();
            users = userDao.loadAll();

            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }


    @Override
    public synchronized void updateUser(User user) {
        try {
            if (user != null) {
                openWritableDb();
                daoSession.update(user);
                Log.d(TAG, "Updated user: " + user.getId() + " from the schema.");
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized User insertOrUpdateUser(User user) {
        try {
            if (user != null) {
                openWritableDb();
                UserDao userDao = daoSession.getUserDao();
                userDao.insertOrReplace(user);
                Log.d(TAG, "Inserted or replace user: " + user.getId() + " to the schema.");
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public synchronized void deleteUserByUsername(String username) {
        try {
            openWritableDb();
            UserDao userDao = daoSession.getUserDao();
            QueryBuilder<User> queryBuilder = userDao.queryBuilder().where(UserDao.Properties.Username.eq(username));
            List<User> userToDelete = queryBuilder.list();
            for (User user : userToDelete) {
                userDao.delete(user);
            }
            daoSession.clear();
            Log.d(TAG, userToDelete.size() + " entry. " + "Deleted user: " + username + " from the schema.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized boolean deleteUserById(Long userId) {
        try {
            openWritableDb();
            UserDao userDao = daoSession.getUserDao();
            userDao.deleteByKey(userId);
            daoSession.clear();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public synchronized User getUserById(Long userId) {
        User user = null;
        try {
            openReadableDb();
            UserDao userDao = daoSession.getUserDao();
            user = userDao.loadByRowId(userId);
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public synchronized User getUserByMobileNo(String mobileNo) {
        User user = null;
        try {
            openReadableDb();

            UserDao userDao = daoSession.getUserDao();
            WhereCondition condition = UserDao.Properties.MobileNo.eq(mobileNo);

            QueryBuilder<User> queryBuilder = userDao.queryBuilder().where(condition);
            List<User> userList = queryBuilder.list();
            if (!userList.isEmpty()) {
                user = userList.get(0);
            }
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public synchronized User getUserByDriverCode(String drivercode) {
        User user = null;
        try {
            openReadableDb();

            UserDao userDao = daoSession.getUserDao();
            WhereCondition condition = UserDao.Properties.MobileNo.eq(drivercode);

            QueryBuilder<User> queryBuilder = userDao.queryBuilder().where(condition);
            List<User> userList = queryBuilder.list();
            if (!userList.isEmpty()) {
                user = userList.get(0);
            }
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public synchronized void deleteUsers() {
        try {
            openWritableDb();
            UserDao userDao = daoSession.getUserDao();
            userDao.deleteAll();
            daoSession.clear();
            Log.d(TAG, "Delete all users from the schema.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public DeviceSetting insertOrUpdateDeviceSetting(DeviceSetting deviceSetting) {
        try {
            openWritableDb();
            DeviceSettingDao deviceSettingDao = daoSession.getDeviceSettingDao();
            deviceSettingDao.insertOrReplace(deviceSetting);
            Log.d(TAG, "Inserted or Replace syncHistory: " + deviceSetting.getKey() + " to the schema.");
            daoSession.clear();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return deviceSetting;
    }

    @Override
    public synchronized DeviceSetting getDeviceSettingByKey(String key) {
        DeviceSetting deviceSetting = null;
        try {
            openReadableDb();
            DeviceSettingDao deviceSettingDao = daoSession.getDeviceSettingDao();
            QueryBuilder<DeviceSetting> queryBuilder = deviceSettingDao.queryBuilder();
            queryBuilder.where(DeviceSettingDao.Properties.Key.eq(key));
            List<DeviceSetting> deviceSettings = queryBuilder.list();
            if (!deviceSettings.isEmpty()) {
                deviceSetting = deviceSettings.get(0);
            }

            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deviceSetting;
    }

    @Override
    public synchronized List<UserPermission> getUserPermissionListByUserId(Long userId) {
        List<UserPermission> userPermissionList = null;
        try {
            openReadableDb();
            UserPermissionDao userPermissionDao = daoSession.getUserPermissionDao();
            QueryBuilder<UserPermission> queryBuilder = userPermissionDao.queryBuilder();
            queryBuilder.where(UserPermissionDao.Properties.UserId.eq(userId));
            userPermissionList = queryBuilder.list();

            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userPermissionList;
    }

    @Override
    public synchronized ChatGroup getChatGroupById(Long id) {
        ChatGroup chatGroup = null;
        try {
            openReadableDb();
            ChatGroupDao chatGroupDao = daoSession.getChatGroupDao();
            chatGroup = chatGroupDao.loadByRowId(id);
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatGroup;
    }

    @Override
    public synchronized List<ChatGroup> getChatGroupList() {
        List<ChatGroup> chatGroupList = null;
        try {
            openReadableDb();
            ChatGroupDao chatGroupDao = daoSession.getChatGroupDao();
            QueryBuilder<ChatGroup> queryBuilder = chatGroupDao.queryBuilder();
            chatGroupList = queryBuilder.list();

            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatGroupList;
    }

    @Override
    public synchronized List<FormTemp> getFormTempList() {
        List<FormTemp> formTemps = null;
        try {
            openReadableDb();
            FormTempDao formTempDao = daoSession.getFormTempDao();
            QueryBuilder<FormTemp> queryBuilder = formTempDao.queryBuilder();
            formTemps = queryBuilder.list();

            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formTemps;
    }

    @Override
    public synchronized List<FormQuestionGroup> getFormQuestionGroupList() {
        List<FormQuestionGroup> formQuestionGroups = null;
        try {
            openReadableDb();
            FormQuestionGroupDao formQuestionGroupDao = daoSession.getFormQuestionGroupDao();
            QueryBuilder<FormQuestionGroup> queryBuilder = formQuestionGroupDao.queryBuilder();
            formQuestionGroups = queryBuilder.list();

            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formQuestionGroups;
    }

    @Override
    public synchronized List<ChatGroup> getChatGroupListByParam(ChatGroup tmpChatGroupFS) {
        List<ChatGroup> chatGroups = null;
        try {
            openReadableDb();
            ChatGroupDao chatGroupDao = daoSession.getChatGroupDao();
            QueryBuilder<ChatGroup> queryBuilder = chatGroupDao.queryBuilder();
            if (tmpChatGroupFS.getId() != null) {
                queryBuilder.where(ChatGroupDao.Properties.Id.eq(tmpChatGroupFS.getId()));
            }
            if (tmpChatGroupFS.getServerGroupId() != null) {
                queryBuilder.where(ChatGroupDao.Properties.ServerGroupId.eq(tmpChatGroupFS.getServerGroupId()));
            }
            if (tmpChatGroupFS.getNotServerGroupIdList() != null && !tmpChatGroupFS.getNotServerGroupIdList().isEmpty()) {
                queryBuilder.where(ChatGroupDao.Properties.ServerGroupId.notIn(tmpChatGroupFS.getNotServerGroupIdList()));
            }

            chatGroups = queryBuilder.list();

            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatGroups;
    }

    @Override
    public synchronized List<ChatGroup> getActiveChatGroupList() {
        List<ChatGroup> chatGroupList = null;
        try {
            openReadableDb();
            ChatGroupDao chatGroupDao = daoSession.getChatGroupDao();
            QueryBuilder<ChatGroup> queryBuilder = chatGroupDao.queryBuilder();
            queryBuilder.where(ChatGroupDao.Properties.StatusEn.eq(GeneralStatus.Active.ordinal()));
            chatGroupList = queryBuilder.list();

            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatGroupList;
    }


    @Override
    public synchronized List<ChatGroupMember> getChatGroupMemberList() {
        List<ChatGroupMember> chatGroupMemberList = null;
        try {
            openReadableDb();
            ChatGroupMemberDao chatGroupMemberDao = daoSession.getChatGroupMemberDao();
            QueryBuilder<ChatGroupMember> queryBuilder = chatGroupMemberDao.queryBuilder();
            chatGroupMemberList = queryBuilder.list();

            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatGroupMemberList;
    }

    @Override
    public synchronized ChatGroup insertChatGroup(ChatGroup chatGroup) {
        try {
            if (chatGroup != null) {
                openWritableDb();
                chatGroup.setId(generateNewId());
                ChatGroupDao chatGroupDao = daoSession.getChatGroupDao();
                chatGroupDao.insert(chatGroup);
                Log.d(TAG, "Inserted ChatGroup: " + chatGroup.getId() + " to the schema.");
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatGroup;
    }

    @Override
    public synchronized void updateChatGroup(ChatGroup chatGroup) {
        try {
            if (chatGroup != null) {
                openWritableDb();
                daoSession.update(chatGroup);
                Log.d(TAG, "Updated ChatGroup: " + chatGroup.getId() + " from the schema.");
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized List<ChatGroupMember> getChatGroupMemberListByParam(ChatGroupMember tmpChatGroupMemberFS) {
        List<ChatGroupMember> chatGroupMembers = null;
        try {
            openReadableDb();
            ChatGroupMemberDao chatGroupMemberDao = daoSession.getChatGroupMemberDao();
            QueryBuilder<ChatGroupMember> queryBuilder = chatGroupMemberDao.queryBuilder();
            if (tmpChatGroupMemberFS.getId() != null) {
                queryBuilder.where(ChatGroupMemberDao.Properties.Id.eq(tmpChatGroupMemberFS.getId()));
            }
            if (tmpChatGroupMemberFS.getChatGroupId() != null) {
                queryBuilder.where(ChatGroupMemberDao.Properties.ChatGroupId.eq(tmpChatGroupMemberFS.getChatGroupId()));
            }
            if (tmpChatGroupMemberFS.getAppUserId() != null) {
                queryBuilder.where(ChatGroupMemberDao.Properties.AppUserId.eq(tmpChatGroupMemberFS.getAppUserId()));
            }
            if (tmpChatGroupMemberFS.getNotAppUserIdList() != null && !tmpChatGroupMemberFS.getNotAppUserIdList().isEmpty()) {
                queryBuilder.where(ChatGroupMemberDao.Properties.AppUserId.notIn(tmpChatGroupMemberFS.getNotAppUserIdList()));
            }
            chatGroupMembers = queryBuilder.list();

            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatGroupMembers;
    }

    @Override
    public synchronized Integer getChatGroupMemberCountByParam(ChatGroupMember tmpChatGroupMemberFS) {
        Integer count = 0;
        try {
            openReadableDb();
            ChatGroupMemberDao chatGroupMemberDao = daoSession.getChatGroupMemberDao();
            QueryBuilder<ChatGroupMember> queryBuilder = chatGroupMemberDao.queryBuilder();
            if (tmpChatGroupMemberFS.getChatGroupId() != null) {
                queryBuilder.where(ChatGroupMemberDao.Properties.ChatGroupId.eq(tmpChatGroupMemberFS.getChatGroupId()));
            }

            count = Long.valueOf(queryBuilder.count()).intValue();

            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    @Override
    public synchronized ChatGroupMember insertChatGroupMember(ChatGroupMember chatGroupMember) {
        try {
            if (chatGroupMember != null) {
                openWritableDb();
                chatGroupMember.setId(generateNewId());
                ChatGroupMemberDao chatGroupMemberDao = daoSession.getChatGroupMemberDao();
                chatGroupMemberDao.insert(chatGroupMember);
                Log.d(TAG, "Inserted ChatGroupMember: " + chatGroupMember.getId() + " to the schema.");
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatGroupMember;
    }

    @Override
    public synchronized void updateChatGroupMember(ChatGroupMember chatGroupMember) {
        try {
            if (chatGroupMember != null) {
                openWritableDb();
                daoSession.update(chatGroupMember);
                Log.d(TAG, "Updated ChatGroupMember: " + chatGroupMember.getId() + " from the schema.");
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void deleteChatGroup(ChatGroup chatGroup) {
        try {
            if (chatGroup != null) {
                openWritableDb();
                daoSession.delete(chatGroup);
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void deleteChatGroupMember(ChatGroupMember chatGroupMember) {
        try {
            if (chatGroupMember != null) {
                openWritableDb();
                daoSession.delete(chatGroupMember);
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void deleteFormTemp() {
        try {
            openWritableDb();
            FormTempDao formTempDao = daoSession.getFormTempDao();
            formTempDao.deleteAll();
            daoSession.clear();
            Log.d(TAG, "Delete all users from the schema.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void deleteFormQuestionGroup() {
        try {
            openWritableDb();
            FormQuestionDao formQuestionDao = daoSession.getFormQuestionDao();
            formQuestionDao.deleteAll();
            daoSession.clear();
            Log.d(TAG, "Delete all formTemp the schema.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized ChatMessage getChatMessageById(Long chatMessageId) {
        ChatMessage chatMessage = null;
        try {
            openReadableDb();
            ChatMessageDao chatMessageDao = daoSession.getChatMessageDao();
            chatMessage = chatMessageDao.loadByRowId(chatMessageId);
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatMessage;
    }

    @Override
    public synchronized void deleteChatMessage(ChatMessage chatMessage) {
        try {
            if (chatMessage != null) {
                openWritableDb();
                daoSession.delete(chatMessage);
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized List<ChatMessage> getChatMessagesByRemoteMessageId(Long remoteMessageId) {
        List<ChatMessage> chatMessageList = null;
        try {
            openReadableDb();
            ChatMessageDao chatMessageDao = daoSession.getChatMessageDao();
            QueryBuilder<ChatMessage> queryBuilder = chatMessageDao.queryBuilder();
            queryBuilder.where(ChatMessageDao.Properties.ServerMessageId.eq(remoteMessageId));
            chatMessageList = queryBuilder.list();

            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatMessageList;
    }

    @Override
    public synchronized ChatMessage insertChatMessage(ChatMessage chatMessage) {
        try {
            if (chatMessage != null) {
                openWritableDb();
                chatMessage.setId(generateNewId());
                ChatMessageDao chatMessageDao = daoSession.getChatMessageDao();
                chatMessageDao.insert(chatMessage);
                Log.d(TAG, "Inserted ChatMessage: " + chatMessage.getId() + " to the schema.");
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatMessage;
    }

    @Override
    public synchronized void updateChatMessage(ChatMessage chatMessage) {
        try {
            if (chatMessage != null) {
                openWritableDb();
                daoSession.update(chatMessage);
                Log.d(TAG, "Updated ChatMessage: " + chatMessage.getId() + " from the schema.");
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void updateChatMessageAsReadByParam(ChatMessage tmpChatMessageFS) {
        try {
            openReadableDb();
            ChatMessageDao chatMessageDao = daoSession.getChatMessageDao();
            QueryBuilder<ChatMessage> queryBuilder = chatMessageDao.queryBuilder();
            if (tmpChatMessageFS.getId() != null) {
                queryBuilder.where(ChatMessageDao.Properties.Id.eq(tmpChatMessageFS.getId()));
            }
            if (tmpChatMessageFS.getChatGroupId() != null) {
                queryBuilder.where(ChatMessageDao.Properties.ChatGroupId.eq(tmpChatMessageFS.getChatGroupId()));
            }
            if (tmpChatMessageFS.getSenderAppUserId() != null) {
                queryBuilder.where(ChatMessageDao.Properties.SenderAppUserId.eq(tmpChatMessageFS.getSenderAppUserId()));
            }
            if (tmpChatMessageFS.getSenderAppUserIdNot() != null) {
                queryBuilder.where(ChatMessageDao.Properties.SenderAppUserId.notEq(tmpChatMessageFS.getSenderAppUserIdNot()));
            }
            if (tmpChatMessageFS.getDeliverIs() != null) {
                queryBuilder.where(ChatMessageDao.Properties.DeliverIs.eq(tmpChatMessageFS.getDeliverIs()));
            }
            if (tmpChatMessageFS.getReadIs() != null) {
                queryBuilder.where(ChatMessageDao.Properties.ReadIs.eq(tmpChatMessageFS.getReadIs()));
            }
            if (tmpChatMessageFS.getReadDateFrom() != null) {
                queryBuilder.where(ChatMessageDao.Properties.ReadIs.ge(tmpChatMessageFS.getReadDateFrom()));
            }
            if (tmpChatMessageFS.getSendingStatusEn() != null) {
                queryBuilder.where(ChatMessageDao.Properties.SendingStatusEn.eq(tmpChatMessageFS.getSendingStatusEn()));
            }

            List<ChatMessage> chatMessages = queryBuilder.list();
            for (ChatMessage chatMessage : chatMessages) {
                chatMessage.setReadIs(Boolean.TRUE);
                chatMessage.setReadDate(new Date());
                chatMessageDao.update(chatMessage);
                Log.d(TAG, "Updated ChatMessage: " + chatMessage.getId() + " as read from the schema.");
            }

            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized ChatMessage getLastChatMessageByGroup(Long groupId) {
        ChatMessage chatMessage = null;
        try {
            openReadableDb();
            ChatMessageDao chatMessageDao = daoSession.getChatMessageDao();
            QueryBuilder<ChatMessage> queryBuilder = chatMessageDao.queryBuilder();
            queryBuilder.where(ChatMessageDao.Properties.ChatGroupId.eq(groupId));
            queryBuilder.orderDesc(ChatMessageDao.Properties.Id);
            queryBuilder.limit(1);
            List<ChatMessage> chatMessageList = queryBuilder.list();
            if (!chatMessageList.isEmpty()) {
                chatMessage = chatMessageList.get(0);
            }

            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatMessage;
    }

    @Override
    public synchronized Integer getCountOfUnreadMessageByGroup(Long groupId, Long senderUserId) {
        Integer count = 0;
        try {
            openReadableDb();
            ChatMessageDao chatMessageDao = daoSession.getChatMessageDao();
            QueryBuilder<ChatMessage> queryBuilder = chatMessageDao.queryBuilder();
            queryBuilder.where(ChatMessageDao.Properties.ChatGroupId.eq(groupId), ChatMessageDao.Properties.SenderAppUserId.notEq(senderUserId), ChatMessageDao.Properties.ReadIs.eq(Boolean.FALSE));
            count = Long.valueOf(queryBuilder.count()).intValue();

            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    @Override
    public synchronized Integer getCountOfUnreadMessage(Long senderUserId) {
        Integer count = 0;
        try {
            openReadableDb();
            ChatMessageDao chatMessageDao = daoSession.getChatMessageDao();
            QueryBuilder<ChatMessage> queryBuilder = chatMessageDao.queryBuilder();
            queryBuilder.where(ChatMessageDao.Properties.SenderAppUserId.notEq(senderUserId), ChatMessageDao.Properties.ReadIs.eq(Boolean.FALSE));
            count = Long.valueOf(queryBuilder.count()).intValue();

            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    @Override
    public synchronized List<ChatMessage> getChatMessageListByParam(ChatMessage chatMessageFS) {
        List<ChatMessage> chatMessages = null;
        try {
            openReadableDb();
            ChatMessageDao chatMessageDao = daoSession.getChatMessageDao();
            QueryBuilder<ChatMessage> queryBuilder = chatMessageDao.queryBuilder();
            if (chatMessageFS.getId() != null) {
                queryBuilder.where(ChatMessageDao.Properties.Id.eq(chatMessageFS.getId()));
            }
            if (chatMessageFS.getChatGroupId() != null) {
                queryBuilder.where(ChatMessageDao.Properties.ChatGroupId.eq(chatMessageFS.getChatGroupId()));
            }
            if (chatMessageFS.getSenderAppUserId() != null) {
                queryBuilder.where(ChatMessageDao.Properties.SenderAppUserId.eq(chatMessageFS.getSenderAppUserId()));
            }
            if (chatMessageFS.getSenderAppUserIdNot() != null) {
                queryBuilder.where(ChatMessageDao.Properties.SenderAppUserId.notEq(chatMessageFS.getSenderAppUserIdNot()));
            }
            if (chatMessageFS.getDeliverIs() != null) {
                queryBuilder.where(ChatMessageDao.Properties.DeliverIs.eq(chatMessageFS.getDeliverIs()));
            }
            if (chatMessageFS.getReadIs() != null) {
                queryBuilder.where(ChatMessageDao.Properties.ReadIs.eq(chatMessageFS.getReadIs()));
            }
            if (chatMessageFS.getReadDateFrom() != null) {
                queryBuilder.where(ChatMessageDao.Properties.ReadDate.ge(chatMessageFS.getReadDateFrom()));
            }
            if (chatMessageFS.getSendingStatusEn() != null) {
                queryBuilder.where(ChatMessageDao.Properties.SendingStatusEn.eq(chatMessageFS.getSendingStatusEn()));
            }
            chatMessages = queryBuilder.list();
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatMessages;
    }

    @Override
    public synchronized List<ChatMessage> getChatMessageListByParamLimit(ChatMessage chatMessageFS, Integer limitSize) {
        List<ChatMessage> chatMessages = null;
        try {
            openReadableDb();
            ChatMessageDao chatMessageDao = daoSession.getChatMessageDao();
            QueryBuilder<ChatMessage> queryBuilder = chatMessageDao.queryBuilder();
            if (chatMessageFS.getId() != null) {
                queryBuilder.where(ChatMessageDao.Properties.Id.eq(chatMessageFS.getId()));
            }
            if (chatMessageFS.getChatGroupId() != null) {
                queryBuilder.where(ChatMessageDao.Properties.ChatGroupId.eq(chatMessageFS.getChatGroupId()));
            }
            if (chatMessageFS.getSenderAppUserId() != null) {
                queryBuilder.where(ChatMessageDao.Properties.SenderAppUserId.eq(chatMessageFS.getSenderAppUserId()));
            }
            if (chatMessageFS.getSenderAppUserIdNot() != null) {
                queryBuilder.where(ChatMessageDao.Properties.SenderAppUserId.notEq(chatMessageFS.getSenderAppUserIdNot()));
            }
            if (chatMessageFS.getDeliverIs() != null) {
                queryBuilder.where(ChatMessageDao.Properties.DeliverIs.eq(chatMessageFS.getDeliverIs()));
            }
            if (chatMessageFS.getReadIs() != null) {
                queryBuilder.where(ChatMessageDao.Properties.ReadIs.eq(chatMessageFS.getReadIs()));
            }
            if (chatMessageFS.getReadDateFrom() != null) {
                queryBuilder.where(ChatMessageDao.Properties.ReadDate.ge(chatMessageFS.getReadDateFrom()));
            }
            if (chatMessageFS.getSendingStatusEn() != null) {
                queryBuilder.where(ChatMessageDao.Properties.SendingStatusEn.eq(chatMessageFS.getSendingStatusEn()));
            }
            Integer totalCount = Long.valueOf(queryBuilder.count()).intValue();
            //System.out.println("totalCount111====" + totalCount);
            //System.out.println("totalCount222====" + limitSize);
           // System.out.println("totalCount333====" + totalCount.compareTo(limitSize));
            if (totalCount.compareTo(limitSize) > 0) {
                queryBuilder.offset(totalCount - limitSize);
                queryBuilder.limit(limitSize);
            }
            chatMessages = queryBuilder.list();
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatMessages;
    }

    @Override
    public synchronized List<ChatMessage> getUnSentChatMessageList(ChatMessage chatMessageFS) {
        List<ChatMessage> chatMessages = null;
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, -5);
            openReadableDb();
            ChatMessageDao chatMessageDao = daoSession.getChatMessageDao();
            QueryBuilder<ChatMessage> queryBuilder = chatMessageDao.queryBuilder();
            WhereCondition condition1 = chatMessageDao.queryBuilder().and(ChatMessageDao.Properties.SendingStatusEn.eq(SendingStatusEn.InProgress.ordinal()),
                    ChatMessageDao.Properties.SendingStatusDate.le(calendar.getTime()));

            WhereCondition condition2 = chatMessageDao.queryBuilder().or(ChatMessageDao.Properties.SendingStatusEn.eq(SendingStatusEn.Pending.ordinal()),
                    condition1);

            WhereCondition condition3 = chatMessageDao.queryBuilder().and(ChatMessageDao.Properties.SenderAppUserId.eq(chatMessageFS.getSenderAppUserId()), condition2);

            queryBuilder.where(condition3);
            chatMessages = queryBuilder.list();

            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatMessages;
    }

    @Override
    public synchronized List<ChatMessage> getAttachmentResumingChatMessageList(ChatMessage chatMessageFS) {
        List<ChatMessage> chatMessages = null;
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, -5);
            openReadableDb();
            ChatMessageDao chatMessageDao = daoSession.getChatMessageDao();
            QueryBuilder<ChatMessage> queryBuilder = chatMessageDao.queryBuilder();
            WhereCondition condition1 = chatMessageDao.queryBuilder().and(ChatMessageDao.Properties.SendingStatusEn.eq(SendingStatusEn.AttachmentResuming.ordinal()),
                    ChatMessageDao.Properties.SendingStatusDate.le(calendar.getTime()), ChatMessageDao.Properties.SenderAppUserId.eq(chatMessageFS.getSenderAppUserId()));

            queryBuilder.where(condition1);
            chatMessages = queryBuilder.list();

            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatMessages;
    }

    @Override
    public synchronized List<ChatMessage> getChatGroupListNotReadNotDelivered(Long userId) {
        List<ChatMessage> chatMessages = null;
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, -5);
            openReadableDb();
            ChatMessageDao chatMessageDao = daoSession.getChatMessageDao();
            QueryBuilder<ChatMessage> queryBuilder = chatMessageDao.queryBuilder();

            WhereCondition condition1 = chatMessageDao.queryBuilder().or(ChatMessageDao.Properties.DeliverIs.eq(Boolean.FALSE), ChatMessageDao.Properties.ReadIs.eq(Boolean.FALSE));

            WhereCondition condition2 = chatMessageDao.queryBuilder().and(ChatMessageDao.Properties.SenderAppUserId.eq(userId), ChatMessageDao.Properties.ServerMessageId.isNotNull(), condition1);

            queryBuilder.where(condition2);
            chatMessages = queryBuilder.list();

            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatMessages;
    }


    @Override
    public synchronized ComplaintReport getComplaintReportById(Long complaintReportId) {
        ComplaintReport complaintReport = null;
        try {
            openReadableDb();
            ComplaintReportDao complaintReportDao = daoSession.getComplaintReportDao();
            complaintReport = complaintReportDao.loadByRowId(complaintReportId);
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return complaintReport;
    }

    @Override
    public synchronized ComplaintReport insertComplaintReport(ComplaintReport complaintReport) {
        try {
            if (complaintReport != null) {
                openWritableDb();
                complaintReport.setId(generateNewId());
                ComplaintReportDao complaintReportDao = daoSession.getComplaintReportDao();
                complaintReportDao.insert(complaintReport);
                Log.d(TAG, "Inserted complaintReport: " + complaintReport.getId() + " to the schema.");
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return complaintReport;
    }

    @Override
    public synchronized void updateComplaintReport(ComplaintReport complaintReport) {
        try {
            if (complaintReport != null) {
                openWritableDb();
                daoSession.update(complaintReport);
                Log.d(TAG, "Updated ComplaintReport: " + complaintReport.getId() + " from the schema.");
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized List<ComplaintReport> getComplaintReportListByParam(ComplaintReport complaintReportFS) {
        List<ComplaintReport> complaintReport = null;
        try {
            openReadableDb();
            ComplaintReportDao complaintReportDao = daoSession.getComplaintReportDao();
            QueryBuilder<ComplaintReport> queryBuilder = complaintReportDao.queryBuilder();
            if (complaintReportFS.getId() != null) {
                queryBuilder.where(ComplaintReportDao.Properties.Id.eq(complaintReportFS.getId()));
            }
            if (complaintReportFS.getUserReportId() != null) {
                queryBuilder.where(ComplaintReportDao.Properties.UserReportId.eq(complaintReportFS.getUserReportId()));
            }
            if (complaintReportFS.getDeliverIs() != null) {
                queryBuilder.where(ComplaintReportDao.Properties.DeliverIs.eq(complaintReportFS.getDeliverIs()));
            }
            if (complaintReportFS.getSendingStatusEn() != null) {
                queryBuilder.where(ComplaintReportDao.Properties.SendingStatusEn.eq(complaintReportFS.getSendingStatusEn()));
            }
            complaintReport = queryBuilder.list();

            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return complaintReport;
    }

    @Override
    public synchronized List<ComplaintReport> getUnSentComplaintReportList(ComplaintReport complaintReportFS) {
        List<ComplaintReport> complaintReport = null;
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, -5);
            openReadableDb();
            ComplaintReportDao complaintReportDao = daoSession.getComplaintReportDao();
            QueryBuilder<ComplaintReport> queryBuilder = complaintReportDao.queryBuilder();
            WhereCondition condition1 = complaintReportDao.queryBuilder().and(ComplaintReportDao.Properties.SendingStatusEn.eq(SendingStatusEn.InProgress.ordinal()),
                    ComplaintReportDao.Properties.SendingStatusDate.le(calendar.getTime()));

            WhereCondition condition2 = complaintReportDao.queryBuilder().or(ComplaintReportDao.Properties.SendingStatusEn.eq(SendingStatusEn.Pending.ordinal()),
                    condition1);

            WhereCondition condition3 = complaintReportDao.queryBuilder().and(ComplaintReportDao.Properties.UserReportId.eq(complaintReportFS.getUserReportId()),
                    ComplaintReportDao.Properties.DeliverIs.eq(Boolean.FALSE), condition2);

            queryBuilder.where(condition3);
            complaintReport = queryBuilder.list();

            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return complaintReport;
    }

    @Override
    public List<ComplaintReport> getComplaintReportListByDate(Long userReportId, Date fromDate, Date toDate) {
        List<ComplaintReport> complaintReport = null;
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, -5);
            openReadableDb();
            ComplaintReportDao complaintReportDao = daoSession.getComplaintReportDao();
            QueryBuilder<ComplaintReport> queryBuilder = complaintReportDao.queryBuilder();

            WhereCondition condition3 = complaintReportDao.queryBuilder().and(ComplaintReportDao.Properties.UserReportId.eq(userReportId),
                    ComplaintReportDao.Properties.ReportDate.ge(fromDate), ComplaintReportDao.Properties.ReportDate.le(toDate));

            queryBuilder.where(condition3);
            complaintReport = queryBuilder.list();

            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return complaintReport;
    }

    @Override
    public synchronized AppUser insertAppUser(AppUser appUser) {
        try {
            openWritableDb();
            AppUserDao appUserDao = daoSession.getAppUserDao();
            appUserDao.insertOrReplace(appUser);
            Log.d(TAG, "Inserted or Replace syncHistory: " + appUser.getId() + " to the schema.");
            daoSession.clear();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return appUser;
    }

    @Override
    public synchronized void saveAppUserList(List<AppUser> appUserList) {
        try {
            if (!appUserList.isEmpty()) {
                openWritableDb();
                asyncSession.insertOrReplaceInTx(AppUser.class, appUserList);
                assertWaitForCompletion1Sec();
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized AppUser getAppUserById(Long id) {
        AppUser appUser = null;
        try {
            openReadableDb();
            AppUserDao appUserDao = daoSession.getAppUserDao();
            appUser = appUserDao.loadByRowId(id);
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appUser;
    }

    @Override
    public synchronized List<SurveyForm> getSurveyFormListByParam(SurveyForm tmpSurveyFormFS) {
        List<SurveyForm> surveyForms = null;
        try {
            openReadableDb();
            SurveyFormDao surveyFormDao = daoSession.getSurveyFormDao();
            QueryBuilder<SurveyForm> queryBuilder = surveyFormDao.queryBuilder();
            if (tmpSurveyFormFS.getId() != null) {
                queryBuilder.where(ChatMessageDao.Properties.Id.eq(tmpSurveyFormFS.getId()));
            }
            if (tmpSurveyFormFS.getStartDate() != null) {
                queryBuilder.where(SurveyFormDao.Properties.StartDate.le(tmpSurveyFormFS.getStartDate()));
            }
            if (tmpSurveyFormFS.getEndDate() != null) {
                queryBuilder.where(SurveyFormDao.Properties.EndDate.ge(tmpSurveyFormFS.getEndDate()));
            }
            surveyForms = queryBuilder.list();
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return surveyForms;
    }

    @Override
    public synchronized SurveyForm getSurveyFormById(Long id) {
        SurveyForm surveyForm = null;
        try {
            openReadableDb();
            SurveyFormDao surveyFormDao = daoSession.getSurveyFormDao();
            surveyForm = surveyFormDao.loadByRowId(id);
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return surveyForm;
    }

    @Override
    public SurveyForm insertOrUpdateSurveyForm(SurveyForm surveyForm) {
        try {
            openWritableDb();
            SurveyFormDao surveyFormDao = daoSession.getSurveyFormDao();
            surveyFormDao.insertOrReplace(surveyForm);
            Log.d(TAG, "Inserted or Replace surveyForm: " + surveyForm.getId() + " to the schema.");
            daoSession.clear();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return surveyForm;
    }

    @Override
    public synchronized void updateSurveyForm(SurveyForm surveyForm) {
        try {
            if (surveyForm != null) {
                openWritableDb();
                daoSession.update(surveyForm);
                Log.d(TAG, "Updated surveyForm: " + surveyForm.getId() + " from the schema.");
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void updateAppUser(AppUser appUser) {
        try {
            if (appUser != null) {
                openWritableDb();
                daoSession.update(appUser);
                Log.d(TAG, "Updated appUser: " + appUser.getId() + " from the schema.");
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized SurveyFormQuestion getSurveyFormQuestionById(Long id) {
        SurveyFormQuestion surveyFormQuestion = null;
        try {
            openReadableDb();
            SurveyFormQuestionDao surveyFormQuestionDao = daoSession.getSurveyFormQuestionDao();
            surveyFormQuestion = surveyFormQuestionDao.loadByRowId(id);
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return surveyFormQuestion;
    }

    @Override
    public synchronized SurveyFormQuestionTemp getSurveyFormQuestionTempById(Long id) {
        SurveyFormQuestionTemp surveyFormQuestionTemp = null;
        try {
            openReadableDb();
            SurveyFormQuestionTempDao surveyFormQuestionTempDao = daoSession.getSurveyFormQuestionTempDao();
            surveyFormQuestionTemp = surveyFormQuestionTempDao.loadByRowId(id);
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return surveyFormQuestionTemp;
    }

    @Override
    public SurveyFormQuestion insertOrUpdateSurveyFormQuestion(SurveyFormQuestion surveyFormQuestion) {
        try {
            openWritableDb();
            SurveyFormQuestionDao surveyFormQuestionDao = daoSession.getSurveyFormQuestionDao();
            surveyFormQuestionDao.insertOrReplace(surveyFormQuestion);
            Log.d(TAG, "Inserted or Replace surveyFormQuestion: " + surveyFormQuestion.getId() + " to the schema.");
            daoSession.clear();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return surveyFormQuestion;
    }

    @Override
    public synchronized void updateSurveyFormQuestion(SurveyFormQuestion surveyFormQuestion) {
        try {
            if (surveyFormQuestion != null) {
                openWritableDb();
                daoSession.update(surveyFormQuestion);
                Log.d(TAG, "Updated surveyFormQuestion: " + surveyFormQuestion.getId() + " from the schema.");
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized List<SurveyFormQuestion> getSurveyFormQuestionListByParam(SurveyFormQuestion tmpSurveyFormQuestionFS) {
        List<SurveyFormQuestion> surveyFormQuestions = null;
        try {
            openReadableDb();
            SurveyFormQuestionDao surveyFormQuestionDao = daoSession.getSurveyFormQuestionDao();
            QueryBuilder<SurveyFormQuestion> queryBuilder = surveyFormQuestionDao.queryBuilder();
            if (tmpSurveyFormQuestionFS.getId() != null) {
                queryBuilder.where(ChatMessageDao.Properties.Id.eq(tmpSurveyFormQuestionFS.getId()));
            }
            if (tmpSurveyFormQuestionFS.getSurveyFormId() != null) {
                queryBuilder.where(SurveyFormQuestionDao.Properties.SurveyFormId.le(tmpSurveyFormQuestionFS.getSurveyFormId()));
            }
            surveyFormQuestions = queryBuilder.list();
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return surveyFormQuestions;
    }

    @Override
    public synchronized List<SurveyForm> getUnSentSurveyFormList(SurveyForm surveyFormFS) {
        List<SurveyForm> surveyForms = null;
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, -5);
            openReadableDb();
            SurveyFormDao surveyFormDao = daoSession.getSurveyFormDao();
            QueryBuilder<SurveyForm> queryBuilder = surveyFormDao.queryBuilder();
            WhereCondition condition1 = surveyFormDao.queryBuilder().and(SurveyFormDao.Properties.SendingStatusEn.eq(SendingStatusEn.InProgress.ordinal()),
                    SurveyFormDao.Properties.SendingStatusDate.le(calendar.getTime()));

            WhereCondition condition2 = surveyFormDao.queryBuilder().or(SurveyFormDao.Properties.SendingStatusEn.eq(SendingStatusEn.Pending.ordinal()),
                    condition1);

            queryBuilder.where(condition2);

            if (surveyFormFS.getStatusEn() != null) {
                queryBuilder.where(SurveyFormDao.Properties.StatusEn.eq(surveyFormFS.getStatusEn()));
            }
            surveyForms = queryBuilder.list();

            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return surveyForms;
    }

    @Override
    public synchronized List<FormQuestion> getFormQuestionListByGroupId(Long id1, Long id2) {
        List<FormQuestion> formQuestions = null;
        try {
            openReadableDb();
            FormQuestionDao formItemAnswerDao = daoSession.getFormQuestionDao();
            QueryBuilder<FormQuestion> queryBuilder = formItemAnswerDao.queryBuilder();
            queryBuilder.where(FormQuestionDao.Properties.FormQuestionGroupId.in(id1));
            queryBuilder.where(FormQuestionDao.Properties.FormId.in(id2));
            formQuestions = queryBuilder.list();
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formQuestions;
    }

    @Override
    public synchronized List<SurveyFormQuestion> getSurveyFormQuestionListByGroupId(Long id1, Long id2) {
        List<SurveyFormQuestion> surveyFormQuestions = null;
        try {
            openReadableDb();
            SurveyFormQuestionDao surveyFormQuestionDao = daoSession.getSurveyFormQuestionDao();
            QueryBuilder<SurveyFormQuestion> queryBuilder = surveyFormQuestionDao.queryBuilder();
            queryBuilder.where(SurveyFormQuestionDao.Properties.FormQuestionGroupId.in(id1));
            queryBuilder.where(SurveyFormQuestionDao.Properties.SurveyFormId.in(id2));
            surveyFormQuestions = queryBuilder.list();
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return surveyFormQuestions;
    }

    @Override
    public synchronized List<Form> getCheckListFormByParam(Form tmpCheckListFormFS) {
        List<Form> forms = null;
        try {
            openReadableDb();
            FormDao formDao = daoSession.getFormDao();
            QueryBuilder<Form> queryBuilder = formDao.queryBuilder();
            if (tmpCheckListFormFS.getId() != null) {
                queryBuilder.where(ChatMessageDao.Properties.Id.eq(tmpCheckListFormFS.getId()));
            }
            if (tmpCheckListFormFS.getStartDate() != null) {
                queryBuilder.where(SurveyFormDao.Properties.StartDate.le(tmpCheckListFormFS.getStartDate()));
            }
            if (tmpCheckListFormFS.getEndDate() != null) {
                queryBuilder.where(SurveyFormDao.Properties.EndDate.ge(tmpCheckListFormFS.getEndDate()));
            }
            forms = queryBuilder.list();
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return forms;
    }

    @Override
    public synchronized List<Form> listForms() {
        List<Form> forms = null;
        try {
            openReadableDb();
            FormDao formDao = daoSession.getFormDao();
            forms = formDao.loadAll();

            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return forms;
    }

    @Override
    public synchronized Form getCheckListFormById(Long id) {
        Form form = null;
        try {
            openReadableDb();
            FormDao formDao = daoSession.getFormDao();
            form = formDao.loadByRowId(id);
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return form;
    }

    @Override
    public Form insertOrUpdateCheckListForm(Form form) {
        try {
            openWritableDb();
            FormDao formDao = daoSession.getFormDao();
            formDao.insertOrReplace(form);
            Log.d(TAG, "Inserted or Replace surveyForm: " + form.getId() + " to the schema.");
            daoSession.clear();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return form;
    }

    @Override
    public synchronized FormQuestion getCheckListFormQuestionById(Long id) {
        FormQuestion formQuestion = null;
        try {
            openReadableDb();
            FormQuestionDao formQuestionDao = daoSession.getFormQuestionDao();
            formQuestion = formQuestionDao.loadByRowId(id);
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formQuestion;
    }

    @Override
    public synchronized FormTemp getCheckListFormTempById(Long id) {
        FormTemp formTemp = null;
        try {
            openReadableDb();
            FormTempDao formTempDao = daoSession.getFormTempDao();
            formTemp = formTempDao.loadByRowId(id);
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formTemp;
    }

    @Override
    public synchronized FormQuestionGroup getCheckListFormQuestionGroupById(Long id) {
        FormQuestionGroup formQuestionGroup = null;
        try {
            openReadableDb();
            FormQuestionGroupDao formQuestionGroupDao = daoSession.getFormQuestionGroupDao();
            formQuestionGroup = formQuestionGroupDao.loadByRowId(id);
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formQuestionGroup;
    }

    @Override
    public synchronized FormAnswer getFormAnswerById(Long id) {
        FormAnswer formAnswer = null;
        try {
            openReadableDb();
            FormAnswerDao formAnswerDao = daoSession.getFormAnswerDao();
            formAnswer = formAnswerDao.loadByRowId(id);
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formAnswer;
    }

    @Override
    public synchronized FormQuestionGroupForm getFormQuestionGroupFormByFormId(Long id) {
        FormQuestionGroupForm formQuestionGroupForm = null;
        try {
            openReadableDb();
            FormQuestionGroupFormDao formQuestionGroupFormDao = daoSession.getFormQuestionGroupFormDao();
            formQuestionGroupForm = formQuestionGroupFormDao.loadByRowId(id);
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formQuestionGroupForm;
    }

    @Override
    public FormQuestion insertOrUpdateChecklistFormQuestion(FormQuestion formQuestion) {
        try {
            openWritableDb();
            FormQuestionDao formQuestionDao = daoSession.getFormQuestionDao();
            formQuestionDao.insertOrReplace(formQuestion);
            Log.d(TAG, "Inserted or Replace surveyFormQuestion: " + formQuestion.getId() + " to the schema.");
            daoSession.clear();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return formQuestion;
    }

    @Override
    public FormTemp saveOrUpdateCheckListFormTemp(FormTemp formTemp) {
        try {
            openWritableDb();
            FormTempDao formTempDao = daoSession.getFormTempDao();
            formTempDao.insertOrReplace(formTemp);
            Log.d(TAG, "Inserted or Replace surveyFormQuestion: " + formTemp.getId() + " to the schema.");
            daoSession.clear();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return formTemp;
    }

    @Override
    public SurveyFormQuestionTemp saveOrUpdateSurveyFormQuestionTemp(SurveyFormQuestionTemp surveyFormQuestionTemp) {
        try {
            openWritableDb();
            SurveyFormQuestionTempDao surveyFormQuestionTempDao = daoSession.getSurveyFormQuestionTempDao();
            surveyFormQuestionTempDao.insertOrReplace(surveyFormQuestionTemp);
            Log.d(TAG, "Inserted or Replace surveyFormQuestion: " + surveyFormQuestionTemp.getId() + " to the schema.");
            daoSession.clear();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return surveyFormQuestionTemp;
    }

    @Override
    public FormQuestionGroup insertOrUpdateChecklistFormQuestionGroup(FormQuestionGroup formQuestionGroup) {
        try {
            openWritableDb();
            FormQuestionGroupDao formQuestionGroupDao = daoSession.getFormQuestionGroupDao();
            formQuestionGroupDao.insertOrReplace(formQuestionGroup);
            Log.d(TAG, "Inserted or Replace surveyFormQuestion: " + formQuestionGroup.getId() + " to the schema.");
            daoSession.clear();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return formQuestionGroup;
    }


    @Override
    public FormQuestionGroupForm saveOrUpdateFormQuestionGroupForm(FormQuestionGroupForm formQuestionGroupForm) {
        try {
            openWritableDb();
            FormQuestionGroupFormDao formQuestionGroupFormDao = daoSession.getFormQuestionGroupFormDao();
            formQuestionGroupFormDao.insertOrReplace(formQuestionGroupForm);
            Log.d(TAG, "Inserted or Replace surveyFormQuestion: " + formQuestionGroupForm.getId() + " to the schema.");
            daoSession.clear();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return formQuestionGroupForm;
    }

    @Override
    public synchronized List<FormQuestion> getFormQuestionListByParam(FormQuestion tmpFormQuestionFS) {
        List<FormQuestion> formQuestions = null;
        try {
            openReadableDb();
            FormQuestionDao formQuestionDao = daoSession.getFormQuestionDao();
            QueryBuilder<FormQuestion> queryBuilder = formQuestionDao.queryBuilder();
            if (tmpFormQuestionFS.getId() != null) {
                queryBuilder.where(ChatMessageDao.Properties.Id.eq(tmpFormQuestionFS.getId()));
            }
            if (tmpFormQuestionFS.getFormId() != null) {
                queryBuilder.where(FormQuestionDao.Properties.FormId.le(tmpFormQuestionFS.getFormId()));
            }
            formQuestions = queryBuilder.list();
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formQuestions;
    }

    @Override
    public synchronized List<FormQuestionGroup> getFormQuestionGroupListByParam(FormQuestionGroup tmpFormQuestionGroupFS) {
        List<FormQuestionGroup> formQuestionGroups = null;
        try {
            openReadableDb();
            FormQuestionGroupDao formQuestionGroupDao = daoSession.getFormQuestionGroupDao();
            QueryBuilder<FormQuestionGroup> queryBuilder = formQuestionGroupDao.queryBuilder();

            if (tmpFormQuestionGroupFS.getFormId() != null) {
                queryBuilder.where(FormQuestionGroupDao.Properties.FormId.le(tmpFormQuestionGroupFS.getFormId()));
            }
            formQuestionGroups = queryBuilder.list();
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formQuestionGroups;
    }

    @Override
    public FormAnswer insertFormAnswer(FormAnswer formAnswer) {
        try {

            if (formAnswer != null) {
                openWritableDb();
                formAnswer.setId(generateNewId());
                FormAnswerDao formAnswerDao = daoSession.getFormAnswerDao();
                formAnswerDao.insert(formAnswer);
                Log.d(TAG, "Inserted or Replace surveyFormQuestion: " + formAnswer.getId() + " to the schema.");
                daoSession.clear();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return formAnswer;
    }

    @Override
    public synchronized List<FormAnswer> getFormAnswerListByParam(FormAnswer tmpFormAnswerFS) {
        List<FormAnswer> formAnswers = null;
        try {
            openReadableDb();
            FormAnswerDao formAnswerDao = daoSession.getFormAnswerDao();
            QueryBuilder<FormAnswer> queryBuilder = formAnswerDao.queryBuilder();
            if (tmpFormAnswerFS.getId() != null) {
                queryBuilder.where(ChatMessageDao.Properties.Id.eq(tmpFormAnswerFS.getId()));
            }
            if (tmpFormAnswerFS.getFormId() != null) {
                queryBuilder.where(FormAnswerDao.Properties.FormId.le(tmpFormAnswerFS.getFormId()));
            }
            formAnswers = queryBuilder.list();
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formAnswers;
    }

    @Override
    public FormItemAnswer insertNewFormAnswer(FormItemAnswer formItemAnswer) {
        try {

            if (formItemAnswer != null) {
                openWritableDb();
                formItemAnswer.setId(generateNewId());
                FormItemAnswerDao formAnswerDao = daoSession.getFormItemAnswerDao();
                formAnswerDao.insert(formItemAnswer);
                Log.d(TAG, "Inserted or Replace surveyFormQuestion1: " + formItemAnswer.getId() + " to the schema.");
                daoSession.clear();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return formItemAnswer;
    }

    @Override
    public FormQuestionGroup insertNewFormQuestionGroup(FormQuestionGroup formQuestionGroup) {
        try {

            if (formQuestionGroup != null) {
                openWritableDb();
                formQuestionGroup.setId(generateNewId());
                FormQuestionGroupDao formQuestionGroupDao = daoSession.getFormQuestionGroupDao();
                formQuestionGroupDao.insert(formQuestionGroup);
                Log.d(TAG, "Inserted or Replace surveyFormQuestion1: " + formQuestionGroup.getId() + " to the schema.");
                daoSession.clear();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return formQuestionGroup;
    }

    @Override
    public FormQuestionGroupForm insertNewFormQuestionGroupForm(FormQuestionGroupForm formQuestionGroupForm) {
        try {

            if (formQuestionGroupForm != null) {
                openWritableDb();
                formQuestionGroupForm.setId(generateNewId());
                FormQuestionGroupFormDao formQuestionGroupFormDao = daoSession.getFormQuestionGroupFormDao();
                formQuestionGroupFormDao.insert(formQuestionGroupForm);
                Log.d(TAG, "Inserted or Replace surveyFormQuestion1: " + formQuestionGroupForm.getId() + " to the schema.");
                daoSession.clear();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return formQuestionGroupForm;
    }

    @Override
    public FormTemp insertNewFormTemp(FormTemp formTemp) {
        try {

            if (formTemp != null) {
                openWritableDb();
                formTemp.setId(generateNewId());
                FormTempDao formItemAnswerDao = daoSession.getFormTempDao();
                formItemAnswerDao.insert(formTemp);
                Log.d(TAG, "Inserted or Replace surveyFormQuestion1: " + formTemp.getId() + " to the schema.");
                daoSession.clear();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return formTemp;
    }

    @Override
    public synchronized List<FormItemAnswer> getFormItemAnswerListByParam(FormItemAnswer tmpFormItemAnswerFS) {
        List<FormItemAnswer> formItemAnswers = null;
        try {
            openReadableDb();
            FormItemAnswerDao formItemAnswerDao = daoSession.getFormItemAnswerDao();
            QueryBuilder<FormItemAnswer> queryBuilder = formItemAnswerDao.queryBuilder();
            if (tmpFormItemAnswerFS.getId() != null) {
                queryBuilder.where(ChatMessageDao.Properties.Id.eq(tmpFormItemAnswerFS.getId()));
            }
            if (tmpFormItemAnswerFS.getFormAnswerId() != null) {
                queryBuilder.where(FormItemAnswerDao.Properties.FormAnswerId.le(tmpFormItemAnswerFS.getFormAnswerId()));
            }
            formItemAnswers = queryBuilder.list();
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formItemAnswers;
    }


    @Override
    public synchronized FormItemAnswer getFormItemAnswerById(Long id) {
        FormItemAnswer formItemAnswer = null;
        try {
            openReadableDb();
            FormItemAnswerDao formItemAnswerDao = daoSession.getFormItemAnswerDao();
            formItemAnswer = formItemAnswerDao.loadByRowId(id);
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formItemAnswer;
    }

    @Override
    public synchronized void updateFormItemAnswer(FormItemAnswer formItemAnswer) {
        try {
            if (formItemAnswer != null) {
                openWritableDb();
                daoSession.update(formItemAnswer);
                Log.d(TAG, "Updated surveyFormQuestion: " + formItemAnswer.getId() + " from the schema.");
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public synchronized void updateFormTemp(FormTemp formTemp) {
        try {
            if (formTemp != null) {
                openWritableDb();
                daoSession.update(formTemp);
                Log.d(TAG, "Updated surveyFormQuestion: " + formTemp.getId() + " FormTemp the schema.");
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void updateSurveyFormQuestionTemp(SurveyFormQuestionTemp surveyFormQuestionTemp) {
        try {
            if (surveyFormQuestionTemp != null) {
                openWritableDb();
                daoSession.update(surveyFormQuestionTemp);
                Log.d(TAG, "Updated surveyFormQuestion: " + surveyFormQuestionTemp.getId() + " surveyFormQuestionTemp the schema.");
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized List<FormItemAnswer> getFormItemAnswerListById(Long id) {
        List<FormItemAnswer> formItemAnswers = null;
        try {
            openReadableDb();
            FormItemAnswerDao formItemAnswerDao = daoSession.getFormItemAnswerDao();
            QueryBuilder<FormItemAnswer> queryBuilder = formItemAnswerDao.queryBuilder();
            queryBuilder.where(FormItemAnswerDao.Properties.FormAnswerId.in(id));
            formItemAnswers = queryBuilder.list();
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formItemAnswers;
    }

    @Override
    public synchronized List<FormAnswer> getFormAnswerListById(Long id) {
        List<FormAnswer> formAnswers = null;
        try {
            openReadableDb();
            FormAnswerDao formAnswerDao = daoSession.getFormAnswerDao();
            QueryBuilder<FormAnswer> queryBuilder = formAnswerDao.queryBuilder();
            queryBuilder.where(FormAnswerDao.Properties.FormId.in(id));
            formAnswers = queryBuilder.list();
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formAnswers;
    }


    @Override
    public synchronized List<FormTemp> getFormTempListById(Long id) {
        List<FormTemp> formTemps = null;
        try {
            openReadableDb();
            FormTempDao formTempDao = daoSession.getFormTempDao();
            QueryBuilder<FormTemp> queryBuilder = formTempDao.queryBuilder();
            queryBuilder.where(FormTempDao.Properties.FormId.in(id));
            formTemps = queryBuilder.list();
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formTemps;
    }

    @Override
    public synchronized List<SurveyFormQuestionTemp> getSurveyFormQuestionTempListById(Long id) {
        List<SurveyFormQuestionTemp> surveyFormQuestionTemps = null;
        try {
            openReadableDb();
            SurveyFormQuestionTempDao surveyFormQuestionTempDao = daoSession.getSurveyFormQuestionTempDao();
            QueryBuilder<SurveyFormQuestionTemp> queryBuilder = surveyFormQuestionTempDao.queryBuilder();
            queryBuilder.where(SurveyFormQuestionTempDao.Properties.SurveyFormId.in(id));
            surveyFormQuestionTemps = queryBuilder.list();
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return surveyFormQuestionTemps;
    }

    @Override
    public synchronized List<SurveyFormQuestionTemp> getSurveyFormQuestionTempListByGroupId(Long id) {
        List<SurveyFormQuestionTemp> surveyFormQuestionTemps = null;
        try {
            openReadableDb();
            SurveyFormQuestionTempDao surveyFormQuestionTempDao = daoSession.getSurveyFormQuestionTempDao();
            QueryBuilder<SurveyFormQuestionTemp> queryBuilder = surveyFormQuestionTempDao.queryBuilder();
            queryBuilder.where(SurveyFormQuestionTempDao.Properties.FormQuestionGroupId.in(id));
            surveyFormQuestionTemps = queryBuilder.list();
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return surveyFormQuestionTemps;
    }

    @Override
    public synchronized List<FormQuestionGroup> getFormQuestionGroupListById(Long id) {
        List<FormQuestionGroup> formQuestionGroups = null;
        try {
            openReadableDb();
            FormQuestionGroupDao formQuestionGroupDao = daoSession.getFormQuestionGroupDao();
            QueryBuilder<FormQuestionGroup> queryBuilder = formQuestionGroupDao.queryBuilder();
            queryBuilder.where(FormQuestionGroupDao.Properties.FormId.in(id));
            formQuestionGroups = queryBuilder.list();
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formQuestionGroups;
    }


    @Override
    public synchronized List<FormQuestionGroupForm> getFormQuestionGroupFormById(Long id) {
        List<FormQuestionGroupForm> formQuestionGroupForms = null;
        try {
            openReadableDb();
            FormQuestionGroupFormDao formQuestionGroupFormDao = daoSession.getFormQuestionGroupFormDao();
            QueryBuilder<FormQuestionGroupForm> queryBuilder = formQuestionGroupFormDao.queryBuilder();
            queryBuilder.where(FormQuestionGroupFormDao.Properties.FormId.in(id));
            formQuestionGroupForms = queryBuilder.list();
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formQuestionGroupForms;
    }

    @Override
    public synchronized List<FormTemp> getFormTempListByFormIdTest(FormTemp formTemp) {
        List<FormTemp> formTemps = null;
        try {
            openReadableDb();
            FormTempDao formTempDao = daoSession.getFormTempDao();
            QueryBuilder<FormTemp> queryBuilder = formTempDao.queryBuilder();
            if (formTemp.getId() != null) {
                queryBuilder.where(ChatMessageDao.Properties.Id.eq(formTemp.getId()));
            }
            if (formTemp.getFormId() != null) {
                queryBuilder.where(FormTempDao.Properties.FormId.le(formTemp.getFormId()));
            }
            formTemps = queryBuilder.list();
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formTemps;
    }

    @Override
    public synchronized List<FormItemAnswer> getFormItemAnswerListByGroupId(Long id, Long formAnswerId) {
        List<FormItemAnswer> formItemAnswers = null;
        try {
            openReadableDb();
            FormItemAnswerDao formItemAnswerDao = daoSession.getFormItemAnswerDao();
            QueryBuilder<FormItemAnswer> queryBuilder = formItemAnswerDao.queryBuilder();
            //queryBuilder.where(FormItemAnswerDao.Properties.FormQuestionGroupId.in(id));
            //queryBuilder.where(FormItemAnswerDao.Properties.FormAnswerId.in(id));

            WhereCondition whereCondition = formItemAnswerDao.queryBuilder().and(FormItemAnswerDao.Properties.FormQuestionGroupId.eq(id),
                    FormItemAnswerDao.Properties.FormAnswerId.eq(formAnswerId));

            queryBuilder.where(whereCondition);
            formItemAnswers = queryBuilder.list();
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formItemAnswers;
    }


    @Override
    public synchronized List<FormTemp> getFormTempListByGroupId(Long id, Long formAnswerId) {
        List<FormTemp> formTemps = null;
        try {
            openReadableDb();
            FormTempDao formTempDao = daoSession.getFormTempDao();
            QueryBuilder<FormTemp> queryBuilder = formTempDao.queryBuilder();
            //queryBuilder.where(FormTempDao.Properties.FormId.in(id));
            //queryBuilder.where(FormTempDao.Properties.FormAnswerId.in(formAnswerId));

            WhereCondition whereCondition = formTempDao.queryBuilder().and(FormTempDao.Properties.FormQuestionGroupId.eq(id),
                    FormTempDao.Properties.FormAnswerId.eq(formAnswerId));

            queryBuilder.where(whereCondition);
            formTemps = queryBuilder.list();
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formTemps;
    }

    @Override
    public synchronized List<FormTemp> getFormTempListByFormId(Long id, Long formId) {
        List<FormTemp> formTemps = null;
        try {
            openReadableDb();
            FormTempDao formTempDao = daoSession.getFormTempDao();
            QueryBuilder<FormTemp> queryBuilder = formTempDao.queryBuilder();
            queryBuilder.where(FormTempDao.Properties.FormQuestionGroupId.in(id));

            WhereCondition whereCondition = formTempDao.queryBuilder().and(FormTempDao.Properties.FormQuestionGroupId.eq(id),
                    FormTempDao.Properties.FormId.eq(formId));

            queryBuilder.where(whereCondition);
            formTemps = queryBuilder.list();
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formTemps;
    }

    @Override
    public synchronized void updateFormAnswer(FormAnswer formAnswer) {
        try {
            if (formAnswer != null) {
                openWritableDb();
                daoSession.update(formAnswer);
                Log.d(TAG, "Updated surveyForm: " + formAnswer.getId() + " from the schema.");
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized List<FormAnswer> getUnSentFormAnswerList(FormAnswer formAnswerFS) {
        List<FormAnswer> formAnswers = null;
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, -5);
            openReadableDb();
            FormAnswerDao formAnswerDao = daoSession.getFormAnswerDao();
            QueryBuilder<FormAnswer> queryBuilder = formAnswerDao.queryBuilder();
            WhereCondition condition1 = formAnswerDao.queryBuilder().and(FormAnswerDao.Properties.SendingStatusEn.eq(SendingStatusEn.InProgress.ordinal()),
                    FormAnswerDao.Properties.SendingStatusDate.le(calendar.getTime()));

            WhereCondition condition2 = formAnswerDao.queryBuilder().or(FormAnswerDao.Properties.SendingStatusEn.eq(SendingStatusEn.Pending.ordinal()),
                    condition1);

            queryBuilder.where(condition2);

            if (formAnswerFS.getStatusEn() != null) {
                queryBuilder.where(FormAnswerDao.Properties.StatusEn.eq(formAnswerFS.getStatusEn()));
            }
            formAnswers = queryBuilder.list();

            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formAnswers;
    }

    @Override
    public AttachFile insertAttachFile(AttachFile attachFile) {
        try {

            if (attachFile != null) {
                openWritableDb();
                attachFile.setId(generateNewId());
                AttachFileDao attachFileDao = daoSession.getAttachFileDao();
                attachFileDao.insert(attachFile);
                Log.d(TAG, "Inserted or Replace surveyFormQuestion: " + attachFile.getId() + " to the schema.");
                daoSession.clear();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return attachFile;
    }

    @Override
    public synchronized List<AttachFile> getPendingAttachFileByEntityId(EntityNameEn entityNameEn, Long entityId) {
        List<AttachFile> attachFileList = null;
        try {
            openReadableDb();
            AttachFileDao attachFileDao = daoSession.getAttachFileDao();
            QueryBuilder<AttachFile> queryBuilder = attachFileDao.queryBuilder();
            WhereCondition condition1 = attachFileDao.queryBuilder().and(AttachFileDao.Properties.SendingStatusEn.eq(SendingStatusEn.Pending.ordinal()),
                    AttachFileDao.Properties.EntityNameEn.eq(entityNameEn.ordinal()), AttachFileDao.Properties.EntityId.eq(entityId));
            queryBuilder.where(condition1);
            attachFileList = queryBuilder.list();

            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return attachFileList;
    }

    @Override
    public synchronized List<AttachFile> getUnSentAttachFileList() {
        List<AttachFile> attachFileList = null;
        try {

            openReadableDb();
            AttachFileDao attachFileDao = daoSession.getAttachFileDao();
            QueryBuilder<AttachFile> queryBuilder = attachFileDao.queryBuilder();
            WhereCondition condition1 = attachFileDao.queryBuilder().or(AttachFileDao.Properties.SendingStatusEn.eq(SendingStatusEn.InProgress.ordinal()),
                    AttachFileDao.Properties.SendingStatusEn.eq(SendingStatusEn.AttachmentResuming.ordinal()));
            queryBuilder.where(condition1);
            attachFileList = queryBuilder.list();

            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return attachFileList;
    }

    @Override
    public synchronized void updateAttachFile(AttachFile attachFile) {
        try {
            if (attachFile != null) {
                openWritableDb();
                daoSession.update(attachFile);
                Log.d(TAG, "Updated ChatMessage: " + attachFile.getId() + " from the schema.");
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized AttachFile getAttachFileById(Long id) {
        AttachFile attachFile = null;
        try {
            openReadableDb();
            AttachFileDao attachFileDao = daoSession.getAttachFileDao();
            attachFile = attachFileDao.loadByRowId(id);
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return attachFile;
    }

    @Override
    public synchronized List<AttachFile> getAttachFileListById(Long id) {
        List<AttachFile> attachFiles = null;
        try {
            openReadableDb();
            AttachFileDao attachFileDao = daoSession.getAttachFileDao();
            QueryBuilder<AttachFile> queryBuilder = attachFileDao.queryBuilder();
            queryBuilder.where(AttachFileDao.Properties.EntityId.in(id));
            attachFiles = queryBuilder.list();
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return attachFiles;
    }

    @Override
    public synchronized void deleteAttachFile(AttachFile attachFile) {
        try {
            if (attachFile != null) {
                openWritableDb();
                daoSession.delete(attachFile);
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized boolean deleteAttachFileById(Long attachId) {
        try {
            openWritableDb();
            AttachFileDao attachFileDao = daoSession.getAttachFileDao();
            attachFileDao.deleteByKey(attachId);
            daoSession.clear();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /*@Override
    public synchronized DBUserDetails insertOrUpdateUserDetails(DBUserDetails userDetails) {
        try {
            if (userDetails != null) {
                openWritableDb();
                daoSession.insertOrReplace(userDetails);
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userDetails;
    }

    @Override
    public synchronized void deleteUserByFirstNameAndGender(String firstName, String gender) {
        try {
            openWritableDb();
            DBUserDetailsDao dao = daoSession.getDBUserDetailsDao();
            WhereCondition condition = dao.queryBuilder().and(DBUserDetailsDao.Properties.FirstName.eq(firstName),
                    DBUserDetailsDao.Properties.Gender.eq(gender));
            QueryBuilder<DBUserDetails> queryBuilder = dao.queryBuilder().where(condition);
            dao.deleteInTx(queryBuilder.list());
            daoSession.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void insertOrUpdatePhoneNumber(DBPhoneNumber phoneNumber) {
        try {
            if (phoneNumber != null) {
                openWritableDb();
                daoSession.insertOrReplace(phoneNumber);
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void bulkInsertPhoneNumbers(Set<DBPhoneNumber> phoneNumbers) {
        try {
            if (phoneNumbers != null && phoneNumbers.size() > 0) {
                openWritableDb();
                asyncSession.insertOrReplaceInTx(DBPhoneNumber.class, phoneNumbers);
                assertWaitForCompletion1Sec();
                daoSession.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
