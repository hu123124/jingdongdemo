package com.example.jingdongdemo.netty;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.jingdongdemo.chat.ChatBotService;
import com.example.jingdongdemo.chat.ChatSessionService;
import com.example.jingdongdemo.common.JwtUtils;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 客服业务 Handler：
 * 握手完成 → 鉴权（URL 带 C 端 JWT 则校验，无效拒连；无 token 视为游客）→ 登记会话；
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
    private final JwtUtils jwtUtils;

    /** WebSocket 握手完成（此时才能拿到带 query 的 URI） */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 心跳探活：60s 没收到客户端数据 → 主动发 Ping，浏览器会自动回 Pong，防止空闲连接被回收
        if (evt instanceof IdleStateEvent) {
            ctx.writeAndFlush(new PingWebSocketFrame());
            return;
        }
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete handshake) {
            String userId = resolveIdentity(handshake.requestUri(), ctx);
            if (userId == null) {
                ctx.close();   // 带了 token 但校验不通过：拒绝连接，防止冒充登录用户
                return;
            }
            ctx.channel().attr(ATTR_USER_ID).set(userId);
            chatSessionService.register(userId, ctx.channel());
            send(ctx, "welcome", "欢迎咨询 JDemo 智能客服！可咨询物流/退货/优惠券/密码问题，或输入「转人工」联系人工客服。");
            return;
        }
        super.userEventTriggered(ctx, evt);
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
            chatSessionService.unregister(userId, ctx.channel());  // 只注销自己这条连接，避免误删同用户其它端
        }
        log.info("客服连接断开: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("客服连接异常: {}", cause.getMessage());
        ctx.close();
    }

    /**
     * 连接身份解析（WebSocket 鉴权）：
     * 1) URL 带 token（C 端 JWT）→ 校验通过以 u{userId} 为身份；token 无效 → 返回 null（调用方拒绝连接）
     * 2) 未带 token → 游客：优先用 ?userId= 参数（便于测试模拟多用户），否则随机 guest-xxx
     */
    private String resolveIdentity(String uri, ChannelHandlerContext ctx) {
        String token = parseQueryParam(uri, "token");
        if (token != null && !token.isEmpty()) {
            Long uid = jwtUtils.getUserId(token);
            if (uid == null) {
                log.warn("WebSocket 握手 token 无效, 拒绝连接: {}", ctx.channel().remoteAddress());
                return null;
            }
            return "u" + uid;
        }
        String userId = parseQueryParam(uri, "userId");
        return (userId == null || userId.isEmpty())
                ? "guest-" + ctx.channel().id().asShortText()
                : userId;
    }

    /** 从握手 URI（/ws?a=1&b=2）解析指定 query 参数 */
    private String parseQueryParam(String uri, String name) {
        try {
            List<String> values = new QueryStringDecoder(uri).parameters().get(name);
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