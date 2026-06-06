package com.zenith.admin.web;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import com.zenith.admin.api.system.PermissionService;
import com.zenith.admin.dto.data.RolePermissionAssignCmd;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/role-permissions")
@RequiredArgsConstructor
public class RolePermissionController {
    private final PermissionService permissionService;

    @PostMapping("/assign")
    public Response assign(@RequestBody @Valid RolePermissionAssignCmd cmd) {
        permissionService.assignRolePermissions(cmd.getRoleId(), cmd.getFunctionIds(), cmd.getMenuIds());
        return Response.buildSuccess();
    }

    @GetMapping("/functions")
    public MultiResponse<Long> listFunctions(@RequestParam Long roleId) {
        return MultiResponse.of(permissionService.getRolePermissions(roleId));
    }

    @GetMapping("/menus")
    public MultiResponse<Long> listMenus(@RequestParam Long roleId) {
        return MultiResponse.of(permissionService.getRoleMenus(roleId));
    }
}
