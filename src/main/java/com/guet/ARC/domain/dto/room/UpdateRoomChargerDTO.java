package com.guet.ARC.domain.dto.room;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@Data
@ToString
public class UpdateRoomChargerDTO {
    @NotEmpty
    private String id;

    private String chargePerson;

    private String chargePersonTel;

    private String originChargePersonId;
}
