package com.zenith.admin.service.system.executor.qry;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.api.DataPermissionService;
import com.zenith.admin.dataobject.OrgDO;
import com.zenith.admin.dataobject.RoleDO;
import com.zenith.admin.dataobject.RoleOrgDO;
import com.zenith.admin.dataobject.UserDO;
import com.zenith.admin.mapper.OrgMapper;
import com.zenith.admin.mapper.RoleOrgMapper;
import com.zenith.admin.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据权限范围查询执行器
 * 根据用户角色计算数据访问范围（全部/自定义部门/本部门及子部门/本部门/仅本人）
 */
@Component
@RequiredArgsConstructor
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
            case DATA_SCOPE_DEPT -> resolveDeptScope(user);
            case DATA_SCOPE_DEPT_AND_CHILD -> resolveDeptAndChildScope(user);
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

    private DataPermissionService.DataScopeInfo resolveDeptScope(UserDO user) {
        Long userOrgId = user.getOrgId();
        if (userOrgId == null) {
            return new DataPermissionService.DataScopeInfo(DATA_SCOPE_SELF, Collections.emptyList());
        }
        return new DataPermissionService.DataScopeInfo(DATA_SCOPE_DEPT, Collections.singletonList(userOrgId));
    }

    private DataPermissionService.DataScopeInfo resolveDeptAndChildScope(UserDO user) {
        Long userOrgId = user.getOrgId();
        if (userOrgId == null) {
            return new DataPermissionService.DataScopeInfo(DATA_SCOPE_SELF, Collections.emptyList());
        }
        List<Long> orgIds = getChildOrgIds(userOrgId);
        orgIds.add(0, userOrgId);
        return new DataPermissionService.DataScopeInfo(DATA_SCOPE_DEPT_AND_CHILD, orgIds);
    }

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
