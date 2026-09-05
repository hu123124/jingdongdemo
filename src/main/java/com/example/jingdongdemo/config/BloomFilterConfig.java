package com.example.jingdongdemo.config;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BloomFilterConfig {

    /** 商品 id 布隆过滤器：预估 10 万个 id，误判率 1% */
    @Bean
    public RBloomFilter<Long> productSkuBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<Long> bloomFilter = redissonClient.getBloomFilter("bloom:product:sku");
        // tryInit 幂等：Redis 里已存在同 key 的过滤器时不会重置数据
        bloomFilter.tryInit(100_000L, 0.01);
        return bloomFilter;
    }
}