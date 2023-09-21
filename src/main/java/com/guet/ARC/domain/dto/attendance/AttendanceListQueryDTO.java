package com.guet.ARC.domain.dto.attendance;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ToString
public class AttendanceListQueryDTO {
    @Min(1)
    private Integer page;

    @Range(min = 1, max = 100)
    private Integer size;

    @NotNull
    private Long startTime;

    @NotNull
    private Long endTime;

    @NotEmpty
    private String roomId;

    private String name;
}
