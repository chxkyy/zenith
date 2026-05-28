package com.zenith.admin.executor;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.zenith.admin.dataobject.TaskDO;
import com.zenith.admin.enums.TaskStatusEnum;
import com.zenith.admin.mapper.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WorkflowTerminateTaskCmdExe {

    private final TaskMapper taskMapper;

    public void execute(Long processInstanceId) {
        LambdaUpdateWrapper<TaskDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(TaskDO::getProcessInstanceId, processInstanceId)
                .eq(TaskDO::getStatus, TaskStatusEnum.PENDING.getCode())
                .set(TaskDO::getStatus, TaskStatusEnum.TERMINATED.getCode());
        taskMapper.update(null, updateWrapper);
    }

    public void executeExcept(Long processInstanceId, Long excludeTaskId) {
        LambdaUpdateWrapper<TaskDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(TaskDO::getProcessInstanceId, processInstanceId)
                .eq(TaskDO::getStatus, TaskStatusEnum.PENDING.getCode())
                .ne(TaskDO::getId, excludeTaskId)
                .set(TaskDO::getStatus, TaskStatusEnum.TERMINATED.getCode());
        taskMapper.update(null, updateWrapper);
    }
}
