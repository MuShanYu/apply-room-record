package com.guet.ARC.domain.enums;

/**
 * 逻辑是与否
 */
public enum LogicWhether {

    /**
     * 表示逻辑否，0
     */
    NO,

    /**
     * 表示逻辑是，1
     */
    YES;

    public boolean getBool() {
        return YES.equals(this);
    }
}
