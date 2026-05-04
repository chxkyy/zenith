package com.zenith.admin.dto.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FunctionUpdateCmd {

    @NotNull(message = "ID不能为空")
    private Long id;

    @NotBlank(message = "功能名称不能为空")
    private String name;

    private String type;

    private Long menuId;

    private String permission;

    private Integer sort;

    private Integer status;
}
