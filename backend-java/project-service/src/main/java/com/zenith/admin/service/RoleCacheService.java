package com.zenith.admin.service;

import com.zenith.admin.dataobject.RoleDO;
import com.zenith.admin.mapper.RoleMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleCacheService {

    private final RoleMapper roleMapper;

    private volatile Map<String, String> roleCodeToNameMap;

    @PostConstruct
    public void init() {
        refreshCache();
    }

    public void refreshCache() {
        List<RoleDO> roles = roleMapper.selectList(null);
        Map<String, String> newMap = new ConcurrentHashMap<>();
        for (RoleDO role : roles) {
            // 同时存储原始code和去除ROLE_前缀的code
            String code = role.getCode();
            newMap.put(code, role.getName());
            if (code.startsWith("ROLE_")) {
                String shortCode = code.substring(5);
                newMap.put(shortCode, role.getName());
            }
        }
        this.roleCodeToNameMap = newMap;
    }

    public String getRoleName(String roleCode) {
        if (roleCode == null || roleCode.isEmpty()) {
            return "";
        }
        return roleCodeToNameMap.getOrDefault(roleCode, roleCode);
    }

    public String getRoleNames(String roleCodes, String separator) {
        if (roleCodes == null || roleCodes.isEmpty()) {
            return "";
        }
        String[] codes = roleCodes.split(separator);
        return java.util.Arrays.stream(codes)
                .map(String::trim)
                .filter(code -> !code.isEmpty())
                .map(this::getRoleName)
                .collect(Collectors.joining(separator));
    }
}
