package top.mushanyu.system.domain;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import top.mushanyu.common.domain.AbstractModel;
import top.mushanyu.common.enums.State;

@Entity
@Table(name = "sys_config")
@Getter
@Setter
public class SysConfig extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String configKey;

    private String configDesc;

    @Enumerated(EnumType.ORDINAL)
    private State state;

    private String configValue;
}