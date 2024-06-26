package com.guet.ARC.common.enmu;

import lombok.Getter;

/**
 * @author Yulf
 * Date 2024/4/17
 */
@Getter
public enum RedisCacheKey {
    /**
     * 房间申请占用记录key，用于房间申请超时未处理，通知申请人
     */
    ROOM_OCCUPANCY_APPLY_KEY("room:occupancy:apply:"),

    /**
     * 房间申请审核即将过期通知，用于通知负责人审核
     */
    ROOM_APPLY_TIMEOUT_NOTIFY_KEY("room:soon:timeout:notify:"),

    MAIL_RESEND_KEY("mail:resend:list"),

    /**
     * websocket channel -> userId
     */
    WEBSOCKET_CHANNEL_TO_USER_ID("websocket:channel:to:userid:map"),
    /**
     * websocket channel -> source（设备类型）
     */
    WEBSOCKET_CHANNEL_TO_SOURCE("websocket:channel:to:source:map"),
    /**
     * websocket userId -> list<chanel>
     */
    WEBSOCKET_USERID_TO_LIST_SOURCE("websocket:userid:to:list:channel:map"),

    WEBSOCKET_USERID_TO_CHANNEL("websocket:userid:to:channel")
    ;

    private final String key;

    RedisCacheKey(String key) {
        this.key = key;
    }

    public String concatKey(String suffix) {
        return key + suffix;
    }
}
