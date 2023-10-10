package com.guet.ARC.domain.dto.attendance;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Data
@ToString
public class AttendanceDetailListDTO {

    @Min(1)
    private Integer page;

    @Range(min = 1, max = 100)
    private Integer size;

    @NotEmpty
    private String userId;

    @NotEmpty
    private String roomId;

    private Long startTime;

    private Long endTime;
}
