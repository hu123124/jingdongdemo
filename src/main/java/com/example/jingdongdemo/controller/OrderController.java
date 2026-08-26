package com.example.jingdongdemo.controller;

import com.example.jingdongdemo.common.R;
import com.example.jingdongdemo.dto.CartBatchDTO;
import com.example.jingdongdemo.dto.CartRequest;
import com.example.jingdongdemo.dto.OrderPageRequest;
import com.example.jingdongdemo.dto.OrderRequest;
import com.example.jingdongdemo.service.CartService;
import com.example.jingdongdemo.service.OrderService;
import com.example.jingdongdemo.vo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/confirm")
    public R<OrderConfirmVO> OrderConfirm(){
        return R.ok(orderService.OrderConfirm());
    }
    @PostMapping
    public R<OrderVO> Order(@RequestBody OrderRequest orderRequest){
        return R.ok(orderService.createOrder(orderRequest));
    }
    @GetMapping
    public R<PageResultVO<OrderListVO>> orderList(OrderPageRequest orderPageRequest){
        return R.ok(orderService.orderList(orderPageRequest));
    }
    @GetMapping("/{orderNo}")
    public R<OrderDetailVO>  orderDetail(@PathVariable("orderNo") String orderNo){
        return R.ok(orderService.orderDetail(orderNo));
    }
    @PutMapping("/{orderNo}/cancel")
    public R<Void>  cancelOrder(@PathVariable("orderNo") String orderNo){
        orderService.cancelOrder(orderNo);
        return R.ok();
    }
    @PutMapping("/{orderNo}/receive")
    public R<Void>  receiveOrder(@PathVariable("orderNo") String orderNo){
        orderService.receiveOrder(orderNo);
        return R.ok();
    }
}
