package com.example.jingdongdemo.controller;

import com.example.jingdongdemo.common.R;
import com.example.jingdongdemo.dto.ReviewRequest;
import com.example.jingdongdemo.service.ReviewService;
import com.example.jingdongdemo.vo.PageResultVO;
import com.example.jingdongdemo.vo.ReviewVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

// controller/ReviewController.java
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/reviews")
    public R<Void> addReview(@RequestBody ReviewRequest req) {
        reviewService.addReview(req);
        return R.ok();
    }

    @GetMapping("/products/{productId}/reviews")
    public R<PageResultVO<ReviewVO>> productReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return R.ok(reviewService.productReviews(productId, pageNum, pageSize));
    }
}