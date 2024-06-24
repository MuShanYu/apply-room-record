package com.guet.ARC.domain.dto.menu;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author Yulf
 * Date 2024/6/24
 */
@Getter
@Setter
@ToString
public class GrantMenuToRoleDTO {

    private List<String> menuIds;

    private String roleId;
}
