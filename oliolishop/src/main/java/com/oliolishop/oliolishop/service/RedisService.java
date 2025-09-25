package com.oliolishop.oliolishop.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oliolishop.oliolishop.configuration.RedisConfig;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class RedisService {
    StringRedisTemplate redisTemplate;
    ObjectMapper objectMapper =new ObjectMapper();

    // Lưu object -> JSON
    public <T> void set(String key, T data, long timeoutSeconds) {
        try {
            String json = objectMapper.writeValueAsString(data);
            redisTemplate.opsForValue().set(key, json, Duration.ofSeconds(timeoutSeconds));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing object", e);
        }
    }

    // Lấy JSON -> object
    public <T> T get(String key, Class<T> clazz) {
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) return null;
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error deserializing object", e);
        }
    }
}
