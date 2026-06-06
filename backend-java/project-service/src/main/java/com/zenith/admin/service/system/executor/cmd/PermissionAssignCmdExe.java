package com.zenith.admin.service.system.executor.cmd;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.alibaba.fastjson2.JSON;
import com.zenith.admin.api.OperLogService;
import com.zenith.admin.dataobject.FunctionDO;
import com.zenith.admin.dataobject.MenuDO;
import com.zenith.admin.dataobject.RoleDO;
import com.zenith.admin.dataobject.RoleFunctionDO;
import com.zenith.admin.dataobject.RoleMenuDO;
import com.zenith.admin.dto.data.OperLogDTO;
import com.zenith.admin.context.UserContext;
import com.zenith.admin.mapper.FunctionMapper;
import com.zenith.admin.mapper.MenuMapper;
import com.zenith.admin.mapper.RoleFunctionMapper;
import com.zenith.admin.mapper.RoleMapper;
import com.zenith.admin.mapper.RoleMenuMapper;
import com.zenith.admin.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 角色权限分配命令执行器
 * 处理角色功能权限和菜单权限的分配事务，包含操作日志记录
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionAssignCmdExe {

    private final RoleMapper roleMapper;
    private final RoleFunctionMapper roleFunctionMapper;
    private final RoleMenuMapper roleMenuMapper;
    private final UserRoleMapper userRoleMapper;
    private final FunctionMapper functionMapper;
    private final MenuMapper menuMapper;
    private final OperLogService operLogService;

    public void execute(Long roleId, List<Long> functionIds, List<Long> menuIds) {
        Long currentUserId = UserContext.getUserId();
        RoleDO role = roleMapper.selectById(roleId);
        String roleName = role != null ? role.getName() : "未知角色";

        String beforeSnapshot = buildPermissionSnapshot(roleId);

        // 删除旧的角色-功能关联
        LambdaQueryWrapper<RoleFunctionDO> functionDeleteWrapper = new LambdaQueryWrapper<>();
        functionDeleteWrapper.eq(RoleFunctionDO::getRoleId, roleId);
        roleFunctionMapper.delete(functionDeleteWrapper);

        // 删除旧的角色-菜单关联
        LambdaQueryWrapper<RoleMenuDO> menuDeleteWrapper = new LambdaQueryWrapper<>();
        menuDeleteWrapper.eq(RoleMenuDO::getRoleId, roleId);
        roleMenuMapper.delete(menuDeleteWrapper);

        LocalDateTime now = LocalDateTime.now();

        // 插入新的角色-功能关联
        if (functionIds != null && !functionIds.isEmpty()) {
            for (Long functionId : functionIds) {
                RoleFunctionDO roleFunction = new RoleFunctionDO();
                roleFunction.setRoleId(roleId);
                roleFunction.setFunctionId(functionId);
                roleFunction.setCreatedTime(now);
                roleFunctionMapper.insert(roleFunction);
            }
        }

        // 解析并插入菜单ID（包含父级菜单）
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
                        .findFirst()
                        .ifPresent(menu -> collectParentIds(allMenus, menu.getParentId(), fullPathIds));
            }
            return fullPathIds;
        }

        return result;
    }

    private void collectParentIds(List<MenuDO> allMenus, Long parentId, Set<Long> collectedIds) {
        if (parentId == null || parentId == 0L || collectedIds.contains(parentId)) {
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
                        .toList();
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

    private void savePermissionLog(Long roleId, String roleName, String beforeSnapshot,
                                   String afterSnapshot, Long operatorId) {
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

    // ---- 内部 DTO 类（仅限本 Executor 使用）----

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
            return functionIds == null ? Collections.emptyList() :
                    functionIds.stream().map(id -> new FunctionDTO(id, "", "", 0)).toList();
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
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    private static class LogRemarkDTO {
        private SnapshotDTO after;
        private SnapshotDTO before;
        private ChangesDTO changes;
        private Long roleId;
        private String roleName;
    }
}
