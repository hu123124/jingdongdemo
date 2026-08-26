package com.example.jingdongdemo.service;

import com.example.jingdongdemo.dto.PaymentRequest;
import com.example.jingdongdemo.vo.CouponVO;
import com.example.jingdongdemo.vo.PaymentVO;
import com.example.jingdongdemo.vo.UserCouponVO;

import java.util.List;

public interface CouponService {

    List<CouponVO> getAvailableCoupon();

    void receiveCoupon(Long couponId);

    List<UserCouponVO> getMineCoupon();
}
