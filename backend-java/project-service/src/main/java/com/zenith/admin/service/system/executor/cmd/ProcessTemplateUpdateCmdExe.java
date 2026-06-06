package com.zenith.admin.service.system.executor.cmd;

import com.alibaba.cola.exception.BizException;
import com.zenith.admin.dataobject.NodeTemplateDO;
import com.zenith.admin.dataobject.ProcessTemplateDO;
import com.zenith.admin.dto.data.NodeTemplateCreateCmd;
import com.zenith.admin.dto.data.ProcessTemplateUpdateCmd;
import com.zenith.admin.mapper.NodeTemplateMapper;
import com.zenith.admin.mapper.ProcessTemplateMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProcessTemplateUpdateCmdExe {

    private final ProcessTemplateMapper processTemplateMapper;
    private final NodeTemplateMapper nodeTemplateMapper;

    public void execute(ProcessTemplateUpdateCmd cmd) {
        ProcessTemplateDO templateDO = processTemplateMapper.selectById(cmd.getId());
        if (templateDO == null) {
            throw new BizException("TEMPLATE_NOT_FOUND", "流程模板不存在");
        }

        templateDO.setName(cmd.getName());
        templateDO.setDescription(cmd.getDescription());
        templateDO.setFormSchema(cmd.getFormSchema());
        templateDO.setVersion(templateDO.getVersion() + 1);
        processTemplateMapper.updateById(templateDO);

        nodeTemplateMapper.deleteByProcessTemplateId(cmd.getId());

        if (cmd.getNodes() != null && !cmd.getNodes().isEmpty()) {
            for (NodeTemplateCreateCmd nodeCmd : cmd.getNodes()) {
                NodeTemplateDO nodeDO = new NodeTemplateDO();
                nodeDO.setProcessTemplateId(cmd.getId());
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
