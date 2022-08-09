package com.guet.ARC.domain;

import javax.annotation.Generated;

public class AccessRecord {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.256+08:00", comments="Source field: tbl_access_record.id")
    private String id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.256+08:00", comments="Source field: tbl_access_record.entry_time")
    private Long entryTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.256+08:00", comments="Source field: tbl_access_record.out_time")
    private Long outTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.256+08:00", comments="Source field: tbl_access_record.state")
    private Short state;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.256+08:00", comments="Source field: tbl_access_record.update_time")
    private Long updateTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.256+08:00", comments="Source field: tbl_access_record.create_time")
    private Long createTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.256+08:00", comments="Source field: tbl_access_record.user_id")
    private String userId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.256+08:00", comments="Source field: tbl_access_record.room_id")
    private String roomId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.256+08:00", comments="Source field: tbl_access_record.id")
    public String getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.256+08:00", comments="Source field: tbl_access_record.id")
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.256+08:00", comments="Source field: tbl_access_record.entry_time")
    public Long getEntryTime() {
        return entryTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.256+08:00", comments="Source field: tbl_access_record.entry_time")
    public void setEntryTime(Long entryTime) {
        this.entryTime = entryTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.256+08:00", comments="Source field: tbl_access_record.out_time")
    public Long getOutTime() {
        return outTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.256+08:00", comments="Source field: tbl_access_record.out_time")
    public void setOutTime(Long outTime) {
        this.outTime = outTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.256+08:00", comments="Source field: tbl_access_record.state")
    public Short getState() {
        return state;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.256+08:00", comments="Source field: tbl_access_record.state")
    public void setState(Short state) {
        this.state = state;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.256+08:00", comments="Source field: tbl_access_record.update_time")
    public Long getUpdateTime() {
        return updateTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.256+08:00", comments="Source field: tbl_access_record.update_time")
    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.256+08:00", comments="Source field: tbl_access_record.create_time")
    public Long getCreateTime() {
        return createTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.256+08:00", comments="Source field: tbl_access_record.create_time")
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.256+08:00", comments="Source field: tbl_access_record.user_id")
    public String getUserId() {
        return userId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.256+08:00", comments="Source field: tbl_access_record.user_id")
    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.256+08:00", comments="Source field: tbl_access_record.room_id")
    public String getRoomId() {
        return roomId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.256+08:00", comments="Source field: tbl_access_record.room_id")
    public void setRoomId(String roomId) {
        this.roomId = roomId == null ? null : roomId.trim();
    }
}