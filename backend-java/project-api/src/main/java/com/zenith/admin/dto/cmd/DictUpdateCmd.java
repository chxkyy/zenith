package com.zenith.admin.dto.cmd;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DictUpdateCmd {

    @NotNull(message = "ID不能为空")
    private Long id;

    @NotBlank(message = "字典名称不能为空")
    private String name;

    private String type;
}
