package com.guet.ARC.config;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Yulf
 * Date 2024/6/7
 */
@Configuration
public class NettyConfig {

    @Bean("onlineUserCountChannelGroup")
    public ChannelGroup onlineUserCountChannelGroup() {
        return new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }

    @Bean("onlineUserQueryChannelGroup")
    public ChannelGroup onlineUserQueryChannelGroup() {
        return new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }

}
