package com.example.jingdongdemo.service;

import com.example.jingdongdemo.dto.AdminLoginRequest;
import com.example.jingdongdemo.vo.AdminLoginVO;

public interface AdminService {
    AdminLoginVO login(AdminLoginRequest req);
    AdminLoginVO me();
}
