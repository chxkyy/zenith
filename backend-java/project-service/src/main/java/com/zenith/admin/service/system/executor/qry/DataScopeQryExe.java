package com.zenith.admin.service.system.executor.qry;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.api.system.DataPermissionService;
import com.zenith.admin.dataobject.OrgDO;
import com.zenith.admin.dataobject.RoleDO;
import com.zenith.admin.dataobject.RoleOrgDO;
import com.zenith.admin.dataobject.UserDO;
import com.zenith.admin.mapper.OrgMapper;
import com.zenith.admin.mapper.RoleOrgMapper;
import com.zenith.admin.mapper.UserMapper;
import com.zenith.admin.service.system.DataPermissionScopeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据权限范围查询执行器（基于角色 dataScope）
 * <p>
 * 根据用户角色计算数据访问范围（全部/自定义部门/本部门及子部门/本部门/仅本人）。
 *
 * <h3>改造说明（TECH.md 决策2）：</h3>
 * <ul>
 *   <li>dataScope=3（本部门及子部门）和 dataScope=4（仅本部门）已委托给
 *       {@link DataPermissionScopeService} 处理</li>
 *   <li>{@link #resolveDeptAndChildScope(UserDO)} 和 {@link #resolveDeptScope(UserDO)}
 *       方法已标记为 {@code @Deprecated}</li>
 *   <li>{@link #getChildOrgIds(Long)} 已标记为 {@code @Deprecated}，
 *       替代方案为 {@code OrgMapper.selectChildOrgIdsRecursive()} （PostgreSQL CTE 递归查询）</li>
 *   <li>新代码应优先使用 {@code @DataPermission(strategy = ORG)} 注解方式</li>
 * </ul>
 *
 * @deprecated 建议使用新的 {@link com.zenith.admin.annotation.DataPermission} 注解框架。
 *             本类保留用于向后兼容，dataScope=1/2/5 的逻辑仍由此类处理，
 *             dataScope=3/4 已委托给 {@link DataPermissionScopeService}。
 */
@Component
@RequiredArgsConstructor
@Deprecated
public class DataScopeQryExe {

    private static final int DATA_SCOPE_ALL = 1;
    private static final int DATA_SCOPE_CUSTOM = 2;
    private static final int DATA_SCOPE_DEPT_AND_CHILD = 3;
    private static final int DATA_SCOPE_DEPT = 4;
    private static final int DATA_SCOPE_SELF = 5;

    private final UserMapper userMapper;
    private final UserRolesQryExe userRolesQryExe;
    private final RoleOrgMapper roleOrgMapper;
    private final OrgMapper orgMapper;

    /** 新的数据权限范围计算服务（策略一 ORG 的替代实现） */
    private final DataPermissionScopeService dataPermissionScopeService;

    public DataPermissionService.DataScopeInfo execute(Long userId) {
        UserDO user = userMapper.selectById(userId);
        if (user == null) {
            return new DataPermissionService.DataScopeInfo(DATA_SCOPE_SELF, Collections.emptyList());
        }

        List<RoleDO> roles = userRolesQryExe.getRoles(userId);

        if (roles.isEmpty()) {
            return new DataPermissionService.DataScopeInfo(DATA_SCOPE_SELF, Collections.emptyList());
        }

        if (userRolesQryExe.isSuperAdmin(roles)) {
            return new DataPermissionService.DataScopeInfo(DATA_SCOPE_ALL, Collections.emptyList());
        }

        int maxDataScope = resolveMaxDataScope(roles);

        return switch (maxDataScope) {
            case DATA_SCOPE_ALL -> new DataPermissionService.DataScopeInfo(DATA_SCOPE_ALL, Collections.emptyList());
            case DATA_SCOPE_CUSTOM -> resolveCustomScope(roles);
            // dataScope=3/4 委托给新的 DataPermissionScopeService（策略一 ORG）
            case DATA_SCOPE_DEPT -> {
                List<Long> orgIds = dataPermissionScopeService.getAccessibleOrgIds(userId);
                yield new DataPermissionService.DataScopeInfo(DATA_SCOPE_DEPT, orgIds);
            }
            case DATA_SCOPE_DEPT_AND_CHILD -> {
                List<Long> orgIds = dataPermissionScopeService.getAccessibleOrgIds(userId);
                yield new DataPermissionService.DataScopeInfo(DATA_SCOPE_DEPT_AND_CHILD, orgIds);
            }
            default -> new DataPermissionService.DataScopeInfo(DATA_SCOPE_SELF, Collections.emptyList());
        };
    }

    public List<Long> getAccessibleOrgIds(Long userId) {
        DataPermissionService.DataScopeInfo dataScopeInfo = execute(userId);
        if (dataScopeInfo.getDataScope() == DATA_SCOPE_ALL) {
            List<OrgDO> allOrgs = orgMapper.selectList(null);
            return allOrgs.stream().map(OrgDO::getId).collect(Collectors.toList());
        }
        return dataScopeInfo.getOrgIds();
    }

    public boolean hasFullAccess(Long userId) {
        return execute(userId).getDataScope() == DATA_SCOPE_ALL;
    }

    private int resolveMaxDataScope(List<RoleDO> roles) {
        int maxDataScope = DATA_SCOPE_SELF;
        for (RoleDO role : roles) {
            Integer scope = role.getDataScope();
            if (scope != null && scope < maxDataScope) {
                maxDataScope = scope;
            }
        }
        return maxDataScope;
    }

    private DataPermissionService.DataScopeInfo resolveCustomScope(List<RoleDO> roles) {
        List<Long> roleIds = roles.stream().map(RoleDO::getId).toList();
        List<RoleOrgDO> roleOrgs = roleOrgMapper.selectList(
                new LambdaQueryWrapper<RoleOrgDO>().in(RoleOrgDO::getRoleId, roleIds)
        );
        List<Long> orgIds = roleOrgs.stream()
                .map(RoleOrgDO::getOrgId)
                .distinct()
                .toList();
        return new DataPermissionService.DataScopeInfo(DATA_SCOPE_CUSTOM, orgIds);
    }

    /**
     * @deprecated 已被 {@link DataPermissionScopeService#getAccessibleOrgIds(Long)} 替代。
     *             使用 PostgreSQL CTE 递归查询（{@code OrgMapper.selectChildOrgIdsRecursive()}），
     *             性能更好且支持环检测。
     */
    @Deprecated
    private DataPermissionService.DataScopeInfo resolveDeptScope(UserDO user) {
        Long userOrgId = user.getOrgId();
        if (userOrgId == null) {
            return new DataPermissionService.DataScopeInfo(DATA_SCOPE_SELF, Collections.emptyList());
        }
        return new DataPermissionService.DataScopeInfo(DATA_SCOPE_DEPT, Collections.singletonList(userOrgId));
    }

    /**
     * @deprecated 已被 {@link DataPermissionScopeService#getAccessibleOrgIds(Long)} 替代。
     */
    @Deprecated
    private DataPermissionService.DataScopeInfo resolveDeptAndChildScope(UserDO user) {
        Long userOrgId = user.getOrgId();
        if (userOrgId == null) {
            return new DataPermissionService.DataScopeInfo(DATA_SCOPE_SELF, Collections.emptyList());
        }
        List<Long> orgIds = getChildOrgIds(userOrgId);
        orgIds.add(0, userOrgId);
        return new DataPermissionService.DataScopeInfo(DATA_SCOPE_DEPT_AND_CHILD, orgIds);
    }

    /**
     * @deprecated 已被 {@code OrgMapper.selectChildOrgIdsRecursive()} 替代。
     *             该方法使用 Java 递归遍历组织树，无环检测能力，
     *             且在组织层级较深时性能较差。新代码请使用 CTE 方式。
     */
    @Deprecated
    private List<Long> getChildOrgIds(Long parentId) {
        List<Long> ids = new ArrayList<>();
        List<OrgDO> children = orgMapper.selectList(
                new LambdaQueryWrapper<OrgDO>().eq(OrgDO::getParentId, parentId)
        );
        for (OrgDO child : children) {
            ids.add(child.getId());
            ids.addAll(getChildOrgIds(child.getId()));
        }
        return ids;
    }
}
