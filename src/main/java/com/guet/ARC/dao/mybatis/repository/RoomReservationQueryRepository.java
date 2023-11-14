package com.guet.ARC.dao.mybatis.repository;

import com.guet.ARC.dao.mybatis.support.RoomDynamicSqlSupport;
import com.guet.ARC.domain.RoomReservation;
import com.guet.ARC.domain.vo.room.RoomReservationAdminVo;
import com.guet.ARC.domain.vo.room.RoomReservationVo;
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

import static com.guet.ARC.dao.mybatis.support.RoomReservationDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

@Mapper
public interface RoomReservationQueryRepository {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.255+08:00", comments="Source Table: tbl_room_reservation")
    BasicColumn[] selectList = BasicColumn.columnList(id, roomUsage, reserveStartTime, reserveEndTime, verifyUserName, state, updateTime, createTime, userId, roomId);

    BasicColumn[] roomReservationList = BasicColumn.columnList(id, roomUsage, reserveStartTime, reserveEndTime, verifyUserName,
            state, updateTime, createTime, RoomDynamicSqlSupport.school,  RoomDynamicSqlSupport.teachBuilding,
            RoomDynamicSqlSupport.category,  RoomDynamicSqlSupport.roomName,  RoomDynamicSqlSupport.equipmentInfo,
            RoomDynamicSqlSupport.capacity, roomId, userId);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.254+08:00", comments="Source Table: tbl_room_reservation")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    long count(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.254+08:00", comments="Source Table: tbl_room_reservation")
    @DeleteProvider(type=SqlProviderAdapter.class, method="delete")
    int delete(DeleteStatementProvider deleteStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.254+08:00", comments="Source Table: tbl_room_reservation")
    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    int insert(InsertStatementProvider<RoomReservation> insertStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.254+08:00", comments="Source Table: tbl_room_reservation")
    @InsertProvider(type=SqlProviderAdapter.class, method="insertMultiple")
    int insertMultiple(MultiRowInsertStatementProvider<RoomReservation> multipleInsertStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.254+08:00", comments="Source Table: tbl_room_reservation")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("RoomReservationResult")
    Optional<RoomReservation> selectOne(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.254+08:00", comments="Source Table: tbl_room_reservation")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="RoomReservationResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="room_usage", property="roomUsage", jdbcType=JdbcType.VARCHAR),
        @Result(column="reserve_start_time", property="reserveStartTime", jdbcType=JdbcType.BIGINT),
        @Result(column="reserve_end_time", property="reserveEndTime", jdbcType=JdbcType.BIGINT),
        @Result(column="verify_user_name", property="verifyUserName", jdbcType=JdbcType.VARCHAR),
        @Result(column="state", property="state", jdbcType=JdbcType.SMALLINT),
        @Result(column="update_time", property="updateTime", jdbcType=JdbcType.BIGINT),
        @Result(column="create_time", property="createTime", jdbcType=JdbcType.BIGINT),
        @Result(column="user_id", property="userId", jdbcType=JdbcType.VARCHAR),
        @Result(column="room_id", property="roomId", jdbcType=JdbcType.VARCHAR)
    })
    List<RoomReservation> selectMany(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="RoomReservationVoResult", value = {
            @Result(column="id", property="id", jdbcType=JdbcType.VARCHAR, id=true),
            @Result(column="room_usage", property="roomUsage", jdbcType=JdbcType.VARCHAR),
            @Result(column="reserve_start_time", property="reserveStartTime", jdbcType=JdbcType.BIGINT),
            @Result(column="reserve_end_time", property="reserveEndTime", jdbcType=JdbcType.BIGINT),
            @Result(column="verify_user_name", property="verifyUserName", jdbcType=JdbcType.VARCHAR),
            @Result(column="state", property="state", jdbcType=JdbcType.SMALLINT),
            @Result(column="update_time", property="updateTime", jdbcType=JdbcType.BIGINT),
            @Result(column="create_time", property="createTime", jdbcType=JdbcType.BIGINT),
            @Result(column="room_id", property="roomId", jdbcType=JdbcType.VARCHAR),
            @Result(column="school", property="school", jdbcType=JdbcType.VARCHAR),
            @Result(column="teach_building", property="teachBuilding", jdbcType=JdbcType.VARCHAR),
            @Result(column="category", property="category", jdbcType=JdbcType.VARCHAR),
            @Result(column="room_name", property="roomName", jdbcType=JdbcType.VARCHAR),
            @Result(column="equipment_info", property="equipmentInfo", jdbcType=JdbcType.VARCHAR),
            @Result(column="capacity", property="capacity", jdbcType=JdbcType.VARCHAR)
    })
    List<RoomReservationVo> selectRoomReservationsVo(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="RoomReservationAdminVoResult", value = {
            @Result(column="id", property="id", jdbcType=JdbcType.VARCHAR, id=true),
            @Result(column="room_usage", property="roomUsage", jdbcType=JdbcType.VARCHAR),
            @Result(column="reserve_start_time", property="reserveStartTime", jdbcType=JdbcType.BIGINT),
            @Result(column="reserve_end_time", property="reserveEndTime", jdbcType=JdbcType.BIGINT),
            @Result(column="verify_user_name", property="verifyUserName", jdbcType=JdbcType.VARCHAR),
            @Result(column="state", property="state", jdbcType=JdbcType.SMALLINT),
            @Result(column="update_time", property="updateTime", jdbcType=JdbcType.BIGINT),
            @Result(column="create_time", property="createTime", jdbcType=JdbcType.BIGINT),
            @Result(column="room_id", property="roomId", jdbcType=JdbcType.VARCHAR),
            @Result(column="user_id", property="userId", jdbcType=JdbcType.VARCHAR),
            @Result(column="school", property="school", jdbcType=JdbcType.VARCHAR),
            @Result(column="teach_building", property="teachBuilding", jdbcType=JdbcType.VARCHAR),
            @Result(column="category", property="category", jdbcType=JdbcType.VARCHAR),
            @Result(column="room_name", property="roomName", jdbcType=JdbcType.VARCHAR),
            @Result(column="equipment_info", property="equipmentInfo", jdbcType=JdbcType.VARCHAR),
            @Result(column="capacity", property="capacity", jdbcType=JdbcType.VARCHAR)
    })
    List<RoomReservationAdminVo> selectRoomReservationsAdminVo(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.254+08:00", comments="Source Table: tbl_room_reservation")
    @UpdateProvider(type=SqlProviderAdapter.class, method="update")
    int update(UpdateStatementProvider updateStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.254+08:00", comments="Source Table: tbl_room_reservation")
    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, roomReservation, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.254+08:00", comments="Source Table: tbl_room_reservation")
    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, roomReservation, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.254+08:00", comments="Source Table: tbl_room_reservation")
    default int deleteByPrimaryKey(String id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.254+08:00", comments="Source Table: tbl_room_reservation")
    default int insert(RoomReservation record) {
        return MyBatis3Utils.insert(this::insert, record, roomReservation, c ->
            c.map(id).toProperty("id")
            .map(roomUsage).toProperty("roomUsage")
            .map(reserveStartTime).toProperty("reserveStartTime")
            .map(reserveEndTime).toProperty("reserveEndTime")
            .map(verifyUserName).toProperty("verifyUserName")
            .map(state).toProperty("state")
            .map(updateTime).toProperty("updateTime")
            .map(createTime).toProperty("createTime")
            .map(userId).toProperty("userId")
            .map(roomId).toProperty("roomId")
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.254+08:00", comments="Source Table: tbl_room_reservation")
    default int insertMultiple(Collection<RoomReservation> records) {
        return MyBatis3Utils.insertMultiple(this::insertMultiple, records, roomReservation, c ->
            c.map(id).toProperty("id")
            .map(roomUsage).toProperty("roomUsage")
            .map(reserveStartTime).toProperty("reserveStartTime")
            .map(reserveEndTime).toProperty("reserveEndTime")
            .map(verifyUserName).toProperty("verifyUserName")
            .map(state).toProperty("state")
            .map(updateTime).toProperty("updateTime")
            .map(createTime).toProperty("createTime")
            .map(userId).toProperty("userId")
            .map(roomId).toProperty("roomId")
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.254+08:00", comments="Source Table: tbl_room_reservation")
    default int insertSelective(RoomReservation record) {
        return MyBatis3Utils.insert(this::insert, record, roomReservation, c ->
            c.map(id).toPropertyWhenPresent("id", record::getId)
            .map(roomUsage).toPropertyWhenPresent("roomUsage", record::getRoomUsage)
            .map(reserveStartTime).toPropertyWhenPresent("reserveStartTime", record::getReserveStartTime)
            .map(reserveEndTime).toPropertyWhenPresent("reserveEndTime", record::getReserveEndTime)
            .map(verifyUserName).toPropertyWhenPresent("verifyUserName", record::getVerifyUserName)
            .map(state).toPropertyWhenPresent("state", record::getState)
            .map(updateTime).toPropertyWhenPresent("updateTime", record::getUpdateTime)
            .map(createTime).toPropertyWhenPresent("createTime", record::getCreateTime)
            .map(userId).toPropertyWhenPresent("userId", record::getUserId)
            .map(roomId).toPropertyWhenPresent("roomId", record::getRoomId)
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.255+08:00", comments="Source Table: tbl_room_reservation")
    default Optional<RoomReservation> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, roomReservation, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.255+08:00", comments="Source Table: tbl_room_reservation")
    default List<RoomReservation> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, roomReservation, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.255+08:00", comments="Source Table: tbl_room_reservation")
    default List<RoomReservation> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, roomReservation, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.255+08:00", comments="Source Table: tbl_room_reservation")
    default Optional<RoomReservation> selectByPrimaryKey(String id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.255+08:00", comments="Source Table: tbl_room_reservation")
    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, roomReservation, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.255+08:00", comments="Source Table: tbl_room_reservation")
    static UpdateDSL<UpdateModel> updateAllColumns(RoomReservation record, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(id).equalTo(record::getId)
                .set(roomUsage).equalTo(record::getRoomUsage)
                .set(reserveStartTime).equalTo(record::getReserveStartTime)
                .set(reserveEndTime).equalTo(record::getReserveEndTime)
                .set(verifyUserName).equalTo(record::getVerifyUserName)
                .set(state).equalTo(record::getState)
                .set(updateTime).equalTo(record::getUpdateTime)
                .set(createTime).equalTo(record::getCreateTime)
                .set(userId).equalTo(record::getUserId)
                .set(roomId).equalTo(record::getRoomId);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.255+08:00", comments="Source Table: tbl_room_reservation")
    static UpdateDSL<UpdateModel> updateSelectiveColumns(RoomReservation record, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(id).equalToWhenPresent(record::getId)
                .set(roomUsage).equalToWhenPresent(record::getRoomUsage)
                .set(reserveStartTime).equalToWhenPresent(record::getReserveStartTime)
                .set(reserveEndTime).equalToWhenPresent(record::getReserveEndTime)
                .set(verifyUserName).equalToWhenPresent(record::getVerifyUserName)
                .set(state).equalToWhenPresent(record::getState)
                .set(updateTime).equalToWhenPresent(record::getUpdateTime)
                .set(createTime).equalToWhenPresent(record::getCreateTime)
                .set(userId).equalToWhenPresent(record::getUserId)
                .set(roomId).equalToWhenPresent(record::getRoomId);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.255+08:00", comments="Source Table: tbl_room_reservation")
    default int updateByPrimaryKey(RoomReservation record) {
        return update(c ->
            c.set(roomUsage).equalTo(record::getRoomUsage)
            .set(reserveStartTime).equalTo(record::getReserveStartTime)
            .set(reserveEndTime).equalTo(record::getReserveEndTime)
            .set(verifyUserName).equalTo(record::getVerifyUserName)
            .set(state).equalTo(record::getState)
            .set(updateTime).equalTo(record::getUpdateTime)
            .set(createTime).equalTo(record::getCreateTime)
            .set(userId).equalTo(record::getUserId)
            .set(roomId).equalTo(record::getRoomId)
            .where(id, isEqualTo(record::getId))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.255+08:00", comments="Source Table: tbl_room_reservation")
    default int updateByPrimaryKeySelective(RoomReservation record) {
        return update(c ->
            c.set(roomUsage).equalToWhenPresent(record::getRoomUsage)
            .set(reserveStartTime).equalToWhenPresent(record::getReserveStartTime)
            .set(reserveEndTime).equalToWhenPresent(record::getReserveEndTime)
            .set(verifyUserName).equalToWhenPresent(record::getVerifyUserName)
            .set(state).equalToWhenPresent(record::getState)
            .set(updateTime).equalToWhenPresent(record::getUpdateTime)
            .set(createTime).equalToWhenPresent(record::getCreateTime)
            .set(userId).equalToWhenPresent(record::getUserId)
            .set(roomId).equalToWhenPresent(record::getRoomId)
            .where(id, isEqualTo(record::getId))
        );
    }
}