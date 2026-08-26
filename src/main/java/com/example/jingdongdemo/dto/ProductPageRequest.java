package com.example.jingdongdemo.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductPageRequest {
    private Integer pageNum;
    private Integer pageSize;
    private Integer categoryId;
    private String keyword;
    private String sort;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
}
