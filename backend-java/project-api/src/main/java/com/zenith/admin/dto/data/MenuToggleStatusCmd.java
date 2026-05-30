package com.zenith.admin.dto.data;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MenuToggleStatusCmd {

    @NotNull(message = "菜单ID不能为空")
    private Long id;
}
