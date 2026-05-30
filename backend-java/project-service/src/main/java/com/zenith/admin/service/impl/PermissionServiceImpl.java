package com.zenith.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.api.OperLogService;
import com.zenith.admin.api.PermissionService;
import com.zenith.admin.dataobject.FunctionDO;
import com.zenith.admin.dataobject.MenuDO;
import com.zenith.admin.dataobject.RoleDO;
import com.zenith.admin.dataobject.RoleFunctionDO;
import com.zenith.admin.dataobject.RoleMenuDO;
import com.zenith.admin.dataobject.UserRoleDO;
import com.zenith.admin.dto.data.MenuDTO;
import com.zenith.admin.dto.data.OperLogDTO;
import com.zenith.admin.context.UserContext;
import com.zenith.admin.mapper.FunctionMapper;
import com.zenith.admin.mapper.MenuMapper;
import com.zenith.admin.mapper.RoleFunctionMapper;
import com.zenith.admin.mapper.RoleMenuMapper;
import com.zenith.admin.mapper.RoleMapper;
import com.zenith.admin.mapper.UserRoleMapper;
import com.alibaba.fastjson2.JSON;
import lombok.NoArgsConstructor;
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
    private final FunctionMapper functionMapper;
    private final MenuMapper menuMapper;
    private final OperLogService operLogService;
    private final ConcurrentHashMap<Long, CachedPermissions> permissionCache = new ConcurrentHashMap<>();
    private final RoleFunctionMapper roleFunctionMapper;
    private final RoleMapper roleMapper;
    private final RoleMenuMapper roleMenuMapper;
    private final UserRoleMapper userRoleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRolePermissions(Long roleId, List<Long> functionIds, List<Long> menuIds) {
        Long currentUserId = UserContext.getUserId();
        RoleDO role = roleMapper.selectById(roleId);
        String roleName = role != null ? role.getName() : "未知角色";

        String beforeSnapshot = buildPermissionSnapshot(roleId);

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

        String afterSnapshot = buildPermissionSnapshot(roleId);

        savePermissionLog(roleId, roleName, beforeSnapshot, afterSnapshot, currentUserId);

        List<UserRoleDO> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRoleDO>().eq(UserRoleDO::getRoleId, roleId)
        );
        for (UserRoleDO ur : userRoles) {
            invalidateCache(ur.getUserId());
        }
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

        Set<Long> allAccessibleMenuIds = roleMenus.stream()
                .map(RoleMenuDO::getMenuId).collect(Collectors.toSet());
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

    public List<Long> getRolesByUserId(Long userId) {
        List<UserRoleDO> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRoleDO>().eq(UserRoleDO::getUserId, userId)
        );
        return userRoles.stream()
                .map(UserRoleDO::getRoleId)
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
    public List<String> getUserRoles(Long userId) {
        List<UserRoleDO> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<UserRoleDO>().eq(UserRoleDO::getUserId, userId)
        );
        return userRoles.stream()
                .map(ur -> ur.getRoleId().toString())
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasPermission(Long userId, String permission) {
        List<String> perms = getUserPermissions(userId);
        return perms.contains("*") || perms.contains(permission);
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

    private record CachedPermissions(Set<String> permissions, long expireAt) {
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    private static class FunctionDTO {
        private Long id;
        private String name;
        private String permission;
        private Integer sort;
    }

    @lombok.Data
    private static class MenuPermissionDTO {
        private List<Long> functionIds;
        private Long menuId;
        private String menuName;

        public List<FunctionDTO> getFunctions() {
            return functionIds == null ? Collections.emptyList() : functionIds.stream()
                    .map(id -> new FunctionDTO(id, "", "", 0)).collect(Collectors.toList());
        }
    }

    @lombok.Data
    private static class SnapshotDTO {
        private List<MenuPermissionDTO> menus;
        private Long roleId;
        private Integer totalFunctions;
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    private static class ChangesDTO {
        private List<ChangeDetailDTO> added;
        private List<ChangeDetailDTO> removed;
    }

    @lombok.Data
    private static class ChangeDetailDTO {
        private Long functionId;
        private Long menuId;
        private String menuName;
        private String name;
        private String permission;
    }

    @lombok.Data
    @NoArgsConstructor
    @lombok.AllArgsConstructor
    private static class LogRemarkDTO {
        private SnapshotDTO after;
        private SnapshotDTO before;
        private ChangesDTO changes;
        private Long roleId;
        private String roleName;
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

    private List<ChangeDetailDTO> buildChangeDetails(Set<Long> ids, SnapshotDTO snapshot) {
        List<ChangeDetailDTO> details = new ArrayList<>();
        for (Long id : ids) {
            for (MenuPermissionDTO menu : snapshot.getMenus()) {
                for (FunctionDTO func : menu.getFunctions()) {
                    if (func.getId().equals(id)) {
                        ChangeDetailDTO detailDTO = new ChangeDetailDTO();
                        detailDTO.setPermission(func.getPermission());
                        detailDTO.setMenuId(func.getId());
                        detailDTO.setMenuName(func.getName());
                        detailDTO.setName(func.getName());
                        detailDTO.setFunctionId(func.getId());
                        details.add(detailDTO);
                    }
                }
            }
        }
        return details;
    }

    private String buildLogRemark(Long roleId, String roleName, String beforeJson, String afterJson) {
        SnapshotDTO before = JSON.parseObject(beforeJson, SnapshotDTO.class);
        SnapshotDTO after = JSON.parseObject(afterJson, SnapshotDTO.class);

        Set<Long> beforeAllIds = new HashSet<>();
        before.getMenus().forEach(m -> beforeAllIds.addAll(m.getFunctionIds()));
        Set<Long> afterAllIds = new HashSet<>();
        after.getMenus().forEach(m -> afterAllIds.addAll(m.getFunctionIds()));

        Set<Long> addedIds = new HashSet<>(afterAllIds);
        addedIds.removeAll(beforeAllIds);
        Set<Long> removedIds = new HashSet<>(beforeAllIds);
        removedIds.removeAll(afterAllIds);

        LogRemarkDTO remark = new LogRemarkDTO();
        remark.setRoleId(roleId);
        remark.setRoleName(roleName);
        remark.setBefore(before);
        remark.setAfter(after);
        remark.setChanges(new ChangesDTO(
                buildChangeDetails(addedIds, after),
                buildChangeDetails(removedIds, before)
        ));

        return JSON.toJSONString(remark);
    }

    private String buildPermissionSnapshot(Long roleId) {
        List<RoleFunctionDO> roleFunctions = roleFunctionMapper.selectList(
                new LambdaQueryWrapper<RoleFunctionDO>().eq(RoleFunctionDO::getRoleId, roleId)
        );
        Set<Long> functionIdSet = roleFunctions.stream()
                .map(RoleFunctionDO::getFunctionId)
                .collect(Collectors.toSet());

        List<FunctionDO> allFunctions = functionMapper.selectList(null);
        List<MenuDO> allMenus = menuMapper.selectList(null);

        Map<Long, MenuDO> menuMap = allMenus.stream()
                .collect(Collectors.toMap(MenuDO::getId, m -> m));

        Map<Long, List<FunctionDTO>> menuFunctionMap = new LinkedHashMap<>();
        for (FunctionDO func : allFunctions) {
            if (func.getMenuId() != null) {
                menuFunctionMap.computeIfAbsent(func.getMenuId(), k -> new ArrayList<>())
                        .add(new FunctionDTO(func.getId(), func.getName(), func.getPermission(), func.getSort()));
            }
        }
        menuFunctionMap.values().forEach(list -> list.sort(Comparator.comparingInt(FunctionDTO::getSort)));

        List<MenuPermissionDTO> menus = new ArrayList<>();
        for (Map.Entry<Long, List<FunctionDTO>> entry : menuFunctionMap.entrySet()) {
            MenuDO menu = menuMap.get(entry.getKey());
            if (menu != null) {
                List<Long> funcIds = entry.getValue().stream()
                        .map(FunctionDTO::getId)
                        .filter(functionIdSet::contains)
                        .collect(Collectors.toList());
                MenuPermissionDTO permissionDTO = new MenuPermissionDTO();
                permissionDTO.setMenuId(menu.getId());
                permissionDTO.setMenuName(menu.getName());
                permissionDTO.setFunctionIds(funcIds);
                menus.add(permissionDTO);
            }
        }

        SnapshotDTO snapshot = new SnapshotDTO();
        snapshot.setMenus(menus);
        snapshot.setRoleId(roleId);
        snapshot.setTotalFunctions(allFunctions.size());
        return JSON.toJSONString(snapshot);
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

    private void invalidateCache(Long userId) {
        permissionCache.remove(userId);
    }

    private boolean isSuperAdmin(List<RoleDO> roles) {
        return roles.stream()
                .anyMatch(role -> SUPER_ADMIN_ROLE_ID.equals(role.getId()));
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
                allMenus.stream()
                        .filter(m -> m.getId().equals(menuId))
                        .findFirst().ifPresent(menu -> collectParentIds(allMenus, menu.getParentId(), fullPathIds));
            }
            return fullPathIds;
        }

        return result;
    }

    private void savePermissionLog(Long roleId, String roleName, String beforeSnapshot, String afterSnapshot,
                                   Long operatorId) {
        try {
            OperLogDTO operLog = new OperLogDTO();
            operLog.setModule("权限管理");
            operLog.setContent(String.format("分配权限 - %s（角色ID: %d）", roleName, roleId));
            operLog.setOperator(operatorId != null ? operatorId.toString() : "system");
            operLog.setIp("-");
            operLog.setResult("成功");
            operLog.setRemark(buildLogRemark(roleId, roleName, beforeSnapshot, afterSnapshot));
            operLog.setCreateUserId(operatorId);
            operLogService.save(operLog);
        } catch (Exception e) {
            log.error("Failed to save permission operation log for roleId: {}", roleId, e);
        }
    }
}
