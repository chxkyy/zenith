package com.zenith.admin.service.system.impl;

import com.zenith.admin.api.system.WorkflowDomainService;
import com.zenith.admin.dto.data.TaskDTO;
import com.zenith.admin.enums.ActionTypeEnum;
import com.zenith.admin.service.system.executor.qry.WorkflowCheckApprovalQryExe;
import com.zenith.admin.service.system.executor.cmd.WorkflowCreateApprovalRecordCmdExe;
import com.zenith.admin.service.system.executor.cmd.WorkflowCreateTaskCmdExe;
import com.zenith.admin.service.system.executor.qry.WorkflowGetTasksQryExe;
import com.zenith.admin.service.system.executor.qry.WorkflowResolveApproversQryExe;
import com.zenith.admin.service.system.executor.cmd.WorkflowTerminateTaskCmdExe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkflowDomainServiceImpl implements WorkflowDomainService {

    private final WorkflowResolveApproversQryExe resolveApproversQryExe;
    private final WorkflowCreateTaskCmdExe createTaskCmdExe;
    private final WorkflowCreateApprovalRecordCmdExe createApprovalRecordCmdExe;
    private final WorkflowGetTasksQryExe getTasksQryExe;
    private final WorkflowCheckApprovalQryExe checkApprovalQryExe;
    private final WorkflowTerminateTaskCmdExe terminateTaskCmdExe;

    @Override
    public List<Long> resolveApprovers(Long nodeTemplateId, Long initiatorId) {
        return resolveApproversQryExe.execute(nodeTemplateId, initiatorId);
    }

    @Override
    public void createTasksForNode(Long processInstanceId, Long nodeTemplateId) {
        createTaskCmdExe.execute(processInstanceId, nodeTemplateId);
    }

    @Override
    public void createApprovalRecord(Long processInstanceId, Integer nodeOrder, String nodeName,
            Long taskId, Long operatorId, ActionTypeEnum actionType, String opinion) {
        createApprovalRecordCmdExe.execute(processInstanceId, nodeOrder, nodeName,
                taskId, operatorId, actionType, opinion);
    }

    @Override
    public List<TaskDTO> getTasksByProcessInstanceAndNode(Long processInstanceId, Integer nodeOrder) {
        return getTasksQryExe.execute(processInstanceId, nodeOrder);
    }

    @Override
    public boolean isAllOriginalTasksApproved(Long processInstanceId, Integer nodeOrder) {
        return checkApprovalQryExe.execute(processInstanceId, nodeOrder);
    }

    @Override
    public void terminatePendingTasks(Long processInstanceId) {
        terminateTaskCmdExe.execute(processInstanceId);
    }

    @Override
    public void terminatePendingTasksExcept(Long processInstanceId, Long excludeTaskId) {
        terminateTaskCmdExe.executeExcept(processInstanceId, excludeTaskId);
    }
}
