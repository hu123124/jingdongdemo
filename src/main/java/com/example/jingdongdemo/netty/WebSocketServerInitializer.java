package com.example.jingdongdemo.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
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
                .addLast(new WebSocketServerProtocolHandler(WS_PATH)) // 3. 自动完成 ws 握手 + 帧编解码
                .addLast(chatHandler);                                // 4. 业务处理
    }
}