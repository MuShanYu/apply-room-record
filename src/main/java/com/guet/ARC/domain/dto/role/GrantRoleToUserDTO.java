package com.guet.ARC.domain.dto.role;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author Yulf
 * Date 2024/6/25
 */
@Getter
@Setter
@ToString
public class GrantRoleToUserDTO {

    private String userId;

    private List<String> roleIds;
}
