package com.example.jingdongdemo.dto;

import lombok.Data;

@Data
public class CartRequest {
    private Long productId;
    private Long skuId;
    private Integer quantity;
}
