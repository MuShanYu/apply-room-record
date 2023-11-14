package com.guet.ARC.domain.dto.notice;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;

/**
 * Author: Yulf
 * Date: 2023/11/13
 */
@Data
@ToString
public class NoticeQueryDTO {

    @Min(1)
    private Integer page;

    @Range(min = 1, max = 100)
    private Integer size;

    private String title;
}
