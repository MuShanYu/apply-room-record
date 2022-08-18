package com.guet.ARC.domain.vo.room;

import com.guet.ARC.domain.Room;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RoomReservationAdminVo extends Room {
    private String name;

    private String roomUsage;

    private String verifyUserName;

    private Long reserveStartTime;

    private Long reserveEndTime;

    private String roomId;

    private String userId;
}
