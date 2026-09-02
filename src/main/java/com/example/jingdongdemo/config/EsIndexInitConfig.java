package com.example.jingdongdemo.config;

import com.example.jingdongdemo.service.EsProductSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class EsIndexInitConfig {

    private final EsProductSyncService esProductSyncService;

    /**
     * 启动时全量同步兜底：删索引→重建→从 MySQL 全量写入在售商品。
     * 双写漏掉的、历史欠账在这里一次对账补齐；
     * ES 不可用只 warn 不阻塞业务启动（搜索走降级，第 7 步完善）
     */
    @Bean
    public ApplicationRunner esProductIndexInit() {
        return args -> {
            try {
                esProductSyncService.syncFull();
                log.info(">>> ES 启动全量同步完成");
            } catch (Exception e) {
                log.warn(">>> ES 不可用，跳过启动全量同步（搜索将降级）：{}", e.getMessage());
            }
        };
    }
}