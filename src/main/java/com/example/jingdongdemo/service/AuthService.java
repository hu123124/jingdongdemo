package com.example.jingdongdemo.service;

import com.example.jingdongdemo.dto.LoginRequest;
import com.example.jingdongdemo.dto.RegisterRequest;
import com.example.jingdongdemo.vo.LoginResultVO;
import com.example.jingdongdemo.vo.UserInfoVO;

public interface AuthService {
    Long register(RegisterRequest request);

    LoginResultVO login(LoginRequest loginRequest);

}