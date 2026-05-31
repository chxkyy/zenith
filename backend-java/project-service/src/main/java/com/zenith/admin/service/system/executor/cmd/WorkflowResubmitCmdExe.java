package com.zenith.admin.service.system.executor.cmd;

import com.zenith.admin.api.WorkflowDomainService;
import com.zenith.admin.dataobject.NodeTemplateDO;
import com.zenith.admin.dataobject.ProcessInstanceDO;
import com.zenith.admin.enums.ActionTypeEnum;
import com.zenith.admin.enums.ProcessStatusEnum;
import com.zenith.admin.mapper.NodeTemplateMapper;
import com.zenith.admin.mapper.ProcessInstanceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WorkflowResubmitCmdExe {

    private final ProcessInstanceMapper processInstanceMapper;
    private final NodeTemplateMapper nodeTemplateMapper;
    private final WorkflowDomainService workflowDomainService;

    public void execute(Long processInstanceId, String formData, Long currentUserId) {
        ProcessInstanceDO instance = processInstanceMapper.selectById(processInstanceId);
        if (instance == null) {
            throw new RuntimeException("流程实例不存在");
        }
        if (!ProcessStatusEnum.RETURNED.getCode().equals(instance.getStatus())) {
            throw new RuntimeException("当前状态不允许重新提交");
        }
        if (!instance.getInitiatorId().equals(currentUserId)) {
            throw new RuntimeException("只有发起人可以重新提交");
        }

        if (formData != null && !formData.isEmpty()) {
            instance.setFormData(formData);
        }
        instance.setStatus(ProcessStatusEnum.IN_PROGRESS.getCode());
        instance.setCurrentNodeOrder(1);
        processInstanceMapper.updateById(instance);

        List<NodeTemplateDO> nodes = nodeTemplateMapper.selectByProcessTemplateId(instance.getProcessTemplateId());
        NodeTemplateDO firstNode = nodes.stream()
                .filter(n -> n.getNodeOrder() == 1)
                .findFirst()
                .orElse(null);
        if (firstNode != null) {
            workflowDomainService.createTasksForNode(instance.getId(), firstNode.getId());
        }

        workflowDomainService.createApprovalRecord(processInstanceId, 0, "重新提交",
                null, currentUserId, ActionTypeEnum.RESUBMIT, null);
    }
}
