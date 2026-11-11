package com.yunshu.common.core;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页查询请求基类
 */
@Data
public class PageQuery implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 页码，从 1 开始 */
    private Integer page = 1;

    /** 每页条数，最大 100 */
    private Integer pageSize = 20;

    /** 排序字段 */
    private String sortBy;

    /** 排序方向：asc / desc */
    private String sortOrder = "desc";

    public Integer getOffset() {
        return (page - 1) * pageSize;
    }

    public Integer getPageNum() {
        if (page == null || page < 1) return 1;
        return page;
    }

    public Integer getPageSize() {
        if (pageSize == null || pageSize < 1) return 20;
        return Math.min(pageSize, 100);
    }
}
