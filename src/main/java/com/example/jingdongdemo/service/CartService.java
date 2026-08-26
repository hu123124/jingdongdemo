package com.example.jingdongdemo.service;

import com.example.jingdongdemo.dto.AddressRequest;
import com.example.jingdongdemo.dto.CartBatchDTO;
import com.example.jingdongdemo.dto.CartRequest;
import com.example.jingdongdemo.vo.AddressVO;
import com.example.jingdongdemo.vo.CartListVO;
import com.example.jingdongdemo.vo.CartVO;

import java.util.List;

public interface CartService {


    void addCart(CartRequest cartRequest);

    CartListVO getCartList();

    void updateQuantity(Long id, Integer quantity);

    void updateChecked(Long id, Integer checked);

    void updateBatchChecked(CartBatchDTO cartBatchDTO);

    void deleteCart(Long id);

    void batchDeleteCart(CartBatchDTO cartBatchDTO);
}
