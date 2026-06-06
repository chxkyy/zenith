package com.zenith.admin.service.system.executor.cmd;

import com.zenith.admin.dataobject.NodeTemplateDO;
import com.zenith.admin.dataobject.ProcessTemplateDO;
import com.zenith.admin.dto.cmd.NodeTemplateCreateCmd;
import com.zenith.admin.dto.cmd.ProcessTemplateCreateCmd;
import com.zenith.admin.mapper.NodeTemplateMapper;
import com.zenith.admin.mapper.ProcessTemplateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProcessTemplateCreateCmdExe {

    private final ProcessTemplateMapper processTemplateMapper;
    private final NodeTemplateMapper nodeTemplateMapper;

    public void execute(ProcessTemplateCreateCmd cmd) {
        ProcessTemplateDO templateDO = new ProcessTemplateDO();
        templateDO.setCode(cmd.getCode());
        templateDO.setName(cmd.getName());
        templateDO.setDescription(cmd.getDescription());
        templateDO.setFormSchema(cmd.getFormSchema());
        templateDO.setStatus(1);
        templateDO.setVersion(1);
        processTemplateMapper.insert(templateDO);

        if (cmd.getNodes() != null && !cmd.getNodes().isEmpty()) {
            for (NodeTemplateCreateCmd nodeCmd : cmd.getNodes()) {
                NodeTemplateDO nodeDO = new NodeTemplateDO();
                nodeDO.setProcessTemplateId(templateDO.getId());
                nodeDO.setNodeOrder(nodeCmd.getNodeOrder());
                nodeDO.setNodeName(nodeCmd.getNodeName());
                nodeDO.setNodeType(nodeCmd.getNodeType());
                nodeDO.setApproverType(nodeCmd.getApproverType());
                nodeDO.setApproverValue(nodeCmd.getApproverValue());
                nodeDO.setOpinionRequired(nodeCmd.getOpinionRequired());
                nodeTemplateMapper.insert(nodeDO);
            }
        }
    }
}
