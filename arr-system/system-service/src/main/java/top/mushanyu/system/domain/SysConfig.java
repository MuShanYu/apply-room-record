package top.mushanyu.system.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;
import top.mushanyu.common.enums.State;

@Entity
@Table(name = "tbl_sys_config")
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
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