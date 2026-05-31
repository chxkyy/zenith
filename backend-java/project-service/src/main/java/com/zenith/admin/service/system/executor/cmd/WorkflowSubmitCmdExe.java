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
public class WorkflowSubmitCmdExe {

    private final ProcessInstanceMapper processInstanceMapper;
    private final NodeTemplateMapper nodeTemplateMapper;
    private final WorkflowDomainService workflowDomainService;

    public void execute(Long processInstanceId, Long currentUserId) {
        ProcessInstanceDO instance = processInstanceMapper.selectById(processInstanceId);
        if (instance == null) {
            throw new RuntimeException("流程实例不存在");
        }
        if (!ProcessStatusEnum.DRAFT.getCode().equals(instance.getStatus())
                && !ProcessStatusEnum.RETURNED.getCode().equals(instance.getStatus())) {
            throw new RuntimeException("当前状态不允许提交");
        }

        List<NodeTemplateDO> nodes = nodeTemplateMapper.selectByProcessTemplateId(instance.getProcessTemplateId());
        if (nodes.isEmpty()) {
            throw new RuntimeException("流程模板没有配置审批节点");
        }

        NodeTemplateDO firstNode = nodes.stream()
                .filter(n -> n.getNodeOrder() == 1)
                .findFirst()
                .orElse(null);
        if (firstNode == null) {
            throw new RuntimeException("流程模板配置错误");
        }

        instance.setStatus(ProcessStatusEnum.IN_PROGRESS.getCode());
        instance.setCurrentNodeOrder(1);
        processInstanceMapper.updateById(instance);

        workflowDomainService.createTasksForNode(instance.getId(), firstNode.getId());

        workflowDomainService.createApprovalRecord(processInstanceId, 0, "发起申请",
                null, currentUserId, ActionTypeEnum.SUBMIT, null);
    }
}
