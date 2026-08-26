package com.example.jingdongdemo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// vo/OrderDetailVO.java
@Data
public class OrderDetailVO {
    private Long orderId;
    private String orderNo;
    private Integer status;
    private String statusText;
    private BigDecimal totalAmount;
    private BigDecimal freight;
    private BigDecimal payAmount;
    private OrderConfirmAddressVO address;              // 从 address_snapshot JSON 解析
    private List<OrderDetailItemVO> items;       // 查 t_order_item
    private LocalDateTime payTime;
    private LocalDateTime shipTime;
    private LocalDateTime receiveTime;
    private LocalDateTime createTime;
    private String remark;
}