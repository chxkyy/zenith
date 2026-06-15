package com.zenith.admin.dto.cmd;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 回收数据权限命令
 */
@Data
public class DataPermissionRevokeCmd {

    /** 目标用户ID */
    @NotNull(message = "目标用户ID不能为空")
    private Long userId;

    /** 业务数据类型标识 */
    @NotBlank(message = "数据类型不能为空")
    private String dataType;

    /** 业务数据记录的主键ID */
    @NotNull(message = "数据ID不能为空")
    private Long dataId;
}
