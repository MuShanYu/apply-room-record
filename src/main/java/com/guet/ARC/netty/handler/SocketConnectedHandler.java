package com.guet.ARC.netty.handler;

import cn.hutool.core.util.StrUtil;
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
public class SocketConnectedHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        log.info("收到用户消息：{}", msg);
        if (msg.text().equalsIgnoreCase("ping")) {
            ctx.writeAndFlush(new TextWebSocketFrame("pong"));
        }
        // 这里可以考虑使用不同的manager处理不同type的消息，将功能解耦
        if (StrUtil.isNotEmpty(msg.text()) && !"ping".equalsIgnoreCase(msg.text())) {
            UserOnlineManager.addChannel(ctx.channel(), msg.text());
            log.info("用户连接成功：{}", msg.text());
            JSONObject jsonObject = JSON.parseObject(msg.text());
            String type = jsonObject.getString("type");
            // 用户第一次进入app，发送的设备信息，通知其他监听的用户
            if (type.equals(SocketMsgType.DEVICE.getType())) {
                // 告知当前设备消息发送成功
                ctx.writeAndFlush(new TextWebSocketFrame("ok"));
                UserOnlineManager.broadCastToOnlineUser();
            } else {
                // 用户发送的消息，通知对应的用户，如果在线的话
                UserOnlineManager.sendMessage(msg.text(), jsonObject.getString("toUserId"));
            }
        }
    }

    /**
     * 握手成功后，钩子回调函数，WebSocketServerProtocolHandler会传播两次该事件
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (ctx.pipeline().get(UserAuthHandler.class) != null) {
            ctx.pipeline().remove(UserAuthHandler.class);
        }
        super.userEventTriggered(ctx, evt);
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
            log.error("SSL 请求异常，非https请求, 异常请求channel: {}, message is {}", ctx.channel(), cause.getMessage());
        } else if (cause instanceof IOException) {
            // 处理 I/O 异常
            log.error("I/O 异常：{}", cause.getMessage());
        } else if (cause instanceof DecoderException) {
            log.error("请求解码异常, 异常请求channel: {}，已关闭. message is {}", ctx.channel(), cause.getMessage());
        } else {
            // 处理其他异常
            log.error("未知异常：", cause);
        }
        // 关闭连接
        UserOnlineManager.removeChannel(ctx.channel());
        UserOnlineManager.broadCastToOnlineUser();
    }
}
