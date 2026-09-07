package com.example.jingdongdemo.controller;

import com.example.jingdongdemo.chat.ChatSessionService;
import com.example.jingdongdemo.common.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 人工客服坐席接口（B 端，需 ADMIN 登录）。
 * 客服在这里查看"正在等人工的用户"并回复，回复经 Netty 实时推送到用户页面。
 */
@RestController
@RequestMapping("/api/admin/chat")
@RequiredArgsConstructor
public class ChatAdminController {

    private final ChatSessionService chatSessionService;

    /** 查看待人工会话列表 */
    @GetMapping("/pending")
    public R<List<Map<String, Object>>> pending() {
        return R.ok(chatSessionService.listPendingSessions());
    }

    /** 人工回复某用户 */
    @PostMapping("/reply")
    public R<Void> reply(@RequestBody Map<String, String> body) {
        String userId = body.get("userId");
        String content = body.get("content");
        boolean ok = chatSessionService.replyToUser(userId, content);
        return ok ? R.ok() : R.error(400, "用户不在线或连接已断开");
    }
}