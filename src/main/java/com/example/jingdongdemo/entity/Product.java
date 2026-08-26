package com.example.jingdongdemo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品实体 —— 对应数据库 t_product 表
 * 字段名用驼峰命名，MyBatis 配置了 map-underscore-to-camel-case
 * 会自动把 create_time -> createTime
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {
    private Long id;
    private Long categoryId;
    private String name;
    private String subtitle;
    private String mainImage;
    private String subImages;
    private String detail;
    private BigDecimal price;
    private Integer stock;
    private Integer status;      // 0下架 1在售
    private Integer sales;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<ProductSKU> skuList;
}
