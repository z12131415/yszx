package com.yunshu.framework.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis 缓存服务封装 — 频控/会话/计数统一入口
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CacheService {

    private final StringRedisTemplate redisTemplate;

    /**
     * 获取计数器值，不存在则返回 null
     */
    public Long getCounter(String key) {
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? Long.parseLong(value) : null;
    }

    /**
     * 递增计数器，返回新值
     */
    public Long increment(String key, long ttlSeconds) {
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            redisTemplate.expire(key, ttlSeconds, TimeUnit.SECONDS);
        }
        return count;
    }

    /**
     * 哈希递增
     */
    public Long hIncrement(String key, String field, long ttlSeconds) {
        Long count = redisTemplate.opsForHash().increment(key, field, 1);
        if (count != null && count == 1) {
            redisTemplate.expire(key, ttlSeconds, TimeUnit.SECONDS);
        }
        return count;
    }

    /**
     * 判断 key 是否存在
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 删除 key
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 设置字符串
     */
    public void set(String key, String value, long ttlSeconds) {
        redisTemplate.opsForValue().set(key, value, ttlSeconds, TimeUnit.SECONDS);
    }

    /**
     * 设置 hash 字段
     */
    public void hSet(String key, String field, String value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    /**
     * 获取 hash 字段值
     */
    public String hGet(String key, String field) {
        return (String) redisTemplate.opsForHash().get(key, field);
    }

    /**
     * JWT 加入黑名单
     */
    public void blacklistJwt(String jti, long remainingMs) {
        redisTemplate.opsForValue().set("jwt:blacklist:admin:" + jti, "1", remainingMs, TimeUnit.MILLISECONDS);
    }

    /**
     * 检查 JWT 是否在黑名单中
     */
    public boolean isJwtBlacklisted(String jti, boolean isAdmin) {
        String key = isAdmin ? "jwt:blacklist:admin:" + jti : "jwt:blacklist:student:" + jti;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 面试会话令牌存储（未完成的面试断点恢复）
     */
    public void setInterviewSession(String interviewId, String userId, long ttlMinutes) {
        redisTemplate.opsForValue().set(
                "interview:active:" + userId, interviewId, ttlMinutes, TimeUnit.MINUTES);
    }

    public String getInterviewSession(String userId) {
        return redisTemplate.opsForValue().get("interview:active:" + userId);
    }

    public void deleteInterviewSession(String userId) {
        redisTemplate.delete("interview:active:" + userId);
    }
}
