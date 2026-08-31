package com.example.jingdongdemo.controller;

import com.example.jingdongdemo.common.R;
import com.example.jingdongdemo.entity.User;
import com.example.jingdongdemo.service.UserService;
import com.example.jingdongdemo.vo.PageResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * B端 - 用户管理
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/v1/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    /** B端 - 用户列表 */
    @GetMapping
    public R<PageResultVO<Map<String, Object>>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "15") Integer pageSize) {
        return R.ok(userService.adminList(pageNum, pageSize));
    }

    /** B端 - 用户详情 */
    @GetMapping("/{id}")
    public R<User> detail(@PathVariable Long id) {
        return R.ok(userService.adminDetail(id));
    }

    /** B端 - 启用/禁用用户 */
    @PutMapping("/{id}/status")
    public R<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        userService.adminUpdateStatus(id, body.get("status"));
        return R.ok();
    }
}
