package com.zenith.admin.service.system.executor.cmd;

import com.zenith.admin.dataobject.ProcessTemplateDO;
import com.zenith.admin.mapper.ProcessTemplateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProcessTemplateUpdateStatusCmdExe {

    private final ProcessTemplateMapper processTemplateMapper;

    public void execute(Long id, Integer status) {
        ProcessTemplateDO templateDO = processTemplateMapper.selectById(id);
        if (templateDO != null) {
            templateDO.setStatus(status);
            processTemplateMapper.updateById(templateDO);
        }
    }
}
