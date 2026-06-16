package com.zenith.admin.web;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.util.PageResponseUtils;
import com.zenith.admin.api.system.ProcessTemplateService;
import com.zenith.admin.dto.system.cmd.ProcessTemplateCreateCmd;
import com.zenith.admin.dto.system.cmd.ProcessTemplateUpdateCmd;
import com.zenith.admin.dto.system.cmd.StatusUpdateCmd;
import com.zenith.admin.dto.system.data.ProcessTemplateDTO;
import com.zenith.admin.dto.system.qry.ProcessTemplatePageQuery;
import com.github.pagehelper.PageInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workflow/process-templates")
@RequiredArgsConstructor
public class ProcessTemplateController {

    private final ProcessTemplateService processTemplateService;

    @PostMapping("/page")
    public com.alibaba.cola.dto.PageResponse<ProcessTemplateDTO> page(@RequestBody @Valid ProcessTemplatePageQuery query) {
        PageInfo<ProcessTemplateDTO> pageInfo = processTemplateService.page(query);
        return PageResponseUtils.of(pageInfo);
    }

    @GetMapping("/detail")
    public SingleResponse<ProcessTemplateDTO> getDetail(@RequestParam Long id) {
        return SingleResponse.of(processTemplateService.getById(id));
    }

    @GetMapping("/list-active")
    public MultiResponse<ProcessTemplateDTO> listActive() {
        List<ProcessTemplateDTO> list = processTemplateService.listActive();
        return MultiResponse.of(list);
    }

    @PostMapping
    public Response create(@RequestBody @Valid ProcessTemplateCreateCmd cmd) {
        processTemplateService.create(cmd);
        return Response.buildSuccess();
    }

    @PostMapping("/update")
    public Response update(@RequestBody @Valid ProcessTemplateUpdateCmd cmd) {
        processTemplateService.update(cmd);
        return Response.buildSuccess();
    }

    @PostMapping("/update-status")
    public Response updateStatus(@RequestBody StatusUpdateCmd cmd) {
        processTemplateService.updateStatus(cmd.getId(), cmd.getStatus());
        return Response.buildSuccess();
    }
}
