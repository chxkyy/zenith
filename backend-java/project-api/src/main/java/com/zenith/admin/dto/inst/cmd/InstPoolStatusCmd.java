package com.zenith.admin.dto.inst.cmd;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 机构池启用/停用命令对象
 */
@Data
public class InstPoolStatusCmd {

    @NotNull(message = "ID不能为空")
    private Long id;

    @NotNull(message = "状态不能为空")
    private Integer status; // 1=启用 0=停用
}
