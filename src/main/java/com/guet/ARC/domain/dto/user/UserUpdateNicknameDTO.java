package com.guet.ARC.domain.dto.user;

import lombok.Data;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

@Data
@ToString
@Validated
public class UserUpdateNicknameDTO {
    @NotEmpty(message = "用户id不能为空")
    private String userId;

    @NotEmpty(message = "用户新昵称不能为空")
    private String nickname;
}
