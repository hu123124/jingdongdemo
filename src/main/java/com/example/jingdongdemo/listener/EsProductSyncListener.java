package com.example.jingdongdemo.listener;

import com.example.jingdongdemo.event.ProductChangedEvent;
import com.example.jingdongdemo.service.EsProductSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 商品变更监听器 — 数据库事务提交后异步同步 ES。
 * ES 失败只记日志（最终一致），漏写由启动时全量同步兜底
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EsProductSyncListener {

    private final EsProductSyncService esProductSyncService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onProductChanged(ProductChangedEvent event) {
        try {
            esProductSyncService.syncOne(event.getProductId());
        } catch (Exception e) {
            log.error(">>> ES 同步失败，商品 id={}，将由启动全量同步兜底：{}",
                    event.getProductId(), e.getMessage(), e);
        }
    }
}