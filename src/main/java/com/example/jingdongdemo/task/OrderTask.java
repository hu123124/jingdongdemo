package com.example.jingdongdemo.task;

import com.example.jingdongdemo.entity.Order;
import com.example.jingdongdemo.entity.OrderItem;

import com.example.jingdongdemo.entity.Order;
import com.example.jingdongdemo.entity.OrderItem;
import com.example.jingdongdemo.mapper.OrderItemMapper;
import com.example.jingdongdemo.mapper.OrderMapper;
import com.example.jingdongdemo.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 定时任务 — 取消超时未支付订单
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTask {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;

    /** 每 30 秒扫一次，取消超过 30 分钟的待付款订单 */
    @Scheduled(fixedDelay = 30000)
    @Transactional
    public void cancelTimeoutOrders() {
        List<Order> timeoutOrders = orderMapper.findTimeoutUnpaidOrders();

        for (Order order : timeoutOrders) {
            log.info("超时取消订单: {}", order.getOrderNo());
            orderMapper.cancelOrder(order.getOrderNo());

            // 释放库存
            List<OrderItem> items = orderItemMapper.getReturnItem(order.getOrderNo());
            for (OrderItem item : items) {
                productMapper.returnSku(item);
            }
        }
    }
}