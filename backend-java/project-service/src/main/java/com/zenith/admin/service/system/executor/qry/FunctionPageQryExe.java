package com.zenith.admin.service.system.executor.qry;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.data.FunctionDTO;
import com.zenith.admin.dto.query.FunctionPageQuery;
import com.zenith.admin.service.system.executor.converter.FunctionConvertor;
import com.zenith.admin.util.PageResponseUtils;
import com.zenith.admin.dataobject.FunctionDO;
import com.zenith.admin.mapper.FunctionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FunctionPageQryExe {

    private final FunctionMapper functionMapper;
    private final FunctionConvertor functionConvertor;

    public PageInfo<FunctionDTO> execute(FunctionPageQuery query) {
        LambdaQueryWrapper<FunctionDO> queryWrapper = new LambdaQueryWrapper<>();

        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            queryWrapper.like(FunctionDO::getName, query.getKeyword());
        }

        if (query.getType() != null && !query.getType().isEmpty()) {
            queryWrapper.eq(FunctionDO::getType, query.getType());
        }

        if (query.getMenuId() != null) {
            queryWrapper.eq(FunctionDO::getMenuId, query.getMenuId());
        }

        queryWrapper.orderByAsc(FunctionDO::getSort);

        PageInfo<FunctionDO> pageInfo = PageHelper.startPage(query.getPageIndex(), query.getPageSize())
                .doSelectPageInfo(() -> functionMapper.selectList(queryWrapper));
        return PageResponseUtils.convert(pageInfo, functionConvertor::toDTOList);
    }
}
