package com.example.jingdongdemo.service;

public interface EsProductSyncService {
    /** 按商品 id 同步单条到 ES（在售→写入/更新，下架或删除→移除文档） */
    void syncOne(Long productId);

    /** 全量重建索引并写入所有在售商品（启动兜底，幂等） */
    void syncFull();
}