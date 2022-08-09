package com.guet.ARC.mapper;

import static com.guet.ARC.mapper.RoomDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

import com.guet.ARC.domain.Room;
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
public interface RoomMapper {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.246+08:00", comments="Source Table: tbl_room")
    BasicColumn[] selectList = BasicColumn.columnList(id, school, teachBuilding, category, roomName, equipmentInfo, capacity, state, updateTime, createTime);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.237+08:00", comments="Source Table: tbl_room")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    long count(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.238+08:00", comments="Source Table: tbl_room")
    @DeleteProvider(type=SqlProviderAdapter.class, method="delete")
    int delete(DeleteStatementProvider deleteStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.239+08:00", comments="Source Table: tbl_room")
    @InsertProvider(type=SqlProviderAdapter.class, method="insert")
    int insert(InsertStatementProvider<Room> insertStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.24+08:00", comments="Source Table: tbl_room")
    @InsertProvider(type=SqlProviderAdapter.class, method="insertMultiple")
    int insertMultiple(MultiRowInsertStatementProvider<Room> multipleInsertStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.24+08:00", comments="Source Table: tbl_room")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @ResultMap("RoomResult")
    Optional<Room> selectOne(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.241+08:00", comments="Source Table: tbl_room")
    @SelectProvider(type=SqlProviderAdapter.class, method="select")
    @Results(id="RoomResult", value = {
        @Result(column="id", property="id", jdbcType=JdbcType.VARCHAR, id=true),
        @Result(column="school", property="school", jdbcType=JdbcType.VARCHAR),
        @Result(column="teach_building", property="teachBuilding", jdbcType=JdbcType.VARCHAR),
        @Result(column="category", property="category", jdbcType=JdbcType.VARCHAR),
        @Result(column="room_name", property="roomName", jdbcType=JdbcType.VARCHAR),
        @Result(column="equipment_info", property="equipmentInfo", jdbcType=JdbcType.VARCHAR),
        @Result(column="capacity", property="capacity", jdbcType=JdbcType.VARCHAR),
        @Result(column="state", property="state", jdbcType=JdbcType.SMALLINT),
        @Result(column="update_time", property="updateTime", jdbcType=JdbcType.BIGINT),
        @Result(column="create_time", property="createTime", jdbcType=JdbcType.BIGINT)
    })
    List<Room> selectMany(SelectStatementProvider selectStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.242+08:00", comments="Source Table: tbl_room")
    @UpdateProvider(type=SqlProviderAdapter.class, method="update")
    int update(UpdateStatementProvider updateStatement);

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.242+08:00", comments="Source Table: tbl_room")
    default long count(CountDSLCompleter completer) {
        return MyBatis3Utils.countFrom(this::count, room, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.242+08:00", comments="Source Table: tbl_room")
    default int delete(DeleteDSLCompleter completer) {
        return MyBatis3Utils.deleteFrom(this::delete, room, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.243+08:00", comments="Source Table: tbl_room")
    default int deleteByPrimaryKey(String id_) {
        return delete(c -> 
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.243+08:00", comments="Source Table: tbl_room")
    default int insert(Room record) {
        return MyBatis3Utils.insert(this::insert, record, room, c ->
            c.map(id).toProperty("id")
            .map(school).toProperty("school")
            .map(teachBuilding).toProperty("teachBuilding")
            .map(category).toProperty("category")
            .map(roomName).toProperty("roomName")
            .map(equipmentInfo).toProperty("equipmentInfo")
            .map(capacity).toProperty("capacity")
            .map(state).toProperty("state")
            .map(updateTime).toProperty("updateTime")
            .map(createTime).toProperty("createTime")
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.245+08:00", comments="Source Table: tbl_room")
    default int insertMultiple(Collection<Room> records) {
        return MyBatis3Utils.insertMultiple(this::insertMultiple, records, room, c ->
            c.map(id).toProperty("id")
            .map(school).toProperty("school")
            .map(teachBuilding).toProperty("teachBuilding")
            .map(category).toProperty("category")
            .map(roomName).toProperty("roomName")
            .map(equipmentInfo).toProperty("equipmentInfo")
            .map(capacity).toProperty("capacity")
            .map(state).toProperty("state")
            .map(updateTime).toProperty("updateTime")
            .map(createTime).toProperty("createTime")
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.246+08:00", comments="Source Table: tbl_room")
    default int insertSelective(Room record) {
        return MyBatis3Utils.insert(this::insert, record, room, c ->
            c.map(id).toPropertyWhenPresent("id", record::getId)
            .map(school).toPropertyWhenPresent("school", record::getSchool)
            .map(teachBuilding).toPropertyWhenPresent("teachBuilding", record::getTeachBuilding)
            .map(category).toPropertyWhenPresent("category", record::getCategory)
            .map(roomName).toPropertyWhenPresent("roomName", record::getRoomName)
            .map(equipmentInfo).toPropertyWhenPresent("equipmentInfo", record::getEquipmentInfo)
            .map(capacity).toPropertyWhenPresent("capacity", record::getCapacity)
            .map(state).toPropertyWhenPresent("state", record::getState)
            .map(updateTime).toPropertyWhenPresent("updateTime", record::getUpdateTime)
            .map(createTime).toPropertyWhenPresent("createTime", record::getCreateTime)
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.247+08:00", comments="Source Table: tbl_room")
    default Optional<Room> selectOne(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectOne(this::selectOne, selectList, room, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.247+08:00", comments="Source Table: tbl_room")
    default List<Room> select(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectList(this::selectMany, selectList, room, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.248+08:00", comments="Source Table: tbl_room")
    default List<Room> selectDistinct(SelectDSLCompleter completer) {
        return MyBatis3Utils.selectDistinct(this::selectMany, selectList, room, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.248+08:00", comments="Source Table: tbl_room")
    default Optional<Room> selectByPrimaryKey(String id_) {
        return selectOne(c ->
            c.where(id, isEqualTo(id_))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.248+08:00", comments="Source Table: tbl_room")
    default int update(UpdateDSLCompleter completer) {
        return MyBatis3Utils.update(this::update, room, completer);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.249+08:00", comments="Source Table: tbl_room")
    static UpdateDSL<UpdateModel> updateAllColumns(Room record, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(id).equalTo(record::getId)
                .set(school).equalTo(record::getSchool)
                .set(teachBuilding).equalTo(record::getTeachBuilding)
                .set(category).equalTo(record::getCategory)
                .set(roomName).equalTo(record::getRoomName)
                .set(equipmentInfo).equalTo(record::getEquipmentInfo)
                .set(capacity).equalTo(record::getCapacity)
                .set(state).equalTo(record::getState)
                .set(updateTime).equalTo(record::getUpdateTime)
                .set(createTime).equalTo(record::getCreateTime);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.249+08:00", comments="Source Table: tbl_room")
    static UpdateDSL<UpdateModel> updateSelectiveColumns(Room record, UpdateDSL<UpdateModel> dsl) {
        return dsl.set(id).equalToWhenPresent(record::getId)
                .set(school).equalToWhenPresent(record::getSchool)
                .set(teachBuilding).equalToWhenPresent(record::getTeachBuilding)
                .set(category).equalToWhenPresent(record::getCategory)
                .set(roomName).equalToWhenPresent(record::getRoomName)
                .set(equipmentInfo).equalToWhenPresent(record::getEquipmentInfo)
                .set(capacity).equalToWhenPresent(record::getCapacity)
                .set(state).equalToWhenPresent(record::getState)
                .set(updateTime).equalToWhenPresent(record::getUpdateTime)
                .set(createTime).equalToWhenPresent(record::getCreateTime);
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.249+08:00", comments="Source Table: tbl_room")
    default int updateByPrimaryKey(Room record) {
        return update(c ->
            c.set(school).equalTo(record::getSchool)
            .set(teachBuilding).equalTo(record::getTeachBuilding)
            .set(category).equalTo(record::getCategory)
            .set(roomName).equalTo(record::getRoomName)
            .set(equipmentInfo).equalTo(record::getEquipmentInfo)
            .set(capacity).equalTo(record::getCapacity)
            .set(state).equalTo(record::getState)
            .set(updateTime).equalTo(record::getUpdateTime)
            .set(createTime).equalTo(record::getCreateTime)
            .where(id, isEqualTo(record::getId))
        );
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.25+08:00", comments="Source Table: tbl_room")
    default int updateByPrimaryKeySelective(Room record) {
        return update(c ->
            c.set(school).equalToWhenPresent(record::getSchool)
            .set(teachBuilding).equalToWhenPresent(record::getTeachBuilding)
            .set(category).equalToWhenPresent(record::getCategory)
            .set(roomName).equalToWhenPresent(record::getRoomName)
            .set(equipmentInfo).equalToWhenPresent(record::getEquipmentInfo)
            .set(capacity).equalToWhenPresent(record::getCapacity)
            .set(state).equalToWhenPresent(record::getState)
            .set(updateTime).equalToWhenPresent(record::getUpdateTime)
            .set(createTime).equalToWhenPresent(record::getCreateTime)
            .where(id, isEqualTo(record::getId))
        );
    }
}