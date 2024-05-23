package com.guet.ARC.common.enmu;

import lombok.Getter;

/**
 * @author Yulf
 * Date 2024/5/23
 */
@Getter
public enum RoleType {

    USER("0", "user"),

    ADMIN("1", "admin"),

    SUPER_ADMIN("2", "super-admin");

    private String name;

    private String id;

    RoleType(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
