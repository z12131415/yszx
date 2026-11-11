package com.yunshu.common.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解 — AOP 切面自动记录
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogOperation {
    /** 操作类型：LOGIN / CREATE / UPDATE / DELETE / REVIEW 等 */
    String actionType();

    /** 操作对象类型 */
    String targetType() default "";

    /** 是否记录请求参数 */
    boolean logRequest() default false;
}
