package com.guet.ARC.domain.vo.apply;

import com.guet.ARC.domain.Application;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Author: Yulf
 * Date: 2023/11/15
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class ApplicationListVo extends Application {
    private String stuNum;

    private String name;
}
