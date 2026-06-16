package com.zenith.admin.web;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import com.zenith.admin.api.system.RoleService;
import com.zenith.admin.util.PageResponseUtils;
import com.zenith.admin.context.UserContext;
import com.zenith.admin.dto.system.qry.IdQuery;
import com.zenith.admin.dto.system.cmd.RoleAddCmd;
import com.zenith.admin.dto.system.data.RoleDTO;
import com.zenith.admin.dto.system.qry.RolePageQuery;
import com.zenith.admin.dto.system.cmd.RoleUpdateCmd;
import com.zenith.admin.dto.system.qry.StatusUpdateQuery;
import com.github.pagehelper.PageInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService service;

    @PostMapping("/status")
    public Response changeStatus(@RequestBody StatusUpdateQuery query) {
        Long currentUserId = UserContext.getUserId();
        service.changeStatus(query.getId(), query.getStatus(), currentUserId);
        return Response.buildSuccess();
    }

    @PostMapping("/delete")
    public Response delete(@RequestBody IdQuery query) {
        Long currentUserId = UserContext.getUserId();
        service.delete(query.getId(), currentUserId);
        return Response.buildSuccess();
    }

    @GetMapping("/get")
    public SingleResponse<RoleDTO> get(@RequestParam Long id) {
        return SingleResponse.of(service.getById(id));
    }

    @GetMapping("/list")
    public MultiResponse<RoleDTO> list() {
        List<RoleDTO> list = service.listAll();
        return MultiResponse.of(list);
    }

    @GetMapping("/list-active")
    public MultiResponse<RoleDTO> listActive() {
        List<RoleDTO> list = service.listActiveRoles();
        return MultiResponse.of(list);
    }

    @PostMapping("/page")
    public com.alibaba.cola.dto.PageResponse<RoleDTO> page(@RequestBody @Valid RolePageQuery query) {
        PageInfo<RoleDTO> pageInfo = service.listByPage(query);
        return PageResponseUtils.of(pageInfo);
    }

    @PostMapping
    public Response save(@RequestBody @Valid RoleAddCmd cmd) {
        Long currentUserId = UserContext.getUserId();
        service.save(cmd, currentUserId);
        return Response.buildSuccess();
    }

    @PostMapping("/update")
    public Response update(@RequestBody @Valid RoleUpdateCmd cmd) {
        Long currentUserId = UserContext.getUserId();
        service.update(cmd, currentUserId);
        return Response.buildSuccess();
    }
}
