package com.guet.ARC.common.enmu;


/**
 * @author Yulf
 * Date 2024/9/3
 */
public enum DelayTaskType {

    /**
     * 签到状态过期未签退提醒
     */
    SIGN_IN_EXPIRED_NOTIFY,

    /**
     * 房间预约已经超时未审核
     */
    RESERVATION_NOT_PROCESSED,

    /**
     * 房间预约即将超时未审核
     */
    RESERVATION_SOON_OVERDUE;
}
