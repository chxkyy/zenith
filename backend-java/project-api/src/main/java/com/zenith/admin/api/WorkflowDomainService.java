package com.zenith.admin.api;

import com.zenith.admin.dataobject.ApprovalRecordDO;
import com.zenith.admin.dataobject.NodeTemplateDO;
import com.zenith.admin.dataobject.ProcessInstanceDO;
import com.zenith.admin.dataobject.TaskDO;
import com.zenith.admin.enums.ActionTypeEnum;

import java.util.List;

public interface WorkflowDomainService {

    List<Long> resolveApprovers(NodeTemplateDO node, Long initiatorId);

    void createTasksForNode(ProcessInstanceDO instance, NodeTemplateDO node);

    ApprovalRecordDO createApprovalRecord(ProcessInstanceDO instance, TaskDO task, Long operatorId, 
            ActionTypeEnum actionType, String opinion);

    List<TaskDO> getTasksByProcessInstanceAndNode(Long processInstanceId, Integer nodeOrder);

    boolean isAllOriginalTasksApproved(Long processInstanceId, Integer nodeOrder);

    void terminatePendingTasks(Long processInstanceId);

    void terminatePendingTasksExcept(Long processInstanceId, Long excludeTaskId);
}
