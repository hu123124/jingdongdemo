package com.example.jingdongdemo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// vo/PaymentVO.java
@Data
public class PaymentVO {
    private String payNo;
    private Integer payChannel;
    private BigDecimal payAmount;
    private String payUrl;       // 模拟支付返回空字符串
    private LocalDateTime expireTime;  // 30分钟后过期
}