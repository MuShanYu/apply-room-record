package com.guet.ARC.controller;

import com.guet.ARC.common.anno.ResponseBodyResult;
import com.guet.ARC.domain.Message;
import com.guet.ARC.domain.enums.MessageType;
import com.guet.ARC.service.MessageService;
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

    @PostMapping("/message/send")
    @ApiOperation(value = "发送消息")
    public void sendMessage(@RequestBody Message message) {
        messageService.sendMessage(message);
    }

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

}
