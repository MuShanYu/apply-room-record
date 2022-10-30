package com.guet.ARC.domain.dto.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

@Data
@ToString
@Validated
public class SysConfigAddDTO {
    @NotEmpty
    private String key;

    @NotEmpty
    private String value;

    @NotEmpty
    private String configDesc;
}
