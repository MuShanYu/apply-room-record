package com.guet.ARC.netty.handler;

import com.guet.ARC.netty.manager.UserOnlineManager;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLHandshakeException;

/**
 * @author Yulf
 * Date 2024/12/6
 */
@Component
@ChannelHandler.Sharable
@Slf4j
public class HeartBeatCheckHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        if (msg.text().equalsIgnoreCase("ping")) {
            ctx.writeAndFlush(new TextWebSocketFrame("pong"));
        } else {
            ctx.fireChannelRead(msg.retain());
        }
    }

    // 由上层IdleStateHandler
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt); // 这里也可以不传播到下面的handler
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                // 服务端10s没有收到客户端的心跳消息了，或者其他消息（用户发送的信息），服务端主动断开连接
                log.warn("客户端异常退出，60s未收到心跳消息，服务端主动断开连接。userId: {}", ctx.channel().attr(AttributeKey.valueOf("userId")).get());
                // 可能连接过了，移除信息
                UserOnlineManager.removeChannel(ctx.channel());
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof SSLHandshakeException) {
            log.error("SSL 握手异常，用户id：{}, message is {}", ctx.channel().attr(AttributeKey.valueOf("userId")).get(), cause.getMessage());
        } else {
            ctx.fireExceptionCaught(cause);
        }
    }
}
