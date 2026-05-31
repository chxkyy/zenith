package com.zenith.admin.service.system.impl;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.TaskService;
import com.zenith.admin.dto.data.*;
import com.zenith.admin.service.system.executor.cmd.TaskApproveCmdExe;
import com.zenith.admin.service.system.executor.cmd.TaskAssignApproverCmdExe;
import com.zenith.admin.service.system.executor.cmd.TaskCountersignCmdExe;
import com.zenith.admin.service.system.executor.qry.TaskDonePageQryExe;
import com.zenith.admin.service.system.executor.cmd.TaskForceEndCmdExe;
import com.zenith.admin.service.system.executor.cmd.TaskRejectCmdExe;
import com.zenith.admin.service.system.executor.cmd.TaskTerminateCmdExe;
import com.zenith.admin.service.system.executor.qry.TaskTodoPageQryExe;
import com.zenith.admin.service.system.executor.cmd.TaskTransferTaskCmdExe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskTodoPageQryExe taskTodoPageQryExe;
    private final TaskDonePageQryExe taskDonePageQryExe;
    private final TaskApproveCmdExe taskApproveCmdExe;
    private final TaskRejectCmdExe taskRejectCmdExe;
    private final TaskCountersignCmdExe taskCountersignCmdExe;
    private final TaskTerminateCmdExe taskTerminateCmdExe;
    private final TaskForceEndCmdExe taskForceEndCmdExe;
    private final TaskTransferTaskCmdExe taskTransferTaskCmdExe;
    private final TaskAssignApproverCmdExe taskAssignApproverCmdExe;

    @Override
    public PageInfo<TaskDTO> pageTodo(TaskPageQuery query, Long currentUserId) {
        return taskTodoPageQryExe.execute(query, currentUserId);
    }

    @Override
    public PageInfo<TaskDTO> pageDone(TaskPageQuery query, Long currentUserId) {
        return taskDonePageQryExe.execute(query, currentUserId);
    }

    @Override
    @Transactional
    public void approve(TaskApproveCmd cmd, Long currentUserId) {
        taskApproveCmdExe.execute(cmd, currentUserId);
    }

    @Override
    @Transactional
    public void reject(TaskRejectCmd cmd, Long currentUserId) {
        taskRejectCmdExe.execute(cmd, currentUserId);
    }

    @Override
    @Transactional
    public void countersign(TaskCountersignCmd cmd, Long currentUserId) {
        taskCountersignCmdExe.execute(cmd, currentUserId);
    }

    @Override
    @Transactional
    public void terminate(Long taskId, String opinion, Long currentUserId) {
        taskTerminateCmdExe.execute(taskId, opinion, currentUserId);
    }

    @Override
    @Transactional
    public void forceEnd(Long processInstanceId, Integer result, String reason) {
        taskForceEndCmdExe.execute(processInstanceId, result, reason);
    }

    @Override
    @Transactional
    public void transferTask(Long taskId, Long targetUserId) {
        taskTransferTaskCmdExe.execute(taskId, targetUserId);
    }

    @Override
    @Transactional
    public void assignApprover(Long taskId, Long approverId) {
        taskAssignApproverCmdExe.execute(taskId, approverId);
    }
}
