package com.zenith.admin.dto.data;

import lombok.Data;

/**
 * 数据权限记录 DTO
 */
@Data
public class DataPermissionDTO {

    private Long id;
    private Long userId;
    private String userName;       // 负责人姓名（关联查询）
    private String dataType;
    private Long dataId;
    private String dataName;        // 数据名称（可选，根据具体业务类型可能为空）
    private Long createUserId;
    private Long createdTime;
    private Long updateTime;
}
