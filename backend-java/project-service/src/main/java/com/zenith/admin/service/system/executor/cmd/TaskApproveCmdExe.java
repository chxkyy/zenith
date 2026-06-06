package com.zenith.admin.service.system.executor.cmd;

import com.alibaba.cola.exception.BizException;
import com.zenith.admin.api.system.WorkflowDomainService;
import com.zenith.admin.dataobject.NodeTemplateDO;
import com.zenith.admin.dataobject.ProcessInstanceDO;
import com.zenith.admin.dataobject.TaskDO;
import com.zenith.admin.dto.data.TaskApproveCmd;
import com.zenith.admin.enums.ActionTypeEnum;
import com.zenith.admin.enums.NodeTypeEnum;
import com.zenith.admin.enums.ProcessStatusEnum;
import com.zenith.admin.enums.TaskStatusEnum;
import com.zenith.admin.mapper.NodeTemplateMapper;
import com.zenith.admin.mapper.ProcessInstanceMapper;
import com.zenith.admin.mapper.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TaskApproveCmdExe {

    private final TaskMapper taskMapper;
    private final ProcessInstanceMapper processInstanceMapper;
    private final NodeTemplateMapper nodeTemplateMapper;
    private final WorkflowDomainService workflowDomainService;

    public void execute(TaskApproveCmd cmd, Long currentUserId) {
        TaskDO task = taskMapper.selectById(cmd.getTaskId());
        if (task == null) {
            throw new BizException("TASK_NOT_FOUND", "任务不存在");
        }
        if (!TaskStatusEnum.PENDING.getCode().equals(task.getStatus())) {
            throw new BizException("TASK_PROCESSED", "任务已被处理");
        }
        if (!task.getAssigneeId().equals(currentUserId)) {
            throw new BizException("NO_PERMISSION", "无权处理此任务");
        }

        task.setStatus(TaskStatusEnum.APPROVED.getCode());
        task.setOpinion(cmd.getOpinion());
        task.setActionType(ActionTypeEnum.APPROVE.getCode());
        task.setActionTime(LocalDateTime.now());
        int rows = taskMapper.updateById(task);
        if (rows == 0) {
            throw new BizException("TASK_CONCURRENT", "任务已被其他人处理");
        }

        ProcessInstanceDO instance = processInstanceMapper.selectById(task.getProcessInstanceId());
        workflowDomainService.createApprovalRecord(instance.getId(), task.getNodeOrder(), task.getNodeName(),
                task.getId(), currentUserId, ActionTypeEnum.APPROVE, cmd.getOpinion());

        processAfterApprove(instance, task);
    }

    private void processAfterApprove(ProcessInstanceDO instance, TaskDO currentTask) {
        if (NodeTypeEnum.COUNTERSIGN.getCode().equals(currentTask.getNodeType())) {
            if (!workflowDomainService.isAllOriginalTasksApproved(instance.getId(), currentTask.getNodeOrder())) {
                return;
            }
        }

        List<NodeTemplateDO> nodes = nodeTemplateMapper.selectByProcessTemplateId(instance.getProcessTemplateId());
        int nextNodeOrder = currentTask.getNodeOrder() + 1;

        NodeTemplateDO nextNode = nodes.stream()
                .filter(n -> n.getNodeOrder() == nextNodeOrder)
                .findFirst()
                .orElse(null);

        if (nextNode == null) {
            instance.setStatus(ProcessStatusEnum.PASSED.getCode());
            processInstanceMapper.updateById(instance);
        } else {
            instance.setCurrentNodeOrder(nextNodeOrder);
            processInstanceMapper.updateById(instance);
            workflowDomainService.createTasksForNode(instance.getId(), nextNode.getId());
        }
    }
}
