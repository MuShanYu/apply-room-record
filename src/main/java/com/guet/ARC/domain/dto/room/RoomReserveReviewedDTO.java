package com.guet.ARC.domain.dto.room;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;

@Data
@ToString
@Validated
public class RoomReserveReviewedDTO {
    @Min(1)
    private Integer page;

    @Range(min = 1, max = 100)
    private Integer size;

    private String school;

    private String teachBuilding;

    private String category;
}
