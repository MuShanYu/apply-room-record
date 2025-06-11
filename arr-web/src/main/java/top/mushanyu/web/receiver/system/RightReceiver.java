package top.mushanyu.web.receiver.system;

import lombok.Getter;
import lombok.Setter;
import top.mushanyu.system.enums.RightClass;
import top.mushanyu.system.enums.RightType;

/**
 * @author MuShanYu
 * Date 2025/6/11
 */
@Getter
@Setter
public class RightReceiver {

    private Long id;

    private String name;

    private RightType type;

    private Boolean assignable;

    private Long parentId;

    private RightClass rightClass;

    private String rightObjectId;

}
