package com.guet.ARC.netty.handler;

import com.guet.ARC.netty.manager.UserOnlineManager;
import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


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
        UserOnlineManager.broadCastToOnlineUser(ctx.channel());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        UserOnlineManager.removeChannel(ctx.channel());
        UserOnlineManager.broadCastToOnlineUser(ctx.channel());
        super.channelUnregistered(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        UserOnlineManager.removeChannel(ctx.channel());
        UserOnlineManager.broadCastToOnlineUser(ctx.channel());
        super.exceptionCaught(ctx, cause);
    }


}
