package com.guet.ARC.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author Yulf
 * Date 2024/12/2
 * 检查是否是明文 HTTP 请求，如果是则返回错误页面，主要用于防止爬虫
 */
@Component
@Slf4j
public class HttpRequestCheckHandler extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 5) {
            return; // 保证有足够数据判断协议
        }
        in.markReaderIndex();
        byte[] bytes = new byte[5];
        in.readBytes(bytes);
        in.resetReaderIndex();

        // 检查是否是明文 HTTP 请求
        String data = new String(bytes, StandardCharsets.US_ASCII);
        if (data.startsWith("GET") || data.startsWith("POST") || data.startsWith("HEAD")) {
            sendFeedbackPage(ctx);
        } else {
            ctx.fireChannelRead(in.retain()); // 非明文请求，继续处理
        }
    }

    private void sendFeedbackPage(ChannelHandlerContext ctx) {
        // 构造 HTTP 响应
        String htmlPage = "<html><body><h1>非法请求</h1>" +
                "<p>请使用 HTTPS 访问此服务。</p>" +
                "<p>关闭连接</p>" +
                "</body></html>";

        String response = "HTTP/1.1 400 Bad Request\r\n" +
                "Content-Type: text/html; charset=UTF-8\r\n" +
                "Content-Length: " + htmlPage.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                "Connection: close\r\n\r\n" +
                htmlPage;

        // 写入并关闭连接
        ctx.writeAndFlush(Unpooled.copiedBuffer(response.getBytes(StandardCharsets.UTF_8)))
                .addListener(ChannelFutureListener.CLOSE);
    }
}
