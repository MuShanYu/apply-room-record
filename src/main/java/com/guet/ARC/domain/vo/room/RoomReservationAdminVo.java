package com.guet.ARC.domain.vo.room;

import com.guet.ARC.domain.enums.ReservationState;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RoomReservationAdminVo {

    private String id;
    private String name;

    private String roomUsage;

    private String verifyUserName;

    private Long reserveStartTime;

    private Long reserveEndTime;

    private String roomId;

    private String userId;

    private String school;

    private String teachBuilding;

    private String category;

    private String roomName;

    private String equipmentInfo;

    private String capacity;

    private ReservationState state;

    private Long updateTime;

    private Long createTime;
}
