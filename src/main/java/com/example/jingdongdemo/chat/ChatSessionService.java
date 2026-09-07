package com.example.jingdongdemo.chat;

import cn.hutool.json.JSONObject;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客服会话管理（内存态）：
 * - userChannels : userId → 在线用户的 Netty Channel（推送通道）
 * - humanMode    : 已转人工的用户集合（机器人不再自动答）
 * - pending      : 人工模式下用户发来的消息队列（客服待办）
 */
@Slf4j
@Service
public class ChatSessionService {

    private final Map<String, Channel> userChannels = new ConcurrentHashMap<>();
    private final java.util.Set<String> humanMode = ConcurrentHashMap.newKeySet();
    private final Map<String, List<String>> pending = new ConcurrentHashMap<>();

    /** 握手完成时登记：userId → channel */
    public void register(String userId, Channel channel) {
        userChannels.put(userId, channel);
        log.info("会话登记: userId={}", userId);
    }

    /** 连接断开时清理 */
    public void unregister(String userId) {
        userChannels.remove(userId);
        humanMode.remove(userId);
        pending.remove(userId);
        log.info("会话清理: userId={}", userId);
    }

    public boolean isHumanMode(String userId) {
        return humanMode.contains(userId);
    }

    /** 转人工：该会话进入人工模式 */
    public void transferToHuman(String userId) {
        humanMode.add(userId);
    }

    /** 人工模式下用户发的消息进入客服待办队列（保留最近 5 条） */
    public void enqueueWaiting(String userId, String content) {
        List<String> list = pending.computeIfAbsent(userId, k -> Collections.synchronizedList(new ArrayList<>()));
        list.add(content);
        if (list.size() > 5) {
            list.remove(0);
        }
    }

    /** 客服视角：列出正在等人工且在线的会话 */
    public List<Map<String, Object>> listPendingSessions() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (String userId : humanMode) {
            if (!userChannels.containsKey(userId)) {
                continue;   // 已离线的不展示
            }
            List<String> msgs = pending.get(userId);
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("userId", userId);
            m.put("lastMessage", msgs == null || msgs.isEmpty() ? "" : msgs.get(msgs.size() - 1));
            m.put("waitingCount", msgs == null ? 0 : msgs.size());
            result.add(m);
        }
        return result;
    }

    /** 客服回复：找到用户 Channel 实时推送；用户已离线则失败 */
    public boolean replyToUser(String userId, String content) {
        Channel ch = userChannels.get(userId);
        if (ch == null || !ch.isActive()) {
            log.warn("客服回复失败，用户不在线: userId={}", userId);
            return false;
        }
        JSONObject resp = new JSONObject();
        resp.set("type", "human_reply");
        resp.set("content", content);
        ch.writeAndFlush(new TextWebSocketFrame(resp.toString()));
        pending.remove(userId);
        log.info("人工客服已回复: userId={}", userId);
        return true;
    }
}