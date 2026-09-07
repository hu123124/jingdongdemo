package com.example.jingdongdemo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.example.jingdongdemo.dto.ProductPageRequest;
import com.example.jingdongdemo.entity.Product;
import com.example.jingdongdemo.entity.ProductSKU;
import com.example.jingdongdemo.loader.ProductDetailLoader;
import com.example.jingdongdemo.mapper.ProductMapper;
import com.example.jingdongdemo.service.ProductService;
import com.example.jingdongdemo.vo.PageResultVO;
import com.example.jingdongdemo.vo.ProductSKUVO;
import com.example.jingdongdemo.vo.ProductSPUVO;
import com.example.jingdongdemo.vo.ProductVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.jingdongdemo.event.ProductChangedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import com.example.jingdongdemo.service.EsProductSearchService;
import com.example.jingdongdemo.cache.MultiLevelCacheService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor  // Lombok：自动生成带所有 final 字段的构造方法，Spring 通过构造方法注入 Mapper
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ApplicationEventPublisher eventPublisher;
    private final EsProductSearchService esProductSearchService;
    /** ES 搜索总开关（生产故障时可一键关掉走 MySQL） */
    @Value("${es.search.enabled:true}")
    private boolean esSearchEnabled;
    // 字段区：加在 esProductSearchService 下面
    private final RBloomFilter<Long> bloomFilter;
    /** 空值缓存标记：区分"缓存里存了空"和"缓存里没有" */
    private static final String EMPTY_CACHE = "__EMPTY__";
    private final RedissonClient redissonClient;
    private final MultiLevelCacheService multiCacheService;
    private final ProductDetailLoader productDetailLoader;

    @Override
    public PageResultVO<ProductVO> listProduct(ProductPageRequest productPageRequest) {
        // ===== ES 全文检索分支：开关开启 且 有关键词 才走 ES =====
        if (esSearchEnabled && productPageRequest.getKeyword() != null
                && !productPageRequest.getKeyword().trim().isEmpty()) {
            try {
                return esProductSearchService.search(productPageRequest);
            } catch (Exception e) {
                log.warn("ES 搜索失败，降级 MySQL LIKE：{}", e.getMessage());
            }
        }
        // 防御：空参数时用默认值
        int pageNum = productPageRequest.getPageNum() != null ? productPageRequest.getPageNum() : 1;
        int pageSize = productPageRequest.getPageSize() != null ? productPageRequest.getPageSize() : 10;
        //构建redis的key
        String cacheKey = "products:list:" + pageNum + ":" + pageSize + ":"
                + productPageRequest.getSort() + ":"
                +  (productPageRequest.getKeyword() != null ? productPageRequest.getKeyword() : "") + ":"
                + productPageRequest.getCategoryId();
        // 随机 TTL（10 分钟±1 分钟）防雪崩；DB 回源、空值回填、防击穿全部交给多级缓存组件
        long ttl = 600 + ThreadLocalRandom.current().nextInt(60);
        return multiCacheService.get(cacheKey, ttl, () -> queryProductPage(pageNum, pageSize, productPageRequest));
    }


    @Override
    public ProductSPUVO getDetailById(Long id) {
        // ① 布隆过滤器前置拦截：说"不存在"就一定不存在，直接返回，不打缓存不打库
        if (!bloomFilter.contains(id)) {
            return null;
        }
        String cacheKey = "product:detail:" + id;
        // 随机 TTL（5 分钟±2 分钟）防雪崩；空值回填与防击穿由多级缓存组件负责
        long ttl = 300 + ThreadLocalRandom.current().nextInt(120);
        return multiCacheService.get(cacheKey, ttl, () -> productDetailLoader.loadDetail(id));
    }

    @Override
    public List<ProductVO> recommend(Integer limit) {
        return productMapper.getRecommend(limit);
    }
    //admin
    // ProductServiceImpl
    @Override
    public void updateStatus(Long id, Integer status) {
        productMapper.updateStatus(id, status);
        //先更新再删除，这里业务不涉及高并发，不考虑双删
        clearProductCache();
        multiCacheService.evict("product:detail:" + id);   // L1+L2 双删详情缓存        // 通知 ES 同步：上架→写文档，下架→syncOne 内部会删文档
        eventPublisher.publishEvent(new ProductChangedEvent(id));
    }

    // ==================== B端 ====================

    /**
     * B端 - 商品列表（含下架商品，分页）
     */
    @Override
    public PageResultVO<Product> adminList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> list = productMapper.listAllAdmin();
        PageInfo<Product> info = new PageInfo<>(list);
        PageResultVO<Product> result = new PageResultVO<>();
        result.setList(list); result.setTotal(info.getTotal());
        result.setPageNum(pageNum); result.setPageSize(pageSize); result.setPages(info.getPages());
        return result;
    }

    /**
     * B端 - 修改商品：SPU 信息 + 同步 SKU 列表（有 id 的更新、没 id 的新增、没提交的删除）
     */
    @Transactional
    @Override
    public void adminUpdate(Long id, Map<String, Object> body) {
        productMapper.updateProduct(id,
                (String) body.get("name"),
                (String) body.get("subtitle"),
                (String) body.get("mainImage"),
                (String) body.get("detail"));
        saveSkus(id, (List<Map<String, Object>>) body.get("skus"));
        //清理缓存
        clearProductCache();
        multiCacheService.evict("product:detail:" + id);
        bloomFilter.add(id);   // 商品存在 → 种进布隆过滤器

        // 通知 ES 同步（改名/改副标题/改 SKU 价格都会反映到文档）
        eventPublisher.publishEvent(new ProductChangedEvent(id));
    }

    /**
     * B端 - 新增商品：SPU + 批量 SKU（规格/价格/库存）
     * @return 新商品 id
     */
    @Transactional
    @Override
    public Long adminCreate(Map<String, Object> body) {
        Product p = new Product();
        p.setCategoryId(Long.valueOf(body.get("categoryId").toString()));
        p.setName((String) body.get("name"));
        p.setSubtitle((String) body.getOrDefault("subtitle", null));
        p.setMainImage((String) body.getOrDefault("mainImage", null));
        p.setDetail((String) body.getOrDefault("detail", null));
        p.setStock(0);
        p.setStatus(1);
        p.setSales(0);
        productMapper.insertProduct(p);

        saveSkus(p.getId(), (List<Map<String, Object>>) body.get("skus"));
        clearProductCache();
        bloomFilter.add(p.getId());
        // 通知 ES 同步（新商品进索引）
        eventPublisher.publishEvent(new ProductChangedEvent(p.getId()));
        return p.getId();
    }

    /**
     * B端 - 同步保存商品 SKU 列表：
     * 有 id 的走更新（保留 sku_id，避免购物车/订单引用失效），没 id 的新增，
     * 最后删除前端没有提交的旧 SKU
     */
    private void saveSkus(Long productId, List<Map<String, Object>> skus) {
        if (skus == null) return;
        List<Long> keepIds = new ArrayList<>();
        for (Map<String, Object> skuBody : skus) {
            ProductSKU sku = new ProductSKU();
            sku.setProductId(productId);
            sku.setSpec((String) skuBody.get("spec"));
            sku.setPrice(new BigDecimal(skuBody.get("price").toString()));
            sku.setStock(Integer.valueOf(skuBody.get("stock").toString()));
            sku.setImage((String) skuBody.get("image"));
            Object idObj = skuBody.get("id");
            if (idObj != null && !idObj.toString().isEmpty()) {
                // 已有 SKU：更新（保留 sku_id，购物车/订单引用不失效）
                Long skuId = Long.valueOf(idObj.toString());
                sku.setId(skuId);
                productMapper.updateSku(sku);
                keepIds.add(skuId);
            } else {
                // 新 SKU：插入（insertSku 回填自增 id）
                sku.setSkuCode(IdUtil.getSnowflakeNextIdStr());
                productMapper.insertSku(sku);
                keepIds.add(sku.getId());
            }
        }
        // 清理前端没保留的旧 SKU
        if (keepIds.isEmpty()) {
            productMapper.deleteSkusByProductId(productId);
        } else {
            productMapper.deleteSkusNotIn(productId, keepIds);
        }
    }

    /**
     * B端 - 删除商品列表缓存（商品增/改/上下架后调用，防止读到旧数据）
     * 多级缓存：本地 L1 + Redis L2 一起按前缀清
     */
    private void clearProductCache() {
        multiCacheService.evictByPrefix("products:list:");
    }

    /** DB 回源：分页查询商品列表（多级缓存未命中时由组件调用） */
    private PageResultVO<ProductVO> queryProductPage(int pageNum, int pageSize, ProductPageRequest productPageRequest) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> list = productMapper.listProducts(productPageRequest);
        List<ProductVO> voList = BeanUtil.copyToList(list, ProductVO.class);
        PageInfo<Product> pageInfo = new PageInfo<>(list);

        PageResultVO<ProductVO> result = new PageResultVO<>();
        result.setList(voList);
        result.setTotal(pageInfo.getTotal());
        result.setPages(pageInfo.getPages());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }
}
