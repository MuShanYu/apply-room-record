package com.guet.ARC.domain.dto.config;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@Data
@ToString
public class SysConfigAddDTO {
    @NotEmpty
    private String key;

    @NotEmpty
    private String value;

    @NotEmpty
    private String configDesc;
}
