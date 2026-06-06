package com.zenith.admin.web;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import com.zenith.admin.api.system.PermissionService;
import com.zenith.admin.dto.data.UserRoleUpdateCmd;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-roles")
@RequiredArgsConstructor
public class UserRoleController {

    private final PermissionService permissionService;

    @GetMapping
    public MultiResponse<String> getUserRoles(@RequestParam Long userId) {
        List<String> roles = permissionService.getUserRoles(userId);
        return MultiResponse.of(roles);
    }

    @PostMapping
    public Response updateUserRoles(@RequestBody @Valid UserRoleUpdateCmd cmd) {
        permissionService.updateUserRoles(cmd.getUserId(), cmd.getRoleIds());
        return Response.buildSuccess();
    }
}
