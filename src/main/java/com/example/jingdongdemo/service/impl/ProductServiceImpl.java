package com.example.jingdongdemo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.example.jingdongdemo.dto.ProductPageRequest;
import com.example.jingdongdemo.entity.Product;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
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

    @Override
    public PageResultVO<ProductVO> listProduct(ProductPageRequest productPageRequest) {
        // 防御：空参数时用默认值
        int pageNum = productPageRequest.getPageNum() != null ? productPageRequest.getPageNum() : 1;
        int pageSize = productPageRequest.getPageSize() != null ? productPageRequest.getPageSize() : 10;
        //构建redis的key
        String cacheKey = "products:list:" + pageNum + ":" + pageSize + ":"
                + productPageRequest.getSort() + ":"
                +  (productPageRequest.getKeyword() != null ? productPageRequest.getKeyword() : "") + ":"
                + productPageRequest.getCategoryId();

        String lockValue = UUID.randomUUID().toString();   // 每个请求唯一，防止误删别人的锁

        //查询缓存
        PageResultVO<ProductVO> cached = (PageResultVO<ProductVO>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            //纯在缓存直接返回缓存
            log.info("商品查询命中缓存");
            return cached;
        }
        log.info("商品查询未命中缓存");
        /**
         * 防击穿
         */
        String rebuildLockKey = "lock:rebuild:" + cacheKey;
        //尝试添加该查询业务的锁
        Boolean gotLock = redisTemplate.opsForValue().setIfAbsent(rebuildLockKey,lockValue,5,TimeUnit.SECONDS);
        //if (gotLock)防止gotLock为null导致 NullPointerException
        if(Boolean.TRUE.equals(gotLock)){//成功获取锁
            try {
                //查询db
              return queryAndCache(pageNum,pageSize,productPageRequest,cacheKey);
            } finally {
                //查看是否为本次查询添加的锁，是就释放锁
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                        "return redis.call('del', KEYS[1]) else return 0 end";
                redisTemplate.execute(new DefaultRedisScript<>(script, Long.class),
                        Collections.singletonList(rebuildLockKey),
                        lockValue);
            }
        }
        for (int i = 0; i < 3; i++) {
            try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            cached = (PageResultVO<ProductVO>) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                log.info("自旋第{}次命中", i + 1);
                return cached;
            }
        }

        // ===== 4. 降级直查 DB =====
        log.warn("自旋 3 次未命中，降级查 DB");
        return queryAndCache(pageNum, pageSize, productPageRequest, cacheKey);
    }

    @Override
    public ProductSPUVO getDetailById(Long id) {
        ProductSPUVO productSPUVO =  productMapper.getProductById(id);
        List<ProductSKUVO> productSKUVOList =  productMapper.getProductSKUSBySPUId(productSPUVO.getId());
        productSPUVO.setSkus(productSKUVOList);
        return productSPUVO;
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
        Set<String> keys = redisTemplate.keys("products:list:*" );
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete( keys );
        }
    }

    private PageResultVO<ProductVO> queryAndCache(int pageNum, int pageSize, ProductPageRequest productPageRequest, String cacheKey) {


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

// 穿透：空数据短 TTL；正常数据随机 TTL 防雪崩
        if (list.isEmpty()) {
            //比正常ttl短是为了防止产生太多空缓存占用太多redis内存
            redisTemplate.opsForValue().set(cacheKey, result, 1, TimeUnit.MINUTES);
        } else {
            int ttl = 600 + ThreadLocalRandom.current().nextInt(60);
            redisTemplate.opsForValue().set(cacheKey, result, ttl, TimeUnit.SECONDS);
        }
        return result;
    }
}
