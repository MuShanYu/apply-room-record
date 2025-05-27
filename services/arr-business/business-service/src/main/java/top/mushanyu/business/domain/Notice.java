package top.mushanyu.business.domain;


import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import top.mushanyu.config.domain.AbstractModel;
import top.mushanyu.common.enums.State;

@Entity
@Table(name = "notice")
@Getter
@Setter
public class Notice extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Long userId;

    @Enumerated(EnumType.ORDINAL)
    private State state;

    private String content;
}