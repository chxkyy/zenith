package com.zenith.admin.dto.system.cmd;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DictItemUpdateCmd {

    @NotNull(message = "ID不能为空")
    private Long id;

    @NotNull(message = "类型不能为空")
    private String type;

    @NotBlank(message = "标签不能为空")
    private String label;

    private String dictValue;

    private Integer sort;
}
