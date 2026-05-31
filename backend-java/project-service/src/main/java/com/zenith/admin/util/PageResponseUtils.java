package com.zenith.admin.util;

import com.alibaba.cola.dto.PageResponse;
import com.github.pagehelper.PageInfo;

import java.util.List;

public final class PageResponseUtils {

    private PageResponseUtils() {
    }

    public static <T> PageResponse<T> of(PageInfo<?> pageInfo) {
        return PageResponse.of(
            (List<T>) pageInfo.getList(),
            (int) pageInfo.getTotal(),
            pageInfo.getPageSize(),
            pageInfo.getPageNum()
        );
    }
}