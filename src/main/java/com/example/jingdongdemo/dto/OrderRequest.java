package com.example.jingdongdemo.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderRequest {
    private Long addressId;
    private Long userCouponId;
    private String remark;
    private Boolean fromCart;
}
