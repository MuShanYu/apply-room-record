package top.mushanyu.system.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.*;
import top.mushanyu.common.domain.AbstractModel;
import top.mushanyu.common.enums.State;

@Entity
@Table(name = "users" )
@Getter
@Setter
@ToString
public class User extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pwd;

    private String stuNum;

    private String name;

    private String institute;

    @Enumerated(EnumType.ORDINAL)
    private State state;

    private String mail;

    private String openId;
}