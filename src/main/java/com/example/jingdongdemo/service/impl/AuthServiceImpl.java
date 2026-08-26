package com.example.jingdongdemo.service.impl;

import com.example.jingdongdemo.common.JwtUtils;
import com.example.jingdongdemo.dto.LoginRequest;
import com.example.jingdongdemo.dto.RegisterRequest;
import com.example.jingdongdemo.entity.User;
import com.example.jingdongdemo.mapper.UserMapper;
import com.example.jingdongdemo.service.AuthService;
import com.example.jingdongdemo.vo.LoginResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    // 加这两行
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    /**
     * 注册
     * @param request
     * @return
     */
    @Override
    public Long register(RegisterRequest request) {
        User exist = userMapper.getByUsername(request.getUsername());
        if(exist!=null){
           throw new RuntimeException("用户名已存在");
        }
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = User.builder()
                .username(request.getUsername())
                .password(encodedPassword)
                .phone(request.getPhone())
                .status("1")
                .build();
        userMapper.insert(user);
        return user.getId();

    }

    @Override
    public LoginResultVO login(LoginRequest loginRequest) {
        User user = userMapper.getByUsername(loginRequest.getUsername());
        if(user==null){
            throw new RuntimeException("用户名不存在");
        }
        if(passwordEncoder.matches(loginRequest.getPassword(),user.getPassword())){
            userMapper.updateLoginTime(user.getId()); // 记录登录时间
            LoginResultVO loginResultVO = new LoginResultVO();
            loginResultVO.setToken(jwtUtils.generateToken(user.getId()));
            loginResultVO.setUserId(user.getId());
            loginResultVO.setUserName(user.getUsername());
            loginResultVO.setNickname(user.getNickname());
            loginResultVO.setAvatar(user.getAvatar());
            return loginResultVO;
        }
        throw new RuntimeException("密码错误");
    }

}
