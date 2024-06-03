package com.guet.ARC.domain.enums;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.guet.ARC.common.enmu.WxMessageTemplateId;
import com.guet.ARC.domain.Room;
import com.guet.ARC.domain.RoomReservation;
import com.guet.ARC.domain.User;
import com.guet.ARC.util.CommonUtils;
import com.guet.ARC.util.WxUtils;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: Yulf
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

    ROOM_RESERVE_IS_TIME_OUT;

    public static ReservationState valueOf(int ordinal) {
        ReservationState[] values = ReservationState.values();
        if (ordinal >= values.length) {
            return null;
        } else {
            return values[ordinal];
        }
    }

    public String generateFeedback(String userName, String roomName, RoomReservation reservation) {
        // 获取周几
        int dayOfWeek = DateUtil.dayOfWeek(new Date(reservation.getReserveStartTime()));
        String timeDateStr = DateUtil.format(new Date(reservation.getReserveStartTime()), "yyyy年MM月dd日 HH:mm") + "~"
                + DateUtil.format(new Date(reservation.getReserveEndTime()), "HH:mm") + "（" + getWeekStr(dayOfWeek) + "）";
        String createTimeStr = DateUtil.format(new Date(reservation.getCreateTime()), "yyyy年MM月dd日 HH:mm");
        switch (this) {
            case ROOM_RESERVE_CANCELED:
                return  userName + "取消了房间" + roomName + "的预约申请。预约时间：" + timeDateStr +
                        "。" + "取消理由：" + reservation.getRemark() + "。";
            case ROOM_RESERVE_TO_BE_REVIEWED:
                return "您收到来自" + userName + "的" + roomName + "房间预约申请，预约时间" + timeDateStr + "，请您及时处理。";
            case ROOM_RESERVE_ALREADY_REVIEWED:
                return "您" + createTimeStr +
                        "发起的" + roomName + "预约申请，预约时间为" + timeDateStr + "，已由审核员审核通过。";
            case ROOM_RESERVE_TO_BE_REJECTED:
                return "您" + createTimeStr + "发起的" + roomName + "预约申请，预约时间为" + timeDateStr
                        + "，审核不通过。原因为：" + reservation.getRemark() + "。";
            default:
                return "";
        }
    }

    public void sendReservationNoticeMessage(Room room, User user, RoomReservation roomReservation) {
        Map<String, Map<String, Object>> data = new HashMap<>();
        // 预约时间段 2022年04月15日 13:00~14:00
        String timeDateStr = DateUtil.format(new Date(roomReservation.getReserveStartTime()), "yyyy年MM月dd日 HH:mm") + "~"
                + DateUtil.format(new Date(roomReservation.getReserveEndTime()), "HH:mm");
        // 处理姓名，wx不可超过五个字符
        String name = user.getName().length() > 5 ? user.getName().substring(0, 5) : user.getName();
        String mem = roomReservation.getRoomUsage().length() > 20 ? roomReservation.getRoomUsage().substring(0, 16) + "..." : roomReservation.getRoomUsage();
        String reason = "";
        if (!StrUtil.isEmpty(roomReservation.getRemark())) {
            reason = roomReservation.getRemark().length() > 20 ?
                    roomReservation.getRemark().substring(0, 16) + "..." : roomReservation.getRemark();
        }
        String templateId;
        switch (this) {
            case ROOM_RESERVE_TO_BE_REVIEWED:
                data.put("name1", CommonUtils.createValueItem(name));
                data.put("thing2", CommonUtils.createValueItem(room.getRoomName()));
                data.put("time60", CommonUtils.createValueItem(timeDateStr));
                data.put("thing7", CommonUtils.createValueItem(mem));
                templateId = WxMessageTemplateId.APPLY_NOTICE_TEMPLATE.getId();
                break;
            case ROOM_RESERVE_ALREADY_REVIEWED:
                data.put("thing33", CommonUtils.createValueItem(name));
                data.put("thing47", CommonUtils.createValueItem(room.getRoomName()));
                data.put("thing13", CommonUtils.createValueItem(timeDateStr));
                data.put("thing4", CommonUtils.createValueItem("符合要求，审核通过。"));
                templateId = WxMessageTemplateId.APPLY_SUCCESS_NOTICE_TEMPLATE.getId();
                break;
            case ROOM_RESERVE_CANCELED:
                data.put("thing10", CommonUtils.createValueItem(name));
                // 预约地点
                data.put("thing13", CommonUtils.createValueItem(room.getRoomName()));
                data.put("thing8", CommonUtils.createValueItem(timeDateStr));
                data.put("thing4", CommonUtils.createValueItem(reason));
                templateId = WxMessageTemplateId.WITHDRAW_NOTICE_TEMPLATE.getId();
                break;
            case ROOM_RESERVE_TO_BE_REJECTED:
                data.put("thing19", CommonUtils.createValueItem(room.getRoomName()));
                data.put("date2", CommonUtils.createValueItem(timeDateStr));
                data.put("thing4", CommonUtils.createValueItem(reason));
                templateId = WxMessageTemplateId.APPLY_FAILED_NOTICE_TEMPLATE.getId();
                break;
            default:
                return;
        }
        if (StrUtil.isNotEmpty(user.getOpenId())) {
            WxUtils.sendSubscriptionMessage(user.getOpenId(), templateId, data);
        }
    }

    private String getWeekStr(int dayOfWeek) {
        switch (dayOfWeek) {
            case 1:
                return "周日";
            case 2:
                return "周一";
            case 3:
                return "周二";
            case 4:
                return "周三";
            case 5:
                return "周四";
            case 6:
                return "周五";
            case 7:
                return "周六";
            default:
                return "";
        }
    }

}
