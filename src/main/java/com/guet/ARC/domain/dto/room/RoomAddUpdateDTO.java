package com.guet.ARC.domain.dto.room;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RoomAddUpdateDTO {

    private String school;

    private String teachBuilding;

    private String category;

    private String roomName;

    private String equipmentInfo;

    private String capacity;

    private String chargePerson;

    private String chargePersonId;

    private String stuNum;
}
