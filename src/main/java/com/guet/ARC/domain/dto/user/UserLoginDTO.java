package com.guet.ARC.domain.dto.user;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserLoginDTO {
    private String tel;

    private String pwd;

    private String key;
}
