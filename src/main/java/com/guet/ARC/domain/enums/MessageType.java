package com.guet.ARC.domain.enums;

/**
 * Author: Yulf
 * Date: 2023/11/13
 */
public enum MessageType {
    // 待办通知
    TODO,
    // 申请结果通知
    RESULT,

    SYSTEM;

    public static MessageType valueOf(int ordinal) {
        MessageType[] values = MessageType.values();
        if (ordinal >= values.length) {
            return null;
        } else {
            return values[ordinal];
        }
    }
}
