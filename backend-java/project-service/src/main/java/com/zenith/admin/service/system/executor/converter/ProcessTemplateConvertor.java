package com.zenith.admin.service.system.executor.converter;

import com.zenith.admin.dataobject.NodeTemplateDO;
import com.zenith.admin.dataobject.ProcessTemplateDO;
import com.zenith.admin.dto.system.data.NodeTemplateDTO;
import com.zenith.admin.dto.system.data.ProcessTemplateDTO;
import com.zenith.admin.enums.NodeTypeEnum;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProcessTemplateConvertor {

    public ProcessTemplateDTO toDTO(ProcessTemplateDO dO) {
        ProcessTemplateDTO dto = new ProcessTemplateDTO();
        dto.setId(dO.getId());
        dto.setCode(dO.getCode());
        dto.setName(dO.getName());
        dto.setDescription(dO.getDescription());
        dto.setFormSchema(dO.getFormSchema());
        dto.setStatus(dO.getStatus());
        dto.setStatusName(dO.getStatus() == 1 ? "启用" : "停用");
        dto.setVersion(dO.getVersion());
        if (dO.getCreatedTime() != null) {
            dto.setCreatedTime(dO.getCreatedTime().toEpochSecond(ZoneOffset.ofHours(8)));
        }
        return dto;
    }

    public NodeTemplateDTO nodeToDTO(NodeTemplateDO dO) {
        NodeTemplateDTO dto = new NodeTemplateDTO();
        dto.setId(dO.getId());
        dto.setProcessTemplateId(dO.getProcessTemplateId());
        dto.setNodeOrder(dO.getNodeOrder());
        dto.setNodeName(dO.getNodeName());
        dto.setNodeType(dO.getNodeType());
        dto.setNodeTypeName(NodeTypeEnum.getByCode(dO.getNodeType()) != null
                ? NodeTypeEnum.getByCode(dO.getNodeType()).getName() : "");
        dto.setApproverType(dO.getApproverType());
        dto.setOpinionRequired(dO.getOpinionRequired());
        return dto;
    }

    public List<ProcessTemplateDTO> toDTOList(List<ProcessTemplateDO> list) {
        return list.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<NodeTemplateDTO> nodeToDTOList(List<NodeTemplateDO> list) {
        return list.stream().map(this::nodeToDTO).collect(Collectors.toList());
    }
}
