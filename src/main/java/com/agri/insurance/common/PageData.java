package com.agri.insurance.common;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class PageData<T> {
    private List<T> content;
    private long total;
    private int page;
    private int size;
    private int totalPages;

    public PageData(Page<T> page) {
        this.content = page.getContent();
        this.total = page.getTotalElements();
        this.page = page.getNumber();
        this.size = page.getSize();
        this.totalPages = page.getTotalPages();
    }
} 