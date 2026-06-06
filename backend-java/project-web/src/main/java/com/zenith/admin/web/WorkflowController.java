package com.zenith.admin.web;

import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.util.PageResponseUtils;
import com.zenith.admin.api.system.WorkflowService;
import com.zenith.admin.context.UserContext;
import com.zenith.admin.dto.data.*;
import com.github.pagehelper.PageInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workflow/process-instances")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowService workflowService;

    @PostMapping("/save-draft")
    public SingleResponse<Long> saveDraft(@RequestBody @Valid ProcessInstanceCreateCmd cmd) {
        Long currentUserId = UserContext.getUserId();
        Long id = workflowService.saveDraft(cmd, currentUserId);
        return SingleResponse.of(id);
    }

    @PostMapping("/submit")
    public Response submit(@RequestBody IdQuery query) {
        Long currentUserId = UserContext.getUserId();
        workflowService.submit(query.getId(), currentUserId);
        return Response.buildSuccess();
    }

    @PostMapping("/revoke")
    public Response revoke(@RequestBody IdQuery query) {
        Long currentUserId = UserContext.getUserId();
        workflowService.revoke(query.getId(), currentUserId);
        return Response.buildSuccess();
    }

    @PostMapping("/cancel")
    public Response cancel(@RequestBody IdQuery query) {
        Long currentUserId = UserContext.getUserId();
        workflowService.cancel(query.getId(), currentUserId);
        return Response.buildSuccess();
    }

    @PostMapping("/resubmit")
    public Response resubmit(@RequestBody ProcessInstanceCreateCmd cmd) {
        Long currentUserId = UserContext.getUserId();
        workflowService.resubmit(cmd.getProcessTemplateId(), cmd.getFormData(), currentUserId);
        return Response.buildSuccess();
    }

    @GetMapping("/detail")
    public SingleResponse<ProcessInstanceDTO> getDetail(@RequestParam Long id) {
        Long currentUserId = UserContext.getUserId();
        return SingleResponse.of(workflowService.getDetail(id, currentUserId));
    }

    @PostMapping("/my/page")
    public com.alibaba.cola.dto.PageResponse<ProcessInstanceDTO> pageMyProcess(@RequestBody @Valid ProcessInstancePageQuery query) {
        Long currentUserId = UserContext.getUserId();
        PageInfo<ProcessInstanceDTO> pageInfo = workflowService.pageMyProcess(query, currentUserId);
        return PageResponseUtils.of(pageInfo);
    }
}
