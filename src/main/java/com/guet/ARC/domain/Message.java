package com.guet.ARC.domain;


import com.guet.ARC.domain.enums.MessageType;
import com.guet.ARC.domain.enums.ReadState;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "tbl_message")
@Data
@ToString
public class Message {

    @Id
    private String id;

    @Enumerated(EnumType.ORDINAL)
    private MessageType messageType;

    @Enumerated(EnumType.ORDINAL)
    private ReadState readState;

    private String content;

    private String messageReceiverId;

    private String messageSenderId;

    private Long createTime;

    private Long updateTime;
}