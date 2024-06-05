package com.guet.ARC.domain.dto.data;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Data
@ToString
public class RoomRecordCountDTO {

    private String roomId; // 是否选择了房间名称，选择房间名称传入的是id

    private String roomCategory;

    @NotNull
    private Long startTime;

    @NotNull
    private Long endTime;

    private String chargerPersonId;
}
