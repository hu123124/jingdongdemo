package com.example.jingdongdemo.service;

import com.example.jingdongdemo.dto.ReviewRequest;
import com.example.jingdongdemo.vo.PageResultVO;
import com.example.jingdongdemo.vo.ReviewVO;

// service/ReviewService.java
public interface ReviewService {
    void addReview(ReviewRequest req);
    PageResultVO<ReviewVO> productReviews(Long productId, Integer pageNum, Integer pageSize);
}