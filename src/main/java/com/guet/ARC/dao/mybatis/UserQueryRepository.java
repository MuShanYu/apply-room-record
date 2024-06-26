package com.guet.ARC.dao.mybatis;

import com.guet.ARC.domain.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.util.SqlProviderAdapter;

import javax.annotation.Generated;
import java.util.List;

import static com.guet.ARC.dao.mybatis.support.UserDynamicSqlSupport.*;

@Mapper
public interface UserQueryRepository {
    BasicColumn[] selectList = BasicColumn.columnList(id, name, pwd, stuNum, institute, state, updateTime, createTime, mail);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-02-23T14:20:41.584+08:00", comments="Source Table: tbl_user")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="UserResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="name", property="name", jdbcType=JdbcType.VARCHAR),
        @Result(column="pwd", property="pwd", jdbcType=JdbcType.VARCHAR),
        @Result(column="stu_num", property="stuNum", jdbcType=JdbcType.VARCHAR),
        @Result(column="institute", property="institute", jdbcType=JdbcType.VARCHAR),
        @Result(column="state", property="state", jdbcType=JdbcType.SMALLINT),
        @Result(column="update_time", property="updateTime", jdbcType=JdbcType.BIGINT),
        @Result(column="create_time", property="createTime", jdbcType=JdbcType.BIGINT),
        @Result(column="mail", property="mail", jdbcType=JdbcType.VARCHAR)
    })
    List<User> selectMany(SelectStatementProvider selectStatement);

}