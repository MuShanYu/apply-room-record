package com.guet.ARC.component;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.guet.ARC.domain.Message;
import com.guet.ARC.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: Yulf
 * Date: 2023/11/16
 */
@Service
@ServerEndpoint("/websocket/{userId}")
@Slf4j
public class MessageWebSocketService {
    private ConcurrentHashMap<String, Session> clientsMap = new ConcurrentHashMap<>();

    @Autowired
    private MessageService messageService;

    private String userId;

    @OnOpen
    public void messageOnOpen(Session session, @PathParam("userId") String userId) {
        log.info("用户{}连接成功", userId);
        // 用户连接后，保存下userId和session方便发送给指定用户信息
        clientsMap.put(userId, session);
        this.userId = userId;
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (!StrUtil.isEmpty(userId)) {
            clientsMap.remove(userId);
        }
    }

    /**
     * 收到客户端消息后调用的方法
     * @ Param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String messageJson, Session session) {
        // message的json字符串
        // 接收到了某人的申请或者房价预约动作
        Message message = JSON.parseObject(messageJson, Message.class);
        // 将消息发送给指定的审批人
        messageService.sendMessage(message); // 保存
        // 发送
        sendMessageTo(messageJson, message.getMessageReceiverId());

    }

    /**
     * @ Param session
     * @ Param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误", error);
    }

    private void sendMessageTo(String message, String targetUserId) {
        if (clientsMap.containsKey(targetUserId)) {
            clientsMap.get(targetUserId).getAsyncRemote().sendText(message);
        }
    }
}
