package com.yunshu.framework.log;

import com.alibaba.fastjson2.JSON;
import com.yunshu.common.annotation.LogOperation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * 操作日志 AOP 切面 — 自动记录管理员操作日志
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final com.yunshu.system.mapper.OperationLogMapper operationLogMapper;

    @Around("@annotation(logOperation)")
    public Object around(ProceedingJoinPoint point, LogOperation logOperation) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = point.proceed();
        long costTime = System.currentTimeMillis() - startTime;

        log.info("操作日志 | actionType={} | targetType={} | cost={}ms",
                logOperation.actionType(), logOperation.targetType(), costTime);

        try {
            writeOperationLog(point, logOperation, costTime);
        } catch (Exception e) {
            log.warn("操作日志写入失败", e);
        }

        return result;
    }

    private void writeOperationLog(ProceedingJoinPoint point, LogOperation logOperation, long costTime) {
        HttpServletRequest request = getCurrentRequest();
        Long userId = getCurrentUserId();
        String username = getCurrentUsername();
        String ipAddr = request != null ? getClientIp(request) : "unknown";

        com.yunshu.system.domain.OperationLog operationLog = new com.yunshu.system.domain.OperationLog();
        operationLog.setUserId(userId);
        operationLog.setUsername(username);
        operationLog.setActionType(logOperation.actionType());
        operationLog.setTargetType(logOperation.targetType());
        operationLog.setIpAddress(ipAddr);
        operationLog.setDetail("cost=" + costTime + "ms");
        operationLog.setCreatedAt(LocalDateTime.now(ZoneOffset.ofHours(8)));

        // 尝试从参数中提取目标ID
        for (Object arg : point.getArgs()) {
            if (arg instanceof Long id) {
                operationLog.setTargetId(id);
            } else if (arg instanceof Long[] ids) {
                operationLog.setTargetId(ids.length > 0 ? ids[0] : null);
            }
        }

        operationLogMapper.insert(operationLog);
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Long) {
            return (Long) auth.getPrincipal();
        }
        return null;
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getDetails() instanceof String details) {
            return details;
        }
        return "system";
    }

    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs != null ? attrs.getRequest() : null;
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwarded = request.getHeader("X-Forwarded-For");
        if (xForwarded != null && !xForwarded.isEmpty() && !"unknown".equalsIgnoreCase(xForwarded)) {
            return xForwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
