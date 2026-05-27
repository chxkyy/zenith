package com.zenith.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.api.DataPermissionService;
import com.zenith.admin.dataobject.OrgDO;
import com.zenith.admin.dataobject.RoleDO;
import com.zenith.admin.dataobject.RoleOrgDO;
import com.zenith.admin.dataobject.UserDO;
import com.zenith.admin.dataobject.UserRoleDO;
import com.zenith.admin.mapper.OrgMapper;
import com.zenith.admin.mapper.RoleMapper;
import com.zenith.admin.mapper.RoleOrgMapper;
import com.zenith.admin.mapper.UserMapper;
import com.zenith.admin.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DataPermissionServiceImpl implements DataPermissionService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private final RoleOrgMapper roleOrgMapper;
    private final OrgMapper orgMapper;

    private static final int DATA_SCOPE_ALL = 1;
    private static final int DATA_SCOPE_CUSTOM = 2;
    private static final int DATA_SCOPE_DEPT_AND_CHILD = 3;
    private static final int DATA_SCOPE_DEPT = 4;
    private static final int DATA_SCOPE_SELF = 5;

    @Override
    public DataScopeInfo getDataScope(Long userId) {
        UserDO user = userMapper.selectById(userId);
        if (user == null) {
            return new DataScopeInfo(DATA_SCOPE_SELF, Collections.emptyList());
        }

        List<UserRoleDO> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRoleDO>().eq(UserRoleDO::getUserId, userId)
        );
        if (userRoles.isEmpty()) {
            return new DataScopeInfo(DATA_SCOPE_SELF, Collections.emptyList());
        }

        List<Long> roleIds = userRoles.stream()
                .map(UserRoleDO::getRoleId)
                .collect(Collectors.toList());

        List<RoleDO> roles = roleMapper.selectList(
                new LambdaQueryWrapper<RoleDO>().in(RoleDO::getId, roleIds)
        );

        boolean isAdmin = roles.stream()
                .anyMatch(role -> Long.valueOf(1L).equals(role.getId()));
        if (isAdmin) {
            return new DataScopeInfo(DATA_SCOPE_ALL, Collections.emptyList());
        }

        int maxDataScope = DATA_SCOPE_SELF;
        for (RoleDO role : roles) {
            Integer scope = role.getDataScope();
            if (scope != null && scope < maxDataScope) {
                maxDataScope = scope;
            }
        }

        if (maxDataScope == DATA_SCOPE_ALL) {
            return new DataScopeInfo(DATA_SCOPE_ALL, Collections.emptyList());
        }

        if (maxDataScope == DATA_SCOPE_CUSTOM) {
            List<RoleOrgDO> roleOrgs = roleOrgMapper.selectList(
                    new LambdaQueryWrapper<RoleOrgDO>().in(RoleOrgDO::getRoleId, roleIds)
            );
            List<Long> orgIds = roleOrgs.stream()
                    .map(RoleOrgDO::getOrgId)
                    .distinct()
                    .collect(Collectors.toList());
            return new DataScopeInfo(DATA_SCOPE_CUSTOM, orgIds);
        }

        Long userOrgId = user.getOrgId();
        if (userOrgId == null) {
            return new DataScopeInfo(DATA_SCOPE_SELF, Collections.emptyList());
        }

        if (maxDataScope == DATA_SCOPE_DEPT) {
            return new DataScopeInfo(DATA_SCOPE_DEPT, Collections.singletonList(userOrgId));
        }

        if (maxDataScope == DATA_SCOPE_DEPT_AND_CHILD) {
            List<Long> orgIds = getChildOrgIds(userOrgId);
            orgIds.add(0, userOrgId);
            return new DataScopeInfo(DATA_SCOPE_DEPT_AND_CHILD, orgIds);
        }

        return new DataScopeInfo(DATA_SCOPE_SELF, Collections.emptyList());
    }

    @Override
    public List<Long> getAccessibleOrgIds(Long userId) {
        DataScopeInfo dataScopeInfo = getDataScope(userId);
        
        if (dataScopeInfo.getDataScope() == DATA_SCOPE_ALL) {
            List<OrgDO> allOrgs = orgMapper.selectList(null);
            return allOrgs.stream().map(OrgDO::getId).collect(Collectors.toList());
        }
        
        return dataScopeInfo.getOrgIds();
    }

    @Override
    public boolean hasFullAccess(Long userId) {
        DataScopeInfo dataScopeInfo = getDataScope(userId);
        return dataScopeInfo.getDataScope() == DATA_SCOPE_ALL;
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
