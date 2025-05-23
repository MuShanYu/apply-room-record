package top.mushanyu.business.domain;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;
import top.mushanyu.common.enums.State;

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