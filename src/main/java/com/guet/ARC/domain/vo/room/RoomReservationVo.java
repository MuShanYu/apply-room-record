package com.guet.ARC.domain.vo.room;

import com.guet.ARC.domain.Room;
import com.guet.ARC.domain.RoomReservation;
import com.guet.ARC.domain.enums.ReservationState;
import com.guet.ARC.domain.enums.RoomState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class RoomReservationVo extends RoomReservation {

    private String school;

    private String teachBuilding;

    private String category;

    private String roomName;

    private String equipmentInfo;

    private String capacity;

}
