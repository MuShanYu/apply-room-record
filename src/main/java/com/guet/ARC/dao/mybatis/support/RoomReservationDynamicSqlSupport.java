package com.guet.ARC.dao.mybatis.support;

import java.sql.JDBCType;
import javax.annotation.Generated;

import com.guet.ARC.domain.enums.ReservationState;
import com.guet.ARC.domain.enums.State;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

public final class RoomReservationDynamicSqlSupport {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.253+08:00", comments="Source Table: tbl_room_reservation")
    public static final RoomReservation roomReservation = new RoomReservation();

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.253+08:00", comments="Source field: tbl_room_reservation.id")
    public static final SqlColumn<String> id = roomReservation.id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.254+08:00", comments="Source field: tbl_room_reservation.room_usage")
    public static final SqlColumn<String> roomUsage = roomReservation.roomUsage;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.254+08:00", comments="Source field: tbl_room_reservation.reserve_start_time")
    public static final SqlColumn<Long> reserveStartTime = roomReservation.reserveStartTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.254+08:00", comments="Source field: tbl_room_reservation.reserve_end_time")
    public static final SqlColumn<Long> reserveEndTime = roomReservation.reserveEndTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.254+08:00", comments="Source field: tbl_room_reservation.verify_user_name")
    public static final SqlColumn<String> verifyUserName = roomReservation.verifyUserName;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.254+08:00", comments="Source field: tbl_room_reservation.state")
    public static final SqlColumn<ReservationState> state = roomReservation.state;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.254+08:00", comments="Source field: tbl_room_reservation.update_time")
    public static final SqlColumn<Long> updateTime = roomReservation.updateTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.254+08:00", comments="Source field: tbl_room_reservation.create_time")
    public static final SqlColumn<Long> createTime = roomReservation.createTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.254+08:00", comments="Source field: tbl_room_reservation.user_id")
    public static final SqlColumn<String> userId = roomReservation.userId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.254+08:00", comments="Source field: tbl_room_reservation.room_id")
    public static final SqlColumn<String> roomId = roomReservation.roomId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.253+08:00", comments="Source Table: tbl_room_reservation")
    public static final class RoomReservation extends SqlTable {
        public final SqlColumn<String> id = column("id", JDBCType.VARCHAR);

        public final SqlColumn<String> roomUsage = column("room_usage", JDBCType.VARCHAR);

        public final SqlColumn<Long> reserveStartTime = column("reserve_start_time", JDBCType.BIGINT);

        public final SqlColumn<Long> reserveEndTime = column("reserve_end_time", JDBCType.BIGINT);

        public final SqlColumn<String> verifyUserName = column("verify_user_name", JDBCType.VARCHAR);

        public final SqlColumn<ReservationState> state = column("state", JDBCType.SMALLINT);

        public final SqlColumn<Long> updateTime = column("update_time", JDBCType.BIGINT);

        public final SqlColumn<Long> createTime = column("create_time", JDBCType.BIGINT);

        public final SqlColumn<String> userId = column("user_id", JDBCType.VARCHAR);

        public final SqlColumn<String> roomId = column("room_id", JDBCType.VARCHAR);

        public RoomReservation() {
            super("tbl_room_reservation");
        }
    }
}