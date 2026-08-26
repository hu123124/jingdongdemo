package com.example.jingdongdemo.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReviewVO {
    private Long id;
    private Long userId;
    private String nickname;       // 匿名时显示 "匿名用户"
    private String avatar;
    private Integer rating;
    private String content;
    private List<String> images;
    private LocalDateTime createTime;
    private Integer isAnonymous;
}
