package com.zenith.admin.service.system.executor.cmd;

import com.alibaba.cola.exception.BizException;
import com.zenith.admin.dataobject.TaskDO;
import com.zenith.admin.enums.TaskStatusEnum;
import com.zenith.admin.mapper.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskAssignApproverCmdExe {

    private final TaskMapper taskMapper;

    public void execute(Long taskId, Long approverId) {
        TaskDO task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new BizException("TASK_NOT_FOUND", "任务不存在");
        }
        if (!TaskStatusEnum.PENDING.getCode().equals(task.getStatus())) {
            throw new BizException("TASK_PROCESSED", "任务已被处理");
        }

        task.setAssigneeId(approverId);
        taskMapper.updateById(task);
    }
}
