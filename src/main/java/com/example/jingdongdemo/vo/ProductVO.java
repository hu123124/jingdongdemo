package com.example.jingdongdemo.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
  public class ProductVO {
      private Long id;
      private Long categoryId;
      private String name;
      private String subtitle;
      private String mainImage;
      private BigDecimal price;
      private Integer sales;
      private Integer status;
  }