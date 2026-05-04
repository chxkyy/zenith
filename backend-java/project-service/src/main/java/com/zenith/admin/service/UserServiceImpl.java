package com.zenith.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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

import java.time.LocalDateTime;
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

    @Override
    public PageInfo<UserDTO> listByPage(UserPageQuery query) {
        PageHelper.startPage(query.getPageIndex(), query.getPageSize());

        QueryWrapper<UserDO> wrapper = new QueryWrapper<>();

        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            wrapper.like("username", query.getKeyword())
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

        if (query.getSortField() != null && !query.getSortField().isEmpty()) {
            String order = query.getSortOrder() != null && "desc".equals(query.getSortOrder()) ? "desc" : "asc";
            wrapper.orderBy(true, "asc".equals(order), query.getSortField());
        }

        List<UserDO> userDOS = userMapper.selectList(wrapper);
        PageInfo<UserDO> pageInfo = new PageInfo<>(userDOS);

        List<UserDTO> dtos = convertToDTOsWithRoles(userDOS);

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

        Map<Long, List<String>> userRolesMap = getUserRolesMap(userIds);
        Map<String, String> roleCodeToNameMap = getRoleCodeToNameMap();

        return userDOS.stream().map(userDO -> {
            UserDTO dto = userConvertor.toDTO(userDO);

            List<String> roleCodes = userRolesMap.getOrDefault(userDO.getId(), Collections.emptyList());
            dto.setRoles(roleCodes);

            String roleNames = roleCodes.stream()
                    .map(code -> roleCodeToNameMap.getOrDefault(code, code))
                    .collect(Collectors.joining(", "));
            dto.setRoleNames(roleNames);

            return dto;
        }).collect(Collectors.toList());
    }

    private Map<Long, List<String>> getUserRolesMap(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        LambdaQueryWrapper<UserRoleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(UserRoleDO::getUserId, userIds);

        List<UserRoleDO> userRoles = userRoleMapper.selectList(wrapper);

        Map<Long, List<Long>> roleIdMap = userRoles.stream()
                .collect(Collectors.groupingBy(
                        UserRoleDO::getUserId,
                        Collectors.mapping(UserRoleDO::getRoleId, Collectors.toList())
                ));

        if (roleIdMap.isEmpty()) {
            return Collections.emptyMap();
        }

        Set<Long> allRoleIds = roleIdMap.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        Map<Long, String> roleIdToCodeMap = getRoleIdToCodeMap(allRoleIds);

        Map<Long, List<String>> result = new HashMap<>();
        roleIdMap.forEach((userId, roleIds) -> {
            List<String> roleCodes = roleIds.stream()
                    .map(roleId -> roleIdToCodeMap.getOrDefault(roleId, "UNKNOWN"))
                    .collect(Collectors.toList());
            result.put(userId, roleCodes);
        });

        return result;
    }

    private Map<Long, String> getRoleIdToCodeSet(Set<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptyMap();
        }

        LambdaQueryWrapper<RoleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(RoleDO::getId, roleIds);

        List<RoleDO> roles = roleMapper.selectList(wrapper);

        return roles.stream()
                .collect(Collectors.toMap(RoleDO::getId, RoleDO::getCode));
    }

    private Map<Long, String> getRoleIdToCodeMap(Collection<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptyMap();
        }

        Set<Long> roleIdSet = new HashSet<>(roleIds);
        return getRoleIdToCodeSet(roleIdSet);
    }

    private Map<String, String> getRoleCodeToNameMap() {
        QueryWrapper<RoleDO> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);

        List<RoleDO> roles = roleMapper.selectList(wrapper);

        return roles.stream()
                .collect(Collectors.toMap(RoleDO::getCode, RoleDO::getName));
    }

    @Override
    public void save(UserAddCmd cmd, Long currentUserId) {
        UserDO userDO = new UserDO();
        userDO.setUsername(cmd.getUsername());
        userDO.setEmail(cmd.getEmail());
        userDO.setOrgId(cmd.getOrgId());
        userDO.setStatus(cmd.getStatus());

        userDO.setCreateUserId(currentUserId);
        userDO.setCreatedTime(LocalDateTime.now());
        userMapper.insert(userDO);

        if (cmd.getRoles() != null && !cmd.getRoles().isEmpty()) {
            saveUserRoles(userDO.getId(), cmd.getRoles());
        }
    }

    private void saveUserRoles(Long userId, List<String> roleCodes) {
        for (String roleCode : roleCodes) {
            QueryWrapper<RoleDO> wrapper = new QueryWrapper<>();
            wrapper.eq("code", roleCode);
            RoleDO roleDO = roleMapper.selectOne(wrapper);
            if (roleDO != null) {
                UserRoleDO userRoleDO = new UserRoleDO();
                userRoleDO.setUserId(userId);
                userRoleDO.setRoleId(roleDO.getId());
                userRoleMapper.insert(userRoleDO);
            }
        }
    }

    @Override
    public void update(UserUpdateCmd cmd, Long currentUserId) {
        UserDO userDO = userMapper.selectById(cmd.getId());
        if (userDO != null) {
            userDO.setUsername(cmd.getUsername());
            userDO.setEmail(cmd.getEmail());
            userDO.setOrgId(cmd.getOrgId());
            userDO.setStatus(cmd.getStatus());
            userDO.setUpdateUserId(currentUserId);
            userDO.setUpdateTime(LocalDateTime.now());
            userMapper.updateById(userDO);

            if (cmd.getRoles() != null) {
                updateUserRoles(userDO.getId(), cmd.getRoles());
            }
        }
    }

    private void updateUserRoles(Long userId, List<String> roleCodes) {
        LambdaQueryWrapper<UserRoleDO> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(UserRoleDO::getUserId, userId);
        userRoleMapper.delete(deleteWrapper);

        if (roleCodes != null && !roleCodes.isEmpty()) {
            saveUserRoles(userId, roleCodes);
        }
    }

    @Override
    public void delete(Long id, Long currentUserId) {
        UserDO userDO = userMapper.selectById(id);
        if (userDO != null) {
            if ("admin".equals(userDO.getUsername())) {
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
            if (0 == status && "admin".equals(userDO.getUsername())) {
                throw new RuntimeException("超级管理员账号不可禁用");
            }
            userDO.setStatus(status);
            userDO.setUpdateUserId(currentUserId);
            userDO.setUpdateTime(LocalDateTime.now());
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
