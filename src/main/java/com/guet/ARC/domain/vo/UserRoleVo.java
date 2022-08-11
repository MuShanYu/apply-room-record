package com.guet.ARC.domain.vo;

import com.guet.ARC.domain.Role;
import com.guet.ARC.domain.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString
public class UserRoleVo extends User {
    private List<Role> roleList;
}
