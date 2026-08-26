package com.example.jingdongdemo.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
  public class ProductSKUVO {
      private Long id;
      private String skuCode;
      private String spec;
      private BigDecimal price;
      private Integer stock;
      private String Images;
  }