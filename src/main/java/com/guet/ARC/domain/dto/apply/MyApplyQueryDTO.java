package com.guet.ARC.domain.dto.apply;

import com.guet.ARC.domain.enums.ReservationState;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;

/**
 * @author liduo
 * @version 1.0
 */
@Data
public class MyApplyQueryDTO {
    @Min(1)
    private Integer page;

    @Range(min = 1, max = 100)
    private Integer size;

    private String roomName;

    private Long startTime;

    private Long endTime;

    private ReservationState state;
}
