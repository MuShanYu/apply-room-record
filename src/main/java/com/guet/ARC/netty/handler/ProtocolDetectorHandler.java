package com.guet.ARC.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * @author Yulf
 * Date 2024/12/6
 */
public class ProtocolDetectorHandler extends SimpleChannelInboundHandler<ByteBuf> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        // 确保有足够的数据来进行协议判断
        if (msg.readableBytes() < 5) {
            // 数据长度不足，不能判断协议类型
            ctx.close();
            return;
        }

        // 读取前 5 个字节，用于协议判断
        byte[] firstBytes = new byte[5];
        msg.getBytes(0, firstBytes);

        if (isHttpRequest(firstBytes)) {
            // 检测到 HTTP 请求，返回 400 错误
            String errorResponse = "HTTP/1.1 400 Bad Request\r\n" +
                    "Content-Type: text/plain\r\n" +
                    "Content-Length: 29\r\n\r\n" +
                    "Bad Request. Code: 400\n";
            ctx.writeAndFlush(ctx.alloc().buffer().writeBytes(errorResponse.getBytes(CharsetUtil.UTF_8)))
                    .addListener(future -> ctx.close());
        } else if (isSslTlsHandshake(firstBytes[0])) {
            // 如果是 SSL/TLS 流量，移除协议检测器并将消息交给 SslHandler 处理
            ctx.pipeline().remove(this); // 移除协议检测器
            ctx.fireChannelRead(msg.retain()); // 将消息继续传递给后续处理器（例如 SslHandler）
        } else {
            // 如果是未知协议流量，关闭连接
            ctx.close();
        }
    }

    private boolean isHttpRequest(byte[] firstBytes) {
        String prefix = new String(firstBytes, CharsetUtil.UTF_8).trim();
        return prefix.startsWith("GET") || prefix.startsWith("POST") || prefix.startsWith("HEAD") ||
                prefix.startsWith("OPTIONS") || prefix.startsWith("PUT") || prefix.startsWith("DELETE");
    }

    private boolean isSslTlsHandshake(byte firstByte) {
        // TLS Handshake starts with 0x16 (SSL/TLS记录头的类型字段)
        return firstByte == 0x16;
    }
}
