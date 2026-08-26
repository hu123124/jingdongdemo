package com.example.jingdongdemo.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderVO {
    private Long orderId;
    private String orderNo;
    private BigDecimal payAmount;
}
