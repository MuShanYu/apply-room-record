package top.mushanyu.system.dto;

import lombok.Getter;
import lombok.Setter;
import top.mushanyu.common.domain.AbstractDTO;
import top.mushanyu.common.enums.State;
import top.mushanyu.system.enums.RightClass;
import top.mushanyu.system.enums.RightType;

/**
 * @author MuShanYu
 * Date 2025/5/26
 */
@Getter
@Setter
public class RightDTO extends AbstractDTO {

    private String name;

    private RightType type;

    private Boolean assignable;

    private Long parentId;

    private RightClass rightClass;

    private String rightObjectId;

    private State state;

   public void updateFields(RightDTO updateDTO) {
       this.name = updateDTO.name;
       this.type = updateDTO.type;
       this.assignable = updateDTO.assignable;
       this.parentId = updateDTO.parentId;
       this.rightClass = updateDTO.rightClass;
       this.rightObjectId = updateDTO.rightObjectId;
   }
}
