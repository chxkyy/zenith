package com.zenith.admin.dto.system.cmd;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MenuUpdateCmd {

    @NotNull(message = "ID不能为空")
    private Long id;

    @NotBlank(message = "菜单名称不能为空")
    private String name;

    private String path;

    private String component;

    private String type;

    private Long parentId;

    private Integer sort;

    private String icon;

    private Integer status;

    private String permission;
}
