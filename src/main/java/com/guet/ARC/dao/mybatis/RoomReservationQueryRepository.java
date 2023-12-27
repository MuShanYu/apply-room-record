package com.guet.ARC.dao.mybatis;

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
    BasicColumn[] selectList = BasicColumn.columnList(id, roomUsage, reserveStartTime, reserveEndTime, verifyUserName,
            state, updateTime, createTime, userId, roomId);

    BasicColumn[] roomReservationList = BasicColumn.columnList(id, roomUsage, reserveStartTime, reserveEndTime, verifyUserName,
            state, updateTime, createTime, RoomDynamicSqlSupport.school,  RoomDynamicSqlSupport.teachBuilding,
            RoomDynamicSqlSupport.category,  RoomDynamicSqlSupport.roomName,  RoomDynamicSqlSupport.equipmentInfo,
            RoomDynamicSqlSupport.capacity, roomId, userId, remark);

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
            @Result(column="capacity", property="capacity", jdbcType=JdbcType.VARCHAR),
            @Result(column="remark", property="remark", jdbcType=JdbcType.VARCHAR)
    })
    List<RoomReservationAdminVo> selectRoomReservationsAdminVo(SelectStatementProvider selectStatement);

    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    long count(SelectStatementProvider selectStatement);
}