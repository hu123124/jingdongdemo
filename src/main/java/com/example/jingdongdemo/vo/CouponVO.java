package com.example.jingdongdemo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// vo/CouponVO.java
@Data
public class CouponVO {
    private Long id;
    private String name;          // "满10000减500"
    private Integer type;         // 1满减 2折扣 3无门槛
    private BigDecimal discountValue;
    private BigDecimal minAmount; // 使用门槛
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}