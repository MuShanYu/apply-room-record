package top.mushanyu.business.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;
import top.mushanyu.business.enums.ReservationState;

@Entity
@Table(name = "tbl_room_reservation")
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
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

    private String remark;
}