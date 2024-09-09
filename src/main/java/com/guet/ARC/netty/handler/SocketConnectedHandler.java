package com.guet.ARC.netty.handler;

import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.core.util.StrUtil;
import com.guet.ARC.dao.UserRepository;
import com.guet.ARC.netty.manager.UserOnlineManager;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;


/**
 * @author Yulf
 * Date 2024/6/11
 */
@ChannelHandler.Sharable
@Component
@Slf4j
public class SocketConnectedHandler extends SimpleChannelInboundHandler<Object> {

    @Value("${netty.websocket.url}")
    private String url;

    @Autowired
    private UserRepository userRepository;

    private WebSocketServerHandshaker handShaker;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            log.info("处理连接");
            handleConnected(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            // 业务逻辑
            log.info("处理消息");
            handleMessage(ctx, (WebSocketFrame) msg);
        }
    }

    private void handleConnected(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        if (!request.decoderResult().isSuccess()
                || !"websocket".equalsIgnoreCase(request.headers().get(HttpHeaderNames.UPGRADE))
                || !request.headers().contains(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE, true)) {
            log.warn("Invalid WebSocket handshake request. Request headers: {}", request.headers());
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            return;
        }
        String userId = String.valueOf(UrlQuery.of(request.uri(), Charset.defaultCharset()).get("userId"));
        if (StrUtil.isEmpty(userId)) {
            log.warn("Unauthorized access. Address is {}.", ctx.channel().remoteAddress());
            ctx.channel().close();
            return;
        }
        if (!userRepository.existsById(userId)) {
            log.warn("Unregistered user. Request address is {}. request id is {}.", ctx.channel().remoteAddress(), userId);
            ctx.channel().close();
            return;
        }
        // 扩展：可以在这里获取不同配置的uri进行路由分发实现不同的业务逻辑
        ctx.channel().attr(AttributeKey.valueOf("userId")).set(userId);
//        log.info("新用户建立连接{}", userId);
        // 握手建立
        WebSocketServerHandshakerFactory handShakerFactory = new WebSocketServerHandshakerFactory(
                url, null, true);
        handShaker = handShakerFactory.newHandshaker(request);
        if(handShaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handShaker.handshake(ctx.channel(), request);
        }
    }

    private void handleMessage(ChannelHandlerContext ctx, WebSocketFrame frame) {
         // 判断是否关闭链路命令
        if (frame instanceof CloseWebSocketFrame) {
            //链接断开请求
            handShaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        // 判断是否Ping消息
        if (frame instanceof PingWebSocketFrame) {
            ctx.writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        // 判断是否Pong消息
        if (frame instanceof PongWebSocketFrame) {
            ctx.writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(frame.getClass().getName() + " frame type not supported");
        }
        TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
        UserOnlineManager.addChannel(ctx.channel(), textFrame.text());
        ctx.fireChannelRead(frame.retain());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
