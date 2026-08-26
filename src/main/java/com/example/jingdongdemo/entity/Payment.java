package com.example.jingdongdemo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {
    private Long id;
    private String orderNo;
    private Long userId;
    private String payNo;
    private Integer payChannel;    // 1支付宝 2微信 3余额
    private BigDecimal payAmount;
    private Integer status;        // 0待支付 1成功 2失败 3已退款
    private LocalDateTime payTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
