package com.guet.ARC.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.extra.cglib.CglibUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.common.domain.ResultCode;
import com.guet.ARC.common.exception.AlertException;
import com.guet.ARC.dao.MessageRepository;
import com.guet.ARC.dao.mybatis.MessageQueryRepository;
import com.guet.ARC.dao.mybatis.query.MessageQuery;
import com.guet.ARC.domain.Message;
import com.guet.ARC.domain.enums.MessageType;
import com.guet.ARC.domain.enums.ReadState;
import com.guet.ARC.domain.vo.message.MessageVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Author: Yulf
 * Date: 2023/11/15
 */
@Service
@Slf4j
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageQueryRepository messageQueryRepository;

    @Autowired
    private MessageQuery messageQuery;

    // 发送消息
    public void sendMessage(Message message) {
        long now = System.currentTimeMillis();
        String userId = StpUtil.getLoginIdAsString();
        message.setId(IdUtil.fastSimpleUUID());
        message.setUpdateTime(now);
        message.setCreateTime(now);
        message.setMessageSenderId(userId);
        messageRepository.save(message);
    }

    // 查询消息列表
    public Map<String, Object> queryMyMessageList(Integer page, Integer size, MessageType messageType) {
        String userId = StpUtil.getLoginIdAsString();
        Page<MessageVo> pageResult = PageHelper.startPage(page, size);
        messageQueryRepository.selectMany(messageQuery.queryMessageListSql(userId, messageType));
        PageInfo<MessageVo> pageInfo = new PageInfo<>(pageResult);
        Map<String, Object> res = new HashMap<>();
        long todoCount = messageRepository.countByMessageReceiverIdAndReadStateAndMessageType(userId, ReadState.UNREAD, MessageType.TODO);
        long resultCount = messageRepository.countByMessageReceiverIdAndReadStateAndMessageType(userId, ReadState.UNREAD, MessageType.RESULT);
        res.put("pageInfo", pageInfo);
        res.put("todoCount", todoCount);
        res.put("resultCount", resultCount);
        return res;
    }

    // 删除消息
    public void deleteMessage(String messageId) {
        messageRepository.deleteById(messageId);
    }

    public void setMessageToRead(String messageId) {
        Message message = CglibUtil.copy(messageRepository.findByIdOrElseNull(messageId), Message.class);
        message.setReadState(ReadState.READ);
        message.setUpdateTime(System.currentTimeMillis());
        messageRepository.save(message);
    }
}
