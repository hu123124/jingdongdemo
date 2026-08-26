package com.example.jingdongdemo.vo;

  import lombok.AllArgsConstructor;
  import lombok.Data;
  import lombok.NoArgsConstructor;
  import java.util.List;

  @Data
  public class PageResultVO<T> {
      private List<T> list;
      private Long total;
      private Integer pageNum;
      private Integer pageSize;
      private Integer pages;
  }