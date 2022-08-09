package com.guet.ARC.mapper;

import java.sql.JDBCType;
import javax.annotation.Generated;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class RoomDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.235+08:00", comments="Source Table: tbl_room")
    public static final Room room = new Room();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.235+08:00", comments="Source field: tbl_room.id")
    public static final SqlColumn<String> id = room.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.236+08:00", comments="Source field: tbl_room.school")
    public static final SqlColumn<String> school = room.school;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.236+08:00", comments="Source field: tbl_room.teach_building")
    public static final SqlColumn<String> teachBuilding = room.teachBuilding;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.236+08:00", comments="Source field: tbl_room.category")
    public static final SqlColumn<String> category = room.category;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.236+08:00", comments="Source field: tbl_room.room_name")
    public static final SqlColumn<String> roomName = room.roomName;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.236+08:00", comments="Source field: tbl_room.equipment_info")
    public static final SqlColumn<String> equipmentInfo = room.equipmentInfo;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.236+08:00", comments="Source field: tbl_room.capacity")
    public static final SqlColumn<String> capacity = room.capacity;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.236+08:00", comments="Source field: tbl_room.state")
    public static final SqlColumn<Short> state = room.state;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.236+08:00", comments="Source field: tbl_room.update_time")
    public static final SqlColumn<Long> updateTime = room.updateTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.236+08:00", comments="Source field: tbl_room.create_time")
    public static final SqlColumn<Long> createTime = room.createTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.235+08:00", comments="Source Table: tbl_room")
    public static final class Room extends SqlTable {
        public final SqlColumn<String> id = column("id", JDBCType.VARCHAR);

        public final SqlColumn<String> school = column("school", JDBCType.VARCHAR);

        public final SqlColumn<String> teachBuilding = column("teach_building", JDBCType.VARCHAR);

        public final SqlColumn<String> category = column("category", JDBCType.VARCHAR);

        public final SqlColumn<String> roomName = column("room_name", JDBCType.VARCHAR);

        public final SqlColumn<String> equipmentInfo = column("equipment_info", JDBCType.VARCHAR);

        public final SqlColumn<String> capacity = column("capacity", JDBCType.VARCHAR);

        public final SqlColumn<Short> state = column("state", JDBCType.SMALLINT);

        public final SqlColumn<Long> updateTime = column("update_time", JDBCType.BIGINT);

        public final SqlColumn<Long> createTime = column("create_time", JDBCType.BIGINT);

        public Room() {
            super("tbl_room");
        }
    }
}