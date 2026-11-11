package com.yunshu.framework.ratelimit;

import com.yunshu.common.annotation.RateLimited;
import com.yunshu.common.enums.ErrorCode;
import com.yunshu.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.concurrent.TimeUnit;

/**
 * 频控 AOP 切面 — 基于 Redis 滑动窗口计数器实现
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final StringRedisTemplate redisTemplate;

    @Around("@annotation(rateLimited)")
    public Object around(ProceedingJoinPoint point, RateLimited rateLimited) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = getClientIp(request);
        String keyPrefix = rateLimited.key();

        // 使用固定窗口计数，key 为 ip + keyPrefix
        String redisKey = "ratelimit:api:" + ip + ":" + keyPrefix;
        Long count = redisTemplate.opsForValue().increment(redisKey);
        if (count != null && count == 1) {
            redisTemplate.expire(redisKey, rateLimited.windowSeconds(), TimeUnit.SECONDS);
        }

        if (count != null && count > rateLimited.maxRequests()) {
            log.warn("触发频控限制: key={}, ip={}", keyPrefix, ip);
            throw new BusinessException(ErrorCode.TOO_MANY_REQUESTS);
        }

        return point.proceed();
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwarded = request.getHeader("X-Forwarded-For");
        if (xForwarded != null && !xForwarded.isEmpty() && !"unknown".equalsIgnoreCase(xForwarded)) {
            return xForwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
