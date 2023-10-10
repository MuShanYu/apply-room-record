package com.guet.ARC.domain.vo.attendance;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

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

    private Integer validAttendanceMills;

    private BigDecimal validAttendanceHours;
}
