package com.example.backend.dto.common;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;


@Data
public class PageResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;
    private boolean first;
    private boolean empty;

    /**
     * 主构造函数：从Page对象创建响应
     */
    public PageResponse(Page<T> page) {
        this.content = page.getContent();
        this.page = page.getNumber();
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.last = page.isLast();
        this.first = page.isFirst();
        this.empty = page.isEmpty();
    }

    /**
     * 备用构造函数：手动构建分页响应
     */
    public PageResponse(
            List<T> content,
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.last = (page + 1) >= totalPages;
        this.first = page == 0;
        this.empty = content.isEmpty();
    }
}
