package com.example.jingdongdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        // Key 用 String 序列化（可读性好）
        template.setKeySerializer(new StringRedisSerializer());
        // Value 用 Jackson JSON 序列化（Java 对象 → JSON，拿出来直接能用）
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        // Hash 的 key 和 value 也用同样的序列化
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());  // 和这行

        template.afterPropertiesSet();
        return template;
    }
}
