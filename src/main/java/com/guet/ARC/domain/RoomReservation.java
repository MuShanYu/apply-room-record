package com.guet.ARC.domain;


import com.guet.ARC.domain.enums.ReservationState;
import com.guet.ARC.domain.enums.State;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "tbl_room_reservation")
@DynamicInsert
@DynamicUpdate
@Data
@ToString
public class RoomReservation {

    @Id
    private String id;

    private String roomUsage;

    private Long reserveStartTime;

    private Long reserveEndTime;

    private String verifyUserName;

    @Enumerated(EnumType.ORDINAL)
    private ReservationState state;

    private Long updateTime;

    private Long createTime;

    private String userId;

    private String roomId;
}