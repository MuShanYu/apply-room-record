package com.guet.ARC.domain.dto.room;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;

/**
 * @author liduo
 * @version 1.0
 */

@Data
public class RoomQueryDTO {
    @Min(1)
    private Integer page;

    @Range(min = 1, max = 100)
    private Integer size;

    private Long startTime;

    private Long endTime;

    private String school;

    private String category;

    private String teachBuilding;
}
