package com.example.jingdongdemo.dto;

import lombok.Data;

// dto/PaymentRequest.java
@Data
public class PaymentRequest {
    private String orderNo;
    private Integer payChannel;  // 1支付宝 2微信 3余额
}