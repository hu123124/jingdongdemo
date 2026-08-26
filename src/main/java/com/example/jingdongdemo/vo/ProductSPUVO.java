package com.example.jingdongdemo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
  public class ProductSPUVO {
      private Long id;
      private String name;
      private String subtitle;
      private Long categoryId;
      private String mainImage;
      private String subImages;
      private String detail;
      private BigDecimal price;
      private Integer stock;
      private Integer sales;
      private Integer status;
      private List<ProductSKUVO> skus;
  }