package com.guet.ARC.domain;


import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tbl_notice")
@Data
@ToString
public class Notice {

    @Id
    private String id;

    private String title;

    private String publishUserId;

    private Long createTime;

    private Long updateTime;

    private Short state;

    private String content;
}