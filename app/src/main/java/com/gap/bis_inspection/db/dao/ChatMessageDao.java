package com.gap.bis_inspection.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.gap.bis_inspection.db.objectmodel.ChatMessage;

import java.util.List;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table "CHAT_MESSAGE".
 */
public class ChatMessageDao extends AbstractDao<ChatMessage, Long> {

    public static final String TABLENAME = "CHAT_MESSAGE";

    /**
     * Properties of entity ChatMessage.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property ServerMessageId = new Property(1, Long.class, "serverMessageId", false, "SERVER_MESSAGE_ID");
        public final static Property ValidUntilDate = new Property(2, java.util.Date.class, "validUntilDate", false, "VALID_UNTIL_DATE");
        public final static Property Message = new Property(3, String.class, "message", false, "MESSAGE");
        public final static Property AttachFileLocalPath = new Property(4, String.class, "attachFileLocalPath", false, "ATTACH_FILE_LOCAL_PATH");
        public final static Property AttachFileUserFileName = new Property(5, String.class, "attachFileUserFileName", false, "ATTACH_FILE_USER_FILE_NAME");
        public final static Property AttachFileRemoteUrl = new Property(6, String.class, "attachFileRemoteUrl", false, "ATTACH_FILE_REMOTE_URL");
        public final static Property SendDate = new Property(7, java.util.Date.class, "sendDate", false, "SEND_DATE");
        public final static Property DateCreation = new Property(8, java.util.Date.class, "dateCreation", false, "DATE_CREATION");
        public final static Property DeliverIs = new Property(9, Boolean.class, "deliverIs", false, "DELIVER_IS");
        public final static Property DeliverDate = new Property(10, java.util.Date.class, "deliverDate", false, "DELIVER_DATE");
        public final static Property ReadIs = new Property(11, Boolean.class, "readIs", false, "READ_IS");
        public final static Property IsCreateNewPvChatGroup = new Property(12, Boolean.class, "isCreateNewPvChatGroup", false, "isCreateNewPvChatGroup");
        public final static Property ReadDate = new Property(13, java.util.Date.class, "readDate", false, "READ_DATE");
        public final static Property SendingStatusEn = new Property(14, Integer.class, "sendingStatusEn", false, "SENDING_STATUS_EN");
        public final static Property SendingStatusDate = new Property(15, java.util.Date.class, "sendingStatusDate", false, "SENDING_STATUS_DATE");
        public final static Property AttachFileSize = new Property(16, Integer.class, "attachFileSize", false, "ATTACH_FILE_SIZE");
        public final static Property AttachFileSentSize = new Property(17, Integer.class, "attachFileSentSize", false, "ATTACH_FILE_SENT_SIZE");
        public final static Property AttachFileReceivedSize = new Property(18, Integer.class, "attachFileReceivedSize", false, "ATTACH_FILE_RECEIVED_SIZE");
        public final static Property FileType = new Property(19, Integer.class, "fileType", false, "FILE_TYPE");
        public final static Property SenderAppUserId = new Property(20, Long.class, "senderAppUserId", false, "SENDER_APP_USER_ID");
        public final static Property ReceiverAppUserId = new Property(21, Long.class, "receiverAppUserId", false, "RECEIVER_APP_USER_ID");
        public final static Property ChatGroupId = new Property(22, Long.class, "chatGroupId", false, "CHAT_GROUP_ID");
    }

    private Query<ChatMessage> appUser_SendChatMessageListQuery;
    private Query<ChatMessage> appUser_ReceiveChatMessageListQuery;
    private Query<ChatMessage> chatGroup_ChatMessageListQuery;

    public ChatMessageDao(DaoConfig config) {
        super(config);
    }

    public ChatMessageDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "\"CHAT_MESSAGE\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"SERVER_MESSAGE_ID\" INTEGER," + // 1: serverMessageId
                "\"VALID_UNTIL_DATE\" INTEGER," + // 2: validUntilDate
                "\"MESSAGE\" TEXT," + // 3: message
                "\"ATTACH_FILE_LOCAL_PATH\" TEXT," + // 4: attachFileLocalPath
                "\"ATTACH_FILE_USER_FILE_NAME\" TEXT," + // 5: attachFileUserFileName
                "\"ATTACH_FILE_REMOTE_URL\" TEXT," + // 6: attachFileRemoteUrl
                "\"SEND_DATE\" INTEGER," + // 7: sendDate
                "\"DATE_CREATION\" INTEGER," + // 8: dateCreation
                "\"DELIVER_IS\" INTEGER," + // 9: deliverIs
                "\"DELIVER_DATE\" INTEGER," + // 10: deliverDate
                "\"READ_IS\" INTEGER," + // 11: readIs
                "\"IsCreateNewPvChatGroup\" INTEGER," + // 11: isCreateNewPvChatGroup
                "\"READ_DATE\" INTEGER," + // 12: readDate
                "\"SENDING_STATUS_EN\" INTEGER," + // 13: sendingStatusEn
                "\"SENDING_STATUS_DATE\" INTEGER," + // 14: sendingStatusDate
                "\"ATTACH_FILE_SIZE\" INTEGER," + // 15: attachFileSize
                "\"ATTACH_FILE_SENT_SIZE\" INTEGER," + // 16: attachFileSentSize
                "\"ATTACH_FILE_RECEIVED_SIZE\" INTEGER," + // 17: attachFileReceivedSize
                "\"FILE_TYPE\" INTEGER," + // 17: FileType
                "\"SENDER_APP_USER_ID\" INTEGER," + // 18: senderAppUserId
                "\"RECEIVER_APP_USER_ID\" INTEGER," + // 19: receiverAppUserId
                "\"CHAT_GROUP_ID\" INTEGER);"); // 20: chatGroupId
    }

    /**
     * Drops the underlying database table.
     */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"CHAT_MESSAGE\"";
        db.execSQL(sql);
    }

    /**
     * @inheritdoc
     */
    @Override
    protected void bindValues(SQLiteStatement stmt, ChatMessage entity) {
        stmt.clearBindings();

        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }

        Long serverMessageId = entity.getServerMessageId();
        if (serverMessageId != null) {
            stmt.bindLong(2, serverMessageId);
        }

        java.util.Date validUntilDate = entity.getValidUntilDate();
        if (validUntilDate != null) {
            stmt.bindLong(3, validUntilDate.getTime());
        }

        String message = entity.getMessage();
        if (message != null) {
            stmt.bindString(4, message);
        }

        String attachFileLocalPath = entity.getAttachFileLocalPath();
        if (attachFileLocalPath != null) {
            stmt.bindString(5, attachFileLocalPath);
        }

        String attachFileUserFileName = entity.getAttachFileUserFileName();
        if (attachFileUserFileName != null) {
            stmt.bindString(6, attachFileUserFileName);
        }

        String attachFileRemoteUrl = entity.getAttachFileRemoteUrl();
        if (attachFileRemoteUrl != null) {
            stmt.bindString(7, attachFileRemoteUrl);
        }

        java.util.Date sendDate = entity.getSendDate();
        if (sendDate != null) {
            stmt.bindLong(8, sendDate.getTime());
        }

        java.util.Date dateCreation = entity.getDateCreation();
        if (dateCreation != null) {
            stmt.bindLong(9, dateCreation.getTime());
        }

        Boolean deliverIs = entity.getDeliverIs();
        if (deliverIs != null) {
            stmt.bindLong(10, deliverIs ? 1L : 0L);
        }

        java.util.Date deliverDate = entity.getDeliverDate();
        if (deliverDate != null) {
            stmt.bindLong(11, deliverDate.getTime());
        }

        Boolean readIs = entity.getReadIs();
        if (readIs != null) {
            stmt.bindLong(12, readIs ? 1L : 0L);
        }

        Boolean isCreateNewPvChatGroup = entity.getCreateNewPvChatGroup();
        if (isCreateNewPvChatGroup != null) {
            stmt.bindLong(13, isCreateNewPvChatGroup ? 1L : 0L);
        }

        java.util.Date readDate = entity.getReadDate();
        if (readDate != null) {
            stmt.bindLong(14, readDate.getTime());
        }

        Integer sendingStatusEn = entity.getSendingStatusEn();
        if (sendingStatusEn != null) {
            stmt.bindLong(15, sendingStatusEn);
        }

        java.util.Date sendingStatusDate = entity.getSendingStatusDate();
        if (sendingStatusDate != null) {
            stmt.bindLong(16, sendingStatusDate.getTime());
        }

        Integer attachFileSize = entity.getAttachFileSize();
        if (attachFileSize != null) {
            stmt.bindLong(17, attachFileSize);
        }

        Integer attachFileSentSize = entity.getAttachFileSentSize();
        if (attachFileSentSize != null) {
            stmt.bindLong(18, attachFileSentSize);
        }

        Integer attachFileReceivedSize = entity.getAttachFileReceivedSize();
        if (attachFileReceivedSize != null) {
            stmt.bindLong(19, attachFileReceivedSize);
        }

        Integer fileType = entity.getFileType();
        if (fileType != null) {
            stmt.bindLong(20, fileType);
        }

        Long senderAppUserId = entity.getSenderAppUserId();
        if (senderAppUserId != null) {
            stmt.bindLong(21, senderAppUserId);
        }

        Long receiverAppUserId = entity.getReceiverAppUserId();
        if (receiverAppUserId != null) {
            stmt.bindLong(22, receiverAppUserId);
        }

        Long chatGroupId = entity.getChatGroupId();
        if (chatGroupId != null) {
            stmt.bindLong(23, chatGroupId);
        }
    }

    /**
     * @inheritdoc
     */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }

    /**
     * @inheritdoc
     */
    @Override
    public ChatMessage readEntity(Cursor cursor, int offset) {
        ChatMessage entity = new ChatMessage( //
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
                cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // serverMessageId
                cursor.isNull(offset + 2) ? null : new java.util.Date(cursor.getLong(offset + 2)), // validUntilDate
                cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // message
                cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // attachFileLocalPath
                cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // attachFileUserFileName
                cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // attachFileRemoteUrl
                cursor.isNull(offset + 7) ? null : new java.util.Date(cursor.getLong(offset + 7)), // sendDate
                cursor.isNull(offset + 8) ? null : new java.util.Date(cursor.getLong(offset + 8)), // dateCreation
                cursor.isNull(offset + 9) ? null : cursor.getShort(offset + 9) != 0, // deliverIs
                cursor.isNull(offset + 10) ? null : new java.util.Date(cursor.getLong(offset + 10)), // deliverDate
                cursor.isNull(offset + 11) ? null : cursor.getShort(offset + 11) != 0, // readIs
                cursor.isNull(offset + 12) ? null : cursor.getShort(offset + 12) != 0, // isCreateNewPvChatGroup
                cursor.isNull(offset + 13) ? null : new java.util.Date(cursor.getLong(offset + 13)), // readDate
                cursor.isNull(offset + 14) ? null : cursor.getInt(offset + 14), // sendingStatusEn
                cursor.isNull(offset + 15) ? null : new java.util.Date(cursor.getLong(offset + 15)), // sendingStatusDate
                cursor.isNull(offset + 16) ? null : cursor.getInt(offset + 16), // attachFileSize
                cursor.isNull(offset + 17) ? null : cursor.getInt(offset + 17), // attachFileSentSize
                cursor.isNull(offset + 18) ? null : cursor.getInt(offset + 18), // attachFileReceivedSize
                cursor.isNull(offset + 19) ? null : cursor.getInt(offset + 19), // fileType
                cursor.isNull(offset + 20) ? null : cursor.getLong(offset + 20), // senderAppUserId
                cursor.isNull(offset + 21) ? null : cursor.getLong(offset + 21), // receiverAppUserId
                cursor.isNull(offset + 22) ? null : cursor.getLong(offset + 22) // chatGroupId
        );
        return entity;
    }

    /**
     * @inheritdoc
     */
    @Override
    public void readEntity(Cursor cursor, ChatMessage entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setServerMessageId(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setValidUntilDate(cursor.isNull(offset + 2) ? null : new java.util.Date(cursor.getLong(offset + 2)));
        entity.setMessage(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setAttachFileLocalPath(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setAttachFileUserFileName(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setAttachFileRemoteUrl(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setSendDate(cursor.isNull(offset + 7) ? null : new java.util.Date(cursor.getLong(offset + 7)));
        entity.setDateCreation(cursor.isNull(offset + 8) ? null : new java.util.Date(cursor.getLong(offset + 8)));
        entity.setDeliverIs(cursor.isNull(offset + 9) ? null : cursor.getShort(offset + 9) != 0);
        entity.setDeliverDate(cursor.isNull(offset + 10) ? null : new java.util.Date(cursor.getLong(offset + 10)));
        entity.setReadIs(cursor.isNull(offset + 11) ? null : cursor.getShort(offset + 11) != 0);
        entity.setCreateNewPvChatGroup(cursor.isNull(offset + 12) ? null : cursor.getShort(offset + 12) != 0);
        entity.setReadDate(cursor.isNull(offset + 13) ? null : new java.util.Date(cursor.getLong(offset + 13)));
        entity.setSendingStatusEn(cursor.isNull(offset + 14) ? null : cursor.getInt(offset + 14));
        entity.setSendingStatusDate(cursor.isNull(offset + 15) ? null : new java.util.Date(cursor.getLong(offset + 15)));
        entity.setAttachFileSize(cursor.isNull(offset + 16) ? null : cursor.getInt(offset + 16));
        entity.setAttachFileSentSize(cursor.isNull(offset + 17) ? null : cursor.getInt(offset + 17));
        entity.setAttachFileReceivedSize(cursor.isNull(offset + 18) ? null : cursor.getInt(offset + 18));
        entity.setFileType(cursor.isNull(offset + 19) ? null : cursor.getInt(offset + 19));
        entity.setSenderAppUserId(cursor.isNull(offset + 20) ? null : cursor.getLong(offset + 20));
        entity.setReceiverAppUserId(cursor.isNull(offset + 21) ? null : cursor.getLong(offset + 21));
        entity.setChatGroupId(cursor.isNull(offset + 22) ? null : cursor.getLong(offset + 22));
    }

    /**
     * @inheritdoc
     */
    @Override
    protected Long updateKeyAfterInsert(ChatMessage entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }

    /**
     * @inheritdoc
     */
    @Override
    public Long getKey(ChatMessage entity) {
        if (entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /**
     * @inheritdoc
     */
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }

    /**
     * Internal query to resolve the "sendChatMessageList" to-many relationship of AppUser.
     */
    public List<ChatMessage> _queryAppUser_SendChatMessageList(Long senderAppUserId) {
        synchronized (this) {
            if (appUser_SendChatMessageListQuery == null) {
                QueryBuilder<ChatMessage> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.SenderAppUserId.eq(null));
                appUser_SendChatMessageListQuery = queryBuilder.build();
            }
        }
        Query<ChatMessage> query = appUser_SendChatMessageListQuery.forCurrentThread();
        query.setParameter(0, senderAppUserId);
        return query.list();
    }

    /**
     * Internal query to resolve the "receiveChatMessageList" to-many relationship of AppUser.
     */
    public List<ChatMessage> _queryAppUser_ReceiveChatMessageList(Long receiverAppUserId) {
        synchronized (this) {
            if (appUser_ReceiveChatMessageListQuery == null) {
                QueryBuilder<ChatMessage> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.ReceiverAppUserId.eq(null));
                appUser_ReceiveChatMessageListQuery = queryBuilder.build();
            }
        }
        Query<ChatMessage> query = appUser_ReceiveChatMessageListQuery.forCurrentThread();
        query.setParameter(0, receiverAppUserId);
        return query.list();
    }

    /**
     * Internal query to resolve the "chatMessageList" to-many relationship of ChatGroup.
     */
    public List<ChatMessage> _queryChatGroup_ChatMessageList(Long chatGroupId) {
        synchronized (this) {
            if (chatGroup_ChatMessageListQuery == null) {
                QueryBuilder<ChatMessage> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.ChatGroupId.eq(null));
                chatGroup_ChatMessageListQuery = queryBuilder.build();
            }
        }
        Query<ChatMessage> query = chatGroup_ChatMessageListQuery.forCurrentThread();
        query.setParameter(0, chatGroupId);
        return query.list();
    }

}