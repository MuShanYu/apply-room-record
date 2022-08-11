package com.guet.ARC.domain.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserRegisterDTO {
    private String stuNum;

    private String name;

    private String institute;

    private String tel;
}
