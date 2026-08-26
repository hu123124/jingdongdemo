package com.example.jingdongdemo.controller;

import com.example.jingdongdemo.common.R;
import com.example.jingdongdemo.dto.OrderPageRequest;
import com.example.jingdongdemo.dto.OrderRequest;
import com.example.jingdongdemo.entity.Coupon;
import com.example.jingdongdemo.service.OrderService;
import com.example.jingdongdemo.service.CouponService;
import com.example.jingdongdemo.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/coupons")
public class CouponController {
    private final CouponService couponService;

    @GetMapping("/available")
    public R<List<CouponVO>> getAvailableCoupon(){
        return R.ok(couponService.getAvailableCoupon());
    }
    @PostMapping("/{couponId}/receive")
    public R<Void> receiveCoupon(@PathVariable("couponId") Long couponId){
        couponService.receiveCoupon(couponId);
        return R.ok();
    }
    @GetMapping("/mine")
    public R<List<UserCouponVO>> getMineCoupon(){
        return R.ok(couponService.getMineCoupon());
    }
}
