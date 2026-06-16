package com.zenith.admin.api.system;

import com.github.pagehelper.PageInfo;
import com.zenith.admin.dto.system.cmd.TaskApproveCmd;
import com.zenith.admin.dto.system.cmd.TaskCountersignCmd;
import com.zenith.admin.dto.system.cmd.TaskRejectCmd;
import com.zenith.admin.dto.system.data.TaskDTO;
import com.zenith.admin.dto.system.qry.TaskPageQuery;

public interface TaskService {

    PageInfo<TaskDTO> pageTodo(TaskPageQuery query, Long currentUserId);

    PageInfo<TaskDTO> pageDone(TaskPageQuery query, Long currentUserId);

    void approve(TaskApproveCmd cmd, Long currentUserId);

    void reject(TaskRejectCmd cmd, Long currentUserId);

    void countersign(TaskCountersignCmd cmd, Long currentUserId);

    void terminate(Long taskId, String opinion, Long currentUserId);

    void forceEnd(Long processInstanceId, Integer result, String reason);

    void transferTask(Long taskId, Long targetUserId);

    void assignApprover(Long taskId, Long approverId);
}
