package com.example.jingdongdemo.netty;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.jingdongdemo.chat.ChatBotService;
import com.example.jingdongdemo.chat.ChatSessionService;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.AttributeKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 客服业务 Handler：
 * 握手完成 → 解析 ?userId=xxx 并登记会话；
 * 聊天消息 → 已转人工则进客服队列，否则机器人 FAQ 回答，命中"转人工"则切换人工模式。
 */
@Slf4j
@Component
@Sharable
@RequiredArgsConstructor
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private static final AttributeKey<String> ATTR_USER_ID = AttributeKey.valueOf("userId");

    private final ChatBotService chatBotService;
    private final ChatSessionService chatSessionService;

    /** WebSocket 握手完成（此时才能拿到带 query 的 URI） */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete handshake) {
            String userId = parseUserId(handshake.requestUri());
            if (userId == null) {
                userId = "guest-" + ctx.channel().id().asShortText();   // 没带 userId 就给个临时身份
            }
            ctx.channel().attr(ATTR_USER_ID).set(userId);
            chatSessionService.register(userId, ctx.channel());
            send(ctx, "welcome", "您好 " + userId + "！我是智能客服，可咨询物流/退货/优惠券/密码问题，或输入「转人工」联系人工客服。");
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        String raw = msg.text();
        log.info("收到客服消息[{}]: {}", ctx.channel().attr(ATTR_USER_ID).get(), raw);

        String type;
        String content;
        try {
            JSONObject obj = JSONUtil.parseObj(raw);
            type = obj.getStr("type", "chat");
            content = obj.getStr("content", raw);
        } catch (Exception e) {
            type = "chat";          // 兼容裸文本
            content = raw;
        }
        if (!"chat".equals(type)) {
            send(ctx, "notice", "暂不支持的消息类型: " + type);
            return;
        }

        String userId = ctx.channel().attr(ATTR_USER_ID).get();

        // ① 已转人工：机器人不再答，消息进客服待办队列
        if (chatSessionService.isHumanMode(userId)) {
            chatSessionService.enqueueWaiting(userId, content);
            send(ctx, "notice", "已收到，人工客服正在处理，请稍候…");
            return;
        }

        // ② 未转人工：先让机器人答
        ChatBotService.ChatAnswer answer = chatBotService.answer(content);
        send(ctx, "reply", answer.content());
        if (answer.needHuman()) {
            // ③ 机器人判定需人工 → 切换人工模式
            chatSessionService.transferToHuman(userId);
            send(ctx, "notice", "已为您转接人工客服并排队，客服接入后会第一时间回复您。");
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        String userId = ctx.channel().attr(ATTR_USER_ID).get();
        if (userId != null) {
            chatSessionService.unregister(userId);
        }
        log.info("客服连接断开: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("客服连接异常: {}", cause.getMessage());
        ctx.close();
    }

    /** 从握手 URI（/ws?userId=1001）解析 userId */
    private String parseUserId(String uri) {
        try {
            List<String> values = new QueryStringDecoder(uri).parameters().get("userId");
            return (values == null || values.isEmpty()) ? null : values.get(0);
        } catch (Exception e) {
            return null;
        }
    }

    private void send(ChannelHandlerContext ctx, String type, String content) {
        JSONObject resp = new JSONObject();
        resp.set("type", type);
        resp.set("content", content);
        ctx.channel().writeAndFlush(new TextWebSocketFrame(resp.toString()));
    }
}