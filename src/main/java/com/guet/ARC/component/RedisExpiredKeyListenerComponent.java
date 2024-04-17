package com.guet.ARC.component;

import cn.hutool.core.util.StrUtil;
import com.guet.ARC.common.enmu.RedisCacheKey;
import com.guet.ARC.dao.RoomRepository;
import com.guet.ARC.dao.RoomReservationRepository;
import com.guet.ARC.dao.UserRepository;
import com.guet.ARC.domain.User;
import com.guet.ARC.domain.enums.ReservationState;
import com.guet.ARC.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

/**
 * @author Yulf
 * Date 2024/3/6
 */
@Component
public class RedisExpiredKeyListenerComponent extends KeyExpirationEventMessageListener {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomReservationRepository roomReservationRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private EmailService emailService;

    // 通过构造函数注入 RedisMessageListenerContainer 给 KeyExpirationEventMessageListener
    public RedisExpiredKeyListenerComponent(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    protected void doRegister(RedisMessageListenerContainer listenerContainer) {
        listenerContainer.addMessageListener(this, new PatternTopic("__keyevent@0__:expired"));
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        SimpleDateFormat sdfFull = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
        if (expiredKey.startsWith("access_record_key:user_id:")) {
            // 获取userID
            String userId = expiredKey.substring("access_record_key:user_id:".length());
            // 根据userId查询用户信息
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                if (!StrUtil.isEmpty(user.getMail())) {
                    // 发送提醒邮件
                    String content = "您的房间签到状态已过期，系统已将您正在签到的房间移除，本次签到将不记录签到时长，请您尽快登录小程序查看。" +
                            "如需记录签到时长，请在本周内提交补卡申请，并联系相应房间负责人或管理同学进行审核。本邮件由系统发出，请勿回复！";
                    emailService.sendSimpleMail(user.getMail(), "房间签到状态过期提醒", content);
                }
            }
        } else if (expiredKey.startsWith(RedisCacheKey.ROOM_OCCUPANCY_APPLY_KEY.getKey())) {
            String roomReservationId = expiredKey.substring(RedisCacheKey.ROOM_OCCUPANCY_APPLY_KEY.getKey().length());
            roomReservationRepository.findById(roomReservationId).ifPresent(roomReservation -> {
                // 判断申请是否已经被处理
                if (ReservationState.ROOM_RESERVE_TO_BE_REVIEWED.equals(roomReservation.getState())) {
                    // 没有被处理，超期还没有审核，标记为超时未处理，并通知申请人
                    roomReservation.setState(ReservationState.ROOM_RESERVE_IS_TIME_OUT);
                    roomReservationRepository.save(roomReservation);
                    roomRepository.findById(roomReservation.getRoomId()).ifPresent(room -> {
                        // 查询用户信息
                        userRepository.findById(roomReservation.getUserId()).ifPresent(user -> {

                            String startTimeStr = sdfFull.format(new Date(roomReservation.getReserveStartTime()));
                            String endTimeStr = sdfTime.format(new Date(roomReservation.getReserveEndTime()));
                            // 发送邮件通知
                            String content = "您预约的房间" + room.getRoomName() + "，预约时间" + startTimeStr + "-" + endTimeStr + "，房间负责人超时未处理，请您登录小程序查看房间预约状态。为不耽误您的行程安排，您可以尝试重新预约其他时间段并联系房间负责人进行审核。本邮件由系统发出，请勿回复！";
                            emailService.sendSimpleMail(user.getMail(), "房间预约超时未处理通知", content);
                        });
                    });
                }
            });
        } else if (expiredKey.startsWith(RedisCacheKey.ROOM_APPLY_TIMEOUT_NOTIFY_KEY.getKey())) {
            String roomReservationId = expiredKey.substring(RedisCacheKey.ROOM_APPLY_TIMEOUT_NOTIFY_KEY.getKey().length());
            roomReservationRepository.findById(roomReservationId).ifPresent(roomReservation -> {
                // 未审核，提醒审核人审核，提前一个小时
                if (ReservationState.ROOM_RESERVE_TO_BE_REVIEWED.equals(roomReservation.getState())) {
                    roomRepository.findById(roomReservation.getRoomId()).ifPresent(room -> {
                        // 查询用户信息
                        userRepository.findById(room.getChargePersonId()).ifPresent(user -> {
                            String startTimeStr = sdfFull.format(new Date(roomReservation.getReserveStartTime()));
                            String endTimeStr = sdfTime.format(new Date(roomReservation.getReserveEndTime()));
                            // 发送邮件通知
                            String content = "用户所预约的房间" + room.getRoomName() + "，预约时间" + startTimeStr + "-" + endTimeStr + "，该申请即将到达用户所预约的起始时间，为不影响申请人行程，请您及时处理。本邮件由系统发出，请勿回复！";
                            emailService.sendSimpleMail(user.getMail(), "房间预约申请未审核通知", content);
                        });
                    });
                }
            });
        }
    }

}
