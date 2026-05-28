package com.zenith.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.api.PermissionService;
import com.zenith.admin.dataobject.FunctionDO;
import com.zenith.admin.dataobject.MenuDO;
import com.zenith.admin.dataobject.RoleDO;
import com.zenith.admin.dataobject.RoleFunctionDO;
import com.zenith.admin.dataobject.RoleMenuDO;
import com.zenith.admin.dataobject.UserRoleDO;
import com.zenith.admin.dto.data.MenuDTO;
import com.zenith.admin.mapper.FunctionMapper;
import com.zenith.admin.mapper.MenuMapper;
import com.zenith.admin.mapper.RoleFunctionMapper;
import com.zenith.admin.mapper.RoleMenuMapper;
import com.zenith.admin.mapper.RoleMapper;
import com.zenith.admin.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private static final Long SUPER_ADMIN_ROLE_ID = 1L;

    private static final long CACHE_TTL_MS = 5 * 60 * 1000L;

    private final UserRoleMapper userRoleMapper;
    private final RoleMenuMapper roleMenuMapper;
    private final RoleFunctionMapper roleFunctionMapper;
    private final RoleMapper roleMapper;
    private final FunctionMapper functionMapper;
    private final MenuMapper menuMapper;

    private final ConcurrentHashMap<Long, CachedPermissions> permissionCache = new ConcurrentHashMap<>();

    private static class CachedPermissions {
        final Set<String> permissions;
        final long expireAt;

        CachedPermissions(Set<String> permissions, long expireAt) {
            this.permissions = permissions;
            this.expireAt = expireAt;
        }
    }

    private Set<String> loadPermissionsFromDB(Long userId) {
        List<UserRoleDO> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRoleDO>().eq(UserRoleDO::getUserId, userId)
        );
        if (userRoles.isEmpty()) {
            return Collections.emptySet();
        }

        List<Long> roleIds = userRoles.stream()
                .map(UserRoleDO::getRoleId)
                .collect(Collectors.toList());

        List<RoleDO> roles = roleMapper.selectList(
                new LambdaQueryWrapper<RoleDO>().in(RoleDO::getId, roleIds)
        );

        boolean isAdmin = isSuperAdmin(roles);
        if (isAdmin) {
            return Collections.singleton("*");
        }

        List<RoleFunctionDO> roleFunctions = roleFunctionMapper.selectList(
                new LambdaQueryWrapper<RoleFunctionDO>().in(RoleFunctionDO::getRoleId, roleIds)
        );
        if (roleFunctions.isEmpty()) {
            return Collections.emptySet();
        }

        List<Long> functionIds = roleFunctions.stream()
                .map(RoleFunctionDO::getFunctionId)
                .distinct()
                .collect(Collectors.toList());

        List<FunctionDO> functions = functionMapper.selectList(
                new LambdaQueryWrapper<FunctionDO>()
                        .in(FunctionDO::getId, functionIds)
                        .eq(FunctionDO::getStatus, 1)
        );

        return functions.stream()
                .map(FunctionDO::getPermission)
                .filter(perm -> perm != null && !perm.isEmpty())
                .collect(Collectors.toSet());
    }

    private void invalidateCache(Long userId) {
        permissionCache.remove(userId);
    }

    @Override
    public List<String> getUserRoles(Long userId) {
        List<UserRoleDO> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRoleDO>().eq(UserRoleDO::getUserId, userId)
        );
        return userRoles.stream()
                .map(ur -> ur.getRoleId().toString())
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getUserPermissions(Long userId) {
        CachedPermissions cached = permissionCache.get(userId);
        if (cached != null && cached.expireAt > System.currentTimeMillis()) {
            return new ArrayList<>(cached.permissions);
        }
        Set<String> perms = loadPermissionsFromDB(userId);
        permissionCache.put(userId, new CachedPermissions(perms, System.currentTimeMillis() + CACHE_TTL_MS));
        return new ArrayList<>(perms);
    }

    @Override
    public List<MenuDTO> getAccessibleMenus(Long userId) {
        List<UserRoleDO> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRoleDO>().eq(UserRoleDO::getUserId, userId)
        );
        if (userRoles.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> roleIds = userRoles.stream()
                .map(UserRoleDO::getRoleId)
                .collect(Collectors.toList());

        List<RoleDO> roles = roleMapper.selectList(
                new LambdaQueryWrapper<RoleDO>().in(RoleDO::getId, roleIds)
        );

        boolean isAdmin = isSuperAdmin(roles);

        List<MenuDO> allMenus;
        if (isAdmin) {
            allMenus = menuMapper.selectList(
                    new LambdaQueryWrapper<MenuDO>()
                            .orderByAsc(MenuDO::getSort)
            );
            return convertToMenuDTOList(allMenus);
        }

        allMenus = menuMapper.selectList(
                new LambdaQueryWrapper<MenuDO>()
                        .eq(MenuDO::getStatus, 1)
                        .orderByAsc(MenuDO::getSort)
        );

        List<RoleMenuDO> roleMenus = roleMenuMapper.selectList(
                new LambdaQueryWrapper<RoleMenuDO>().in(RoleMenuDO::getRoleId, roleIds)
        );

        Set<Long> accessibleMenuIds = roleMenus.stream()
                .map(RoleMenuDO::getMenuId)
                .collect(Collectors.toSet());

        Set<Long> allAccessibleMenuIds = new HashSet<>(accessibleMenuIds);
        for (MenuDO menu : allMenus) {
            if (allAccessibleMenuIds.contains(menu.getId())) {
                addParentMenuIds(allMenus, menu.getParentId(), allAccessibleMenuIds);
            }
        }

        List<MenuDO> accessibleMenus = allMenus.stream()
                .filter(menu -> allAccessibleMenuIds.contains(menu.getId()))
                .collect(Collectors.toList());

        return convertToMenuDTOList(accessibleMenus);
    }

    private void addParentMenuIds(List<MenuDO> allMenus, Long parentId, Set<Long> accessibleMenuIds) {
        if (parentId == null || parentId == 0L) {
            return;
        }
        if (accessibleMenuIds.contains(parentId)) {
            return;
        }
        accessibleMenuIds.add(parentId);
        for (MenuDO menu : allMenus) {
            if (menu.getId().equals(parentId)) {
                addParentMenuIds(allMenus, menu.getParentId(), accessibleMenuIds);
                break;
            }
        }
    }

    private List<MenuDTO> convertToMenuDTOList(List<MenuDO> menuDOs) {
        return menuDOs.stream().map(menuDO -> {
            MenuDTO dto = new MenuDTO();
            dto.setId(menuDO.getId());
            dto.setParentId(menuDO.getParentId());
            dto.setName(menuDO.getName());
            dto.setPath(menuDO.getPath());
            dto.setComponent(menuDO.getComponent());
            dto.setIcon(menuDO.getIcon());
            dto.setSort(menuDO.getSort());
            dto.setStatus(menuDO.getStatus());
            dto.setType(menuDO.getType());
            dto.setPermission(menuDO.getPermission());
            dto.setCreatedTime(menuDO.getCreatedTime());
            dto.setUpdateTime(menuDO.getUpdateTime());
            dto.setCreateUserId(menuDO.getCreateUserId());
            dto.setUpdateUserId(menuDO.getUpdateUserId());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public boolean hasPermission(Long userId, String permission) {
        List<String> perms = getUserPermissions(userId);
        return perms.contains("*") || perms.contains(permission);
    }

    public List<Long> getRolesByUserId(Long userId) {
        List<UserRoleDO> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRoleDO>().eq(UserRoleDO::getUserId, userId)
        );
        return userRoles.stream()
                .map(UserRoleDO::getRoleId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> getRoleMenus(Long roleId) {
        List<RoleMenuDO> roleMenus = roleMenuMapper.selectList(
                new LambdaQueryWrapper<RoleMenuDO>().eq(RoleMenuDO::getRoleId, roleId)
        );
        return roleMenus.stream()
                .map(RoleMenuDO::getMenuId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> getRolePermissions(Long roleId) {
        List<RoleFunctionDO> roleFunctions = roleFunctionMapper.selectList(
                new LambdaQueryWrapper<RoleFunctionDO>().eq(RoleFunctionDO::getRoleId, roleId)
        );
        return roleFunctions.stream()
                .map(RoleFunctionDO::getFunctionId)
                .collect(Collectors.toList());
    }

    public boolean hasPermission(Long userId, Long menuId) {
        List<Long> roleIds = getRolesByUserId(userId);
        if (roleIds.isEmpty()) {
            return false;
        }

        List<RoleMenuDO> roleMenus = roleMenuMapper.selectList(
                new LambdaQueryWrapper<RoleMenuDO>()
                        .in(RoleMenuDO::getRoleId, roleIds)
                        .eq(RoleMenuDO::getMenuId, menuId)
        );

        return !roleMenus.isEmpty();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserRoles(Long userId, List<Long> roleIds) {
        LambdaQueryWrapper<UserRoleDO> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(UserRoleDO::getUserId, userId);
        userRoleMapper.delete(deleteWrapper);

        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                UserRoleDO userRole = new UserRoleDO();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRole.setCreatedTime(LocalDateTime.now());
                userRoleMapper.insert(userRole);
            }
        }
        invalidateCache(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRolePermissions(Long roleId, List<Long> functionIds, List<Long> menuIds) {
        LambdaQueryWrapper<RoleFunctionDO> functionDeleteWrapper = new LambdaQueryWrapper<>();
        functionDeleteWrapper.eq(RoleFunctionDO::getRoleId, roleId);
        roleFunctionMapper.delete(functionDeleteWrapper);

        LambdaQueryWrapper<RoleMenuDO> menuDeleteWrapper = new LambdaQueryWrapper<>();
        menuDeleteWrapper.eq(RoleMenuDO::getRoleId, roleId);
        roleMenuMapper.delete(menuDeleteWrapper);

        LocalDateTime now = LocalDateTime.now();

        if (functionIds != null && !functionIds.isEmpty()) {
            for (Long functionId : functionIds) {
                RoleFunctionDO roleFunction = new RoleFunctionDO();
                roleFunction.setRoleId(roleId);
                roleFunction.setFunctionId(functionId);
                roleFunction.setCreatedTime(now);
                roleFunctionMapper.insert(roleFunction);
            }
        }

        Set<Long> resolvedMenuIds = resolveMenuIds(functionIds, menuIds);
        if (!resolvedMenuIds.isEmpty()) {
            for (Long menuId : resolvedMenuIds) {
                RoleMenuDO roleMenu = new RoleMenuDO();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                roleMenu.setCreatedTime(now);
                roleMenuMapper.insert(roleMenu);
            }
        }

        List<UserRoleDO> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRoleDO>().eq(UserRoleDO::getRoleId, roleId)
        );
        for (UserRoleDO ur : userRoles) {
            invalidateCache(ur.getUserId());
        }
    }

    private Set<Long> resolveMenuIds(List<Long> functionIds, List<Long> menuIds) {
        Set<Long> result = new HashSet<>();

        if (menuIds != null) {
            result.addAll(menuIds);
        }

        if (functionIds != null && !functionIds.isEmpty()) {
            List<FunctionDO> functions = functionMapper.selectList(
                    new LambdaQueryWrapper<FunctionDO>().in(FunctionDO::getId, functionIds)
            );

            Set<Long> functionMenuIds = functions.stream()
                    .map(FunctionDO::getMenuId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            result.addAll(functionMenuIds);
        }

        if (!result.isEmpty()) {
            List<MenuDO> allMenus = menuMapper.selectList(null);
            Set<Long> fullPathIds = new HashSet<>(result);
            for (Long menuId : result) {
                MenuDO menu = allMenus.stream()
                        .filter(m -> m.getId().equals(menuId))
                        .findFirst()
                        .orElse(null);
                if (menu != null) {
                    collectParentIds(allMenus, menu.getParentId(), fullPathIds);
                }
            }
            return fullPathIds;
        }

        return result;
    }

    private void collectParentIds(List<MenuDO> allMenus, Long parentId, Set<Long> collectedIds) {
        if (parentId == null || parentId == 0L) {
            return;
        }
        if (collectedIds.contains(parentId)) {
            return;
        }
        collectedIds.add(parentId);
        for (MenuDO menu : allMenus) {
            if (menu.getId().equals(parentId)) {
                collectParentIds(allMenus, menu.getParentId(), collectedIds);
                break;
            }
        }
    }

    private boolean isSuperAdmin(List<RoleDO> roles) {
        return roles.stream()
                .anyMatch(role -> SUPER_ADMIN_ROLE_ID.equals(role.getId()));
    }
}
