package com.example.jingdongdemo.config;

import com.example.jingdongdemo.entity.Product;
import com.example.jingdongdemo.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 启动时把数据库里所有商品 id 全量种进布隆过滤器，
 * 防止历史数据（在过滤器创建之前就存在的商品）被误判成"不存在"
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductBloomFilterInitRunner implements ApplicationRunner {

    private final RBloomFilter<Long> bloomFilter;
    private final ProductMapper productMapper;

    @Override
    public void run(ApplicationArguments args) {
        List<Product> all = productMapper.listAllAdmin();   // 全量商品（含下架）
        for (Product p : all) {
            bloomFilter.add(p.getId());                      // add 幂等，重复无害
        }
        log.info("布隆过滤器初始化完成，共种入 {} 个商品 id", all.size());
    }
}