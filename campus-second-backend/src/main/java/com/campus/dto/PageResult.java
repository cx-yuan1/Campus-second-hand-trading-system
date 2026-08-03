package com.campus.dto;

import lombok.Data;

import java.util.List;

/**
 * 分页结果 DTO，确保前端正确解析 records 和 total
 */
@Data
public class PageResult<T> {
    private List<T> records;
    private long total;

    public PageResult(List<T> records, long total) {
        this.records = records;
        this.total = total;
    }
}
