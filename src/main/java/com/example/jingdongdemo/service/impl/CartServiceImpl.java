package com.example.jingdongdemo.service.impl;


import com.example.jingdongdemo.dto.AddressRequest;
import com.example.jingdongdemo.dto.CartBatchDTO;
import com.example.jingdongdemo.dto.CartRequest;
import com.example.jingdongdemo.entity.Address;
import com.example.jingdongdemo.entity.Cart;
import com.example.jingdongdemo.mapper.AddressMapper;
import com.example.jingdongdemo.mapper.CartMapper;
import com.example.jingdongdemo.service.AddressService;
import com.example.jingdongdemo.service.CartService;
import com.example.jingdongdemo.vo.AddressVO;
import com.example.jingdongdemo.vo.CartListVO;
import com.example.jingdongdemo.vo.CartVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartMapper cartMapper;

    @Override
    public void addCart(CartRequest cartRequest) {
        Cart cart = Cart.builder()
                .productId(cartRequest.getProductId())
                .skuId(cartRequest.getSkuId())
                .quantity(cartRequest.getQuantity())
                .userId((Long)SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .checked(1)
                .build();
        cartMapper.addCart(cart);
    }

    @Override
    public CartListVO getCartList() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<CartVO> cartVOList = cartMapper.getCarts(userId);
        CartListVO cartListVO = new CartListVO();
        cartListVO.setItems(cartVOList);
        int totalCount = 0;
        BigDecimal checkedAmount = BigDecimal.ZERO;
        for (CartVO item : cartVOList) {
            if (item.getQuantity() != null) totalCount += item.getQuantity();
            if (item.getChecked() != null && item.getChecked() == 1
                    && item.getPrice() != null && item.getQuantity() != null) {
                checkedAmount = checkedAmount.add(
                        item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                );
            }
        }
        cartListVO.setTotalCount(totalCount);
        cartListVO.setCheckedAmount(checkedAmount);
        return cartListVO;
    }

    @Override
    public void updateQuantity(Long id, Integer quantity) {
        cartMapper.updateQuantityWithId(id,quantity);
    }

    @Override
    public void updateChecked(Long id,Integer checked) {
        cartMapper.updateChecked(id,checked);
    }

    @Override
    public void updateBatchChecked(CartBatchDTO cartBatchDTO) {
        cartMapper.updateBatchChecked(cartBatchDTO.getChecked(),cartBatchDTO.getIds());
    }

    @Override
    public void deleteCart(Long id) {
        cartMapper.deleteCart(id);
    }

    @Override
    public void batchDeleteCart(CartBatchDTO cartBatchDTO) {
        cartMapper.batchDeleteCart(cartBatchDTO.getIds());
    }
}
