package com.guet.ARC.domain.vo.room;

import com.guet.ARC.domain.Room;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class RoomReservationVo extends Room {
    private String roomUsage;

    private String verifyUserName;

    private Long reserveStartTime;

    private Long reserveEndTime;

    private String roomId;
}
