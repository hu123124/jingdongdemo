package com.example.jingdongdemo.service;

import com.example.jingdongdemo.dto.PaymentRequest;
import com.example.jingdongdemo.entity.Coupon;
import com.example.jingdongdemo.vo.CouponVO;
import com.example.jingdongdemo.vo.PageResultVO;
import com.example.jingdongdemo.vo.PaymentVO;
import com.example.jingdongdemo.vo.UserCouponVO;

import java.util.List;
import java.util.Map;

public interface CouponService {

    List<CouponVO> getAvailableCoupon();

    void receiveCoupon(Long couponId);

    List<UserCouponVO> getMineCoupon();

    // ==================== B端 ====================

    /**
     * B端 - 优惠券列表（分页）
     */
    PageResultVO<Coupon> adminList(Integer pageNum, Integer pageSize);

    /**
     * B端 - 优惠券详情
     */
    Coupon adminDetail(Long id);

    /**
     * B端 - 新增优惠券
     */
    void adminCreate(Map<String, Object> body);

    /**
     * B端 - 修改优惠券
     */
    void adminUpdate(Long id, Map<String, Object> body);

    /**
     * B端 - 删除优惠券
     */
    void adminDelete(Long id);

    /**
     * B端 - 启用/禁用优惠券
     */
    void adminUpdateStatus(Long id, Integer status);
}
