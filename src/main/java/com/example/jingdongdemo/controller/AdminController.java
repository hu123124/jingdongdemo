package com.example.jingdongdemo.controller;

import com.example.jingdongdemo.common.R;
import com.example.jingdongdemo.service.AdminService;
import com.example.jingdongdemo.vo.AdminLoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController @RequiredArgsConstructor
@RequestMapping("/api/admin/v1/admins")
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/me")
    public R<AdminLoginVO> me() {
        return R.ok(adminService.me());
    }
}
