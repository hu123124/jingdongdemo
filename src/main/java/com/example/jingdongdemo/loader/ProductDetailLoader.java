package com.example.jingdongdemo.loader;

import com.example.jingdongdemo.mapper.ProductMapper;
import com.example.jingdongdemo.vo.ProductSKUVO;
import com.example.jingdongdemo.vo.ProductSPUVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品详情回源加载器：当 L1 Caffeine / L2 Redis 都未命中时，
 * 由它负责从 DB 组装完整详情（SPU + SKU + 展示价 = SKU 最低价）。
 * 独立成组件便于管理，与缓存机制（cache 包）解耦；返回 null 表示商品不存在。
 */
@Component
@RequiredArgsConstructor
public class ProductDetailLoader {

    private final ProductMapper productMapper;

    public ProductSPUVO loadDetail(Long id) {
        ProductSPUVO productSPUVO = productMapper.getProductById(id);
        if (productSPUVO == null) {
            return null;   // 组件空值会由缓存层回填空值标记防穿透
        }
        List<ProductSKUVO> productSKUVOList = productMapper.getProductSKUSBySPUId(productSPUVO.getId());
        productSPUVO.setSkus(productSKUVOList);
        if (productSKUVOList != null && !productSKUVOList.isEmpty()) {
            productSPUVO.setPrice(productSKUVOList.stream()
                    .map(ProductSKUVO::getPrice)
                    .min(BigDecimal::compareTo)
                    .orElse(null));
        }
        return productSPUVO;
    }
}