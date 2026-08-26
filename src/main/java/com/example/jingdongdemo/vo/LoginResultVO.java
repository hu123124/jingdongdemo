package com.example.jingdongdemo.vo;

import lombok.Data;

//Spring 把 Java 对象转成 JSON 时，调的是 getter 方法。@Data 自动生成 getToken()、getUserId() 等方法，不加的话返回 { } 空的。
@Data
public class LoginResultVO {
    private String token;
    private Long userId;
    private String userName;
    private String nickname;
    private String avatar;
}
