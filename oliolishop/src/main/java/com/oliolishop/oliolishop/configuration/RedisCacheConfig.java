package com.oliolishop.oliolishop.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisCacheConfig {

    // 1. Định nghĩa cách cache sẽ serialize key và value
    @Bean
    public RedisCacheConfiguration cacheConfiguration(ObjectMapper objectMapper) {
        
        // Sử dụng ObjectMapper được inject bởi Spring để đảm bảo tính nhất quán
        GenericJackson2JsonRedisSerializer jsonSerializer =
            new GenericJackson2JsonRedisSerializer(objectMapper); 

        return RedisCacheConfiguration.defaultCacheConfig()
                // Cấu hình TTL mặc định (ví dụ: 30 phút)
                .entryTtl(Duration.ofMinutes(30))
                .disableCachingNullValues() 
                // Sử dụng String Serializer cho Key
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                // BẮT BUỘC: Sử dụng JSON Serializer cho Value
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer));
    }
    
    // 2. Tùy chỉnh TTL cho các cache names của location service
    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer(RedisCacheConfiguration cacheConfiguration) {
        return (builder) -> builder
                .withCacheConfiguration("provinces", cacheConfiguration.entryTtl(Duration.ofHours(1)))
                .withCacheConfiguration("districtsByProvince", cacheConfiguration.entryTtl(Duration.ofHours(1)))
                .withCacheConfiguration("wardsByDistrict", cacheConfiguration.entryTtl(Duration.ofHours(1)));
    }
}