package com.zenith.admin.dto.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DictItemAddCmd {

    @NotNull(message = "类型不能为空")
    private String type;

    @NotBlank(message = "标签不能为空")
    private String label;

    @NotBlank(message = "值不能为空")
    private String dictValue;

    private Integer sort;
}
