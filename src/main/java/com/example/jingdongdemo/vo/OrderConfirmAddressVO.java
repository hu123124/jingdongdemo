package com.example.jingdongdemo.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderConfirmAddressVO {
    private Long id;
    private String consignee;
    private String phone;
    private String fullAddress;
}
