package com.guet.ARC.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.guet.ARC.common.enmu.RedisCacheKey;
import com.guet.ARC.util.CommonUtils;
import com.guet.ARC.util.RedisCacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Yulf
 * Date 2024/4/18
 */
@Slf4j
@Component
public class MailResendJob {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String username;

    @Autowired
    private RedisCacheUtil<String> redisCacheUtil;

    // 每五分钟检查一下
    @Scheduled(cron = "0 */5 * ? * *")
    public void execute() {
        List<String> jsonStringData = redisCacheUtil.getPopCacheList(RedisCacheKey.MAIL_RESEND_KEY.getKey());
        System.out.println(jsonStringData);
        if (!jsonStringData.isEmpty()) {
            for (String jsonString : jsonStringData) {
                JSONObject jsonObject = JSON.parseObject(jsonString);
                String to = jsonObject.getString("to");
                String subject = jsonObject.getString("subject");
                String content = jsonObject.getString("content");
                try {
                    if (CommonUtils.isValidMail(to)) {
                        sendSimpleMail(to, subject, content);
                    }
                } catch (Exception e) {
                    log.error("mail {} resend failed. sys stop send. the error message is {}.", to, e.getMessage());
                }
            }
        }
    }

    @Async
    public void sendSimpleMail(String to, String subject, String content) {
        //创建SimpleMailMessage对象
        SimpleMailMessage message = new SimpleMailMessage();
        //邮件发送人
        message.setFrom(username);
        //邮件接收人
        message.setTo(to);
        //邮件主题
        message.setSubject(subject);
        //邮件内容
        message.setText(content);
        //发送邮件
        javaMailSender.send(message);
    }
}
