package com.guet.ARC.domain.vo.room;

import com.guet.ARC.domain.RoomReservation;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class RoomReservationUserVo extends RoomReservation {
    private String name;
}
