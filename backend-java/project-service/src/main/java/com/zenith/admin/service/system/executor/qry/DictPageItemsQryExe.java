package com.zenith.admin.service.system.executor.qry;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.dataobject.DictItemDO;
import com.zenith.admin.dto.system.data.DictItemDTO;
import com.zenith.admin.dto.system.qry.DictItemPageQuery;
import com.zenith.admin.mapper.DictItemMapper;
import com.zenith.admin.service.system.executor.converter.DictConvertor;
import com.zenith.admin.util.PageResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DictPageItemsQryExe {

    private final DictConvertor dictConvertor;
    private final DictItemMapper dictItemMapper;

    public PageInfo<DictItemDTO> execute(DictItemPageQuery query) {
        LambdaQueryWrapper<DictItemDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DictItemDO::getType, query.getType());
        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            queryWrapper.and(wrapper -> wrapper.like(DictItemDO::getLabel, query.getKeyword()).or().like(DictItemDO::getDictValue, query.getKeyword()));
        }
        queryWrapper.orderByAsc(DictItemDO::getSort);

        PageInfo<DictItemDO> pageInfo = PageHelper.startPage(query.getPageIndex(), query.getPageSize())
                .doSelectPageInfo(() -> dictItemMapper.selectList(queryWrapper));
        return PageResponseUtils.convert(pageInfo, dictConvertor::toItemDTOList);
    }
}
