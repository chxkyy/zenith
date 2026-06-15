package com.zenith.admin.dto.query;

import lombok.Data;

/**
 * 数据权限查询条件
 * <p>
 * 支持两种查询模式：
 * <ul>
 *   <li>按用户查询：传入 userId，返回该用户负责的所有数据权限</li>
 *   <li>按数据查询：传入 dataType + dataId，返回该数据的所有负责人</li>
 * </ul>
 * </p>
 */
@Data
public class DataPermissionQuery {

    /** 用户ID（按用户查询时使用） */
    private Long userId;

    /** 业务数据类型标识（按数据查询时使用） */
    private String dataType;

    /** 业务数据记录主键ID（按数据查询时使用） */
    private Long dataId;
}
