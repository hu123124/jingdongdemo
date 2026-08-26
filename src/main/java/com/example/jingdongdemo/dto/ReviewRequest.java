package com.example.jingdongdemo.dto;

import lombok.Data;
import java.util.List;

@Data
public class ReviewRequest {
    private String orderNo;
    private Long productId;
    private Integer rating;        // 1-5星
    private String content;
    private List<String> images;   // 图片URL数组
    private Integer isAnonymous;   // 0否 1是
}
