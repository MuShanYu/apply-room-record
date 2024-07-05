package com.guet.ARC.domain;

import com.guet.ARC.domain.enums.RoomState;
import com.guet.ARC.domain.enums.State;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

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