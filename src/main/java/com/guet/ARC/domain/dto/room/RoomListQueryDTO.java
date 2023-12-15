package com.guet.ARC.domain.dto.room;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;


@Data
@ToString
public class RoomListQueryDTO {
    @Min(1)
    private Integer page;

    @Range(min = 1, max = 100)
    private Integer size;

    private String school;

    private String teachBuilding;

    private String category;

    private String roomName;

    private String chargePersonId;
}
