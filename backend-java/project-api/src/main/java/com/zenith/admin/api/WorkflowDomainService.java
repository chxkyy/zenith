package com.zenith.admin.api;

import com.zenith.admin.dto.data.TaskDTO;
import com.zenith.admin.enums.ActionTypeEnum;

import java.util.List;

public interface WorkflowDomainService {

    List<Long> resolveApprovers(Long nodeTemplateId, Long initiatorId);

    void createTasksForNode(Long processInstanceId, Long nodeTemplateId);

    void createApprovalRecord(Long processInstanceId, Integer nodeOrder, String nodeName,
            Long taskId, Long operatorId, ActionTypeEnum actionType, String opinion);

    List<TaskDTO> getTasksByProcessInstanceAndNode(Long processInstanceId, Integer nodeOrder);

    boolean isAllOriginalTasksApproved(Long processInstanceId, Integer nodeOrder);

    void terminatePendingTasks(Long processInstanceId);

    void terminatePendingTasksExcept(Long processInstanceId, Long excludeTaskId);
}
