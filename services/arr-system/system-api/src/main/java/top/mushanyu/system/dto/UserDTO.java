package top.mushanyu.system.dto;

import lombok.Getter;
import lombok.Setter;
import top.mushanyu.common.domain.AbstractDTO;
import top.mushanyu.common.enums.State;

/**
 * @author MuShanYu
 * Date 2025/6/5
 */
@Getter
@Setter
public class UserDTO extends AbstractDTO {

    private String stuNum;

    private String name;

    private String institute;

    private State state;

    private String mail;

    private String openId;
}
