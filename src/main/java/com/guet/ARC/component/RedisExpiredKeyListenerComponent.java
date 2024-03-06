package com.guet.ARC.component;

import cn.hutool.core.util.StrUtil;
import com.guet.ARC.dao.UserRepository;
import com.guet.ARC.domain.User;
import com.guet.ARC.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

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
        }
    }

}
