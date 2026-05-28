package com.zenith.admin.executor;

import com.alibaba.cola.exception.BizException;
import com.zenith.admin.api.WorkflowDomainService;
import com.zenith.admin.dataobject.ProcessInstanceDO;
import com.zenith.admin.dataobject.TaskDO;
import com.zenith.admin.enums.ActionTypeEnum;
import com.zenith.admin.enums.ProcessStatusEnum;
import com.zenith.admin.enums.TaskStatusEnum;
import com.zenith.admin.mapper.ProcessInstanceMapper;
import com.zenith.admin.mapper.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TaskTerminateCmdExe {

    private final TaskMapper taskMapper;
    private final ProcessInstanceMapper processInstanceMapper;
    private final WorkflowDomainService workflowDomainService;

    public void execute(Long taskId, String opinion, Long currentUserId) {
        TaskDO task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BizException("TASK_NOT_FOUND", "任务不存在");
        }
        if (!TaskStatusEnum.PENDING.getCode().equals(task.getStatus())) {
            throw new BizException("TASK_PROCESSED", "任务已被处理");
        }
        if (!task.getAssigneeId().equals(currentUserId)) {
            throw new BizException("NO_PERMISSION", "无权处理此任务");
        }

        task.setStatus(TaskStatusEnum.TERMINATED.getCode());
        task.setOpinion(opinion);
        task.setActionType(ActionTypeEnum.TERMINATE.getCode());
        task.setActionTime(LocalDateTime.now());
        taskMapper.updateById(task);

        ProcessInstanceDO instance = processInstanceMapper.selectById(task.getProcessInstanceId());
        instance.setStatus(ProcessStatusEnum.PASSED.getCode());
        processInstanceMapper.updateById(instance);

        workflowDomainService.terminatePendingTasks(task.getProcessInstanceId());
        workflowDomainService.createApprovalRecord(instance.getId(), task.getNodeOrder(), task.getNodeName(),
                task.getId(), currentUserId, ActionTypeEnum.TERMINATE, opinion);
    }
}
