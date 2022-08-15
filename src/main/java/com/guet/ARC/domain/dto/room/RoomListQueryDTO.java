package com.guet.ARC.domain.dto.room;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;

/**
 * @author liduo
 * @version 1.0
 */
@Data
public class RoomListQueryDTO {
    @Min(1)
    private Integer page;

    @Range(min = 1, max = 100)
    private Integer size;

    private String school;

    private String teachBuilding;

    private String category;
}
