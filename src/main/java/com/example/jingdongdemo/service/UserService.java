package com.example.jingdongdemo.service;

import com.example.jingdongdemo.dto.LoginRequest;
import com.example.jingdongdemo.dto.PasswordUpdateRequest;
import com.example.jingdongdemo.dto.RegisterRequest;
import com.example.jingdongdemo.dto.UpdateProfileRequest;
import com.example.jingdongdemo.entity.User;
import com.example.jingdongdemo.vo.LoginResultVO;
import com.example.jingdongdemo.vo.UserInfoVO;

import java.util.Map;

public interface UserService {

    UserInfoVO info();

    void updateInfo(UpdateProfileRequest updateProfileRequest);

    void changePassword(PasswordUpdateRequest passwordUpdateRequest);
}