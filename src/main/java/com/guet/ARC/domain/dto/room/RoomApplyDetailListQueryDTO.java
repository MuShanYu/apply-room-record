package com.guet.ARC.domain.dto.room;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class RoomApplyDetailListQueryDTO {
    @Min(1)
    private Integer page;

    @Range(min = 1, max = 100)
    private Integer size;

    @NotEmpty
    private String roomId;

    @NotNull
    private Long startTime;

    @NotNull
    private Long endTime;
}
