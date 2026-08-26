package com.example.jingdongdemo.vo;

import lombok.Data;

import java.util.List;

@Data
public class CategoryVO {
    private Long id;
    private String name;
    private String icon;
    private List<CategoryVO> children;
}
