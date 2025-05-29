package top.mushanyu.system.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import top.mushanyu.config.domain.AbstractModel;
import top.mushanyu.common.enums.State;
import top.mushanyu.system.enums.RightClass;
import top.mushanyu.system.enums.RightType;

/**
 * @author MuShanYu
 * Date 2025/5/23
 */
@Entity
@Table(name = "rights")
@Getter
@Setter
public class Right extends AbstractModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.ORDINAL)
    private RightType type;

    private Boolean assignable;

    private Long parentId;

    @Enumerated(EnumType.ORDINAL)
    private RightClass rightClass;

    private String rightObjectId;

    @Enumerated(EnumType.ORDINAL)
    private State state;
}
