package com.example.jingdongdemo.controller;

import com.example.jingdongdemo.common.R;
import com.example.jingdongdemo.dto.LoginRequest;
import com.example.jingdongdemo.dto.RegisterRequest;
import com.example.jingdongdemo.service.AuthService;
import com.example.jingdongdemo.vo.LoginResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    AuthService authService;

    @PostMapping("/register")
    public R<Map<String,String>> register(@RequestBody RegisterRequest registerRequest){
        Long userId = authService.register(registerRequest);
        Map<String,String> data = new HashMap<>();
        data.put("userId",String.valueOf(userId));
        return R.ok(data);
    }

    @PostMapping("/login")
    public R<LoginResultVO> login(@RequestBody LoginRequest loginRequest){
        LoginResultVO loginResultVO =authService.login(loginRequest);
        return R.ok(loginResultVO);
    }
    @PostMapping("logout")
    public R<Void> logout(){
        return R.ok();
    }

}
