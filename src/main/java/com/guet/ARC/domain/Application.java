package com.guet.ARC.domain;

import com.guet.ARC.domain.enums.ApplicationType;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "tbl_application" )
@Data
@ToString
public class Application {

    @Id
    private String id;

    private String title;

    private String reason;

    // 数据库为_连接命名，实体类使用驼峰明明，jpa默认遇到大写字母转为_连接，与数据库对应。
    @Enumerated(EnumType.ORDINAL)
    private ApplicationType applicationType;

    private String matterRecordId;

    private String handleUserId;

    private String applyUserId;

    private Short state;

    private String remarks;

    private Long createTime;

    private Long updateTime;
}