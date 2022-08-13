package com.guet.ARC.domain.dto.user;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserRoleChangeDTO {
    private String userId;

    private String[] roleIds;
}
