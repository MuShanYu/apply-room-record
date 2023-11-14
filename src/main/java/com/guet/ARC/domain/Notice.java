package com.guet.ARC.domain;


import com.guet.ARC.domain.enums.State;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

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

    @Enumerated(EnumType.ORDINAL)
    private State state;

    private String content;
}