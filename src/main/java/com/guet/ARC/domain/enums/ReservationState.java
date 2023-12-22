package com.guet.ARC.domain.enums;

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
    ROOM_RESERVE_TO_BE_REVIEWED {
        @Override
        public void sendReservationNoticeMessage(Room room, User user, RoomReservation roomReservation) {
            Map<String, Map<String, Object>> data = new HashMap<>();
            // 处理姓名，wx不可超过五个字符
            String name = user.getName().length() > 5 ? user.getName().substring(0, 5) : user.getName();
            data.put("name1", CommonUtils.createValueItem(name));
            data.put("thing2", CommonUtils.createValueItem(room.getRoomName()));
            // 预约时间段 2022年04月15日 13:00~14:00
            SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm");
            SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
            String timeDateStr = sdf.format(new Date(roomReservation.getReserveStartTime())) + "~" + sdf2.format(new Date(roomReservation.getReserveEndTime()));
            data.put("time60", CommonUtils.createValueItem(timeDateStr));
            // 预约理由
            String mem = roomReservation.getRoomUsage().length() > 20 ? roomReservation.getRoomId().substring(0, 20) : roomReservation.getRoomUsage();
            data.put("thing7", CommonUtils.createValueItem(mem));
            log.info("ready send message to {}, message is {}", user.getName(), data);
            if (!StrUtil.isEmpty(user.getOpenId())) {
                WxUtils.sendSubscriptionMessage(user.getOpenId(), WxMessageTemplateId.APPLY_NOTICE_TEMPLATE.getId(), data);
            }
        }
    },
    // 已审核
    ROOM_RESERVE_ALREADY_REVIEWED {
        @Override
        public void sendReservationNoticeMessage(Room room, User user, RoomReservation roomReservation) {
            Map<String, Map<String, Object>> data = new HashMap<>();
            // 处理姓名，wx不可超过五个字符
            String name = user.getName().length() > 5 ? user.getName().substring(0, 5) : user.getName();
            data.put("thing33", CommonUtils.createValueItem(name));

            data.put("thing47", CommonUtils.createValueItem(room.getRoomName()));
            // 预约时间段 2022年04月15日 13:00~14:00
            SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm");
            SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
            String timeDateStr = sdf.format(new Date(roomReservation.getReserveStartTime())) + "~" + sdf2.format(new Date(roomReservation.getReserveEndTime()));
            data.put("thing13", CommonUtils.createValueItem(timeDateStr));


            data.put("thing4", CommonUtils.createValueItem("符合要求，审核通过。"));
            log.info("ready send message to {}, message is {}", user.getName(), data);
            if (!StrUtil.isEmpty(user.getOpenId())) {
                WxUtils.sendSubscriptionMessage(user.getOpenId(), WxMessageTemplateId.APPLY_SUCCESS_NOTICE_TEMPLATE.getId(), data);
            }
        }
    },

    // 用户取消
    ROOM_RESERVE_CANCELED {
        @Override
        public void sendReservationNoticeMessage(Room room, User user, RoomReservation roomReservation) {
            Map<String, Map<String, Object>> data = new HashMap<>();
            // 处理姓名，wx不可超过五个字符
            String name = user.getName().length() > 5 ? user.getName().substring(0, 5) : user.getName();
            data.put("thing10", CommonUtils.createValueItem(name));
            // 预约地点
            data.put("thing13", CommonUtils.createValueItem(room.getRoomName()));
            // 预约时间段 2022年04月15日 13:00~14:00
            SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
            SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
            String timeDateStr = sdf2.format(new Date(roomReservation.getReserveStartTime())) + "-" + sdf2.format(new Date(roomReservation.getReserveEndTime()))
                    + "时段，" + sdf.format(new Date(roomReservation.getReserveStartTime()));
            log.info("str length {}", timeDateStr.length());
            data.put("thing8", CommonUtils.createValueItem(timeDateStr));

            String reason = "";
            if (!StrUtil.isEmpty(roomReservation.getRemark())) {
                reason = roomReservation.getRemark().length() > 20 ?
                        roomReservation.getRemark().substring(0, 16) + "..." : roomReservation.getRemark();
            }
            data.put("thing4", CommonUtils.createValueItem(reason));
            log.info("ready send message to {}, message is {}", user.getName(), data);
            if (!StrUtil.isEmpty(user.getOpenId())) {
                WxUtils.sendSubscriptionMessage(user.getOpenId(), WxMessageTemplateId.WITHDRAW_NOTICE_TEMPLATE.getId(), data);
            }
        }
    },

    // 审核不通过
    ROOM_RESERVE_TO_BE_REJECTED {
        @Override
        public void sendReservationNoticeMessage(Room room, User user, RoomReservation roomReservation) {
            Map<String, Map<String, Object>> data = new HashMap<>();
            data.put("thing1", CommonUtils.createValueItem(room.getRoomName()));

            // 预约时间段 2022年04月15日 13:00~14:00
            SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm");
            SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
            String timeDateStr = sdf.format(new Date(roomReservation.getReserveStartTime())) + "~" + sdf2.format(new Date(roomReservation.getReserveEndTime()));
            data.put("date2", CommonUtils.createValueItem(timeDateStr));

            String reason = roomReservation.getRemark().length() > 20 ?
                    roomReservation.getRemark().substring(0, 16) + "..." : roomReservation.getRemark();
            data.put("thing17", CommonUtils.createValueItem(reason));
            log.info("ready send message to {}, message is {}", user.getName(), data);
            if (!StrUtil.isEmpty(user.getOpenId())) {
                WxUtils.sendSubscriptionMessage(user.getOpenId(), WxMessageTemplateId.APPLY_FAILED_NOTICE_TEMPLATE.getId(), data);
            }
        }
    },

    ROOM_RESERVE_IS_TIME_OUT {
        @Override
        public void sendReservationNoticeMessage(Room room, User user, RoomReservation roomReservation) {

        }
    };

    public abstract void sendReservationNoticeMessage(Room room, User user, RoomReservation roomReservation);

    public static ReservationState valueOf(int ordinal) {
        ReservationState[] values = ReservationState.values();
        if (ordinal >= values.length) {
            return null;
        } else {
            return values[ordinal];
        }
    }
}
