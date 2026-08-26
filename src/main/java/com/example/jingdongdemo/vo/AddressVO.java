package com.example.jingdongdemo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AddressVO {
    private Long id;
    private String consignee;
    private String phone;
    private String province;
    private String city;
    private String district;
    private String detail;
    private Integer isDefault;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
