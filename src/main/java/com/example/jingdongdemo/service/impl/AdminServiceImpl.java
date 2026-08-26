package com.example.jingdongdemo.service.impl;

import com.example.jingdongdemo.common.JwtUtils;
import com.example.jingdongdemo.dto.AdminLoginRequest;
import com.example.jingdongdemo.entity.Admin;
import com.example.jingdongdemo.mapper.AdminMapper;
import com.example.jingdongdemo.service.AdminService;
import com.example.jingdongdemo.vo.AdminLoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final AdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public AdminLoginVO login(AdminLoginRequest req) {
        Admin admin = adminMapper.getByUsername(req.getUsername());
        if (admin == null || !passwordEncoder.matches(req.getPassword(), admin.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        if (admin.getStatus() != 1) throw new RuntimeException("账号已禁用");

        AdminLoginVO vo = new AdminLoginVO();
        vo.setToken(jwtUtils.generateToken(admin.getId(),"ADMIN"));
        vo.setAdminId(admin.getId());
        vo.setUsername(admin.getUsername());
        vo.setRealName(admin.getRealName());
        return vo;
    }

    @Override
    public AdminLoginVO me() {
        Long adminId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Admin admin = adminMapper.getById(adminId);
        AdminLoginVO vo = new AdminLoginVO();
        vo.setAdminId(admin.getId());
        vo.setUsername(admin.getUsername());
        vo.setRealName(admin.getRealName());
        return vo;
    }
}
