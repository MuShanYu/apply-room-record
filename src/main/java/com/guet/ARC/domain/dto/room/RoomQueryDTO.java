package com.guet.ARC.domain.dto.room;

import lombok.Data;

/**
 * @author liduo
 * @version 1.0
 */

@Data
public class RoomQueryDTO {
    private Long startTime;

    private Long endTime;

    private String school;

    private String category;

    private String teachBuilding;
}
