package com.guet.ARC.domain;

import javax.annotation.Generated;

public class SysConfig {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.538+08:00", comments="Source field: tbl_sys_config.id")
    private String id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.541+08:00", comments="Source field: tbl_sys_config.config_key")
    private String configKey;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.541+08:00", comments="Source field: tbl_sys_config.config_desc")
    private String configDesc;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.541+08:00", comments="Source field: tbl_sys_config.state")
    private Short state;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.541+08:00", comments="Source field: tbl_sys_config.create_time")
    private Long createTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.541+08:00", comments="Source field: tbl_sys_config.update_time")
    private Long updateTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.541+08:00", comments="Source field: tbl_sys_config.config_value")
    private String configValue;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.54+08:00", comments="Source field: tbl_sys_config.id")
    public String getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.541+08:00", comments="Source field: tbl_sys_config.id")
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.541+08:00", comments="Source field: tbl_sys_config.config_key")
    public String getConfigKey() {
        return configKey;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.541+08:00", comments="Source field: tbl_sys_config.config_key")
    public void setConfigKey(String configKey) {
        this.configKey = configKey == null ? null : configKey.trim();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.541+08:00", comments="Source field: tbl_sys_config.config_desc")
    public String getConfigDesc() {
        return configDesc;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.541+08:00", comments="Source field: tbl_sys_config.config_desc")
    public void setConfigDesc(String configDesc) {
        this.configDesc = configDesc == null ? null : configDesc.trim();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.541+08:00", comments="Source field: tbl_sys_config.state")
    public Short getState() {
        return state;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.541+08:00", comments="Source field: tbl_sys_config.state")
    public void setState(Short state) {
        this.state = state;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.541+08:00", comments="Source field: tbl_sys_config.create_time")
    public Long getCreateTime() {
        return createTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.541+08:00", comments="Source field: tbl_sys_config.create_time")
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.541+08:00", comments="Source field: tbl_sys_config.update_time")
    public Long getUpdateTime() {
        return updateTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.541+08:00", comments="Source field: tbl_sys_config.update_time")
    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.541+08:00", comments="Source field: tbl_sys_config.config_value")
    public String getConfigValue() {
        return configValue;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-23T17:21:59.541+08:00", comments="Source field: tbl_sys_config.config_value")
    public void setConfigValue(String configValue) {
        this.configValue = configValue == null ? null : configValue.trim();
    }
}