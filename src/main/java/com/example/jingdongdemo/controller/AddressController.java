package com.example.jingdongdemo.controller;

import com.example.jingdongdemo.common.R;
import com.example.jingdongdemo.dto.AddressRequest;
import com.example.jingdongdemo.dto.LoginRequest;
import com.example.jingdongdemo.dto.RegisterRequest;
import com.example.jingdongdemo.service.AddressService;
import com.example.jingdongdemo.service.AuthService;
import com.example.jingdongdemo.vo.AddressVO;
import com.example.jingdongdemo.vo.LoginResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/addresses")
public class AddressController {

    private final AddressService addressService;

    @GetMapping
        public R<List<AddressVO>> showAddresslist(){
        return R.ok(addressService.showAddresslist());
    }

    @GetMapping("/{id}")
    public R<AddressVO> showAddress(@PathVariable Long id){
        return R.ok(addressService.getAddressDetail(id));
    }
    @PostMapping
    public R<Void> addAddress(@RequestBody AddressRequest addressRequest){
        addressService.buildAddress(addressRequest);
        return R.ok();
    }
    @PutMapping("/{id}")
    public R<Void> updateAddress(@PathVariable Long id, @RequestBody AddressRequest addressRequest){
        addressService.alterAddress(addressRequest,id);
        return R.ok();
    }
    @DeleteMapping("/{id}")
    public R<Void> deleteAddress(@PathVariable Long id){
        addressService.deleteAddress(id);
        return R.ok();
    }
    @PutMapping("/{id}/default")
    public R<Void> setDefaultAddress(@PathVariable Long id){
        addressService.setDefaultAddress(id);
        return R.ok();
    }
}
