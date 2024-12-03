package com.guet.ARC.netty;

import cn.hutool.core.io.resource.ClassPathResource;
import com.guet.ARC.ApplyRoomRecordConfig;
import com.guet.ARC.netty.handler.HttpRequestCheckHandler;
import com.guet.ARC.netty.handler.SocketConnectedHandler;
import com.guet.ARC.netty.handler.UserAuthHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLException;
import java.net.InetSocketAddress;

/**
 * @author Yulf
 * Date 2024/6/6
 */
@Component
@Slf4j
public class NettyBootstrapRunner implements ApplicationRunner, ApplicationListener<ContextClosedEvent>, ApplicationContextAware {

    @Value("${netty.websocket.port}")
    private int port;

    @Value("${netty.websocket.pemFile}")
    private String pemFile;

    @Value("${netty.websocket.keyFile}")
    private String keyFile;

    private Channel serverChannel;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 使用自签名证书进行 SSL 配置
        ApplyRoomRecordConfig globalConfig = applicationContext.getBean(ApplyRoomRecordConfig.class);
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.localAddress(new InetSocketAddress(port));
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    addSslHandler(pipeline, socketChannel, globalConfig);
                    pipeline.addLast(new HttpServerCodec());//请求解码器
                    pipeline.addLast(new HttpObjectAggregator(65536));//将多个消息转换成单一的消息对象
                    pipeline.addLast(new ChunkedWriteHandler());//支持异步发送大的码流，一般用于发送文件流
                    pipeline.addLast(new WebSocketServerCompressionHandler());//压缩处理
                    pipeline.addLast(new UserAuthHandler());// 用户认证处理
                    // 参数配置请百度
                    pipeline.addLast(new WebSocketServerProtocolHandler("/websocket", null, true, 16384, false, true, 60000L));//websocket协议处理
                    pipeline.addLast(applicationContext.getBean(SocketConnectedHandler.class)); // 自定义处理器，处理消息发送与在线统计
                }
            });
            serverChannel = serverBootstrap.bind().sync().channel();
            log.info("websocket 服务启动，port={}", this.port);
            serverChannel.closeFuture().sync();
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        if (this.serverChannel != null) {
            this.serverChannel.close();
        }
        log.info("websocket 服务停止");
    }

    private void addSslHandler(ChannelPipeline pipeline, SocketChannel socketChannel, ApplyRoomRecordConfig globalConfig) {
        // 取决于你的配置，如果配置了是，那么请您同时配置pem和key文件
        // 这两个文件请放在resource目录下
        if (globalConfig.getUseWebsocketSSL()) {
            try {
                ClassPathResource pem = new ClassPathResource(pemFile);
                ClassPathResource key = new ClassPathResource(keyFile);
                SslContext sslCtx = SslContextBuilder.forServer(pem.getStream(), key.getStream()).build();
                pipeline.addLast(new HttpRequestCheckHandler());
                pipeline.addLast(sslCtx.newHandler(socketChannel.alloc()));  // 添加 SSL 处理
            } catch (SSLException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
