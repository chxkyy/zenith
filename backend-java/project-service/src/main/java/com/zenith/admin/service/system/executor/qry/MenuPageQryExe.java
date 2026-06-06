package com.zenith.admin.service.system.executor.qry;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.service.system.executor.converter.MenuConvertor;
import com.zenith.admin.util.PageResponseUtils;
import com.zenith.admin.dataobject.MenuDO;
import com.zenith.admin.dto.data.MenuDTO;
import com.zenith.admin.dto.query.MenuPageQuery;
import com.zenith.admin.mapper.MenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MenuPageQryExe {

    private final MenuMapper menuMapper;
    private final MenuConvertor menuConvertor;

    public PageInfo<MenuDTO> execute(MenuPageQuery query) {
        LambdaQueryWrapper<MenuDO> queryWrapper = new LambdaQueryWrapper<>();

        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            queryWrapper.and(wrapper -> {
                wrapper.like(MenuDO::getName, query.getKeyword())
                        .or().like(MenuDO::getPath, query.getKeyword());
            });
        }

        if (query.getType() != null && !query.getType().isEmpty()) {
            queryWrapper.eq(MenuDO::getType, query.getType());
        }

        if (query.getParentId() != null) {
            queryWrapper.eq(MenuDO::getParentId, query.getParentId());
        }

        queryWrapper.orderByAsc(MenuDO::getSort);

        PageInfo<MenuDO> pageInfo = PageHelper.startPage(query.getPageIndex(), query.getPageSize())
                .doSelectPageInfo(() -> menuMapper.selectList(queryWrapper));
        return PageResponseUtils.convert(pageInfo, menuConvertor::toDTOList);
    }
}
