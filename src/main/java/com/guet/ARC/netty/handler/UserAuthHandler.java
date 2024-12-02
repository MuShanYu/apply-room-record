package com.guet.ARC.netty.handler;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.core.util.StrUtil;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;

/**
 * @author Yulf
 * Date 2024/12/2
 */
@ChannelHandler.Sharable
@Component
@Slf4j
public class UserAuthHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) {
        String token = String.valueOf(UrlQuery.of(msg.uri(), Charset.defaultCharset()).get("token"));
        String platform = String.valueOf(UrlQuery.of(msg.uri(), Charset.defaultCharset()).get("platform"));
        // 是否是授权访问socket
        String userId = (String) StpUtil.getLoginIdByToken(token);
        if (StrUtil.isEmpty(token) || StrUtil.isEmpty(userId)) {
            errorResponse(ctx);
            ctx.channel().close();
            return;
        }
        if (StrUtil.isEmpty(platform)) {
            log.warn("Platform param is invalid. Param is {}.", platform);
            errorResponse(ctx);
            ctx.channel().close();
            return;
        }
        // 信息记录流转到业务逻辑中
        ctx.channel().attr(AttributeKey.valueOf("userId")).set(userId);
        ctx.channel().attr(AttributeKey.valueOf("platform")).set(platform);
        ctx.fireChannelRead(msg.retain());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("UserAuthHandler exceptionCaught: {}", cause.getMessage());
        super.exceptionCaught(ctx, cause);
    }

    private void errorResponse(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UNAUTHORIZED);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
