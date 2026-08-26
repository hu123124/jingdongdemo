package com.example.jingdongdemo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// vo/OrderListVO.java
@Data
public class OrderListVO {
    private Long orderId;
    private String orderNo;
    private Integer status;
    private String statusText;    // 待付款/待发货... 在 Service 里根据 status 转
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private String consignee;     // 从 address_snapshot JSON 解析
    private String phone;         // 脱敏
    private LocalDateTime createTime;
}