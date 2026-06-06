package com.zenith.admin.service.system.executor.qry;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.dataobject.ProcessTemplateDO;
import com.zenith.admin.dto.data.ProcessTemplateDTO;
import com.zenith.admin.mapper.ProcessTemplateMapper;
import com.zenith.admin.service.system.executor.converter.ProcessTemplateConvertor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProcessTemplateListActiveQryExe {

    private final ProcessTemplateMapper processTemplateMapper;
    private final ProcessTemplateConvertor processTemplateConvertor;

    public List<ProcessTemplateDTO> execute() {
        LambdaQueryWrapper<ProcessTemplateDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProcessTemplateDO::getStatus, 1);
        queryWrapper.orderByAsc(ProcessTemplateDO::getName);

        List<ProcessTemplateDO> list = processTemplateMapper.selectList(queryWrapper);
        return processTemplateConvertor.toDTOList(list);
    }
}
