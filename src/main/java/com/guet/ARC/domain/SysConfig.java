package com.guet.ARC.domain;

import com.guet.ARC.domain.enums.State;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "tbl_sys_config")
@DynamicInsert
@DynamicUpdate
@Data
@ToString
public class SysConfig {

    @Id
    private String id;

    private String configKey;

    private String configDesc;

    @Enumerated(EnumType.ORDINAL)
    private State state;

    private Long createTime;

    private Long updateTime;

    private String configValue;
}