package com.example.jingdongdemo.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CaffeineConfig {

    /**
     * L1 本地缓存，全局单实例。
     * key 直接用完整业务 key（如 product:detail:1），与 Redis 保持一致，便于统一失效。
     */
    @Bean
    public Cache<String, Object> localCache(
            @Value("${cache.caffeine.maximum-size:10000}") long maximumSize,
            @Value("${cache.caffeine.expire-after-write-seconds:300}") long expireAfterWriteSeconds) {
        return Caffeine.newBuilder()
                .maximumSize(maximumSize)                                  // 容量上限，超了按 LRU 淘汰
                .expireAfterWrite(Duration.ofSeconds(expireAfterWriteSeconds)) // 写入后过期
                .recordStats()   // 记录命中率，联调时可查 CacheStats
                .build();
    }
}