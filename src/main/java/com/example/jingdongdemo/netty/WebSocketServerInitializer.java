package com.example.jingdongdemo.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolConfig;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 每个 WebSocket 连接的流水线装配。
 * 浏览器先发 HTTP 升级请求 → 前三个 handler 完成"升级为 ws 协议"
 * → 之后收发的是 WebSocket Frame → 交给 ChatHandler 做业务。
 */
@Component
@RequiredArgsConstructor
public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {

    /** WebSocket 路径，后面 nginx 反代按同一路径转发 */
    public static final String WS_PATH = "/ws";

    private final ChatHandler chatHandler;

    @Override
    protected void initChannel(SocketChannel ch) {
        ch.pipeline()
                .addLast(new HttpServerCodec())                       // 1. HTTP 编解码（握手请求）
                .addLast(new HttpObjectAggregator(65536))             // 2. 聚合 HTTP 分块报文
                .addLast(new WebSocketServerProtocolHandler(WebSocketServerProtocolConfig.newBuilder()
                        .websocketPath(WS_PATH)
                        .checkStartsWith(true)   // 允许 /ws?token=xxx 带参握手；默认精确匹配会把带 query 的请求放行给下游导致无人应答挂起
                        .build()))               // 3. 自动完成 ws 握手 + 帧编解码
                .addLast(new IdleStateHandler(60, 0, 0, TimeUnit.SECONDS)) // 4. 60s 没收数据判定空闲（心跳探活用）
                .addLast(chatHandler);                                // 5. 业务处理
    }
}