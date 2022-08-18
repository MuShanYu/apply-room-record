package com.guet.ARC.domain.dto.room;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author liduo
 * @version 1.0
 */

@Data
public class ApplyRoomDTO {
    @NotEmpty
    private String roomId;

    private Long startTime;

    private Long endTime;

    @NotEmpty
    private String roomUsage;
}
