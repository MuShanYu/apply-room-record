package com.guet.ARC.domain;


import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tbl_message")
@Data
@ToString
public class Message {

    @Id
    private String id;

    private Short messageType;

    private Short readState;

    private String content;

    private String messageReceiverId;

    private String messageSenderId;

    private Long createTime;

    private Long updateTime;
}