package top.mushanyu.business.dto;

import lombok.Getter;
import lombok.Setter;
import top.mushanyu.business.enums.RoomState;
import top.mushanyu.common.domain.AbstractDTO;

@Getter
@Setter
public class RoomDTO extends AbstractDTO {

    private Long id;

    private String school;

    private String teachBuilding;

    private String category;

    private String name;

    private String equipmentInfo;

    private String capacity;

    private RoomState state;

    private Long ownerId;
}