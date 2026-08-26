package com.example.jingdongdemo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateProfileRequest {
    private String email;
    private String nickname;
    private String avatar;
    private String gender;
}
