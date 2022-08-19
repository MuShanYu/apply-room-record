package com.guet.ARC.mapper;

import com.guet.ARC.domain.AccessRecord;
import com.guet.ARC.domain.vo.record.UserAccessRecordCountVo;
import com.guet.ARC.domain.vo.record.UserAccessRecordVo;
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

import static com.guet.ARC.mapper.AccessRecordDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

@Mapper
public interface AccessRecordMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.258+08:00", comments="Source Table: tbl_access_record")
    BasicColumn[] selectList = BasicColumn.columnList(id, entryTime, outTime, state, updateTime, createTime, userId, roomId);

    BasicColumn[] selectVoList = BasicColumn.columnList(id, entryTime, outTime, state, updateTime, createTime,
            userId, roomId, RoomDynamicSqlSupport.school,  RoomDynamicSqlSupport.teachBuilding,
            RoomDynamicSqlSupport.category,  RoomDynamicSqlSupport.roomName);

    BasicColumn[] selectCountVoList = BasicColumn.columnList(roomId, RoomDynamicSqlSupport.school,
            RoomDynamicSqlSupport.teachBuilding, RoomDynamicSqlSupport.category,  RoomDynamicSqlSupport.roomName);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.257+08:00", comments="Source Table: tbl_access_record")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    long count(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.257+08:00", comments="Source Table: tbl_access_record")
    @DeleteProvider(type=SqlProviderAdapter.class, method="delete")
    int delete(DeleteStatementProvider deleteStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.257+08:00", comments="Source Table: tbl_access_record")
    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    int insert(InsertStatementProvider<AccessRecord> insertStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.257+08:00", comments="Source Table: tbl_access_record")
    @InsertProvider(type=SqlProviderAdapter.class, method="insertMultiple")
    int insertMultiple(MultiRowInsertStatementProvider<AccessRecord> multipleInsertStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.257+08:00", comments="Source Table: tbl_access_record")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("AccessRecordResult")
    Optional<AccessRecord> selectOne(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.257+08:00", comments="Source Table: tbl_access_record")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="AccessRecordResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="entry_time", property="entryTime", jdbcType=JdbcType.BIGINT),
        @Result(column="out_time", property="outTime", jdbcType=JdbcType.BIGINT),
        @Result(column="state", property="state", jdbcType=JdbcType.SMALLINT),
        @Result(column="update_time", property="updateTime", jdbcType=JdbcType.BIGINT),
        @Result(column="create_time", property="createTime", jdbcType=JdbcType.BIGINT),
        @Result(column="user_id", property="userId", jdbcType=JdbcType.VARCHAR),
        @Result(column="room_id", property="roomId", jdbcType=JdbcType.VARCHAR)
    })
    List<AccessRecord> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="AccessRecordResultVo", value = {
            @Result(column="id", property="id", jdbcType=JdbcType.VARCHAR, id=true),
            @Result(column="entry_time", property="entryTime", jdbcType=JdbcType.BIGINT),
            @Result(column="out_time", property="outTime", jdbcType=JdbcType.BIGINT),
            @Result(column="state", property="state", jdbcType=JdbcType.SMALLINT),
            @Result(column="update_time", property="updateTime", jdbcType=JdbcType.BIGINT),
            @Result(column="create_time", property="createTime", jdbcType=JdbcType.BIGINT),
            @Result(column="user_id", property="userId", jdbcType=JdbcType.VARCHAR),
            @Result(column="room_id", property="roomId", jdbcType=JdbcType.VARCHAR),
            @Result(column="school", property="school", jdbcType=JdbcType.VARCHAR),
            @Result(column="teach_building", property="teachBuilding", jdbcType=JdbcType.VARCHAR),
            @Result(column="category", property="category", jdbcType=JdbcType.VARCHAR),
            @Result(column="room_name", property="roomName", jdbcType=JdbcType.VARCHAR)
    })
    List<UserAccessRecordVo> selectVo(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="AccessRecordResultCountVo", value = {
            @Result(column="room_id", property="roomId", jdbcType=JdbcType.VARCHAR),
            @Result(column="school", property="school", jdbcType=JdbcType.VARCHAR),
            @Result(column="teach_building", property="teachBuilding", jdbcType=JdbcType.VARCHAR),
            @Result(column="category", property="category", jdbcType=JdbcType.VARCHAR),
            @Result(column="room_name", property="roomName", jdbcType=JdbcType.VARCHAR)
    })
    List<UserAccessRecordCountVo> selectCountVo(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.257+08:00", comments="Source Table: tbl_access_record")
    @UpdateProvider(type=SqlProviderAdapter.class, method="update")
    int update(UpdateStatementProvider updateStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.257+08:00", comments="Source Table: tbl_access_record")
    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, accessRecord, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.257+08:00", comments="Source Table: tbl_access_record")
    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, accessRecord, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.257+08:00", comments="Source Table: tbl_access_record")
    default int deleteByPrimaryKey(String id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.257+08:00", comments="Source Table: tbl_access_record")
    default int insert(AccessRecord record) {
        return MyBatis3Utils.insert(this::insert, record, accessRecord, c ->
            c.map(id).toProperty("id")
            .map(entryTime).toProperty("entryTime")
            .map(outTime).toProperty("outTime")
            .map(state).toProperty("state")
            .map(updateTime).toProperty("updateTime")
            .map(createTime).toProperty("createTime")
            .map(userId).toProperty("userId")
            .map(roomId).toProperty("roomId")
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.258+08:00", comments="Source Table: tbl_access_record")
    default int insertMultiple(Collection<AccessRecord> records) {
        return MyBatis3Utils.insertMultiple(this::insertMultiple, records, accessRecord, c ->
            c.map(id).toProperty("id")
            .map(entryTime).toProperty("entryTime")
            .map(outTime).toProperty("outTime")
            .map(state).toProperty("state")
            .map(updateTime).toProperty("updateTime")
            .map(createTime).toProperty("createTime")
            .map(userId).toProperty("userId")
            .map(roomId).toProperty("roomId")
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.258+08:00", comments="Source Table: tbl_access_record")
    default int insertSelective(AccessRecord record) {
        return MyBatis3Utils.insert(this::insert, record, accessRecord, c ->
            c.map(id).toPropertyWhenPresent("id", record::getId)
            .map(entryTime).toPropertyWhenPresent("entryTime", record::getEntryTime)
            .map(outTime).toPropertyWhenPresent("outTime", record::getOutTime)
            .map(state).toPropertyWhenPresent("state", record::getState)
            .map(updateTime).toPropertyWhenPresent("updateTime", record::getUpdateTime)
            .map(createTime).toPropertyWhenPresent("createTime", record::getCreateTime)
            .map(userId).toPropertyWhenPresent("userId", record::getUserId)
            .map(roomId).toPropertyWhenPresent("roomId", record::getRoomId)
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.258+08:00", comments="Source Table: tbl_access_record")
    default Optional<AccessRecord> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, accessRecord, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.258+08:00", comments="Source Table: tbl_access_record")
    default List<AccessRecord> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, accessRecord, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.258+08:00", comments="Source Table: tbl_access_record")
    default List<AccessRecord> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, accessRecord, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.258+08:00", comments="Source Table: tbl_access_record")
    default Optional<AccessRecord> selectByPrimaryKey(String id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.258+08:00", comments="Source Table: tbl_access_record")
    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, accessRecord, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.258+08:00", comments="Source Table: tbl_access_record")
    static UpdateDSL<UpdateModel> updateAllColumns(AccessRecord record, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(id).equalTo(record::getId)
                .set(entryTime).equalTo(record::getEntryTime)
                .set(outTime).equalTo(record::getOutTime)
                .set(state).equalTo(record::getState)
                .set(updateTime).equalTo(record::getUpdateTime)
                .set(createTime).equalTo(record::getCreateTime)
                .set(userId).equalTo(record::getUserId)
                .set(roomId).equalTo(record::getRoomId);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.258+08:00", comments="Source Table: tbl_access_record")
    static UpdateDSL<UpdateModel> updateSelectiveColumns(AccessRecord record, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(id).equalToWhenPresent(record::getId)
                .set(entryTime).equalToWhenPresent(record::getEntryTime)
                .set(outTime).equalToWhenPresent(record::getOutTime)
                .set(state).equalToWhenPresent(record::getState)
                .set(updateTime).equalToWhenPresent(record::getUpdateTime)
                .set(createTime).equalToWhenPresent(record::getCreateTime)
                .set(userId).equalToWhenPresent(record::getUserId)
                .set(roomId).equalToWhenPresent(record::getRoomId);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.258+08:00", comments="Source Table: tbl_access_record")
    default int updateByPrimaryKey(AccessRecord record) {
        return update(c ->
            c.set(entryTime).equalTo(record::getEntryTime)
            .set(outTime).equalTo(record::getOutTime)
            .set(state).equalTo(record::getState)
            .set(updateTime).equalTo(record::getUpdateTime)
            .set(createTime).equalTo(record::getCreateTime)
            .set(userId).equalTo(record::getUserId)
            .set(roomId).equalTo(record::getRoomId)
            .where(id, isEqualTo(record::getId))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.258+08:00", comments="Source Table: tbl_access_record")
    default int updateByPrimaryKeySelective(AccessRecord record) {
        return update(c ->
            c.set(entryTime).equalToWhenPresent(record::getEntryTime)
            .set(outTime).equalToWhenPresent(record::getOutTime)
            .set(state).equalToWhenPresent(record::getState)
            .set(updateTime).equalToWhenPresent(record::getUpdateTime)
            .set(createTime).equalToWhenPresent(record::getCreateTime)
            .set(userId).equalToWhenPresent(record::getUserId)
            .set(roomId).equalToWhenPresent(record::getRoomId)
            .where(id, isEqualTo(record::getId))
        );
    }
}