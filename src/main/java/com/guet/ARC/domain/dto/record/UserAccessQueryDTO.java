package com.guet.ARC.domain.dto.record;

import lombok.Data;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

@Data
@ToString
@Validated
public class UserAccessQueryDTO {
    private Integer page;

    private Integer size;

    private Long startTime;

    private Long endTime;

    @NotEmpty
    private String roomId;
}
