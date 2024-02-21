package com.guet.ARC.domain.dto.user;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@ToString
public class UserRegisterDTO {

    @NotEmpty(message = "学号不能为空")
    @Size(min = 6, max = 20, message = "学号长度不正确")
    private String stuNum;

    @NotEmpty(message = "姓名不能为空")
    @Size(min = 2, max = 6, message = "姓名长度不正确")
    private String name;

    @NotEmpty(message = "学院不能为空")
    @Size(min = 2, max = 20, message = "学院长度不正确")
    private String institute;

    @Pattern(regexp = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}", message = "邮箱格式不正确")
    private String mail;

    private Integer code;
}
