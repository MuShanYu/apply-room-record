package com.guet.ARC.domain.vo.attendance;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AttendanceCountListVo {

    private String userId;

    // user
    private String stuNum;

    private String institute;

    private String name;

    private Integer validAttendanceTime;
}
