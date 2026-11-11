package com.yunshu.common.annotation;

import java.lang.annotation.*;

/**
 * 频控注解 — 用于 AOP 切面统一拦截
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimited {
    /** 限流键前缀，如 "sms:send" */
    String key() default "";

    /** 时间窗口（秒） */
    int windowSeconds() default 60;

    /** 最大请求次数 */
    int maxRequests() default 1;
}
