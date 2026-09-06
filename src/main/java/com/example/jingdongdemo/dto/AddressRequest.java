package com.example.jingdongdemo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressRequest {
    @NotBlank(message = "收货人不能为空")
    private String consignee;
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    private String province;
    private String city;
    private String district;
    @NotBlank(message = "详细地址不能为空")
    private String detail;
    private Integer isDefault;
}
