package com.zenith.admin.service.system.executor.qry;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.dataobject.DictDO;
import com.zenith.admin.dto.data.DictDTO;
import com.zenith.admin.dto.data.DictPageQuery;
import com.zenith.admin.mapper.DictMapper;
import com.zenith.admin.service.system.executor.converter.DictConvertor;
import com.zenith.admin.util.PageResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DictPageQryExe {

    private final DictConvertor dictConvertor;
    private final DictMapper dictMapper;

    public PageInfo<DictDTO> execute(DictPageQuery query) {
        LambdaQueryWrapper<DictDO> queryWrapper = new LambdaQueryWrapper<>();
        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            queryWrapper.and(wrapper -> wrapper.like(DictDO::getName, query.getKeyword()).or().like(DictDO::getType,
                    query.getKeyword()));
        }

        PageInfo<DictDO> pageInfo = PageHelper.startPage(query.getPageIndex(), query.getPageSize())
                .doSelectPageInfo(() -> dictMapper.selectList(queryWrapper));
        return PageResponseUtils.convert(pageInfo, dictConvertor::toDTOList);
    }
}
