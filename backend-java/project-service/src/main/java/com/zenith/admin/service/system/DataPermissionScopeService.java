package com.zenith.admin.service.system;

import java.util.List;

/**
 * 数据权限范围计算服务
 * <p>
 * 根据当前用户身份，计算其在不同策略下的数据访问范围。
 * 结果支持 Redis 缓存（TTL 5 分钟），组织架构变更时需主动清除缓存。
 * </p>
 *
 * @see com.zenith.admin.annotation.DataPermission
 * @see com.zenith.admin.annotation.DataPermissionStrategy
 */
public interface DataPermissionScopeService {

    /**
     * 获取策略一（ORG）所需的下级组织 ID 列表（含用户自身所属组织）
     *
     * <p>用于纯组织架构控制：按 org_id 过滤数据。</p>
     *
     * @param userId 当前登录用户ID
     * @return 可访问的组织ID列表（含自身组织及所有下级组织）
     */
    List<Long> getAccessibleOrgIds(Long userId);

    /**
     * 获取策略二（OWNER_ORG）所需的下级用户 ID 列表（含用户自身）
     *
     * <p>用于人员-数据绑定 + 组织架构混合控制：
     * 返回当前用户及其所有下级组织的用户的 ID 列表，
     * 用于关联 t_data_permission 表过滤数据。</p>
     *
     * @param userId 当前登录用户ID
     * @return 可访问的用户ID列表（含自身及所有下级用户）
     */
    List<Long> getAccessibleUserIds(Long userId);

    /**
     * 清除指定用户的数据权限范围缓存
     *
     * <p>在以下场景调用：
     * <ul>
     *   <li>用户组织归属变更（orgId 修改）</li>
     *   <li>组织架构变更（增删改组织、调整父子关系）</li>
     * </ul>
     * </p>
     *
     * @param userId 需要清除缓存的用户ID
     */
    void clearCache(Long userId);
}
