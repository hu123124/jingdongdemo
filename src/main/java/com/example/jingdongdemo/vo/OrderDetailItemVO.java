package com.example.jingdongdemo.vo;

import lombok.Data;

import java.math.BigDecimal;

// vo/OrderDetailItemVO.java
@Data
public class OrderDetailItemVO {
    private Long productId;
    private Long skuId;
    private String productName;
    private String skuSpec;
    private String productImage;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
}