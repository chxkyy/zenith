package com.zenith.admin.service.system.executor.qry;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zenith.admin.dataobject.FunctionDO;
import com.zenith.admin.dataobject.RoleDO;
import com.zenith.admin.dataobject.RoleFunctionDO;
import com.zenith.admin.mapper.FunctionMapper;
import com.zenith.admin.mapper.RoleFunctionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 用户权限解析查询执行器
 * 负责从数据库加载用户权限字符串集合，并提供5分钟TTL缓存
 */
@Component
@RequiredArgsConstructor
public class PermissionResolveQryExe {

    private static final long CACHE_TTL_MS = 5 * 60 * 1000L;

    private final RoleFunctionMapper roleFunctionMapper;
    private final FunctionMapper functionMapper;
    private final UserRolesQryExe userRolesQryExe;

    /**
     * 用户权限缓存：userId -> (permissions, expireAt)
     */
    private final ConcurrentHashMap<Long, CachedPermissions> permissionCache = new ConcurrentHashMap<>();

    /**
     * 获取用户权限标识列表（带缓存）
     */
    public List<String> execute(Long userId) {
        CachedPermissions cached = permissionCache.get(userId);
        if (cached != null && cached.expireAt > System.currentTimeMillis()) {
            return new ArrayList<>(cached.permissions);
        }
        Set<String> perms = loadPermissionsFromDB(userId);
        permissionCache.put(userId,
                new CachedPermissions(perms, System.currentTimeMillis() + CACHE_TTL_MS));
        return new ArrayList<>(perms);
    }

    /**
     * 使指定用户的缓存失效
     */
    public void invalidateCache(Long userId) {
        permissionCache.remove(userId);
    }

    /**
     * 判断用户是否拥有指定权限
     */
    public boolean hasPermission(Long userId, String permission) {
        List<String> perms = execute(userId);
        return perms.contains("*") || perms.contains(permission);
    }

    private Set<String> loadPermissionsFromDB(Long userId) {
        List<RoleDO> roles = userRolesQryExe.getRoles(userId);

        if (roles.isEmpty()) {
            return Collections.emptySet();
        }

        if (userRolesQryExe.isSuperAdmin(roles)) {
            return Collections.singleton("*");
        }

        List<Long> roleIds = roles.stream().map(RoleDO::getId).toList();

        List<RoleFunctionDO> roleFunctions = roleFunctionMapper.selectList(
                new LambdaQueryWrapper<RoleFunctionDO>().in(RoleFunctionDO::getRoleId, roleIds)
        );
        if (roleFunctions.isEmpty()) {
            return Collections.emptySet();
        }

        List<Long> functionIds = roleFunctions.stream()
                .map(RoleFunctionDO::getFunctionId)
                .distinct()
                .toList();

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

    private record CachedPermissions(Set<String> permissions, long expireAt) {}
}
