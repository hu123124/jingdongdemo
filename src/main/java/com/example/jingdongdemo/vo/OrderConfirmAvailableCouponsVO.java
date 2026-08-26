package com.example.jingdongdemo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
  public class OrderConfirmAvailableCouponsVO {
    private Long userCouponId;
    private String name;
    private BigDecimal discount;
  }