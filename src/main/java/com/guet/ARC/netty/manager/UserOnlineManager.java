package com.guet.ARC.netty.manager;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Yulf
 * Date 2024/6/11
 */
@Component
@Slf4j
public class UserOnlineManager {

    private static final AttributeKey<String> USER_ID_KEY = AttributeKey.valueOf("userId");

    private static final AttributeKey<String> PLATFORM_KEY = AttributeKey.valueOf("platform");

    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

    private static final ConcurrentMap<Channel, String> channelToSource = new ConcurrentHashMap<>();

    private static final ConcurrentMap<String, ConcurrentLinkedQueue<String>> userIdToSources = new ConcurrentHashMap<>();

    private static final ConcurrentMap<Channel, String> channelToPlatform =  new ConcurrentHashMap<>();

    private static final ConcurrentMap<String, Channel> userIdToChannel =  new ConcurrentHashMap<>();

    public static void addChannel(Channel channel, String source) {
        String userId = channel.attr(USER_ID_KEY).get();
        String platform = channel.attr(PLATFORM_KEY).get();
        if ("wx".equals(platform)) {
            userIdToChannel.put(userId, channel);
        }
        channelToPlatform.put(channel, platform);
        channelToSource.put(channel, source);
        // CopyOnWriteArrayList 线程安全
        userIdToSources.computeIfAbsent(userId, k -> new ConcurrentLinkedQueue<>()).add(source);
    }

    public static void removeChannel(Channel channel) {
        String userId = channel.attr(USER_ID_KEY).get();
        try {
            lock.writeLock().lock();
            if (channelToSource.containsKey(channel)) { // 避免重复移除
                channel.close();
                String source = channelToSource.remove(channel);
                channelToPlatform.remove(channel);
                userIdToChannel.remove(userId);
                ConcurrentLinkedQueue<String> sources = userIdToSources.getOrDefault(userId, new ConcurrentLinkedQueue<>());
                sources.remove(source);
                if (sources.isEmpty()) {
                    userIdToSources.remove(userId);
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static ConcurrentMap<String, ConcurrentLinkedQueue<String>> getOnlineUserIdToSources() {
        return userIdToSources;
    }

    public static void broadCastToOnlineUser() {
        try {
            lock.readLock().lock();
            for (Channel channel : channelToSource.keySet()) {
                String platform = channelToPlatform.get(channel);
                if ("web".equals(platform)) {
                    if (channel.isActive() && channel.isWritable()) {
                        // 向在线用户发送消息
                        channel.writeAndFlush(new TextWebSocketFrame(IdUtil.fastSimpleUUID()));
                    } else {
                        // 异步删除失效的通道
                        removeChannel(channel);
                    }
                }
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    public static void sendMessage(String message, String toUserId) {
        if (StrUtil.isEmpty(toUserId) || StrUtil.isEmpty(message)) {
            return;
        }
        Channel channel = userIdToChannel.get(toUserId);
        if (channel != null) {
            if (channel.isActive() && channel.isWritable()) {
                channel.writeAndFlush(new TextWebSocketFrame(message));
            } else {
                log.warn("发送消息失败，Channel 状态无效，移除用户连接。toUserId: {}", toUserId);
                removeChannel(channel);
            }
        }
    }
}
