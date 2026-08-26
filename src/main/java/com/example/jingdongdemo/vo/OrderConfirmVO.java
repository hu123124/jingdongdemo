package com.example.jingdongdemo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
  public class OrderConfirmVO {
    private OrderConfirmAddressVO address;
    private List<OrderConfirmItemVO> items;
    private BigDecimal totalAmount;
    private BigDecimal freight;
    private BigDecimal payAmount;
    private List<OrderConfirmAvailableCouponsVO> availableCoupons;  // 优惠券先 null，后面补
  }