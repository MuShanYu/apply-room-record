package com.guet.ARC.domain.dto.room;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@Data
@ToString
public class RoomAddUpdateDTO {

    @NotEmpty
    private String id;

    private String school;

    private String teachBuilding;

    private String category;

    private String roomName;

    private String equipmentInfo;

    private String capacity;

    private String chargePerson;

    private String chargePersonTel;

    private String chargePersonId;
}
