package com.yunshu.common.core;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实体基类 — 所有数据库实体统一继承
 */
@Data
public abstract class BaseEntity {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    /** 逻辑删除标记：0=正常 1=已删除 */
    private Integer isDeleted = 0;
}
