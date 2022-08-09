package com.guet.ARC.domain;

import javax.annotation.Generated;

public class Role {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.262+08:00", comments="Source field: tbl_role.id")
    private String id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.262+08:00", comments="Source field: tbl_role.role_name")
    private String roleName;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.262+08:00", comments="Source field: tbl_role.role_des")
    private String roleDes;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.262+08:00", comments="Source field: tbl_role.state")
    private Short state;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.262+08:00", comments="Source field: tbl_role.create_time")
    private Long createTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.262+08:00", comments="Source field: tbl_role.update_time")
    private Long updateTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.262+08:00", comments="Source field: tbl_role.id")
    public String getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.262+08:00", comments="Source field: tbl_role.id")
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.262+08:00", comments="Source field: tbl_role.role_name")
    public String getRoleName() {
        return roleName;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.262+08:00", comments="Source field: tbl_role.role_name")
    public void setRoleName(String roleName) {
        this.roleName = roleName == null ? null : roleName.trim();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.262+08:00", comments="Source field: tbl_role.role_des")
    public String getRoleDes() {
        return roleDes;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.262+08:00", comments="Source field: tbl_role.role_des")
    public void setRoleDes(String roleDes) {
        this.roleDes = roleDes == null ? null : roleDes.trim();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.262+08:00", comments="Source field: tbl_role.state")
    public Short getState() {
        return state;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.262+08:00", comments="Source field: tbl_role.state")
    public void setState(Short state) {
        this.state = state;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.262+08:00", comments="Source field: tbl_role.create_time")
    public Long getCreateTime() {
        return createTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.262+08:00", comments="Source field: tbl_role.create_time")
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.262+08:00", comments="Source field: tbl_role.update_time")
    public Long getUpdateTime() {
        return updateTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.262+08:00", comments="Source field: tbl_role.update_time")
    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }
}