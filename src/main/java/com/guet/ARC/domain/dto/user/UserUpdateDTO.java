package com.guet.ARC.domain.dto.user;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ToString
public class UserUpdateDTO {
    @NotNull
    @NotBlank
    private String id;

    @Length(min = 10, max = 62)
    private String stuNum;

    @Length(min = 1, max = 16)
    private String name;

    @Length(min = 2, max = 62)
    private String institute;
}
