package com.guet.ARC.domain;


import com.guet.ARC.domain.enums.MessageType;
import com.guet.ARC.domain.enums.ReadState;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "tbl_message")
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
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