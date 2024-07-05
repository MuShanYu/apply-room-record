package com.guet.ARC.domain.dto.data;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Data
@ToString
public class RoomReservationCountDTO {
    private String roomId;

    private String roomCategory;

    @NotNull
    private Long startTime;

    @NotNull
    private Long endTime;

    private String chargerPersonId;
}
