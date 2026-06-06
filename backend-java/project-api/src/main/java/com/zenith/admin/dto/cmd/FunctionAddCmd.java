package com.zenith.admin.dto.cmd;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FunctionAddCmd {

    @NotBlank(message = "功能名称不能为空")
    private String name;

    private String type;

    private Long menuId;

    private String permission;

    private Integer sort;

    private Integer status;
}
