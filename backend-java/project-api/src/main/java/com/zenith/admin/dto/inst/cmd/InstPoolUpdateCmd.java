package com.zenith.admin.dto.inst.cmd;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 编辑机构池命令对象
 */
@Data
public class InstPoolUpdateCmd {

    @NotNull(message = "ID不能为空")
    private Long id;

    @NotBlank(message = "池名称不能为空")
    private String name;

    @NotBlank(message = "池类型不能为空")
    private String poolType;

    /**
     * 描述说明
     */
    private String description;
}
