package top.mushanyu.system.cnodition;

import lombok.Getter;
import lombok.Setter;
import top.mushanyu.common.domain.PageCondition;

/**
 * @author MuShanYu
 * Date 2025/6/6
 */
@Getter
@Setter
public class RoleCondition extends PageCondition {

    private String name;
}
