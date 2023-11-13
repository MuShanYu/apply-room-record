package com.guet.ARC.dao.mybatis;

import static com.guet.ARC.dao.mybatis.support.ApplicationDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

import com.guet.ARC.domain.Application;
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
public interface ApplicationQueryRepository {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-13T11:46:32.4144335+08:00", comments="Source Table: tbl_application")
    BasicColumn[] selectList = BasicColumn.columnList(id, title, reason, applicationType, matterRecordId, handleUserId, applyUserId, state, remarks, createTime, updateTime);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-13T11:46:32.4144335+08:00", comments="Source Table: tbl_application")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    long count(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-13T11:46:32.4144335+08:00", comments="Source Table: tbl_application")
    @DeleteProvider(type=SqlProviderAdapter.class, method="delete")
    int delete(DeleteStatementProvider deleteStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-13T11:46:32.4144335+08:00", comments="Source Table: tbl_application")
    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    int insert(InsertStatementProvider<Application> insertStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-13T11:46:32.4144335+08:00", comments="Source Table: tbl_application")
    @InsertProvider(type=SqlProviderAdapter.class, method="insertMultiple")
    int insertMultiple(MultiRowInsertStatementProvider<Application> multipleInsertStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-13T11:46:32.4144335+08:00", comments="Source Table: tbl_application")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("ApplicationResult")
    Optional<Application> selectOne(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-13T11:46:32.4144335+08:00", comments="Source Table: tbl_application")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="ApplicationResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="title", property="title", jdbcType=JdbcType.VARCHAR),
        @Result(column="reason", property="reason", jdbcType=JdbcType.VARCHAR),
        @Result(column="application_type", property="applicationType", jdbcType=JdbcType.SMALLINT),
        @Result(column="matter_record_id", property="matterRecordId", jdbcType=JdbcType.VARCHAR),
        @Result(column="handle_user_id", property="handleUserId", jdbcType=JdbcType.VARCHAR),
        @Result(column="apply_user_id", property="applyUserId", jdbcType=JdbcType.VARCHAR),
        @Result(column="state", property="state", jdbcType=JdbcType.SMALLINT),
        @Result(column="remarks", property="remarks", jdbcType=JdbcType.VARCHAR),
        @Result(column="create_time", property="createTime", jdbcType=JdbcType.BIGINT),
        @Result(column="update_time", property="updateTime", jdbcType=JdbcType.BIGINT)
    })
    List<Application> selectMany(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-13T11:46:32.4144335+08:00", comments="Source Table: tbl_application")
    @UpdateProvider(type=SqlProviderAdapter.class, method="update")
    int update(UpdateStatementProvider updateStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-13T11:46:32.4144335+08:00", comments="Source Table: tbl_application")
    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, application, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-13T11:46:32.4144335+08:00", comments="Source Table: tbl_application")
    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, application, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-13T11:46:32.4144335+08:00", comments="Source Table: tbl_application")
    default int deleteByPrimaryKey(String id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-13T11:46:32.4144335+08:00", comments="Source Table: tbl_application")
    default int insert(Application record) {
        return MyBatis3Utils.insert(this::insert, record, application, c ->
            c.map(id).toProperty("id")
            .map(title).toProperty("title")
            .map(reason).toProperty("reason")
            .map(applicationType).toProperty("applicationType")
            .map(matterRecordId).toProperty("matterRecordId")
            .map(handleUserId).toProperty("handleUserId")
            .map(applyUserId).toProperty("applyUserId")
            .map(state).toProperty("state")
            .map(remarks).toProperty("remarks")
            .map(createTime).toProperty("createTime")
            .map(updateTime).toProperty("updateTime")
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-13T11:46:32.4144335+08:00", comments="Source Table: tbl_application")
    default int insertMultiple(Collection<Application> records) {
        return MyBatis3Utils.insertMultiple(this::insertMultiple, records, application, c ->
            c.map(id).toProperty("id")
            .map(title).toProperty("title")
            .map(reason).toProperty("reason")
            .map(applicationType).toProperty("applicationType")
            .map(matterRecordId).toProperty("matterRecordId")
            .map(handleUserId).toProperty("handleUserId")
            .map(applyUserId).toProperty("applyUserId")
            .map(state).toProperty("state")
            .map(remarks).toProperty("remarks")
            .map(createTime).toProperty("createTime")
            .map(updateTime).toProperty("updateTime")
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-13T11:46:32.4144335+08:00", comments="Source Table: tbl_application")
    default int insertSelective(Application record) {
        return MyBatis3Utils.insert(this::insert, record, application, c ->
            c.map(id).toPropertyWhenPresent("id", record::getId)
            .map(title).toPropertyWhenPresent("title", record::getTitle)
            .map(reason).toPropertyWhenPresent("reason", record::getReason)
            .map(applicationType).toPropertyWhenPresent("applicationType", record::getApplicationType)
            .map(matterRecordId).toPropertyWhenPresent("matterRecordId", record::getMatterRecordId)
            .map(handleUserId).toPropertyWhenPresent("handleUserId", record::getHandleUserId)
            .map(applyUserId).toPropertyWhenPresent("applyUserId", record::getApplyUserId)
            .map(state).toPropertyWhenPresent("state", record::getState)
            .map(remarks).toPropertyWhenPresent("remarks", record::getRemarks)
            .map(createTime).toPropertyWhenPresent("createTime", record::getCreateTime)
            .map(updateTime).toPropertyWhenPresent("updateTime", record::getUpdateTime)
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-13T11:46:32.4144335+08:00", comments="Source Table: tbl_application")
    default Optional<Application> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, application, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-13T11:46:32.4144335+08:00", comments="Source Table: tbl_application")
    default List<Application> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, application, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-13T11:46:32.4144335+08:00", comments="Source Table: tbl_application")
    default List<Application> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, application, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-13T11:46:32.4144335+08:00", comments="Source Table: tbl_application")
    default Optional<Application> selectByPrimaryKey(String id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-13T11:46:32.4144335+08:00", comments="Source Table: tbl_application")
    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, application, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-13T11:46:32.4144335+08:00", comments="Source Table: tbl_application")
    static UpdateDSL<UpdateModel> updateAllColumns(Application record, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(id).equalTo(record::getId)
                .set(title).equalTo(record::getTitle)
                .set(reason).equalTo(record::getReason)
                .set(applicationType).equalTo(record::getApplicationType)
                .set(matterRecordId).equalTo(record::getMatterRecordId)
                .set(handleUserId).equalTo(record::getHandleUserId)
                .set(applyUserId).equalTo(record::getApplyUserId)
                .set(state).equalTo(record::getState)
                .set(remarks).equalTo(record::getRemarks)
                .set(createTime).equalTo(record::getCreateTime)
                .set(updateTime).equalTo(record::getUpdateTime);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-13T11:46:32.4144335+08:00", comments="Source Table: tbl_application")
    static UpdateDSL<UpdateModel> updateSelectiveColumns(Application record, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(id).equalToWhenPresent(record::getId)
                .set(title).equalToWhenPresent(record::getTitle)
                .set(reason).equalToWhenPresent(record::getReason)
                .set(applicationType).equalToWhenPresent(record::getApplicationType)
                .set(matterRecordId).equalToWhenPresent(record::getMatterRecordId)
                .set(handleUserId).equalToWhenPresent(record::getHandleUserId)
                .set(applyUserId).equalToWhenPresent(record::getApplyUserId)
                .set(state).equalToWhenPresent(record::getState)
                .set(remarks).equalToWhenPresent(record::getRemarks)
                .set(createTime).equalToWhenPresent(record::getCreateTime)
                .set(updateTime).equalToWhenPresent(record::getUpdateTime);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-13T11:46:32.415424+08:00", comments="Source Table: tbl_application")
    default int updateByPrimaryKey(Application record) {
        return update(c ->
            c.set(title).equalTo(record::getTitle)
            .set(reason).equalTo(record::getReason)
            .set(applicationType).equalTo(record::getApplicationType)
            .set(matterRecordId).equalTo(record::getMatterRecordId)
            .set(handleUserId).equalTo(record::getHandleUserId)
            .set(applyUserId).equalTo(record::getApplyUserId)
            .set(state).equalTo(record::getState)
            .set(remarks).equalTo(record::getRemarks)
            .set(createTime).equalTo(record::getCreateTime)
            .set(updateTime).equalTo(record::getUpdateTime)
            .where(id, isEqualTo(record::getId))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-13T11:46:32.415424+08:00", comments="Source Table: tbl_application")
    default int updateByPrimaryKeySelective(Application record) {
        return update(c ->
            c.set(title).equalToWhenPresent(record::getTitle)
            .set(reason).equalToWhenPresent(record::getReason)
            .set(applicationType).equalToWhenPresent(record::getApplicationType)
            .set(matterRecordId).equalToWhenPresent(record::getMatterRecordId)
            .set(handleUserId).equalToWhenPresent(record::getHandleUserId)
            .set(applyUserId).equalToWhenPresent(record::getApplyUserId)
            .set(state).equalToWhenPresent(record::getState)
            .set(remarks).equalToWhenPresent(record::getRemarks)
            .set(createTime).equalToWhenPresent(record::getCreateTime)
            .set(updateTime).equalToWhenPresent(record::getUpdateTime)
            .where(id, isEqualTo(record::getId))
        );
    }
}