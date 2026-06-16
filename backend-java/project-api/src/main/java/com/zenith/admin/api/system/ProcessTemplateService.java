package com.zenith.admin.api.system;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.system.cmd.ProcessTemplateCreateCmd;
import com.zenith.admin.dto.system.cmd.ProcessTemplateUpdateCmd;
import com.zenith.admin.dto.system.data.ProcessTemplateDTO;
import com.zenith.admin.dto.system.qry.ProcessTemplatePageQuery;

import java.util.List;

public interface ProcessTemplateService {

    PageInfo<ProcessTemplateDTO> page(ProcessTemplatePageQuery query);

    ProcessTemplateDTO getById(Long id);

    List<ProcessTemplateDTO> listActive();

    void create(ProcessTemplateCreateCmd cmd);

    void update(ProcessTemplateUpdateCmd cmd);

    void updateStatus(Long id, Integer status);
}
