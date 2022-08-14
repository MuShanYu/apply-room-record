package com.guet.ARC.domain.dto.room;

import lombok.Data;

/**
 * @author liduo
 * @version 1.0
 */

@Data
public class ApplyRoomDTO {
    private String roomId;

    private Long startTime;

    private Long endTime;

    private String roomUsage;
}
