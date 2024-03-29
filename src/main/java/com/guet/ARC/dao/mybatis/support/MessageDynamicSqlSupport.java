package com.guet.ARC.dao.mybatis.support;

import java.sql.JDBCType;
import javax.annotation.Generated;

import com.guet.ARC.domain.enums.MessageType;
import com.guet.ARC.domain.enums.ReadState;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class MessageDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0542654+08:00", comments="Source Table: tbl_message")
    public static final Message message = new Message();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0542654+08:00", comments="Source field: tbl_message.id")
    public static final SqlColumn<String> id = message.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0542654+08:00", comments="Source field: tbl_message.message_type")
    public static final SqlColumn<MessageType> messageType = message.messageType;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0542654+08:00", comments="Source field: tbl_message.read_state")
    public static final SqlColumn<ReadState> readState = message.readState;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0542654+08:00", comments="Source field: tbl_message.content")
    public static final SqlColumn<String> content = message.content;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0542654+08:00", comments="Source field: tbl_message.message_receiver_id")
    public static final SqlColumn<String> messageReceiverId = message.messageReceiverId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0542654+08:00", comments="Source field: tbl_message.message_sender_id")
    public static final SqlColumn<String> messageSenderId = message.messageSenderId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0552621+08:00", comments="Source field: tbl_message.create_time")
    public static final SqlColumn<Long> createTime = message.createTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0552621+08:00", comments="Source field: tbl_message.update_time")
    public static final SqlColumn<Long> updateTime = message.updateTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0542654+08:00", comments="Source Table: tbl_message")
    public static final class Message extends SqlTable {
        public final SqlColumn<String> id = column("id", JDBCType.VARCHAR);

        public final SqlColumn<MessageType> messageType = column("message_type", JDBCType.SMALLINT);

        public final SqlColumn<ReadState> readState = column("read_state", JDBCType.SMALLINT);

        public final SqlColumn<String> content = column("content", JDBCType.VARCHAR);

        public final SqlColumn<String> messageReceiverId = column("message_receiver_id", JDBCType.VARCHAR);

        public final SqlColumn<String> messageSenderId = column("message_sender_id", JDBCType.VARCHAR);

        public final SqlColumn<Long> createTime = column("create_time", JDBCType.BIGINT);

        public final SqlColumn<Long> updateTime = column("update_time", JDBCType.BIGINT);

        public Message() {
            super("tbl_message");
        }
    }
}