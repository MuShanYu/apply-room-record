package com.guet.ARC.domain.vo.attendance;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AttendanceDetailListVo {
    private String id;

    private Long entryTime;

    private Long outTime;

    private Short state;

    // user
    private String stuNum;

    private String institute;

    private String name;

    private Integer validAttendanceTime;
}
