package com.zenith.admin.dto.data;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatusUpdateCmd {

    @NotNull(message = "ID不能为空")
    private Long id;

    @NotNull(message = "状态不能为空")
    private Integer status;
}
