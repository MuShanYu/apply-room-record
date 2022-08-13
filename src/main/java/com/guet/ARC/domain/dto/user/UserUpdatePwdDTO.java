package com.guet.ARC.domain.dto.user;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserUpdatePwdDTO {
    private String tel;

    private String code;

    private String key;

    private String pwd;
}
