package top.mushanyu.business.domain;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import top.mushanyu.business.enums.ReservationState;

import java.time.LocalDateTime;

@Entity
@Table(name = "room_reservation")
@Getter
@Setter
public class RoomReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomUsage;

    private LocalDateTime reserveStartTime;

    private LocalDateTime reserveEndTime;

    private Long verifyUserId;

    @Enumerated(EnumType.ORDINAL)
    private ReservationState state;

    private Long userId;

    private Long roomId;

    private String remark;
}