package com.zenith.admin.dto.system.qry;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IdQuery {
    @NotNull(message = "ID不能为空")
    private Long id;
}
