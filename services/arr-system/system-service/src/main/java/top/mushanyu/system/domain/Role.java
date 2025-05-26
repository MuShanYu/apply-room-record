package top.mushanyu.system.domain;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import top.mushanyu.common.domain.AbstractModel;
import top.mushanyu.common.enums.State;

@Entity
@Table(name = "role")
@Getter
@Setter
public class Role extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String des;

    @Enumerated(EnumType.ORDINAL)
    private State state;
}