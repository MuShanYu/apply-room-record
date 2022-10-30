package com.guet.ARC.domain.dto.user;

import lombok.Data;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

@Data
@ToString
@Validated
public class UserUpdateNameDTO {
    @NotEmpty(message = "用户id不能为空")
    private String userId;

    @NotEmpty(message = "用户新姓名不能为空")
    private String name;
}
