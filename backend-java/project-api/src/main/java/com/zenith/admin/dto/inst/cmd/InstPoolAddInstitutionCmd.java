package com.zenith.admin.dto.inst.cmd;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 机构入池命令对象
 */
@Data
public class InstPoolAddInstitutionCmd {

    @NotNull(message = "机构池ID不能为空")
    private Long poolId;

    @NotNull(message = "机构ID不能为空")
    private Long institutionId;

    /**
     * 备注
     */
    private String remark;
}
