package com.zenith.admin.executor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.DictConvertor;
import com.zenith.admin.dataobject.DictDO;
import com.zenith.admin.dto.data.DictDTO;
import com.zenith.admin.dto.data.DictPageQuery;
import com.zenith.admin.mapper.DictMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DictPageQryExe {

    private final DictMapper dictMapper;
    private final DictConvertor dictConvertor;

    public PageInfo<DictDTO> execute(DictPageQuery query) {
        LambdaQueryWrapper<DictDO> queryWrapper = new LambdaQueryWrapper<>();
        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            queryWrapper.and(wrapper -> {
                wrapper.like(DictDO::getName, query.getKeyword()).or().like(DictDO::getType, query.getKeyword());
            });
        }

        PageInfo<DictDO> pageInfo = PageHelper.startPage(query.getPageIndex(), query.getPageSize())
                .doSelectPageInfo(() -> dictMapper.selectList(queryWrapper));
        List<DictDTO> dtos = dictConvertor.toDTOList(pageInfo.getList());

        PageInfo<DictDTO> result = new PageInfo<>();
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());
        result.setList(dtos);
        return result;
    }
}
