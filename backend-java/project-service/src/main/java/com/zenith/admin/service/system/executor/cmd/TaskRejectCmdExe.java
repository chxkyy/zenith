package com.zenith.admin.service.system.executor.cmd;

import com.alibaba.cola.exception.BizException;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.zenith.admin.api.WorkflowDomainService;
import com.zenith.admin.dataobject.NodeTemplateDO;
import com.zenith.admin.dataobject.ProcessInstanceDO;
import com.zenith.admin.dataobject.TaskDO;
import com.zenith.admin.dto.data.TaskRejectCmd;
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
public class TaskRejectCmdExe {

    private final TaskMapper taskMapper;
    private final ProcessInstanceMapper processInstanceMapper;
    private final NodeTemplateMapper nodeTemplateMapper;
    private final WorkflowDomainService workflowDomainService;

    public void execute(TaskRejectCmd cmd, Long currentUserId) {
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

        task.setStatus(TaskStatusEnum.REJECTED.getCode());
        task.setOpinion(cmd.getOpinion());
        task.setActionType(ActionTypeEnum.REJECT.getCode());
        task.setActionTime(LocalDateTime.now());
        taskMapper.updateById(task);

        ProcessInstanceDO instance = processInstanceMapper.selectById(task.getProcessInstanceId());
        workflowDomainService.createApprovalRecord(instance.getId(), task.getNodeOrder(), task.getNodeName(),
                task.getId(), currentUserId, ActionTypeEnum.REJECT, cmd.getOpinion());

        if (NodeTypeEnum.COUNTERSIGN.getCode().equals(task.getNodeType())) {
            LambdaUpdateWrapper<TaskDO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(TaskDO::getProcessInstanceId, task.getProcessInstanceId())
                    .eq(TaskDO::getNodeOrder, task.getNodeOrder())
                    .eq(TaskDO::getStatus, TaskStatusEnum.PENDING.getCode())
                    .set(TaskDO::getStatus, TaskStatusEnum.TERMINATED.getCode());
            taskMapper.update(null, updateWrapper);
        }

        if (task.getNodeOrder() == 1) {
            instance.setStatus(ProcessStatusEnum.RETURNED.getCode());
            instance.setCurrentNodeOrder(null);
        } else {
            List<NodeTemplateDO> nodes = nodeTemplateMapper.selectByProcessTemplateId(instance.getProcessTemplateId());
            int prevNodeOrder = task.getNodeOrder() - 1;
            NodeTemplateDO prevNode = nodes.stream()
                    .filter(n -> n.getNodeOrder() == prevNodeOrder)
                    .findFirst()
                    .orElse(null);

            if (prevNode != null) {
                instance.setCurrentNodeOrder(prevNodeOrder);
                workflowDomainService.createTasksForNode(instance.getId(), prevNode.getId());
            }
        }
        processInstanceMapper.updateById(instance);
    }
}
