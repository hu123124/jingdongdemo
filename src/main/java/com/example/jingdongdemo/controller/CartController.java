package com.example.jingdongdemo.controller;

import com.example.jingdongdemo.common.R;
import com.example.jingdongdemo.dto.AddressRequest;
import com.example.jingdongdemo.dto.CartBatchDTO;
import com.example.jingdongdemo.dto.CartRequest;
import com.example.jingdongdemo.service.AddressService;
import com.example.jingdongdemo.service.CartService;
import com.example.jingdongdemo.vo.AddressVO;
import com.example.jingdongdemo.vo.CartListVO;
import com.example.jingdongdemo.vo.CartVO;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Delete;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart")
public class CartController {
    private final CartService cartService;

    @PostMapping
    public R<Void> addCart(@RequestBody CartRequest cartRequest){
        cartService.addCart(cartRequest);
        return R.ok();
    }

    @GetMapping
    public R<CartListVO> getCart(){
        return R.ok(cartService.getCartList());
    }

    @PutMapping("/{id}/quantity")
    public R<Void> updateQuantity(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        cartService.updateQuantity(id, body.get("quantity"));
        return R.ok();
    }

    @PutMapping("/{id}/checked")
    public R<Void> checkedCart(@PathVariable Long id,@RequestBody Map<String, Integer> body){
        cartService.updateChecked(id,body.get("checked"));
        return R.ok();
    }
    @PutMapping("/checked/batch")
    public R<Void> batchCheckedCart( @RequestBody CartBatchDTO cartBatchDTO){
        cartService.updateBatchChecked(cartBatchDTO);
        return R.ok();
    }
    @DeleteMapping("/{id}")
    public R<Void> deleteCart(@PathVariable Long id){
        cartService.deleteCart(id);
        return R.ok();
    }
    @DeleteMapping("/batch")
    public R<Void> batchDeleteCart( @RequestBody CartBatchDTO cartBatchDTO){
        cartService.batchDeleteCart(cartBatchDTO);
        return R.ok();
    }
}
