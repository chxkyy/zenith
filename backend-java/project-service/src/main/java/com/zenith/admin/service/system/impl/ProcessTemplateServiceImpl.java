package com.zenith.admin.service.system.impl;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.ProcessTemplateService;
import com.zenith.admin.dto.data.*;
import com.zenith.admin.service.system.executor.cmd.ProcessTemplateCreateCmdExe;
import com.zenith.admin.service.system.executor.cmd.ProcessTemplateUpdateCmdExe;
import com.zenith.admin.service.system.executor.cmd.ProcessTemplateUpdateStatusCmdExe;
import com.zenith.admin.service.system.executor.qry.ProcessTemplateGetByIdQryExe;
import com.zenith.admin.service.system.executor.qry.ProcessTemplateListActiveQryExe;
import com.zenith.admin.service.system.executor.qry.ProcessTemplatePageQryExe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessTemplateServiceImpl implements ProcessTemplateService {

    private final ProcessTemplatePageQryExe processTemplatePageQryExe;
    private final ProcessTemplateGetByIdQryExe processTemplateGetByIdQryExe;
    private final ProcessTemplateListActiveQryExe processTemplateListActiveQryExe;
    private final ProcessTemplateCreateCmdExe processTemplateCreateCmdExe;
    private final ProcessTemplateUpdateCmdExe processTemplateUpdateCmdExe;
    private final ProcessTemplateUpdateStatusCmdExe processTemplateUpdateStatusCmdExe;

    @Override
    public PageInfo<ProcessTemplateDTO> page(ProcessTemplatePageQuery query) {
        return processTemplatePageQryExe.execute(query);
    }

    @Override
    public ProcessTemplateDTO getById(Long id) {
        return processTemplateGetByIdQryExe.execute(id);
    }

    @Override
    public List<ProcessTemplateDTO> listActive() {
        return processTemplateListActiveQryExe.execute();
    }

    @Override
    @Transactional
    public void create(ProcessTemplateCreateCmd cmd) {
        processTemplateCreateCmdExe.execute(cmd);
    }

    @Override
    @Transactional
    public void update(ProcessTemplateUpdateCmd cmd) {
        processTemplateUpdateCmdExe.execute(cmd);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        processTemplateUpdateStatusCmdExe.execute(id, status);
    }
}
