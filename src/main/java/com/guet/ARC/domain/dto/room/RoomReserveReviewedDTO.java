package com.guet.ARC.domain.dto.room;

import com.guet.ARC.domain.enums.ReservationState;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@ToString
public class RoomReserveReviewedDTO {
    @Min(1)
    private Integer page;

    @Range(min = 1, max = 100)
    private Integer size;

    private String school;

    private String teachBuilding;

    private String category;

    @NotNull(message = "状态不能为空")
    private ReservationState state;
}
