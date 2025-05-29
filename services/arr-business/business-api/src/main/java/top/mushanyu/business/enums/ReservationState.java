package top.mushanyu.business.enums;

import lombok.extern.slf4j.Slf4j;
/**
 * @author: MuShanYu
 * Date: 2023/11/22
 */
@Slf4j
public enum ReservationState {
    // 待审核
    ROOM_RESERVE_TO_BE_REVIEWED,
    // 已审核
    ROOM_RESERVE_ALREADY_REVIEWED,
    // 用户取消
    ROOM_RESERVE_CANCELED,
    // 审核不通过
    ROOM_RESERVE_TO_BE_REJECTED,

    ROOM_RESERVE_IS_TIME_OUT
}
