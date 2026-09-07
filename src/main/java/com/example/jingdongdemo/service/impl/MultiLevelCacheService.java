package com.example.jingdongdemo.cache;

import com.example.jingdongdemo.loader.ProductDetailLoader;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Caffeine(L1) + Redis(L2) + DB(L3) 多级缓存读写组件。
 *
 * 读：L1 → L2 → loader(DB)，逐级回填；删：evict / evictByPrefix 同时清两级。
 *
 * 防击穿：Caffeine 的 get(key, mappingFunction) 是原子加载——同一 key 的并发请求
 *         只会有一个线程执行 mapping（查 Redis/DB），其余阻塞等结果，进程内天然防击穿；
 *         多实例部署需跨进程互斥时，可在业务 loader 内自行加 Redisson 锁。
 * 防穿透：loader 返回 null 时回填空值标记（NULL_MARKER），Redis 用短 TTL 兜底。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MultiLevelCacheService {

    /** 空值标记：Caffeine 不允许存 null value，用占位串区分"没缓存"和"缓存了空" */
    private static final String NULL_MARKER = "\u0000NULL\u0000";

    private final Cache<String, Object> localCache;              // L1：Caffeine
    private final RedisTemplate<String, Object> redisTemplate;   // L2：Redis
    private final ProductDetailLoader productDetailLoader;

    /**
     * 三级读取（原子加载，进程内防击穿）。
     *
     * @param key            完整业务 key，如 product:detail:1
     * @param redisTtlSeconds Redis(L2) 过期秒数；空值会取更短的 TTL
     * @param dbLoader        L1/L2 都未命中时回源 DB 的加载函数，返回 null 表示"查无此数据"
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, long redisTtlSeconds, Supplier<T> dbLoader) {
        // L1 检查：命中直接返回（打日志方便观察命中链路）
        Object local = localCache.getIfPresent(key);
        if (local != null) {
            log.debug("[多级缓存] L1 命中: {}", key);
            return NULL_MARKER.equals(local) ? null : (T) local;
        }
        // L1 未命中：原子加载 L2/L3（同 key 并发只有一个线程执行 mapping，进程内防击穿）
        Object cached = localCache.get(key, k -> loadFromRedisOrDb(k, redisTtlSeconds, dbLoader));
        return NULL_MARKER.equals(cached) ? null : (T) cached;
    }

    /** L2/L3 加载：只在 L1 未命中且拿到该 key 的加载权时执行 */
    private Object loadFromRedisOrDb(String key, long redisTtlSeconds, Supplier<?> dbLoader) {
        // L2 Redis
        Object redisVal = redisTemplate.opsForValue().get(key);
        if (redisVal != null) {
            log.debug("[多级缓存] L2 命中,回填 L1: {}", key);
            return redisVal;
        }
        // L3 DB
        log.debug("[多级缓存] L1/L2 均未命中,加载 DB: {}", key);
        Object value = dbLoader.get();
        if (value == null) {
            // 空值也回填（防穿透）：Redis 短 TTL 兜底，业务真实变更走 evict 立即恢复
            long emptyTtl = Math.min(redisTtlSeconds, 60);
            redisTemplate.opsForValue().set(key, NULL_MARKER, emptyTtl, TimeUnit.SECONDS);
            log.debug("[多级缓存] 查无数据,空值回填: {}", key);
            return NULL_MARKER;
        }
        redisTemplate.opsForValue().set(key, value, redisTtlSeconds, TimeUnit.SECONDS);
        log.debug("[多级缓存] DB 加载完成,回填 L2+L1: {}", key);
        return value;
    }

    /** 写库/更新后调用：本地 + Redis 同时失效 */
    public void evict(String key) {
        localCache.invalidate(key);
        redisTemplate.delete(key);
        log.debug("[多级缓存] 已双删: {}", key);
    }

    /** 按前缀批量失效，如 products:list:* */
    public void evictByPrefix(String prefix) {
        localCache.asMap().keySet().removeIf(k -> k.startsWith(prefix));
        Set<String> keys = redisTemplate.keys(prefix + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
        log.debug("[多级缓存] 已按前缀 {} 批量失效", prefix);
    }
}