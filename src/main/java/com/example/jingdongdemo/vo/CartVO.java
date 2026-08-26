package com.example.jingdongdemo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
  public class CartVO {
      private Long id;
      private Long productId;
      private String productName;
      private Long skuId;
      private String skuSpec;
      private String productImage;
      private BigDecimal price;
      private Integer quantity;
      private Integer checked;
      private Integer stock;
  }