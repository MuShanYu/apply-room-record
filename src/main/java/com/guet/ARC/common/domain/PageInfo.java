package com.guet.ARC.common.domain;


import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class PageInfo<T> {

    public PageInfo() {
    }

    public PageInfo(Page<T> page) {
        this.page = page.getNumber() + 1;
        this.totalSize = page.getTotalElements();
        this.pageData = page.getContent();
    }

    public PageInfo(com.github.pagehelper.Page<T> page) {
        this.page = page.getPageNum();
        this.totalSize = page.getTotal();
        this.pageData = page.getResult();
    }

    private Integer page;
    private Long totalSize;
    private List<T> pageData;
}
