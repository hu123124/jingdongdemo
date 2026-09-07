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

    /**
     * 人工回复某用户。
     * 权限说明：本接口路径挂在 /api/admin/** 下，SecurityConfig 已统一要求 ADMIN 角色（与其它 B 端接口一致）。
     */
    @PostMapping("/reply")
    public R<Void> reply(@RequestBody Map<String, String> body) {
        String userId = body.get("userId");
        String content = body.get("content");
        if (userId == null || userId.trim().isEmpty()) {
            return R.error(400, "userId 不能为空");
        }
        if (content == null || content.trim().isEmpty()) {
            return R.error(400, "回复内容不能为空");
        }
        if (content.trim().length() > 500) {
            return R.error(400, "回复内容不能超过 500 字");
        }
        boolean ok = chatSessionService.replyToUser(userId.trim(), content.trim());
        return ok ? R.ok() : R.error(400, "用户不在线或连接已断开");
    }
}