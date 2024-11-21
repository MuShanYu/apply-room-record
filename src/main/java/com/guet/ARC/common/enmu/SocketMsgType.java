package com.guet.ARC.common.enmu;

import lombok.Getter;

/**
 * @author Yulf
 * Date 2024/11/19
 */
@Getter
public enum SocketMsgType {
    /**
     * 设备信息
     */
    DEVICE("device"),

    /**
     * 用户消息
     */
    USER_MESSAGE("user_message");

    private final String type;

    SocketMsgType(String type) {
        this.type = type;
    }
}
