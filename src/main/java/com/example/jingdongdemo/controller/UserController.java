package com.example.jingdongdemo.controller;

import com.example.jingdongdemo.common.R;
import com.example.jingdongdemo.dto.PasswordUpdateRequest;
import com.example.jingdongdemo.dto.UpdateProfileRequest;
import com.example.jingdongdemo.entity.User;
import com.example.jingdongdemo.service.UserService;
import com.example.jingdongdemo.vo.UserInfoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/api/v1/users")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public R<UserInfoVO> info() {
       return R.ok(userService.info());
    }
    @PutMapping("/me")
    public R<Void> updateInfo(@RequestBody UpdateProfileRequest updateProfileRequest) {
        userService.updateInfo(updateProfileRequest);
        return R.ok();
    }
    @PutMapping("/password")
    public R<Void> changePassword(@RequestBody PasswordUpdateRequest passwordUpdateRequest) {
        userService.changePassword(passwordUpdateRequest);
        return R.ok();
    }
}
