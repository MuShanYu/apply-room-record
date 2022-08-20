package com.guet.ARC.domain.vo.record;

import com.guet.ARC.domain.AccessRecord;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserAccessRecordVo extends AccessRecord {
    private String roomName;

    private String category;

    private String teachBuilding;

    private String school;

    private String capacity;
}
