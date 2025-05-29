package top.mushanyu.system.dto;

import lombok.Getter;
import lombok.Setter;
import top.mushanyu.common.domain.AbstractDTO;
import top.mushanyu.common.enums.State;
import top.mushanyu.system.enums.RoleType;

/**
 * @author MuShanYu
 * Date 2025/5/27
 */
@Getter
@Setter
public class RoleDTO extends AbstractDTO {

    private String name;

    private String des;

    private State state;

    private RoleType type;
}
