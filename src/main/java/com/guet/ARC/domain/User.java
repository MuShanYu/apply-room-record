package com.guet.ARC.domain;

import javax.annotation.Generated;

public class User {
    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.264+08:00", comments="Source field: tbl_user.id")
    private String id;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.264+08:00", comments="Source field: tbl_user.nickname")
    private String nickname;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.264+08:00", comments="Source field: tbl_user.pwd")
    private String pwd;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.264+08:00", comments="Source field: tbl_user.stu_num")
    private String stuNum;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.264+08:00", comments="Source field: tbl_user.name")
    private String name;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.265+08:00", comments="Source field: tbl_user.tel")
    private String tel;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.265+08:00", comments="Source field: tbl_user.institute")
    private String institute;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.265+08:00", comments="Source field: tbl_user.state")
    private Short state;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.265+08:00", comments="Source field: tbl_user.update_time")
    private Long updateTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.265+08:00", comments="Source field: tbl_user.create_time")
    private Long createTime;

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.264+08:00", comments="Source field: tbl_user.id")
    public String getId() {
        return id;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.264+08:00", comments="Source field: tbl_user.id")
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.264+08:00", comments="Source field: tbl_user.nickname")
    public String getNickname() {
        return nickname;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.264+08:00", comments="Source field: tbl_user.nickname")
    public void setNickname(String nickname) {
        this.nickname = nickname == null ? null : nickname.trim();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.264+08:00", comments="Source field: tbl_user.pwd")
    public String getPwd() {
        return pwd;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.264+08:00", comments="Source field: tbl_user.pwd")
    public void setPwd(String pwd) {
        this.pwd = pwd == null ? null : pwd.trim();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.264+08:00", comments="Source field: tbl_user.stu_num")
    public String getStuNum() {
        return stuNum;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.264+08:00", comments="Source field: tbl_user.stu_num")
    public void setStuNum(String stuNum) {
        this.stuNum = stuNum == null ? null : stuNum.trim();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.264+08:00", comments="Source field: tbl_user.name")
    public String getName() {
        return name;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.265+08:00", comments="Source field: tbl_user.name")
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.265+08:00", comments="Source field: tbl_user.tel")
    public String getTel() {
        return tel;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.265+08:00", comments="Source field: tbl_user.tel")
    public void setTel(String tel) {
        this.tel = tel == null ? null : tel.trim();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.265+08:00", comments="Source field: tbl_user.institute")
    public String getInstitute() {
        return institute;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.265+08:00", comments="Source field: tbl_user.institute")
    public void setInstitute(String institute) {
        this.institute = institute == null ? null : institute.trim();
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.265+08:00", comments="Source field: tbl_user.state")
    public Short getState() {
        return state;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.265+08:00", comments="Source field: tbl_user.state")
    public void setState(Short state) {
        this.state = state;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.265+08:00", comments="Source field: tbl_user.update_time")
    public Long getUpdateTime() {
        return updateTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.265+08:00", comments="Source field: tbl_user.update_time")
    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.265+08:00", comments="Source field: tbl_user.create_time")
    public Long getCreateTime() {
        return createTime;
    }

    @Generated(value="org.mybatis.generator.api.MyBatisGenerator", date="2022-08-09T20:41:25.265+08:00", comments="Source field: tbl_user.create_time")
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}