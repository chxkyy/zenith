package com.zenith.admin.service.system.executor.qry;

import com.zenith.admin.dataobject.NodeTemplateDO;
import com.zenith.admin.dataobject.ProcessTemplateDO;
import com.zenith.admin.dto.data.NodeTemplateDTO;
import com.zenith.admin.dto.data.ProcessTemplateDTO;
import com.zenith.admin.mapper.NodeTemplateMapper;
import com.zenith.admin.mapper.ProcessTemplateMapper;
import com.zenith.admin.service.system.executor.converter.ProcessTemplateConvertor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProcessTemplateGetByIdQryExe {

    private final ProcessTemplateMapper processTemplateMapper;
    private final NodeTemplateMapper nodeTemplateMapper;
    private final ProcessTemplateConvertor processTemplateConvertor;

    public ProcessTemplateDTO execute(Long id) {
        ProcessTemplateDO templateDO = processTemplateMapper.selectById(id);
        if (templateDO == null) {
            return null;
        }

        ProcessTemplateDTO dto = processTemplateConvertor.toDTO(templateDO);

        List<NodeTemplateDO> nodes = nodeTemplateMapper.selectByProcessTemplateId(id);
        List<NodeTemplateDTO> nodeDTOs = processTemplateConvertor.nodeToDTOList(nodes);
        dto.setNodes(nodeDTOs);

        return dto;
    }
}
