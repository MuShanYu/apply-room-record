package com.guet.ARC.domain;

import javax.annotation.Generated;

public class RoomReservation {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.252+08:00", comments="Source field: tbl_room_reservation.id")
    private String id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.252+08:00", comments="Source field: tbl_room_reservation.room_usage")
    private String roomUsage;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.252+08:00", comments="Source field: tbl_room_reservation.reserve_start_time")
    private Long reserveStartTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.252+08:00", comments="Source field: tbl_room_reservation.reserve_end_time")
    private Long reserveEndTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.253+08:00", comments="Source field: tbl_room_reservation.verify_user_name")
    private String verifyUserName;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.253+08:00", comments="Source field: tbl_room_reservation.state")
    private Short state;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.253+08:00", comments="Source field: tbl_room_reservation.update_time")
    private Long updateTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.253+08:00", comments="Source field: tbl_room_reservation.create_time")
    private Long createTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.253+08:00", comments="Source field: tbl_room_reservation.user_id")
    private String userId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.253+08:00", comments="Source field: tbl_room_reservation.room_id")
    private String roomId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.252+08:00", comments="Source field: tbl_room_reservation.id")
    public String getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.252+08:00", comments="Source field: tbl_room_reservation.id")
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.252+08:00", comments="Source field: tbl_room_reservation.room_usage")
    public String getRoomUsage() {
        return roomUsage;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.252+08:00", comments="Source field: tbl_room_reservation.room_usage")
    public void setRoomUsage(String roomUsage) {
        this.roomUsage = roomUsage == null ? null : roomUsage.trim();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.252+08:00", comments="Source field: tbl_room_reservation.reserve_start_time")
    public Long getReserveStartTime() {
        return reserveStartTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.252+08:00", comments="Source field: tbl_room_reservation.reserve_start_time")
    public void setReserveStartTime(Long reserveStartTime) {
        this.reserveStartTime = reserveStartTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.253+08:00", comments="Source field: tbl_room_reservation.reserve_end_time")
    public Long getReserveEndTime() {
        return reserveEndTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.253+08:00", comments="Source field: tbl_room_reservation.reserve_end_time")
    public void setReserveEndTime(Long reserveEndTime) {
        this.reserveEndTime = reserveEndTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.253+08:00", comments="Source field: tbl_room_reservation.verify_user_name")
    public String getVerifyUserName() {
        return verifyUserName;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.253+08:00", comments="Source field: tbl_room_reservation.verify_user_name")
    public void setVerifyUserName(String verifyUserName) {
        this.verifyUserName = verifyUserName == null ? null : verifyUserName.trim();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.253+08:00", comments="Source field: tbl_room_reservation.state")
    public Short getState() {
        return state;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.253+08:00", comments="Source field: tbl_room_reservation.state")
    public void setState(Short state) {
        this.state = state;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.253+08:00", comments="Source field: tbl_room_reservation.update_time")
    public Long getUpdateTime() {
        return updateTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.253+08:00", comments="Source field: tbl_room_reservation.update_time")
    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.253+08:00", comments="Source field: tbl_room_reservation.create_time")
    public Long getCreateTime() {
        return createTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.253+08:00", comments="Source field: tbl_room_reservation.create_time")
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.253+08:00", comments="Source field: tbl_room_reservation.user_id")
    public String getUserId() {
        return userId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.253+08:00", comments="Source field: tbl_room_reservation.user_id")
    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.253+08:00", comments="Source field: tbl_room_reservation.room_id")
    public String getRoomId() {
        return roomId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.253+08:00", comments="Source field: tbl_room_reservation.room_id")
    public void setRoomId(String roomId) {
        this.roomId = roomId == null ? null : roomId.trim();
    }
}