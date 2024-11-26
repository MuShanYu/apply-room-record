package com.guet.ARC.dao.mybatis;

import static com.guet.ARC.dao.mybatis.support.MessageDynamicSqlSupport.*;

import com.guet.ARC.dao.mybatis.support.UserDynamicSqlSupport;

import java.util.List;
import javax.annotation.Generated;

import com.guet.ARC.domain.Message;
import com.guet.ARC.domain.vo.message.MessageVo;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;

@Mapper
public interface MessageQueryRepository {
    BasicColumn[] selectList = BasicColumn.columnList(id, messageType, readState, content, messageReceiverId, messageSenderId, createTime,
            updateTime, UserDynamicSqlSupport.name, UserDynamicSqlSupport.stuNum);

    @Generated(value = "org.mybatis.generator.api.MyBatisGenerator", date = "2023-11-14T09:19:41.0572554+08:00", comments = "Source Table: tbl_message")
    @SelectProvider(type = SqlProviderAdapter.class, method = "select")
    @Results(id = "MessageResult", value = {
            @Result(column = "id", property = "id", jdbcType = JdbcType.VARCHAR, id = true),
            @Result(column = "message_type", property = "messageType", jdbcType = JdbcType.SMALLINT),
            @Result(column = "read_state", property = "readState", jdbcType = JdbcType.SMALLINT),
            @Result(column = "content", property = "content", jdbcType = JdbcType.VARCHAR),
            @Result(column = "message_receiver_id", property = "messageReceiverId", jdbcType = JdbcType.VARCHAR),
            @Result(column = "message_sender_id", property = "messageSenderId", jdbcType = JdbcType.VARCHAR),
            @Result(column = "create_time", property = "createTime", jdbcType = JdbcType.BIGINT),
            @Result(column = "update_time", property = "updateTime", jdbcType = JdbcType.BIGINT),
            @Result(column = "name", property = "senderUserName", jdbcType = JdbcType.VARCHAR),
            @Result(column = "stu_num", property = "stuNum", jdbcType = JdbcType.VARCHAR),
    })
    List<MessageVo> selectMany(SelectStatementProvider selectStatement);

    @Select({
            "SELECT DISTINCT id FROM (",
                "SELECT message_sender_id as id, create_time ",
                "FROM tbl_message ",
                "WHERE message_receiver_id = #{userId} ",
                "AND message_type = #{messageType} ",
                "UNION ",
                "SELECT message_receiver_id as id, create_time ",
                "FROM tbl_message ",
                "WHERE message_sender_id = #{userId} ",
                "AND message_type = #{messageType}",
                "ORDER BY create_time DESC",
            ") as temp"
    })
    List<String> querySenderOrReceiverIds(@Param("userId") String userId, @Param("messageType") int messageType);

    @Select({
            "SELECT * ",
            "FROM tbl_message ",
            "WHERE message_receiver_id = #{receiverId} ",
            "AND message_sender_id = #{senderId} ",
            "AND message_type = #{messageType} ",
            "UNION ",
            "SELECT * ",
            "FROM tbl_message ",
            "WHERE message_receiver_id = #{senderId} ",
            "AND message_sender_id = #{receiverId} ",
            "AND message_type = #{messageType} ",
            "ORDER BY create_time DESC ",
            "LIMIT 1"
    })
    @ResultMap("MessageResult")
    Message queryLatestMsg(String senderId, String receiverId, int messageType);

}