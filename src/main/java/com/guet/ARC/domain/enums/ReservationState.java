package com.guet.ARC.domain.enums;

import com.guet.ARC.common.enmu.WxMessageTemplateId;
import com.guet.ARC.domain.Room;
import com.guet.ARC.domain.RoomReservation;
import com.guet.ARC.domain.User;
import com.guet.ARC.util.CommonUtils;
import com.guet.ARC.util.WxUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: Yulf
 * Date: 2023/11/22
 */
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
            // 时分秒
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
            data.put("time22", CommonUtils.createValueItem(sdf.format(new Date(roomReservation.getReserveStartTime()))));
            data.put("time23", CommonUtils.createValueItem(sdf.format(new Date(roomReservation.getReserveEndTime()))));
            // 预约理由
            String mem = roomReservation.getRoomUsage().length() > 20 ? roomReservation.getRoomId().substring(0, 20) : roomReservation.getRoomUsage();
            data.put("thing7", CommonUtils.createValueItem(mem));
            // 发送
            WxUtils.sendSubscriptionMessage(user.getOpenId(), WxMessageTemplateId.APPLY_NOTICE_TEMPLATE.getId(), data);
        }
    },
    // 已审核
    ROOM_RESERVE_ALREADY_REVIEWED {
        @Override
        public void sendReservationNoticeMessage(Room room, User user, RoomReservation roomReservation) {
            Map<String, Map<String, Object>> data = new HashMap<>();
            // 处理姓名，wx不可超过五个字符
            String name = user.getName().length() > 5 ? user.getName().substring(0, 5) : user.getName();
            data.put("name5", CommonUtils.createValueItem(name));
            // 时分秒
            data.put("thing2", CommonUtils.createValueItem(room.getRoomName()));
            data.put("phrase1", CommonUtils.createValueItem("审核通过"));
            // 发送
            WxUtils.sendSubscriptionMessage(user.getOpenId(), WxMessageTemplateId.APPLY_RESULT_NOTICE_TEMPLATE.getId(), data);
        }
    },

    // 用户取消
    ROOM_RESERVE_CANCELED {
        @Override
        public void sendReservationNoticeMessage(Room room, User user, RoomReservation roomReservation) {
            Map<String, Map<String, Object>> data = new HashMap<>();
            // 处理姓名，wx不可超过五个字符
            String name = user.getName().length() > 5 ? user.getName().substring(0, 5) : user.getName();
            data.put("name1", CommonUtils.createValueItem(name));
            // 时分秒
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
            data.put("time4", CommonUtils.createValueItem(sdf.format(new Date(roomReservation.getUpdateTime()))));
            String reason = "取消预约房间" + room.getRoomName();
            data.put("phrase3", CommonUtils.createValueItem(reason));
            // 发送
            WxUtils.sendSubscriptionMessage(user.getOpenId(), WxMessageTemplateId.WITHDRAW_NOTICE_TEMPLATE.getId(), data);
        }
    },

    // 审核不通过
    ROOM_RESERVE_TO_BE_REJECTED {
        @Override
        public void sendReservationNoticeMessage(Room room, User user, RoomReservation roomReservation) {
            Map<String, Map<String, Object>> data = new HashMap<>();
            // 处理姓名，wx不可超过五个字符
            String name = user.getName().length() > 5 ? user.getName().substring(0, 5) : user.getName();
            data.put("name5", CommonUtils.createValueItem(name));
            // 时分秒
            data.put("thing2", CommonUtils.createValueItem(room.getRoomName()));
            data.put("phrase1", CommonUtils.createValueItem("审核不通过，拒绝原因已发放至您的邮箱"));
            // 发送
            WxUtils.sendSubscriptionMessage(user.getOpenId(), WxMessageTemplateId.APPLY_RESULT_NOTICE_TEMPLATE.getId(), data);
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
