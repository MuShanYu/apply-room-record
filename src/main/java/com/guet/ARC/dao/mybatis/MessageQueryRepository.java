package com.guet.ARC.dao.mybatis;

import static com.guet.ARC.dao.mybatis.support.MessageDynamicSqlSupport.*;

import com.guet.ARC.dao.mybatis.support.UserDynamicSqlSupport;
import com.guet.ARC.domain.Message;

import java.util.List;
import javax.annotation.Generated;

import com.guet.ARC.domain.vo.message.MessageVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;

@Mapper
public interface MessageQueryRepository {
    BasicColumn[] selectList = BasicColumn.columnList(id, messageType, readState, content, messageReceiverId, messageSenderId, createTime,
            updateTime, UserDynamicSqlSupport.name);

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

    })
    List<MessageVo> selectMany(SelectStatementProvider selectStatement);
}