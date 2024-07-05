package com.guet.ARC.domain;


import com.guet.ARC.domain.enums.State;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "tbl_notice")
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
@ToString
public class Notice {

    @Id
    private String id;

    private String title;

    private String publishUserId;

    private Long createTime;

    private Long updateTime;

    @Enumerated(EnumType.ORDINAL)
    private State state;

    private String content;
}