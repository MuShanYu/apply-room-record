package com.guet.ARC.domain.dto.record;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ToString
public class UserAccessCountDataQueryDTO {
    @NotNull
    private Long startTime;

    @NotNull
    private Long endTime;

    @NotEmpty
    private String roomId;
}
