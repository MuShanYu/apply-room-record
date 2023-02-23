package com.guet.ARC.mapper;

import com.guet.ARC.domain.User;
import org.apache.ibatis.annotations.*;
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

import javax.annotation.Generated;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.guet.ARC.mapper.UserDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

@Mapper
public interface UserMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-02-23T14:20:41.59+08:00", comments="Source Table: tbl_user")
    BasicColumn[] selectList = BasicColumn.columnList(id, nickname, pwd, stuNum, name, tel, institute, state, updateTime, createTime, mail);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-02-23T14:20:41.581+08:00", comments="Source Table: tbl_user")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    long count(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-02-23T14:20:41.582+08:00", comments="Source Table: tbl_user")
    @DeleteProvider(type=SqlProviderAdapter.class, method="delete")
    int delete(DeleteStatementProvider deleteStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-02-23T14:20:41.582+08:00", comments="Source Table: tbl_user")
    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    int insert(InsertStatementProvider<User> insertStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-02-23T14:20:41.583+08:00", comments="Source Table: tbl_user")
    @InsertProvider(type=SqlProviderAdapter.class, method="insertMultiple")
    int insertMultiple(MultiRowInsertStatementProvider<User> multipleInsertStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-02-23T14:20:41.583+08:00", comments="Source Table: tbl_user")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("UserResult")
    Optional<User> selectOne(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-02-23T14:20:41.584+08:00", comments="Source Table: tbl_user")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="UserResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="nickname", property="nickname", jdbcType=JdbcType.VARCHAR),
        @Result(column="pwd", property="pwd", jdbcType=JdbcType.VARCHAR),
        @Result(column="stu_num", property="stuNum", jdbcType=JdbcType.VARCHAR),
        @Result(column="name", property="name", jdbcType=JdbcType.VARCHAR),
        @Result(column="tel", property="tel", jdbcType=JdbcType.VARCHAR),
        @Result(column="institute", property="institute", jdbcType=JdbcType.VARCHAR),
        @Result(column="state", property="state", jdbcType=JdbcType.SMALLINT),
        @Result(column="update_time", property="updateTime", jdbcType=JdbcType.BIGINT),
        @Result(column="create_time", property="createTime", jdbcType=JdbcType.BIGINT),
        @Result(column="mail", property="mail", jdbcType=JdbcType.VARCHAR)
    })
    List<User> selectMany(SelectStatementProvider selectStatement);

    @Select("select mail from tbl_user " +
            "where id = (select charge_person_id from tbl_room where id = #{roomId})")
    String queryChargeUserMailByRoomId(@Param("roomId") String roomId);

    @Select("select mail from tbl_user where id = #{userId}")
    String queryUserMailById(@Param("userId") String userId);

    @Select("select tel from tbl_user where tel = #{tel} and id != #{userId}")
    String isTelExisted(@Param("tel") String tel, @Param("userId") String userId);

    @Select("select mail from tbl_user where mail = #{mail} and id != #{userId}")
    String isMailExisted(@Param("mail") String mail, @Param("userId") String userId);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-02-23T14:20:41.585+08:00", comments="Source Table: tbl_user")
    @UpdateProvider(type=SqlProviderAdapter.class, method="update")
    int update(UpdateStatementProvider updateStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-02-23T14:20:41.585+08:00", comments="Source Table: tbl_user")
    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, user, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-02-23T14:20:41.586+08:00", comments="Source Table: tbl_user")
    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, user, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-02-23T14:20:41.587+08:00", comments="Source Table: tbl_user")
    default int deleteByPrimaryKey(String id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-02-23T14:20:41.587+08:00", comments="Source Table: tbl_user")
    default int insert(User record) {
        return MyBatis3Utils.insert(this::insert, record, user, c ->
            c.map(id).toProperty("id")
            .map(nickname).toProperty("nickname")
            .map(pwd).toProperty("pwd")
            .map(stuNum).toProperty("stuNum")
            .map(name).toProperty("name")
            .map(tel).toProperty("tel")
            .map(institute).toProperty("institute")
            .map(state).toProperty("state")
            .map(updateTime).toProperty("updateTime")
            .map(createTime).toProperty("createTime")
            .map(mail).toProperty("mail")
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-02-23T14:20:41.588+08:00", comments="Source Table: tbl_user")
    default int insertMultiple(Collection<User> records) {
        return MyBatis3Utils.insertMultiple(this::insertMultiple, records, user, c ->
            c.map(id).toProperty("id")
            .map(nickname).toProperty("nickname")
            .map(pwd).toProperty("pwd")
            .map(stuNum).toProperty("stuNum")
            .map(name).toProperty("name")
            .map(tel).toProperty("tel")
            .map(institute).toProperty("institute")
            .map(state).toProperty("state")
            .map(updateTime).toProperty("updateTime")
            .map(createTime).toProperty("createTime")
            .map(mail).toProperty("mail")
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-02-23T14:20:41.589+08:00", comments="Source Table: tbl_user")
    default int insertSelective(User record) {
        return MyBatis3Utils.insert(this::insert, record, user, c ->
            c.map(id).toPropertyWhenPresent("id", record::getId)
            .map(nickname).toPropertyWhenPresent("nickname", record::getNickname)
            .map(pwd).toPropertyWhenPresent("pwd", record::getPwd)
            .map(stuNum).toPropertyWhenPresent("stuNum", record::getStuNum)
            .map(name).toPropertyWhenPresent("name", record::getName)
            .map(tel).toPropertyWhenPresent("tel", record::getTel)
            .map(institute).toPropertyWhenPresent("institute", record::getInstitute)
            .map(state).toPropertyWhenPresent("state", record::getState)
            .map(updateTime).toPropertyWhenPresent("updateTime", record::getUpdateTime)
            .map(createTime).toPropertyWhenPresent("createTime", record::getCreateTime)
            .map(mail).toPropertyWhenPresent("mail", record::getMail)
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-02-23T14:20:41.591+08:00", comments="Source Table: tbl_user")
    default Optional<User> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, user, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-02-23T14:20:41.591+08:00", comments="Source Table: tbl_user")
    default List<User> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, user, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-02-23T14:20:41.591+08:00", comments="Source Table: tbl_user")
    default List<User> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, user, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-02-23T14:20:41.592+08:00", comments="Source Table: tbl_user")
    default Optional<User> selectByPrimaryKey(String id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-02-23T14:20:41.592+08:00", comments="Source Table: tbl_user")
    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, user, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-02-23T14:20:41.592+08:00", comments="Source Table: tbl_user")
    static UpdateDSL<UpdateModel> updateAllColumns(User record, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(id).equalTo(record::getId)
                .set(nickname).equalTo(record::getNickname)
                .set(pwd).equalTo(record::getPwd)
                .set(stuNum).equalTo(record::getStuNum)
                .set(name).equalTo(record::getName)
                .set(tel).equalTo(record::getTel)
                .set(institute).equalTo(record::getInstitute)
                .set(state).equalTo(record::getState)
                .set(updateTime).equalTo(record::getUpdateTime)
                .set(createTime).equalTo(record::getCreateTime)
                .set(mail).equalTo(record::getMail);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-02-23T14:20:41.593+08:00", comments="Source Table: tbl_user")
    static UpdateDSL<UpdateModel> updateSelectiveColumns(User record, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(id).equalToWhenPresent(record::getId)
                .set(nickname).equalToWhenPresent(record::getNickname)
                .set(pwd).equalToWhenPresent(record::getPwd)
                .set(stuNum).equalToWhenPresent(record::getStuNum)
                .set(name).equalToWhenPresent(record::getName)
                .set(tel).equalToWhenPresent(record::getTel)
                .set(institute).equalToWhenPresent(record::getInstitute)
                .set(state).equalToWhenPresent(record::getState)
                .set(updateTime).equalToWhenPresent(record::getUpdateTime)
                .set(createTime).equalToWhenPresent(record::getCreateTime)
                .set(mail).equalToWhenPresent(record::getMail);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-02-23T14:20:41.594+08:00", comments="Source Table: tbl_user")
    default int updateByPrimaryKey(User record) {
        return update(c ->
            c.set(nickname).equalTo(record::getNickname)
            .set(pwd).equalTo(record::getPwd)
            .set(stuNum).equalTo(record::getStuNum)
            .set(name).equalTo(record::getName)
            .set(tel).equalTo(record::getTel)
            .set(institute).equalTo(record::getInstitute)
            .set(state).equalTo(record::getState)
            .set(updateTime).equalTo(record::getUpdateTime)
            .set(createTime).equalTo(record::getCreateTime)
            .set(mail).equalTo(record::getMail)
            .where(id, isEqualTo(record::getId))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2023-02-23T14:20:41.594+08:00", comments="Source Table: tbl_user")
    default int updateByPrimaryKeySelective(User record) {
        return update(c ->
            c.set(nickname).equalToWhenPresent(record::getNickname)
            .set(pwd).equalToWhenPresent(record::getPwd)
            .set(stuNum).equalToWhenPresent(record::getStuNum)
            .set(name).equalToWhenPresent(record::getName)
            .set(tel).equalToWhenPresent(record::getTel)
            .set(institute).equalToWhenPresent(record::getInstitute)
            .set(state).equalToWhenPresent(record::getState)
            .set(updateTime).equalToWhenPresent(record::getUpdateTime)
            .set(createTime).equalToWhenPresent(record::getCreateTime)
            .set(mail).equalToWhenPresent(record::getMail)
            .where(id, isEqualTo(record::getId))
        );
    }
}