package com.zenith.admin.dto.data;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DictAddCmd {

    @NotBlank(message = "字典名称不能为空")
    private String name;

    @NotBlank(message = "字典类型编码不能为空")
    private String type;
}
