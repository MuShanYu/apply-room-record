package com.guet.ARC.domain.dto.log;

import com.guet.ARC.common.enmu.BusinessType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Yulf
 * Date 2024/6/14
 */
@Getter
@Setter
@ToString
public class SysOperateLogQueryDTO {

    private Integer page;

    private Integer size;

    private String name;

    private BusinessType businessType;

    private Long startTime;

    private Long endTime;

}
