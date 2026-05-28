package com.zenith.admin.executor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.DictConvertor;
import com.zenith.admin.dataobject.DictItemDO;
import com.zenith.admin.dto.data.DictItemDTO;
import com.zenith.admin.dto.data.DictItemPageQuery;
import com.zenith.admin.mapper.DictItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DictPageItemsQryExe {

    private final DictItemMapper dictItemMapper;
    private final DictConvertor dictConvertor;

    public PageInfo<DictItemDTO> execute(DictItemPageQuery query) {
        LambdaQueryWrapper<DictItemDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DictItemDO::getType, query.getType());
        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            queryWrapper.and(wrapper -> {
                wrapper.like(DictItemDO::getLabel, query.getKeyword()).or().like(DictItemDO::getDictValue, query.getKeyword());
            });
        }
        queryWrapper.orderByAsc(DictItemDO::getSort);

        PageInfo<DictItemDO> pageInfo = PageHelper.startPage(query.getPageIndex(), query.getPageSize())
                .doSelectPageInfo(() -> dictItemMapper.selectList(queryWrapper));
        List<DictItemDTO> dtos = dictConvertor.toItemDTOList(pageInfo.getList());

        PageInfo<DictItemDTO> result = new PageInfo<>();
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());
        result.setList(dtos);
        return result;
    }
}
