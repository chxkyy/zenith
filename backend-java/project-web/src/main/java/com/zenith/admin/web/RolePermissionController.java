package com.zenith.admin.web;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import com.zenith.admin.api.PermissionService;
import com.zenith.admin.dto.data.RolePermissionAssignCmd;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/role-permissions")
@RequiredArgsConstructor
public class RolePermissionController {

    private final PermissionService permissionService;

    @GetMapping
    public MultiResponse<Long> list(@RequestParam Long roleId) {
        return MultiResponse.of(permissionService.getRolePermissions(roleId));
    }

    @PostMapping("/assign")
    public Response assign(@RequestBody @Valid RolePermissionAssignCmd cmd) {
        permissionService.assignRolePermissions(cmd.getRoleId(), cmd.getFunctionIds());
        return Response.buildSuccess();
    }
}
