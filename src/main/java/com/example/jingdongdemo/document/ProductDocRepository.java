package com.example.jingdongdemo.document;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * ES 仓储：Spring Data 自动实现增删改查，
 * save/saveAll/deleteById 等开箱即用（不用写一行实现）
 */
public interface ProductDocRepository extends ElasticsearchRepository<ProductDoc, Long> {
}