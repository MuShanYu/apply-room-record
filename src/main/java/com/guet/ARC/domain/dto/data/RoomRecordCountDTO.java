package com.guet.ARC.domain.dto.data;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RoomRecordCountDTO {

    private String roomId; // 是否选择了房间名称，选择房间名称传入的是id

    private String roomCategory;

    private Long startTime;

    private Long endTime;
}
