package com.example.jingdongdemo.service.impl;



import com.example.jingdongdemo.dto.LoginRequest;
import com.example.jingdongdemo.dto.PasswordUpdateRequest;
import com.example.jingdongdemo.dto.UpdateProfileRequest;
import com.example.jingdongdemo.entity.User;
import com.example.jingdongdemo.mapper.UserMapper;
import com.example.jingdongdemo.service.AuthService;
import com.example.jingdongdemo.service.UserService;
import com.example.jingdongdemo.vo.LoginResultVO;
import com.example.jingdongdemo.vo.UserInfoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserInfoVO info() {
        Long userId =(Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userMapper.getByUserId(userId);

        UserInfoVO vo = new UserInfoVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setEmail(user.getEmail());
        vo.setAvatar(user.getAvatar());
        vo.setGender(user.getGender());
        vo.setCreateTime(user.getCreateTime());
        //substring(0,3)0开始，数3个
        vo.setPhone(user.getPhone().substring(0,3)+"****"+user.getPhone().substring(7));
        return vo;
    }

    @Override
    public void updateInfo(UpdateProfileRequest updateProfileRequest) {
        User user =new User();
        user.setId(((Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal()));
        user.setGender(updateProfileRequest.getGender());
        user.setNickname(updateProfileRequest.getNickname());
        user.setEmail(updateProfileRequest.getEmail());
        user.setAvatar(updateProfileRequest.getAvatar());
        userMapper.updateInfo(user);
    }

    @Override
    public void changePassword(PasswordUpdateRequest passwordUpdateRequest) {
        Long userId =(Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userMapper.getByUserId(userId);
        if(passwordEncoder.matches(passwordUpdateRequest.getOldPassword(), user.getPassword())){
            user.setPassword(passwordEncoder.encode(passwordUpdateRequest.getNewPassword()));
            userMapper.updateInfo(user);
        }
    }
}
