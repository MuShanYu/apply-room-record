package top.mushanyu.system.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import top.mushanyu.common.domain.AbstractDTO;
import top.mushanyu.common.enums.State;
import top.mushanyu.system.enums.RoleType;

import java.util.List;

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

    private List<Long> rightIds;

    public void updateFields(RoleDTO updateDTO) {
        this.name = updateDTO.getName();
        this.des = updateDTO.getDes();
        this.type = updateDTO.getType();
    }
}
