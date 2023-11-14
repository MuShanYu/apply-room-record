package com.guet.ARC.dao.mybatis.repository;

import static com.guet.ARC.dao.mybatis.support.MessageDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

import com.guet.ARC.domain.Message;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.annotation.Generated;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.delete.DeleteDSLCompleter;
import org.mybatis.dynamic.sql.delete.render.DeleteStatementProvider;
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider;
import org.mybatis.dynamic.sql.insert.render.MultiRowInsertStatementProvider;
import org.mybatis.dynamic.sql.select.CountDSLCompleter;
import org.mybatis.dynamic.sql.select.SelectDSLCompleter;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.update.UpdateDSL;
import org.mybatis.dynamic.sql.update.UpdateDSLCompleter;
import org.mybatis.dynamic.sql.update.UpdateModel;
import org.mybatis.dynamic.sql.update.render.UpdateStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;
import org.mybatis.dynamic.sql.util.mybatis3.MyBatis3Utils;

@Mapper
public interface MessageQueryRepository {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0612516+08:00", comments="Source Table: tbl_message")
    BasicColumn[] selectList = BasicColumn.columnList(id, messageType, readState, content, messageReceiverId, messageSenderId, createTime, updateTime);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0552621+08:00", comments="Source Table: tbl_message")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    long count(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0562588+08:00", comments="Source Table: tbl_message")
    @DeleteProvider(type=SqlProviderAdapter.class, method="delete")
    int delete(DeleteStatementProvider deleteStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0562588+08:00", comments="Source Table: tbl_message")
    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    int insert(InsertStatementProvider<Message> insertStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0572554+08:00", comments="Source Table: tbl_message")
    @InsertProvider(type=SqlProviderAdapter.class, method="insertMultiple")
    int insertMultiple(MultiRowInsertStatementProvider<Message> multipleInsertStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0572554+08:00", comments="Source Table: tbl_message")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("MessageResult")
    Optional<Message> selectOne(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0572554+08:00", comments="Source Table: tbl_message")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="MessageResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="message_type", property="messageType", jdbcType=JdbcType.SMALLINT),
        @Result(column="read_state", property="readState", jdbcType=JdbcType.SMALLINT),
        @Result(column="content", property="content", jdbcType=JdbcType.VARCHAR),
        @Result(column="message_receiver_id", property="messageReceiverId", jdbcType=JdbcType.VARCHAR),
        @Result(column="message_sender_id", property="messageSenderId", jdbcType=JdbcType.VARCHAR),
        @Result(column="create_time", property="createTime", jdbcType=JdbcType.BIGINT),
        @Result(column="update_time", property="updateTime", jdbcType=JdbcType.BIGINT)
    })
    List<Message> selectMany(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0582519+08:00", comments="Source Table: tbl_message")
    @UpdateProvider(type=SqlProviderAdapter.class, method="update")
    int update(UpdateStatementProvider updateStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0582519+08:00", comments="Source Table: tbl_message")
    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, message, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0592485+08:00", comments="Source Table: tbl_message")
    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, message, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0592485+08:00", comments="Source Table: tbl_message")
    default int deleteByPrimaryKey(String id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0592485+08:00", comments="Source Table: tbl_message")
    default int insert(Message record) {
        return MyBatis3Utils.insert(this::insert, record, message, c ->
            c.map(id).toProperty("id")
            .map(messageType).toProperty("messageType")
            .map(readState).toProperty("readState")
            .map(content).toProperty("content")
            .map(messageReceiverId).toProperty("messageReceiverId")
            .map(messageSenderId).toProperty("messageSenderId")
            .map(createTime).toProperty("createTime")
            .map(updateTime).toProperty("updateTime")
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0602455+08:00", comments="Source Table: tbl_message")
    default int insertMultiple(Collection<Message> records) {
        return MyBatis3Utils.insertMultiple(this::insertMultiple, records, message, c ->
            c.map(id).toProperty("id")
            .map(messageType).toProperty("messageType")
            .map(readState).toProperty("readState")
            .map(content).toProperty("content")
            .map(messageReceiverId).toProperty("messageReceiverId")
            .map(messageSenderId).toProperty("messageSenderId")
            .map(createTime).toProperty("createTime")
            .map(updateTime).toProperty("updateTime")
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0602455+08:00", comments="Source Table: tbl_message")
    default int insertSelective(Message record) {
        return MyBatis3Utils.insert(this::insert, record, message, c ->
            c.map(id).toPropertyWhenPresent("id", record::getId)
            .map(messageType).toPropertyWhenPresent("messageType", record::getMessageType)
            .map(readState).toPropertyWhenPresent("readState", record::getReadState)
            .map(content).toPropertyWhenPresent("content", record::getContent)
            .map(messageReceiverId).toPropertyWhenPresent("messageReceiverId", record::getMessageReceiverId)
            .map(messageSenderId).toPropertyWhenPresent("messageSenderId", record::getMessageSenderId)
            .map(createTime).toPropertyWhenPresent("createTime", record::getCreateTime)
            .map(updateTime).toPropertyWhenPresent("updateTime", record::getUpdateTime)
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0622477+08:00", comments="Source Table: tbl_message")
    default Optional<Message> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, message, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0622477+08:00", comments="Source Table: tbl_message")
    default List<Message> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, message, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0622477+08:00", comments="Source Table: tbl_message")
    default List<Message> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, message, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0632353+08:00", comments="Source Table: tbl_message")
    default Optional<Message> selectByPrimaryKey(String id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0632353+08:00", comments="Source Table: tbl_message")
    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, message, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0632353+08:00", comments="Source Table: tbl_message")
    static UpdateDSL<UpdateModel> updateAllColumns(Message record, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(id).equalTo(record::getId)
                .set(messageType).equalTo(record::getMessageType)
                .set(readState).equalTo(record::getReadState)
                .set(content).equalTo(record::getContent)
                .set(messageReceiverId).equalTo(record::getMessageReceiverId)
                .set(messageSenderId).equalTo(record::getMessageSenderId)
                .set(createTime).equalTo(record::getCreateTime)
                .set(updateTime).equalTo(record::getUpdateTime);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0632353+08:00", comments="Source Table: tbl_message")
    static UpdateDSL<UpdateModel> updateSelectiveColumns(Message record, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(id).equalToWhenPresent(record::getId)
                .set(messageType).equalToWhenPresent(record::getMessageType)
                .set(readState).equalToWhenPresent(record::getReadState)
                .set(content).equalToWhenPresent(record::getContent)
                .set(messageReceiverId).equalToWhenPresent(record::getMessageReceiverId)
                .set(messageSenderId).equalToWhenPresent(record::getMessageSenderId)
                .set(createTime).equalToWhenPresent(record::getCreateTime)
                .set(updateTime).equalToWhenPresent(record::getUpdateTime);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0642323+08:00", comments="Source Table: tbl_message")
    default int updateByPrimaryKey(Message record) {
        return update(c ->
            c.set(messageType).equalTo(record::getMessageType)
            .set(readState).equalTo(record::getReadState)
            .set(content).equalTo(record::getContent)
            .set(messageReceiverId).equalTo(record::getMessageReceiverId)
            .set(messageSenderId).equalTo(record::getMessageSenderId)
            .set(createTime).equalTo(record::getCreateTime)
            .set(updateTime).equalTo(record::getUpdateTime)
            .where(id, isEqualTo(record::getId))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0642323+08:00", comments="Source Table: tbl_message")
    default int updateByPrimaryKeySelective(Message record) {
        return update(c ->
            c.set(messageType).equalToWhenPresent(record::getMessageType)
            .set(readState).equalToWhenPresent(record::getReadState)
            .set(content).equalToWhenPresent(record::getContent)
            .set(messageReceiverId).equalToWhenPresent(record::getMessageReceiverId)
            .set(messageSenderId).equalToWhenPresent(record::getMessageSenderId)
            .set(createTime).equalToWhenPresent(record::getCreateTime)
            .set(updateTime).equalToWhenPresent(record::getUpdateTime)
            .where(id, isEqualTo(record::getId))
        );
    }
}