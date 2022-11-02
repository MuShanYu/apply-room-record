package com.guet.ARC.domain.dto.record;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@Data
@ToString
public class UserAccessQueryDTO {
    private Integer page;

    private Integer size;

    private Long startTime;

    private Long endTime;

    @NotEmpty
    private String roomId;
}
