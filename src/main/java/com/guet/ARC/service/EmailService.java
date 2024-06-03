package com.guet.ARC.service;

import cn.hutool.extra.mail.MailUtil;
import com.guet.ARC.common.enmu.RedisCacheKey;
import com.guet.ARC.util.CommonUtils;
import com.guet.ARC.util.RedisCacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private RedisCacheUtil<String> redisCacheUtil;

    @Value("${spring.mail.username}")
    private String username;

    /**
     * 简单文本邮件
     *
     * @param to      收件人
     * @param subject 主题
     * @param content 内容
     */
    @Async
    public void sendSimpleMail(String to, String subject, String content) {
        try {
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
            if (CommonUtils.isValidMail(to)) {
                javaMailSender.send(message);
            }
        } catch (Exception e) {
            log.error("mail send failed. the mail is {}, and the error message is {}", to, e.getMessage());
            // 构建重发对象
            redisCacheUtil.pushDataToCacheList(RedisCacheKey.MAIL_RESEND_KEY.getKey(), buildSimpleMailSendJsonString(to, subject, content));
        }
    }

    /**
     * html邮件
     *
     * @param to      收件人,多个时参数形式 ："xxx@xxx.com,xxx@xxx.com,xxx@xxx.com"
     * @param subject 主题
     * @param content 内容
     */
    @Async
    public void sendHtmlMail(String to, String subject, String content) {
        //获取MimeMessage对象
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper;
        try {
            messageHelper = new MimeMessageHelper(message, true);
            //邮件发送人
            messageHelper.setFrom(username);
            //邮件接收人,设置多个收件人地址
            InternetAddress[] internetAddressTo = InternetAddress.parse(to);
            messageHelper.setTo(internetAddressTo);
            //messageHelper.setTo(to);
            //邮件主题
            message.setSubject(subject);
            //邮件内容，html格式
            messageHelper.setText(content, true);
            //发送
            javaMailSender.send(message);
        } catch (Exception e) {
            // 记录发送失败，添加发送任务重发
            log.error("mail send failed. the mail is {}, and the error is", to, e);
            redisCacheUtil.pushDataToCacheList(RedisCacheKey.MAIL_RESEND_KEY.getKey(), buildSimpleMailSendJsonString(to, subject, content));
        }
    }

    /**
     * 带附件的邮件
     *
     * @param to       收件人
     * @param subject  主题
     * @param content  内容
     * @param filePath 附件
     */
    public void sendAttachmentsMail(String to, String subject, String content, String filePath) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(username);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            FileSystemResource file = new FileSystemResource(new File(filePath));
            String fileName = filePath.substring(filePath.lastIndexOf(File.separator));
            helper.addAttachment(fileName, file);
            javaMailSender.send(message);
        } catch (Exception e) {
            log.error("mail send failed. the mail is {}, and the error is", to, e);
            redisCacheUtil.pushDataToCacheList(RedisCacheKey.MAIL_RESEND_KEY.getKey(), buildSimpleMailSendJsonString(to, subject, content));
        }
    }

    private String buildSimpleMailSendJsonString(String to, String subject, String content) {
        return "{" +
                "\"to\":" + "\"" +
                to + "\"" +
                ",\"subject\":" + "\"" +
                subject + "\"" +
                ",\"content\":" + "\"" +
                content + "\"" +
                "}";
    }

}
