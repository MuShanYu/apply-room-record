package com.guet.ARC.common.domain;


import lombok.Data;

import java.util.List;

@Data
public class PageInfo<T> {
    private Integer page;
    private Long totalSize;
    private List<T> pageData;
}
