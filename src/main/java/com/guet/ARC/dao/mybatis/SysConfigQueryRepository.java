package com.guet.ARC.dao.mybatis;

import static com.guet.ARC.dao.mybatis.support.SysConfigDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

import com.guet.ARC.domain.SysConfig;
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
public interface SysConfigQueryRepository {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.555+08:00", comments="Source Table: tbl_sys_config")
    BasicColumn[] selectList = BasicColumn.columnList(id, configKey, configDesc, state, createTime, updateTime, configValue);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.546+08:00", comments="Source Table: tbl_sys_config")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    long count(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.547+08:00", comments="Source Table: tbl_sys_config")
    @DeleteProvider(type=SqlProviderAdapter.class, method="delete")
    int delete(DeleteStatementProvider deleteStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.548+08:00", comments="Source Table: tbl_sys_config")
    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    int insert(InsertStatementProvider<SysConfig> insertStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.548+08:00", comments="Source Table: tbl_sys_config")
    @InsertProvider(type=SqlProviderAdapter.class, method="insertMultiple")
    int insertMultiple(MultiRowInsertStatementProvider<SysConfig> multipleInsertStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.549+08:00", comments="Source Table: tbl_sys_config")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("SysConfigResult")
    Optional<SysConfig> selectOne(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.549+08:00", comments="Source Table: tbl_sys_config")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="SysConfigResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="config_key", property="configKey", jdbcType=JdbcType.VARCHAR),
        @Result(column="config_desc", property="configDesc", jdbcType=JdbcType.VARCHAR),
        @Result(column="state", property="state", jdbcType=JdbcType.SMALLINT),
        @Result(column="create_time", property="createTime", jdbcType=JdbcType.BIGINT),
        @Result(column="update_time", property="updateTime", jdbcType=JdbcType.BIGINT),
        @Result(column="config_value", property="configValue", jdbcType=JdbcType.LONGVARCHAR)
    })
    List<SysConfig> selectMany(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.55+08:00", comments="Source Table: tbl_sys_config")
    @UpdateProvider(type=SqlProviderAdapter.class, method="update")
    int update(UpdateStatementProvider updateStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.551+08:00", comments="Source Table: tbl_sys_config")
    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, sysConfig, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.551+08:00", comments="Source Table: tbl_sys_config")
    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, sysConfig, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.552+08:00", comments="Source Table: tbl_sys_config")
    default int deleteByPrimaryKey(String id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.552+08:00", comments="Source Table: tbl_sys_config")
    default int insert(SysConfig record) {
        return MyBatis3Utils.insert(this::insert, record, sysConfig, c ->
            c.map(id).toProperty("id")
            .map(configKey).toProperty("configKey")
            .map(configDesc).toProperty("configDesc")
            .map(state).toProperty("state")
            .map(createTime).toProperty("createTime")
            .map(updateTime).toProperty("updateTime")
            .map(configValue).toProperty("configValue")
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.553+08:00", comments="Source Table: tbl_sys_config")
    default int insertMultiple(Collection<SysConfig> records) {
        return MyBatis3Utils.insertMultiple(this::insertMultiple, records, sysConfig, c ->
            c.map(id).toProperty("id")
            .map(configKey).toProperty("configKey")
            .map(configDesc).toProperty("configDesc")
            .map(state).toProperty("state")
            .map(createTime).toProperty("createTime")
            .map(updateTime).toProperty("updateTime")
            .map(configValue).toProperty("configValue")
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.554+08:00", comments="Source Table: tbl_sys_config")
    default int insertSelective(SysConfig record) {
        return MyBatis3Utils.insert(this::insert, record, sysConfig, c ->
            c.map(id).toPropertyWhenPresent("id", record::getId)
            .map(configKey).toPropertyWhenPresent("configKey", record::getConfigKey)
            .map(configDesc).toPropertyWhenPresent("configDesc", record::getConfigDesc)
            .map(state).toPropertyWhenPresent("state", record::getState)
            .map(createTime).toPropertyWhenPresent("createTime", record::getCreateTime)
            .map(updateTime).toPropertyWhenPresent("updateTime", record::getUpdateTime)
            .map(configValue).toPropertyWhenPresent("configValue", record::getConfigValue)
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.556+08:00", comments="Source Table: tbl_sys_config")
    default Optional<SysConfig> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, sysConfig, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.556+08:00", comments="Source Table: tbl_sys_config")
    default List<SysConfig> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, sysConfig, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.557+08:00", comments="Source Table: tbl_sys_config")
    default List<SysConfig> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, sysConfig, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.557+08:00", comments="Source Table: tbl_sys_config")
    default Optional<SysConfig> selectByPrimaryKey(String id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.557+08:00", comments="Source Table: tbl_sys_config")
    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, sysConfig, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.558+08:00", comments="Source Table: tbl_sys_config")
    static UpdateDSL<UpdateModel> updateAllColumns(SysConfig record, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(id).equalTo(record::getId)
                .set(configKey).equalTo(record::getConfigKey)
                .set(configDesc).equalTo(record::getConfigDesc)
                .set(state).equalTo(record::getState)
                .set(createTime).equalTo(record::getCreateTime)
                .set(updateTime).equalTo(record::getUpdateTime)
                .set(configValue).equalTo(record::getConfigValue);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.558+08:00", comments="Source Table: tbl_sys_config")
    static UpdateDSL<UpdateModel> updateSelectiveColumns(SysConfig record, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(id).equalToWhenPresent(record::getId)
                .set(configKey).equalToWhenPresent(record::getConfigKey)
                .set(configDesc).equalToWhenPresent(record::getConfigDesc)
                .set(state).equalToWhenPresent(record::getState)
                .set(createTime).equalToWhenPresent(record::getCreateTime)
                .set(updateTime).equalToWhenPresent(record::getUpdateTime)
                .set(configValue).equalToWhenPresent(record::getConfigValue);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.559+08:00", comments="Source Table: tbl_sys_config")
    default int updateByPrimaryKey(SysConfig record) {
        return update(c ->
            c.set(configKey).equalTo(record::getConfigKey)
            .set(configDesc).equalTo(record::getConfigDesc)
            .set(state).equalTo(record::getState)
            .set(createTime).equalTo(record::getCreateTime)
            .set(updateTime).equalTo(record::getUpdateTime)
            .set(configValue).equalTo(record::getConfigValue)
            .where(id, isEqualTo(record::getId))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.56+08:00", comments="Source Table: tbl_sys_config")
    default int updateByPrimaryKeySelective(SysConfig record) {
        return update(c ->
            c.set(configKey).equalToWhenPresent(record::getConfigKey)
            .set(configDesc).equalToWhenPresent(record::getConfigDesc)
            .set(state).equalToWhenPresent(record::getState)
            .set(createTime).equalToWhenPresent(record::getCreateTime)
            .set(updateTime).equalToWhenPresent(record::getUpdateTime)
            .set(configValue).equalToWhenPresent(record::getConfigValue)
            .where(id, isEqualTo(record::getId))
        );
    }
}