package com.zenith.admin.dto.data;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class RolePermissionAssignCmd {
    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    private List<Long> functionIds;

    private List<Long> menuIds;
}
