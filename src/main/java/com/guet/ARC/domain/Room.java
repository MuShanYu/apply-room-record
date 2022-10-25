package com.guet.ARC.domain;

import javax.annotation.Generated;

public class Room {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-10-24T13:45:23.504+08:00", comments="Source field: tbl_room.id")
    private String id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-10-24T13:45:23.506+08:00", comments="Source field: tbl_room.school")
    private String school;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-10-24T13:45:23.506+08:00", comments="Source field: tbl_room.teach_building")
    private String teachBuilding;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-10-24T13:45:23.506+08:00", comments="Source field: tbl_room.category")
    private String category;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-10-24T13:45:23.506+08:00", comments="Source field: tbl_room.room_name")
    private String roomName;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-10-24T13:45:23.507+08:00", comments="Source field: tbl_room.equipment_info")
    private String equipmentInfo;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-10-24T13:45:23.507+08:00", comments="Source field: tbl_room.capacity")
    private String capacity;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-10-24T13:45:23.507+08:00", comments="Source field: tbl_room.state")
    private Short state;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-10-24T13:45:23.507+08:00", comments="Source field: tbl_room.update_time")
    private Long updateTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-10-24T13:45:23.507+08:00", comments="Source field: tbl_room.create_time")
    private Long createTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-10-24T13:45:23.507+08:00", comments="Source field: tbl_room.charge_person")
    private String chargePerson;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-10-24T13:45:23.508+08:00", comments="Source field: tbl_room.charge_person_id")
    private String chargePersonId;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-10-24T13:45:23.506+08:00", comments="Source field: tbl_room.id")
    public String getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-10-24T13:45:23.506+08:00", comments="Source field: tbl_room.id")
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-10-24T13:45:23.506+08:00", comments="Source field: tbl_room.school")
    public String getSchool() {
        return school;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-10-24T13:45:23.506+08:00", comments="Source field: tbl_room.school")
    public void setSchool(String school) {
        this.school = school == null ? null : school.trim();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-10-24T13:45:23.506+08:00", comments="Source field: tbl_room.teach_building")
    public String getTeachBuilding() {
        return teachBuilding;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-10-24T13:45:23.506+08:00", comments="Source field: tbl_room.teach_building")
    public void setTeachBuilding(String teachBuilding) {
        this.teachBuilding = teachBuilding == null ? null : teachBuilding.trim();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-10-24T13:45:23.506+08:00", comments="Source field: tbl_room.category")
    public String getCategory() {
        return category;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-10-24T13:45:23.506+08:00", comments="Source field: tbl_room.category")
    public void setCategory(String category) {
        this.category = category == null ? null : category.trim();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-10-24T13:45:23.507+08:00", comments="Source field: tbl_room.room_name")
    public String getRoomName() {
        return roomName;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-10-24T13:45:23.507+08:00", comments="Source field: tbl_room.room_name")
    public void setRoomName(String roomName) {
        this.roomName = roomName == null ? null : roomName.trim();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-10-24T13:45:23.507+08:00", comments="Source field: tbl_room.equipment_info")
    public String getEquipmentInfo() {
        return equipmentInfo;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-10-24T13:45:23.507+08:00", comments="Source field: tbl_room.equipment_info")
    public void setEquipmentInfo(String equipmentInfo) {
        this.equipmentInfo = equipmentInfo == null ? null : equipmentInfo.trim();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-10-24T13:45:23.507+08:00", comments="Source field: tbl_room.capacity")
    public String getCapacity() {
        return capacity;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-10-24T13:45:23.507+08:00", comments="Source field: tbl_room.capacity")
    public void setCapacity(String capacity) {
        this.capacity = capacity == null ? null : capacity.trim();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-10-24T13:45:23.507+08:00", comments="Source field: tbl_room.state")
    public Short getState() {
        return state;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-10-24T13:45:23.507+08:00", comments="Source field: tbl_room.state")
    public void setState(Short state) {
        this.state = state;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-10-24T13:45:23.507+08:00", comments="Source field: tbl_room.update_time")
    public Long getUpdateTime() {
        return updateTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-10-24T13:45:23.507+08:00", comments="Source field: tbl_room.update_time")
    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-10-24T13:45:23.507+08:00", comments="Source field: tbl_room.create_time")
    public Long getCreateTime() {
        return createTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-10-24T13:45:23.507+08:00", comments="Source field: tbl_room.create_time")
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-10-24T13:45:23.507+08:00", comments="Source field: tbl_room.charge_person")
    public String getChargePerson() {
        return chargePerson;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-10-24T13:45:23.508+08:00", comments="Source field: tbl_room.charge_person")
    public void setChargePerson(String chargePerson) {
        this.chargePerson = chargePerson == null ? null : chargePerson.trim();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-10-24T13:45:23.508+08:00", comments="Source field: tbl_room.charge_person_id")
    public String getChargePersonId() {
        return chargePersonId;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-10-24T13:45:23.508+08:00", comments="Source field: tbl_room.charge_person_id")
    public void setChargePersonId(String chargePersonId) {
        this.chargePersonId = chargePersonId == null ? null : chargePersonId.trim();
    }
}