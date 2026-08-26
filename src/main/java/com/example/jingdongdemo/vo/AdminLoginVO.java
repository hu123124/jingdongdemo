package com.example.jingdongdemo.vo;

import lombok.Data;

@Data
public class AdminLoginVO {
    private String token;
    private Long adminId;
    private String username;
    private String realName;
}
