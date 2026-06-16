package com.zenith.admin.dto.system.cmd;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 批量分配数据权限命令
 */
@Data
public class DataPermissionBatchAssignCmd {

    /** 目标用户ID（负责人） */
    @NotNull(message = "目标用户ID不能为空")
    private Long userId;

    /** 业务数据类型标识 */
    @NotBlank(message = "数据类型不能为空")
    private String dataType;

    /** 业务数据记录主键ID列表 */
    @NotEmpty(message = "数据ID列表不能为空")
    private List<Long> dataIds;
}
