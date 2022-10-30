package com.guet.ARC.domain.dto.apply;

import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;

/**
 * @author liduo
 * @version 1.0
 */
@Data
@Validated
public class MyApplyQueryDTO {
    @Min(1)
    private Integer page;

    @Range(min = 1, max = 100)
    private Integer size;

    private String school;

    private String teachBuilding;

    private String category;

    private Long startTime;

    private Long endTime;
}
