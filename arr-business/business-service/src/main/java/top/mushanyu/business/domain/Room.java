package top.mushanyu.business.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;
import top.mushanyu.business.enums.RoomState;

@Entity
@Table(name = "tbl_room")
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
@ToString
public class Room {

    @Id
    private String id;

    private String school;

    private String teachBuilding;

    private String category;

    private String roomName;

    private String equipmentInfo;

    private String capacity;

    @Enumerated(EnumType.ORDINAL)
    private RoomState state;

    private Long updateTime;

    private Long createTime;

    private String chargePerson;

    private String chargePersonId;
}