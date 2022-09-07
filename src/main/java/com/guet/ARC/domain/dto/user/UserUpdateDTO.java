package com.guet.ARC.domain.dto.user;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Data
@ToString
public class UserUpdateDTO {
    @NotEmpty
    private String id;

    @Length(min = 1, max = 32)
    private String stuNum;

    @Length(min = 1, max = 16)
    private String name;

    @Length(min = 2, max = 62)
    private String institute;
}
