package com.example.jingdongdemo.service.impl;


import com.example.jingdongdemo.common.JwtUtils;
import com.example.jingdongdemo.dto.AddressRequest;
import com.example.jingdongdemo.dto.LoginRequest;
import com.example.jingdongdemo.dto.RegisterRequest;
import com.example.jingdongdemo.entity.Address;
import com.example.jingdongdemo.entity.User;
import com.example.jingdongdemo.mapper.AddressMapper;
import com.example.jingdongdemo.mapper.UserMapper;
import com.example.jingdongdemo.service.AddressService;
import com.example.jingdongdemo.service.AuthService;
import com.example.jingdongdemo.vo.AddressVO;
import com.example.jingdongdemo.vo.LoginResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {


    private final AddressMapper addressMapper;

    @Override
    public List<AddressVO> showAddresslist() {
        Address address = Address.builder()
                .userId((Long)SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .build();
        List<Address> addressList = addressMapper.getByUserId(address.getUserId());
        List<AddressVO> addressVOlist = new ArrayList<>();
        for (Address a:addressList){
            AddressVO vo = new AddressVO();
                BeanUtils.copyProperties(a,vo);
            addressVOlist.add(vo);
        }
        return addressVOlist;
    }

    @Override
    public AddressVO getAddressDetail(Long id) {
        Address address = addressMapper.getByAddressId(id);
        AddressVO addressVO = new AddressVO();
        BeanUtils.copyProperties(address,addressVO);
        return addressVO;
    }

    @Override
    public void buildAddress(AddressRequest addressRequest) {
        Address address = new Address();
        address.setUserId((Long)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if(addressRequest.getIsDefault() == 1){addressMapper.setAllisDefaultByUserId(address.getUserId());}
        BeanUtils.copyProperties(addressRequest,address);
        addressMapper.buildAddress(address);
    }

    @Override
    public void alterAddress(AddressRequest addressRequest, Long id) {
        Address address = new Address();
        address.setUserId((Long)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if(addressRequest.getIsDefault() == 1){addressMapper.setAllisDefaultByUserId(address.getUserId());}
        BeanUtils.copyProperties(addressRequest,address);
        address.setId(id);
        addressMapper.alterAddress(address);
    }

    @Override
    public void deleteAddress(Long id) {
        Address address =  addressMapper.getByAddressId(id);
        if(!address.getUserId().equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())){
            throw new RuntimeException("操作越界");
        }
        addressMapper.deleteAddress(id);
    }

    @Override
    public void setDefaultAddress(Long id) {
        Address address =  Address.builder()
                .id(id)
                .userId((Long)SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .isDefault(1)
                .build();
        addressMapper.setAllisDefaultByUserId(address.getUserId());
        addressMapper.alterAddress(address);
    }
}
