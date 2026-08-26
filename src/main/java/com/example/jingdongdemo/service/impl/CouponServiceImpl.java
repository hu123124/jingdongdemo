package com.example.jingdongdemo.service.impl;


import com.example.jingdongdemo.dto.CartBatchDTO;
import com.example.jingdongdemo.dto.CartRequest;
import com.example.jingdongdemo.entity.Cart;
import com.example.jingdongdemo.entity.Coupon;
import com.example.jingdongdemo.entity.UserCoupon;
import com.example.jingdongdemo.mapper.CartMapper;
import com.example.jingdongdemo.mapper.CouponMapper;
import com.example.jingdongdemo.service.CartService;
import com.example.jingdongdemo.service.CouponService;
import com.example.jingdongdemo.vo.CartListVO;
import com.example.jingdongdemo.vo.CartVO;
import com.example.jingdongdemo.vo.CouponVO;
import com.example.jingdongdemo.vo.UserCouponVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponMapper couponMapper;

    @Override
    public List<CouponVO> getAvailableCoupon() {
        return couponMapper.getAvailableCoupon((Long)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @Transactional
    @Override
    public void receiveCoupon(Long couponId) {
        Long userId = (Long)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Coupon coupon =couponMapper.getByCouponId(couponId,userId);
        if(coupon!=null){
            UserCoupon userCoupon=UserCoupon.builder()
                    .userId(userId)
                    .couponId(coupon.getId())
                    .status(0)
                    .build();
            couponMapper.receiveCoupon(userCoupon);
            couponMapper.incrReceiveCount(couponId);
            return;
        }
        throw new RuntimeException("领取失败");
    }

    @Override
    public List<UserCouponVO> getMineCoupon() {
        return couponMapper.getMineCoupon((Long)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}
