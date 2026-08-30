package com.example.jingdongdemo.entity;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {
    private Long id;
    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;
    private BigDecimal freight;
    private BigDecimal payAmount;
    private Integer status;
    private String addressSnapshot;
    private LocalDateTime payTime;
    private LocalDateTime shipTime;
    private LocalDateTime receiveTime;
    private LocalDateTime closeTime;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String requestId;
}
