package com.zenith.admin.web;

import com.alibaba.cola.dto.Response;
import com.zenith.admin.util.PageResponseUtils;
import com.zenith.admin.api.system.TaskService;
import com.zenith.admin.api.system.WorkflowService;
import com.zenith.admin.dto.system.data.ProcessInstanceDTO;
import com.zenith.admin.dto.system.qry.ProcessInstancePageQuery;
import com.github.pagehelper.PageInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workflow/admin")
@RequiredArgsConstructor
public class WorkflowAdminController {

    private final WorkflowService workflowService;
    private final TaskService taskService;

    @PostMapping("/process-instances/page")
    public com.alibaba.cola.dto.PageResponse<ProcessInstanceDTO> pageAllProcess(@RequestBody @Valid ProcessInstancePageQuery query) {
        PageInfo<ProcessInstanceDTO> pageInfo = workflowService.pageAllProcess(query);
        return PageResponseUtils.of(pageInfo);
    }

    @PostMapping("/force-end")
    public Response forceEnd(@RequestBody ForceEndCmd cmd) {
        taskService.forceEnd(cmd.getProcessInstanceId(), cmd.getResult(), cmd.getReason());
        return Response.buildSuccess();
    }

    @PostMapping("/transfer-task")
    public Response transferTask(@RequestBody TransferTaskCmd cmd) {
        taskService.transferTask(cmd.getTaskId(), cmd.getTargetUserId());
        return Response.buildSuccess();
    }

    @PostMapping("/assign-approver")
    public Response assignApprover(@RequestBody AssignApproverCmd cmd) {
        taskService.assignApprover(cmd.getTaskId(), cmd.getApproverId());
        return Response.buildSuccess();
    }

    @lombok.Data
    public static class ForceEndCmd {
        private Long processInstanceId;
        private Integer result;
        private String reason;
    }

    @lombok.Data
    public static class TransferTaskCmd {
        private Long taskId;
        private Long targetUserId;
    }

    @lombok.Data
    public static class AssignApproverCmd {
        private Long taskId;
        private Long approverId;
    }
}
