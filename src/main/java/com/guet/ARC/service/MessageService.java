package com.guet.ARC.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.dao.MessageRepository;
import com.guet.ARC.dao.mybatis.MessageQueryRepository;
import com.guet.ARC.dao.mybatis.query.MessageQuery;
import com.guet.ARC.domain.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * Author: Yulf
 * Date: 2023/11/15
 */
@Service
public class MessageService {

    @Autowired
    private MessageQuery messageQuery;

    @Autowired
    private MessageQueryRepository messageQueryRepository;

    @Autowired
    private MessageRepository messageRepository;

    // 发送消息
    public void sendMessage(Message message) {
        long now = System.currentTimeMillis();
        String userId = StpUtil.getSession().getString("userId");
        message.setId(IdUtil.fastSimpleUUID());
        message.setUpdateTime(now);
        message.setCreateTime(now);
        message.setMessageSenderId(userId);
        messageRepository.saveAndFlush(message);
    }

    // 查询消息列表
    public PageInfo<Message> queryMyMessageList(Integer page, Integer size) {
        String userId = StpUtil.getSession().getString("userId");
        Page<Message> pageResult = messageRepository.findByMessageReceiverId(userId, PageRequest.of(page, size));
        return new PageInfo<>(pageResult);
    }

    // 删除消息
    public void deleteMessage(String messageId) {
        messageRepository.deleteById(messageId);
    }
}
