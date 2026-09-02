package com.example.jingdongdemo.service.impl;

import com.example.jingdongdemo.document.ProductDoc;
import com.example.jingdongdemo.document.ProductDocRepository;
import com.example.jingdongdemo.entity.Product;
import com.example.jingdongdemo.mapper.ProductMapper;
import com.example.jingdongdemo.service.EsProductSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EsProductSyncServiceImpl implements EsProductSyncService {

    private final ProductMapper productMapper;
    private final ProductDocRepository docRepository;
    private final ElasticsearchOperations operations;

    @Override
    public void syncOne(Long productId) {
        Product p = productMapper.getProductWithPrice(productId);
        if (p == null) {
            // 商品已不存在 → 删文档（防脏数据）
            docRepository.deleteById(productId);
            log.info(">>> ES 同步：商品 {} 不存在，已删除文档", productId);
            return;
        }
        if (p.getStatus() == null || p.getStatus() != 1) {
            // 下架 → 删文档（下架商品不该被搜到）
            docRepository.deleteById(productId);
            log.info(">>> ES 同步：商品 {} 已下架，已删除文档", productId);
            return;
        }
        docRepository.save(toDoc(p));
        log.info(">>> ES 同步：商品 {} 已写入/更新", productId);
    }

    @Override
    public void syncFull() {
        List<Product> onSale = productMapper.listAllAdmin().stream()
                .filter(p -> p.getStatus() != null && p.getStatus() == 1)
                .toList();
        // 全量重建式同步：删索引 → 重建（带 mapping）→ 批量写入。MySQL 是唯一事实源，重启即对账
        IndexOperations indexOps = operations.indexOps(ProductDoc.class);
        if (indexOps.exists()) {
            indexOps.delete();
        }
        indexOps.createWithMapping();
        docRepository.saveAll(onSale.stream().map(this::toDoc).toList());
        log.info(">>> ES 全量同步完成，共写入 {} 个在售商品", onSale.size());
    }

    private ProductDoc toDoc(Product p) {
        return ProductDoc.builder()
                .id(p.getId())
                .name(p.getName())
                .subtitle(p.getSubtitle())
                .categoryId(p.getCategoryId() == null ? null : String.valueOf(p.getCategoryId()))
                .price(p.getPrice())
                .mainImage(p.getMainImage())
                .sales(p.getSales())
                .build();
    }
}