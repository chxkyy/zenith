package com.zenith.admin.dto.inst.cmd;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 提交评审命令对象
 */
@Data
public class InstAdmissionSubmitCmd {

    @NotNull(message = "申请单ID不能为空")
    private Long admissionId;
}
