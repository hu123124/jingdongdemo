package com.example.jingdongdemo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Review {
    private Long id;
    private Long orderId;
    private String orderNo;
    private Long productId;
    private Long userId;
    private Integer rating;
    private String content;
    private String images;
    private Integer isAnonymous;
    private LocalDateTime createTime;
}
