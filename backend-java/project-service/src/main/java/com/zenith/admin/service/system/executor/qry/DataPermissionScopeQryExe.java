package com.zenith.admin.service.system.executor.qry;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.dataobject.UserDO;
import com.zenith.admin.mapper.DataPermissionMapper;
import com.zenith.admin.mapper.OrgMapper;
import com.zenith.admin.mapper.UserMapper;
import com.zenith.admin.service.system.DataPermissionScopeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 数据权限范围查询执行器
 * <p>
 * 根据用户身份计算数据访问范围，支持两种策略：
 * <ul>
 *   <li>策略一（ORG）：计算下级组织 ID 列表</li>
 *   <li>策略二（OWNER_ORG）：计算下级用户 ID 列表</li>
 * </ul>
 * 结果缓存到 Redis（TTL = 5 分钟），组织架构变更时通过 clearCache 清除。
 * </p>
 *
 * @see DataPermissionScopeService
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataPermissionScopeQryExe implements DataPermissionScopeService {

    /** 缓存 TTL：5 分钟（PRODUCT.md #48） */
    private static final long CACHE_TTL_SECONDS = 300;

    /** 缓存 Key 前缀：策略一（ORG）— 组织ID列表 */
    private static final String CACHE_KEY_ORG_PREFIX = "dp:org:";

    /** 缓存 Key 前缀：策略二（OWNER_ORG）— 用户ID列表 */
    private static final String CACHE_KEY_USER_PREFIX = "dp:user:";

    private final OrgMapper orgMapper;
    private final UserMapper userMapper;
    private final DataPermissionMapper dataPermissionMapper;
    private final StringRedisTemplate redisTemplate;

    // ==================== 策略一（ORG）：组织范围 ====================

    @Override
    public List<Long> getAccessibleOrgIds(Long userId) {
        String cacheKey = CACHE_KEY_ORG_PREFIX + userId;

        // 1. 尝试从缓存读取
        String cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return parseLongList(cached);
        }

        // 2. 缓存未命中，计算组织范围
        List<Long> orgIds = computeAccessibleOrgIds(userId);

        // 3. 写入缓存
        if (!orgIds.isEmpty()) {
            redisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(orgIds),
                    CACHE_TTL_SECONDS, TimeUnit.SECONDS);
        }

        return orgIds;
    }

    /**
     * 计算用户可访问的组织 ID 列表（含自身所属组织及所有下级组织）
     * 使用 PostgreSQL CTE 递归查询（Step 9 已实现 selectChildOrgIdsRecursive）
     */
    private List<Long> computeAccessibleOrgIds(Long userId) {
        UserDO user = userMapper.selectById(userId);
        if (user == null || user.getOrgId() == null) {
            return Collections.emptyList();
        }

        // 使用 CTE 递归查询获取所有下级组织（含自身）
        List<Long> childOrgIds = orgMapper.selectChildOrgIdsRecursive(user.getOrgId());

        if (childOrgIds == null || childOrgIds.isEmpty()) {
            // CTE 查询失败或无结果，回退到仅返回自身组织
            log.warn("CTE 递归查询返回空结果，userId={}, orgId={}，回退到仅自身组织", userId, user.getOrgId());
            return Collections.singletonList(user.getOrgId());
        }

        return childOrgIds;
    }

    // ==================== 策略二（OWNER_ORG）：用户范围 ====================

    @Override
    public List<Long> getAccessibleUserIds(Long userId) {
        String cacheKey = CACHE_KEY_USER_PREFIX + userId;

        // 1. 尝试从缓存读取
        String cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return parseLongList(cached);
        }

        // 2. 缓存未命中，计算用户范围
        List<Long> userIds = computeAccessibleUserIds(userId);

        // 3. 写入缓存
        if (!userIds.isEmpty()) {
            redisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(userIds),
                    CACHE_TTL_SECONDS, TimeUnit.SECONDS);
        }

        return userIds;
    }

    /**
     * 计算用户可访问的用户 ID 列表（含自身及所有下级组织的所有用户）
     *
     * <p>逻辑：</p>
     * <ol>
     *   <li>获取用户所属组织及所有下级组织 ID 列表</li>
     *   <li>查询这些组织下的所有用户 ID</li>
     *   <li>返回用户 ID 列表（含自身）</li>
     * </ol>
     */
    private List<Long> computeAccessibleUserIds(Long userId) {
        // 获取可访问的组织列表（复用策略一的逻辑）
        List<Long> accessibleOrgIds = computeAccessibleOrgIds(userId);
        if (accessibleOrgIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 查询这些组织下的所有用户
        List<UserDO> usersInOrgs = userMapper.selectList(
                new LambdaQueryWrapper<UserDO>()
                        .in(UserDO::getOrgId, accessibleOrgIds)
                        .eq(UserDO::getStatus, 1)  // 仅有效状态的用户
                        .select(UserDO::getId)
        );

        if (usersInOrgs == null || usersInOrgs.isEmpty()) {
            return Collections.emptyList();
        }

        return usersInOrgs.stream()
                .map(UserDO::getId)
                .collect(Collectors.toList());
    }

    // ==================== 缓存管理 ====================

    @Override
    public void clearCache(Long userId) {
        redisTemplate.delete(CACHE_KEY_ORG_PREFIX + userId);
        redisTemplate.delete(CACHE_KEY_USER_PREFIX + userId);
        log.debug("已清除用户 {} 的数据权限缓存", userId);
    }

    // ==================== 工具方法 ====================

    /**
     * 解析 Redis 中缓存的 JSON 数组为 Long 列表
     */
    private List<Long> parseLongList(String json) {
        try {
            return JSON.parseArray(json, Long.class);
        } catch (Exception e) {
            log.warn("解析缓存 JSON 失败: {}", json, e);
            return Collections.emptyList();
        }
    }
}
