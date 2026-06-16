package com.zenith.admin.web;

import com.alibaba.cola.dto.Response;
import com.zenith.admin.util.PageResponseUtils;
import com.zenith.admin.api.system.TaskService;
import com.zenith.admin.context.UserContext;
import com.zenith.admin.dto.system.cmd.TaskApproveCmd;
import com.zenith.admin.dto.system.cmd.TaskCountersignCmd;
import com.zenith.admin.dto.system.cmd.TaskRejectCmd;
import com.zenith.admin.dto.system.data.TaskDTO;
import com.zenith.admin.dto.system.qry.TaskPageQuery;
import com.github.pagehelper.PageInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workflow/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/todo/page")
    public com.alibaba.cola.dto.PageResponse<TaskDTO> pageTodo(@RequestBody @Valid TaskPageQuery query) {
        Long currentUserId = UserContext.getUserId();
        PageInfo<TaskDTO> pageInfo = taskService.pageTodo(query, currentUserId);
        return PageResponseUtils.of(pageInfo);
    }

    @PostMapping("/done/page")
    public com.alibaba.cola.dto.PageResponse<TaskDTO> pageDone(@RequestBody @Valid TaskPageQuery query) {
        Long currentUserId = UserContext.getUserId();
        PageInfo<TaskDTO> pageInfo = taskService.pageDone(query, currentUserId);
        return PageResponseUtils.of(pageInfo);
    }

    @PostMapping("/approve")
    public Response approve(@RequestBody @Valid TaskApproveCmd cmd) {
        Long currentUserId = UserContext.getUserId();
        taskService.approve(cmd, currentUserId);
        return Response.buildSuccess();
    }

    @PostMapping("/reject")
    public Response reject(@RequestBody @Valid TaskRejectCmd cmd) {
        Long currentUserId = UserContext.getUserId();
        taskService.reject(cmd, currentUserId);
        return Response.buildSuccess();
    }

    @PostMapping("/countersign")
    public Response countersign(@RequestBody @Valid TaskCountersignCmd cmd) {
        Long currentUserId = UserContext.getUserId();
        taskService.countersign(cmd, currentUserId);
        return Response.buildSuccess();
    }

    @PostMapping("/terminate")
    public Response terminate(@RequestBody TaskApproveCmd cmd) {
        Long currentUserId = UserContext.getUserId();
        taskService.terminate(cmd.getTaskId(), cmd.getOpinion(), currentUserId);
        return Response.buildSuccess();
    }
}
