package com.guet.ARC.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.dao.MessageRepository;
import com.guet.ARC.dao.mybatis.MessageQueryRepository;
import com.guet.ARC.dao.mybatis.query.MessageQuery;
import com.guet.ARC.domain.Message;
import com.guet.ARC.domain.vo.message.MessageVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

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
        String userId = StpUtil.getSession().getString("userId");
        message.setId(IdUtil.fastSimpleUUID());
        message.setUpdateTime(now);
        message.setCreateTime(now);
        message.setMessageSenderId(userId);
        messageRepository.saveAndFlush(message);
    }

    // 查询消息列表
    public PageInfo<MessageVo> queryMyMessageList(Integer page, Integer size) {
        String userId = StpUtil.getSession().getString("userId");
        Page<MessageVo> pageResult = PageHelper.startPage(page, size);
        List<MessageVo> messageVos = messageQueryRepository.selectMany(messageQuery.queryMessageListSql(userId));
        return new PageInfo<>(pageResult);
    }

    // 删除消息
    public void deleteMessage(String messageId) {
        messageRepository.deleteById(messageId);
    }
}
