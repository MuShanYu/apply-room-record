package com.guet.ARC.netty.handler;

import com.guet.ARC.netty.manager.UserOnlineManager;
import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.ssl.NotSslRecordException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;


/**
 * @author Yulf
 * Date 2024/6/6
 */
@ChannelHandler.Sharable
@Component
@Slf4j
public class UserOnlineHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        UserOnlineManager.broadCastToOnlineUser();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
//        log.info("用户断开连接：{}", ctx.channel().attr(AttributeKey.valueOf("userId")).get());
        UserOnlineManager.removeChannel(ctx.channel());
        UserOnlineManager.broadCastToOnlineUser();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof NotSslRecordException) {
            // 处理 SSL 相关的异常
            log.error("SSL 请求异常，非https请求, 异常请求channel: {}", ctx.channel());
        } else if (cause instanceof IOException) {
            // 处理 I/O 异常
            log.error("I/O 异常：{}", cause.getMessage());
        } else {
            // 处理其他异常
            log.error("未知异常：", cause);
        }
        // 关闭连接
        UserOnlineManager.removeChannel(ctx.channel());
        UserOnlineManager.broadCastToOnlineUser();
    }
}
