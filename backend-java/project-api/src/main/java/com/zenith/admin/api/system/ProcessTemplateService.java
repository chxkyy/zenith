package com.zenith.admin.api.system;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.cmd.ProcessTemplateCreateCmd;
import com.zenith.admin.dto.cmd.ProcessTemplateUpdateCmd;
import com.zenith.admin.dto.data.ProcessTemplateDTO;
import com.zenith.admin.dto.query.ProcessTemplatePageQuery;

import java.util.List;

public interface ProcessTemplateService {

    PageInfo<ProcessTemplateDTO> page(ProcessTemplatePageQuery query);

    ProcessTemplateDTO getById(Long id);

    List<ProcessTemplateDTO> listActive();

    void create(ProcessTemplateCreateCmd cmd);

    void update(ProcessTemplateUpdateCmd cmd);

    void updateStatus(Long id, Integer status);
}
