package com.zenith.admin.service.system.executor.cmd;

import com.alibaba.cola.exception.BizException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.api.system.WorkflowDomainService;
import com.zenith.admin.dataobject.ProcessInstanceDO;
import com.zenith.admin.dataobject.TaskDO;
import com.zenith.admin.dataobject.UserRoleDO;
import com.zenith.admin.dto.system.cmd.TaskCountersignCmd;
import com.zenith.admin.enums.ActionTypeEnum;
import com.zenith.admin.enums.ApproverTypeEnum;
import com.zenith.admin.enums.TaskStatusEnum;
import com.zenith.admin.mapper.ProcessInstanceMapper;
import com.zenith.admin.mapper.TaskMapper;
import com.zenith.admin.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TaskCountersignCmdExe {

    private final TaskMapper taskMapper;
    private final ProcessInstanceMapper processInstanceMapper;
    private final UserRoleMapper userRoleMapper;
    private final WorkflowDomainService workflowDomainService;

    public void execute(TaskCountersignCmd cmd, Long currentUserId) {
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

        task.setStatus(TaskStatusEnum.COUNTERSIGNED.getCode());
        task.setOpinion(cmd.getOpinion());
        task.setActionType(ActionTypeEnum.COUNTERSIGN.getCode());
        task.setActionTime(LocalDateTime.now());
        taskMapper.updateById(task);

        ProcessInstanceDO instance = processInstanceMapper.selectById(task.getProcessInstanceId());
        workflowDomainService.createApprovalRecord(instance.getId(), task.getNodeOrder(), task.getNodeName(),
                task.getId(), currentUserId, ActionTypeEnum.COUNTERSIGN, cmd.getOpinion());

        createCountersignTasks(cmd, task, instance);
    }

    private void createCountersignTasks(TaskCountersignCmd cmd, TaskDO task, ProcessInstanceDO instance) {
        List<Long> approverIds = cmd.getApproverIds();
        if (ApproverTypeEnum.ROLE.getCode().equals(cmd.getApproverType())) {
            LambdaQueryWrapper<UserRoleDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(UserRoleDO::getRoleId, approverIds);
            List<UserRoleDO> userRoles = userRoleMapper.selectList(queryWrapper);
            approverIds = userRoles.stream()
                    .map(UserRoleDO::getUserId)
                    .distinct()
                    .collect(Collectors.toList());
        }

        for (Long approverId : approverIds) {
            TaskDO newTask = new TaskDO();
            newTask.setProcessInstanceId(instance.getId());
            newTask.setNodeOrder(task.getNodeOrder());
            newTask.setNodeName(task.getNodeName() + "(加签)");
            newTask.setNodeType(task.getNodeType());
            newTask.setAssigneeId(approverId);
            newTask.setAssigneeType(2);
            newTask.setParentTaskId(task.getId());
            newTask.setStatus(TaskStatusEnum.PENDING.getCode());
            newTask.setVersion(0);
            taskMapper.insert(newTask);
        }
    }
}
