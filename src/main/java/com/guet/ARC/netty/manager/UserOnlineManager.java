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
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Yulf
 * Date 2024/6/11
 */
@Component
@Slf4j
public class UserOnlineManager {

    private static ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

    private static final ConcurrentMap<Channel, String> channelToSource = new ConcurrentHashMap<>();

    private static final ConcurrentMap<String, List<String>> userIdToSources = new ConcurrentHashMap<>();

    private static final ConcurrentMap<Channel, String> channelToPlatform =  new ConcurrentHashMap<>();

    public static void addChannel(Channel channel, String source) {
        String userId = String.valueOf(channel.attr(AttributeKey.valueOf("userId")).get());
        String platform = String.valueOf(channel.attr(AttributeKey.valueOf("platform")).get());
        channelToPlatform.put(channel, platform);
        channelToSource.put(channel, source);
        List<String> sources = userIdToSources.getOrDefault(userId, new ArrayList<>());
        sources.add(source);
//        log.info("连接设备{}", sources);
        userIdToSources.put(userId, sources);
    }

    public static void removeChannel(Channel channel) {
        try {
            lock.writeLock().lock();
            channel.close();
            String source = channelToSource.getOrDefault(channel, null);
            channelToPlatform.remove(channel); // 移除连接平台信息
            if (StrUtil.isNotEmpty(source)) {
                String userId = String.valueOf(channel.attr(AttributeKey.valueOf("userId")).get());
                log.info("移除用户连接{}", userId);
                List<String> sources = userIdToSources.getOrDefault(userId, new ArrayList<>());
                // 删除
                sources.remove(source);
                channelToSource.remove(channel);
                // 完全下线
                if (sources.isEmpty()) {
                    userIdToSources.remove(userId);
                }
            }
        } finally {
            lock.writeLock().unlock();
        }

    }

    public static ConcurrentMap<String, List<String>> getOnlineUserIdToSources() {
        return userIdToSources;
    }

    public static void broadCastToOnlineUser() {
        try {
            lock.readLock().lock();
            Set<Channel> channels = channelToSource.keySet();
            for (Channel channel : channels) {
                String platform = channelToPlatform.get(channel);
                // web管理的需要通知有新用户并展示
                if ("web".equals(platform)) {
                    if (channel.isActive() && channel.isOpen() && channel.isWritable() && channel.isRegistered()) {
                        log.info("转发给用户{}更新信息", channel.attr(AttributeKey.valueOf("userId")).get());
                        channel.writeAndFlush(new TextWebSocketFrame(IdUtil.fastSimpleUUID()));
                    } else {
                        //log.info("remove channel: {}", channel.id());
                        removeChannel(channel);
                    }
                }
            }
        } finally {
            lock.readLock().unlock();
        }
    }
}
