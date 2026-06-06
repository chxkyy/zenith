package com.zenith.admin.util;

import com.alibaba.cola.dto.PageResponse;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.function.Function;

public final class PageResponseUtils {

    private PageResponseUtils() {
    }

    public static <T> PageResponse<T> of(PageInfo<?> pageInfo) {
        return PageResponse.of((List<T>) pageInfo.getList(), (int) pageInfo.getTotal(), pageInfo.getPageSize(),
                pageInfo.getPageNum());
    }

    /**
     * 将 PageInfo<DO> 转换为 PageInfo<DTO>
     *
     * @param source   源 PageInfo（通常包含 DO 列表）
     * @param converter DO列表 → DTO列表 的转换函数
     * @return 转换后的 PageInfo（包含 DTO 列表）
     */
    public static <D, T> PageInfo<T> convert(PageInfo<D> source, Function<List<D>, List<T>> converter) {
        List<T> list = converter.apply(source.getList());
        PageInfo<T> result = new PageInfo<>();
        result.setTotal(source.getTotal());
        result.setPageNum(source.getPageNum());
        result.setPageSize(source.getPageSize());
        result.setPages(source.getPages());
        result.setList(list);
        return result;
    }
}
