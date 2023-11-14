package com.guet.ARC.dao.mybatis;

import static com.guet.ARC.dao.mybatis.support.ApplicationDynamicSqlSupport.*;

import com.guet.ARC.domain.Application;
import java.util.List;
import javax.annotation.Generated;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;

@Mapper
public interface ApplicationQueryRepository {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0702123+08:00", comments="Source Table: tbl_application")
    BasicColumn[] selectList = BasicColumn.columnList(id, title, reason, applicationType, matterRecordId, handleUserId, applyUserId, state, remarks, createTime, updateTime);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-11-14T09:19:41.0702123+08:00", comments="Source Table: tbl_application")
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

}