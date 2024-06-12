package com.guet.ARC.common.enmu;

/**
 * @author Yulf
 * Date 2024/6/7
 */
public enum SocketBusinessType {

    /**
     * 在线人员统计业务
     */
    ONLINE_COUNT,

    /**
     * 其他自定义业务逻辑
     */
    OTHER;

    public static SocketBusinessType valueOf(int ordinal) {
        SocketBusinessType[] values = SocketBusinessType.values();
        if (ordinal >= values.length) {
            return null;
        } else {
            return values[ordinal];
        }
    }
}
