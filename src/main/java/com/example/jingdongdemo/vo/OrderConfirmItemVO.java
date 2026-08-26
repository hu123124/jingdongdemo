package com.example.jingdongdemo.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderConfirmItemVO {
    private Long productId;
    private Long skuId;
    private String productName;
    private String skuSpec;
    private String productImage;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
}
