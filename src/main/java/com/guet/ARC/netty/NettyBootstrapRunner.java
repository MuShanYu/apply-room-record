package com.guet.ARC.netty;

import cn.hutool.core.io.resource.ClassPathResource;
import com.guet.ARC.netty.handler.SocketConnectedHandler;
import com.guet.ARC.netty.handler.UserOnlineHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
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

    private Channel serverChannel;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        /*final SslContext sslCtx;
        // 使用自签名证书进行 SSL 配置
        ClassPathResource pem = new ClassPathResource("x.pem");
        ClassPathResource key = new ClassPathResource("x.key");
        sslCtx = SslContextBuilder.forServer(pem.getStream(), key.getStream()).build();*/
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
//                    pipeline.addLast(sslCtx.newHandler(socketChannel.alloc()));  // 添加 SSL 处理
                    pipeline.addLast(new HttpServerCodec());//请求解码器
                    pipeline.addLast(new HttpObjectAggregator(65536));//将多个消息转换成单一的消息对象
                    pipeline.addLast(new ChunkedWriteHandler());//支持异步发送大的码流，一般用于发送文件流
                    pipeline.addLast(new WebSocketServerCompressionHandler());//压缩处理
                    pipeline.addLast(new IdleStateHandler(60, 0, 0));
                    pipeline.addLast(applicationContext.getBean(SocketConnectedHandler.class));
                    pipeline.addLast(applicationContext.getBean(UserOnlineHandler.class));
                }
            });
            serverChannel = serverBootstrap.bind().sync().channel();
            log.info("websocket 服务启动，port={}", this.port);
            serverChannel.closeFuture().sync();
        }catch (Exception e) {
            log.error(e.getMessage());
        }
        finally {
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
}
