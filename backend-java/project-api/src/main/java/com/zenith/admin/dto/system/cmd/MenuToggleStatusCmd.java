package com.zenith.admin.dto.system.cmd;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MenuToggleStatusCmd {

    @NotNull(message = "菜单ID不能为空")
    private Long id;
}
