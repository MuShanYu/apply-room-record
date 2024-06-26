package com.guet.ARC.domain;

import com.guet.ARC.domain.enums.State;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.annotation.Generated;
import javax.persistence.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_user_role" )
@DynamicUpdate
@DynamicInsert
@Getter
@Setter
@ToString
public class UserRole {

    @Id
    private String id;

    private String userId;

    private String roleId;

    @Enumerated(EnumType.ORDINAL)
    private State state;

    private Long createTime;

    private Long updateTime;
}