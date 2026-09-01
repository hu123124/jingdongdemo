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
public class ProductSKU {
    private Long id;
    private Long productId;
    private String skuCode;
    private String spec;
    private BigDecimal price;
    private Integer stock;
    private String image;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
