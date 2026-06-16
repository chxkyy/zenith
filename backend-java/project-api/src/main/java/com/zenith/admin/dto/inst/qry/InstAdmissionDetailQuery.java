package com.zenith.admin.dto.inst.qry;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 申请单详情查询对象
 */
@Data
public class InstAdmissionDetailQuery {

    @NotNull(message = "申请单ID不能为空")
    private Long id;
}
