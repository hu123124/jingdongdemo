package com.example.jingdongdemo.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
  public class CartListVO {
      private List<CartVO> items;
      private Integer totalCount;
      private BigDecimal checkedAmount;
  }