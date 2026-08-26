package com.example.jingdongdemo.dto;

import lombok.Data;

import java.util.List;

@Data
  public class CartBatchDTO {
      private List<Long> ids;
      private Integer checked;  // 删时不用这个字段
  }