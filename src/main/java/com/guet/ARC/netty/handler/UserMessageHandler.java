package com.guet.ARC.netty.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.guet.ARC.common.enmu.SocketMsgType;
import com.guet.ARC.netty.manager.UserOnlineManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.ssl.NotSslRecordException;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * @author Yulf
 * Date 2024/6/11
 * socket连接成功后进行的处理
 */
@ChannelHandler.Sharable
@Component
@Slf4j
public class UserMessageHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        // 这里可以考虑使用不同的manager处理不同type的消息，将功能解耦
        JSONObject jsonObject = JSON.parseObject(msg.text());
        String type = jsonObject.getString("type");
        // 用户第一次进入app，发送的设备信息，通知其他监听的用户
        if (type.equals(SocketMsgType.DEVICE.getType())) {
            // 告知当前设备消息发送成功
            ctx.writeAndFlush(new TextWebSocketFrame("ok"));
            UserOnlineManager.addChannel(ctx.channel(), msg.text());
            UserOnlineManager.broadCastToOnlineUser();
        } else {
            // 用户发送的消息，通知对应的用户，如果在线的话
            UserOnlineManager.sendMessage(msg.text(), jsonObject.getString("toUserId"));
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
//        log.info("用户断开连接：{}", ctx.channel().attr(AttributeKey.valueOf("userId")).get());
        UserOnlineManager.removeChannel(ctx.channel());
        UserOnlineManager.broadCastToOnlineUser();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof NotSslRecordException) {
            // 处理 SSL 相关的异常
            log.error("SSL 请求异常，非https请求, userId: {}, message is {}", ctx.channel().attr(AttributeKey.valueOf("userId")).get(), cause.getMessage());
        } else if (cause instanceof IOException) {
            // 处理 I/O 异常
            log.error("I/O 异常：{}, userId: {}", cause.getMessage(), ctx.channel().attr(AttributeKey.valueOf("userId")).get());
        } else if (cause instanceof DecoderException) {

            log.error("DecoderException, userId: {}，已关闭. message is {}", ctx.channel().attr(AttributeKey.valueOf("userId")).get(), cause.getMessage());
        } else {
            // 处理其他异常
            log.error("未知异常：", cause);
        }
        // 关闭连接
        UserOnlineManager.removeChannel(ctx.channel());
        UserOnlineManager.broadCastToOnlineUser();
    }
}
