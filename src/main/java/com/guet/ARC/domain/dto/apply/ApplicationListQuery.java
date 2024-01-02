package com.guet.ARC.domain.dto.apply;

import com.guet.ARC.domain.enums.ApplicationState;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;

/**
 * Author: Yulf
 * Date: 2023/11/15
 */
@Data
@ToString
public class ApplicationListQuery {
    @Min(1)
    private Integer page;

    @Range(min = 1, max = 100)
    private Integer size;

    private String stuNum;

    private String startDateStr;

    private String endDateStr;

    private ApplicationState applicationState;
}
