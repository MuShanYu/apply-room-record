package com.guet.ARC.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.guet.ARC.common.domain.PageInfo;
import com.guet.ARC.dao.MessageRepository;
import com.guet.ARC.dao.UserRepository;
import com.guet.ARC.dao.mybatis.MessageQueryRepository;
import com.guet.ARC.domain.Message;
import com.guet.ARC.domain.User;
import com.guet.ARC.domain.enums.MessageType;
import com.guet.ARC.domain.enums.ReadState;
import com.guet.ARC.domain.vo.message.MessageVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    private UserRepository userRepository;

    @Autowired
    private MessageQueryRepository messageQueryRepository;

    // 发送消息
    public Message sendMessage(Message message) {
        long now = System.currentTimeMillis();
        String userId = StpUtil.getLoginIdAsString();
        message.setId(IdUtil.fastSimpleUUID());
        message.setUpdateTime(now);
        message.setCreateTime(now);
        message.setMessageSenderId(userId);
        if (StrUtil.isNotEmpty(message.getMessageReceiverId()) && message.getMessageReceiverId().equals(userId)) {
            // 自己给自己发送消息，置为已读
            message.setReadState(ReadState.READ);
        } else {
            message.setReadState(ReadState.UNREAD);
        }
        return messageRepository.save(message);
    }

    // 查询消息列表
    // 消息聚合处理
    public Map<String, Object> queryMyMessageList(Integer page, Integer size, MessageType messageType) {
        String userId = StpUtil.getLoginIdAsString();
        PageHelper.startPage(page, size);
        // 消息接收者是我，或者发送者是我，将这些id进行合并
        List<String> msgUserIds = messageQueryRepository.querySenderOrReceiverIds(userId, messageType.ordinal());
        List<MessageVo> messageVos = new ArrayList<>();
        for (String msgUserId : msgUserIds) {
            MessageVo messageVo = new MessageVo();
            // 我收到了sender的信息
            Message message = messageQueryRepository.queryLatestMsg(msgUserId, userId, messageType.ordinal());
            long notReadCount = messageRepository.countByMessageReceiverIdAndMessageSenderIdAndReadStateAndMessageType(userId, msgUserId, ReadState.UNREAD, messageType);
            BeanUtil.copyProperties(message, messageVo);
            messageVo.setNotReadCount(notReadCount);
            User user = userRepository.findByIdOrElseNull(msgUserId);
            messageVo.setSenderUserName(user.getName());
            messageVo.setStuNum(user.getStuNum());
            messageVos.add(messageVo);
        }
        Map<String, Object> res = new HashMap<>();
        long todoCount = messageRepository.countByMessageReceiverIdAndReadStateAndMessageType(userId, ReadState.UNREAD, MessageType.TODO);
        long resultCount = messageRepository.countByMessageReceiverIdAndReadStateAndMessageType(userId, ReadState.UNREAD, MessageType.RESULT);
        long sysCount = messageRepository.countByMessageReceiverIdAndReadStateAndMessageType(userId, ReadState.UNREAD, MessageType.SYSTEM);
        messageVos.sort((a, b) -> (int)(b.getCreateTime() - a.getCreateTime()));
        PageInfo<MessageVo> pageInfo = new PageInfo<>();
        pageInfo.setPage(page);
        pageInfo.setTotalSize((long)messageVos.size());
        pageInfo.setPageData(messageVos);
        res.put("pageInfo", pageInfo);
        res.put("todoCount", todoCount);
        res.put("resultCount", resultCount);
        res.put("sysCount", sysCount);
        return res;
    }

    // 删除消息
    public void deleteMessage(String messageId) {
        messageRepository.deleteById(messageId);
    }

    public void setMessageToRead(List<String> messageIds) {
        messageRepository.updateReadStateByIds(messageIds);
    }

    public PageInfo<Message> queryMessageBySenderId(String senderId, String receiverId, MessageType type, Integer page, Integer size) {
        String curUserId = StpUtil.getLoginIdAsString();
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by("createTime").descending());
        // 查询我收到的消息，以及我发送的消息
        Page<Message> receiverIsMe = messageRepository.findByMessageSenderIdAndMessageReceiverIdAndMessageType(senderId, receiverId, type, pageRequest);
        Page<Message> senderIsMe = messageRepository.findByMessageSenderIdAndMessageReceiverIdAndMessageType(receiverId, senderId, type, pageRequest);
        // 合并消息
        List<Message> res = new ArrayList<>();
        res.addAll(receiverIsMe.getContent());
        res.addAll(senderIsMe.getContent());
        // res中去除id相同的
        Set<String> idSet = new HashSet<>();
        res.removeIf(message -> !idSet.add(message.getId()));
        res.sort((a, b) -> (int)(b.getCreateTime() - a.getCreateTime()));
        PageInfo<Message> pageInfo = new PageInfo<>();
        pageInfo.setPageData(res);
        pageInfo.setTotalSize((long)res.size());
        pageInfo.setPage(page);
        return pageInfo;
    }
}
