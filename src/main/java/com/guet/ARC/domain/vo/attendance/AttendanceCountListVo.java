package com.guet.ARC.domain.vo.attendance;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
public class AttendanceCountListVo {

    private String userId;

    // user
    private String stuNum;

    private String institute;

    private String name;

    private Integer validAttendanceMills;

    private BigDecimal validAttendanceHours;
}
