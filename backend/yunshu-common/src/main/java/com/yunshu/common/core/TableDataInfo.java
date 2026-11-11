package com.yunshu.common.core;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果包装
 */
@Data
public class TableDataInfo<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 总记录数 */
    private Long total;

    /** 列表数据 */
    private List<T> list;

    /** 当前页码 */
    private Integer pageNum;

    /** 每页条数 */
    private Integer pageSize;

    /** 总页数 */
    private Integer totalPages;

    public static <T> TableDataInfo<T> of(List<T> list, long total, int pageNum, int pageSize) {
        TableDataInfo<T> result = new TableDataInfo<>();
        result.setList(list);
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setTotalPages((int) Math.ceil((double) total / pageSize));
        return result;
    }
}
