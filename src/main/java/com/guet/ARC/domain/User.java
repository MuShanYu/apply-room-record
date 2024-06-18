package com.guet.ARC.domain;


import com.guet.ARC.domain.enums.State;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "tbl_user" )
@DynamicUpdate
@DynamicInsert
@Data
@ToString
public class User {

    @Id
    private String id;

    private String pwd;

    private String stuNum;

    private String name;

    private String institute;

    @Enumerated(EnumType.ORDINAL)
    private State state;

    private Long updateTime;

    private Long createTime;

    private String mail;

    private String openId;
}