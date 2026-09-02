package com.example.jingdongdemo.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 商品变更事件 — 商品新增/修改/上下架后，触发 ES 同步
 */
@Getter
@AllArgsConstructor
public class ProductChangedEvent {
    /** 商品 id（监听器会重新查库判断：该写入还是该删除） */
    private Long productId;
}