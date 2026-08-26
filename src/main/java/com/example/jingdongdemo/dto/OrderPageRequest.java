package com.example.jingdongdemo.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderPageRequest {
    private Integer status;
    private Integer pageNum;
    private Integer pageSize;
}
