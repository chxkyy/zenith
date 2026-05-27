package com.zenith.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zenith.admin.api.DataPermissionService;
import com.zenith.admin.api.UserService;
import com.zenith.admin.UserConvertor;
import com.zenith.admin.dataobject.OrgDO;
import com.zenith.admin.dataobject.RoleDO;
import com.zenith.admin.dataobject.UserDO;
import com.zenith.admin.dataobject.UserRoleDO;
import com.zenith.admin.dto.data.UserAddCmd;
import com.zenith.admin.dto.data.UserDTO;
import com.zenith.admin.dto.data.UserPageQuery;
import com.zenith.admin.dto.data.UserUpdateCmd;
import com.zenith.admin.mapper.OrgMapper;
import com.zenith.admin.mapper.RoleMapper;
import com.zenith.admin.mapper.UserMapper;
import com.zenith.admin.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final OrgMapper orgMapper;
    private final UserConvertor userConvertor;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;
    private final DataPermissionService dataPermissionService;

    @Override
    public PageInfo<UserDTO> listByPage(UserPageQuery query) {
        QueryWrapper<UserDO> wrapper = new QueryWrapper<>();

        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            wrapper.like("login_id", query.getKeyword())
                   .or().like("username", query.getKeyword())
                   .or().like("email", query.getKeyword());
        }

        if (query.getOrgId() != null) {
            List<Long> allOrgIds = getChildOrgIds(query.getOrgId());
            if (!allOrgIds.isEmpty()) {
                wrapper.in("org_id", allOrgIds);
            }
        }

        if (query.getStatus() != null) {
            wrapper.eq("status", query.getStatus());
        }

        if (query.getCurrentUserId() != null && !dataPermissionService.hasFullAccess(query.getCurrentUserId())) {
            List<Long> accessibleOrgIds = dataPermissionService.getAccessibleOrgIds(query.getCurrentUserId());
            if (accessibleOrgIds.isEmpty()) {
                wrapper.apply("1 = 0");
            } else {
                wrapper.in("org_id", accessibleOrgIds);
            }
        }

        if (query.getSortField() != null && !query.getSortField().isEmpty()) {
            String order = query.getSortOrder() != null && "desc".equals(query.getSortOrder()) ? "desc" : "asc";
            wrapper.orderBy(true, "asc".equals(order), query.getSortField());
        }

        PageInfo<UserDO> pageInfo = PageHelper.startPage(query.getPageIndex(), query.getPageSize())
                .doSelectPageInfo(() -> userMapper.selectList(wrapper));

        List<UserDTO> dtos = convertToDTOsWithRoles(pageInfo.getList());

        PageInfo<UserDTO> result = new PageInfo<>();
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setPages(pageInfo.getPages());
        result.setList(dtos);
        return result;
    }

    private List<UserDTO> convertToDTOsWithRoles(List<UserDO> userDOS) {
        if (userDOS == null || userDOS.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> userIds = userDOS.stream()
                .map(UserDO::getId)
                .collect(Collectors.toList());

        Map<Long, List<Long>> userRoleIdsMap = getUserRoleIdsMap(userIds);
        Map<Long, String> roleIdToNameMap = getRoleIdToNameMap();

        return userDOS.stream().map(userDO -> {
            UserDTO dto = userConvertor.toDTO(userDO);

            List<Long> roleIds = userRoleIdsMap.getOrDefault(userDO.getId(), Collections.emptyList());
            List<String> roleNames = roleIds.stream()
                    .map(roleId -> roleIdToNameMap.getOrDefault(roleId, "未知角色"))
                    .collect(Collectors.toList());
            
            dto.setRoles(roleIds.stream().map(String::valueOf).collect(Collectors.toList()));
            dto.setRoleNames(String.join(", ", roleNames));

            return dto;
        }).collect(Collectors.toList());
    }

    private Map<Long, List<Long>> getUserRoleIdsMap(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        LambdaQueryWrapper<UserRoleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(UserRoleDO::getUserId, userIds);

        List<UserRoleDO> userRoles = userRoleMapper.selectList(wrapper);

        return userRoles.stream()
                .collect(Collectors.groupingBy(
                        UserRoleDO::getUserId,
                        Collectors.mapping(UserRoleDO::getRoleId, Collectors.toList())
                ));
    }

    private Map<Long, String> getRoleIdToNameMap() {
        QueryWrapper<RoleDO> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        List<RoleDO> roles = roleMapper.selectList(wrapper);
        return roles.stream()
                .collect(Collectors.toMap(RoleDO::getId, RoleDO::getName));
    }

    @Override
    public void save(UserAddCmd cmd, Long currentUserId) {
        UserDO userDO = new UserDO();
        userDO.setLoginId(cmd.getLoginId());
        userDO.setUsername(cmd.getUsername());
        userDO.setEmail(cmd.getEmail());
        userDO.setOrgId(cmd.getOrgId());
        userDO.setStatus(cmd.getStatus());
        userMapper.insert(userDO);

        if (cmd.getRoles() != null && !cmd.getRoles().isEmpty()) {
            saveUserRoles(userDO.getId(), cmd.getRoles());
        }
    }

    private void saveUserRoles(Long userId, List<String> roleIds) {
        for (String roleIdStr : roleIds) {
            try {
                Long roleId = Long.parseLong(roleIdStr);
                UserRoleDO userRoleDO = new UserRoleDO();
                userRoleDO.setUserId(userId);
                userRoleDO.setRoleId(roleId);
                userRoleMapper.insert(userRoleDO);
            } catch (NumberFormatException e) {
                // ignore invalid role id
            }
        }
    }

    @Override
    public void update(UserUpdateCmd cmd, Long currentUserId) {
        UserDO userDO = userMapper.selectById(cmd.getId());
        if (userDO != null) {
            if (cmd.getLoginId() != null && !cmd.getLoginId().isEmpty()) {
                userDO.setLoginId(cmd.getLoginId());
            }
            userDO.setUsername(cmd.getUsername());
            userDO.setEmail(cmd.getEmail());
            userDO.setOrgId(cmd.getOrgId());
            userDO.setStatus(cmd.getStatus());
            userMapper.updateById(userDO);

            if (cmd.getRoles() != null) {
                updateUserRoles(userDO.getId(), cmd.getRoles());
            }
        }
    }

    private void updateUserRoles(Long userId, List<String> roleIds) {
        LambdaQueryWrapper<UserRoleDO> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(UserRoleDO::getUserId, userId);
        userRoleMapper.delete(deleteWrapper);

        if (roleIds != null && !roleIds.isEmpty()) {
            saveUserRoles(userId, roleIds);
        }
    }

    @Override
    public void delete(Long id, Long currentUserId) {
        UserDO userDO = userMapper.selectById(id);
        if (userDO != null) {
            if ("admin".equals(userDO.getLoginId())) {
                throw new RuntimeException("超级管理员账号不可删除");
            }
            
            LambdaQueryWrapper<UserRoleDO> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserRoleDO::getUserId, id);
            userRoleMapper.delete(wrapper);
            
            userMapper.deleteById(id);
        }
    }

    @Override
    public UserDTO getById(Long id) {
        UserDO userDO = userMapper.selectById(id);
        if (userDO == null) {
            return null;
        }
        List<UserDTO> dtos = convertToDTOsWithRoles(Collections.singletonList(userDO));
        return dtos.isEmpty() ? null : dtos.get(0);
    }

    @Override
    public UserDTO getByLoginId(String loginId) {
        LambdaQueryWrapper<UserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDO::getLoginId, loginId);
        UserDO userDO = userMapper.selectOne(wrapper);
        if (userDO == null) {
            return null;
        }
        List<UserDTO> dtos = convertToDTOsWithRoles(Collections.singletonList(userDO));
        return dtos.isEmpty() ? null : dtos.get(0);
    }

    @Override
    public void resetPassword(Long id) {
        UserDO userDO = userMapper.selectById(id);
        if (userDO != null) {
            userMapper.updateById(userDO);
        }
    }

    @Override
    public void changeStatus(Long id, Integer status, Long currentUserId) {
        UserDO userDO = userMapper.selectById(id);
        if (userDO != null) {
            if (0 == status && "admin".equals(userDO.getLoginId())) {
                throw new RuntimeException("超级管理员账号不可禁用");
            }
            userDO.setStatus(status);
            userMapper.updateById(userDO);
        }
    }

    private List<Long> getChildOrgIds(Long parentId) {
        List<Long> ids = new ArrayList<>();
        ids.add(parentId);
        QueryWrapper<OrgDO> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", parentId);
        List<OrgDO> children = orgMapper.selectList(wrapper);
        for (OrgDO child : children) {
            ids.addAll(getChildOrgIds(child.getId()));
        }
        return ids;
    }
}
