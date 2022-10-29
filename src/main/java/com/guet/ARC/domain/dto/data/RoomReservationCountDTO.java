package com.guet.ARC.domain.dto.data;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RoomReservationCountDTO {
    private String roomId;

    private String roomCategory;

    private Long startTime;

    private Long endTime;
}
