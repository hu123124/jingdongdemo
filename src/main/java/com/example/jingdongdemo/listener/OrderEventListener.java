package com.example.jingdongdemo.listener;

import com.example.jingdongdemo.event.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 订单事件监听器 — 异步处理，不阻塞下单主流程
 */
@Slf4j
@Component
public class OrderEventListener {

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("订单创建事件触发，订单号: {}, 用户ID: {}", event.getOrderNo(), event.getUserId());
    }
}