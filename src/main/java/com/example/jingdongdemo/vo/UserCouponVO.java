package com.example.jingdongdemo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// vo/UserCouponVO.java
@Data
public class UserCouponVO {
    private Long id;              // t_user_coupon.id → 下单时传这个
    private String name;
    private Integer type;
    private BigDecimal discountValue;
    private BigDecimal minAmount;
    private Integer status;       // 0未使用 1已使用 2已过期
    private LocalDateTime receiveTime;
}