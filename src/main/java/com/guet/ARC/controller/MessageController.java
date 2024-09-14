package com.guet.ARC.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.bean.BeanUtil;
import com.guet.ARC.common.anno.ResponseBodyResult;
import com.guet.ARC.domain.Message;
import com.guet.ARC.domain.User;
import com.guet.ARC.domain.dto.message.MessageDTO;
import com.guet.ARC.domain.enums.MessageType;
import com.guet.ARC.service.EmailService;
import com.guet.ARC.service.MessageService;
import com.guet.ARC.service.UserService;
import com.guet.ARC.util.AsyncRunUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Author: Yulf
 * Date: 2023/11/15
 */
@Api(tags = "消息模块")
@RestController
@ResponseBodyResult
@Validated
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @DeleteMapping("/message/{messageId}")
    @ApiOperation(value = "删除消息")
    public void deleteMessage(@PathVariable("messageId") String messageId) {
        messageService.deleteMessage(messageId);
    }

    @GetMapping("/message/list")
    @ApiOperation(value = "查询我的消息列表")
    public Map<String, Object> queryMyMessageList(@RequestParam("page") Integer page,
                                                  @RequestParam("size") Integer size,
                                                  @RequestParam("messageType")MessageType messageType) {
        return messageService.queryMyMessageList(page, size, messageType);
    }

    @PutMapping("/message/{messageId}")
    @ApiOperation(value = "将消息设为已读")
    public void setMessageToReadApi(@PathVariable("messageId") String messageId) {
        messageService.setMessageToRead(messageId);
    }

    @PostMapping("/message/add")
    @ApiOperation(value = "发送消息")
    @SaCheckPermission(value = {"system:user:sendMsg"})
    public void sendMessage(@RequestBody MessageDTO messageDTO) {
        for (Integer type : messageDTO.getSendType()) {
            handleSendMessage(messageDTO, type);
        }
    }

    private void handleSendMessage(MessageDTO dto, Integer sendType) {
        switch (sendType) {
            case 0:
                messageService.sendMessage(BeanUtil.copyProperties(dto, Message.class, "sendType"));
                break;
            case 1:
                AsyncRunUtil.getInstance().submit(() -> {
                    User user = userService.findUserById(dto.getMessageReceiverId());
                    emailService.sendSimpleMail(user.getMail(), "系统通知", dto.getContent());
                });
                break;
        }
    }

}
