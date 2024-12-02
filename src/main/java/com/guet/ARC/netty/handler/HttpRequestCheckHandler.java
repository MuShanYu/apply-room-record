package com.guet.ARC.netty.handler;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Yulf
 * Date 2024/12/2
 */
@ChannelHandler.Sharable
@Component
@Slf4j
public class HttpRequestCheckHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) {
        // 检查协议
        // 如果请求的协议版本不是 HTTPS，直接返回 400 错误并关闭连接
        if (msg.protocolVersion() != HttpVersion.HTTP_1_1) {
            ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            ctx.close();
            return;
        }
        // 非websocket请求直接关闭连接
        if (!"websocket".equalsIgnoreCase(msg.headers().get(HttpHeaderNames.UPGRADE))) {
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
            ctx.close();
            return;
        }
        ctx.fireChannelRead(msg.retain());
    }
}
