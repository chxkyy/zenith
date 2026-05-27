package com.zenith.admin.dto.data;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleAddCmd {

    @NotBlank(message = "角色名称不能为空")
    private String name;

    private Integer status;

    private String description;
}
