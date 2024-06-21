package com.guet.ARC.domain;


import com.guet.ARC.domain.enums.State;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "tbl_role")
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
@ToString
public class Role {

    @Id
    private String id;

    private String roleName;

    private String roleDes;

    @Enumerated(EnumType.ORDINAL)
    private State state;

    private Long createTime;

    private Long updateTime;
}