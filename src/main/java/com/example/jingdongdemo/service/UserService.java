package com.example.jingdongdemo.service;

import com.example.jingdongdemo.dto.LoginRequest;
import com.example.jingdongdemo.dto.PasswordUpdateRequest;
import com.example.jingdongdemo.dto.RegisterRequest;
import com.example.jingdongdemo.dto.UpdateProfileRequest;
import com.example.jingdongdemo.entity.User;
import com.example.jingdongdemo.vo.LoginResultVO;
import com.example.jingdongdemo.vo.PageResultVO;
import com.example.jingdongdemo.vo.UserInfoVO;

import java.util.Map;

public interface UserService {

    UserInfoVO info();

    void updateInfo(UpdateProfileRequest updateProfileRequest);

    void changePassword(PasswordUpdateRequest passwordUpdateRequest);

    // ==================== B端 ====================

    /**
     * B端 - 用户列表（分页）
     */
    PageResultVO<Map<String, Object>> adminList(Integer pageNum, Integer pageSize);

    /**
     * B端 - 用户详情
     */
    User adminDetail(Long id);

    /**
     * B端 - 启用/禁用用户
     */
    void adminUpdateStatus(Long id, Integer status);
}