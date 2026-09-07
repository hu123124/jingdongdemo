package com.example.jingdongdemo.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Netty WebSocket 客服服务端：随 Spring Boot 启动，独立端口（默认 8090，不占 Tomcat 8080）。
 * boss 线程组负责 accept 新连接，worker 线程组负责连接上的 IO 读写。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NettyWebSocketServer implements ApplicationRunner, DisposableBean {

    @Value("${netty.server.port:8090}")
    private int port;

    private final WebSocketServerInitializer serverInitializer;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        bossGroup = new NioEventLoopGroup(1);       // 1 个线程负责 accept
        workerGroup = new NioEventLoopGroup();      // 默认 CPU 核数*2，处理 IO
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)          // 连接等待队列
                    .childOption(ChannelOption.SO_KEEPALIVE, true)  // TCP 保活
                    .childHandler(serverInitializer);               // 每个新连接套用同一条 pipeline
            serverChannel = bootstrap.bind(port).sync().channel();
        } catch (Exception e) {
            // 客服是核心功能：启动失败必须让应用启动失败（CICD 健康检查会告警），不能静默降级
            log.error("Netty WebSocket 客服服务端启动失败, 端口={}", port, e);
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            throw new IllegalStateException("Netty WebSocket 客服服务端启动失败(端口 " + port + ")", e);
        }
        log.info("Netty WebSocket 客服服务端已启动: ws://localhost:{}{}", port, WebSocketServerInitializer.WS_PATH);
    }

    @Override
    public void destroy() {
        log.info("正在关闭 Netty WebSocket 服务端...");
        if (serverChannel != null) {
            serverChannel.close();
        }
        if (bossGroup != null) bossGroup.shutdownGracefully();
        if (workerGroup != null) workerGroup.shutdownGracefully();
        log.info("Netty WebSocket 服务端已关闭");
    }
}