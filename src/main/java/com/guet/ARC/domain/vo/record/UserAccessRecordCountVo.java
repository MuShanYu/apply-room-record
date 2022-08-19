package com.guet.ARC.domain.vo.record;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserAccessRecordCountVo {
    private String roomId;

    private String roomName;

    private String category;

    private String teachBuilding;

    private String school;

    private long entryTimes;

    private long outTimes;
}
