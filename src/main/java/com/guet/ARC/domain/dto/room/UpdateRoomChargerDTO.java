package com.guet.ARC.domain.dto.room;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UpdateRoomChargerDTO {
    private String id;

    private String chargePerson;

    private String chargePersonTel;

    private String originChargePersonId;
}
