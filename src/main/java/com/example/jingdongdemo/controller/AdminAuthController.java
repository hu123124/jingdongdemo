package com.example.jingdongdemo.controller;

import com.example.jingdongdemo.common.R;
import com.example.jingdongdemo.dto.AdminLoginRequest;
import com.example.jingdongdemo.service.AdminService;
import com.example.jingdongdemo.vo.AdminLoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController @RequiredArgsConstructor
@RequestMapping("/api/admin/v1/auth")
public class AdminAuthController {
    private final AdminService adminService;

    @PostMapping("/login")
    public R<AdminLoginVO> login(@RequestBody AdminLoginRequest req) {
        return R.ok(adminService.login(req));
    }

    @PostMapping("/logout")
    public R<Void> logout() {
        return R.ok();
    }
}

// 管理员信息接口放 AdminController 里
// GET /api/admin/v1/admins/me → adminService.me()
