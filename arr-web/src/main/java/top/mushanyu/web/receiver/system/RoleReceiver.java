package top.mushanyu.web.receiver.system;

import lombok.Getter;
import lombok.Setter;
import top.mushanyu.system.enums.RoleType;

import java.util.List;

/**
 * @author MuShanYu
 * Date 2025/6/9
 */
@Getter
@Setter
public class RoleReceiver {
    private Long id;

    private String name;

    private String des;

    private RoleType type;

    private List<Long> rightIds;
}
