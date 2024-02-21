package com.guet.ARC.domain.dto.user;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserUpdatePwdDTO {
    private String stuNum;

    private Integer code;

    private String pwd;
}
