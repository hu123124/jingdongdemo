package com.example.jingdongdemo.service;

import com.example.jingdongdemo.dto.AddressRequest;
import com.example.jingdongdemo.entity.Product;
import com.example.jingdongdemo.vo.AddressVO;

import java.util.List;

public interface AddressService {

    List<AddressVO> showAddresslist();

    AddressVO getAddressDetail(Long id);

    void buildAddress(AddressRequest addressRequest);

    void alterAddress(AddressRequest addressRequest, Long id);

    void deleteAddress(Long id);

    void setDefaultAddress(Long id);
}
