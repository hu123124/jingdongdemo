package com.example.jingdongdemo.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单创建事件 — 下单后异步解耦（发短信、记日志等）
 */
@Getter
@AllArgsConstructor
public class OrderCreatedEvent {
    private String orderNo;
    private Long userId;
}