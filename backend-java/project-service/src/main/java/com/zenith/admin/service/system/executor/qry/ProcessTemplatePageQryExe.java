package com.zenith.admin.service.system.executor.qry;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.dataobject.ProcessTemplateDO;
import com.zenith.admin.dto.system.data.ProcessTemplateDTO;
import com.zenith.admin.dto.system.qry.ProcessTemplatePageQuery;
import com.zenith.admin.mapper.ProcessTemplateMapper;
import com.zenith.admin.service.system.executor.converter.ProcessTemplateConvertor;
import com.zenith.admin.util.PageResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProcessTemplatePageQryExe {

    private final ProcessTemplateMapper processTemplateMapper;
    private final ProcessTemplateConvertor processTemplateConvertor;

    public PageInfo<ProcessTemplateDTO> execute(ProcessTemplatePageQuery query) {
        LambdaQueryWrapper<ProcessTemplateDO> queryWrapper = new LambdaQueryWrapper<>();
        if (query.getName() != null && !query.getName().isEmpty()) {
            queryWrapper.like(ProcessTemplateDO::getName, query.getName());
        }
        if (query.getStatus() != null) {
            queryWrapper.eq(ProcessTemplateDO::getStatus, query.getStatus());
        }
        queryWrapper.orderByDesc(ProcessTemplateDO::getCreatedTime);

        PageInfo<ProcessTemplateDO> pageInfo = PageHelper.startPage(query.getPageIndex(), query.getPageSize())
                .doSelectPageInfo(() -> processTemplateMapper.selectList(queryWrapper));

        return PageResponseUtils.convert(pageInfo, processTemplateConvertor::toDTOList);
    }
}
